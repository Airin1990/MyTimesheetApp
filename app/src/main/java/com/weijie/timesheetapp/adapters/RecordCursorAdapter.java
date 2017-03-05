package com.weijie.timesheetapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.database.TSContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by weiji_000 on 3/4/2017.
 */

public class RecordCursorAdapter extends CursorAdapter {

    public RecordCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.single_row_record, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv1 = (TextView) view.findViewById(R.id.date_tv);
        TextView tv2 = (TextView) view.findViewById(R.id.start_row);
        TextView tv3 = (TextView) view.findViewById(R.id.end_row);
        TextView tv4 = (TextView) view.findViewById(R.id.break_row);
        TextView tv5 = (TextView) view.findViewById(R.id.work_row);

        SimpleDateFormat fromString = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat toString = new SimpleDateFormat("EEEE, MMM dd, yyyy");
        String date = cursor.getString(cursor.getColumnIndex(TSContract.RecordEntry.COLUMN_DATE));
        try {
            date = toString.format(fromString.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String start_t = cursor.getString(cursor.getColumnIndex(TSContract.RecordEntry.COLUMN_START_TIME));
        String end_t = cursor.getString(cursor.getColumnIndex(TSContract.RecordEntry.COLUMN_END_TIME));
        int break_t = cursor.getInt(cursor.getColumnIndex(TSContract.RecordEntry.COLUMN_BREAK));
        String b_t = "" + String.valueOf(break_t/60) +"hr "+String.valueOf(break_t%60)+" min";
        int work_t = cursor.getInt(cursor.getColumnIndex(TSContract.RecordEntry.COLUMN_WORK_TIME));
        String w_t = "" + String.valueOf(work_t/60) +"hr "+String.valueOf(work_t%60)+" min";

        tv1.setText(date);
        tv2.setText(start_t);
        tv3.setText(end_t);
        tv4.setText(b_t);
        tv5.setText(w_t);

    }
}
