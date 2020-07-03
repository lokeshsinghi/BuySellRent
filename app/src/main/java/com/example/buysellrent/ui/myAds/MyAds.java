package com.example.buysellrent.ui.myAds;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buysellrent.Adapters.RecyclerAdsAdapter;
import com.example.buysellrent.Class.AdvertisementCarModel;
import com.example.buysellrent.R;
import com.example.buysellrent.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAds extends Fragment {

    RecyclerView recyclerAds;
    private ArrayList<AdvertisementCarModel> ads = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_ads, container, false);
        recyclerAds = view.findViewById(R.id.recyclerAds);

        getAds();
        return view;
    }

    private void getAds() {
        ads.clear();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        Query query = FirebaseDatabase.getInstance().getReference("Ads").orderByChild("sellerId").equalTo(user.getUid());
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
                Toast.makeText(MyAds.this.getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRecyclerAds() {
//        Log.d(TAG, "initRecyclerView: init recyclerview.");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerAds.setLayoutManager(layoutManager);
        RecyclerAdsAdapter adapter = new RecyclerAdsAdapter(getActivity(), ads);
        recyclerAds.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
