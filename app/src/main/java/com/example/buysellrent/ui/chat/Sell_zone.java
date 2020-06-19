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

import com.example.buysellrent.Adapter.UserAdapter;
import com.example.buysellrent.Class.ChatBox;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Sell_zone extends Fragment {


    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private Set<User> mUsers;
    private List<String> usersList;
    private List<User> mmm;
    ProgressBar progressBar;
    DatabaseReference reference;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_sell_zone, container, false);
        recyclerView=view.findViewById(R.id.sell_view);
        progressBar =view.findViewById(R.id.pBar2);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
        usersList=new ArrayList<>();



        reference= FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ChatBox chatBox=snapshot.getValue(ChatBox.class);

                    assert fuser!=null;

                    if(chatBox.getSender().equals(fuser.getUid())){
                        usersList.add(chatBox.getReceiver());
                    }
                    if(chatBox.getReceiver().equals(fuser.getUid())){
                        usersList.add(chatBox.getSender());
                    }
                }
                for(String a:usersList)
                {
                    System.out.println(a);
                }

                readChats();


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    private void readChats() {
        mUsers=new HashSet<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    assert user!=null;
                    for(String id: usersList)
                    {
                        if(user.getId().equals(id)){

                                mUsers.add(user);

                        }
                    }
                }
                mmm=new ArrayList<>();
                for(User user:mUsers)
                {
                    mmm.add(user);
                }
                progressBar.setVisibility(View.GONE);
                userAdapter=new UserAdapter(getContext(),mmm,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}