package com.weijie.timesheetapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.models.Timesheet;

import java.util.List;

/**
 * Created by weiji_000 on 3/5/2017.
 */

public class TSAdapter extends ArrayAdapter<Timesheet> {

    private List<Timesheet> tslist;
    private boolean[] isChecked;
    boolean showCheckbox;

    public TSAdapter(Context context, List<Timesheet> list, boolean showCheckbox) {
        super(context, R.layout.single_row_ts, list);
        tslist = list;
        this.showCheckbox = showCheckbox;
        isChecked = new boolean[tslist.size()];
    }

    private class ViewHolder {
        TextView name;
        CheckBox cb;
        TextView author;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Timesheet ts = tslist.get(position);
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_row_ts, parent, false);
            vh = new ViewHolder();
            vh.name = (TextView) convertView.findViewById(R.id.ts_name);
            vh.cb = (CheckBox) convertView.findViewById(R.id.ts_checkbox);
            vh.author = (TextView) convertView.findViewById(R.id.ts_author);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.name.setText(ts.getName());
        vh.author.setText(String.valueOf(ts.getAuthorID()));
        vh.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChecked[position] = b;
            }
        });
        vh.cb.setChecked(isChecked[position]);
        if(showCheckbox) {
            vh.cb.setVisibility(View.VISIBLE);
        } else {
            vh.cb.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
