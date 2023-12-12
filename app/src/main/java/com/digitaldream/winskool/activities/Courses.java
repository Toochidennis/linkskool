package com.digitaldream.winskool.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.adapters.CoursesAdapter;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.models.StudentTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.dialog.CustomDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Courses extends AppCompatActivity implements CoursesAdapter.OnCourseClickListener {
    private DatabaseHelper databaseHelper;
    private Dao<CourseTable,Long> courseDao;
    private List<CourseTable> courses;
    CoursesAdapter adapter;
    private boolean selectAll=false;
    private Menu menu;
    private FloatingActionButton sendBtn;
    private String classId;
    private String students;
    private TextView countText;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        Intent i = getIntent();
        String level = i.getStringExtra("level");
        classId = i.getStringExtra("class");
        databaseHelper = new DatabaseHelper(this);
        try {
            courseDao = DaoManager.createDao(databaseHelper.getConnectionSource(),CourseTable.class);
            courses = courseDao.queryForAll();
            Random rnd = new Random();
            for (CourseTable ct: courses){
                int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                ct.setColor(currentColor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        sendBtn = findViewById(R.id.send);
        countText = findViewById(R.id.count);
        RecyclerView recyclerView = findViewById(R.id.courses);
        adapter = new CoursesAdapter(this,courses,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);

        String from = i.getStringExtra("from");
        TextView title = findViewById(R.id.debt_fee_title);
        TextView desc = findViewById(R.id.desc);
        TextView yearTxt = findViewById(R.id.year);
        int prevYear = Integer.parseInt(RegYearList.year)-1;
        String termText = "";
        if(RegYearList.term.equals("1")){
            termText="First Term";
        }else if(RegYearList.term.equals("2")){
            termText = "Second Term";
        }else if(RegYearList.term.equals("3")){
            termText = "Third Term";
        }
        yearTxt.setText(prevYear+"/"+RegYearList.year+" "+termText);


        if(from!=null && from.equals("single")){
            StudentTable s = (StudentTable) i.getSerializableExtra("student");
            String firstname = s.getStudentFirstname();
            String surname = s.getStudentSurname();
            String middleName = s.getStudentMiddlename();
        //    desc.setText(AdminResultDashboardActivity.class_name);
            String st=s.getStudentId();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("student_id",st);
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                students = jsonArray.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                firstname = firstname.substring(0,1).toUpperCase()+""+firstname.substring(1).toLowerCase();
                surname = surname.substring(0,1).toUpperCase()+""+surname.substring(1).toLowerCase();
                middleName = middleName.substring(0,1).toUpperCase()+""+middleName.substring(1).toLowerCase();
            }catch (StringIndexOutOfBoundsException e){
                e.printStackTrace();
            }
            String name = surname+" "+firstname+" "+middleName;
            title.setText(name);
            getStudentRegistration(st,RegYearList.year,classId,RegYearList.term,RegYearList.db);

        }else{
            students = i.getStringExtra("student");
//            title.setText(AdminResultDashboardActivity.class_name);
            desc.setText("Bulk Registration");

        }
        FloatingActionButton sendBtn = findViewById(R.id.send);
        sendBtn.setOnClickListener(v -> {
            JSONArray jsonArray = new JSONArray();
            for (CourseTable ct : courses){
                if(ct.isSelected()){
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("course_id",ct.getCourseId());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            registerCourse(RegYearList.classId,RegYearList.year,RegYearList.term,jsonArray.toString(),RegYearList.db);
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_all,menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.select_all:
                if(selectAll){
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check_box2));
                    selection(false);
                    selectAll=false;


                }else {
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check_box3));
                    selection(true);
                    selectAll=true;

                }
                countText.setText(""+getSelectedCount());


                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onCourseClick(int position) {
        CourseTable st = courses.get(position);
        if(st.isSelected()){
            st.setSelected(false);
        }else {
            st.setSelected(true);
        }
        countText.setText(""+getSelectedCount());
        adapter.notifyDataSetChanged();
        invalidateOptionsMenu();
        selectAll=false;
        showOrHideFAB();
    }



    @Override
    public void onCourseLongClick(int position) {

    }

    private void selection(boolean value){
        boolean show = false;
        for ( CourseTable st : courses){
            st.setSelected(value);
            if(st.isSelected()){
                show=true;
            }else {
                show=false;
            }
        }
        adapter.notifyDataSetChanged();
        if(show){
            sendBtn.setVisibility(View.VISIBLE);
        }else {
            sendBtn.setVisibility(View.GONE);
        }
    }

    private void showOrHideFAB(){
        boolean show = false;

        for (CourseTable st : courses){
            if(st.isSelected()){
                show=true;
                break;
            }
        }
        if(show){
            sendBtn.setVisibility(View.VISIBLE);
        }else {
            sendBtn.setVisibility(View.GONE);
        }
    }

    private void getCourses(String classId,String year,String term, String db){
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = Login.urlBase+"/getCurrentRegistration.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("class",classId);
                param.put("year",year);
                param.put("term",term);
                param.put("_db",db);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void registerCourse(String classId,String year,String term,String courses, String db){
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = Login.urlBase+"/classRegistration.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                dialog.dismiss();
                if(response.trim().equals("1")){
                    Toast.makeText(Courses.this,"Operation was successful",Toast.LENGTH_SHORT).show();
                    finish();
                }else if(response.equals("0")) {
                    Toast.makeText(Courses.this,"Operation failed",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("class",classId);
                param.put("year",year);
                param.put("term",term);
                param.put("course",courses);
                param.put("_db",db);
                if(students!=null){
                    param.put("student_id",students);
                }
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private int getSelectedCount(){
        int a =0;
        for(CourseTable ct : courses){
            if(ct.isSelected()){
                a++;
            }
        }
        return a;
    }

    private void getStudentRegistration(String id,String year,String classId,String term,String db){
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = Login.urlBase+"/getStudentRegistration.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int b=0;b<jsonArray.length();b++){
                        JSONObject jsonObject = jsonArray.getJSONObject(b);
                        String courseName =  jsonObject.getString("course_name");
                        String courseId = jsonObject.getString("course");
                        getCourseList(courses, courseId);
                    }
                    adapter.notifyDataSetChanged();
                    countText.setText(""+getSelectedCount());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("id",id);
                param.put("class",classId);
                param.put("year",year);
                param.put("term",term);
                param.put("_db",db);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private List<CourseTable> getCourseList(List<CourseTable> list,String value){
        for (CourseTable s : list){
            if(value.equals(s.getCourseId())){
                s.setSelected(true);
            }
        }
        return list;
    }

}
