package com.example.buysellrent.ui.home.Ads;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.buysellrent.Adapter.SectionPagerAdapter;
import com.example.buysellrent.Adapters.ViewPagerAdapter;
import com.example.buysellrent.R;
import com.google.android.material.tabs.TabLayout;

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
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        adImages.setAdapter(viewPagerAdapter);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(),2000,4000);
        dotscount =viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];
        tabLayout = findViewById(R.id.specs_tab);
        specsPager = findViewById(R.id.specs_pager);
        setUpViewPager(specsPager);
        tabLayout.setupWithViewPager(specsPager);

        for(int i=0; i < dotscount; i++){
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0,8,0);
            sliderDotspanel.addView(dots[i],params);

        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.active_dot));
        adImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i=0;i<dotscount;i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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