package com.weijie.timesheetapp.models;

/**
 * Created by weiji_000 on 2/18/2017.
 */

public class Timesheet {
    private long TID;
    private String name;
    private String authorID;

    public Timesheet(long TID, String name, String authorID) {
        this.TID = TID;
        this.name = name;
        this.authorID = authorID;
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

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }
}
