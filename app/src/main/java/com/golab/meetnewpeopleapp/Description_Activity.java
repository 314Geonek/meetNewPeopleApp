package com.golab.meetnewpeopleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.golab.meetnewpeopleapp.Cards.Cards;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Description_Activity extends AppCompatActivity {
    private String jsonMyObject;
    private Cards profile;
    private AppCompatImageView profileImage;
    private TextView name, about, city, job;
    private RadioGroup gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_);
        name = findViewById(R.id.name);
        about = findViewById(R.id.about);
        city = findViewById(R.id.city);
        job = findViewById(R.id.job);
        gender = findViewById(R.id.radioGroupMyGender);
        profileImage = findViewById(R.id.picture);
        String jsonMyObject = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("myObject");
            profile = new Gson().fromJson(jsonMyObject, Cards.class);
            findViewById(R.id.reportBtn).setVisibility(!profile.getId().
                    equals(FirebaseAuth.getInstance().getCurrentUser().getUid())? View.VISIBLE : View.GONE);
            if(extras.getString("admin")!=null)
                findViewById(R.id.reportBtn).setVisibility(View.GONE);
            fillData();
        }
    }

    private void fillData() {
        name.setText(profile.getName());
        about.setText(profile.getAboutMe());
        city.setText(profile.getCity());
        job.setText(profile.getJob());
        if (profile.getGender().equals("male")) {
            findViewById(R.id.male).setBackgroundResource(R.drawable.radio_button_round_left_selected);
        } else
        {
            findViewById(R.id.female).setBackgroundResource(R.drawable.radio_button_round_right_selected);
        }
        profileImage.setBackground(null);
        Glide.with(getApplication()).load(profile.getProfileImageUrl())
                .apply(RequestOptions.circleCropTransform()).into(profileImage);
    }

    public void goBack(View view) {
        finish();
    }
    public void repMessages(View View)
    {
        sendReportToDatabase("Messages");
        hideLayoutReport();
    }

    public void repDescription(View View)
    {
        sendReportToDatabase("Description");
        hideLayoutReport();
    }
    public void repPhoto(View view)
    {
        sendReportToDatabase("Photo");
        hideLayoutReport();
    }
    private void sendReportToDatabase(String description) {
        Map report = new HashMap<>();
        report.put("ReportedBy", FirebaseAuth.getInstance().getCurrentUser().getUid());
        report.put("Time", Timestamp.now());
        report.put("Reason", description);
        FirebaseFirestore.getInstance().collection("users")
                .document(profile.getId()).collection("Reports").document().set(report);
    }


    public void doNothing(View view){}

    public void hideReports(View view)
    {
        hideLayoutReport();
    }
    public void sendReport(View view) { findViewById(R.id.groundsList).setVisibility(View.VISIBLE); }
    private void hideLayoutReport() { findViewById(R.id.groundsList).setVisibility(View.GONE);}
}