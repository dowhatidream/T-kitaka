package com.example.tkitaka_fb.Model;

public class User {

    private String userID;
    private String userName;
    private String userEmail;
    private String userProfile;
    private String userBirth;
    private String userPhone;
    private String userIntroduction;
    private String userStatus;
    private String userGrade;

    public User() {
    }

    public User(String userID, String userName, String userEmail, String userProfile, String userBirth, String userPhone, String userIntroduction, String userStatus, String userGrade) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userProfile = userProfile;
        this.userBirth = userBirth;
        this.userPhone = userPhone;
        this.userIntroduction = userIntroduction;
        this.userStatus = userStatus;
        this.userGrade = userGrade;
    }

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserProfile() {
        return userProfile;
    }
    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserBirth() {
        return userBirth;
    }
    public void setUserBirth(String userBirth) {
        this.userBirth = userBirth;
    }

    public String getUserPhone() {
        return userPhone;
    }
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserIntroduction() {
        return userIntroduction;
    }
    public void setUserIntroduction(String userIntroduction) {
        this.userIntroduction = userIntroduction;
    }

    public String getUserStatus() {
        return userStatus;
    }
    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserGrade() {
        return userGrade;
    }
    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }
}
