package com.weijie.timesheetapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.network.Controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Response;

public class SummaryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SummaryActivity.class.getSimpleName();
    private Button button;
    private TextView sum1;
    private TextView sum2;
    private TextView sum3;
    private TextView sum4;
    private TextView sum5;
    private TextView sum6;
    private TextView sum7;
    private TextView sum8;

    SmoothDateRangePickerFragment smoothDateRangePickerFragment;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private String start_date;
    private String end_date;
    private String stids;
    private String rtids;
    private long uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        stids = getIntent().getStringExtra("stids");
        rtids = getIntent().getStringExtra("rtids");
        uid = getIntent().getLongExtra("uid", 0);

        sum1 = (TextView) findViewById(R.id.sum_val1);
        sum2 = (TextView) findViewById(R.id.sum_val2);
        sum3 = (TextView) findViewById(R.id.sum_val3);
        sum4 = (TextView) findViewById(R.id.sum_val4);
        sum5 = (TextView) findViewById(R.id.sum_val5);
        sum6 = (TextView) findViewById(R.id.sum_val6);
        sum7 = (TextView) findViewById(R.id.sum_val7);
        sum8 = (TextView) findViewById(R.id.sum_val8);

        button = (Button) findViewById(R.id.generate_report);
        button.setVisibility(View.INVISIBLE);
        button.setOnClickListener(this);

        smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                    @Override
                    public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                               int yearStart, int monthStart,
                                               int dayStart, int yearEnd,
                                               int monthEnd, int dayEnd) {
                        // grab the date range, do what you want
                        Calendar cal = Calendar.getInstance();
                        cal.set(yearStart, monthStart, dayStart);
                        start_date = sdf.format(cal.getTime());
                        cal.set(yearEnd, monthEnd, dayEnd);
                        end_date = sdf.format(cal.getTime());
                        button.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void selectDateRange(View view) {
        smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.generate_report) {
            generateReport();
        }
    }

    private void generateReport() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String param = "?start=" + start_date +"&end=" + end_date;
                if (!stids.isEmpty()) {
                    param += "&stids=" + stids;
                }
                if (!rtids.isEmpty()) {
                    param += "&rtids=" + rtids +"&uid=" + uid;
                }
                Log.d(TAG, param);
                Response resp = Controller.AppEvent(Controller.Action.GET_SUMMARY, param, null);
                try {
                    String json = resp.body().string();
                    Log.d(TAG, json);
                    JSONObject jsonObject = new JSONObject(json);

                    JSONArray field1 = jsonObject.getJSONArray("week_summary");
                    Log.v(TAG, field1.toString());
                    String val1 = "";
                    for (int i = 0; i < field1.length(); i++) {
                        JSONObject temp = field1.getJSONObject(i);
                        Log.v(TAG, temp.toString());
                        if (temp.optString("start", null) != null) {
                            val1 += temp.getString("start") +" ~ "+ temp.getString("end")+"\n";
                            val1 += "Total: " +(temp.getInt("work_time")/60) + "hr " + (temp.getInt("work_time")%60)+ "min\n";
                        }
                        else if (temp.optString("Average", null) != null){
                            val1 += "\nAverage: " + (temp.getInt("Average")/60) + "hr " + (temp.getInt("Average")%60)+ "min/week";
                        }
                    }
                    final String finalVal1 = val1.isEmpty() ? "No Data": val1;

                    JSONArray field2 = jsonObject.getJSONArray("weekday_summary");
                    String val2 = "";
                    for (int i = 0; i < field2.length(); i++) {
                        JSONObject temp = field2.getJSONObject(i);
                        Log.v(TAG, temp.toString());
                        if (temp.optString("start", null) != null) {
                            val2 += temp.getString("start") +" ~ "+ temp.getString("end")+"\n";
                            val2 += "Total: " +(temp.getInt("work_time")/60) + "hr " + (temp.getInt("work_time")%60)+ "min\n";
                        }
                        else if (temp.optString("Average", null) != null){
                            val2 += "\nAverage: " + (temp.getInt("Average")/60) + "hr " + (temp.getInt("Average")%60)+ "min/week";
                        }
                    }
                    final String finalVal2 = val2.isEmpty() ? "No Data" : val2;

                    int field3 = jsonObject.getInt("weekend_total");
                    int field4 = jsonObject.getInt("weekend_average");
                    String val3 = "";
                    if (field3 == -1 || field4 == -1) {
                        val3 = "No Data";
                    } else {
                        val3 += "Total: " + field3 / 60 + "hr " + field3 % 60 + "min\n";
                        val3 += "Average: " + field4 / 60 + "hr " + field4 % 60 + "min";
                    }
                    final String finalVal3 = val3;

                    JSONArray field5 = jsonObject.getJSONArray("month_summary");
                    String val4 = "";
                    for (int i = 0; i < field5.length(); i++) {
                        JSONObject temp = field5.getJSONObject(i);
                        Log.v(TAG, temp.toString());
                        if (temp.optString("month", null) != null) {
                            val4 += temp.getString("month") +": "+ (temp.getInt("work_time")/60) + "hr " + (temp.getInt("work_time")%60)+ "min\n";
                        }
                        else if (temp.optString("Average", null) != null){
                            val4 += "\nAverage: " + (temp.getInt("Average")/60) + "hr " + (temp.getInt("Average")%60)+ "min/month";
                        }
                    }
                    final String finalVal4 = val4.isEmpty() ? "No Data" : val4;

                    int field6 = jsonObject.getInt("average_hours_per_day");
                    int field7 = jsonObject.getInt("average_break_per_day");
                    String field8 = jsonObject.getString("last_day_updated");
                    int field9 = jsonObject.getInt("number_of_days_since_last_update");

                    final String val5 = field6/60 + "hr " + field6%60 +"min";
                    final String val6 = field7/60 + "hr " + field7%60 +"min";
                    final String val7 = field8.isEmpty()? "No Data":field8;
                    final String val8 = field9 >= 0? String.valueOf(field9) +" days":"No Data";

                    SummaryActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sum1.setText(finalVal1);
                            sum2.setText(finalVal2);
                            sum3.setText(finalVal3);
                            sum4.setText(finalVal4);
                            sum5.setText(val5);
                            sum6.setText(val6);
                            sum7.setText(val7);
                            sum8.setText(val8);
                        }
                    });



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        thread.start();
    }
}
