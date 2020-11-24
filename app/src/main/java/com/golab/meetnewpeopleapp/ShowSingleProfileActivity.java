package com.golab.meetnewpeopleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.golab.meetnewpeopleapp.Cards.Cards;
import com.golab.meetnewpeopleapp.matches.MatchesActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class ShowSingleProfileActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AppCompatImageView ivPicture;
    private AppCompatTextView tvNameAge, tvLocation;
    private String userId;
    private Cards profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_single_profile);
        userId = getIntent().getExtras().getString("id");
        mAuth= FirebaseAuth.getInstance();
        if(!userId.equals(mAuth.getCurrentUser().getUid())) {
            findViewById(R.id.btnGoToProfileEdit).setVisibility(View.GONE);
            findViewById(R.id.btnRemoveMatch).setVisibility(View.VISIBLE);
            findViewById(R.id.btnBack).setVisibility(View.VISIBLE);
            findViewById(R.id.profile).setVisibility(View.GONE);
            findViewById(R.id.btnMainActivity).setVisibility(View.GONE);
            findViewById(R.id.btnMatchesActivity).setVisibility(View.GONE);
            findViewById(R.id.logout).setVisibility(View.GONE);
        }
        db=  FirebaseFirestore.getInstance();
        ivPicture= findViewById(R.id.image);
        tvNameAge = findViewById(R.id.nameAgeTxt);
        tvLocation = findViewById(R.id.locationNameTxt);
        fillData();
    }

    public void goToMainActivity(View view) {
        finish();
        return;
    }

    public void goToMatches(View view) {
        Intent intent=new Intent(ShowSingleProfileActivity.this, MatchesActivity.class);
        startActivity(intent);
        finish();
        return;
    }
    private void fillData() {
        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            if(documentSnapshot.exists()){
                profile=new Cards(documentSnapshot, "" );
                fillCard();
            }
        }
    });

    }
    private void  fillCard() {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        userId=profile.getId();
        tvNameAge.setText(profile.getName());
        if (!profile.getProfileImageUrl().isEmpty())
            Glide.with(getApplication()).load(profile.getProfileImageUrl()).apply(options).into(ivPicture);
        tvLocation.setText("0 km away");
    }



    public void goBack(View view) {
        finish();
    }

    public void goToProfileEdit(View view) {
        Intent intent=new Intent(ShowSingleProfileActivity.this, Settings.class);
        startActivity(intent);
        finish();
        return;
    }
    public void askLogout(View view) {
        findViewById(R.id.makeSureLogout).setVisibility(View.VISIBLE);
    }
    public void sureLogout(View view) {
        mAuth.signOut();
        finish();
        Intent intent=new Intent(ShowSingleProfileActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    public void cancelLogout(View view) {
        hideAskAboutLogout();
    }
    private void hideAskAboutLogout() {
        findViewById(R.id.makeSureLogout).setVisibility(View.GONE);
    }
    public void  goToDescription(View view) {
        Intent intent=new Intent(ShowSingleProfileActivity.this, Description_Activity.class);
        intent.putExtra("myObject", new Gson().toJson(profile));
        startActivity(intent);
    }
    public void askRemoveMatch(View view) {
        findViewById(R.id.makeSureRemoveMatch).setVisibility(View.VISIBLE);
    }
    public void cancelRemoveMatch(View view) {
        hideRemoveMatchMakeSure();
    }
    private void hideRemoveMatchMakeSure() {
        findViewById(R.id.makeSureRemoveMatch).setVisibility(View.GONE);
    }
    public void sureRemoveMatch(View view) {
        String currentUser= mAuth.getCurrentUser().getUid();
        String secondUser = userId;
        db.collection("Matches").document(getIntent().getExtras().getString("idMatch")).delete();
        db.collection("users").document(currentUser).collection("SwipedBy").document(secondUser).delete();
        db.collection("users").document(secondUser).collection("SwipedBy").document(currentUser).delete();
        finish();
    }
    public void doNothingRemoveMatch(View view) {
    }
    public void doNothingLogout(View view) {
    }

}