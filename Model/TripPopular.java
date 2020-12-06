package com.example.tkitaka_fb.Model;

public class TripPopular {
    private int tripPopular;
    private String country;

    public TripPopular() {
    }

    public TripPopular(int tripPopular, String country) {
        this.tripPopular = tripPopular;
        this.country = country;
    }

    public int getTripPopular() {
        return tripPopular;
    }
    public void setTripPopular(int tripPopular) {
        this.tripPopular = tripPopular;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
