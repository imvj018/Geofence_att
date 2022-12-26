package com.example.att;

import static androidx.recyclerview.widget.RecyclerView.*;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SubmitTimesheet extends AppCompatActivity {
    ImageView profile, home, notify, logout;
    String w_date, employeeid;
    String timesheeturl = "https://testapi.innovasivtech.com/emp_attendance/timesheet/draft.php";
    private RecyclerView recyclerView;
    private Adapter adapter;
    private List<TSlistItem> TSListItems;
    CardView submitall;
    String id, project_name, empid, project_code, task_name, description, hours, date, post_date, w_status, status, newid;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_timesheet);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        employeeid = sharedPreferences.getString("empid", "");
        profile = findViewById(R.id.profilebutton);
        home = findViewById(R.id.homebutton);
        notify = findViewById(R.id.notifybutton);
        logout = findViewById(R.id.logoutbutton);
        submitall = findViewById(R.id.submitallbutton);
        sessionManager = new SessionManager(getApplicationContext());
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubmitTimesheet.this, profile.class);
                startActivity(intent);
            }
        });
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubmitTimesheet.this, notification.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubmitTimesheet.this, Dashboard.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SubmitTimesheet.this);
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
        fetchtimesheet();

        submitall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suballts(timesheeturl);
            }
        });

    }

    private void suballts(String timesheeturl) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, timesheeturl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("body");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectx = jsonArray.getJSONObject(i);
                        id = jsonObjectx.getString("id");
                        project_name = jsonObjectx.getString("project_name");
                        empid = jsonObjectx.getString("empid");
                        project_code = jsonObjectx.getString("project_code");
                        task_name = jsonObjectx.getString("task_name");
                        description = jsonObjectx.getString("description");
                        hours = jsonObjectx.getString("hours");
                        date = jsonObjectx.getString("date");
                        post_date = jsonObjectx.getString("post_date");
                        w_status = jsonObjectx.getString("w_status");


                        if (empid.equals(employeeid)) {

                            newid = id;

                            new submitTS().execute("https://testapi.innovasivtech.com/attendance/timesheet/patch.php");

                        }

                    }

                    Intent intent = new Intent(SubmitTimesheet.this, Dashboard.class);
                    startActivity(intent);

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
//                                        empId, projName, projCode, jobName, descrIption, hoUrs, daTe, wstatus, status

                                        object.getString("emp_id"),
                                        object.getString("project_name"),
                                        object.getString("project_code"),
                                        object.getString("task_name"),
                                        object.getString("description"),
                                        object.getString("hours"),
                                        object.getString("today_date"),
                                        object.getString("work_status"),
                                        object.getString("id")
                                );
                                if (object.getString("emp_id").equals(employeeid)) {
                                    TSListItems.add(item);


                                }

                            }
                            adapter = new Subtsadapter(TSListItems, getApplicationContext());
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

    @SuppressLint("StaticFieldLeak")
    private class submitTS extends AsyncTask<String, Void, String> {
        String server_response;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            java.net.URL url;
            HttpURLConnection urlConnection = null;
            try {


                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();


                JSONObject jsonObjectFinal = new JSONObject();
                System.out.println("XXXXXXXXXX---------" + newid);
                jsonObjectFinal.put("id", newid);
                jsonObjectFinal.put("w_status", "request");


                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(jsonObjectFinal.toString());

                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();

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
        Intent intent = new Intent(SubmitTimesheet.this, timesheet.class);
        startActivity(intent);

    }
}