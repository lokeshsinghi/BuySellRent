package com.example.buysellrent.ui.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.buysellrent.R;
import com.github.chrisbanes.photoview.PhotoView;

public class FullScreenView extends AppCompatActivity {

    private PhotoView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_view);

        imageView = findViewById(R.id.fullImageView);
        Intent getCallingActivity = getIntent();
        if (getCallingActivity != null) {
            Uri uri = getCallingActivity.getData();
            if (uri != null && imageView != null) {
                Glide.with(FullScreenView.this).load(uri).into(imageView);
            }
        }
    }
}