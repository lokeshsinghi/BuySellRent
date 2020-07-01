package com.example.buysellrent.ui.sell.forms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buysellrent.R;
import com.example.buysellrent.ui.sell.CommonForm;

public class ExtraForm extends AppCompatActivity {


    private Button next;
    private TextView category;
    private EditText brand,title,desc;
    private TextView desc_length,title_length;
    private String Brand,Title,Desc;

    private int flag=0,flag2=1;
    private String cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_form);
        category=findViewById(R.id.custom_ad_name);


        Bundle bundle=getIntent().getExtras();
        if(bundle.getString("category")!=null){
            category.setText(bundle.getString("category"));
            cat=bundle.getString("category");
        }
        else
            category.setText("Unidentified");

        next=findViewById(R.id.ad_form_next);

        brand=findViewById(R.id.brand);
        title=findViewById(R.id.ad_title);
        desc=findViewById(R.id.desc);

        title_length=findViewById(R.id.title_length);
        desc_length=findViewById(R.id.desc_length);

        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int len=desc.length();
                desc_length.setText(""+len+" / 4096");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int len=title.length();
                title_length.setText(""+len+" / 70");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag2=1;
                Brand=brand.getText().toString();

                Title=title.getText().toString();
                Desc=desc.getText().toString();


                if (Title.equalsIgnoreCase("")||Title.length()<10){
                    title.setError("Add appropriate title!");
                    flag2=0;}else

                if (Desc.equalsIgnoreCase("")||Desc.length()<10){
                    desc.setError("Minimum 10 characters are required!");
                    flag2=0;}




                else
                {
                    final Bundle bundle = new Bundle();
                    bundle.putString("brand",Brand);
                    bundle.putString("title",Title);
                    bundle.putString("description",Desc);
                    bundle.putString("category",cat);

                    Intent intent = new Intent(ExtraForm.this, CommonForm.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });



    }
}