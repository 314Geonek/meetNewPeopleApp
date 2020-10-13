package com.golab.meetnewpeopleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ImageViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.golab.meetnewpeopleapp.Cards.Array_Adapter;
import com.golab.meetnewpeopleapp.Cards.cards;
import com.golab.meetnewpeopleapp.matches.MatchesActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    List<cards> rowItems;
    private cards card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        mAuth= FirebaseAuth.getInstance();
        db=  FirebaseFirestore.getInstance();

        FillMyData();


    }

    public void goToMainActivity(View view) {
        finish();
        return;
    }

    public void goToMatches(View view) {
        Intent intent=new Intent(MyProfileActivity.this, MatchesActivity.class);
        startActivity(intent);
        finish();
        return;
    }
    private void FillMyData()
    {   db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            if(documentSnapshot.exists()){
                String profileImageUrl = "default";
                if (!documentSnapshot.get("profileImageUrl").toString().equals("default")) {
                    profileImageUrl = documentSnapshot.get("profileImageUrl").toString();
                }
                cards item = new cards(documentSnapshot.getId(), documentSnapshot.get("name").toString(), profileImageUrl);


            }
        }
    });

    }

    public void goToProfileEdit(View view) {
        Intent intent=new Intent(MyProfileActivity.this, Settings.class);
        startActivity(intent);
        finish();
        return;
    }
}