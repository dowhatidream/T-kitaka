package com.example.tkitaka_fb.Model;

public class Chat {
    String senderID;
    String receiverID;
    String messege;
    String sTime;
    String file;
    Boolean isSeen;

    public Chat() {
    }

    public Chat(String senderID, String receiverID, String messege, String sTime, String file, Boolean isSeen) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messege = messege;
        this.sTime = sTime;
        this.file = file;
        this.isSeen = isSeen;
    }

    public String getSenderID() {
        return senderID;
    }
    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }
    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessege() {
        return messege;
    }
    public void setMessege(String messege) {
        this.messege = messege;
    }

    public String getsTime() {
        return sTime;
    }
    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }

    public Boolean getIsSeen() {
        return isSeen;
    }
    public void setIsSeen(Boolean isSeen) {
        this.isSeen = isSeen;
    }
}
