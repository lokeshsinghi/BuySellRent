package com.example.buysellrent.ui.ads;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.buysellrent.R;

public class AdsFragment extends Fragment {

    private AdsViewModel adsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        adsViewModel =
                ViewModelProviders.of(this).get(AdsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ads, container, false);
        final TextView textView = root.findViewById(R.id.text_ads);
        adsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}