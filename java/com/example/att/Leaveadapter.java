package com.example.att;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Leaveadapter extends RecyclerView.Adapter<Leaveadapter.ViewHolder> {
    private List<LeavelistItem> LeavelistItem;
    private Context context;

    public Leaveadapter(List<LeavelistItem> LeavelistItem, Context applicationContext) {
        this.LeavelistItem = LeavelistItem;
        this.context = applicationContext;
    }

    @NonNull
    @Override
    public Leaveadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leavecard, parent, false);
        return new Leaveadapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Leaveadapter.ViewHolder holder, int position) {
        String id, username, emp_id, reason, leave_days, start_date, end_date, leave_apply_time, status, rejected_reason;
        id = LeavelistItem.get(position).getid();
        username = LeavelistItem.get(position).getusername();
        emp_id = LeavelistItem.get(position).getemp_id();
        reason = LeavelistItem.get(position).getreason();
        leave_days = LeavelistItem.get(position).getleave_days();
        start_date = LeavelistItem.get(position).getstart_date();
        end_date = LeavelistItem.get(position).getend_date();
        leave_apply_time = LeavelistItem.get(position).getleave_apply_time();
        status = LeavelistItem.get(position).getstatus();
        rejected_reason = LeavelistItem.get(position).getrejected_reason();


        holder.date.setText("From : " + start_date + "   To : " + end_date);
        holder.days.setText(leave_days);
        holder.appliedon.setText("Applied on   " + leave_apply_time);
        holder.reason.setText("Reason : " + reason);

        if (status.equals("rejected")){
            holder.status.setText("Rejected");
            holder.status.setTextColor(Color.parseColor("#FF3628"));
        }
        if (status.equals("approved")){
            holder.status.setText("Approved");
            holder.status.setTextColor(Color.parseColor("#01A963"));
        }
        if (status.equals("request")){
            holder.status.setText("Request");
            holder.status.setTextColor(Color.parseColor("#4691FB"));
        }
        if (!rejected_reason.equals("")) {
            holder.rej.setText("Rejected reason : " + rejected_reason);
        }


    }

    @Override
    public int getItemCount() {

        return LeavelistItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, days, status, reason, rej, appliedon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.datee);
            days = itemView.findViewById(R.id.days);
            status = itemView.findViewById(R.id.status);
            reason = itemView.findViewById(R.id.reason);
            rej = itemView.findViewById(R.id.rejreason);
            appliedon = itemView.findViewById(R.id.appliedon);


        }
    }
}
