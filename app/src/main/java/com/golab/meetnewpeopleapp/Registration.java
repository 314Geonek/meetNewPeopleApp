package com.golab.meetnewpeopleapp;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.TextViewCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.pow;


public class Registration extends AppCompatActivity {

    private Button mRegister;
    private EditText mEmail, mPassword, mName;
    private ImageView mPhoto;
    private RadioGroup mRadioGroupMyGender,mRadioGroupSearchedGender;
    private String resultUrl;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        mRegister = (Button) findViewById(R.id.register);
        mEmail = (EditText) findViewById(R.id.email);
        mPhoto = findViewById(R.id.myProfilImage);
        mPassword = (EditText) findViewById(R.id.password);
        mName = (EditText) findViewById(R.id.name);
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
                if (user !=null){
                    Intent intent = new Intent(Registration.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectId = mRadioGroupMyGender.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) findViewById(selectId);
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                if(isDataCorrect())
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if (!task.isSuccessful())
                                {
                                    try
                                    {
                                        throw task.getException();
                                    }
                                    catch (FirebaseAuthWeakPasswordException weakPassword)
                                    {
                                        Toast.makeText(Registration.this, "Weak password", Toast.LENGTH_SHORT).show();
                                    }
                                    catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                    {
                                        Toast.makeText(Registration.this, "Malformed email", Toast.LENGTH_SHORT).show();

                                    }
                                    catch (FirebaseAuthUserCollisionException existEmail)
                                    {
                                        Toast.makeText(Registration.this, "Email exists", Toast.LENGTH_SHORT).show();

                                    }
                                    catch (Exception e)
                                    {
                                        Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    String userId = mAuth.getCurrentUser().getUid();
                                    Map userInfo = new HashMap<>();
                                    userInfo.put("name", name);
                                    userInfo.put("sex", radioButton.getText().toString());
                                    userInfo.put("profileImageUrl", "default");
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("users").document(userId).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>()
                                    {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Registration.this, "Succesful", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                        }
                );
            }
        });
    }

    private boolean isDataCorrect() {
        boolean output=true;
        String error="";
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(mEmail.getText().toString().trim().matches(emailPattern)||mEmail.getText().length()<3)
        {
            mEmail.setError("Email not Validate");
            output=false;
        }
        if(mRadioGroupSearchedGender.getCheckedRadioButtonId()== -1)
        {   error = error.concat( getResources().getString(R.string.uHaveToSelectSearchedGender)).concat("\n");
            output=false;
        }
        if(mRadioGroupMyGender.getCheckedRadioButtonId()==-1)
        {   error = error.concat( getResources().getString(R.string.uHaveToSelectYourGender).concat("\n"));
            output=false;
        }
        if(mPassword.getText().length()<6)
        {
            mPassword.setError("Password too short <6");
            output=false;
        }
        if(mName.getText().length()<3)
        {
            mName.setError("Name too short <3");
            output=false;
        }
        if(resultUrl==null)
        {   error = error.concat( getResources().getString(R.string.uHaveTOAddPhoto).concat("\n"));
            output=false;
        }
        if(!error.isEmpty())
        Toast.makeText(Registration.this,error,Toast.LENGTH_SHORT).show();
        return output;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

}