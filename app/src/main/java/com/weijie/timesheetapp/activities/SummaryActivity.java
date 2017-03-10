package com.weijie.timesheetapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.weijie.timesheetapp.R;

public class SummaryActivity extends AppCompatActivity {


    SmoothDateRangePickerFragment smoothDateRangePickerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                    @Override
                    public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                               int yearStart, int monthStart,
                                               int dayStart, int yearEnd,
                                               int monthEnd, int dayEnd) {
                        // grab the date range, do what you want
                    }
                });
    }

    public void selectDateRange(View view) {
        smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");
    }
}
