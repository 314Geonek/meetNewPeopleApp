package com.golab.meetnewpeopleapp.matches;

import com.golab.meetnewpeopleapp.chat.ChatObject;

public class MatchesObject {
    private String userId;
    private String name;
    private String profileImageUrl;
    private String matchId;
    private ChatObject lastMessage;
    public MatchesObject (String userId, String name, String profileImageUrl, String matchId, ChatObject lastMessage){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.matchId = matchId;
        this.lastMessage= lastMessage;
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

    public ChatObject getLastMessage() {
        return lastMessage;
    }
}
