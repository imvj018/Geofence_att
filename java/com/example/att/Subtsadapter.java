package com.example.att;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class Subtsadapter extends RecyclerView.Adapter<Subtsadapter.ViewHolder> {

    private List<TSlistItem> TSListItems;
    private Context context;
    String pcode, pname, task, desc, date, hours, wsts, sts;
    public Subtsadapter(List<TSlistItem> TSListItems, Context context) {
        this.TSListItems = TSListItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subtscard, parent, false);
        return new Subtsadapter.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        pname = TSListItems.get(position).getprojname();
        pcode = TSListItems.get(position).getprojcode();
        task = TSListItems.get(position).getjobname();
        desc = TSListItems.get(position).getdescription();
        date = TSListItems.get(position).getdate();
        hours = TSListItems.get(position).gethours();
        wsts = TSListItems.get(position).getWstatus();
        sts = TSListItems.get(position).getStatus();
        holder.projname.setText("Project : " + pcode + " " + pname);
        holder.taskname.setText("Task name : " + task);
        holder.datetext.setText("Worked " + hours + "  hours on " + date);
        holder.description.setText("Description : " + desc);

        holder.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new subTS().execute("https://testapi.innovasivtech.com/emp_attendance/timesheet/submitTs.php");
                System.out.println(sts);
                holder.submit.setVisibility(View.INVISIBLE);
                holder.delete.setVisibility(View.INVISIBLE);
                holder.disptext.setText("Submitted");

            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditTimesheet.class);
                intent.putExtra("pname", pname);
                intent.putExtra("pcode", pcode);
                intent.putExtra("tname", task);
                intent.putExtra("desc", desc);
                intent.putExtra("hours", hours);
                intent.putExtra("date", date);
                intent.putExtra("id", sts);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(sts);
                holder.submit.setVisibility(View.INVISIBLE);
                holder.delete.setVisibility(View.INVISIBLE);
                holder.disptext.setText("Deleted");

            }
        });

//        if (wsts.equals("draft") && sts.equals("")) {
//            holder.layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
//
//        }


    }

    @Override
    public int getItemCount() {
        return TSListItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView projname, taskname, description, datetext;
        LinearLayout layout;
        CardView submit, edit, delete;
        TextView disptext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.linearlayouts);
            projname = itemView.findViewById(R.id.projnames);
            taskname = itemView.findViewById(R.id.jobtitles);
            description = itemView.findViewById(R.id.jobdescs);
            datetext = itemView.findViewById(R.id.dates);

            submit = itemView.findViewById(R.id.submitbutton);
            edit = itemView.findViewById(R.id.editbutton);
            delete = itemView.findViewById(R.id.deletebutton);

            disptext = itemView.findViewById(R.id.textdisplay);

        }
    }

    private class subTS extends AsyncTask<String, Void, String> {
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


                jsonObjectFinal.put("id", sts);

                jsonObjectFinal.put("status", "");
                jsonObjectFinal.put("work_status", "request");




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
}

