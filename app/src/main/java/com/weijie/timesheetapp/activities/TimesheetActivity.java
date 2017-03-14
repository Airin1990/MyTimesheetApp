package com.weijie.timesheetapp.activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.bind.SqlDateTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.adapters.RecordCursorAdapter;
import com.weijie.timesheetapp.adapters.ShareUserAdapter;
import com.weijie.timesheetapp.database.TSContract;
import com.weijie.timesheetapp.models.Record;
import com.weijie.timesheetapp.models.User;
import com.weijie.timesheetapp.network.Controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.Response;

import static com.weijie.timesheetapp.network.Controller.AppEvent;

public class TimesheetActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final String TAG = TimesheetActivity.class.getSimpleName();
    private static final int RECORD_LOADER = 0;
    private boolean shouldExecuteOnResume;
    private long currentTID;
    private long currentUID;
    private List<User> userList;
    private int mode;
    private int status;

    RecordCursorAdapter mRecordCursorAdapter;
    ListView listView;
    Spinner spinner;
    ToggleButton toggleButton;
    android.support.v7.app.AlertDialog dialog;
    ProgressDialog loading;
    ListView userlv;
    ShareUserAdapter shareUserAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesheet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loading = new ProgressDialog(this);
        loading.setCancelable(true);
        loading.setMessage("Loading timesheet...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                // mode = 1 if insertion, mode = 0 if update
                intent.putExtra("mode",1);
                intent.putExtra("TID", currentTID);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentTID = getIntent().getLongExtra("TID", 0);
        currentUID = getIntent().getLongExtra("UID", 0);
        Log.d(TAG, ""+currentTID);
        mode = getIntent().getIntExtra("mode", TSContract.ShareEntry.MODE_EDIT);
        status = getIntent().getIntExtra("status", TSContract.ShareEntry.STATUS_ACCEPTED);

        setTitle(getIntent().getStringExtra("sheetName"));

        if (mode != TSContract.ShareEntry.MODE_VIEWONLY) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        listView = (ListView) findViewById(R.id.ts_listview);
        View emptyView = getLayoutInflater().inflate(R.layout.empty_listview, null);
        addContentView(emptyView, listView.getLayoutParams());
        listView.setEmptyView(emptyView);
        listView.setVisibility(View.INVISIBLE);
        mRecordCursorAdapter = new RecordCursorAdapter(this, null);
        listView.setAdapter(mRecordCursorAdapter);
        listView.setOnItemClickListener(this);
        populateDummyData();
        shouldExecuteOnResume = false;
        getLoaderManager().initLoader(RECORD_LOADER, null, this);

        spinner = (Spinner) findViewById(R.id.spinner);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] projection = {TSContract.RecordEntry.COLUMN_RID+ " as "+ BaseColumns._ID,
                        TSContract.RecordEntry.COLUMN_DATE,
                        TSContract.RecordEntry.COLUMN_START_TIME,
                        TSContract.RecordEntry.COLUMN_END_TIME,
                        TSContract.RecordEntry.COLUMN_BREAK,
                        TSContract.RecordEntry.COLUMN_WORK_TIME,
                        TSContract.RecordEntry.COLUMN_COMMENTS,
                        TSContract.RecordEntry.COLUMN_TID};
                Cursor c;
                String scd = toggleButton.isChecked() ? " DESC":" ASC";
                switch (i) {
                    case 1:
                        c = getContentResolver().query(TSContract.RecordEntry.CONTENT_URI, projection, null, null, TSContract.RecordEntry.COLUMN_DATE + scd);
                        mRecordCursorAdapter.changeCursor(c);
                        break;
                    case 2:
                        c = getContentResolver().query(TSContract.RecordEntry.CONTENT_URI, projection, null, null, TSContract.RecordEntry.COLUMN_START_TIME + scd);
                        mRecordCursorAdapter.changeCursor(c);
                        break;
                    case 3:
                        c = getContentResolver().query(TSContract.RecordEntry.CONTENT_URI, projection, null, null, TSContract.RecordEntry.COLUMN_END_TIME + scd);
                        mRecordCursorAdapter.changeCursor(c);
                        break;
                    case 4:
                        c = getContentResolver().query(TSContract.RecordEntry.CONTENT_URI, projection, null, null, TSContract.RecordEntry.COLUMN_BREAK + scd);
                        mRecordCursorAdapter.changeCursor(c);
                        break;
                    case 5:
                        c = getContentResolver().query(TSContract.RecordEntry.CONTENT_URI, projection, null, null, TSContract.RecordEntry.COLUMN_WORK_TIME + scd);
                        mRecordCursorAdapter.changeCursor(c);
                        break;
                    case 0: default:
                        c = getContentResolver().query(TSContract.RecordEntry.CONTENT_URI, projection, null, null, null);
                        mRecordCursorAdapter.changeCursor(c);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String scd = b ? " DESC":" ASC";
                String[] projection = {TSContract.RecordEntry.COLUMN_RID+ " as "+ BaseColumns._ID,
                        TSContract.RecordEntry.COLUMN_DATE,
                        TSContract.RecordEntry.COLUMN_START_TIME,
                        TSContract.RecordEntry.COLUMN_END_TIME,
                        TSContract.RecordEntry.COLUMN_BREAK,
                        TSContract.RecordEntry.COLUMN_WORK_TIME,
                        TSContract.RecordEntry.COLUMN_COMMENTS,
                        TSContract.RecordEntry.COLUMN_TID};
                Cursor c;
                switch (spinner.getSelectedItemPosition()) {
                    case 1:
                        c = getContentResolver().query(TSContract.RecordEntry.CONTENT_URI, projection, null, null, TSContract.RecordEntry.COLUMN_DATE + scd);
                        mRecordCursorAdapter.changeCursor(c);
                        break;
                    case 2:
                        c = getContentResolver().query(TSContract.RecordEntry.CONTENT_URI, projection, null, null, TSContract.RecordEntry.COLUMN_START_TIME + scd);
                        mRecordCursorAdapter.changeCursor(c);
                        break;
                    case 3:
                        c = getContentResolver().query(TSContract.RecordEntry.CONTENT_URI, projection, null, null, TSContract.RecordEntry.COLUMN_END_TIME + scd);
                        mRecordCursorAdapter.changeCursor(c);
                        break;
                    case 4:
                        c = getContentResolver().query(TSContract.RecordEntry.CONTENT_URI, projection, null, null, TSContract.RecordEntry.COLUMN_BREAK + scd);
                        mRecordCursorAdapter.changeCursor(c);
                        break;
                    case 5:
                        c = getContentResolver().query(TSContract.RecordEntry.CONTENT_URI, projection, null, null, TSContract.RecordEntry.COLUMN_WORK_TIME + scd);
                        mRecordCursorAdapter.changeCursor(c);
                        break;
                    case 0: default:
                        c = getContentResolver().query(TSContract.RecordEntry.CONTENT_URI, projection, null, null, null);
                        mRecordCursorAdapter.changeCursor(c);
                        break;
                }
            }
        });


    }

    @Override
    protected void onResume() {
        if(shouldExecuteOnResume){
            populateDummyData();
        } else{
            shouldExecuteOnResume = true;
        }
        super.onResume();
    }

    private void populateDummyData() {
        loading.show();
        Thread schedule = new Thread(new Runnable(){
            @Override
            public void run() {
                try {

                    Response resp = null;
                    if (status != TSContract.ShareEntry.STATUS_REVOKED) {
                        resp = AppEvent(Controller.Action.DISPLAY_RECORD_LIST, "?tid=" + currentTID, null);
                    } else {
                        resp = AppEvent(Controller.Action.DISPLAY_REVOKE_RECORDS, "?tid=" + currentTID +"&uid=" + currentUID, null);
                    }

                    Type type = new TypeToken<List<Record>>(){}.getType();

                    String json = resp.body().string();

                    Log.d(TAG, json);

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

                    TimesheetActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setVisibility(View.VISIBLE);
                            loading.dismiss();
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error :: " + e.getMessage(), e);
                }
            }
        });
        schedule.start();
    }

    private int deleteAllRecord() {
        int rowsDeleted = getContentResolver().delete(TSContract.RecordEntry.CONTENT_URI, null, null);
        return rowsDeleted;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mode != TSContract.ShareEntry.MODE_VIEWONLY)
            getMenuInflater().inflate(R.menu.ts_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_remove:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_sharing:
                showShareConfirmationDialog();
                return true;
            case R.id.action_edit:
                showEditConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showEditConfirmationDialog() {
        final EditText nameEditText = new EditText(this);
        nameEditText.setHint("Enter timesheet name here");
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.edit)
                .setMessage(R.string.edit_ts_msg)
                .setView(nameEditText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        renameTimesheet();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void renameTimesheet() {
        Response response = Controller.AppEvent(Controller.Action.UPDATE_CURRENT_TS, "", null);
    }

    private void showShareConfirmationDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_share_form, null);
        dialog = new android.support.v7.app.AlertDialog.Builder(this, R.style.AppTheme_CustomDialog)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText editText = (EditText) view.findViewById(R.id.editText);
        FancyButton confirm = (FancyButton) view.findViewById(R.id.confirm_act);
        Button addBt = (Button) view.findViewById(R.id.add_share);
        final Switch modeSwitch = (Switch) view.findViewById(R.id.mode_switch);
        userlv = (ListView) view.findViewById(R.id.user_list);
        userList = getShareUserList();
        shareUserAdapter = new ShareUserAdapter(this, userList, currentTID);
        userlv.setAdapter(shareUserAdapter);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    dialog.dismiss();
            }
        });

        addBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validEmail(editText.getText().toString())) {
                    String email = editText.getText().toString();
                    boolean mode = modeSwitch.isChecked();
                    int ret = shareTimesheet(email, mode);
                    if (ret == 1) {
                        editText.setError("This user is already shared");
                    } else if (ret == 2) {
                        editText.setError("This Email is not registered");
                    } else {
                        userList = getShareUserList();
                        shareUserAdapter = new ShareUserAdapter(TimesheetActivity.this, userList, currentTID);
                        userlv.setAdapter(shareUserAdapter);
                    }
                }
                else {
                    editText.setError(getString(R.string.error_invalid_email));
                }
            }
        });

        dialog.show();
    }

    private List<User> getShareUserList() {
        final List<User> list = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Response resp = Controller.AppEvent(Controller.Action.GET_SHARE_USER_LIST, "/ulist?tid="+currentTID, null);
                JSONArray jsonArray;
                try {
                    String json = resp.body().string();
                    jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject temp = (JSONObject) jsonArray.get(i);
                        if (temp.getInt("shareStatus") != TSContract.ShareEntry.STATUS_REVOKED) {
                            User user = new User(temp.getLong("uid"),
                                    temp.getString("firstName"),
                                    temp.getString("lastName"),
                                    temp.optString("email", ""),
                                    temp.getInt("shareMode"),
                                    temp.getInt("shareStatus"));
                            list.add(user);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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

        return list;
    }

    private boolean validEmail(String s) {
        if (s.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(s).matches())
            return false;
        else
            return true;
    }

    private int shareTimesheet(final String email, final boolean mode) {
        //find if the user is in userlist
        for (User u: userList) {
            if (u.getEmail().equals(email)) return 1;
        }
        //Query if user is in db
        final long[] share_uid = new long[1];
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Response resp = Controller.AppEvent(Controller.Action.GET_USER_PROFILE, "?email=" + email, null);
                try {
                    String json = resp.body().string();
                    if (!json.isEmpty()) {
                        JSONObject jsonObject = new JSONObject(json);
                        share_uid[0] = jsonObject.getLong("uid");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        thread1.start();
        try {
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!(share_uid[0] > 0)) {
            return 2;
        }

        //Post user share relation

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    json.put("uid", share_uid[0]);
                    json.put("tid", currentTID);
                    json.put("shareMode", mode? TSContract.ShareEntry.MODE_EDIT: TSContract.ShareEntry.MODE_VIEWONLY);
                    json.put("shareStatus", TSContract.ShareEntry.STATUS_PENDING);
                    Response response = Controller.AppEvent(Controller.Action.ADD_SHARE, "", json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        thread2.start();
        try {
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return 0;

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.delete_timesheet_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteTimesheet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteTimesheet() {
        Response response = Controller.AppEvent(Controller.Action.DELETE_RECORD, "",null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {TSContract.RecordEntry.COLUMN_RID+ " as "+ BaseColumns._ID,
                TSContract.RecordEntry.COLUMN_DATE,
                TSContract.RecordEntry.COLUMN_START_TIME,
                TSContract.RecordEntry.COLUMN_END_TIME,
                TSContract.RecordEntry.COLUMN_BREAK,
                TSContract.RecordEntry.COLUMN_WORK_TIME,
                TSContract.RecordEntry.COLUMN_COMMENTS,
                TSContract.RecordEntry.COLUMN_TID };
        return new CursorLoader(this, TSContract.RecordEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mRecordCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecordCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor c = ((CursorAdapter) adapterView.getAdapter()).getCursor();
        c.moveToPosition(i);

        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra("mode", mode);
        intent.putExtra("editMode", 0);
        intent.putExtra("rid", c.getString(c.getColumnIndex(BaseColumns._ID)));
        intent.putExtra("date", c.getString(c.getColumnIndex(TSContract.RecordEntry.COLUMN_DATE)));
        intent.putExtra("s",c.getString(c.getColumnIndex(TSContract.RecordEntry.COLUMN_START_TIME)));
        intent.putExtra("e",c.getString(c.getColumnIndex(TSContract.RecordEntry.COLUMN_END_TIME)));
        intent.putExtra("b",c.getInt(c.getColumnIndex(TSContract.RecordEntry.COLUMN_BREAK)));
        intent.putExtra("w",c.getInt(c.getColumnIndex(TSContract.RecordEntry.COLUMN_WORK_TIME)));
        intent.putExtra("c",c.getString(c.getColumnIndex(TSContract.RecordEntry.COLUMN_COMMENTS)));
        intent.putExtra("TID", currentTID);
        startActivity(intent);

    }
}
