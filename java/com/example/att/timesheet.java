package com.example.att;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class timesheet extends AppCompatActivity {
    ImageView profile, home, notify, logout;
    CardView addts, subts, viewts;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesheet);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        profile = findViewById(R.id.profilebutton);
        home = findViewById(R.id.homebutton);
        notify = findViewById(R.id.notifybutton);
        logout = findViewById(R.id.logoutbutton);
        addts = findViewById(R.id.addtscard);
        viewts = findViewById(R.id.viewtscard);
        subts = findViewById(R.id.subtscard);
        sessionManager = new SessionManager(getApplicationContext());
        addts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(timesheet.this, addTimesheet.class);
                startActivity(intent);
            }
        });
        subts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(timesheet.this, SubmitTimesheet.class);
                startActivity(intent);
            }
        });

        viewts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(timesheet.this, viewTimesheet.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(timesheet.this, profile.class);
                startActivity(intent);
            }
        });
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(timesheet.this, notification.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(timesheet.this, Dashboard.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(timesheet.this);
                builder.setMessage("Do you want to Logout?");
                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessionManager.setlogin(false);
                        sessionManager.setUsername("");
                        startActivity(new Intent(getApplicationContext(),
                                Login.class));
                        finish();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(timesheet.this, Dashboard.class);
        startActivity(intent);

    }
}