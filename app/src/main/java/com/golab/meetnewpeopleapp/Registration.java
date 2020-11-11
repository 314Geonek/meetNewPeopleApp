package com.golab.meetnewpeopleapp;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.TextViewCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.pow;


public class Registration extends AppCompatActivity {

    private Button mRegister;
    private EditText mEmail, mPassword, mName;
    private ImageView mPhoto;
    private RadioGroup mRadioGroupMyGender,mRadioGroupSearchedGender;
    private Uri resultUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorageRef;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef= FirebaseStorage.getInstance();
        mRegister = (Button) findViewById(R.id.register);
        mEmail = (EditText) findViewById(R.id.email);
        mPhoto = findViewById(R.id.myProfilImage);
        mPassword = (EditText) findViewById(R.id.password);
        mName = (EditText) findViewById(R.id.nameMy);
        mRadioGroupMyGender = (RadioGroup) findViewById(R.id.radioGroupMyGender);
        mRadioGroupSearchedGender = (RadioGroup) findViewById(R.id.radioGroupSearchedGender);
        mRadioGroupMyGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton male = findViewById(R.id.male);
                RadioButton female = findViewById(R.id.female);

                if (male.getId()==radioGroup.getCheckedRadioButtonId())
                {
                    male.setBackgroundResource(R.drawable.radio_button_round_left_selected);
                    female.setBackgroundResource(R.drawable.radio_button_round_right);}
                else
                {
                    female.setBackgroundResource(R.drawable.radio_button_round_right_selected);
                    male.setBackgroundResource(R.drawable.radio_button_round_left);
                }

            }
        });
        mRadioGroupSearchedGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton male = findViewById(R.id.searchMale);
                RadioButton female = findViewById(R.id.searchFemale);
                RadioButton both = findViewById(R.id.searchBoth);
                if (male.getId()==radioGroup.getCheckedRadioButtonId())
                {
                    male.setBackgroundResource(R.drawable.radio_button_round_left_selected);
                    female.setBackgroundResource(R.drawable.radio_button_round_right);
                    both.setBackgroundResource(R.drawable.radio_button_mid);
                }
                else if(female.getId()==radioGroup.getCheckedRadioButtonId())
                {
                    female.setBackgroundResource(R.drawable.radio_button_round_right_selected);
                    male.setBackgroundResource(R.drawable.radio_button_round_left);
                    both.setBackgroundResource(R.drawable.radio_button_mid);

                }
                else{
                    female.setBackgroundResource(R.drawable.radio_button_round_right);
                    male.setBackgroundResource(R.drawable.radio_button_round_left);
                    both.setBackgroundResource(R.drawable.radio_button_mid_selected);
                }
            }
        });

        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(Registration.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if(isDataCorrect())
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful())
                                {
                                    saveUserData();
                                }
                                else  {
                                    try
                                    { throw task.getException(); }
                                    catch (FirebaseAuthUserCollisionException existEmail)
                                    {
                                            Toast.makeText(Registration.this, getResources().getString(R.string.emailExists), Toast.LENGTH_SHORT).show();
                                        }
                                        catch (Exception e)
                                        {
                                            Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            }
                        }
                );
            }
        });

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 71 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            resultUri = data.getData();
            Glide.with(getApplication()).asBitmap().load(resultUri).into(mPhoto);
        }
    }
    private void saveUserData()
    {
        String userId = mAuth.getCurrentUser().getUid();
        HashMap userInfo = new HashMap<>();
        userInfo.put("name", mName.getText().toString());
        userInfo.put("gender", getMySex());
        userInfo.put("lookingFor", getSearchedSex());
        userInfo.put("searchingRange", 50);
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).set(userInfo);
        savePhoto(userId);
    }
    private void savePhoto(final String userId)
    {
        final StorageReference storageReference = mStorageRef.getReference().child("profileImageUrl/"+userId);
        final UploadTask uploadTask = storageReference.putFile(resultUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap newImage = new HashMap();
                        newImage.put("profileImageUrl",uri.toString());
                        db.collection("users").document(userId).update(newImage);
                    }
                });
            }
        });
    }
    private boolean isDataCorrect() {
        boolean output=true;
        String error="";
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches())
        {   mEmail.setError("Email not Validate");
            output=false; }
        if(mRadioGroupSearchedGender.getCheckedRadioButtonId()== -1)
        {   error = error.concat( getResources().getString(R.string.uHaveToSelectSearchedGender)).concat("\n");
            output=false; }
        if(mRadioGroupMyGender.getCheckedRadioButtonId()==-1)
        {   error = error.concat( getResources().getString(R.string.uHaveToSelectYourGender).concat("\n"));
            output=false; }
        if(mPassword.getText().length()<6)
        {   mPassword.setError("Password too short <6");
            output=false; }
        if(mName.getText().length()<3)
        {   mName.setError("Name too short <3");
            output=false; }
        if(resultUri==null)
        {   error = error.concat( getResources().getString(R.string.uHaveTOAddPhoto).concat("\n"));
            output=false; }
        if(!error.isEmpty())
            Toast.makeText(Registration.this,error,Toast.LENGTH_SHORT).show();
        return output;
    }

    private String getSearchedSex()
    {
        switch (mRadioGroupSearchedGender.getCheckedRadioButtonId())
        {
            case R.id.searchMale: return "Male";
            case R.id.searchFemale: return "Female";
            case R.id.searchBoth: return "MaleFemale";
            default: return "";
        }
    }

    private String getMySex()
    {
        return mRadioGroupMyGender.getCheckedRadioButtonId()==R.id.male ? "Male" : "Female";
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.signOut();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

}