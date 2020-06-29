package com.example.buysellrent.ui.home.Ads;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buysellrent.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class FullScreenAdImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_ad_image);

        PhotoView fullScreenImageView = findViewById(R.id.full_screen_ad_image);

        Intent intent = getIntent();
        if(intent != null){
            String imageUri = intent.getStringExtra("fsImage");
            Picasso
                    .with(this)
                    .load(imageUri)
                    .into(fullScreenImageView);
        }
    }
}