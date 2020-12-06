package com.example.tkitaka_fb.Model;


public class Planbook {
    private String planbookID;
    public String date;
    private String title;
    private String place;
    private String deDate;
    private String reDate;
    private String diffDay;
    private String userID;

    public Planbook() {
    }

    public Planbook(String planbookID, String date, String title, String place, String deDate, String reDate, String userID, String diffDay) {
        this.planbookID=planbookID;
        this.date = date;
        this.title = title;
        this.place = place;
        this.deDate = deDate;
        this.reDate = reDate;
        this.userID = userID;
        this.diffDay = diffDay;
    }

    public String getPlanbookID() {
        return planbookID;
    }
    public void setPlanbookID(String planbookID) {
        this.planbookID = planbookID;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }

    public String getDeDate() {
        return deDate;
    }
    public void setDeDate(String deDate) {
        this.deDate = deDate;
    }

    public String getReDate() {
        return reDate;
    }
    public void setReDate(String reDate) {
        this.reDate = reDate;
    }

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDiffDay() {
        return diffDay;
    }
    public void setDiffDay(String diffDay) {
        this.diffDay = diffDay;
    }

}
