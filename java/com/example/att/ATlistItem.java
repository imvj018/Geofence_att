package com.example.att;

public class ATlistItem {
    public final String empid, username, date, hours, location, note, notex;

    public ATlistItem(String empid, String username, String date, String hours, String location, String note, String notex) {
        this.empid = empid;
        this.username = username;
        this.date = date;
        this.hours = hours;
        this.location = location;
        this.note = note;
        this.notex = notex;
    }

    public String getEmpid() {
        return empid;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getHours() {
        return hours;
    }

    public String getLocation() {
        return location;
    }

    public String getNote() {
        return note;
    }

    public String getNotex() {
        return notex;
    }
}
