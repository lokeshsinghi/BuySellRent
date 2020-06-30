package com.example.buysellrent.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buysellrent.Class.AdvertisementCarModel;
import com.example.buysellrent.R;
import com.example.buysellrent.ui.home.Ads.AdDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.fabiomsr.moneytextview.MoneyTextView;

import java.util.ArrayList;

public class RecyclerAdsAdapter extends RecyclerView.Adapter<RecyclerAdsAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    ArrayList<AdvertisementCarModel> ads = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private Context mContext;

    public RecyclerAdsAdapter( Context mContext,ArrayList<AdvertisementCarModel> ads) {
        this.ads = ads;
        this.mContext = mContext;
    }



    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ads_feed,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: ");

        String adId=ads.get(position).getAdId();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("AdImages").child(adId).child("image1");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Glide.with(mContext)
                        .asBitmap()
                        .load(dataSnapshot.getValue())
                        .into(holder.adImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.adTitle.setText(ads.get(position).getTitle());
        holder.adPrice.setAmount(ads.get(position).getPrice());
        holder.adLocation.setText(ads.get(position).getAddress());
        holder.adLayout.setOnClickListener(new View.OnClickListener()   {
            @Override
            public void onClick(View view)  {
                Log.d(TAG, "onClick: Clicked on"+ ads.get(position));
                final String adId = ads.get(position).getAdId();

                final ArrayList<String> images = new ArrayList<>();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AdImages").child(adId);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int n = (int) dataSnapshot.getChildrenCount();
                        for(int i = 1; i <= n; i++){
                            images.add(dataSnapshot.child("image"+i).getValue(String.class));
                            Log.d("TAG", "Okay");
                        }
                        Intent intent = new Intent(mContext, AdDetails.class);
                        intent.putExtra("adId",adId);
                        intent.putExtra("images",images);
                        mContext.startActivity(intent);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView adImage;
        TextView adTitle;
        TextView adLocation;
        MoneyTextView adPrice;
        LinearLayout adLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            adImage = itemView.findViewById(R.id.adImage);
            adTitle = itemView.findViewById(R.id.adTitle);
            adPrice = itemView.findViewById(R.id.adPrice);
            adLocation = itemView.findViewById(R.id.adLocation);
            adLayout = itemView.findViewById(R.id.adLayout);
        }
    }
}
