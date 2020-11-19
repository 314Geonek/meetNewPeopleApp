package com.golab.meetnewpeopleapp.chat;

public class ChatObject {
    private String message;
    private Boolean currentUser;
    private  boolean readed;
    public ChatObject(String message, Boolean currentUser, Boolean readed) {
        this.message = message;
        this.currentUser = currentUser;
        this.readed = readed;
    }
    public String getMessage() {
        return message;
    }
    public Boolean getCurrentUser() {
        return currentUser;
    }

    public boolean isReaded() {
        return readed;
    }
}
