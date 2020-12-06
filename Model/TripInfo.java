package com.example.tkitaka_fb.Model;

public class TripInfo {
    private String tripInfoID;
    private String codeID;
    private String deDate;
    private String reDate;
    private String theme;
    private String age;
    private String cDate;
    private String uDate;
    private Boolean isChecked;
    private String userID;

    public TripInfo(){

    }

    public TripInfo(String codeID, String userID){
        this.codeID = codeID;
        this.userID = userID;
    }

    public TripInfo(String tripInfoID, String codeID, String deDate){
        this.tripInfoID = tripInfoID;
        this.codeID = codeID;
        this.deDate = deDate;
    }

    public TripInfo(String tripInfoID, String codeID, String deDate, String reDate, String theme, String age, String cDate, String uDate, String userID){
        this.tripInfoID = tripInfoID;
        this.codeID = codeID;
        this.deDate = deDate;
        this.reDate = reDate;
        this.theme = theme;
        this.age = age;
        this.cDate = cDate;
        this.uDate = uDate;
        this.userID = userID;
    }

    public TripInfo(String tripInfoID, String codeID, String deDate, String reDate, String theme, String age, String cDate, String uDate, Boolean isChecked, String userID) {
        this.tripInfoID = tripInfoID;
        this.codeID = codeID;
        this.deDate = deDate;
        this.reDate = reDate;
        this.theme = theme;
        this.age = age;
        this.cDate = cDate;
        this.uDate = uDate;
        this.isChecked = isChecked;
        this.userID = userID;
    }

    public String getTripInfoID() {
        return tripInfoID;
    }
    public void setTripInfoID(String tripInfoID) {
        this.tripInfoID = tripInfoID;
    }

    public String getCodeID() {
        return codeID;
    }
    public void setCodeID(String codeID) {
        this.codeID = codeID;
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

    public String getTheme() {
        return theme;
    }
    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getAge() {
        return age;
    }
    public void setAge(String age) {
        this.age = age;
    }

    public String getcDate() {
        return cDate;
    }
    public void setcDate(String cDate) {
        this.cDate = cDate;
    }

    public String getuDate() {
        return uDate;
    }
    public void setuDate(String uDate) {
        this.uDate = uDate;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }
    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
}
