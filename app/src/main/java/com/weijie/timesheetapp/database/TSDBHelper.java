package com.weijie.timesheetapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by weiji_000 on 2/25/2017.
 */

public class TSDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = TSDBHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "timesheet.db";
    public static final int DATABASE_VERSION = 1;

    public TSDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Build SQL query by appending entries
        String SQL_CREATE_TS_TABLE = "CREATE TABLE IF NOT EXISTS " + TSContract.TSEntry.TABLE_NAME + " ("
                + TSContract.TSEntry.COLUMN_TID + " INTEGER PRIMARY KEY NOT NULL, "
                + TSContract.TSEntry.COLUMN_TNAME + " TEXT NOT NULL, "
                + TSContract.TSEntry.COLUMN_UID + " INTEGER NOT NULL, "
                + TSContract.TSEntry.COLUMN_TCREATED + " TEXT, "
                + TSContract.TSEntry.COLUMN_TUPDATED + " TEXT);";

        String SQL_CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TSContract.UserEntry.TABLE_NAME + " ("
                + TSContract.UserEntry.COLUMN_UID + " INTEGER PRIMARY KEY NOT NULL, "
                + TSContract.UserEntry.COLUMN_UNAME + " TEXT NOT NULL, "
                + TSContract.UserEntry.COLUMN_UEMAIL + " TEXT, "
                + TSContract.UserEntry.COLUMN_UFBID + " INTEGER, "
                + TSContract.UserEntry.COLUMN_UCREATED + " TEXT);";

        String SQL_CREATE_RECORD_TABLE = "CREATE TABLE IF NOT EXISTS " + TSContract.RecordEntry.TABLE_NAME + " ("
                + TSContract.RecordEntry.COLUMN_RID + " INTEGER PRIMARY KEY NOT NULL, "
                + TSContract.RecordEntry.COLUMN_DATE + " TEXT NOT NULL, "
                + TSContract.RecordEntry.COLUMN_START_TIME + " TEXT NOT NULL, "
                + TSContract.RecordEntry.COLUMN_END_TIME + " TEXT NOT NULL, "
                + TSContract.RecordEntry.COLUMN_BREAK + " TEXT NOT NULL, "
                + TSContract.RecordEntry.COLUMN_WORK_TIME + " TEXT NOT NULL, "
                + TSContract.RecordEntry.COLUMN_COMMENTS + " TEXT, "
                + TSContract.RecordEntry.COLUMN_TID + " INTEGER NOT NULL, "
                + TSContract.RecordEntry.COLUMN_IS_WEEKEND + " INTEGER NOT NULL, "
                + TSContract.RecordEntry.COLUMN_RCREATED + " TEXT, "
                + TSContract.RecordEntry.COLUMN_RUPDATED + " TEXT);";


        String SQL_CREATE_SHARE_TABLE = "CREATE TABLE IF NOT EXISTS " + TSContract.ShareEntry.TABLE_NAME + " ("
                + TSContract.ShareEntry.COLUMN_SID + " INTEGER PRIMARY KEY NOT NULL, "
                + TSContract.ShareEntry.COLUMN_TID + " INTEGER NOT NULL, "
                + TSContract.ShareEntry.COLUMN_UID + " INTEGER NOT NULL, "
                + TSContract.ShareEntry.COLUMN_SHARE_MODE + " INTEGER NOT NULL, "
                + TSContract.ShareEntry.COLUMN_SHARE_STATUS + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_TS_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_RECORD_TABLE);
        db.execSQL(SQL_CREATE_SHARE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
