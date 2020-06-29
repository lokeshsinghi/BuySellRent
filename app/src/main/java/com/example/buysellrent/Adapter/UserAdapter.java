package com.example.buysellrent.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buysellrent.Class.ChatBox;
import com.example.buysellrent.Class.User;
import com.example.buysellrent.R;
import com.example.buysellrent.ui.chat.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;
    String lastMessage;
    boolean isImage, isSeen;


    public UserAdapter(Context mContext, List<User> mUsers,boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getFullName());

        if(user.getImageUrl().equals(""))
        {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Glide.with(mContext).load(user.getImageUrl()).into(holder.profile_image);
        }
        if(isChat) {
            lastMessage(user.getId(), holder.last, holder.imageMsg, holder.username);
        }
        if(isChat){

            if(user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.img_on.setVisibility(View.GONE);
            }
        }
        else
            holder.img_on.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                intent.putExtra("type","online");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private TextView last;
        private  ImageView imageMsg;

        public ViewHolder(View itemView){
            super(itemView);

            username=itemView.findViewById(R.id.user_name);
            img_on=itemView.findViewById(R.id.img_on);
            profile_image=itemView.findViewById(R.id.user_image);
            last= itemView.findViewById(R.id.last_msg);
            imageMsg = itemView.findViewById(R.id.imageMsg);
        }
    }

    private void lastMessage(final String userId, final TextView last, final ImageView imageMsg, final TextView userName) {
        lastMessage = "";
        isImage = false;
        isSeen = true;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ChatBox chatBox = ds.getValue(ChatBox.class);
                    if(chatBox.getReceiver().equals(user.getUid()) && chatBox.getSender().equals(userId)) {
                        if(chatBox.getType().equals("text")) {
                            lastMessage = chatBox.getMessage();
                            isImage = false;
                        }
                        else{
                            lastMessage = "Photo:";
                            isImage = true;
                        }
                        if(!chatBox.isIsseen())
                            isSeen = false;
                    }
                    if(chatBox.getSender().equals(user.getUid()) && chatBox.getReceiver().equals(userId)) {
                        if(chatBox.getType().equals("text")) {
                            lastMessage = "You: " + chatBox.getMessage();
                            isImage = false;
                        }
                        else{
                            isImage = true;
                            lastMessage = "You:";
                        }
                    }
                }

                //Set last message
                if(lastMessage.isEmpty()) {
                    last.setText(R.string.noMsg);
                }
                else {
                    last.setText(lastMessage);
                }

                //For image message
                if(isImage) {
                    imageMsg.setVisibility(View.VISIBLE);
                }

                //set unread message bold
                if(!isSeen) {
                    last.setTypeface(null, Typeface.BOLD);
                    userName.setTypeface(null, Typeface.BOLD);
                }
                else {
                    last.setTypeface(null, Typeface.NORMAL);
                    userName.setTypeface(null, Typeface.NORMAL);
                }
                //Set to default value
                isImage = false;
                lastMessage = "";
                isSeen = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
