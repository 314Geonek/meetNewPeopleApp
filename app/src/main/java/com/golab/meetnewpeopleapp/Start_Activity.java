package com.golab.meetnewpeopleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.golab.meetnewpeopleapp.admin.AdminMainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Start_Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_);
        mAuth= FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    if (user.isEmailVerified()) {
                        goToActivity(user);
                    }else {goToLogin();
                    }
                }else{
               goToLogin();
                }
            }
        };
    }

    private void goToActivity(FirebaseUser user) {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Intent intent;
                if (documentSnapshot.exists()) {
                    intent = new Intent(Start_Activity.this, MainActivity.class);
                }else {
                    intent = new Intent(Start_Activity.this, AdminMainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });
    }

    private void goToLogin()
    {
        Intent intent;
        intent = new Intent(Start_Activity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}