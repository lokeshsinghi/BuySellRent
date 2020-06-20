package com.example.buysellrent.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.buysellrent.Class.User;
import com.example.buysellrent.MainActivity;
import com.example.buysellrent.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;
    ProgressBar progressBar;
    TextView username, editProfile;
    CircleImageView profilePic;
    Button signOut;
    FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        signOut = root.findViewById(R.id.signOut);
        username = root.findViewById(R.id.name);
        editProfile = root.findViewById(R.id.editProfile);
        profilePic = root.findViewById(R.id.dp);
        progressBar = root.findViewById(R.id.progress_bar);
        mAuth = FirebaseAuth.getInstance();

        editProfile.setText(R.string.edit);

        //SignOut
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // For getting offline from server.{

                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase databaseReference=FirebaseDatabase.getInstance();
                final DatabaseReference lastConnected=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("lastConnected");
                final DatabaseReference infoConnected=databaseReference.getReference(".info/connected");

                infoConnected.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean connected=dataSnapshot.getValue(Boolean.class);

                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("status");
                        DatabaseReference databaseReference2=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("lastConnected");

                        lastConnected.setValue(ServerValue.TIMESTAMP);
                        databaseReference.setValue("offline");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("Error"+databaseError);
                    }
                });
            // }

                mAuth.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user1 = dataSnapshot.getValue(User.class);
                        progressBar.setVisibility(View.GONE);
                        if(getActivity() == null)
                            return;
                        if(user1.getImageUrl().equals("")) {
                            profilePic.setImageResource(R.drawable.ic_deafault_dp);
                        }
                        else
                            Glide.with(getActivity()).load(user1.getImageUrl()).into(profilePic);
                        username.setText(user1.getFullName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //Edit Profile
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
            }
        });



        return root;
    }
}