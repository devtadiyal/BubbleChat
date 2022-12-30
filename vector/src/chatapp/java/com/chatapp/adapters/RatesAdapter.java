package com.chatapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chatapp.activity.Rates;

import org.matrix.androidsdk.MXSession;

import java.util.List;

import im.vector.R;

public class RatesAdapter extends ArrayAdapter<Rates> {

    Context context;
    int layoutResourceId;
    List<Rates> Items;
    MXSession mSession;


    public RatesAdapter(Context context, List<Rates> objects) {
        super(context, R.layout.rates_row_item, objects);
        this.context = context;
        this.Items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.rates_row_item, parent, false);

            holder = new ViewHolder();
            holder.txtRates = (TextView) row.findViewById(R.id.rates);
            holder.txtDestination = (TextView) row.findViewById(R.id.destination);
            holder.txtPrefix = (TextView) row.findViewById(R.id.prefix);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        Rates Item = this.Items.get(position);

        holder.txtRates.setText(Item.getRate());
        holder.txtDestination.setText(Item.getDestination());
        holder.txtPrefix.setText(Item.getPrefix());
        return row;

    }


    private class ViewHolder {
        TextView txtRates;
        TextView txtPrefix;
        TextView txtDestination;
    }

    @Override
    public int getCount() {
        return this.Items.size();
    }

    @Override
    public Rates getItem(int position) {
        return this.Items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
