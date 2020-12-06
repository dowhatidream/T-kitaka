package com.example.tkitaka_fb.Model;

public class ChatList {
    private String userID;

    public ChatList() {
    }

    public ChatList(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
}
