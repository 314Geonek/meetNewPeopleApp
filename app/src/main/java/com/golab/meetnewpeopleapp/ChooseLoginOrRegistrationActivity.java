package com.golab.meetnewpeopleapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChooseLoginOrRegistrationActivity extends AppCompatActivity {
    private Button mLogin;
    private Button mRegistration;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_or_registration);
        mLogin= findViewById(R.id.login);
        mRegistration= findViewById(R.id.registration);
        mAuth = FirebaseAuth.getInstance();
//        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener()
//        {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
//            {
//                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user != null) {
//                    if (user.isEmailVerified()) {
//                        Intent intent = new Intent(ChooseLoginOrRegistrationActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//
//            }
//        };
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChooseLoginOrRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChooseLoginOrRegistrationActivity.this, Registration.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onStart()
    {
    //    mAuth.addAuthStateListener(firebaseAuthStateListener);
        super.onStart();

    }
    @Override
    protected void onStop()
    {
        super.onStop();
       // mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}