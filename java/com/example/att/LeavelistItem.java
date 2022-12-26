package com.example.att;

public class LeavelistItem {
    public final String id, username, emp_id, reason, leave_days, start_date, end_date, leave_apply_time, status, rejected_reason;

    public LeavelistItem(String id, String username, String emp_id, String reason, String leave_days, String start_date, String end_date, String leave_apply_time, String status, String rejected_reason) {
        this.id = id;
        this.username = username;
        this.emp_id = emp_id;
        this.reason = reason;
        this.leave_days = leave_days;
        this.start_date = start_date;
        this.end_date = end_date;
        this.leave_apply_time = leave_apply_time;
        this.status = status;
        this.rejected_reason = rejected_reason;
    }

    public String getid() {return id;}
    public String getusername() {return username;}
    public String getemp_id() {return emp_id;}
    public String getreason() {return reason;}
    public String getleave_days() {return leave_days;}
    public String getstart_date() {return start_date;}
    public String getend_date() {return end_date;}
    public String getleave_apply_time() {return leave_apply_time;}
    public String getstatus() {return status;}
    public String getrejected_reason() {return rejected_reason;}
}
