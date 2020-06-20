package com.example.buysellrent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.buysellrent.Class.CustomProgressBar;
import com.example.buysellrent.Class.User;
import com.example.buysellrent.SignIn.EmailSignIn;
import com.example.buysellrent.SignIn.EmailVerification;
import com.example.buysellrent.SignIn.PhoneSignIn;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button phoneButton, gButton, emailButton;
    Button fButton;
    LoginButton defaultBtn;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private int RC_SIGN_IN = 1;
    private CallbackManager callbackManager;
    CustomProgressBar customProgressBar;
    private static final String TAG = "FACEBOOKAUTHENTICATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneButton = findViewById(R.id.phone);
        gButton = findViewById(R.id.google);
        fButton = findViewById(R.id.facebook);
        emailButton = findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        customProgressBar = new CustomProgressBar(MainActivity.this);
        callbackManager = CallbackManager.Factory.create();

        updateUI(firebaseUser);

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

        //Google signInClient dialog builder
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);


        //Phone signIn
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PhoneSignIn.class));
            }
        });

        //Email and password signIn and signUp
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EmailSignIn.class));
            }
        });

        //Google SignIn
        gButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressBar.loadDialog();
                signIn();
            }
        });

//        Facebook SignIn
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email"));
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleAccessToken" + accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Authentication Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    FUpdateUI(user);
                }
                else{
                    customProgressBar.dismissDialog();
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Update database after login through facebook
    private void FUpdateUI(FirebaseUser user) {
        if(user != null) {
            String name = "", email = "", id, url = "", phonenum;
            for (UserInfo profile : user.getProviderData()) {
                String providerId = profile.getProviderId();
                if(providerId.equals("facebook.com")) {
                    email = profile.getEmail();
                    name = profile.getDisplayName();
                    url = profile.getPhotoUrl().toString();
                    phonenum = profile.getPhoneNumber();
                }
            }
            id = user.getUid();
            User user1 = new User(name, email, id, url);

            FirebaseDatabase.getInstance().getReference("Users")
                    .child(id).setValue(user1)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                customProgressBar.dismissDialog();
                                Intent intent = new Intent(MainActivity.this, startScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
        }
    }

    //Google SignIn start
    private void signIn() {
        googleSignInClient.signOut();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            GUpdateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            GUpdateUI(null);
                        }
                    }
                });
    }

    //After signing in with google. Adding the user's data to firebase
    public void GUpdateUI(FirebaseUser user) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (user != null && googleSignInAccount != null) {
            String name = googleSignInAccount.getDisplayName();
            String email = googleSignInAccount.getEmail();
            String imageUrl = googleSignInAccount.getPhotoUrl().toString();
            String id = user.getUid();
            User user1 = new User(name, email, id, imageUrl);

            FirebaseDatabase.getInstance().getReference("Users")
                    .child(id).setValue(user1)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            customProgressBar.dismissDialog();
                            if(task.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, startScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                //error message
                            }
                        }
                    });
        }
    }
    //Check if user is already logged in or not
    public void updateUI(FirebaseUser user) {
        if (user != null) {
            if(user.isEmailVerified()) {
                startActivity(new Intent(MainActivity.this, startScreen.class));
            }
            else {
                Intent intent = new Intent(MainActivity.this, EmailVerification.class);
                intent.putExtra("Email", user.getEmail());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            finish();
        }
    }
}