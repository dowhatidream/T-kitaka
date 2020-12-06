package com.example.tkitaka_fb.Model; // 이승연 최종수정 1003

public class PBMS {
    private String PBMSID;
    private String accommondation;
    private String meal;
    private String sTransportation;
    private String lTransportation;
    private String expense;
    private String preplan;
    private String spending;
    private String flight;
    private String guide;
    private String smoking;
    private String tmi;
    private String userID;

    public PBMS(){

    }

    public PBMS(String accommondation, String meal, String sTransportation, String lTransportation, String expense, String preplan, String spending, String flight, String guide, String smoking, String userID) {
        this.accommondation = accommondation;
        this.meal = meal;
        this.sTransportation = sTransportation;
        this.lTransportation = lTransportation;
        this.expense = expense;
        this.preplan = preplan;
        this.spending = spending;
        this.flight = flight;
        this.guide = guide;
        this.smoking = smoking;
        this.userID = userID;
    }

    public PBMS(String PBMSID, String accommondation, String meal, String sTransportation, String lTransportation, String expense, String preplan, String spending, String flight, String guide, String smoking, String tmi, String userID) {
        this.PBMSID = PBMSID;
        this.accommondation = accommondation;
        this.meal = meal;
        this.sTransportation = sTransportation;
        this.lTransportation = lTransportation;
        this.expense = expense;
        this.preplan = preplan;
        this.spending = spending;
        this.flight = flight;
        this.guide = guide;
        this.smoking = smoking;
        this.tmi = tmi;
        this.userID = userID;
    }

    public String getPBMSID() {
        return PBMSID;
    }
    public void setPBMSID(String PBMSID) {
        this.PBMSID = PBMSID;
    }

    public String getAccommondation() {
        return accommondation;
    }
    public void setAccommondation(String accommondation) {
        this.accommondation = accommondation;
    }

    public String getMeal() {
        return meal;
    }
    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getsTransportation() {
        return sTransportation;
    }
    public void setsTransportation(String sTransportation) {
        this.sTransportation = sTransportation;
    }

    public String getlTransportation() {
        return lTransportation;
    }
    public void setlTransportation(String lTransportation) {
        this.lTransportation = lTransportation;
    }

    public String getExpense() {
        return expense;
    }
    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getPreplan() {
        return preplan;
    }
    public void setPreplan(String preplan) {
        this.preplan = preplan;
    }

    public String getSpending() {
        return spending;
    }
    public void setSpending(String spending) {
        this.spending = spending;
    }

    public String getFlight() {
        return flight;
    }
    public void setFlight(String flight) {
        this.flight = flight;
    }

    public String getGuide() {
        return guide;
    }
    public void setGuide(String guide) {
        this.guide = guide;
    }

    public String getSmoking() {
        return smoking;
    }
    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public String getTmi() {
        return tmi;
    }
    public void setTmi(String tmi) {
        this.tmi = tmi;
    }

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
}
