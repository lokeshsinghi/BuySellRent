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

import com.example.buysellrent.Class.AdvertisementCarModel;
import com.example.buysellrent.R;
import com.example.buysellrent.ui.home.Ads.AdDetails;

import org.fabiomsr.moneytextview.MoneyTextView;

import java.util.ArrayList;

public class RecyclerAdsAdapter extends RecyclerView.Adapter<RecyclerAdsAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    ArrayList<AdvertisementCarModel> ads = new ArrayList<>();
    //private ArrayList<String> mImageUrls = new ArrayList<>();
    private Context mContext;

    public RecyclerAdsAdapter( Context mContext,ArrayList<AdvertisementCarModel> ads) {
        this.ads = ads;
        //this.mImageUrls = mImageUrls;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ads_feed,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: ");
//        Glide.with(mContext)
//                .asBitmap()
//                .load(ads.get(position).getImageList().get(0))
//                .into(holder.adImage);
        holder.adTitle.setText(ads.get(position).getTitle());
        holder.adPrice.setAmount(ads.get(position).getPrice());
        holder.adLayout.setOnClickListener(new View.OnClickListener()   {
            @Override
            public void onClick(View view)  {
                Log.d(TAG, "onClick: Clicked on"+ ads.get(position));
                Intent intent = new Intent(mContext, AdDetails.class);
                mContext.startActivity(intent);
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
        MoneyTextView adPrice;
        TextView adLocation;
        LinearLayout adLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            adImage = itemView.findViewById(R.id.adImage);
            adTitle = itemView.findViewById(R.id.adTitle);
            adPrice = itemView.findViewById(R.id.adPrice);
            adLayout = itemView.findViewById(R.id.adLayout);
        }
    }
}
