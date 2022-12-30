package com.chatapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chatapp.activity.CallDetails;

import java.util.List;

import im.vector.R;

public class CallDetailsAdapter extends ArrayAdapter<CallDetails> {

    Context context;
    List<CallDetails> Items;


    public CallDetailsAdapter(Context context, List<CallDetails> objects) {
        super(context, R.layout.call_details_row_item, objects);
        this.context = context;
        this.Items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        CallDetailsAdapter.ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.call_details_row_item, parent, false);

            holder = new ViewHolder();
            holder.date = (TextView) row.findViewById(R.id.date);
            holder.destination = (TextView) row.findViewById(R.id.destination);
            holder.duration = (TextView) row.findViewById(R.id.duration);
            holder.cost = (TextView) row.findViewById(R.id.cost);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        CallDetails Item = this.Items.get(position);

        holder.date.setText(Item.getDate());
        holder.destination.setText(Item.getDestination());
        holder.duration.setText(Item.getDuration());
        holder.cost.setText(Item.getCost());
        return row;

    }


    private class ViewHolder {
        TextView date;
        TextView destination;
        TextView duration;
        TextView cost;
    }

    @Override
    public int getCount() {
        return this.Items.size();
    }

    @Override
    public CallDetails getItem(int position) {
        return this.Items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
