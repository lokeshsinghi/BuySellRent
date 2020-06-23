package com.example.buysellrent.ui.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.buysellrent.Class.CustomProgressBar;
import com.example.buysellrent.Class.User;
import com.example.buysellrent.MainActivity;
import com.example.buysellrent.R;
import com.example.buysellrent.SignIn.PhoneSignIn;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    Toolbar toolbar;
    CircleImageView profilePic;
    EditText fullName, phoneNum, email;
    Button fbConnect, GConnect;
    ProgressBar progressBar;
    CustomProgressBar customProgressBar;
    private GoogleSignInClient googleSignInClient;
    private int RC_SIGN_IN = 1;
    FirebaseAuth mAuth;
    CallbackManager callbackManager;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fullName = findViewById(R.id.name);
        phoneNum = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        profilePic = findViewById(R.id.dp);
        progressBar = findViewById(R.id.progress_bar);
        fbConnect = findViewById(R.id.connectFb);
        GConnect = findViewById(R.id.connectG);
        toolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();
        customProgressBar = new CustomProgressBar(EditProfile.this);
        callbackManager = CallbackManager.Factory.create();

        toolbar.setTitle("");
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_baseline_close_24);
        upArrow.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user1 = dataSnapshot.getValue(User.class);
                        progressBar.setVisibility(View.GONE);
                        if(user1.getImageUrl().equals("")) {
                            profilePic.setImageResource(R.drawable.ic_deafault_dp);
                        }
                        else {
                            //Temp fix. Get back to it.
                            Glide.with(getApplicationContext()).load(user1.getImageUrl()).into(profilePic);
                        }
                        name = user1.getFullName();
                        fullName.setText(name);
                        email.setText(user1.getEmail());
                        if(!user1.getPhoneNum().equals(""))
                            phoneNum.setText(user1.getPhoneNum().substring(4));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //Google signInClient dialog builder
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(EditProfile.this, gso);

        //Check which providers are linked
        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                fbConnect.setText(R.string.disconnect);
            }
            if (user.getProviderId().equals("google.com")) {
                GConnect.setText(R.string.disconnect);
            }
        }

        //Facebook login manager
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                customProgressBar.loadDialog();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        //Connect or Disconnect to Facebook
        fbConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String isConnected = fbConnect.getText().toString();
                if(isConnected.equalsIgnoreCase("CONNECT"))
                    LoginManager.getInstance().logInWithReadPermissions(EditProfile.this, Arrays.asList("email"));
                else{
                    customProgressBar.loadDialog();
                    for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                        if (user.getProviderId().equals("facebook.com")) {
                            mAuth.getCurrentUser().unlink(user.getProviderId())
                                    .addOnCompleteListener(EditProfile.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Auth provider unlinked from account
                                                customProgressBar.dismissDialog();
                                                fbConnect.setText(R.string.connect);
                                                Toast.makeText(EditProfile.this, "Facebook account disconnected.",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                customProgressBar.dismissDialog();
                                                Toast.makeText(EditProfile.this, task.getException().getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });

        //Connect or disconnect to Google
        GConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String isConnected = GConnect.getText().toString();
                if(isConnected.equalsIgnoreCase("CONNECT")) {
                    customProgressBar.loadDialog();
                    signIn();
                }
                else{
                    customProgressBar.loadDialog();
                    for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                        if (user.getProviderId().equals("google.com")) {
                            mAuth.getCurrentUser().unlink(user.getProviderId())
                                    .addOnCompleteListener(EditProfile.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Auth provider unlinked from account
                                                customProgressBar.dismissDialog();
                                                GConnect.setText(R.string.connect);
                                                Toast.makeText(EditProfile.this, "Google account disconnected.",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                customProgressBar.dismissDialog();
                                                Toast.makeText(EditProfile.this, task.getException().getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        }
                    }
                }
            }
        });

        //Link Phone
        phoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfile.this, PhoneSignIn.class));
            }
        });

    }

    private void signIn() {
        googleSignInClient.signOut();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_done: {
                // done btn functionalities goes here
                if(!fullName.getText().toString().equals(name)) {
                    //update database
                }
                break;
            }
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //Result of Login Manager Activity or Google SignInClient
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        if(requestCode == RC_SIGN_IN) {
            customProgressBar.dismissDialog();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Check if signIn was successful or not
    private void firebaseAuthWithGoogle(String idToken) {
        customProgressBar.loadDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            customProgressBar.dismissDialog();
                            Toast.makeText(EditProfile.this, "Linked to Google", Toast.LENGTH_SHORT).show();
                            GConnect.setText(R.string.disconnect);
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            GUpdateUI(user);
                        } else {
                            customProgressBar.dismissDialog();
                            Toast.makeText(EditProfile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            // If sign in fails, display a message to the user.
//                            GUpdateUI(null);
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
//        Log.d(TAG, "handleAccessToken" + accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        //Link facebook
        mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    customProgressBar.dismissDialog();
                    Toast.makeText(EditProfile.this, "Linked to facebook account", Toast.LENGTH_SHORT).show();
                    fbConnect.setText(R.string.disconnect);
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    FUpdateUI(user);
                }
                else{
                    customProgressBar.dismissDialog();
                    Toast.makeText(EditProfile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    static void loadImage(RequestManager glide, String url, CircleImageView view) {
//        glide.load(url).into(view);
//    }
}