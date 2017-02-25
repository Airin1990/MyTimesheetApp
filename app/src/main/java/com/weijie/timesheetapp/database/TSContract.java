package com.weijie.timesheetapp.database;

import android.net.Uri;

import static android.R.attr.mode;

/**
 * Created by weiji_000 on 2/23/2017.
 */

public final class TSContract {

    // Base Uri path for all content provider access
    public static final String CONTENT_AUTHORITY = "com.weijie.timesheetapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //sub path for specific content provider
    public static final String PATH_SHEET = "timesheets";
    public static final String PATH_RECORD = "records";
    public static final String PATH_USER = "users";
    public static final String PATH_SHARE = "shares";

    // Default constructor
    private TSContract() {}

    // Content provider for general timesheet
    public static final class TSEntry {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SHEET);
        // TABLE CONTSANTS
        public static final String TABLE_NAME = "timesheets";
        public static final String COLUMN_TID = "TID";
        public static final String COLUMN_TNAME = "TName";
        public static final String COLUMN_UID = "UID";
        public static final String COLUMN_TCREATED = "TCreated";
        public static final String COLUMN_TUPDATED = "TUpdated";
    }

    //Content provider for user info
    public static final class UserEntry {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USER);
        //TABLE CONSTANTS
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_UID = "UID";
        public static final String COLUMN_UNAME = "UName";
        public static final String COLUMN_UEMAIL = "UEmail";
        public static final String COLUMN_UFBID = "UFacebookID";
        public static final String COLUMN_UCREATED = "UCreated";
    }

    //Content provider for timesheet daily record table
    public static final class RecordEntry {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RECORD);
        //TABLE CONSTANTS
        public static final String TABLE_NAME = "records";
        public static final String COLUMN_RID = "RID";
        public static final String COLUMN_DATE = "Date";
        public static final String COLUMN_START_TIME = "StartTime";
        public static final String COLUMN_END_TIME = "EndTime";
        public static final String COLUMN_BREAK = "Break";
        public static final String COLUMN_WORK_TIME = "NetWorkTime";
        public static final String COLUMN_COMMENTS = "Comments";
        public static final String COLUMN_TID = "TID";
        public static final String COLUMN_IS_WEEKEND = "IsWeekend";
        public static final String COLUMN_RCREATED = "Created";
        public static final String COLUMN_RUPDATED = "Updated";

        // Values for IsWeekend
        public static final int DAY_WEEKDAY = 0;
        public static final int DAY_WEEKEND = 1;

        // check valid IsWeekend input
        public static boolean isValidDay(int day) {
            if (day == DAY_WEEKDAY || day == DAY_WEEKEND)
                return true;
            return false;
        }

    }

    //Content Provider has all the share relations between user and table
    // (not include ownership relation)
    public static final class ShareEntry {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SHARE);
        //TABLE CONSTANTS
        public static final String TABLE_NAME = "shares";
        public static final String COLUMN_SID = "SID";
        public static final String COLUMN_TID = "TID";
        public static final String COLUMN_UID = "UID";
        public static final String COLUMN_SHARE_MODE = "ShareMode";
        public static final String COLUMN_SHARE_STATUS = "ShareStatus";

        // Value for share mode and status
        public static final int MODE_VIEWONLY = 0;
        public static final int MODE_EDIT = 1;

        public static final int STATUS_PENDING = 0;
        public static final int STATUS_ACCEPTED = 1;
        public static final int STATUS_REVOKED = 2;
        public static final int STATUS_REVOKED_PENDING = 3;

        //check valid input
        public static Boolean isValidMode(int mode) {
            if (mode == MODE_EDIT || mode == MODE_VIEWONLY) {
                return true;
            }
            return false;
        }

        public static Boolean isValidStatus(int status) {
            if (mode == STATUS_ACCEPTED || mode == STATUS_PENDING || mode == STATUS_REVOKED
                    || mode == STATUS_REVOKED_PENDING) {
                return true;
            }
            return false;
        }

    }
}
