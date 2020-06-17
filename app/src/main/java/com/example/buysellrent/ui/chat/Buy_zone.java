package com.example.buysellrent.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.buysellrent.Adapter.UserAdapter;
import com.example.buysellrent.Class.User;
import com.example.buysellrent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.buysellrent.R.string.error;


public class Buy_zone extends Fragment {


    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    ProgressBar progressBar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       View view =inflater.inflate(R.layout.fragment_buy_zone, container, false);
       recyclerView=view.findViewById(R.id.buy_view);
        progressBar =view.findViewById(R.id.pBar);
        progressBar.setVisibility(View.VISIBLE);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

       mUsers=new ArrayList<>();
       readUsers();
       return view;
    }

    private void readUsers() {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        User user=snapshot.getValue(User.class);
                        assert user!=null;
                        assert firebaseUser!=null;

                        if(!user.getId().equals(firebaseUser.getUid())){
                            mUsers.add(user);
                        }
                }
                progressBar.setVisibility(View.GONE);
                userAdapter=new UserAdapter(getContext(),mUsers);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}