package com.example.att;

import androidx.appcompat.app.AppCompatActivity;

public class locList extends AppCompatActivity {
    private final String name;
    private final String lat;
    private final String lon;

    public locList(String name, String lat, String lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }
    public String name() {
        return name;
    }
    public String lat() {
        return lat;
    }

    public String lon() {
        return lon;
    }
}

