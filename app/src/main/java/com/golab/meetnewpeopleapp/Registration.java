package com.golab.meetnewpeopleapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class Registration extends AppCompatActivity {

    private Button mRegister;
    private EditText mEmail, mPassword, mName;
    private ImageView mPhoto;
    private RadioGroup mRadioGroupMyGender,mRadioGroupSearchedGender;
    private Uri resultUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage mStorageRef;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef= FirebaseStorage.getInstance();
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mName = (EditText) findViewById(R.id.nameMy);
        mRadioGroupMyGender = (RadioGroup) findViewById(R.id.radioGroupMyGender);
        mRadioGroupSearchedGender = (RadioGroup) findViewById(R.id.radioGroupSearchedGender);

        mPhoto = findViewById(R.id.myProfilImage);
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71);
            }
        });

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

        mRegister = (Button) findViewById(R.id.register);
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
                                    mAuth.getCurrentUser().sendEmailVerification();
                                    saveUserData();
                                }
                                else  {
                                    try
                                    { throw task.getException(); }
                                    catch (FirebaseAuthUserCollisionException existEmail)
                                    { Toast.makeText(Registration.this,
                                            getResources().getString(R.string.emailExists), Toast.LENGTH_SHORT).show();}
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 71 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            resultUri = data.getData();
            Glide.with(getApplication()).asBitmap().apply(RequestOptions.circleCropTransform())
                    .load(resultUri).into(mPhoto);
        }
    }
    private void saveUserData()
    {
        String userId = mAuth.getCurrentUser().getUid();
        Map userInfo = new HashMap<>();
        userInfo.put("name", mName.getText().toString());
        userInfo.put("gender", getMyGender());
        userInfo.put("lookingFor", getSearchedGender());
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
                        goToLogin();
                    }
                });
            }
        });
    }

    private void goToLogin() {
        mAuth.signOut();
        finish();
    }

    private boolean isDataCorrect() {
        boolean output=true;
        String error="";
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches())
        {   mEmail.setError(getResources().getString(R.string.emailvalidateerr));
            output=false; }
        if(mRadioGroupSearchedGender.getCheckedRadioButtonId()== -1)
        {   error = error.concat( getResources().getString(R.string.uHaveToSelectSearchedGender)).concat("\n");
            output=false; }
        if(mRadioGroupMyGender.getCheckedRadioButtonId()==-1)
        {   error = error.concat( getResources().getString(R.string.uHaveToSelectYourGender).concat("\n"));
            output=false; }
        if(mPassword.getText().length()<6)
        {   mPassword.setError(getResources().getString(R.string.passwordtiishort));
            output=false; }
        if(mName.getText().length()<3)
        {   mName.setError(getResources().getString(R.string.nameLenght));
            output=false; }
        if(resultUri==null)
        {   error = error.concat( getResources().getString(R.string.uHaveTOAddPhoto).concat("\n"));
            output=false; }
        if(!error.isEmpty())
            Toast.makeText(Registration.this,error,Toast.LENGTH_SHORT).show();
        return output;
    }

    private String getSearchedGender()
    {
        switch (mRadioGroupSearchedGender.getCheckedRadioButtonId())
        {
            case R.id.searchMale: return "Male";
            case R.id.searchFemale: return "Female";
            case R.id.searchBoth: return "Male Female";
            default: return "";
        }
    }

    private String getMyGender()
    {
        return mRadioGroupMyGender.getCheckedRadioButtonId()==R.id.male ? "male" : "female";
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}