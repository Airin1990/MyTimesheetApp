package com.weijie.timesheetapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.models.User;

import java.util.List;

/**
 * Created by weiji_000 on 3/12/2017.
 */

public class ShareUserAdapter extends ArrayAdapter<User> {

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
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = userList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.single_row_share_form, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.share_username);
            viewHolder.isEdit = (Switch) convertView.findViewById(R.id.mode_select);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(user.getFullName());
        viewHolder.isEdit.setChecked(user.getShareMode() == 1);
        return convertView;
    }
}
