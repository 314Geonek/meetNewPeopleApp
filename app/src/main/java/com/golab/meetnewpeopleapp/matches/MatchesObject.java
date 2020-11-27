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
        this.name = this.name.length()>=10 ? this.name.substring(0,7).concat("...") : this.name;
    }

    public String getMatchId() {
        return matchId;
    }
    public String getUserId(){
        return userId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public ChatObject getLastMessage() {
        return lastMessage;
    }
}
