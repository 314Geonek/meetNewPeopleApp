package com.golab.meetnewpeopleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText mEmail;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mEmail=(EditText) findViewById(R.id.emailForReset);
        mAuth= FirebaseAuth.getInstance();

    }

    public void back(View view) {
        finish();
        return;
    }

    public void resetPassword(View view) {
        if(mAuth != null) {
            Toast.makeText(ResetPasswordActivity.this,"Recovery Email has been  sent to " + mEmail.getText().toString(), Toast.LENGTH_LONG).show();
            Log.w(" if Email authenticated", "Recovery Email has been  sent to " + mEmail.getText().toString());
            mAuth.sendPasswordResetEmail(mEmail.getText().toString());
            finish();
            return;
        } else {
            Toast.makeText(ResetPasswordActivity.this,"Error", Toast.LENGTH_LONG).show();
            Log.w(" error ", " bad entry ");
        }
    }
}