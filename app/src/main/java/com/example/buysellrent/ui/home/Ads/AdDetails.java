package com.example.buysellrent.ui.home.Ads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.buysellrent.Adapter.SectionPagerAdapter;
import com.example.buysellrent.Adapters.ViewPagerAdapter;
import com.example.buysellrent.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.fabiomsr.moneytextview.MoneyTextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class AdDetails extends AppCompatActivity {

    ViewPager adImages;
    LinearLayout sliderDotspanel;
    TabLayout tabLayout;
    ViewPager specsPager;
    private int dotscount;
    private ImageView[] dots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);
        adImages = (ViewPager)findViewById(R.id.adImages);
        sliderDotspanel = (LinearLayout) findViewById(R.id.sliderDots);
        Intent intent = getIntent();
        final String adId = intent.getStringExtra("adId");
        final Context mContext=this;
        final ArrayList<String> images = new ArrayList<>();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AdImages").child(adId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int n = (int) dataSnapshot.getChildrenCount();
                for(int i = 1; i <= n; i++){
                    images.add(dataSnapshot.child("image"+i).getValue(String.class));
                    Log.d("TAG", "Okay");
                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mContext,adId,images);
                    adImages.setAdapter(viewPagerAdapter);
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new MyTimerTask(),2000,4000);
                    dotscount =viewPagerAdapter.getCount();
                    dots = new ImageView[dotscount];
                    tabLayout = findViewById(R.id.specs_tab);
                    specsPager = findViewById(R.id.specs_pager);
                    setUpViewPager(specsPager);
                    tabLayout.setupWithViewPager(specsPager);
                    final TextView adTitle = (TextView) findViewById(R.id.adTitle);
                    final MoneyTextView adPrice = (MoneyTextView) findViewById(R.id.adPrice);



                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Ads").child(adId);
                    reference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            adTitle.setText(dataSnapshot.child("title").getValue(String.class));
                            adPrice.setAmount(dataSnapshot.child("price").getValue(float.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    for(int j=0; j < dotscount; j++){
                        dots[j] = new ImageView(mContext);
                        dots[j].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(8, 0,8,0);
                        sliderDotspanel.addView(dots[j],params);

                    }
                    dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.active_dot));
                    adImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            for(int k=0;k<dotscount;k++){
                                dots[k].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.nonactive_dot));
                            }
                            dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.active_dot));
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //images.add("https://firebasestorage.googleapis.com/v0/b/buysellrent-37ba8.appspot.com/o/ad_uploads%2F1593181077382.jpg?alt=media&token=fe5dd3f2-c03d-429e-be68-1d1b91d46f01");



    }

    private void setUpViewPager(ViewPager specsPager) {
        SectionPagerAdapter specsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        specsPagerAdapter.addFragment(new SpecsFragment(),"SPECIFICATIONS");
        specsPagerAdapter.addFragment(new DescriptionFragment(),"DESCRIPTION");
        specsPager.setAdapter(specsPagerAdapter);
    }

    public class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            AdDetails.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0;i<dotscount;i++) {
                        if(adImages.getCurrentItem() == dotscount-1 && i==dotscount-1) {
                            adImages.setCurrentItem(0);
                        }
                        if (adImages.getCurrentItem() == i) {
                            adImages.setCurrentItem(i+1);
                            break;
                        }
                    }
                }
            });
        }
    }
}