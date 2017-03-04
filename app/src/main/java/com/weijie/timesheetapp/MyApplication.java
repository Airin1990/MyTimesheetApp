package com.weijie.timesheetapp;

import android.app.Application;
import android.util.Log;

import com.weijie.timesheetapp.network.HttpGateway;

/**
 * Created by weiji_000 on 2/28/2017.
 */

public class MyApplication extends Application {


    private static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication mInstance;
    private HttpGateway httpGateway;


    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate of MyApplication");
        super.onCreate();
        mInstance = this;
        httpGateway = new HttpGateway();
    }

    private long userId;

    public HttpGateway getHttpGateway() {
        return httpGateway;
    }

    public static MyApplication getInstance() {
        return mInstance;
    }
}
