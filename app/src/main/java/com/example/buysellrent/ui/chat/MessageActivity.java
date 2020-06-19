package com.example.buysellrent.ui.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView dp;
    TextView name;
    TextView status;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    EditText message;
    ImageView send;
    MessageAdapter messageAdapter;
    List<ChatBox> mchat;
    RecyclerView recyclerView;
    String userid,type;
    ValueEventListener seenListener;
    ValueEventListener statusListener;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        dp = findViewById(R.id.chat_dp);
        name = findViewById(R.id.chat_name);
        status = findViewById(R.id.status);
        message = findViewById(R.id.chat_message);
        send = findViewById(R.id.send_chat);

        recyclerView = findViewById(R.id.message_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        userid = getIntent().getStringExtra("userid");
        type = getIntent().getStringExtra("type");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(userid);



        send.setOnClickListener(new View.OnClickListener() {
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
                User user =dataSnapshot.getValue(User.class);

                name.setText(user.getFullName());
                if(user.getImageUrl().equals("")){
                    dp.setImageResource(R.drawable.logo_temp);
                }
                else{
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(dp);
                }
                readMessage(firebaseUser.getUid(),userid,user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        setStatus(userid);
        seenMessage(userid);


    }


    private void setStatus(final String uid)
    {
        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(uid).child("status");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String temp=dataSnapshot.getValue(String.class);
                if(temp.equals("online"))
                {
                    status.setText("online");
                }
                else{/*
                    databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(uid).child("lastConnected");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String,String> last= (Map<String, String>) dataSnapshot.getValue();
                            if(last.equals(0))
                            {
                                status.setText("Last seen: "+never"");
                            }
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
                                String time=simpleDateFormat.format(last);
                                status.setText("Last seen: "+time);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),"ERROR"+databaseError,Toast.LENGTH_SHORT).show();
                        }
                    });


                */
                    databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(uid);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //HashMap<String,String > map=new HashMap<>();
                            String time=dataSnapshot.child("lastConnected").getValue().toString();
                            Long temp=Long.parseLong(time);

                            Calendar smsTime = Calendar.getInstance();
                            smsTime.setTimeInMillis(temp);

                            Calendar now=Calendar.getInstance();

                            if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)){
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
                                String timef=simpleDateFormat.format(temp);
                                status.setText("Last seen: "+timef);
                            }
                            else
                            {
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM hh:mm a");
                                String timef=simpleDateFormat.format(temp);
                                status.setText("Last seen: "+timef);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                status.setText(ServerValue.TIMESTAMP.toString());}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"ERROR"+databaseError,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void seenMessage(final String userid){
        databaseReference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ChatBox chatBox=snapshot.getValue(ChatBox.class);
                    if(chatBox.getReceiver().equals(firebaseUser.getUid())&& chatBox.getSender().equals(userid)){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
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

      /*  if(imageurl.equals(""))
            imageurl=findViewById(R.drawable.logo_temp);*/
        mchat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatBox chatBox = snapshot.getValue(ChatBox.class);
                    if(chatBox.getReceiver().equals(myid) && chatBox.getSender().equals(userid) ||
                            chatBox.getReceiver().equals(userid) && chatBox.getSender().equals(myid)){
                        mchat.add(chatBox);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this,mchat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)  {

            }
        });
    }
  /*  private void status(String status){
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);
    }*/

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
        databaseReference.removeEventListener(seenListener);


    }

}