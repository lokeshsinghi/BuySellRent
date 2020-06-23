package com.example.buysellrent.ui.chat;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buysellrent.Adapter.MessageAdapter;
import com.example.buysellrent.Class.ChatBox;
import com.example.buysellrent.Class.User;
import com.example.buysellrent.Notification.Client;
import com.example.buysellrent.Notification.Data;
import com.example.buysellrent.Notification.Response;
import com.example.buysellrent.Notification.Sender;
import com.example.buysellrent.Notification.Token;
import com.example.buysellrent.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

import com.example.buysellrent.Notification.APIService;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView dp;
    private TextView name;
    private TextView status;
    private FirebaseUser firebaseUser;
    private EditText message;
    private MessageAdapter messageAdapter;
    private List<ChatBox> chatBoxList;
    private RecyclerView recyclerView;
    private String userid, imageUrl;
    private ImageView sendImage;
    private Uri uri;
    APIService apiService;
    boolean notify = false;
    private String type;
    private ValueEventListener seenListener;
    private DatabaseReference seenReference;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        dp = findViewById(R.id.chat_dp);
        name = findViewById(R.id.chat_name);
        status = findViewById(R.id.status);
        message = findViewById(R.id.chat_message);
        sendImage = findViewById(R.id.image);
        ImageView ivSend = findViewById(R.id.send_chat);

        recyclerView = findViewById(R.id.message_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        userid = getIntent().getStringExtra("userid");
        // String type = getIntent().getStringExtra("type");
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        type = getIntent().getStringExtra("type");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = message.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(firebaseUser.getUid(), userid, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "Type a message to send", Toast.LENGTH_SHORT).show();
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


        //Send Image
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[] {"Take a photo", "Choose from gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setTitle("Choose an option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0) {

                        }
                        else if(i == 1) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Select an image"), 438);
                        }
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Message");

            final DatabaseReference dataref = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(firebaseUser.getUid())
                    .child(userid);

            dataref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        dataref.child("id").setValue(userid);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(userid).child(firebaseUser.getUid());
            chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        chatRef1.child("id").setValue(firebaseUser.getUid());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //ok up to here
            final StorageReference filePath = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = uri.toString();

                            HashMap<String, Object> hashmap = new HashMap<>();
                            hashmap.put("sender", firebaseUser.getUid());
                            hashmap.put("receiver", userid);
                            hashmap.put("message", imageUrl);
                            hashmap.put("isseen", false);
                            hashmap.put("type", "image");

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child("Chats").push().setValue(hashmap);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //if the upload is not successfull
                    //hiding the progress dialog
//                    progressDialog.dismiss();


                    //and displaying error message
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress=0;

                            progress = (double) (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
//                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
    }


    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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
            String timeValue = dataSnapshot.child("lastConnected").getValue().toString();
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
        seenReference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=seenReference.addValueEventListener(new ValueEventListener() {
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

    private void sendMessage(String sender, final String receiver, final String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("sender",sender);
        hashmap.put("receiver",receiver);
        hashmap.put("message",message);
        hashmap.put("isseen",false);
        hashmap.put("type", "text");

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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(sender);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(notify) {
                    sendNotification(receiver, user.getFullName(), message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(final String receiver, final String fullName, final String msg) {
        DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = token.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Token token1 = dataSnapshot1.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, fullName + ": " + msg, "New Message", receiver);
                    Sender sender = new Sender(data, token1.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if(!response.isSuccessful()) {
                                        Toast.makeText(MessageActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
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

    @Override
    protected void onPause() {
        super.onPause();
        seenReference.removeEventListener(seenListener);


    }

    @Override
    protected void onResume() {
        super.onResume();
        seenReference.addListenerForSingleValueEvent(seenListener);
    }
}