package com.example.buysellrent.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buysellrent.R;

import java.util.ArrayList;
import java.util.List;

public class AdvertisementAdapter extends RecyclerView.Adapter<AdvertisementAdapter.ViewHolder> {
    private Context mContext;
    private List<Bitmap> images;
    private ArrayList<Uri> imageList;



    public AdvertisementAdapter(Context mContext, List<Bitmap> images, ArrayList<Uri> imageList) {
        this.mContext = mContext;
        this.images = images;
        this.imageList=imageList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view= LayoutInflater.from(mContext).inflate(R.layout.ad_item,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Bitmap bitmap=images.get(position);
        holder.image.setImageBitmap(bitmap);
        holder.count.setText(""+(position+1));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);


// 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Do you want to delete ?")
                        .setTitle("Delete");


// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        images.remove(position);
                        imageList.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
//        Glide.with(mContext).asBitmap().load(bitmap).into(holder.image);
    }

/*
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Bitmap ad=images.get(position);

        if(ad.getImageUrl().equals(""))
        {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Glide.with(mContext).load(user.getImageUrl()).into(holder.profile_image);
        }
        if(ischat){

            if(user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.img_on.setVisibility(View.GONE);
            }
        }
        else
            holder.img_on.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                intent.putExtra("type","online");
                mContext.startActivity(intent);
            }
        });
    }*/

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image;
        private TextView count;
        public ViewHolder(View itemView){
            super(itemView);
            count=itemView.findViewById(R.id.count_ad);
            image=itemView.findViewById(R.id.ad_image);
        }
    }

}
