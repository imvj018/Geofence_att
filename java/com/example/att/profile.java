package com.example.att;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class profile extends AppCompatActivity {
    ImageView home, notify, logout, profileimg, editprof;
    String url, employeeid;
    TextView profname, profempid, profmailid, profphone, profaddress, profteam, profrole, profdoj, profdob;
    String id, firstname, lastname, username, empid, mailid, phone, address, team, role, doj, dob, password, gender, imageurl;
    String userurl = "https://testapi.innovasivtech.com/emp_attendance/profile_api/read.php";
    final LoadingDialog loadingDialog = new LoadingDialog(profile.this);
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        profileimg = findViewById(R.id.testimg);
        editprof = findViewById(R.id.editprof);
        home = findViewById(R.id.homebutton);
        notify = findViewById(R.id.notifybutton);
        logout = findViewById(R.id.logoutbutton);
        profname = findViewById(R.id.profname);
        profempid = findViewById(R.id.profempid);
        profmailid = findViewById(R.id.profmail);
        profphone = findViewById(R.id.profphone);
        profaddress = findViewById(R.id.profaddress);
        profteam = findViewById(R.id.profteam);
        profrole = findViewById(R.id.profrole);
        profdoj = findViewById(R.id.profdoj);
        profdob = findViewById(R.id.profdob);
        sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        employeeid = sharedPreferences.getString("empid", "");

        getuserdata(userurl);
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile.this, notification.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile.this, Dashboard.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(profile.this);
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
        editprof.setOnClickListener(v -> {
            System.out.println("Edit Button clicked");
        });

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(profile.this, Dashboard.class);
        startActivity(intent);

    }
    private void getuserdata(String userurl) {
        loadingDialog.startLoadingDialog();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        @SuppressLint("SetTextI18n") StringRequest stringRequest = new StringRequest(Request.Method.GET, userurl, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("body");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    id = jsonObject1.getString("id");
                    username = jsonObject1.getString("username");
                    firstname = jsonObject1.getString("first_name");
                    lastname = jsonObject1.getString("last_name");
                    empid = jsonObject1.getString("emp_id");
                    password = jsonObject1.getString("user_password");
                    dob = jsonObject1.getString("dob");
                    doj = jsonObject1.getString("date_of_join");
                    phone = jsonObject1.getString("mobile_no");
                    mailid = jsonObject1.getString("official_email_id");
                    team = jsonObject1.getString("module");
                    role = jsonObject1.getString("job_role");
                    imageurl = jsonObject1.getString("user_image");
                    address = jsonObject1.getString("address");
                    gender = jsonObject1.getString("gender");

                    if (empid.equals(employeeid)) {
                        if (!imageurl.equals("")) {
                            url = "https://attendance.innovasivtech.com/" + imageurl;
                        }
                        if ((imageurl.equals("") || (imageurl.equals("null"))) && gender.equals("Male")) {
                            url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT2HoveSx5prCIWhGymQ6z5-G-F3rejFBbVuA&usqp=CAU";
                        }
                        if ((imageurl.equals("") || (imageurl.equals("null"))) && gender.equals("Female")) {
                            url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSi04ki-P6zB2a4JVo5Oq_HDO5Qbs8smuzkPg&usqp=CAU";
                        }

                        Glide.with(this).load(url).into(profileimg);
                        profname.setText(firstname + " " + lastname);
                        profempid.setText(empid);
                        profmailid.setText(mailid);
                        profphone.setText(phone);
                        profteam.setText(team);
                        profrole.setText(role);
                        profdoj.setText(doj);
                        profdob.setText(dob);
                        profaddress.setText(address);
                        loadingDialog.dismissDialog();
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
}