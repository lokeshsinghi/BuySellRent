package com.example.buysellrent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.buysellrent.Class.ChatBox;
import com.example.buysellrent.Class.User;
import com.example.buysellrent.Notification.Token;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class startScreen extends AppCompatActivity {

    public static String newLocation;
    FirebaseAuth mAuth;
    String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        Intent intent = getIntent();
        newLocation = intent.getStringExtra("location");

        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navView.getChildAt(0);
        View view = bottomNavigationMenuView.getChildAt(1);
        final BottomNavigationItemView itemView = (BottomNavigationItemView) view;
        final View badge = LayoutInflater.from(this).inflate(R.layout.unread_message_layout, bottomNavigationMenuView, false);
        final TextView tv = badge.findViewById(R.id.tvUnreadChats);

        final String uId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference("Chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        itemView.removeView(badge);
                        Set<String> cnt = new HashSet<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ChatBox chatBox = ds.getValue(ChatBox.class);
                            if (chatBox.getReceiver().equals(uId) && !chatBox.isIsseen())
                                cnt.add(chatBox.getSender());
                        }
                        if (cnt.size() != 0) {
                            tv.setText(Integer.toString(cnt.size()));
                            itemView.addView(badge);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        updateUI();
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateUI() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            uId = firebaseUser.getUid();
        } else {
            startActivity(new Intent(startScreen.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateToken(String token) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        databaseReference.child(uId).setValue(token1);
    }
}