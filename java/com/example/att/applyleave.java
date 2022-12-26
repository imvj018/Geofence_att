package com.example.att;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class applyleave extends AppCompatActivity {
    TextInputLayout startdate, enddate, noofdate, reason;
    TextInputEditText sdate, edate;
    CardView apply, cancel;
    String fdate, tdate, Lreason, noofdays, fdater, tdater, apply_time;
    String employeeid, username;
    private RadioGroup radioGroup;
    RadioButton radioButton;
    ImageView profile, home, notify, logout;
    SimpleDateFormat cdate, ctime;
    final LoadingDialog loadingDialog = new LoadingDialog(applyleave.this);
    SessionManager sessionManager;
    String leadid, leadurl = "https://testapi.innovasivtech.com/emp_attendance/lead_api/read.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyleave);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        startdate = findViewById(R.id.f1);
        sdate = findViewById(R.id.f11);
        edate = findViewById(R.id.f21);
        enddate = findViewById(R.id.f2);
        noofdate = findViewById(R.id.f3);
        reason = findViewById(R.id.f4);
        apply = findViewById(R.id.crcard);
        cancel = findViewById(R.id.cancard);
        profile = findViewById(R.id.profilebutton);
        home = findViewById(R.id.homebutton);
        notify = findViewById(R.id.notifybutton);
        logout = findViewById(R.id.logoutbutton);
        sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        employeeid = sharedPreferences.getString("empid", "");
        username = sharedPreferences.getString("name", "");
        cdate = new SimpleDateFormat("dd-MM-yyyy");
        ctime = new SimpleDateFormat("HH:mm:ss");
        apply_time = (ctime.format(new Date())) + "   " + (cdate.format(new Date()));
        loadTLdata(leadurl);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(applyleave.this, profile.class);
                startActivity(intent);
            }
        });
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(applyleave.this, notification.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(applyleave.this, Dashboard.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(applyleave.this);
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

        radioGroup = findViewById(R.id.groupradio);
        radioGroup.clearCheck();
        sdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        applyleave.this,
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

                                fdate = tsdate + "-" + tsmonth + "-" + year;
                                fdater = year + "-" + tsmonth + "-" + tsdate;
                                Objects.requireNonNull(startdate.getEditText()).setText(fdate);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
        edate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        applyleave.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @RequiresApi(api = Build.VERSION_CODES.O)
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

                                tdate = tsdate + "-" + tsmonth + "-" + year;
                                tdater = year + "-" + tsmonth + "-" + tsdate;
                                if (fdate.equals(tdate)) {
                                    Objects.requireNonNull(enddate.getEditText()).setText(tdate);
                                    Objects.requireNonNull(noofdate.getEditText()).setText("1");
                                    radioGroup.setVisibility(View.VISIBLE);

                                    radioGroup.setOnCheckedChangeListener(
                                            new RadioGroup
                                                    .OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(RadioGroup group,
                                                                             int checkedId) {

                                                    radioButton = group.findViewById(checkedId);
                                                    int selectedId = radioGroup.getCheckedRadioButtonId();
                                                    if (selectedId == -1) {
                                                        Objects.requireNonNull(noofdate.getEditText()).setText("1");
                                                    } else {

                                                        radioButton = radioGroup.findViewById(selectedId);
                                                        String days = String.valueOf(radioButton.getText());
                                                        if (days.equals("Full day")) {
                                                            Objects.requireNonNull(noofdate.getEditText()).setText("1");
                                                        }
                                                        if (days.equals("Half day")) {
                                                            Objects.requireNonNull(noofdate.getEditText()).setText("0.5");
                                                        }

                                                    }
                                                }
                                            });


                                } else {
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

                                    try {
                                        Date testdate1 = simpleDateFormat.parse(fdate);
                                        Date testdate2 = simpleDateFormat.parse(tdate);
                                        assert testdate1 != null;
                                        assert testdate2 != null;
                                        boolean isBefore = testdate1.before(testdate2);
                                        if (!isBefore) {
                                            Toast.makeText(applyleave.this, "Enter valid date", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Objects.requireNonNull(enddate.getEditText()).setText(tdate);
                                            radioGroup.setVisibility(View.INVISIBLE);
                                            long diffInMillies = Math.abs(testdate2.getTime() - testdate1.getTime());
                                            long diff = (TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)) + 1;
                                            Objects.requireNonNull(noofdate.getEditText()).setText(Long.toString(diff));

                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }


                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lreason = Objects.requireNonNull(reason.getEditText()).getText().toString();
                noofdays = Objects.requireNonNull(noofdate.getEditText()).getText().toString();
                if ((!Lreason.equals("")) && (!noofdays.equals("")) && (!fdater.equals("")) && (!tdater.equals(""))) {
                    new postleave().execute("https://testapi.innovasivtech.com/emp_attendance/leave_api/create.php");
                    Toast.makeText(getApplicationContext(), "Leave applied!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(applyleave.this, ViewLeaves.class);
                    startActivity(intent);

                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {


                Objects.requireNonNull(startdate.getEditText()).setText("");
                Objects.requireNonNull(enddate.getEditText()).setText("");
                Objects.requireNonNull(noofdate.getEditText()).setText("");
                Objects.requireNonNull(reason.getEditText()).setText("");
            }
        });
    }
    private void loadTLdata(String leadurl) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, leadurl, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("body");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject hit = jsonArray.getJSONObject(i);

                    String usernamel = hit.getString("username");
                    String lead_empid = hit.getString("lead_empid");


                    if (username.equals(usernamel)) {
                        leadid = lead_empid;

                    }


                }


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
        Intent intent = new Intent(applyleave.this, leave.class);
        startActivity(intent);

    }

    @SuppressLint("StaticFieldLeak")
    private class postleave extends AsyncTask<String, Void, String> {
        String server_response;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            java.net.URL url;
            HttpURLConnection urlConnection;
            try {


                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();


                JSONObject jsonObjectFinal = new JSONObject();

                jsonObjectFinal.put("username", username);
                jsonObjectFinal.put("emp_id", employeeid);
                jsonObjectFinal.put("reason", Lreason);
                jsonObjectFinal.put("leave_days", noofdays);
                jsonObjectFinal.put("start_date", fdater);
                jsonObjectFinal.put("end_date", tdater);
                jsonObjectFinal.put("leave_apply_time", apply_time);
                jsonObjectFinal.put("status", "request");
                jsonObjectFinal.put("rejected_reason", "");
                jsonObjectFinal.put("lead_name", leadid);


                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(jsonObjectFinal.toString());

                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", server_response);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Response", "" + server_response);
        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}