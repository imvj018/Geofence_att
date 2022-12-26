package com.example.att;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class viewTimesheet extends AppCompatActivity {
    ImageView profile, home, notify, logout;

    TextView buttondate;
    String timesheeturl = "https://testapi.innovasivtech.com/emp_attendance/timesheet/read.php";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    String w_date, newdate, employeeid;
    private List<TSlistItem> TSListItems;
    final LoadingDialog loadingDialog = new LoadingDialog(viewTimesheet.this);
    CardView seldate, showall;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_timesheet);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        employeeid = sharedPreferences.getString("empid", "");
        profile = findViewById(R.id.profilebutton);
        home = findViewById(R.id.homebutton);
        notify = findViewById(R.id.notifybutton);
        logout = findViewById(R.id.logoutbutton);
        showall = findViewById(R.id.showallbutton);
        buttondate = findViewById(R.id.showdate);

        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TSListItems.clear();
                showtimesheet();
                buttondate.setText("Select Date");
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(viewTimesheet.this, profile.class);
                startActivity(intent);
            }
        });
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(viewTimesheet.this, notification.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(viewTimesheet.this, Dashboard.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(viewTimesheet.this);
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


        recyclerView = (RecyclerView) findViewById(R.id.timesheetlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TSListItems = new ArrayList<>();
        showtimesheet();
        seldate = findViewById(R.id.showdatebutton);
        seldate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        viewTimesheet.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String tsdate, tsmonth;
                                tsdate = String.valueOf(dayOfMonth);
                                tsmonth = String.valueOf(monthOfYear + 1);

                                if (dayOfMonth < 10) {
                                    tsdate = "0" + tsdate;
                                }
                                if (monthOfYear < 10) {
                                    tsmonth = "0" + tsmonth;
                                }
                                w_date = tsdate + "-" + tsmonth + "-" + year;
                                newdate = year + "-" + tsmonth + "-" + tsdate;
                                loadingDialog.startLoadingDialog();
                                buttondate.setText(w_date);

                                fetchtimesheet();


                            }
                        },
                        year, month, day);
                datePickerDialog.show();

            }
        });

    }

    private void showtimesheet() {
        loadingDialog.startLoadingDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                timesheeturl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("body");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                TSlistItem item = new TSlistItem(
                                        object.getString("emp_id"),
                                        object.getString("project_name"),
                                        object.getString("project_code"),
                                        object.getString("task_name"),
                                        object.getString("description"),
                                        object.getString("hours"),
                                        object.getString("today_date"),
                                        object.getString("work_status"),
                                        object.getString("rejected_reason")
                                );

                                if ((!object.getString("work_status").equals("")) && object.getString("emp_id").equals(employeeid)) {
                                    TSListItems.add(item);

                                }
                                loadingDialog.dismissDialog();
                            }
                            adapter = new TSadapter(TSListItems, getApplicationContext());
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void fetchtimesheet() {

        TSListItems.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                timesheeturl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("body");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                TSlistItem item = new TSlistItem(
                                        object.getString("emp_id"),
                                        object.getString("project_name"),
                                        object.getString("project_code"),
                                        object.getString("task_name"),
                                        object.getString("description"),
                                        object.getString("hours"),
                                        object.getString("today_date"),
                                        object.getString("work_status"),
                                        object.getString("rejected_reason")
                                );

                                if ( (!object.getString("work_status").equals("")) && (object.getString("today_date").equals(newdate)) && object.getString("emp_id").equals(employeeid)) {
                                    TSListItems.add(item);


                                }
                                loadingDialog.dismissDialog();
                            }
                            adapter = new TSadapter(TSListItems, getApplicationContext());
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(viewTimesheet.this, timesheet.class);
        startActivity(intent);

    }
}