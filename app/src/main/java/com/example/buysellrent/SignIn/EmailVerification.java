package com.example.buysellrent.SignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buysellrent.Class.CustomProgressBar;
import com.example.buysellrent.MainActivity;
import com.example.buysellrent.R;
import com.example.buysellrent.startScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class EmailVerification extends AppCompatActivity {

    TextView textView;
    Button button, signOut, next;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    CustomProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        firebaseUser.reload();
        if(firebaseUser.isEmailVerified()) {
            startActivity(new Intent(EmailVerification.this, startScreen.class));
            finish();
        }

        String email = getIntent().getStringExtra("Email");
        textView = findViewById(R.id.text2);
        button = findViewById(R.id.button2);
        signOut = findViewById(R.id.signOut);
        next = findViewById(R.id.next);

        textView.setText(getString(R.string.sent, email));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(EmailVerification.this, "Verification Email sent", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(EmailVerification.this, "Couldn't send verification email. Please try again later.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(EmailVerification.this, MainActivity.class));
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.loadDialog();
                firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            if (firebaseUser.isEmailVerified()) {
                                startActivity(new Intent(EmailVerification.this, startScreen.class));
                                finish();
                            } else {
                                progressBar.dismissDialog();
                                Toast.makeText(EmailVerification.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            progressBar.dismissDialog();
                            Toast.makeText(EmailVerification.this, "Problem verifying email. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}