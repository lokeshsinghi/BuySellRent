package com.example.buysellrent.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buysellrent.MainActivity;
import com.example.buysellrent.R;
import com.example.buysellrent.SignIn.PhoneSignIn;
import com.example.buysellrent.ui.sell.CommonForm;
import com.example.buysellrent.ui.sell.forms.BikeForm;
import com.example.buysellrent.ui.sell.forms.CarForm;
import com.example.buysellrent.ui.sell.forms.ExtraForm;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.content.ContextCompat.startActivity;

public class RecyclerSellCategory extends RecyclerView.Adapter<RecyclerSellCategory.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private Context mContext;


    public RecyclerSellCategory( Context mContext,ArrayList<String> mNames, ArrayList<String> mImageUrls) {
        this.mNames = mNames;
        this.mImageUrls = mImageUrls;
        this.mContext = mContext;
    }

    @Override
    public RecyclerSellCategory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sell_categories,parent,false);
        return new RecyclerSellCategory.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerSellCategory.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: ");
        Glide.with(mContext)
                .asBitmap()
                .load(mImageUrls.get(position))
                .into(holder.image);
        holder.name.setText(mNames.get(position));

        holder.relativeLayout.setOnClickListener(new View.OnClickListener()   {
            @Override
            public void onClick(View view)  {
                Log.d(TAG, "onClick: Clicked on"+ mNames.get(position));
                Toast.makeText(mContext, mNames.get(position), Toast.LENGTH_SHORT).show();
                Intent intent;
                if(mNames.get(position).equals("Cars")){
                    intent=new Intent(mContext, CarForm.class);
                    intent.putExtra("category",mNames.get(position));
                    mContext.startActivity(intent);
                }
                else if(mNames.get(position).equals("Bikes"))
                {
                    intent=new Intent(mContext, BikeForm.class);
                    intent.putExtra("category",mNames.get(position));
                    mContext.startActivity(intent);
                }
                else
                {

                    intent=new Intent(mContext, ExtraForm.class);
                    intent.putExtra("category",mNames.get(position));
                    mContext.startActivity(intent);
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView name;
        RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view);
            name = itemView.findViewById(R.id.name);
            relativeLayout=itemView.findViewById(R.id.sell_ad_layout);
        }
    }
}
