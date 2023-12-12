package com.digitaldream.winskool.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.adapters.CoursesAdapter;
import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.dialog.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StudentRegistrationDetails extends AppCompatActivity implements CoursesAdapter.OnCourseClickListener {
    private CoursesAdapter adapter;
    private List<CourseTable> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration_details);
        Intent i = getIntent();
        String id = i.getStringExtra("id");
        String name = i.getStringExtra("name");
        RecyclerView recyclerView = findViewById(R.id.courses);
        courses = new ArrayList<>();
        adapter = new CoursesAdapter(this,courses,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        TextView title = findViewById(R.id.debt_fee_title);
        TextView description = findViewById(R.id.desc);
        TextView desc1=findViewById(R.id.desc1);
        int prevYear = Integer.parseInt(CourseRegistrationDetails.year)-1;
        String term = CourseRegistrationDetails.term;
        String termText="";
        switch (term) {
            case "1":
                termText = "First Term";
                break;
            case "2":
                termText = "Second Term";
                break;
            case "3":
                termText = "Third Term";
                break;
        }
        desc1.setText(prevYear+"/"+CourseRegistrationDetails.year+" "+termText);
//        description.setText(AdminResultDashboardActivity.class_name);
        title.setText(name);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        getStudentRegistration(id,CourseRegistrationDetails.year,RegYearList.classId,CourseRegistrationDetails.term,RegYearList.db);
    }


    private void getStudentRegistration(String id,String year,String classId,String term,String db){
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = Login.urlBase+"/getStudentRegistration.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Random rnd = new Random();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int a=0;a<jsonArray.length();a++){
                        JSONObject obj = jsonArray.getJSONObject(a);
                        String courseName = obj.getString("course_name");
                        String courseId = obj.getString("course");
                        CourseTable ct = new CourseTable();
                        ct.setCourseId(courseId);
                        ct.setCourseName(courseName);
                        ct.setShowText(true);
                        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        ct.setColor(currentColor);
                        courses.add(ct);
                    }
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
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

    @Override
    public void onCourseClick(int position) {

    }

    @Override
    public void onCourseLongClick(int position) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
}
