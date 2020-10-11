package com.golab.meetnewpeopleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.golab.meetnewpeopleapp.matches.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfilMenuActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_menu);
        mAuth=FirebaseAuth.getInstance();
    }
    public void goToMainActivity(View view) {
        finish();
        return;
    }

    public void goToMatches(View view) {
        Intent intent=new Intent(ProfilMenuActivity.this, MatchesActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToMyProfile(View view) {
    }

    public void goToProferences(View view) {
    }

    public void logOut(View view) {
            mAuth.signOut();
            Intent intent=new Intent(ProfilMenuActivity.this, ChooseLoginOrRegistrationActivity.class);
            startActivity(intent);
            finish();
            return;
    }

    public void goToProfileEdit(View view) {
        Intent intent=new Intent(ProfilMenuActivity.this, Settings.class);
        startActivity(intent);
        finish();
        return;
    }
}