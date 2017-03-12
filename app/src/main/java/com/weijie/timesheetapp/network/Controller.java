package com.weijie.timesheetapp.network;

import com.weijie.timesheetapp.MyApplication;

import org.json.JSONObject;

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
        public static final int DISPLAY_TS_LIST = 1002;
        public static final int GET_SHARE_TS_LIST = 1003;
        public static final int GET_SUMMARY = 1004;
        public static final int GET_USER_PROFILE = 1005;
        public static final int GET_USER_SHARE_STATUS = 1006;
        public static final int GET_SHARE_USER_LIST = 1007;

        public static final int INSERT_RECORD = 2001;
        public static final int INSERT_NEW_TS = 2002;
        public static final int ADD_NEW_USER = 2003;
        public static final int ADD_SHARE = 2004;

        public static final int UPDATE_RECORD = 3001;
        public static final int UPDATE_CURRENT_TS = 3002;
        public static final int UPDATE_USER = 3003;
        public static final int UPDATE_SHARE = 3004;

        public static final int DELETE_RECORD = 4001;
        public static final int DELETE_TIMESHEET = 4002;
        public static final int DELETE_USER = 4003;
        public static final int DELETE_SHARE = 4004;
    }

    public static Response AppEvent (int action, String param, JSONObject json) {

        Response resp = null;
        switch (action) {
            case Action.DISPLAY_RECORD_LIST:
                resp = MyApplication.getInstance().getHttpGateway().doGet(BASE_URL + RECORD_URL);
                break;
            case Action.DISPLAY_TS_LIST:
                resp = MyApplication.getInstance().getHttpGateway().doGet(BASE_URL + SHEET_URL + param);
                break;
            case Action.GET_SHARE_TS_LIST:
                break;
            case Action.GET_SUMMARY:
                break;
            case Action.GET_USER_PROFILE:
                resp = MyApplication.getInstance().getHttpGateway().doGet(BASE_URL + USER_URL + param);
                break;
            case Action.GET_USER_SHARE_STATUS:
                break;
            case Action.GET_SHARE_USER_LIST:
                resp = MyApplication.getInstance().getHttpGateway().doGet(BASE_URL + SHEET_URL+ param);
                break;
            case Action.INSERT_RECORD:
                resp = MyApplication.getInstance().getHttpGateway().doPost(BASE_URL + RECORD_URL, param, json);
                break;
            case Action.INSERT_NEW_TS:
                break;
            case Action.ADD_NEW_USER:
                resp = MyApplication.getInstance().getHttpGateway().doPost(BASE_URL + USER_URL, param, json);
                break;
            case Action.ADD_SHARE:
                resp = MyApplication.getInstance().getHttpGateway().doPost(BASE_URL + SHARE_URL, param, json);
                break;
            case Action.UPDATE_RECORD:
                resp = MyApplication.getInstance().getHttpGateway().doPut(BASE_URL + RECORD_URL, "/"+param, json);
                break;
            case Action.UPDATE_CURRENT_TS:
                break;
            case Action.UPDATE_USER:
                break;
            case Action.DELETE_RECORD:
                break;
            case Action.UPDATE_SHARE:
                resp = MyApplication.getInstance().getHttpGateway().doPut(BASE_URL + SHARE_URL, param, json);
                break;
            case Action.DELETE_TIMESHEET:
                break;
            case Action.DELETE_USER:
                break;
            case Action.DELETE_SHARE:
                resp = MyApplication.getInstance().getHttpGateway().doDelete(BASE_URL + SHARE_URL, param);
                break;
            default:
                break;
        }
        return resp;
    }

}
