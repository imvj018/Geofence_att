package com.example.att;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TSadapter extends RecyclerView.Adapter<TSadapter.ViewHolder> {

    private List<TSlistItem> TSListItems;
    private Context context;

    public TSadapter(List<TSlistItem> TSListItems, Context context) {
        this.TSListItems = TSListItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timesheetcard, parent, false);
        return new TSadapter.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String pcode, pname, task, desc, hours, wsts, sts, date;
        pcode = TSListItems.get(position).getprojname();
        pname = TSListItems.get(position).getprojcode();
        task = TSListItems.get(position).getjobname();
        desc = TSListItems.get(position).getdescription();
        hours = TSListItems.get(position).gethours();
        wsts = TSListItems.get(position).getWstatus();
        sts = TSListItems.get(position).getStatus();
        date = TSListItems.get(position).getdate();
        holder.projname.setText("Project : " + pcode + " " + pname);
        holder.taskname.setText("Task name : " + task );
        holder.date.setText("Date : " + date + " (" + hours + "  hours)");
        holder.description.setText("Description : " + desc);

        if (wsts.equals("request") ) {
            holder.status.setText("Requested");
            holder.status.setTextColor(Color.parseColor("#45B6F8"));

        }
        if (wsts.equals("accept") ) {
            holder.status.setText("Approved");
            holder.status.setTextColor(Color.parseColor("#01A963"));

        }
        if (wsts.equals("reject") ) {
            holder.status.setText("Rejected");
            holder.status.setTextColor(Color.parseColor("#FF3628"));

        }
        if (!sts.equals("")) {
            holder.reason.setText("Rejected reason : " + sts);
        }

    }

    @Override
    public int getItemCount() {
        return TSListItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView projname, taskname, description, date, status, reason;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.linearlayout);
            projname = itemView.findViewById(R.id.projname);
            taskname = itemView.findViewById(R.id.jobtitle);
            description = itemView.findViewById(R.id.jobdesc);
            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
            reason = itemView.findViewById(R.id.reason);
        }
    }
}
