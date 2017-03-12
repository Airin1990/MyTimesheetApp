package com.weijie.timesheetapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.activities.TimesheetActivity;
import com.weijie.timesheetapp.database.TSContract;
import com.weijie.timesheetapp.models.User;
import com.weijie.timesheetapp.network.Controller;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

/**
 * Created by weiji_000 on 3/12/2017.
 */

public class ShareUserAdapter extends ArrayAdapter<User> {

    private static final String TAG = ShareUserAdapter.class.getSimpleName();
    private List<User> userList;
    private long tid;
    private Context context;

    public ShareUserAdapter(Context context, List<User> list, long tid) {
        super(context, R.layout.single_row_share_form, list);
        this.context = context;
        this.tid = tid;
        this.userList = list;
    }

    private class ViewHolder {
        TextView name;
        Switch isEdit;
        Button revokeBt;
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        User user = userList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.single_row_share_form, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.share_username);
            viewHolder.isEdit = (Switch) convertView.findViewById(R.id.mode_select);
            viewHolder.revokeBt = (Button) convertView.findViewById(R.id.revoke_sign);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(user.getFullName());
        viewHolder.isEdit.setOnCheckedChangeListener(null);
        viewHolder.isEdit.setChecked(user.getShareMode() == TSContract.ShareEntry.MODE_EDIT);
        viewHolder.isEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                final User temp = userList.get(position);
                final String param = "?uid="+temp.getUid()+"&tid="+tid;
                int shareStaus = temp.getShareStatus();
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("shareMode", b ? TSContract.ShareEntry.MODE_EDIT:TSContract.ShareEntry.MODE_VIEWONLY);
                    jsonObject.put("shareStatus", shareStaus);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Response resp = Controller.AppEvent(Controller.Action.UPDATE_SHARE, param, jsonObject);
                        try {
                            if (resp.isSuccessful())
                                Log.d(TAG, resp.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ((TimesheetActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshList(position, b);
                            }
                        });
                    }
                });
                thread.start();
            }
        });

            viewHolder.revokeBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final User temp = userList.get(position);
                    final String param = "?uid="+temp.getUid()+"&tid="+tid;
                    int shareMode = temp.getShareMode();
                    final JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("shareMode", shareMode);
                        jsonObject.put("shareStatus", TSContract.ShareEntry.STATUS_REVOKED);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Response resp = Controller.AppEvent(Controller.Action.UPDATE_SHARE, param, jsonObject);
                            try {
                                if (resp.isSuccessful())
                                    Log.d(TAG, resp.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ((TimesheetActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    revokeList(position);
                                }
                            });
                        }
                    });
                    thread.start();
                }
            });

        return convertView;
    }

    private void refreshList(int position, boolean b) {
        userList.get(position).setShareMode(b ? TSContract.ShareEntry.MODE_EDIT:TSContract.ShareEntry.MODE_VIEWONLY);
        notifyDataSetChanged();
    }

    private void revokeList(int position) {
        userList.remove(position);
        notifyDataSetChanged();
    }
}
