package com.example.buysellrent.ui.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buysellrent.Adapter.MessageAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView dp;
    private TextView name;
    private TextView status;
    private FirebaseUser firebaseUser;
    private EditText message;
    private MessageAdapter messageAdapter;
    private List<ChatBox> chatBoxList;
    private RecyclerView recyclerView;
    private String userid;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        dp = findViewById(R.id.chat_dp);
        name = findViewById(R.id.chat_name);
        status = findViewById(R.id.status);
        message = findViewById(R.id.chat_message);
        ImageView ivSend = findViewById(R.id.send_chat);

        recyclerView = findViewById(R.id.message_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        userid = getIntent().getStringExtra("userid");
       // String type = getIntent().getStringExtra("type");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(userid);

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userid,msg);
                }
                else{
                    Toast.makeText(MessageActivity.this,"Type a message to send",Toast.LENGTH_SHORT).show();
                }
                message.setText("");
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setStatus(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        seenMessage(userid);
    }

    private void setStatus(DataSnapshot dataSnapshot){
        User user =dataSnapshot.getValue(User.class);

        if (user != null) {
            name.setText(user.getFullName());

            if (user.getImageUrl().equals("")) {
                dp.setImageResource(R.drawable.logo_temp);
            } else {
                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(dp);
            }
            readMessage(firebaseUser.getUid(), userid, user.getImageUrl());
        }

        if(Objects.equals(dataSnapshot.child("status").getValue(), "online")) {
            status.setText(R.string.online);
        }else{
            String timeValue = Objects.requireNonNull(dataSnapshot.child("lastConnected").getValue()).toString();
            long temp=Long.parseLong(timeValue);

            Calendar smsTime = Calendar.getInstance();
            smsTime.setTimeInMillis(temp);
            Calendar now=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat;
            if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)){
                 simpleDateFormat=new SimpleDateFormat("hh:mm a", Locale.getDefault());
            } else {
                 simpleDateFormat=new SimpleDateFormat("dd-MMM hh:mm a", Locale.getDefault());
            }
            String time = getString(R.string.last_seen)+" "+simpleDateFormat.format(temp);
            status.setText(time);
        }
    }

    private void seenMessage(final String userid){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatBox chatBox = snapshot.getValue(ChatBox.class);
                    assert chatBox != null;
                    if (chatBox.getReceiver().equals(firebaseUser.getUid()) && chatBox.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("sender",sender);
        hashmap.put("receiver",receiver);
        hashmap.put("message",message);
        hashmap.put("isseen",false);

        reference.child("Chats").push().setValue(hashmap);

        final DatabaseReference dataref = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userid);

        dataref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    dataref.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver).child(firebaseUser.getUid());
        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    chatRef1.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readMessage(final String myid, final String userid, final String imageurl){

        chatBoxList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatBoxList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatBox chatBox = snapshot.getValue(ChatBox.class);
                    assert chatBox != null;
                    if(chatBox.getReceiver().equals(myid) && chatBox.getSender().equals(userid) ||
                            chatBox.getReceiver().equals(userid) && chatBox.getSender().equals(myid)){
                        chatBoxList.add(chatBox);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, chatBoxList,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)  {

            }
        });
    }



}