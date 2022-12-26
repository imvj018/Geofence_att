package com.example.att;

import androidx.appcompat.app.AppCompatActivity;

public class TSlistItem extends AppCompatActivity {
    private final String empid;
    private final String projname;
    private final String projcode;
    private final String jobname;
    private final String description;
    private final String hours;
    private final String date;
    private final String wstatus;
    private final String status;


    public TSlistItem(String empId, String projName, String projCode, String jobName, String descrIption, String hoUrs, String daTe, String wstatus, String status) {
        this.empid = empId;
        this.projname = projName;
        this.projcode = projCode;
        this.jobname = jobName;
        this.description = descrIption;
        this.hours = hoUrs;
        this.date = daTe;
        this.wstatus = wstatus;
        this.status = status;


    }

    public String getempid() {
        return empid;
    }

    public String getprojname() {
        return projname;
    }

    public String getprojcode() {
        return projcode;
    }

    public String getjobname() {
        return jobname;
    }

    public String getdescription() {
        return description;
    }

    public String gethours() {
        return hours;
    }

    public String getdate() {
        return date;
    }

    public String getWstatus() {
        return wstatus;
    }

    public String getStatus() {
        return status;
    }

}
