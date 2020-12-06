package com.example.tkitaka_fb.Model;

public class Faq {

    public String faqID;
    public String title;
    public String content;
    public String category;
    public String cDate;
    public String userID;

    public Faq(){

    }

    public Faq(String faqID, String title, String contnet, String category, String cDate, String userID){
        this.faqID = faqID;
        this.title = title;
        this.content = contnet;
        this.category = category;
        this.cDate = cDate;
        this.userID = userID;
    }

    public String getFaqID() {
        return faqID;
    }

    public void setFaqID(String faqID) {
        this.faqID = faqID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getcDate() {
        return cDate;
    }

    public void setcDate(String cDate) {
        this.cDate = cDate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
