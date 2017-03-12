package com.weijie.timesheetapp.models;

/**
 * Created by weiji_000 on 2/18/2017.
 */

public class User {
    private long uid;
    private String firstName;
    private String lastName;
    private String email;
    private int shareMode;
    private int shareStatus;


    public User(long uid, String firstName, String lastName, String email, int shareMode, int shareStatus) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.shareMode = shareMode;
        this.shareStatus = shareStatus;
    }



    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return firstName+" "+lastName;
    }

    public int getShareMode() {
        return shareMode;
    }

    public void setShareMode(int shareMode) {
        this.shareMode = shareMode;
    }

    public int getShareStatus() {
        return shareStatus;
    }

    public void setShareStatus(int shareStatus) {
        this.shareStatus = shareStatus;
    }
}
