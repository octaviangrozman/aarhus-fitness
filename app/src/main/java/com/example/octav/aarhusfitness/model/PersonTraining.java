package com.example.octav.aarhusfitness.model;

import java.io.Serializable;

/**
 * Created by Octav on 1/7/2018.
 */

public class PersonTraining implements Serializable {
    private String email;
    private String displayName;
    private String photoUrl;
    private String phoneNumber;
    private String time;

    public PersonTraining() {

    }

    public PersonTraining(String email, String displayName, String photoUrl, String phoneNumber) {
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "PersonTraining{" +
                "email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", photoUrl=" + photoUrl +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
