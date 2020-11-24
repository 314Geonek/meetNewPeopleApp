package com.golab.meetnewpeopleapp.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.golab.meetnewpeopleapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class Create_new_admin_Activity extends AppCompatActivity {
    private EditText mEmail, mRepeatEmail, mPassword;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_admin_);
        mEmail = findViewById(R.id.email);
        mRepeatEmail = findViewById(R.id.repeatEmail);
        mPassword = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
    }
    public void validateData(View view)
    {
        if(mEmail.getText().toString().equals(mRepeatEmail.getText().toString()))
            verifyCurrentUser();
        else
            Toast.makeText(Create_new_admin_Activity.this, getResources().getString(R.string.emails_not_match), Toast.LENGTH_LONG).show();
    }
    private void verifyCurrentUser()
    {
        AuthCredential credential = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), mPassword.getText().toString());
        mAuth.getCurrentUser().reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String password = ""+ (Math.random()*(10000000-1000000+1)+10000000);
        mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), password);
        finish();
    }


    public void back(View view) {
        finish();
    }
}