package com.example.buysellrent.SignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.EmbossMaskFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buysellrent.Class.CustomProgressBar;
import com.example.buysellrent.R;
import com.example.buysellrent.startScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailSignIn extends AppCompatActivity {

    Button login;
    TextView createNew, forgot, warning;
    EditText email, pass;
    FirebaseAuth mAuth;
    CustomProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_in);

        login = findViewById(R.id.login);
        createNew = findViewById(R.id.create);
        forgot = findViewById(R.id.forgot);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.editPassword);
        mAuth = FirebaseAuth.getInstance();
        warning = findViewById(R.id.invalid);
        progressBar = new CustomProgressBar(EmailSignIn.this);

        createNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailSignIn.this, EmailSignUp.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warning.setVisibility(View.INVISIBLE);
                String userEmail = email.getText().toString();
                String password = pass.getText().toString();
                if(userEmail.equalsIgnoreCase("")) {
                    email.setError("This field cannot be empty.");
                }
                else if(password.equalsIgnoreCase("")) {
                    pass.setError("This field cannot be empty.");
                }
                else {
                    progressBar.loadDialog();
                    mAuth.signInWithEmailAndPassword(userEmail, password)
                            .addOnCompleteListener(EmailSignIn.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    }
                                    else {
                                        progressBar.dismissDialog();
                                        updateUI(null);
                                        Toast.makeText(EmailSignIn.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(EmailSignIn.this, EmailVerification.class);
            intent.putExtra("Email", user.getEmail());
            startActivity(intent);
        } else {
//                warning.setVisibility(View.VISIBLE);
        }
    }
}