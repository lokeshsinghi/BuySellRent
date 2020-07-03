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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Favourites extends Fragment {

    RecyclerView recyclerAds;
    private ArrayList<AdvertisementCarModel> ads = new ArrayList<>();
    private Set<String> s;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        recyclerAds = view.findViewById(R.id.recyclerAds);
        s = new HashSet<>();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        FirebaseDatabase.getInstance().getReference("Favourites").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        s.clear();
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String adId = dataSnapshot1.getKey();
                            assert adId != null;
                            Log.d("Ads", adId);
                            s.add(adId);
                        }
                        getAds();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return view;
    }

    private void getAds() {
        ads.clear();
        Query query = FirebaseDatabase.getInstance().getReference("Ads");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot adSnapshot : dataSnapshot.getChildren() ){
                    AdvertisementCarModel ad = adSnapshot.getValue(AdvertisementCarModel.class);
                    if(s.contains(ad.getAdId()))
                        ads.add(ad);
                }
                initRecyclerAds();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Favourites.this.getActivity(),"Error",Toast.LENGTH_SHORT).show();
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
