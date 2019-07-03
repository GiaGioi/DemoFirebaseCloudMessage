package com.gioidev.demofirebasecloudmessage.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gioidev.demofirebasecloudmessage.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;


public class LoginActivity extends AppCompatActivity {

    static final int Google_Sign = 123;
    private FirebaseAuth Auth;
    private Button login, logout;
    private ProgressBar progressCircular;
    private ImageView image;
    private TextView tvName;
    GoogleSignInClient signInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        Auth = FirebaseAuth.getInstance();

        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder().requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });
        if (Auth.getCurrentUser() !=null){
            FirebaseUser user = Auth.getCurrentUser();
            updateUI(user);
        }

    }

    void SignIn() {
        progressCircular.setVisibility(View.VISIBLE);
        Intent signIntent = signInClient.getSignInIntent();
        startActivityForResult(signIntent, Google_Sign);
    }
    void Logout(){
        FirebaseAuth.getInstance().signOut();
        signInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Google_Sign) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            GoogleSignInAccount account = null;
            try {
                account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG","firebaseWithGoogle" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressCircular.setVisibility(View.INVISIBLE);
                            Log.d("TAG","SignIn Success!");

                            FirebaseUser user = Auth.getCurrentUser();

                        }else {
                            progressCircular.setVisibility(View.INVISIBLE);
                            Log.d("TAG","SignIn Fail", task.getException());

                            Toast.makeText(LoginActivity.this, "SignIn Failed!", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user){
        if (user!=null){
            String name = user.getDisplayName();
            String email = user.getEmail();
            String photo  = String.valueOf(user.getPhotoUrl());

            tvName.append("Info: \n");
            tvName.append(name +" \n");
            tvName.append(email);

            Picasso.get().load(photo).into(image);
            login.setVisibility(View.INVISIBLE);
            logout.setVisibility(View.VISIBLE);
        }else {
            tvName.setText(getString(R.string.firebase_login));
            Picasso.get().load(R.drawable.ic_firebase_logo).into(image);
            login.setVisibility(View.VISIBLE);
            logout.setVisibility(View.INVISIBLE);
        }
    }

    private void init() {
        image = findViewById(R.id.image);
        tvName = findViewById(R.id.tvName);
        progressCircular = findViewById(R.id.progress_circular);
        login = findViewById(R.id.login);
        logout = findViewById(R.id.logout);
    }


}
