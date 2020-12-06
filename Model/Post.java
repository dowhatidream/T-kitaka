package com.example.tkitaka_fb.Model;

public class Post {

    public String userID;
    public String postID;
    public String userName;
    public String userProfilePic;
    public String title;
    public String category;
    public String content;
    public String date;
    public String postImage;
    private int postCount;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String userID, String postID, String userName, String userProfilePic, String title, String category, String content, String date, String postImage, int postCount) {
        this.userID = userID;
        this.postID = postID;
        this.userName = userName;
        this.title = title;
        this.category = category;
        this.content = content;
        this.date = date;
        this.postImage = postImage;
        this.userProfilePic = userProfilePic;
        this.postCount = postCount;
    }

    public String getPostID() {
        return postID;
    }
    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String cDate) {
        this.date = cDate;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }
    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getPostImage() {
        return postImage;
    }
    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public int getPostCount() {
        return postCount;
    }
    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }
}

