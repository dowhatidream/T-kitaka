package com.example.tkitaka_fb.Model;

public class AdminPost {

    public String adPostID;
    public String userID;
    public String userName;
    public String title;
    public String category;
    public String content;
    public String date;
    public String postImage;

    public AdminPost() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public AdminPost(String userID, String userName, String adPostID,String title, String category, String content, String date, String postImage) {
        this.adPostID = adPostID;
        this.userID = userID;
        this.userName = userName;
        this.title = title;
        this.category = category;
        this.content = content;
        this.date = date;
        this.postImage = postImage;
    }

    public String getAdPostID() {
        return adPostID;
    }
    public void setAdPostID(String adPostID) {
        this.adPostID = adPostID;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String cDate) {
        this.date = cDate;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostImage() {
        return postImage;
    }
    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

}
