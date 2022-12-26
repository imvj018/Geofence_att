package com.example.att;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.List;

public class Atthistory extends AppCompatActivity {
    ImageView profile, home, notify, logout;
    String employeeid, atturl = "https://testapi.innovasivtech.com/emp_attendance/overall_work_hours_api/read.php";
    private ProgressBar progressBar;
    final LoadingDialog loadingDialog = new LoadingDialog(Atthistory.this);
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ATlistItem> ATListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atthistory);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        employeeid = sharedPreferences.getString("empid", "");
        profile = findViewById(R.id.profilebutton);
        home = findViewById(R.id.homebutton);
        notify = findViewById(R.id.notifybutton);
        logout = findViewById(R.id.logoutbutton);
        recyclerView = (RecyclerView) findViewById(R.id.attlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ATListItem = new ArrayList<>();


        showattentries();
        profile.setOnClickListener(view -> {
            Intent intent = new Intent(Atthistory.this, profile.class);
            startActivity(intent);
        });
        notify.setOnClickListener(view -> {
            Intent intent = new Intent(Atthistory.this, notification.class);
            startActivity(intent);
        });
        home.setOnClickListener(view -> {
            Intent intent = new Intent(Atthistory.this, Dashboard.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        logout.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Atthistory.this);
            builder.setMessage("Do you want to Logout?");
            builder.setTitle("Alert !");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", (dialog, which) -> {
                Intent intent = new Intent(Atthistory.this, Login.class);
                startActivity(intent);
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    private void showattentries() {
        loadingDialog.startLoadingDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                atturl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("body");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                ATlistItem item = new ATlistItem(
                                        object.getString("emp_id"),
                                        object.getString("username"),
                                        object.getString("dates"),
                                        object.getString("hours"),
                                        object.getString("location"),
                                        object.getString("note2"),
                                        object.getString("note3")
                                );
                                if (object.getString("emp_id").equals(employeeid)) {
                                    ATListItem.add(item);

                                    loadingDialog.dismissDialog();
                                }

                            }
                            adapter = new ATadapter(ATListItem, getApplicationContext());
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
}