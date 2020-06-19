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

import com.example.buysellrent.R;
import com.example.buysellrent.ui.home.Ads.FullScreenAdImage;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] images = {R.drawable.slide1,R.drawable.slide2,R.drawable.slide3};

    public ViewPagerAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return images.length;
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
        imageView.setImageResource(images[position]);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i < images.length; i++){
                    if(position == i){
//                        Toast.makeText(context, "Image"+i+" clicked",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, FullScreenAdImage.class);
//                        Uri uri = Uri.parse("android.resource://com.example.buysellrent/"+images[position]);
                        intent.putExtra("fsImage",images[position]);
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
