package com.example.buysellrent.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.buysellrent.Class.User;
import com.example.buysellrent.R;
import com.example.buysellrent.ui.chat.MessageActivity;

import org.w3c.dom.Text;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;

    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user=mUsers.get(position);
        holder.username.setText(user.getFullName());

        if(user.getImageUrl().equals(""))
        {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Glide.with(mContext).load(user.getImageUrl()).into(holder.profile_image);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                intent.putExtra("type","BUYER");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        public ViewHolder(View itemView){
            super(itemView);

            username=itemView.findViewById(R.id.user_name);
            profile_image=itemView.findViewById(R.id.user_image);
        }
    }

}
