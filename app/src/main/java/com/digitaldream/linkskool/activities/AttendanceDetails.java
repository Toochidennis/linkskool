package com.digitaldream.linkskool.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.adapters.AttendanceDetailsAdapter;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AttendanceDetails extends AppCompatActivity {

    private AttendanceDetailsAdapter mAdapter;
    private List<StudentTable> mStudentTableList;
    private RecyclerView mRecyclerView;
    private RelativeLayout mLayout;
    private TextView detailsDate;

    private String classId;
    private String courseId;
    private String db;
    private String date;
    private String from;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_attendance);

        Toolbar toolbar = findViewById(R.id.bar_tool);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Attendance Details");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);


        Intent intent = getIntent();
        classId = intent.getStringExtra("classId");
        courseId = intent.getStringExtra("courseId");
        db = intent.getStringExtra("db");
        date = intent.getStringExtra("date");
        from = intent.getStringExtra("from");
        String className = intent.getStringExtra("class_name");
        String courseName = intent.getStringExtra("course_name");


        TextView name = findViewById(R.id.details_title);
        detailsDate = findViewById(R.id.details_date);
        String currentDate = CourseAttendance.dateConverter(date);
        detailsDate.setText(currentDate);

        if (from.equals("class")) {
            name.setText(className);
        } else {
            name.setText(courseName);
        }

        mStudentTableList = new ArrayList<>();

        mRecyclerView = findViewById(R.id.details_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mAdapter = new AttendanceDetailsAdapter(this, mStudentTableList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        mLayout = findViewById(R.id.details_empty_state);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;

        }
        return false;
    }

    public void getAttendance(String sClassId, String sCourseId, String sDate
            , String sDb, String sFrom) {

        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = Login.urlBase + "/getAttendance.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.i("Response", response);
                    dialog1.dismiss();
                    JSONArray jsonArray;
                    String json = "";
                    try {
                        jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            json = jsonObject.getString("register");
                        }
                        Log.i("json", json);
                        JSONArray jsonArray1 = new JSONArray(json);
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object = jsonArray1.getJSONObject(j);
                            String name = object.getString("name");

                            StudentTable studentTable = new StudentTable();
                            studentTable.setStudentFullName(name);
                            mStudentTableList.add(studentTable);
                        }

                        mAdapter.notifyDataSetChanged();

                        if (mStudentTableList.isEmpty()) {
                            mLayout.setVisibility(View.VISIBLE);
                        } else {
                            mLayout.setVisibility(View.GONE);
                        }

                    } catch (JSONException sE) {
                        sE.printStackTrace();
                    }

                }, error -> {
            dialog1.dismiss();
            Toast.makeText(this, "Something went wrong!",
                    Toast.LENGTH_SHORT).show();
            mLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            ImageView imageView = findViewById(R.id.image);
            TextView textView = findViewById(R.id.error_message);
            imageView.setImageResource(R.drawable.no_internet);
            textView.setText("Seems like you're not connected to the internet!");
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("class", sClassId);
                stringMap.put("date", sDate);

                if (sFrom.equals("class")) {
                    stringMap.put("course", sCourseId);
                } else if (sFrom.equals("course")) {
                    stringMap.put("course", sCourseId);
                }
                stringMap.put("_db", sDb);
                return stringMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAttendance(classId, courseId, date, db, from);
    }



}
