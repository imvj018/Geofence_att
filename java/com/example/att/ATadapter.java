package com.example.att;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ATadapter extends RecyclerView.Adapter<ATadapter.ViewHolder> {
    private List<ATlistItem> ATListItem;
    private Context context;

    public ATadapter(List<ATlistItem> atListItem, Context applicationContext) {
        this.ATListItem = atListItem;
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public ATadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attcard, parent, false);
        return new ATadapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ATadapter.ViewHolder holder, int position) {
        String empid, username, date, hours, location, note, notex;
        empid = ATListItem.get(position).getEmpid();
        username = ATListItem.get(position).getUsername();
        date = ATListItem.get(position).getDate();
        hours = ATListItem.get(position).getHours();
        location = ATListItem.get(position).getLocation();
        note = ATListItem.get(position).getNote();
        notex = ATListItem.get(position).getNotex();

        holder.date.setText("Date  " + date + "  |  Hours  " + hours);
        holder.location.setText("Work location : " + location );
    }

    @Override
    public int getItemCount() {

        return ATListItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, location;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            location = itemView.findViewById(R.id.location);


        }
    }
}
