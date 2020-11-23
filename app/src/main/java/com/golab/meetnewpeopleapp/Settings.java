package com.golab.meetnewpeopleapp;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ramotion.fluidslider.FluidSlider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class Settings extends AppCompatActivity {
    private EditText mName, mAboutMe, mJob, mCity;
    private Button mBack, mConfirm;
    private ImageView mProfileImage;
    private FirebaseAuth mAuth;
    private SwitchCompat rangeSwitch;
    private FirebaseFirestore db;
    private String userID, name, aboutMe, profileImageUrl, job, city;
    private RadioGroup mRadioGroupSex;
    private Uri resultUri;
    private FluidSlider slider;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mRadioGroupSex = (RadioGroup) findViewById(R.id.radioGroupSex);
        mName  = (EditText) findViewById(R.id.name);
        mAboutMe = (EditText) findViewById(R.id.aboutMe);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mBack = (Button) findViewById(R.id.back);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mConfirm = (Button) findViewById(R.id.confirm);
        rangeSwitch = (SwitchCompat) findViewById(R.id.switcher);
        db = FirebaseFirestore.getInstance();
        mJob = findViewById(R.id.job);
        mCity = findViewById(R.id.City);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        slider = findViewById(R.id.seekBar);
        slider.setPositionListener(new Function1<Float, Unit>() {
            @Override
            public Unit invoke(Float pos) {
                final String value = String.valueOf((int)(2 + ( pos * 198)));
                slider.setBubbleText(value.concat(" km"));
                return Unit.INSTANCE; }
        }
        );
        mRadioGroupSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton male = findViewById(R.id.male);
                RadioButton female = findViewById(R.id.female);
                RadioButton both = findViewById(R.id.both);

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
                            both.setBackgroundResource(R.drawable.radio_button_mid_selected); }
            }
        });
        getUserInfo();
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71);
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
        rangeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                slider.setEnabled(isChecked);
                slider.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
    }
    private void getUserInfo()
    {
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()&&task.getResult().exists())
                {
                    Map<String, Object> map = (Map<String,Object>)  task.getResult().getData();
                    if(map.get("name")!=null)
                    {
                            name = map.get("name").toString();
                            mName.setText(name);
                    }
                    if(map.get("city")!=null)
                    {
                        city = map.get("city").toString();
                        mCity.setText(city);
                    }
                    if(map.get("job")!=null)
                    {
                        job = map.get("job").toString();
                        mJob.setText(job);
                    }
                    if(map.get("aboutMe")!=null)
                    {
                            aboutMe = map.get("aboutMe").toString();
                            mAboutMe.setText(aboutMe);
                    }
                    if(map.get("lookingFor")!=null)
                    {
                        String lookingFor = map.get("lookingFor").toString();
                        switch (lookingFor)
                        {
                            case "Male":
                                mRadioGroupSex.check(R.id.male);
                                break;
                            case "Female":
                                mRadioGroupSex.check(R.id.female);
                                break;
                            case "Male Female":
                                mRadioGroupSex.check(R.id.both);
                            default: break;
                        }
                    }

                    if(!map.get("profileImageUrl").equals("default"))
                    {

                        profileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(profileImageUrl).apply(RequestOptions.circleCropTransform()).into(mProfileImage);

                    }
                    if(map.get("searchingRange")!=null)
                    if(map.get("searchingRange").toString().equals("false")) {
                        rangeSwitch.setChecked(false);
                    }
                    else
                        {
                            rangeSwitch.setChecked(true);
                            Float value = Float.parseFloat(map.get("searchingRange").toString());
                            value = (value - 2)/198f;
                            slider.setPosition(value);
                            }

                    }


                }



        });
    }
    private void saveUserInformation() {
        name =  mName.getText().toString();
        aboutMe = mAboutMe.getText().toString();
        city = mCity.getText().toString();
        job = mJob.getText().toString();
        Map userInfo = new HashMap();
        userInfo.put("name",name);
        userInfo.put("aboutMe",aboutMe);
        userInfo.put("job",job);
        userInfo.put("city",city);
        if(R.id.female==mRadioGroupSex.getCheckedRadioButtonId())
        {
            userInfo.put("lookingFor", "Female");
        }
            else if(R.id.male==mRadioGroupSex.getCheckedRadioButtonId())
            {
                userInfo.put("lookingFor", "Male");
            }
            else{
                userInfo.put("lookingFor", "Male Female");
                }

            userInfo.put("searchingRange",rangeSwitch.isChecked() ?  slider.getBubbleText().substring(0, slider.getBubbleText().length()-3) : "false");
            db.collection("users").document(mAuth.getCurrentUser().getUid()).update(userInfo);

        if(resultUri!= null)
        {
            final StorageReference storageReference = mStorageRef.child("profileImageUrl/"+userID);
            final UploadTask uploadTask = storageReference.putFile(resultUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map newImage = new HashMap();
                                    newImage.put("profileImageUrl",uri.toString());
                                    db.collection("users").document(userID).update(newImage);
                                }
                            });
                }
            });
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 71 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            resultUri = data.getData();
            Glide.with(getApplication()).load(resultCode).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
        }
    }

    public void goBack(View view) {
        finish();
    }
}