package com.digitaldream.linkskool.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.adapters.CourseRegAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.GeneralSettingModel;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.dialog.CustomDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class CourseRegistration extends AppCompatActivity implements CourseRegAdapter.OnStudentSelectListener {
    private List<StudentTable> studentList;
    private Dao<StudentTable,Long> studentDao;
    private Dao<GeneralSettingModel,Long> schoolDao;
    private CourseRegAdapter adapter;
    String studentLevelId;
    String studentClass;
    private boolean selectAll=false;
    private Menu menu;
    private FloatingActionButton sendBtn;
    private List<GeneralSettingModel> list;
    private static String db,year,term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_registration);
        DatabaseHelper databaseHelper = new DatabaseHelper(CourseRegistration.this);
        Intent i = getIntent();
        studentLevelId = i.getStringExtra("levelId");
        studentClass = i.getStringExtra("classId");


        try {
        studentDao = DaoManager.createDao(databaseHelper.getConnectionSource(),StudentTable.class);
        schoolDao = DaoManager.createDao(databaseHelper.getConnectionSource(),GeneralSettingModel.class);
         list = schoolDao.queryForAll();
        QueryBuilder<StudentTable,Long> queryBuilder = studentDao.queryBuilder();

        queryBuilder.where().eq("studentLevel", studentLevelId).and().eq("studentClass", studentClass);
        studentList = queryBuilder.query();
            Random rnd = new Random();
            for (StudentTable ct: studentList){
                int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                ct.setColor(currentColor);
            }
        }catch (SQLiteException | SQLException e){
            e.printStackTrace();
        }
        TextView className = findViewById(R.id.class_name);
//        className.setText(AdminResultDashboardActivity.class_name);
        TextView yearTxt = findViewById(R.id.year);


        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");
        term = sharedPreferences.getString("term","");
        year = list.get(0).getSchoolYear();
        int prevYear = Integer.parseInt(year)-1;
        String termText = "";
        if(term.equals("1")){
            termText="First Term";
        }else if(term.equals("2")){
            termText = "Second Term";
        }else if(term.equals("3")){
            termText = "Third Term";
        }
        yearTxt.setText(prevYear+"/"+year+" "+termText);

        RecyclerView recyclerView = findViewById(R.id.students);
        adapter = new CourseRegAdapter(this,studentList,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        sendBtn = findViewById(R.id.send);
        sendBtn.setOnClickListener(v -> {
            JSONArray jsonArray = new JSONArray();
            for(StudentTable st : studentList){
                if(st.isSelected()){
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("student_id",st.getStudentId());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            Intent intent = new Intent(CourseRegistration.this,Courses.class);
            intent.putExtra("level",studentLevelId);
            intent.putExtra("class",studentClass);
            intent.putExtra("student",jsonArray.toString());
            startActivity(intent);
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        RelativeLayout rl1 = findViewById(R.id.copy_reg);
        RelativeLayout rl2 = findViewById(R.id.course_registration);
        rl1.setOnClickListener(v -> copyCourseRegistration());
        rl2.setOnClickListener(v -> {
            Intent intent = new Intent(CourseRegistration.this,Courses.class);
            intent.putExtra("class",studentClass);
            intent.putExtra("level",studentLevelId);
            intent.putExtra("from","bulk");
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStudents(studentClass,year,term,db);

    }

    @Override
    public void onStudentSelect(int position) {
        StudentTable st = studentList.get(position);
        Intent intent = new Intent(this,Courses.class);
        intent.putExtra("level",studentLevelId);
        intent.putExtra("class",studentClass);
        intent.putExtra("from","single");
        intent.putExtra("student",st);
        startActivity(intent);

    }

    @Override
    public void onStudentLongClick(int position) {
        StudentTable st = studentList.get(position);
        if(st.isSelected()){
            st.setSelected(false);
        }else {
            st.setSelected(true);
        }
        adapter.notifyDataSetChanged();
        invalidateOptionsMenu();
        selectAll=false;
        showOrHideFAB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_all,menu);
        this.menu=menu;
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

                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    private void getStudents(String classId,String year,String term, String db){
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = Login.urlBase+"/getCurrentRegistration.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                dialog.dismiss();
                if(response!=null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int a = 0; a < jsonArray.length(); a++) {
                            JSONObject object = jsonArray.getJSONObject(a);
                            String id = object.getString("student_id");
                            String count = object.getString("count");
                            getStudentList(studentList, id, count);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                param.put("_db",db);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void selection(boolean value){
        boolean show = false;
        for ( StudentTable st : studentList){
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

        for (StudentTable st : studentList){
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

    private List<StudentTable> getStudentList(List<StudentTable> list,String value,String count){
        for (StudentTable s : list){
            if(value.equals(s.getStudentId())){
                s.setCourseCount(count);
            }
        }
        return list;
    }

    private void copyCourseRegistration(){
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = Login.urlBase+"/copyRegistration.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.i("response",response);
                if(response!=null && response.equals("1")){
                    Toast.makeText(CourseRegistration.this,"Copied Previous registration successfully ",Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    startActivity(intent);
                    finish();
                }else if(response!=null && response.equals("0")){
                    Toast.makeText(CourseRegistration.this,"Operation failed ",Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
                Toast.makeText(CourseRegistration.this,"Error "+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("class",RegYearList.classId);
                param.put("year",year);
                param.put("term",term);
                param.put("_db",db);
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
