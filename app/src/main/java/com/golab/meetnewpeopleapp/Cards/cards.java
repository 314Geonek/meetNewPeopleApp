package com.golab.meetnewpeopleapp.Cards;


import android.media.Image;

public class cards {
    private String userId;
    private String name;
    private String profileImageUrl;
    private int distance;
    public cards (String userId, String name, String profileImageUrl, int distance){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.distance = distance;
    }

    public cards() {

    }

    public String getUserId(){
        return userId;
    }
    public void setUserID(String userID){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getDistance() {
        return distance;
    }
}