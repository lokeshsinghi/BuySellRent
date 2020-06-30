package com.example.buysellrent.ui.home.Ads;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buysellrent.Adapters.RecyclerAdsAdapter;
import com.example.buysellrent.Class.AdvertisementCarModel;
import com.example.buysellrent.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdsByCategory extends AppCompatActivity {
    private String category;
    TextView categoryView;
    RecyclerView recyclerAds;
    private ArrayList<AdvertisementCarModel> ads = new ArrayList<AdvertisementCarModel>();
    DatabaseReference referenceAdvertisements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_by_category);
        categoryView = (TextView) findViewById(R.id.category);
        recyclerAds = findViewById(R.id.recyclerAds);
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        categoryView.setText(category);
        getAds();
    }

    private void getAds() {
        ads.clear();
        Query query = FirebaseDatabase.getInstance().getReference().child("Ads").orderByChild("category").equalTo(category);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot adSnapshot : dataSnapshot.getChildren() ){
                    AdvertisementCarModel ad = adSnapshot.getValue(AdvertisementCarModel.class);
                    ads.add(ad);
                }
                initRecyclerAds();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdsByCategory.this,"Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRecyclerAds() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerAds.setLayoutManager(layoutManager);
        RecyclerAdsAdapter adapter = new RecyclerAdsAdapter(this, ads);
        recyclerAds.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}