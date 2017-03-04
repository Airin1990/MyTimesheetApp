package com.weijie.timesheetapp.network;

import com.weijie.timesheetapp.MyApplication;

import okhttp3.Response;

/**
 * Created by weiji_000 on 2/28/2017.
 */

public class Controller {

    private static final String BASE_URL = "http://LowCost-env.mytkurtxsj.us-east-1.elasticbeanstalk.com/webapi";
    private static final String RECORD_URL = "/records";
    private static final String SHEET_URL = "/sheets";
    private static final String USER_URL = "/users";
    private static final String SHARE_URL = "/shares";
    private static final String SUMMARY_URL = "/summary";

    public class Action {
        public static final int DISPLAY_RECORD_LIST = 1001;

        public static final int INSERT_RECORD = 2001;

        public static final int  UPDATE_RECORD = 3001;

        public static final int DELETE_RECORD = 4001;
    }

    public static Response AppEvent (int action) {

        Response resp = null;
        switch (action) {
            case Action.DISPLAY_RECORD_LIST:
                resp = MyApplication.getInstance().getHttpGateway().doGet(BASE_URL + RECORD_URL);
                break;

            case Action.INSERT_RECORD:


            case Action.UPDATE_RECORD:


            case Action.DELETE_RECORD:

        }
        return resp;
    }

}
