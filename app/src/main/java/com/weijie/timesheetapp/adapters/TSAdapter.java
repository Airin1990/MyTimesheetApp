package com.weijie.timesheetapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.activities.TimesheetActivity;
import com.weijie.timesheetapp.models.Timesheet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiji_000 on 3/5/2017.
 */

public class TSAdapter extends ArrayAdapter<Object> {


    private static final int TYPE_TIMESHEET = 1;
    private static final int TYPE_STRING = 0;
    private static final String MY_TS_STRING ="My Timesheet";
    private static final String SHARE_TS_STRING = "Shared Timesheet";
    private static final String REVOKED_TS_STRING = "Revoked Timesheet";

    private List<Object> tslist;
    private boolean[] isChecked;
    boolean showCheckbox;
    Context context;

    public TSAdapter(Context context, List<Object> list, boolean showCheckbox) {
        super(context, R.layout.single_row_ts, list);
        this.context = context;
        tslist = list;
        this.showCheckbox = showCheckbox;
        isChecked = new boolean[tslist.size()];
    }

    //get selected tids to generate summary
    public List<Long> getSelectedTIDs() {
        List<Long> tids = new ArrayList<>();
        for (int i = 0; i < tslist.size(); i++) {
            if (isChecked[i]) {
                tids.add(((Timesheet)tslist.get(i)).getTID());
            }
        }
        return tids;
    }

    @Override
    public int getItemViewType(int position) {
        if (tslist.get(position) instanceof  Timesheet) {
            return TYPE_TIMESHEET;
        } else {
            return TYPE_STRING;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        int rowType = getItemViewType(position);
        if (convertView == null) {
            switch (rowType) {
                case TYPE_STRING:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.section_header, parent, false);
                    break;
                case TYPE_TIMESHEET:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_row_ts, parent, false);
                    break;
            }
        }

        switch (rowType) {
            case TYPE_STRING:
                String header = (String) tslist.get(position);
                TextView headertx = (TextView) convertView.findViewById(R.id.section_header);
                headertx.setText(header);
                switch (header) {
                    case MY_TS_STRING:
                        headertx.setBackgroundResource(R.color.SheetBG);
                        break;
                    case SHARE_TS_STRING:
                        headertx.setBackgroundResource(R.color.ShareBG);
                        break;
                    case REVOKED_TS_STRING:
                        headertx.setBackgroundResource(R.color.RevokeBG);
                        break;
                }
                break;
            case TYPE_TIMESHEET:
                Timesheet ts = (Timesheet) tslist.get(position);
                TextView name = (TextView) convertView.findViewById(R.id.ts_name);
                CheckBox cb = (CheckBox) convertView.findViewById(R.id.ts_checkbox);
                TextView author = (TextView) convertView.findViewById(R.id.ts_author);
                ImageView arrow = (ImageView) convertView.findViewById(R.id.ts_arrow);
                name.setText(ts.getName());
                author.setText(ts.getAuthorName());
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        isChecked[position] = b;
                    }
                });
                cb.setChecked(isChecked[position]);
                if(showCheckbox) {
                    cb.setVisibility(View.VISIBLE);
                    arrow.setVisibility(View.INVISIBLE);
                } else {
                    cb.setVisibility(View.INVISIBLE);
                    arrow.setVisibility(View.VISIBLE);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, TimesheetActivity.class);
                            Timesheet data = (Timesheet) tslist.get(position);
                            long tid = data.getTID();
                            int mode = data.getShareMode();
                            int status = data.getShareStatus();
                            intent.putExtra("TID", tid);
                            intent.putExtra("mode", mode);
                            intent.putExtra("status", status);
                            context.startActivity(intent);
                        }
                    });
                }
                break;
        }

        return convertView;
    }
}
