package com.weijie.timesheetapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.bind.SqlDateTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.database.TSContract;
import com.weijie.timesheetapp.models.Record;
import com.weijie.timesheetapp.network.Controller;

import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.Response;

public class TimesheetActivity extends AppCompatActivity {

    private final static String TAG = TimesheetActivity.class.getSimpleName();

    ArrayList<String> dummyData;
    ArrayList<Record> recordList;
    ArrayAdapter arrayAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesheet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateDummyData();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dummyData);
        listView = (ListView) findViewById(R.id.ts_listview);
        View emptyView = getLayoutInflater().inflate(R.layout.empty_listview, null);
        addContentView(emptyView, listView.getLayoutParams());
        listView.setEmptyView(emptyView);
        listView.setAdapter(arrayAdapter);
    }

    private void populateDummyData() {
        dummyData = new ArrayList<>(Arrays.asList("One","Two","Three","Four","Five","Six","Seven","Eight"));
        Thread schedule = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Response resp = Controller.AppEvent(Controller.Action.DISPLAY_RECORD_LIST);

                    Type type = new TypeToken<List<Record>>(){}.getType();

                    String json = resp.body().string();

                    SqlDateTypeAdapter sqlAdapter = new SqlDateTypeAdapter();

                    GsonBuilder gsonBuilder = new GsonBuilder()
                            .registerTypeAdapter(java.util.Date.class, sqlAdapter)
                            .setDateFormat("yyyy-MM-dd");

                    gsonBuilder.registerTypeAdapter(java.sql.Timestamp.class, new JsonDeserializer<Timestamp>() {

                        @Override
                        public Timestamp deserialize(JsonElement json, Type type, JsonDeserializationContext deserializationContext) throws JsonParseException {

                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                            try {
                                Date date = dateFormat.parse(json.getAsString());
                                long output=date.getTime()/1000L;
                                String str=Long.toString(output);
                                long timestamp = Long.parseLong(str) * 1000;
                                return new Timestamp(timestamp);
                            } catch (ParseException e) {
                                throw new JsonParseException(e);
                            }

                        }
                    });

                    gsonBuilder.registerTypeAdapter(java.sql.Time.class, new JsonDeserializer<Time>() {
                        @Override
                        public Time deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss'Z'");
                            try {
                                Date date = dateFormat.parse(json.getAsString());
                                Time time = new Time(date.getTime());
                                return time;
                            } catch (ParseException e) {
                                throw new JsonParseException(e);
                            }
                        }
                    });

                    Gson gson = gsonBuilder.create();
                    final List<Record> fromJson = gson.fromJson(json,type);
                    deleteAllRecord();
                    ContentValues[] valueList = new ContentValues[fromJson.size()];

                    for (int i = 0; i < fromJson.size(); i++) {
                        ContentValues value = new ContentValues();
                        Record r = fromJson.get(i);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
                        value.put(TSContract.RecordEntry.COLUMN_RID,r.getRid());
                        value.put(TSContract.RecordEntry.COLUMN_DATE, df.format(r.getDate()));
                        value.put(TSContract.RecordEntry.COLUMN_START_TIME, tf.format(r.getStart_time()));
                        value.put(TSContract.RecordEntry.COLUMN_END_TIME, tf.format(r.getEnd_time()));
                        value.put(TSContract.RecordEntry.COLUMN_BREAK, r.getBreak_time());
                        value.put(TSContract.RecordEntry.COLUMN_WORK_TIME, r.getWork_time());
                        value.put(TSContract.RecordEntry.COLUMN_COMMENTS, r.getComments());
                        value.put(TSContract.RecordEntry.COLUMN_TID, r.getTid());
                        value.put(TSContract.RecordEntry.COLUMN_IS_WEEKEND, r.getIs_weekend());
                        valueList[i] = value;
                    }
                    getContentResolver().bulkInsert(TSContract.RecordEntry.CONTENT_URI, valueList);

                    Log.d(TAG,fromJson.get(0)+":"+String.valueOf(fromJson.size()));
                    TimesheetActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayAdapter = new ArrayAdapter(TimesheetActivity.this, android.R.layout.simple_list_item_1, fromJson);
                            listView.setAdapter(arrayAdapter);
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error :: " + e.getMessage(), e);
                }
            }
        });
        schedule.start();
    }

    private void insertRecord(Record r) {
        ContentValues values = new ContentValues();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        values.put(TSContract.RecordEntry.COLUMN_RID,r.getRid());
        values.put(TSContract.RecordEntry.COLUMN_DATE, df.format(r.getDate()));
        values.put(TSContract.RecordEntry.COLUMN_START_TIME, tf.format(r.getStart_time()));
        values.put(TSContract.RecordEntry.COLUMN_END_TIME, tf.format(r.getEnd_time()));
        values.put(TSContract.RecordEntry.COLUMN_BREAK, r.getBreak_time());
        values.put(TSContract.RecordEntry.COLUMN_WORK_TIME, r.getWork_time());
        values.put(TSContract.RecordEntry.COLUMN_COMMENTS, r.getComments());
        values.put(TSContract.RecordEntry.COLUMN_TID, r.getTid());
        values.put(TSContract.RecordEntry.COLUMN_IS_WEEKEND, r.getIs_weekend());
        getContentResolver().insert(TSContract.RecordEntry.CONTENT_URI, values);

    }

    private int deleteAllRecord() {
        int rowsDeleted = getContentResolver().delete(TSContract.RecordEntry.CONTENT_URI, null, null);
        return rowsDeleted;
    }

}
