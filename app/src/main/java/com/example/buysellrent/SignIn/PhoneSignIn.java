package com.example.buysellrent.SignIn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.buysellrent.R;

public class PhoneSignIn extends AppCompatActivity {

    EditText phoneNumber;
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_sign_in);

        phoneNumber = findViewById(R.id.phone);
        nextButton = findViewById(R.id.next);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = phoneNumber.getText().toString().trim();
                if (number.isEmpty() || number.length() < 10)
                    phoneNumber.setError("Enter a valid phone number.");
                else {
                    number = "+977" + number;
                    Intent intent = new Intent(PhoneSignIn.this, OtpVerification.class);
                    intent.putExtra("phoneNumber", number);
                    startActivity(intent);
                }
            }
        });
    }
}