package com.golab.meetnewpeopleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.golab.meetnewpeopleapp.matches.MatchesActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfileActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AppCompatImageView ivPicture;
    private AppCompatTextView tvNameAge, tvLocation;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        mAuth= FirebaseAuth.getInstance();
        db=  FirebaseFirestore.getInstance();
        ivPicture= findViewById(R.id.image);
        tvNameAge = findViewById(R.id.nameAgeTxt);
        tvLocation = findViewById(R.id.locationNameTxt);
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
    {
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            if(documentSnapshot.exists()){
                fillCard(documentSnapshot);
            }
        }
    });

    }
    private void  fillCard(DocumentSnapshot documentSnapshot)
    {   RequestOptions options = new RequestOptions();
        options.centerCrop();
        userId=documentSnapshot.getId();
        if (!documentSnapshot.get("profileImageUrl").toString().isEmpty()) {
            Glide.with(getApplication()).load(documentSnapshot.get("profileImageUrl").toString()).apply(options).into(ivPicture);
        if(!documentSnapshot.get("name").toString().isEmpty())
            tvNameAge.setText(documentSnapshot.get("name").toString());
        }
    }
    public void goToProfileEdit(View view) {
        Intent intent=new Intent(MyProfileActivity.this, Settings.class);
        startActivity(intent);
        finish();
        return;
    }
    public void  goToDescription(View view) {

        Intent intent=new Intent(MyProfileActivity.this, MyProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        startActivity(intent);
    }

}