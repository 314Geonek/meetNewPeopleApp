package com.golab.meetnewpeopleapp.Cards;


import android.media.Image;

import com.google.firebase.firestore.DocumentSnapshot;

import java.sql.Timestamp;

public class Cards {
    private String id, name, aboutMe, profileImageUrl, city, gender, job, age, distance;
    public Cards(DocumentSnapshot snapshot, String distance) {
        id=snapshot.getId();
        name = snapshot.get("name")!=null ? snapshot.get("name").toString() : "";
        aboutMe = snapshot.get("aboutMe")!=null ? snapshot.get("aboutMe").toString() : "";
        profileImageUrl = snapshot.get("profileImageUrl")!=null ? snapshot.get("profileImageUrl").toString() : "";
        city = snapshot.get("city")!=null ? snapshot.get("city").toString() : "";
        gender = snapshot.get("gender")!=null ? snapshot.get("gender").toString() : "";
        job = snapshot.get("job")!=null ? snapshot.get("job").toString() : "";
        //age = snapshot.get("job")!=null ? Timestamp.valueOf(snapshot.get("job").toString()).toString() : "";
        this.distance = distance;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getCity() {
        return city;
    }

    public String getGender() {
        return gender;
    }

    public String getJob() {
        return job;
    }
}