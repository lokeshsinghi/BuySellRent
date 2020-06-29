package com.example.buysellrent.ui.home.Ads;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.buysellrent.R;

public class DescriptionFragment extends Fragment {

    TextView description;
    private String desc;
    View root;
    public DescriptionFragment(String desc) {
        this.desc = desc;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_description, container, false);
        description = root.findViewById(R.id.description);
        description.setText(desc);
        // Inflate the layout for this fragment
        return root;
    }
}