package com.example.buysellrent.SignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buysellrent.Class.OtpEditText;
import com.example.buysellrent.Class.User;
import com.example.buysellrent.R;
import com.example.buysellrent.startScreen;
import com.example.buysellrent.ui.settings.EditProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpVerification extends AppCompatActivity {

    private String verificationCode;
    Button next;
    TextView textView;
    OtpEditText editText;
    FirebaseAuth mAuth;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        final String number = getIntent().getStringExtra("phoneNumber");
        phone = number;
        sendOtp(number);
        next = findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();
        editText = findViewById(R.id.otp);
        textView = findViewById(R.id.resend);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = editText.getText().toString().trim();
                if(code.isEmpty() || code.length() < 6) {
                    editText.setError("Enter the valid code.");
                    editText.requestFocus();
                }
                else{
                    verifyCode(code);
                }
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOtp(number);
            }
        });

    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, code);
        editText.setText(code);
        signIn(credential);
    }

    private void signIn(PhoneAuthCredential credential) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser == null) {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                updateUI(firebaseUser);
                            } else {
                                //flag with getMessage
                                Toast.makeText(OtpVerification.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else{
            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser firebaseUser1 = FirebaseAuth.getInstance().getCurrentUser();
                                link(firebaseUser1);
                            }
                            else{
                                Toast.makeText(OtpVerification.this, task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    //Link phone to already created account
    private void link(FirebaseUser firebaseUser) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("phoneNum", phone);
        FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()).updateChildren(childUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            startActivity(new Intent(OtpVerification.this, EditProfile.class));
                            finish();
                        }
                        else {
                            Toast.makeText(OtpVerification.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Create a new user with phone signIn
    private void updateUI(FirebaseUser firebaseUser) {
        User user1 = new User();
        user1.setFullName("User" + System.currentTimeMillis());
        user1.setPhoneNum(phone);
        user1.setId(firebaseUser.getUid());
        user1.setImageUrl("");
        user1.setEmail("");
        FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()).setValue(user1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Intent intent = new Intent(OtpVerification.this, startScreen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void sendOtp(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCode = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
//            Toast.makeText(OtpVerification.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}