package com.weijie.timesheetapp.network;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by weiji_000 on 2/23/2017.
 */

public final class HttpGateway {


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient okHttpClient;

    public HttpGateway() {
        okHttpClient = new OkHttpClient();
    }

    public Response doGet(String getUrl) {


        Request request = new Request.Builder()
                .url(getUrl)
                .build();


        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;

    }

    public Response doPost(String postUrl, String param, JSONObject json) {

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(postUrl+param)
                .post(body)
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public Response doPut(String postUrl, String param, JSONObject json) {
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(postUrl+param)
                .put(body)
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
