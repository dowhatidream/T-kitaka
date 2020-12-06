package com.example.tkitaka_fb.Model;

public class Planning {
    private String planningID;
    private String title;
    private String place;
    private String time;
    private String planbookID;
    private String userID;
    private String cDate;
    private String date;

    public Planning() {
    }

    public Planning(String planningID, String title, String place, String date, String cDate, String time, String planbookID, String userID) {
        this.planningID = planningID;
        this.title = title;
        this.place = place;
        this.date = date;
        this.time = time;
        this.planbookID = planbookID;
        this.userID = userID;
        this.cDate = cDate;
    }

    public String getPlanningID() {
        return planningID;
    }
    public void setPlanningID(String planningID) {
        this.planningID = planningID;
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

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getPlanbookID() {
        return planbookID;
    }
    public void setPlanbookID(String planbookID) {
        this.planbookID = planbookID;
    }

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getCDate() {
        return cDate;
    }
    public void setCDate(String cDate) {
        this.cDate = cDate;
    }


}