package com.weijie.timesheetapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.adapters.TSAdapter;
import com.weijie.timesheetapp.models.Timesheet;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Profile profile;
    TextView username_tv;
    ProfilePictureView userPic;
    List<Timesheet> list;
    ListView listView;
    TSAdapter tsAdapter;
    boolean showCheckbox = false;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showCheckbox) {
                    Intent intent = new Intent(getApplicationContext(), SummaryActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), TimesheetActivity.class);
                    startActivity(intent);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);

//        String name = getIntent().getStringExtra("user_name");
//        String id = getIntent().getStringExtra("user_id");
//        Log.v("facebook", name + id);
//        username_tv = (TextView) hView.findViewById(R.id.user_tv);
//        username_tv.setText(name);
//        userPic = (ProfilePictureView) hView.findViewById(R.id.profile_pic_view);
//        userPic.setProfileId(id);

        listView = (ListView) findViewById(R.id.homepage_list);
        populateTimesheetInfo();
        tsAdapter = new TSAdapter(this, list, showCheckbox);
        TextView textView = new TextView(this);
        textView.setText("My Timesheet");
        listView.addHeaderView(textView);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        listView.setAdapter(tsAdapter);

    }

    private void populateTimesheetInfo() {
        list = new ArrayList<>();
        list.add(new Timesheet(1,"first ts","jake"));
        list.add(new Timesheet(2,"second ts","james"));
        list.add(new Timesheet(3,"third ts","julia"));
        list.add(new Timesheet(4,"fourth ts","jacob"));
        list.add(new Timesheet(5,"fifth ts","jamie"));
        list.add(new Timesheet(1,"first ts","jake"));
        list.add(new Timesheet(2,"second ts","james"));
        list.add(new Timesheet(3,"third ts","julia"));
        list.add(new Timesheet(4,"fourth ts","jacob"));
        list.add(new Timesheet(5,"fifth ts","jamie"));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_multi_select) {
            showCheckbox = !showCheckbox;
            tsAdapter = new TSAdapter(this, list, showCheckbox);
            listView.setAdapter(tsAdapter);
            if (showCheckbox) {
                fab.setImageResource(R.drawable.ic_assignment_white_24dp);
            } else {
                fab.setImageResource(R.drawable.ic_add_white_24dp);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
