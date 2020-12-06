package com.example.tkitaka_fb.Model;

public class Answer {
    private String answerID;
    private String content;
    String cDate;
    private String questionID;
    private String userID;

    public Answer(){

    }

    public Answer(String answerID, String content, String cDate, String questionID, String userID){
        this.answerID = answerID;
        this.content = content;
        this.cDate = cDate;
        this.questionID = questionID;
        this.userID = userID;
    }

    public String getAnswerID() {
        return answerID;
    }

    public void setAnswerID(String answerID) {
        this.answerID = answerID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getcDate() {
        return cDate;
    }

    public void setcDate(String cDate) {
        this.cDate = cDate;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
