package com.example.att;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
import java.util.Calendar;
import java.util.Objects;

public class EditTimesheet extends AppCompatActivity {
    ImageView profile, home, notify, logout;
    TextInputLayout projdd, projectcode, jtitle, jdesc, whours, wdate;
    CardView update, cancel;
    SessionManager sessionManager;
    TextInputEditText date;
    SimpleDateFormat cdate;
    String id, pname, pcode, tname, desc, workhours, workdate;
    String employee_id, username,  w_date;
    String newtname, newdesc, newhours, newdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timesheet);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        id = getIntent().getStringExtra("id");
        pname = getIntent().getStringExtra("pname");
        pcode = getIntent().getStringExtra("pcode");
        tname = getIntent().getStringExtra("tname");
        desc = getIntent().getStringExtra("desc");
        workhours = getIntent().getStringExtra("hours");
        workdate = getIntent().getStringExtra("date");

        sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        employee_id = sharedPreferences.getString("empid", "");
        username = sharedPreferences.getString("name", "");
        profile = findViewById(R.id.profilebutton);
        home = findViewById(R.id.homebutton);
        notify = findViewById(R.id.notifybutton);
        logout = findViewById(R.id.logoutbutton);

        projdd = findViewById(R.id.f1);
        projectcode = findViewById(R.id.f2);
        jtitle = findViewById(R.id.f3);
        jdesc = findViewById(R.id.f4);
        whours = findViewById(R.id.f6);
        wdate = findViewById(R.id.f7);
        date = findViewById(R.id.f71);

        update = findViewById(R.id.crcard);
        cancel = findViewById(R.id.cancard);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditTimesheet.this, profile.class);
                startActivity(intent);
            }
        });
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditTimesheet.this, notification.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditTimesheet.this, Dashboard.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditTimesheet.this);
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

        projdd.getEditText().setText(pname);
        projectcode.getEditText().setText(pcode);
        jtitle.getEditText().setText(tname);
        jdesc.getEditText().setText(desc);
        whours.getEditText().setText(workhours);
        wdate.getEditText().setText(workdate);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditTimesheet.this,
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
                                Objects.requireNonNull(wdate.getEditText()).setText(w_date);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              newtname = jtitle.getEditText().getText().toString();
              newdesc = jdesc.getEditText().getText().toString();
              newhours = whours.getEditText().getText().toString();
              newdate = wdate.getEditText().getText().toString();

              if(newtname.equals("") || newdesc.equals("") || newhours.equals("") || newdate.equals("")){
                  Toast.makeText(getApplicationContext(), "Fill all the data!", Toast.LENGTH_SHORT).show();
              }
              else{
                  new editTS().execute("https://testapi.innovasivtech.com/emp_attendance/timesheet/appupdate.php");
                  Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                  Intent intent = new Intent(EditTimesheet.this, SubmitTimesheet.class);
                  startActivity(intent);
              }
              System.out.println(newtname + newdesc + newhours + newdate);
            }
        });
    }
    private class editTS extends AsyncTask<String, Void, String> {
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

                newtname = jtitle.getEditText().getText().toString();
                newdesc = jdesc.getEditText().getText().toString();
                newhours = whours.getEditText().getText().toString();
                newdate = wdate.getEditText().getText().toString();

                JSONObject jsonObjectFinal = new JSONObject();


                jsonObjectFinal.put("id", id);

                jsonObjectFinal.put("task_name", newtname);
                jsonObjectFinal.put("hours", newhours);
                jsonObjectFinal.put("today_date", newdate);
                jsonObjectFinal.put("description", newdesc);



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
        Intent intent = new Intent(EditTimesheet.this, SubmitTimesheet.class);
        startActivity(intent);

    }

}