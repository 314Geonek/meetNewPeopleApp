package com.golab.meetnewpeopleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.golab.meetnewpeopleapp.Cards.Cards;
import com.google.gson.Gson;

public class Description_Activity extends AppCompatActivity {
    private String jsonMyObject;
    private  Cards profile;
    private AppCompatImageView profileImage;
    private TextView name, about, city, job;
    private RadioGroup gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_);
        String jsonMyObject = null;
        Bundle extras = getIntent().getExtras();
        name = findViewById(R.id.name);
        about = findViewById(R.id.about);
        city = findViewById(R.id.city);
        job = findViewById(R.id.job);
        gender = findViewById(R.id.radioGroupMyGender);
        profileImage = findViewById(R.id.picture);

        if (extras != null) {
            jsonMyObject = extras.getString("myObject");
            profile = new Gson().fromJson(jsonMyObject, Cards.class);
            fillData();
        }

    }
    private void fillData() {
        System.out.println(profile.getName());
        name.setText(profile.getName());
        about.setText(profile.getAboutMe());
        city.setText(profile.getCity());
        job.setText(profile.getJob());
        int genderid = profile.getGender().equals("Male") ? R.id.male : R.id.female;
        if (profile.getGender().equals("Male")) {
            findViewById(R.id.male).setBackgroundResource(R.drawable.radio_button_round_left_selected);
        } else
        {
            findViewById(R.id.female).setBackgroundResource(R.drawable.radio_button_round_right_selected);
        }
        profileImage.setBackground(null);
        Glide.with(getApplication()).load(profile.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(profileImage);



    }

    public void goBack(View view) {
        finish();
    }
}