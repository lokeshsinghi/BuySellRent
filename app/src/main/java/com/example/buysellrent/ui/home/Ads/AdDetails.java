package com.example.buysellrent.ui.home.Ads;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.buysellrent.ui.chat.MessageActivity;
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
    Button sendMessage;
    Button call;
    private int dotscount;
    private String sellerId;
    private String phoneNum;
    private String description;
    private ImageView[] dots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);
        adImages = (ViewPager)findViewById(R.id.adImages);
        sendMessage = (Button) findViewById(R.id.send_message);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdDetails.this, MessageActivity.class);
                intent.putExtra("userid",sellerId);
                startActivity(intent);
            }
        });
        call = (Button) findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNum, null));
                startActivity(intent);
            }
        });
        sliderDotspanel = (LinearLayout) findViewById(R.id.sliderDots);
        Intent intent = getIntent();
        String adId = intent.getStringExtra("adId");
        ArrayList<String> images = intent.getStringArrayListExtra("images");

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,adId,images);
        adImages.setAdapter(viewPagerAdapter);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(),2000,4000);
        dotscount =viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];
        tabLayout = findViewById(R.id.specs_tab);
        specsPager = findViewById(R.id.specs_pager);
        tabLayout.setupWithViewPager(specsPager);
        final TextView adTitle = (TextView) findViewById(R.id.adTitle);
        final MoneyTextView adPrice = (MoneyTextView) findViewById(R.id.adPrice);



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ads").child(adId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adTitle.setText(dataSnapshot.child("title").getValue(String.class));
                adPrice.setAmount(dataSnapshot.child("price").getValue(float.class));
                description = dataSnapshot.child("desc").getValue(String.class);
                sellerId = dataSnapshot.child("sellerId").getValue(String.class);
                phoneNum = dataSnapshot.child("number").getValue(String.class);
                setUpViewPager(specsPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        specsPagerAdapter.addFragment(new DescriptionFragment(description),"DESCRIPTION");
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