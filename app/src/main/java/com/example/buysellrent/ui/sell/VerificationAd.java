package com.example.buysellrent.ui.sell;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.buysellrent.MainActivity;
import com.example.buysellrent.R;
import com.example.buysellrent.ui.home.HomeFragment;

public class VerificationAd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_ad);




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(VerificationAd.this, MainActivity.class);
        startActivity(intent);
    }
}