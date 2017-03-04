package com.weijie.timesheetapp.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
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

    public void doPost(String postUrl, String param, String json) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("author","test");
            jsonObject.put("message","hello Weijie");
            jsonObject.put("created", "2017-02-22T22:09:58.686-05:00");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json = jsonObject.toString();
        Log.d("OKHttp json",json);

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d("OKHttp",res);
            }
        });

    }
}
