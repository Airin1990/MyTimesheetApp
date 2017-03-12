package com.weijie.timesheetapp.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.database.TSContract;
import com.weijie.timesheetapp.network.Controller;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Response;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EditorActivity.class.getSimpleName();
    EditText date;
    EditText start_time;
    EditText end_time;
    EditText break_time;
    EditText work_time;
    Button button;
    EditText comments;
    int editMode = 0;
    private int mode;
    String record_ID;

    DatePickerDialog mDatePicker;
    TimePickerDialog mTimePicker;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat tf = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_discard);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab_save);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRecord();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        date = (EditText) findViewById(R.id.date_edit);
        start_time = (EditText) findViewById(R.id.st_edit);
        end_time = (EditText) findViewById(R.id.et_edit);
        break_time = (EditText) findViewById(R.id.b_edit);
        work_time = (EditText) findViewById(R.id.wt_edit);
        button = (Button) findViewById(R.id.cal_button);
        comments = (EditText) findViewById(R.id.comment_edit);

        mode = getIntent().getIntExtra("mode", TSContract.ShareEntry.MODE_EDIT);

        if (mode != TSContract.ShareEntry.MODE_VIEWONLY) {
            setTitle("Edit Mode");
        } else {
            setTitle("View Only Mode");
            fab.setVisibility(View.GONE);
            fab2.setVisibility(View.GONE);
        }

        editMode = getIntent().getIntExtra("editMode", 1);
        if (editMode == 1) {
            populateDefaultValue();
        } else {
            populateDefaultValue();
            record_ID = getIntent().getStringExtra("rid");
            date.setText(getIntent().getStringExtra("date"));
            start_time.setText(getIntent().getStringExtra("s"));
            end_time.setText(getIntent().getStringExtra("e"));
            int pt1 = getIntent().getIntExtra("b", 0);
            int pt2 = getIntent().getIntExtra("w", 0);
            try {
                break_time.setText(tf.format(tf.parse(""+pt1/60+":"+pt1%60)));
                work_time.setText(tf.format(tf.parse(""+pt2/60+":"+pt2%60)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            comments.setText(getIntent().getStringExtra("c"));
        }


    }

    private void saveRecord() {
        Thread schedule = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("date",date.getText().toString());
                    jsonObject.put("start_time",start_time.getText().toString()+":00");
                    jsonObject.put("end_time", end_time.getText().toString()+":00");
                    jsonObject.put("break_time", toMins(break_time.getText().toString()));
                    jsonObject.put("work_time", toMins(work_time.getText().toString()));
                    jsonObject.put("is_weekend", 0);
                    jsonObject.put("tid", 1299);
                    jsonObject.put("comments", comments.getText().toString());
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, jsonObject.toString());
                Response response;
                if (editMode == 1) {
                    response = Controller.AppEvent(Controller.Action.INSERT_RECORD, "", jsonObject);
                }
                else {
                    response = Controller.AppEvent(Controller.Action.UPDATE_RECORD, record_ID, jsonObject);
                }
                if (response.isSuccessful()) {
                    EditorActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (editMode == 1) {
                                Toast.makeText(getApplicationContext(), "New record added!", Toast.LENGTH_LONG).show();
                                EditorActivity.this.finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Record upadted!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        schedule.start();
    }

    private void populateDefaultValue() {
        date.setText(df.format(new Date()));
        date.setInputType(InputType.TYPE_NULL);
        if (mode != TSContract.ShareEntry.MODE_VIEWONLY)
            date.setOnClickListener(this);
        start_time.setText("08:00");
        start_time.setInputType(InputType.TYPE_NULL);
        if (mode != TSContract.ShareEntry.MODE_VIEWONLY)
            start_time.setOnClickListener(this);
        end_time.setText("17:00");
        end_time.setInputType(InputType.TYPE_NULL);
        if (mode != TSContract.ShareEntry.MODE_VIEWONLY)
            end_time.setOnClickListener(this);
        break_time.setText("01:00");
        break_time.setInputType(InputType.TYPE_NULL);
        if (mode != TSContract.ShareEntry.MODE_VIEWONLY)
            break_time.setOnClickListener(this);
        work_time.setInputType(InputType.TYPE_NULL);
        work_time.setText("08:00");
        if (mode != TSContract.ShareEntry.MODE_VIEWONLY)
            button.setOnClickListener(this);
        else
            button.setEnabled(false);
        if (mode == TSContract.ShareEntry.MODE_VIEWONLY)
            comments.setInputType(InputType.TYPE_NULL);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        Date sTime = new Date();
        Calendar mTime = Calendar.getInstance();
        int mHour;
        int mMin;
        switch(id) {
            case R.id.date_edit:
                Date curDate = new Date();
                try {
                    curDate = df.parse(date.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar mcurrentDate = Calendar.getInstance();
                mcurrentDate.setTime(curDate);
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                mDatePicker = new DatePickerDialog(this,AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear,selectedmonth,selectedday);
                        date.setText(df.format(newDate.getTime()));
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
                break;
            case R.id.st_edit:
                try {
                    sTime = tf.parse(start_time.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mTime.setTime(sTime);
                mHour = mTime.get(Calendar.HOUR_OF_DAY);
                mMin = mTime.get(Calendar.MINUTE);

                mTimePicker = new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(Calendar.HOUR_OF_DAY, hour);
                        newDate.set(Calendar.MINUTE, minute);
                        start_time.setText(tf.format(newDate.getTime()));
                    }
                }, mHour, mMin, true);
                mTimePicker.setTitle("Select start time");
                mTimePicker.show();
                break;
            case R.id.et_edit:
                try {
                    sTime = tf.parse(end_time.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mTime.setTime(sTime);
                mHour = mTime.get(Calendar.HOUR_OF_DAY);
                mMin = mTime.get(Calendar.MINUTE);

                mTimePicker = new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(Calendar.HOUR_OF_DAY, hour);
                        newDate.set(Calendar.MINUTE, minute);
                        end_time.setText(tf.format(newDate.getTime()));
                    }
                }, mHour, mMin, true);
                mTimePicker.setTitle("Select end time");
                mTimePicker.show();
                break;
            case R.id.b_edit:
                try {
                    sTime = tf.parse(break_time.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mTime.setTime(sTime);
                mHour = mTime.get(Calendar.HOUR_OF_DAY);
                mMin = mTime.get(Calendar.MINUTE);

                mTimePicker = new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(Calendar.HOUR_OF_DAY, hour);
                        newDate.set(Calendar.MINUTE, minute);
                        break_time.setText(tf.format(newDate.getTime()));
                    }
                }, mHour, mMin, true);
                mTimePicker.setTitle("Select break time");
                mTimePicker.show();
                break;

            case R.id.cal_button:
                ComputeNetWorkTime();
                break;
        }

    }


    private int toMins(String s) {
        String[] hourMin = s.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int mins = Integer.parseInt(hourMin[1]);
        int hoursInMins = hour * 60;
        return hoursInMins + mins;
    }

    private void ComputeNetWorkTime() {
        try {
            Date st = tf.parse(start_time.getText().toString());
            Date et = tf.parse(end_time.getText().toString());
            Date bt = tf.parse(break_time.getText().toString());
            if (et.getTime() - st.getTime() <= 0) {
                work_time.setText("");
                Toast.makeText(this, "The end time is earlier than start time, please check!", Toast.LENGTH_LONG).show();
                return;
            }
            long dif = et.getTime() - st.getTime();
            int difMin =(int) dif / (60 * 1000) % 60;
            int difHours = (int) dif / (60 * 60 * 1000) % 24;

            Date interval = tf.parse(""+difHours+":"+difMin);

            if (interval.getTime() - bt.getTime() <= 0) {
                work_time.setText("");
                Toast.makeText(this, "No valid work hours, please check!", Toast.LENGTH_LONG).show();
                return;
            }
            dif = interval.getTime() - bt.getTime();
            difMin =(int) dif / (60 * 1000) % 60;
            difHours = (int) dif / (60 * 60 * 1000) % 24;

            Log.e("calculate time dif",""+difHours+":"+difMin);
            Calendar newDate = Calendar.getInstance();
            newDate.set(Calendar.HOUR_OF_DAY, difHours);
            newDate.set(Calendar.MINUTE, difMin);
            work_time.setText(tf.format(newDate.getTime()));


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
