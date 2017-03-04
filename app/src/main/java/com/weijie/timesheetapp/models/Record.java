package com.weijie.timesheetapp.models;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by weiji_000 on 3/1/2017.
 */

public class Record implements Serializable{
    private long rid;
    private Date date;
    private Time start_time;
    private Time end_time;
    private int break_time;
    private int work_time;
    private String comments;
    private long tid;
    private int is_weekend;
    private Timestamp created;
    private Timestamp updated;

    public Record(long rid, Date date, Time start_time, Time end_time, int break_time, int work_time, String comments, long tid, int is_weekend, Timestamp created, Timestamp updated) {
        this.rid = rid;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.break_time = break_time;
        this.work_time = work_time;
        this.comments = comments;
        this.tid = tid;
        this.is_weekend = is_weekend;
        this.created = created;
        this.updated = updated;
    }

    public long getRid() {
        return rid;
    }

    public void setRid(long rid) {
        this.rid = rid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getStart_time() {
        return start_time;
    }

    public void setStart_time(Time start_time) {
        this.start_time = start_time;
    }

    public Time getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Time end_time) {
        this.end_time = end_time;
    }

    public int getBreak_time() {
        return break_time;
    }

    public void setBreak_time(int break_time) {
        this.break_time = break_time;
    }

    public int getWork_time() {
        return work_time;
    }

    public void setWork_time(int work_time) {
        this.work_time = work_time;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public int getIs_weekend() {
        return is_weekend;
    }

    public void setIs_weekend(int is_weekend) {
        this.is_weekend = is_weekend;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "Record{" +
                "rid=" + rid +
                ", date=" + date +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", break_time=" + break_time +
                ", work_time=" + work_time +
                ", comments='" + comments + '\'' +
                ", tid=" + tid +
                ", is_weekend=" + is_weekend +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }
}
