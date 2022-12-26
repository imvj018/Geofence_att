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

public class ViewLeaves extends AppCompatActivity {
    ImageView profile, home, notify, logout;
    String employeeid, atturl = "https://testapi.innovasivtech.com/emp_attendance/leave_api/read.php";
    private ProgressBar progressBar;
    final LoadingDialog loadingDialog = new LoadingDialog(ViewLeaves.this);
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<LeavelistItem> LeavelistItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_leaves);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        employeeid = sharedPreferences.getString("empid", "");
        profile = findViewById(R.id.profilebutton);
        home = findViewById(R.id.homebutton);
        notify = findViewById(R.id.notifybutton);
        logout = findViewById(R.id.logoutbutton);
        recyclerView = (RecyclerView) findViewById(R.id.leavelist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LeavelistItem = new ArrayList<>();

        profile.setOnClickListener(view -> {
            Intent intent = new Intent(ViewLeaves.this, profile.class);
            startActivity(intent);
        });
        notify.setOnClickListener(view -> {
            Intent intent = new Intent(ViewLeaves.this, notification.class);
            startActivity(intent);
        });
        home.setOnClickListener(view -> {
            Intent intent = new Intent(ViewLeaves.this, Dashboard.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        logout.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewLeaves.this);
            builder.setMessage("Do you want to Logout?");
            builder.setTitle("Alert !");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", (dialog, which) -> {
                Intent intent = new Intent(ViewLeaves.this, Login.class);
                startActivity(intent);
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        showleaveentries();
    }

    private void showleaveentries() {
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
                                LeavelistItem item = new LeavelistItem(
                                        object.getString("id"),
                                        object.getString("username"),
                                        object.getString("emp_id"),
                                        object.getString("reason"),
                                        object.getString("leave_days"),
                                        object.getString("start_date"),
                                        object.getString("end_date"),
                                        object.getString("leave_apply_time"),
                                        object.getString("status"),
                                        object.getString("rejected_reason")
                                );
                                if (object.getString("emp_id").equals(employeeid)) {
                                    LeavelistItem.add(item);
                                    loadingDialog.dismissDialog();
                                }

                            }
                            adapter = new Leaveadapter(LeavelistItem, getApplicationContext());
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
        Intent intent = new Intent(ViewLeaves.this, leave.class);
        startActivity(intent);

    }
}