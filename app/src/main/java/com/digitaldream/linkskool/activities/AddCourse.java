package com.digitaldream.linkskool.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AddCourse extends AppCompatActivity {
    private EditText level,courseName,courseCode;
    private DatabaseHelper databaseHelper;
    private Dao<LevelTable,Long> levelDao;
    private List<LevelTable> levelList;
    private Button saveBtn;
    private Dao<CourseTable,Long> courseDao;
    private List<CourseTable> courseList;
    private String course_name,course_code,courseId;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        courseName = findViewById(R.id.addcourse_course_name);
        courseCode = findViewById(R.id.addcourse_course_code);
        saveBtn = findViewById(R.id.add_course);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Course");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        databaseHelper = new DatabaseHelper(this);
        try {
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(),LevelTable.class);
            courseDao = DaoManager.createDao(databaseHelper.getConnectionSource(),CourseTable.class);
            QueryBuilder<LevelTable,Long> queryBuilder = levelDao.queryBuilder();
            queryBuilder.where().eq("levelId", CourseSettings.levelId);
            levelList = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });
        if(getIntent().getStringExtra("from").equals("edit")) {
            Intent i = getIntent();
            course_name = i.getStringExtra("course_name").toUpperCase();
            course_code = i.getStringExtra("course_code").toUpperCase();
            courseId = i.getStringExtra("course_id");
            courseName.setText(course_name);
            courseCode.setText(course_code);
            getSupportActionBar().setTitle("Edit Course");

        }
    }

    private void validateInput(){
        if(courseName.getText().toString().equals("") || courseCode.getText().toString().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("All fields are required");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }else{
            if(getIntent().getStringExtra("from").equals("edit")){
                editCourseApiCall();
            }else if(getIntent().getStringExtra("from").equals("add")) {
                addCourseApiCall();
            }
        }
    }

    private void addCourseApiCall(){

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        final String db = sharedPreferences.getString("db","");
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = Login.urlBase+"/addCourse.php?course_name="+courseName.getText().toString()+"&course_code="+courseCode.getText().toString()+"&_db="+db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if(status.equals("success")){
                        JSONObject recordsObject = object.getJSONObject("records");
                        String courseId = recordsObject.getString("id");
                        String courseName = recordsObject.getString("course_name");
                        String courseCode = recordsObject.getString("course_code");
                        QueryBuilder<CourseTable, Long> queryBuilder = courseDao.queryBuilder();
                        queryBuilder.where().eq("courseId", courseId);
                        courseList = queryBuilder.query();
                        if (courseList.isEmpty()) {
                            CourseTable ct = new CourseTable();
                            ct.setCourseId(courseId);
                            ct.setCourseName(courseName);
                            ct.setCourseCode(courseCode);
                            courseDao.create(ct);
                        }
                        dialog1.dismiss();
                        onBackPressed();
                        Toast.makeText(AddCourse.this,"Course saved successfully",Toast.LENGTH_SHORT).show();



                    }else if(status.equals("failed")){
                        dialog1.dismiss();
                        Toast.makeText(AddCourse.this,"Course name already exists",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void editCourseApiCall(){

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        final String db = sharedPreferences.getString("db","");
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = Login.urlBase+"/addCourse.php?id="+courseId+"&course_name="+courseName.getText().toString()+"&course_code="+courseCode.getText().toString()+"&_db="+db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if(status.equals("success")){
                        JSONObject recordsObject = object.getJSONObject("records");
                        String courseId = recordsObject.getString("id");
                        String courseName = recordsObject.getString("course_name");
                        String courseCode = recordsObject.getString("course_code");
                        UpdateBuilder<CourseTable, Long> updateBuilder = courseDao.updateBuilder();
                        updateBuilder.updateColumnValue("courseName",courseName);
                        updateBuilder.updateColumnValue("courseCode",courseCode);
                        updateBuilder.where().eq("courseId",courseId);
                        updateBuilder.update();
                        dialog1.dismiss();
                        onBackPressed();
                        Toast.makeText(AddCourse.this,"Operation was successful",Toast.LENGTH_SHORT).show();



                    }else if(status.equals("failed")){
                        dialog1.dismiss();
                        Toast.makeText(AddCourse.this,"Course name already exists",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }
}
