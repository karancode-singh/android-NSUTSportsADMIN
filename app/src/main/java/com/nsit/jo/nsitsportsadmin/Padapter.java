package com.nsit.jo.nsitsportsadmin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aditya on 13/7/17.
 */

public class Padapter extends ArrayAdapter<Entry> {

    public Padapter(Context context, List<Entry> data) {
        super(context, R.layout.p_custom_row, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater lf = LayoutInflater.from(getContext());
        View customView = lf.inflate(R.layout.p_custom_row, parent, false);
        Entry rData = getItem(position);
        TextView tv_t1 = (TextView) customView.findViewById(R.id.t1);
        TextView tv_t2 = (TextView) customView.findViewById(R.id.t2);
        TextView tv_date = (TextView) customView.findViewById(R.id.date);
        TextView tv_time = (TextView) customView.findViewById(R.id.time);
        TextView tv_tag = (TextView) customView.findViewById(R.id.pTag);
        if (chooseCriteria.selectedSport.equals("Cricket")) {
            tv_t1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            tv_t2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        }
        tv_t1.setText(rData.team1);
        tv_t2.setText(rData.team2);
        tv_date.setText(rData.date);
        tv_time.setText(rData.time);
        tv_tag.setText(rData.tag);
        if (rData.tag.equals(""))
            tv_tag.setVisibility(View.GONE);
        return customView;
    }
}
