package com.example.buysellrent.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.buysellrent.Class.User;
import com.example.buysellrent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    Toolbar toolbar;
    CircleImageView profilePic;
    EditText fullName, phoneNum, email;
    Button fbConnect, GConnect;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fullName = findViewById(R.id.name);
        phoneNum = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        profilePic = findViewById(R.id.dp);
        progressBar = findViewById(R.id.progress_bar);
        fbConnect = findViewById(R.id.connectFb);
        GConnect = findViewById(R.id.connectG);
        toolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();

        toolbar.setTitle("");
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_baseline_close_24);
        upArrow.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user1 = dataSnapshot.getValue(User.class);
                        progressBar.setVisibility(View.GONE);
                        if(user1.getImageUrl().equals("")) {
                            profilePic.setImageResource(R.drawable.ic_deafault_dp);
                        }
                        else {
                            //Temp fix. Get back to it.
                            Glide.with(getApplicationContext()).load(user1.getImageUrl()).into(profilePic);
                        }
                        fullName.setText(user1.getFullName());
                        email.setText(user1.getEmail());
                        phoneNum.setText(user1.getPhoneNum());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_done: {
                // done btn functionalities goes here
                break;
            }
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//    static void loadImage(RequestManager glide, String url, CircleImageView view) {
//        glide.load(url).into(view);
//    }
}