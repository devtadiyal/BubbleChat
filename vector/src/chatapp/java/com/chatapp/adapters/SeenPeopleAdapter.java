package com.chatapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.SeenPeopleModel;

import java.util.ArrayList;

import im.vector.R;
import im.vector.view.VectorCircularImageView;

public class SeenPeopleAdapter extends RecyclerView.Adapter<SeenPeopleAdapter.SeenPeopleHolder> {

    ArrayList<SeenPeopleModel> list = new ArrayList<>();

    @NonNull
    @Override
    public SeenPeopleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seen_people_row, parent, false);
        return new SeenPeopleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeenPeopleHolder holder, int position) {
        SeenPeopleModel s = list.get(position);
        holder.time.setText(s.getTime());
        holder.name.setText(s.getNo());
        holder.icon.setImageResource(R.drawable.default_contact_avatar);
    }

    public SeenPeopleAdapter(ArrayList<SeenPeopleModel> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SeenPeopleHolder extends RecyclerView.ViewHolder {

        VectorCircularImageView icon;
        TextView name, time;

        public SeenPeopleHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.picon);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
        }
    }
}
