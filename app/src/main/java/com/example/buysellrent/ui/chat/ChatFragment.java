package com.example.buysellrent.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.buysellrent.Adapters.SectionPagerAdapter;
import com.example.buysellrent.R;
import com.google.android.material.tabs.TabLayout;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class ChatFragment extends Fragment {

    //private ChatViewModel chatViewModel;
    TabLayout tabLayout;
    ViewPager viewPager;
    View root;

    public ChatFragment(){

    }
    public static  ChatFragment getInstance()
    {
        return new ChatFragment();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_chat, container, false);

        tabLayout=root.findViewById(R.id.chat_tab);
        viewPager=root.findViewById(R.id.chat_view);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPagerAdapter adapter=new SectionPagerAdapter(getChildFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFragment(new Buy_zone(),"BUYING");
        adapter.addFragment(new Sell_zone(),"SELLING");
        viewPager.setAdapter(adapter);
    }
}