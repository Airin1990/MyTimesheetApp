package com.weijie.timesheetapp.models;

/**
 * Created by weiji_000 on 2/18/2017.
 */

public class Timesheet {
    private long TID;
    private String name;
    private long authorID;
    private int shareMode;
    private int shareStatus;

    public Timesheet(long TID, String name, long authorID, int shareMode, int shareStatus) {
        this.TID = TID;
        this.name = name;
        this.authorID = authorID;
        this.shareMode = shareMode;
        this.shareStatus = shareStatus;
    }

    public long getTID() {
        return TID;
    }

    public void setTID(long TID) {
        this.TID = TID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAuthorID() {
        return authorID;
    }

    public void setAuthorID(long authorID) {
        this.authorID = authorID;
    }

    public int getShareStatus() {
        return shareStatus;
    }

    public void setShareStatus(int shareStatus) {
        this.shareStatus = shareStatus;
    }

    public int getShareMode() {
        return shareMode;
    }

    public void setShareMode(int shareMode) {
        this.shareMode = shareMode;
    }
}
