package com.example.att;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class addTimesheet extends AppCompatActivity {
    ImageView profile, home, notify, logout;
    TextInputLayout projectcode, jtitle, jdesc, whours, wdate;
    Spinner projectdd;
    CardView create, cancel;
    String employee_id, username, project_name, project_code, w_date, w_dateforDB;
    String pcode, jname, jdescription, work_hours, work_date, post_date, leadid;
    String projurl = "https://testapi.innovasivtech.com/emp_attendance/project_assign_api/read.php",
            leadurl = "https://testapi.innovasivtech.com/emp_attendance/lead_api/read.php";
    SessionManager sessionManager;
    ArrayList<String> projectlist;
    private RequestQueue mRequestQueue;
    TextInputEditText date;
    SimpleDateFormat cdate;
    final LoadingDialog loadingDialog = new LoadingDialog(addTimesheet.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timesheet);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        employee_id = sharedPreferences.getString("empid", "");
        username = sharedPreferences.getString("name", "");
        profile = findViewById(R.id.profilebutton);
        home = findViewById(R.id.homebutton);
        notify = findViewById(R.id.notifybutton);
        logout = findViewById(R.id.logoutbutton);

        projectdd = findViewById(R.id.projdd);
        projectcode = findViewById(R.id.f2);
        jtitle = findViewById(R.id.f3);
        jdesc = findViewById(R.id.f4);
        whours = findViewById(R.id.f6);
        wdate = findViewById(R.id.f7);
        date = findViewById(R.id.f71);

        create = findViewById(R.id.crcard);
        cancel = findViewById(R.id.cancard);

        projectlist = new ArrayList<>();

        loadTLdata(leadurl);
        projectlist.add("Select Project");
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addTimesheet.this, profile.class);
                startActivity(intent);
            }
        });
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addTimesheet.this, notification.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addTimesheet.this, Dashboard.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(addTimesheet.this);
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
        cdate = new SimpleDateFormat("yyyy-MM-dd");
        post_date = cdate.format(new Date());
        loadprojectlist(projurl);
        projectdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                project_name = projectdd.getItemAtPosition(projectdd.getSelectedItemPosition()).toString();
                fetchcode(projurl);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        addTimesheet.this,
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
                                w_dateforDB = year + "-" + tsmonth + "-" + tsdate;

                                Objects.requireNonNull(wdate.getEditText()).setText(w_date);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pcode = Objects.requireNonNull(projectcode.getEditText()).getText().toString();
                jname = Objects.requireNonNull(jtitle.getEditText()).getText().toString();
                jdescription = Objects.requireNonNull(jdesc.getEditText()).getText().toString();
                work_hours = Objects.requireNonNull(whours.getEditText()).getText().toString();
                work_date = Objects.requireNonNull(wdate.getEditText()).getText().toString();
                loadingDialog.startLoadingDialog();
                if (pcode.equals("") || jname.equals("") || jdescription.equals("") || work_hours.equals("") || work_date.equals("")) {
                    Toast.makeText(getApplicationContext(), "Fill all the data!", Toast.LENGTH_SHORT).show();
                } else {
                    new postTS().execute("https://testapi.innovasivtech.com/emp_attendance/timesheet/create.php");
                    Toast.makeText(getApplicationContext(), "Time sheet record created!", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }

                loadingDialog.dismissDialog();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
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
                        System.out.println(leadid);
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

    private void fetchcode(String projurl) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, projurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("body");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        String pname = jsonObject1.getString("project_name");
                        String prjctcode = jsonObject1.getString("project_code");
                        if (pname.equals(project_name)) {
                            project_code = prjctcode;
                            Objects.requireNonNull(projectcode.getEditText()).setText(project_code);
                            break;
                        } else {
                            Objects.requireNonNull(projectcode.getEditText()).setText("");
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void loadprojectlist(String projurl) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, projurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("body");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String username_assign = jsonObject1.getString("username_assign");
                        String pcode = jsonObject1.getString("project_code");
                        String pname = jsonObject1.getString("project_name");
                        if (username_assign.equals(username)) {
                            projectlist.add(pname);
                        }
                    }
                    projectdd.setAdapter(new ArrayAdapter<String>(addTimesheet.this, android.R.layout.simple_spinner_dropdown_item, projectlist));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private class postTS extends AsyncTask<String, Void, String> {
        String server_response;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            java.net.URL url;
            HttpURLConnection urlConnection = null;
            try {


                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                pcode = Objects.requireNonNull(projectcode.getEditText()).getText().toString();
                jname = Objects.requireNonNull(jtitle.getEditText()).getText().toString();
                jdescription = Objects.requireNonNull(jdesc.getEditText()).getText().toString();
                work_hours = Objects.requireNonNull(whours.getEditText()).getText().toString();
                work_date = Objects.requireNonNull(wdate.getEditText()).getText().toString();

                JSONObject jsonObjectFinal = new JSONObject();


                jsonObjectFinal.put("username", username);
                jsonObjectFinal.put("emp_id", employee_id);
                jsonObjectFinal.put("project_name", project_name);
                jsonObjectFinal.put("project_code", pcode);
                jsonObjectFinal.put("status", "draft");
                jsonObjectFinal.put("work_status", "");
                jsonObjectFinal.put("posting_date", post_date);
                jsonObjectFinal.put("task_name", jname);
                jsonObjectFinal.put("hours", work_hours);
                jsonObjectFinal.put("today_date", w_dateforDB);
                jsonObjectFinal.put("description", jdescription);
                jsonObjectFinal.put("image", "");
                jsonObjectFinal.put("rejected_reason", "");
                jsonObjectFinal.put("leadname", leadid);


                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(jsonObjectFinal.toString());

                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();

                assert urlConnection != null;
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream2(urlConnection.getInputStream());
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

    private String readStream2(InputStream in) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(addTimesheet.this, timesheet.class);
        startActivity(intent);

    }
}