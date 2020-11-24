package com.golab.meetnewpeopleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.golab.meetnewpeopleapp.admin.AdminMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private Button mLogin;
    private EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(getIntent().getExtras()!=null )
        Toast.makeText(LoginActivity.this, getIntent().getExtras().getString("email").concat(getResources().getString(R.string.notVerifiedEmail)), Toast.LENGTH_LONG).show();

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    if (!user.isEmailVerified()) {
                        user.sendEmailVerification();
                        Toast.makeText(LoginActivity.this, user.getEmail() +"  "+ getResources().getString(R.string.notVerifiedEmail), Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                    else {
                        logIntoApplication(user);
                    }
                }
            }
        };
        setContentView(R.layout.activity_login);
        mLogin= findViewById(R.id.login);
        mEmail=findViewById(R.id.email);
        mPassword=findViewById(R.id.password);
    }
    private void logIntoApplication(FirebaseUser user)
    {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Intent intent;
                if (documentSnapshot.exists()) {
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                }else {
                    intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();
//        if(null!=mAuth.getCurrentUser())
//            mAuth.signOut();
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
    {       if(e.getMessage()!=null)
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

    public void goToRegistration(View view) {
        Intent intent=new Intent(LoginActivity.this, Registration.class);
        startActivity(intent);
    }
}