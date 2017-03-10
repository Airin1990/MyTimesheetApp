package com.weijie.timesheetapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.adapters.TSAdapter;
import com.weijie.timesheetapp.models.Timesheet;
import com.weijie.timesheetapp.network.Controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.Response;

public class HomepageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HomepageActivity.class.getSimpleName();
    Profile profile;
    FirebaseUser firebaseUser;
    TextView username_tv;
    ProfilePictureView userPic;
    ListView listView;
    TSAdapter tsAdapter;
    TSAdapter shareAdapter;
    boolean showCheckbox = false;
    FloatingActionButton fab;
    AlertDialog dialog;
    ListView sharelv;

    private long userId;
    private List<Timesheet> tsList;
    private List<Object> pendlingList;
    private List<Object> adapterList;
    private boolean hasPendingShare = true;

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
                    Toast.makeText(getApplicationContext(), "The selected TIDs are:" + tsAdapter.getSelectedTIDs(), Toast.LENGTH_LONG).show();
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

        // If user is not authenticated
        if (!ObtainUserInfo()) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            this.finish();
        }

        listView = (ListView) findViewById(R.id.homepage_list);

        View emptyView = getLayoutInflater().inflate(R.layout.empty_listview, null);
        addContentView(emptyView, listView.getLayoutParams());
        listView.setEmptyView(emptyView);

    }

    private boolean ObtainUserInfo() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        username_tv = (TextView) hView.findViewById(R.id.user_tv);
        userPic = (ProfilePictureView) hView.findViewById(R.id.profile_pic_view);

        if ((profile = Profile.getCurrentProfile()) != null) {
            username_tv.setText(profile.getName());
            userPic.setProfileId(profile.getId());
            // retrieve user info by fbid
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Response response = Controller.AppEvent(Controller.Action.GET_USER_PROFILE, "?fbid=" + profile.getId(), null);

                    String json = null;
                    try {
                        json = response.body().string();
                        Log.d(TAG, json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // If fb user first time login
                    if (json.isEmpty()) {
                        JSONObject newjson = new JSONObject();
                        try {
                            newjson.put("fbid", profile.getId());
                            newjson.put("firstName", profile.getFirstName());
                            newjson.put("lastName", profile.getLastName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Response resp = Controller.AppEvent(Controller.Action.ADD_NEW_USER, "", newjson);
                        try {
                            json = resp.body().string();
                            Log.d(TAG, json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    JSONObject userjson;
                    try {
                        userjson = new JSONObject(json);
                        userId = userjson.getLong("uid");
                        Log.d(TAG, "Facebook User:" + userId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;

        } else if ((firebaseUser = FirebaseAuth.getInstance().getCurrentUser()) != null) {
            // retrieve user info by email, the user surely has the profile
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Response response = Controller.AppEvent(Controller.Action.GET_USER_PROFILE, "?email=" + firebaseUser.getEmail(), null);

                    String json = null;
                    try {
                        json = response.body().string();
                        Log.d(TAG, json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (!json.isEmpty()) {
                        JSONObject userjson;
                        try {
                            userjson = new JSONObject(json);
                            userId = userjson.getLong("uid");
                            final String firstName = userjson.getString("firstName");
                            final String lastName = userjson.getString("lastName");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    username_tv.setText(firstName + " " + lastName);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Log.d(TAG, "Email user:" + userId);
                }
            });

            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }

        return false;
    }

    private void populateTimesheetInfo() {
        tsList = new ArrayList<>();
        // retrieve timesheets, share ts by user id

        Thread tsthread = new Thread(new Runnable() {
            @Override
            public void run() {
                Response resp = Controller.AppEvent(Controller.Action.DISPLAY_TS_LIST, "?uid="+String.valueOf(userId), null);
                JSONArray jsonArray;
                try {
                    String json = resp.body().string();
                    jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject temp = (JSONObject) jsonArray.get(i);
                        Timesheet tsnew = new Timesheet(temp.getLong("tid"),
                                temp.getString("tName"),
                                temp.getLong("uid"),
                                temp.getInt("shareMode"),
                                temp.getInt("shareStatus"));
                        tsList.add(tsnew);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterList = new ArrayList<>();
                        pendlingList = new ArrayList<>();
                        pendlingList.addAll(tsList);
                        adapterList.add(new String("My Timesheet"));
                        adapterList.addAll(tsList);
                        adapterList.add(new String("Shared Timesheet"));
                        adapterList.addAll(tsList);
                        adapterList.add(new String("Revoked Timesheet"));
                        adapterList.addAll(tsList);
                        shareAdapter = new TSAdapter(getApplicationContext(), pendlingList, true);
                        sharelv.setAdapter(shareAdapter);
                        tsAdapter = new TSAdapter(getApplicationContext(), adapterList, showCheckbox);
                        listView.setAdapter(tsAdapter);
                        dialog.show();
                    }
                });
            }
        });

        tsthread.start();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (userId > 0) {
            populateTimesheetInfo();
            if (hasPendingShare) {
                showShareReminder();
            }
        }
    }

    private void showShareReminder() {
        View view = getLayoutInflater().inflate(R.layout.dialog_pop_up, null);
        dialog = new AlertDialog.Builder(this, R.style.AppTheme_CustomDialog)
                .setView(view)
                .create();
        sharelv = (ListView) view.findViewById(R.id.share_lv);
        FancyButton accept = (FancyButton) view.findViewById(R.id.accept_act);
        FancyButton dismiss = (FancyButton) view.findViewById(R.id.dismiss_act);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "TiDs are:"+shareAdapter.getSelectedTIDs(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "TiDs are:"+shareAdapter.getSelectedTIDs(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
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
        } else if (id == R.id.action_multi_select) {
            showCheckbox = !showCheckbox;
            tsAdapter = new TSAdapter(this, adapterList, showCheckbox);
            listView.setAdapter(tsAdapter);
            if (showCheckbox) {
                fab.setImageResource(R.drawable.ic_assignment_white_24dp);
            } else {
                fab.setImageResource(R.drawable.ic_add_white_24dp);
            }
            return true;
        } else if (id == R.id.action_log_out) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
            }
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            this.finish();
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
