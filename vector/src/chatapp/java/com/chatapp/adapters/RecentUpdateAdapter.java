package com.chatapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chatapp.CR;
import com.chatapp.activity.SelfStatusActivity;
import com.chatapp.activity.SelfStatusActivity2;
import com.chatapp.activity.StatusActivity2;
import com.chatapp.share.RecentModel;
import com.chatapp.status_module.StatusActivity;

import org.matrix.androidsdk.MXSession;

import java.util.ArrayList;

import im.vector.Matrix;
import im.vector.R;
import im.vector.util.VectorUtils;
import im.vector.view.VectorCircularImageView;

public class RecentUpdateAdapter extends RecyclerView.Adapter<RecentUpdateAdapter.RecentHolder> {

    private ArrayList<RecentModel> list;
    private Context context;
    private IShareAdapterClickListner listner;
    private MXSession mSession ;
    public IShareAdapterClickListner getListner() {
        return listner;
    }

    public void setListner(IShareAdapterClickListner listner) {
        this.listner = listner;
    }

    public RecentUpdateAdapter(ArrayList<RecentModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_holder, parent, false);
        return new RecentHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecentHolder holder, int position) {
        RecentModel model = list.get(position);
        if (position == 0) {
            holder.container.setVisibility(View.VISIBLE);
            holder.recentUpdate.setVisibility(View.VISIBLE);
            holder.userTime.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);
            if (model.getImageList().size() > 0) {
                holder.userTime.setText("");
//                holder.userName.setText(model.getUserName());
                holder.userName.setText("Your status");
                Glide.with(holder.userIcon.getContext()).load(model.getImageList().get(model.getImageList().size()-1))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.default_contact_avatar)
                        .into(holder.userIcon);
            }else{
                mSession = Matrix.getInstance(context).getDefaultSession();
                VectorUtils.loadUserAvatar(context, mSession, holder.userIcon, mSession.getMyUser());
                holder.userTime.setText("Tap to status update");
                holder.userName.setText("My Status");
            }
        } else {
            holder.divider.setVisibility(View.GONE);
            holder.userName.setText(model.getUserName());
            holder.userTime.setVisibility(View.GONE);
            holder.recentUpdate.setVisibility(View.GONE);
            if (model.getImageList().size() > 0)
                Glide.with(holder.userIcon.getContext()).load(model.getImageList().get(model.getImageList().size() - 1)).into(holder.userIcon);
            holder.container.setVisibility(View.VISIBLE);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    if (model.getImageList().size() > 0) {
                        CR.resources.clear();
                        for (int i = 0; i < model.getImageList().size(); i++) {
                            CR.resources.add(model.getImageList().get(i));
                        }
                        Intent i = new Intent(context, SelfStatusActivity2.class);
                        i.putExtra("model", model);
                        i.putExtra("self", false);
                        context.startActivity(i);
                    } else {
                        listner.onStatusClick();
                    }
                } else {
                    if (model.getImageList().size() > 0) {
                        CR.resources.clear();
                        for (int i = 0; i < model.getImageList().size(); i++) {
                            CR.resources.add(model.getImageList().get(i));
                        }
                        Intent i = new Intent(context, StatusActivity2.class);
                        i.putExtra("model", model);
                        i.putExtra("self", true);
                        context.startActivity(i);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecentHolder extends RecyclerView.ViewHolder {

        VectorCircularImageView userIcon;
        TextView userName, userTime;
        ConstraintLayout container;
        TextView recentUpdate;
        View divider;

        public RecentHolder(@NonNull View itemView) {
            super(itemView);
            userIcon = itemView.findViewById(R.id.user_status_icon);
            userName = itemView.findViewById(R.id.my_status);
            userTime = itemView.findViewById(R.id.time);
            container = itemView.findViewById(R.id.container);
            recentUpdate = itemView.findViewById(R.id.recentUpdate);
            divider = itemView.findViewById(R.id.devider_two);

        }
    }

    public interface IShareAdapterClickListner {
        void onStatusClick();
    }
}
