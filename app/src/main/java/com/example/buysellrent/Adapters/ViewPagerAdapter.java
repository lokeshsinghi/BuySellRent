package com.example.buysellrent.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.buysellrent.R;
import com.example.buysellrent.ui.home.Ads.FullScreenAdImage;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private String adId;
    private LayoutInflater layoutInflater;
    private ArrayList<String> images = new ArrayList<String >();

    public ViewPagerAdapter(Context context,String adId,ArrayList<String> images){
        this.adId = adId;
        this.context = context;
        this.images = images;
    }
    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position){
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.adimage_layout,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        Glide.with(context)
                .load(images.get(position))
                .into(imageView);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i < images.size(); i++){
                    if(position == i){
//                        Toast.makeText(context, "Image"+i+" clicked",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, FullScreenAdImage.class);
                        intent.putExtra("fsImage",images.get(position));
                        context.startActivity(intent);
                    }
                }
            }
        });
        ViewPager vp = (ViewPager) container;
        vp.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}