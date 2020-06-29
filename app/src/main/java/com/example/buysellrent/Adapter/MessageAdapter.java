package com.example.buysellrent.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dmallcott.dismissibleimageview.DismissibleImageView;
import com.example.buysellrent.R;
import com.example.buysellrent.Class.ChatBox;
import com.example.buysellrent.ui.chat.FullScreenView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

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
            final ChatBox chatBox = mchat.get(position);
            if(chatBox.getType().equals("image")) {
                holder.show_message.setVisibility(View.GONE);
                Glide.with(mcontext).load(chatBox.getMessage()).into(holder.imageMessage);
            }
            else {
                holder.imageMessage.setVisibility(View.GONE);
                holder.show_message.setText((chatBox.getMessage()));
            }
            if(imageurl.equals("")){
                holder.profilepic.setImageResource(R.drawable.logo_temp);
            }
            else{
                Glide.with(mcontext).load(imageurl).into(holder.profilepic);
            }
            if(position==mchat.size()-1) {
                if (chatBox.isIsseen()) {
                    holder.text_seen.setText("seen");
                } else {
                    holder.text_seen.setText("sent");
                }
            }
            else{
                holder.text_seen.setVisibility(View.GONE);
            }
            holder.imageMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mcontext, FullScreenView.class);
                    intent.setData(Uri.parse(chatBox.getMessage()));
                    mcontext.startActivity(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
        return mchat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profilepic;
        public TextView text_seen;
        public ImageView imageMessage;

        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profilepic = itemView.findViewById(R.id.chat_dp);
            text_seen=itemView.findViewById(R.id.seen);
            imageMessage = itemView.findViewById(R.id.image);
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
