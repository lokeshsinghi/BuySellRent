package com.example.buysellrent.ui.myAds;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.buysellrent.Class.AdvertisementCarModel;
import com.example.buysellrent.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdsFragment extends Fragment {

    int c;
    String AdId;
    ArrayList<String> images;
    DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_ads, container, false);
        images=new ArrayList<>();

        databaseReference= FirebaseDatabase.getInstance().getReference( "Ads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AdvertisementCarModel car=snapshot.getValue(AdvertisementCarModel.class);
                    assert car != null;
                    c=car.getImgCount();
                    AdId=car.getAdId();
                    images=getImageList(c,AdId);
                    for(String a:images){
                        Log.e("random",a);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }



    @NonNull
    private ArrayList<String> getImageList(int c, String AdId){
        final ArrayList<String> imageList=new ArrayList<>();
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("AdImages").child(AdId);

        for(int i=1;i<=c;i++)
        {
            String temp="image"+i;
            imageList.add(databaseReference.child(temp).toString());
        }
        return imageList;

    }
}
