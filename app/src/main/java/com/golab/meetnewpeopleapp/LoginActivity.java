package com.golab.meetnewpeopleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private Button mLogin;
    private EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLogin= findViewById(R.id.login);
        mEmail=findViewById(R.id.email);
        mPassword=findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)
                {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };
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

    public void goToResetPassword(View view) {
        Intent intent=new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    public void logIn(View view) {

        final String email= mEmail.getText().toString();
        final String password= mPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loginErrorHandler(e);
            }
        });
    }
    private void loginErrorHandler(Exception e)
    {
            switch (e.getMessage())
            {
                case "There is no user record corresponding to this identifier. The user may have been deleted.":
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.notFoundEmail), Toast.LENGTH_SHORT).show();
                    break;
                case "The password is invalid or the user does not have a password.":
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.wrongPassword), Toast.LENGTH_SHORT).show();
                    break;
                case "A network error (such as timeout, interrupted connection or unreachable host) has occurred.":
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.networkError), Toast.LENGTH_SHORT).show();
                    break;
            }
    }
}