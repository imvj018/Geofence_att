package com.example.att;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class leave extends AppCompatActivity {
    ImageView profile, home, notify, logout;
    TextView leavecount;
    String Leavecount, employeeid, leaveurl = "https://testapi.innovasivtech.com/emp_attendance/leave_api/read.php";
    CardView applyleave, displeave;
    float noofleaves;
    SessionManager sessionManager;
    final LoadingDialog loadingDialog = new LoadingDialog(leave.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        profile = findViewById(R.id.profilebutton);
        home = findViewById(R.id.homebutton);
        notify = findViewById(R.id.notifybutton);
        logout = findViewById(R.id.logoutbutton);
        leavecount = findViewById(R.id.count);
        applyleave = findViewById(R.id.applyleave);
        displeave = findViewById(R.id.displeave);
        loadingDialog.startLoadingDialog();
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        employeeid = sharedPreferences.getString("empid", "");
        sessionManager = new SessionManager(getApplicationContext());
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(leave.this, profile.class);
                startActivity(intent);
            }
        });
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(leave.this, notification.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(leave.this, Dashboard.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(leave.this);
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
        getdays(leaveurl);

        applyleave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(leave.this, applyleave.class);
                startActivity(intent);
            }
        });
        displeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(leave.this, ViewLeaves.class);
                startActivity(intent);
            }
        });

    }

    private void getdays(String leaveurl) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, leaveurl, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("body");
                noofleaves = 0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String id = jsonObject1.getString("id");
                    String empid = jsonObject1.getString("emp_id");
                    String noofdays = jsonObject1.getString("leave_days");
                    String status = jsonObject1.getString("status");
                    if ((empid.equals(employeeid)) && (status.equals("approved"))) {
                        noofleaves = noofleaves + Float.parseFloat(noofdays);
                    }

                }
                float remleave = 12 - noofleaves;
                Leavecount = Float.toString(remleave);
                leavecount.setText(Leavecount);
                if (Float.parseFloat(Leavecount) < 0) {
                    leavecount.setTextColor(Color.parseColor("#FF291A"));
                }
                loadingDialog.dismissDialog();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(leave.this, Dashboard.class);
        startActivity(intent);

    }
}