package com.example.buysellrent.ui.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.buysellrent.Adapter.MessageAdapter;
import com.example.buysellrent.Class.ChatBox;
import com.example.buysellrent.Class.User;
import com.example.buysellrent.Notification.Data;
import com.example.buysellrent.Notification.Sender;
import com.example.buysellrent.Notification.Token;
import com.example.buysellrent.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView dp;
    private TextView name, status;
    private FirebaseUser firebaseUser;
    private EditText message;
    private MessageAdapter messageAdapter;
    private List<ChatBox> chatBoxList;
    private RecyclerView recyclerView;
    private String userid, imageUrl, type;
    private ImageView sendImage;
    private Uri uri;
    private RequestQueue requestQueue;
    private boolean notify = false;
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
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        type = getIntent().getStringExtra("type");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        currentUser(userid);

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


        //Send Image message
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence[] options = new CharSequence[]{"Take a photo", "Choose from gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setTitle("Choose an option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                        PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.CAMERA) ==
                                        PackageManager.PERMISSION_GRANTED) {
                                    callCamera();
                                } else {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 10);
                                }
                            } else {
                                callCamera();
                            }
                        } else if (i == 1) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select an image"), 438);
                        }
                    }
                });
                builder.show();
            }
        });

    }

    //Check whether required permissions were granted or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                callCamera();
            } else {
                Toast.makeText(MessageActivity.this, "Grant Permission to continue.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Open camera to take pictures
    private void callCamera() {
        Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            String authorities = getApplicationContext().getPackageName() + ".fileprovider";
            File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
            Uri imageUri = FileProvider.getUriForFile(MessageActivity.this, authorities, file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, 448);
        }
    }

    //Activity result for image picker/camera activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 438 || requestCode == 448) && resultCode == RESULT_OK && data != null) {
            File file = null;
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Sending...");
            progressDialog.show();
            if (requestCode == 448) {
                file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                uri = FileProvider.getUriForFile(MessageActivity.this, getApplicationContext().getPackageName() + ".fileprovider", file);
            } else {
                uri = data.getData();
            }
            assert uri != null;
            notify = true;
            Log.d("Capture", uri.toString());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Message");

            final StorageReference filePath = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));

            final File finalFile = file;
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
                            final DatabaseReference dataref = FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(firebaseUser.getUid())
                                    .child(userid);

                            String patternForDateExtended = "yyyyMMddHHmmss";
                            @SuppressLint("SimpleDateFormat") DateFormat dff = new SimpleDateFormat(patternForDateExtended);
                            Date today = Calendar.getInstance().getTime();
                            String rightNowEverything = dff.format(today) + Calendar.getInstance().get(Calendar.MILLISECOND);
                            final Map<String, Object> mp = new HashMap<>();
                            mp.put("id", userid);
                            mp.put("last", rightNowEverything);

                            dataref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataref.setValue(mp);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            final Map<String, Object> mp2 = new HashMap<>();
                            mp2.put("id", firebaseUser.getUid());
                            mp2.put("last", rightNowEverything);

                            final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(userid).child(firebaseUser.getUid());
                            chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    chatRef1.setValue(mp2);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            if (finalFile != null) {
                                boolean delete = finalFile.delete();
                            }
                            progressDialog.dismiss();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (notify) {
                                        sendNotification(userid, user.getFullName(), "Image");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //if the upload is not successfull
                    //hiding the progress dialog
                    if (finalFile != null) {
                        boolean delete = finalFile.delete();
                    }
                    progressDialog.dismiss();


                    //and displaying error message
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = 0;

                    progress = (double) (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("In progress " + ((int) progress) + "%");
                }
            });
        }
    }

    //Get file extension for a file
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //Set dp and online/offline status
    private void setStatus(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);

        if (user != null) {
            name.setText(user.getFullName());

            if (user.getImageUrl().equals("")) {
                dp.setImageResource(R.drawable.logo_temp);
            } else {
                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(dp);
            }
            readMessage(firebaseUser.getUid(), userid, user.getImageUrl());
        }

        if (Objects.equals(dataSnapshot.child("status").getValue(), "online")) {
            status.setText(R.string.online);
        } else {
            String timeValue = dataSnapshot.child("lastConnected").getValue().toString();
            long temp = Long.parseLong(timeValue);

            Calendar smsTime = Calendar.getInstance();
            smsTime.setTimeInMillis(temp);
            Calendar now = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat;
            if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
                simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            } else {
                simpleDateFormat = new SimpleDateFormat("dd-MMM hh:mm a", Locale.getDefault());
            }
            String time = getString(R.string.last_seen) + " " + simpleDateFormat.format(temp);
            status.setText(time);
        }
    }

    //Check whether the message sent was seen or not
    private void seenMessage(final String userid) {
        seenReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = seenReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatBox chatBox = snapshot.getValue(ChatBox.class);
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

    //Send a text message
    private void sendMessage(String sender, final String receiver, final String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("sender", sender);
        hashmap.put("receiver", receiver);
        hashmap.put("message", message);
        hashmap.put("isseen", false);
        hashmap.put("type", "text");

        reference.child("Chats").push().setValue(hashmap);

        final DatabaseReference dataref = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userid);

        String patternForDateExtended = "yyyyMMddHHmmss";
        @SuppressLint("SimpleDateFormat") DateFormat dff = new SimpleDateFormat(patternForDateExtended);
        Date today = Calendar.getInstance().getTime();
        String rightNowEverything = dff.format(today) + Calendar.getInstance().get(Calendar.MILLISECOND);
        final Map<String, Object> mp = new HashMap<>();
        mp.put("id", receiver);
        mp.put("last", rightNowEverything);

        dataref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataref.setValue(mp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Map<String, Object> mp2 = new HashMap<>();
        mp2.put("id", sender);
        mp2.put("last", rightNowEverything);

        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver).child(firebaseUser.getUid());
        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatRef1.setValue(mp2);
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
                if (notify) {
                    sendNotification(receiver, user.getFullName(), message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Message notification for messages
    private void sendNotification(final String receiver, final String fullName, final String msg) {
        DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = token.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Token token1 = dataSnapshot1.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, fullName + ": " + msg, "New Message", receiver);
                    Sender sender = new Sender(data, token1.getToken());

                    try {
                        JSONObject senderJSONOnj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJSONOnj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("JSON_RESPONSE", "onResponse : " + response.toString());
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse : " + error.toString());
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAAagxZTEU:APA91bHV3UAqSyONQ7hDLyAQchbDOEh7EltWkAWiNmd2WvGyRUWurusxsE9p1pmDO9lEESHgl9eID5BIiszRmOuaUIovqi-4SjykerrglVMBy6eBoNPSesatQ6x9zo0mivJwfRIskJnU");

                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Show the chat between users
    private void readMessage(final String myId, final String userId, final String imageUrl) {

        chatBoxList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatBoxList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatBox chatBox = snapshot.getValue(ChatBox.class);
                    if (chatBox.getReceiver().equals(myId) && chatBox.getSender().equals(userId) ||
                            chatBox.getReceiver().equals(userId) && chatBox.getSender().equals(myId)) {
                        chatBoxList.add(chatBox);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, chatBoxList, imageUrl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Shared Preference for notifications
    private void currentUser(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("SP_USER", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CURRENT_USER_ID", userId);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        seenReference.removeEventListener(seenListener);
        currentUser("none");
    }

    @Override
    protected void onResume() {
        super.onResume();
        seenReference.addListenerForSingleValueEvent(seenListener);
        currentUser(userid);
    }
}