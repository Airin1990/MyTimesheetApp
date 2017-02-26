package com.weijie.timesheetapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.weijie.timesheetapp.R;

import java.util.ArrayList;
import java.util.Arrays;

public class TimesheetActivity extends AppCompatActivity {


    ArrayList<String> dummyData;
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
        listView.setAdapter(arrayAdapter);
    }

    private void populateDummyData() {
        dummyData = new ArrayList<>(Arrays.asList("One","Two","Three","Four","Five","Six","Seven","Eight"));

    }

}
