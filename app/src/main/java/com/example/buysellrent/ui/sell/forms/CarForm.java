package com.example.buysellrent.ui.sell.forms;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.buysellrent.R;

public class CarForm extends AppCompatActivity {


    private Button man,auto;
    private TextView category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_form);
        category=findViewById(R.id.custom_ad_name);
        Bundle bundle=getIntent().getExtras();
        if(bundle.getString("category")!=null){
            category.setText(bundle.getString("category"));
        }
        else
            category.setText("Unidentified");
        man=(Button)findViewById(R.id.manual);
        auto=(Button)findViewById(R.id.auto);

        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                man.setBackgroundResource(R.drawable.background_buttons);
                man.setTextColor(Color.parseColor("#FFFFFF"));
                auto.setBackgroundResource(R.drawable.custom_button);
                auto.setTextColor(Color.parseColor("#000000"));
            }
        });

        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto.setBackgroundResource(R.drawable.background_buttons);
                auto.setTextColor(Color.parseColor("#FFFFFF"));
                man.setBackgroundResource(R.drawable.custom_button);
                man.setTextColor(Color.parseColor("#000000"));
            }
        });


    }
}