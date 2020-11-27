package com.golab.meetnewpeopleapp.Cards;


import android.media.Image;

import com.google.firebase.firestore.DocumentSnapshot;

import java.sql.Timestamp;

public class Cards {
    private String id, name, aboutMe, profileImageUrl, city, gender, job, age, distance;
    private int reportedForChat, reportedForPhoto, reportedForDesc;
    public Cards(DocumentSnapshot snapshot, String distance) {
        id=snapshot.getId();
        name = snapshot.get("name")!=null ? snapshot.get("name").toString() : "";
        aboutMe = snapshot.get("aboutMe")!=null ? snapshot.get("aboutMe").toString() : "";
        profileImageUrl = snapshot.get("profileImageUrl")!=null ? snapshot.get("profileImageUrl").toString() : "";
        city = snapshot.get("city")!=null ? snapshot.get("city").toString() : "";
        gender = snapshot.get("gender")!=null ? snapshot.get("gender").toString() : "";
        job = snapshot.get("job")!=null ? snapshot.get("job").toString() : "";
        name = name.length()>=10 ? name.substring(0,7).concat("...") : name;
        this.distance = distance;
    }

    public Cards(DocumentSnapshot snapshot, String distance, int reportedForChat, int reportedForDesc, int reportedForPhoto) {
        id=snapshot.getId();
        name = snapshot.get("name")!=null ? snapshot.get("name").toString() : "";
        aboutMe = snapshot.get("aboutMe")!=null ? snapshot.get("aboutMe").toString() : "";
        profileImageUrl = snapshot.get("profileImageUrl")!=null ? snapshot.get("profileImageUrl").toString() : "";
        city = snapshot.get("city")!=null ? snapshot.get("city").toString() : "";
        gender = snapshot.get("gender")!=null ? snapshot.get("gender").toString() : "";
        job = snapshot.get("job")!=null ? snapshot.get("job").toString() : "";
        name = name.length()>=10 ? name.substring(0,7).concat("..."): name ;
        this.distance = distance;
        this.reportedForChat=reportedForChat;
        this.reportedForDesc = reportedForDesc;
        this.reportedForPhoto = reportedForPhoto;
    }

    public int getReportedForChat() {
        return reportedForChat;
    }

    public int getReportedForPhoto() {
        return reportedForPhoto;
    }

    public int getReportedForDesc() {
        return reportedForDesc;
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