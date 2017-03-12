package com.weijie.timesheetapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.activities.HomepageActivity;
import com.weijie.timesheetapp.database.TSContract;
import com.weijie.timesheetapp.models.Timesheet;
import com.weijie.timesheetapp.network.Controller;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

/**
 * Created by weiji_000 on 3/11/2017.
 */

public class PendingAdapter extends ArrayAdapter<Timesheet> {

    private final static String TAG = PendingAdapter.class.getSimpleName();
    private List<Timesheet> pendingList;
    private long uid;
    Context context;

    private static class ViewHolder {
        TextView tvName;
        TextView tvAuthor;
        Button btAccept;
        Button btDismiss;
    }


    public PendingAdapter(Context context, List<Timesheet> list, long uid) {
        super(context, R.layout.single_row_popup, list);
        this.pendingList = list;
        this.context = context;
        this.uid = uid;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Timesheet ts = pendingList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_row_popup, parent, false);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.pop_tname);
            viewHolder.tvAuthor = (TextView) convertView.findViewById(R.id.pop_uname);
            viewHolder.btAccept = (Button) convertView.findViewById(R.id.tick_sign);
            viewHolder.btDismiss = (Button) convertView.findViewById(R.id.cross_sign);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(ts.getName());
        viewHolder.tvAuthor.setText(ts.getAuthorName());
        viewHolder.btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timesheet temp = pendingList.get(position);
                long selectedTID = temp.getTID();
                int shareMode = temp.getShareMode();
                final String param = "?uid="+String.valueOf(uid)+"&tid="+String.valueOf(selectedTID);
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("shareMode", shareMode);
                    jsonObject.put("shareStatus", TSContract.ShareEntry.STATUS_ACCEPTED);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Response resp = Controller.AppEvent(Controller.Action.UPDATE_SHARE,param, jsonObject);
                        try {
                            if (resp.isSuccessful())
                            Log.d(TAG, resp.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ((HomepageActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshList(position);
                            }
                        });
                    }
                });
                thread.start();
            }
        });

        viewHolder.btDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timesheet temp = pendingList.get(position);
                long selectedTID = temp.getTID();
                final String param = "?uid="+String.valueOf(uid)+"&tid="+String.valueOf(selectedTID);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Response resp = Controller.AppEvent(Controller.Action.DELETE_SHARE,param, null);
                        try {
                            if (resp.isSuccessful())
                                Log.d(TAG, resp.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ((HomepageActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshList(position);
                            }
                        });
                    }
                });
                thread.start();
            }
        });

        return convertView;
    }

    public void refreshList(int position) {
        this.pendingList.remove(position);
        notifyDataSetChanged();
    }
}
