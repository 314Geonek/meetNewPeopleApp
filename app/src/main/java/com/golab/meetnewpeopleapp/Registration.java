package com.golab.meetnewpeopleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.lang.Math.pow;

public class Registration extends AppCompatActivity {
    private Button mRegister;
    private EditText mEmail, mPassword, mName;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    Intent intent=new Intent(Registration.this, MainActivity.class);
                    startActivity(intent);
                    finish();;
                    return;
                }

            }
        };

        mName=findViewById(R.id.name);
        mRegister= findViewById(R.id.register);
        mEmail=findViewById(R.id.email);
        mRadioGroup=findViewById(R.id.radioGroup);
        mPassword=findViewById(R.id.password);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedSex =  mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton =(RadioButton) findViewById(selectedSex);
                final String name = mName.getText().toString();
                if(radioButton.getText() == null)
                {
                    Toast.makeText(Registration.this,"no sex selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String email= mEmail.getText().toString();
                final String password= mPassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(Registration.this,"@string/registration_error", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String userId= mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb= FirebaseDatabase.getInstance().getReference().child("Users").child(radioButton.getText().toString()).child(userId).child("name");
                            currentUserDb.setValue(name);
                        }
                    }
                });
            }
        });
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