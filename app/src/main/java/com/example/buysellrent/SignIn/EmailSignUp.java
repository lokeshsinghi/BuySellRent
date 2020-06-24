package com.example.buysellrent.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.buysellrent.Class.CustomProgressBar;
import com.example.buysellrent.Class.User;
import com.example.buysellrent.MainActivity;
import com.example.buysellrent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class EmailSignUp extends AppCompatActivity {

    EditText name, pass, rPass, email;
    TextView login;
    Button signUp;
    FirebaseAuth mAuth;
    String fullName, password, rPassword, mail;
    CustomProgressBar pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);

        name = findViewById(R.id.name);
        pass = findViewById(R.id.editPassword);
        rPass = findViewById(R.id.repeatPass);
        email = findViewById(R.id.email);
        signUp = findViewById(R.id.signUp);
        login = findViewById(R.id.logIn);
        mAuth = FirebaseAuth.getInstance();
        pd = new CustomProgressBar(EmailSignUp.this);


        //already have an account
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailSignUp.this, MainActivity.class));
            }
        });


        //Email and password signUp
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullName = name.getText().toString();
                mail = email.getText().toString().trim();
                password = pass.getText().toString();
                rPassword = rPass.getText().toString();

                if(fullName.equalsIgnoreCase("")) {
                    name.setError("This field cannot be empty.");
                }
                else if(password.equalsIgnoreCase("")) {
                    pass.setError("This field cannot be empty.");
                }
                else if(rPassword.equalsIgnoreCase("")) {
                    rPass.setError("This field cannot be empty.");
                }
                else if(mail.equalsIgnoreCase("")) {
                    email.setError("This field cannot be empty.");
                }
                else if(!password.equals(rPassword)) {
                    rPass.setError("Passwords didn't match. Try Again.");
                    rPass.setText("");
                }
                else if(!isValidEmail(mail)) {
                    email.setError("Enter a valid email.");
                }
                else if(pass.length() < 6) {
                    pass.setError("Enter a combination of atleast 6 letters");
                }
                else {
                    pd.loadDialog();
                    mAuth.createUserWithEmailAndPassword(mail, password)
                            .addOnCompleteListener(EmailSignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        //Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        pd.dismissDialog();
                                        // If sign in fails, display a message to the user.
                                        Log.w("SignIn Failed", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(EmailSignUp.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void updateUI(final FirebaseUser user) {
        if (user != null) {
            final String fullName, mail;
            fullName = name.getText().toString();
            mail = email.getText().toString().trim();
            String userId = user.getUid();

            User user1 = new User(fullName, mail, userId);

            FirebaseDatabase.getInstance().getReference("Users")
                    .child(user.getUid()).setValue(user1)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            pd.dismissDialog();
                                            Intent intent = new Intent(EmailSignUp.this, EmailVerification.class);
                                            intent.putExtra("Email", mail);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    });
        } else {
            //
        }
    }
}