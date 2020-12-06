package com.example.tkitaka_fb.Model;


public class Question {
    private String questionID;
    private String cDate;
    private String title;
    private String content;
    private String category;
    private String answer;
    private String userID;

    public Question() {
    }

    public Question(String questionID, String cDate, String title, String category, String content, String answer, String userID) {
        this.questionID = questionID;
        this.cDate = cDate;
        this.title = title;
        this.category = category;
        this.content = content;
        this.answer = answer;
        this.userID = userID;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getcDate() {
        return cDate;
    }

    public void setcDate(String cDate) {
        this.cDate = cDate;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
