package com.example.tkitaka_fb.Model;
import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class Comment {

    public String commentID;
    public String postID;
    public String userID;
    public String userName;
    public String date;
    public String content;
    public String commentImage;
    public String userProfilePic;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String userID, String commentID, String postID, String userName, String date, String content, String commentImage, String userProfilePic) {
        this.userID = userID;
        this.commentID = commentID;
        this.postID = postID;
        this.userName = userName;
        this.date = date;
        this.content = content;
        this.commentImage = commentImage;
        this.userProfilePic = userProfilePic;
    }

    public String getPostID() {
        return postID;
    }
    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getCommentID() {
        return commentID;
    }
    public void setCommentID(String commentID) {
        this.commentID = commentID;
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

    public String getUserProfilePic() {
        return userProfilePic;
    }
    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getCommentImage() {
        return commentImage;
    }
    public void setCommentImage(String commentImage) {
        this.commentImage = commentImage;
    }


}
