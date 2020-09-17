package com.golab.meetnewpeopleapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {
    private EditText mName, mPhone, mAboutMe;
    private Button mBack, mConfirm;
    private ImageView mProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private String userID, name, phone, aboutMe, profileImageUrl;
    private RadioGroup mRadioGroupSex;
    private Uri resultUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mRadioGroupSex = (RadioGroup) findViewById(R.id.radioGroupSex);
        mName  = (EditText) findViewById(R.id.name);
        mPhone = (EditText) findViewById(R.id.phone);
        mAboutMe = (EditText) findViewById(R.id.aboutMe);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        getUserInfo();
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });
    }
    private void getUserInfo()
    {
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0)
                {
                    Map<String, Object> map = (Map<String,Object>) snapshot.getValue();
                    if(map.get("name")!=null)
                    {
                            name = map.get("name").toString();
                            mName.setText(name);
                    }
                    if(map.get("phone")!=null)
                    {
                            phone = map.get("phone").toString();
                            mPhone.setText(phone);
                    }
                    if(map.get("aboutMe")!=null)
                    {
                            aboutMe = map.get("aboutMe").toString();
                            mAboutMe.setText(aboutMe);
                    }
                    if(map.get("wantedSex")!=null)
                    {
                        String wantedSex = map.get("wantedSex").toString();
                        switch (wantedSex)
                        {
                            case "Male":
                                mRadioGroupSex.check(R.id.male);
                                break;
                            case "Female":
                                mRadioGroupSex.check(R.id.female);
                                break;
                            default: break;
                        }
                    }
                    if(map.get("profileImageUrl")!=null)
                    {
                            profileImageUrl = map.get("profileImageUrl").toString();
                            Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void saveUserInformation() {
        name =  mName.getText().toString();
        phone = mPhone.getText().toString();
        aboutMe = mAboutMe.getText().toString();
        Map userInfo = new HashMap();
        userInfo.put("name",name);
        userInfo.put("phone",phone);
        userInfo.put("aboutMe",aboutMe);
        userInfo.put("wantedSex", mRadioGroupSex.getCheckedRadioButtonId()==R.id.male ? "Male" : "Female");
        mCustomerDatabase.updateChildren(userInfo);
        if(resultUri!= null)
        {   ///not sure final
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mCustomerDatabase.updateChildren(newImage);
                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }
        else {finish();}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK)
        {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}