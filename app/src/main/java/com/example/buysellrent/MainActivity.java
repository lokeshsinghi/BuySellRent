package com.example.buysellrent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.buysellrent.SignIn.EmailSignIn;
import com.example.buysellrent.SignIn.PhoneSignIn;

public class MainActivity extends AppCompatActivity {

    Button phoneButton, gButton, fButton, emailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneButton = findViewById(R.id.phone);
        gButton = findViewById(R.id.google);
        fButton = findViewById(R.id.facebook);
        emailButton = findViewById(R.id.email);

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PhoneSignIn.class));
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EmailSignIn.class));
            }
        });
    }
}