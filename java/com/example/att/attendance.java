package com.example.att;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class attendance extends AppCompatActivity {
    ImageView profile, home, notify, logout;
    CardView inbutton, outbutton;
    LottieAnimationView addlocbutton;
    TextView whours;
    String employeeid, username, workplace, newlocname, newlat, newlon, logoutID;
    String indate, intime, outtime, outdate;
    String time1, time2;
    String disphours, dispmins, dispsecs, dispacthours;
    String init_time, act_hours, overall_time, today;
    String locurl = "https://testapi.innovasivtech.com/emp_attendance/location_api/read.php",
            loginurl = "https://testapi.innovasivtech.com/emp_attendance/user_clockin_api/read.php",
            attread = "https://testapi.innovasivtech.com/emp_attendance/attendance_api/read.php",
            overall_att = "https://testapi.innovasivtech.com/emp_attendance/overall_work_hours_api/read.php";
    private ProgressBar progressBar;
    final LoadingDialog loadingDialog = new LoadingDialog(attendance.this);
    String loginsts = "", buttoncheck1 = "", checkOAhours = "";
    int hoursdiff, minsdiff, secsdiff;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ATlistItem> ATListItem;
    SimpleDateFormat cdate, ctime, timeFormat, simpleDateFormat;
    FusedLocationProviderClient mFusedLocationClient;
    double minlat, maxlat, minlon, maxlon;
    double latitude, longitude;
    int PERMISSION_ID = 44;
    SessionManager sessionManager;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        profile = findViewById(R.id.profilebutton);
        home = findViewById(R.id.homebutton);

        notify = findViewById(R.id.notifybutton);
        logout = findViewById(R.id.logoutbutton);
        inbutton = findViewById(R.id.incard);
        outbutton = findViewById(R.id.outcard);
        addlocbutton = findViewById(R.id.locanim);

        whours = findViewById(R.id.hours);
        progressBar = findViewById(R.id.progressBar);
        sessionManager = new SessionManager(getApplicationContext());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        List<locList> locationlist = new ArrayList<>();

        getlogintime(loginurl);
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        employeeid = sharedPreferences.getString("empid", "");
        username = sharedPreferences.getString("name", "");
        System.out.println("-------------" + username + "-----------------");
        cdate = new SimpleDateFormat("yyyy-MM-dd");
        ctime = new SimpleDateFormat("HH:mm:ss");
        today = cdate.format(new Date());

        recyclerView = (RecyclerView) findViewById(R.id.attlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ATListItem = new ArrayList<>();
        showattentries();

        inbutton.setOnClickListener(view -> {

            indate = cdate.format(new Date());
            intime = ctime.format(new Date());

            getInLocation();


        });
        outbutton.setOnClickListener(view -> {
            outdate = cdate.format(new Date());
            outtime = ctime.format(new Date());
            getOutLocation();
            ATListItem.clear();
            showattentries();

        });
        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("TEST TEST TEST TEST ");
            }
        });
        addlocbutton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(attendance.this);
            builder.setMessage("Do you want to Add this location?");
            builder.setTitle("Alert!");
            builder.setCancelable(true);
            final EditText input = new EditText(attendance.this);
            input.setHint("Enter a name for this location.");

            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Yes", (dialog, which) -> {
                newlocname = input.getText().toString();
                addnewlocation();
                loadingDialog.dismissDialog();
                Toast.makeText(getApplicationContext(), "Location '" + newlocname + "' added", Toast.LENGTH_LONG).show();
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        profile.setOnClickListener(view -> {
            Intent intent = new Intent(attendance.this, profile.class);
            startActivity(intent);
        });
        notify.setOnClickListener(view -> {
            Intent intent = new Intent(attendance.this, notification.class);
            startActivity(intent);
        });
        home.setOnClickListener(view -> {
            Intent intent = new Intent(attendance.this, Dashboard.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        logout.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(attendance.this);
            builder.setMessage("Do you want to Logout?");
            builder.setTitle("Alert !");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", (dialog, which) -> {
                sessionManager.setlogin(false);
                sessionManager.setUsername("");
                startActivity(new Intent(getApplicationContext(),
                        Login.class));
                finish();
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(attendance.this, Dashboard.class);
        startActivity(intent);

    }

    private void showattentries() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                overall_att,
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

    @SuppressLint("MissingPermission")
    private void addnewlocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                loadingDialog.startLoadingDialog();
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();

                        loadingDialog.dismissDialog();
                        Toast.makeText(getApplicationContext(), "No Location detected... Try again!", Toast.LENGTH_LONG).show();
                    } else {


                        newlat = String.valueOf(location.getLatitude());
                        newlon = String.valueOf(location.getLongitude());
//                        Geocoder geocoder;
//                        List<Address> addresses;
//                        geocoder = new Geocoder(context, Locale.getDefault());
//                        try {
//                            addresses = geocoder.getFromLocation(newlat, newlon, 1);
//                            if(addresses.size()>0)
//                            {
//                                String cityName = addresses.get(0).getAddressLine(0);
//                                String stateName = addresses.get(0).getAddressLine(1);
//                                //Toast.makeText(getApplicationContext(),stateName , 1).show();
//                                String countryName = addresses.get(0).getAddressLine(2);
//                            }
//                        }catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
                        new postnewloc().execute("https://testapi.innovasivtech.com/emp_attendance/location_api/create.php");
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on your location!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {

            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void getInLocation() {

        if (checkPermissions()) {

            if (isLocationEnabled()) {

                loadingDialog.startLoadingDialog();
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                        loadingDialog.dismissDialog();
                        Toast.makeText(getApplicationContext(), "No Location detected... Try again!", Toast.LENGTH_LONG).show();
                    } else {

                        minlat = location.getLatitude() - 0.0015;
                        maxlat = location.getLatitude() + 0.0015;
                        minlon = location.getLongitude() - 0.0015;
                        maxlon = location.getLongitude() + 0.0015;

                        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                                locurl,
                                s -> {

                                    try {
                                        JSONObject jsonObject = new JSONObject(s);
                                        JSONArray array = jsonObject.getJSONArray("body");

                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject object = array.getJSONObject(i);
                                            locList item = new locList(
                                                    object.getString("name"),
                                                    object.getString("lat"),
                                                    object.getString("lon")

                                            );
                                            latitude = Double.parseDouble(object.getString("lat"));
                                            longitude = Double.parseDouble(object.getString("lon"));
                                            workplace = object.getString("name");
                                            if ((minlat <= latitude) && (maxlat >= latitude) && (minlon <= longitude) && (maxlon >= longitude)) {
                                                new postatt().execute("https://testapi.innovasivtech.com/emp_attendance/user_clockin_api/update.php");
                                                inbutton.setVisibility(View.INVISIBLE);
                                                outbutton.setVisibility(View.VISIBLE);
                                                finish();
                                                overridePendingTransition(0, 0);
                                                startActivity(getIntent());
                                                overridePendingTransition(0, 0);
                                                loginsts = "OK";
                                                loadingDialog.dismissDialog();
                                                Toast.makeText(getApplicationContext(), "Logged in at " + workplace, Toast.LENGTH_LONG).show();
                                            }


                                        }
                                        if (!loginsts.equals("OK")) {
                                            loadingDialog.dismissDialog();
                                            Toast.makeText(getApplicationContext(), "Get into a working location to clock-in...! ", Toast.LENGTH_LONG).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                },
                                volleyError -> Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show());

                        RequestQueue requestQueue = Volley.newRequestQueue(attendance.this);
                        requestQueue.add(stringRequest);

                    }
                });
            } else {
                Toast.makeText(this, "Please turn on your location!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {

            requestPermissions();

        }
    }

    @SuppressLint("MissingPermission")
    private void getOutLocation() {

        if (checkPermissions()) {

            if (isLocationEnabled()) {
                loadingDialog.startLoadingDialog();
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                        loadingDialog.dismissDialog();
                        Toast.makeText(getApplicationContext(), "No Location detected... Try again!", Toast.LENGTH_LONG).show();

                    } else {

                        minlat = location.getLatitude() - 0.0015;
                        maxlat = location.getLatitude() + 0.0015;
                        minlon = location.getLongitude() - 0.0015;
                        maxlon = location.getLongitude() + 0.0015;

                        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                                locurl,
                                s -> {

                                    try {
                                        JSONObject jsonObject = new JSONObject(s);
                                        JSONArray array = jsonObject.getJSONArray("body");

                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject object = array.getJSONObject(i);
                                            locList item = new locList(
                                                    object.getString("name"),
                                                    object.getString("lat"),
                                                    object.getString("lon")

                                            );
                                            latitude = Double.parseDouble(object.getString("lat"));
                                            longitude = Double.parseDouble(object.getString("lon"));
                                            workplace = object.getString("name");
                                            if ((minlat <= latitude) && (maxlat >= latitude) && (minlon <= longitude) && (maxlon >= longitude)) {
                                                getatthours(overall_att);
                                                stoptimer();
                                                new closeatt().execute("https://testapi.innovasivtech.com/emp_attendance/user_clockin_api/update.php");
                                                new createlog().execute("https://testapi.innovasivtech.com/emp_attendance/attendance_api/create.php");
                                                outbutton.setVisibility(View.INVISIBLE);
                                                inbutton.setVisibility(View.VISIBLE);
                                                finish();
                                                overridePendingTransition(0, 0);
                                                startActivity(getIntent());
                                                overridePendingTransition(0, 0);
                                                loginsts = "OK";
                                                loadingDialog.dismissDialog();
                                                Toast.makeText(getApplicationContext(), "Logged out at " + workplace, Toast.LENGTH_LONG).show();
                                            }

                                        }
                                        if (!loginsts.equals("OK")) {
                                            loadingDialog.dismissDialog();
                                            Toast.makeText(getApplicationContext(), "Get into a working location to clock-out...! ", Toast.LENGTH_LONG).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                },
                                volleyError -> Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show());

                        RequestQueue requestQueue = Volley.newRequestQueue(attendance.this);
                        requestQueue.add(stringRequest);

                    }
                });
            } else {
                Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {

            requestPermissions();
        }
    }

    private void getatthours(String overall_att) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, overall_att, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("body");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String id = jsonObject1.getString("id");
                    String empid = jsonObject1.getString("emp_id");
                    String username = jsonObject1.getString("username");
                    String hours = jsonObject1.getString("hours");
                    String date = jsonObject1.getString("dates");


                    if ((empid.equals(employeeid)) && (date.equals(today))) {
                        System.out.println("------------------------------------------------");
                        logoutID = id;
                        checkOAhours = "OK";
                        new updatelogout().execute("https://testapi.innovasivtech.com/emp_attendance/overall_work_hours_api/update.php");

                    }

                }
                if (!checkOAhours.equals("OK")) {
                    new createlogout().execute("https://testapi.innovasivtech.com/emp_attendance/overall_work_hours_api/create.php");

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

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {


        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;


    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("getLastLocation()1");
            }
        }
    }

    //    @Override
    //    public void onResume() {
    //        super.onResume();
    //        if (checkPermissions()) {
    //            System.out.println("getLastLocation()");
    //        }
    //    }

    private void getlogintime(String loginurl) {
        loadingDialog.startLoadingDialog();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, loginurl, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("body");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String name = jsonObject1.getString("emp_id");
                    String ct = jsonObject1.getString("curr_time");
                    String cd = jsonObject1.getString("curr_date");


                    if ((name.equals(employeeid)) && (!ct.equals("")) && (cd.equals(today))) {

                        time1 = ct;
                        inbutton.setVisibility(View.INVISIBLE);
                        outbutton.setVisibility(View.VISIBLE);
                        intimecalc();
                        buttoncheck1 = "OK";

                    }

                }
                if (!buttoncheck1.equals("OK")) {
                    inbutton.setVisibility(View.VISIBLE);
                    outbutton.setVisibility(View.INVISIBLE);
                    loadingDialog.dismissDialog();
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

    private void stoptimer() {
        handler.removeCallbacks(runnable);
    }

    private void getactivehours(String attread) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        @SuppressLint("SimpleDateFormat") StringRequest stringRequest = new StringRequest(Request.Method.GET, attread, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("body");
                init_time = "00:00:00";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String id = jsonObject1.getString("id");
                    String name = jsonObject1.getString("emp_id");
                    String ct = jsonObject1.getString("user_current_date");
                    String cd = jsonObject1.getString("user_total_hours");

                    if (name.equals(employeeid) && ct.equals(today)) {

                        timeFormat = new SimpleDateFormat("HH:mm:ss");
                        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                        Date date1 = timeFormat.parse(init_time);
                        Date date2 = timeFormat.parse(cd);

                        assert date1 != null;
                        assert date2 != null;
                        long sum = date1.getTime() + date2.getTime();

                        init_time = timeFormat.format(new Date(sum));

                    }

                }
                act_hours = init_time;

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    @SuppressLint("SimpleDateFormat")
    private void intimecalc() {
        getactivehours(attread);
        handler.postDelayed(runnable = () -> {
            handler.postDelayed(runnable, delay);

            simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

            time2 = simpleDateFormat.format(new Date());
            Date date1, date2;
            try {
                date1 = simpleDateFormat.parse(time1);
                date2 = simpleDateFormat.parse(time2);
                assert date2 != null;
                assert date1 != null;
                long differenceInMilliSeconds
                        = Math.abs(date2.getTime() - date1.getTime());

                int pbseconds = ((int) differenceInMilliSeconds) / 1000;
                long differenceInHours
                        = (differenceInMilliSeconds / (60 * 60 * 1000))
                        % 24;

                hoursdiff = (int) differenceInHours;
                disphours = String.valueOf(hoursdiff);
                if (hoursdiff < 10) {
                    disphours = "0" + hoursdiff;
                }
                long differenceInMinutes
                        = (differenceInMilliSeconds / (60 * 1000)) % 60;

                minsdiff = (int) differenceInMinutes;
                dispmins = String.valueOf(minsdiff);
                if (minsdiff < 10) {
                    dispmins = "0" + minsdiff;
                }
                long differenceInSeconds
                        = (differenceInMilliSeconds / 1000) % 60;
                secsdiff = (int) differenceInSeconds;
                dispsecs = String.valueOf(secsdiff);
                if (secsdiff < 10) {
                    dispsecs = "0" + secsdiff;
                }
                dispacthours = disphours + ":" + dispmins + ":" + dispsecs;
                simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date exist_time = simpleDateFormat.parse(act_hours);
                Date new_time = simpleDateFormat.parse(dispacthours);
                assert exist_time != null;
                int extraseconds = (int) (exist_time.getTime() / 1000);
                assert new_time != null;
                long sum = exist_time.getTime() + new_time.getTime();

                overall_time = simpleDateFormat.format(new Date(sum));
                whours.setText(overall_time);
                progressBar.setProgress(pbseconds + extraseconds);
                progressBar.setMax(36000);
                loadingDialog.dismissDialog();

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }, delay);
        super.onResume();
    }

    @SuppressLint("StaticFieldLeak")
    private class updatelogout extends AsyncTask<String, Void, String> {
        String server_response;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            java.net.URL url;
            HttpURLConnection urlConnection;
            try {


                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();


                JSONObject jsonObjectFinal = new JSONObject();
                jsonObjectFinal.put("id", logoutID);
                jsonObjectFinal.put("emp_id", employeeid);
                jsonObjectFinal.put("username", username);
                jsonObjectFinal.put("dates", today);
                jsonObjectFinal.put("hours", overall_time);
                jsonObjectFinal.put("location", workplace);
                jsonObjectFinal.put("note2", "");
                jsonObjectFinal.put("note3", "");


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

    @SuppressLint("StaticFieldLeak")
    private class createlogout extends AsyncTask<String, Void, String> {
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
                jsonObjectFinal.put("emp_id", employeeid);
                jsonObjectFinal.put("username", username);
                jsonObjectFinal.put("dates", today);
                jsonObjectFinal.put("hours", overall_time);
                jsonObjectFinal.put("location", workplace);
                jsonObjectFinal.put("note2", "");
                jsonObjectFinal.put("note3", "");


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

    @SuppressLint("StaticFieldLeak")
    private class postnewloc extends AsyncTask<String, Void, String> {
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
                jsonObjectFinal.put("name", newlocname);
                jsonObjectFinal.put("lat", newlat);
                jsonObjectFinal.put("lon", newlon);
                jsonObjectFinal.put("status", "request");
                jsonObjectFinal.put("added_by", username);


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

    @SuppressLint("StaticFieldLeak")
    private class createlog extends AsyncTask<String, Void, String> {
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
                jsonObjectFinal.put("emp_id", employeeid);
                jsonObjectFinal.put("username", username);
                jsonObjectFinal.put("user_clockin_time", time1);
                jsonObjectFinal.put("user_clockout_time", outtime);
                jsonObjectFinal.put("user_current_date", outdate);
                jsonObjectFinal.put("user_total_hours", dispacthours);
                jsonObjectFinal.put("attendance_form", "Present");


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

    @SuppressLint("StaticFieldLeak")
    private class closeatt extends AsyncTask<String, Void, String> {
        String server_response;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            java.net.URL url;
            HttpURLConnection urlConnection;
            try {


                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();


                JSONObject jsonObjectFinal = new JSONObject();
                jsonObjectFinal.put("curr_date", "");
                jsonObjectFinal.put("curr_time", "");
                jsonObjectFinal.put("username", username);
                jsonObjectFinal.put("emp_id", employeeid);
                jsonObjectFinal.put("button_color", "#6610f2");
                jsonObjectFinal.put("button_id", "clock_in");
                jsonObjectFinal.put("button_name", "clock in");
                jsonObjectFinal.put("location", workplace);
                jsonObjectFinal.put("status", "0");


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

    @SuppressLint("StaticFieldLeak")
    private class postatt extends AsyncTask<String, Void, String> {
        String server_response;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            java.net.URL url;
            HttpURLConnection urlConnection;
            try {


                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.connect();


                JSONObject jsonObjectFinal = new JSONObject();

                jsonObjectFinal.put("curr_date", indate);
                jsonObjectFinal.put("curr_time", intime);
                jsonObjectFinal.put("username", username);
                jsonObjectFinal.put("emp_id", employeeid);
                jsonObjectFinal.put("button_color", "#ffc107");
                jsonObjectFinal.put("button_id", "clock_out");
                jsonObjectFinal.put("button_name", "clock out");
                jsonObjectFinal.put("location", workplace);
                jsonObjectFinal.put("status", "1");


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