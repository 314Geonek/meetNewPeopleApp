package com.golab.meetnewpeopleapp.matches;

public class MatchesObject {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String matchId;
    public MatchesObject (String userId, String name, String profileImageUrl, String matchId){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.matchId = matchId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
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
}
