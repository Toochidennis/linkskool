package com.digitaldream.winskool.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.adapters.CoursesSettingsAdapter;
import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class CourseSettings extends AppCompatActivity implements CoursesSettingsAdapter.OnEditBtnClickListener {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<CourseTable> courseList;
    private Dao<CourseTable,Long> courseDao;
    private Dao<LevelTable,Long> levelDao;
    private List<LevelTable> levelList;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton fab,fab2;
    private String course_title;
    private String course_code,course_id;
    CoursesSettingsAdapter adapter;
    private JSONObject jsonObject;
    private String db;
    private Spinner levelSelector;
    private List<String> spinnerLevelList;
    public static String levelId;
    private FrameLayout layout,emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_settings);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.add_grade);
        fab2 = findViewById(R.id.fab_course1);
        layout = findViewById(R.id.course_unempty_state);
        emptyLayout = findViewById(R.id.course_setting_empty_state);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Course Settings");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
         db = sharedPreferences.getString("db","");

        CoursesSettingsAdapter.selectedIds.clear();
        databaseHelper = new DatabaseHelper(this);
        try {
            courseDao = DaoManager.createDao(databaseHelper.getConnectionSource(),CourseTable.class);
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(),LevelTable.class);
            courseList = courseDao.queryForAll();
            levelList = levelDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        recyclerView = findViewById(R.id.course_settings_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        if(!courseList.isEmpty()) {
            emptyLayout.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            adapter = new CoursesSettingsAdapter(this, courseList, this);
            recyclerView.setAdapter(adapter);
        }else{
            layout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popUpDialog("Add Course");
                Intent intent = new Intent(CourseSettings.this, AddCourse.class);
                intent.putExtra("from","add");
                startActivity(intent);


            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //popUpDialog("Add course");
                Intent intent = new Intent(CourseSettings.this,AddCourse.class);
                intent.putExtra("from","add");
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.delete_menu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete ?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jsonObject = new JSONObject();

                        JSONArray jsonArray = new JSONArray();
                        for(int a = 0;a<CoursesSettingsAdapter.selectedIds.size();a++) {

                            JSONObject object = new JSONObject();

                            try {
                                object.put("id",CoursesSettingsAdapter.selectedIds.get(a));
                                jsonArray.put(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        try {
                            jsonObject.put("deleteCourse",jsonArray);
                            Log.i("response",jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        deleteCourseApiCall();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
        }
        return false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(CoursesSettingsAdapter.selectedIds.size()>0){
            getMenuInflater().inflate(R.menu.delete_selection_menu,menu);
        }else{
            getSupportActionBar().setTitle("Course Settings");
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if(CoursesSettingsAdapter.selectedIds.size()>0){
            CoursesSettingsAdapter.selectedIds.clear();
            //adapter = new CoursesSettingsAdapter(this,courseList,this);
            //recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            invalidateOptionsMenu();

        }else{
            super.onBackPressed();

        }
    }

    @Override
    public void onEditClick(final int position) {
        Intent intent = new Intent(CourseSettings.this,AddCourse.class);
        intent.putExtra("course_name",courseList.get(position).getCourseName());
        intent.putExtra("course_code",courseList.get(position).getCourseCode());
        intent.putExtra("course_id",courseList.get(position).getCourseId());
        intent.putExtra("from","edit");
        startActivity(intent);
           }



    private void deleteCourseApiCall(){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        String url = Login.urlBase+"/deleteCourse.php?deleteCourse="+jsonObject.toString()+"&_db="+db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog1.dismiss();
                Log.i("response",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status =  jsonObject.getString("status");
                    if(status.equals("success")){
                        for(int a=0; a<CoursesSettingsAdapter.selectedIds.size();a++) {
                            DeleteBuilder<CourseTable, Long> deleteBuilder = courseDao.deleteBuilder();
                            deleteBuilder.where().eq("courseId",CoursesSettingsAdapter.selectedIds.get(a) );
                            deleteBuilder.delete();
                        }
                       courseList =  courseDao.queryForAll();
                        adapter = new CoursesSettingsAdapter(CourseSettings.this,courseList,CourseSettings.this);

                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(CourseSettings.this,"Operation was successful",Toast.LENGTH_SHORT).show();
                    }else if(status.equals("failed")){
                        Toast.makeText(CourseSettings.this,"Operation failed",Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
                Toast.makeText(CourseSettings.this,"Error connecting to server",Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(CourseSettings.this);
        requestQueue.add(stringRequest);
    }



    @Override
    protected void onResume() {
        super.onResume();
        try {
            courseList = courseDao.queryForAll();
            if(!courseList.isEmpty()) {
                emptyLayout.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                adapter = new CoursesSettingsAdapter(this, courseList, this);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }else{
                layout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
