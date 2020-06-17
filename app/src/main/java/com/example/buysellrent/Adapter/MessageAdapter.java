package com.example.buysellrent.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buysellrent.R;
import com.example.buysellrent.ui.chat.ChatBox;
import com.example.buysellrent.ui.chat.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mcontext;
    private List<ChatBox> mchat;
    private String imageurl;
    FirebaseUser fuser;


    public MessageAdapter(Context mcontext, List<ChatBox> mchat, String imageurl){
        this.mchat = mchat;
        this.mcontext = mcontext;
        this.imageurl = imageurl;
    }
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent,int viewType){
        if(viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);}
        else{
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChatBox chatBox = mchat.get(position);
            holder.show_message.setText((chatBox.getMessage()));
            if(imageurl.equals("")){
                holder.profilepic.setImageResource(R.drawable.logo_temp);
            }
            else{
                Glide.with(mcontext).load(imageurl).into(holder.profilepic);}

    }

    @Override
    public int getItemCount() {

        return mchat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profilepic;

        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profilepic = itemView.findViewById(R.id.chat_dp);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mchat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
