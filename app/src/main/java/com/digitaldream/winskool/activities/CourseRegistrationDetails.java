package com.digitaldream.winskool.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.adapters.CourseRegAdapter;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.GeneralSettingModel;
import com.digitaldream.winskool.models.PrevYrModel;
import com.digitaldream.winskool.models.StudentTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.dialog.CustomDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CourseRegistrationDetails extends AppCompatActivity implements CourseRegAdapter.OnStudentSelectListener {
    private Dao<GeneralSettingModel,Long> schoolDao;
    private DatabaseHelper databaseHelper;
    private CourseRegAdapter adapter;
    private List<StudentTable> studentList;
    public static String year;
    public static String term;
    private LinearLayout emptyText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_registration_details);
        databaseHelper = new DatabaseHelper(this);
        List<GeneralSettingModel> list = null;
        try {
            schoolDao = DaoManager.createDao(databaseHelper.getConnectionSource(),GeneralSettingModel.class);
            list=schoolDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        year="";
        if(list!=null) {
            year = list.get(0).getSchoolYear();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        Intent i = getIntent();
        PrevYrModel pr = (PrevYrModel) i.getSerializableExtra("object");
        TextView className = findViewById(R.id.debt_fee_title);
        TextView desc = findViewById(R.id.desc);

//        className.setText(AdminResultDashboardActivity.class_name);
        String currentYear = pr.getYear();
        String name = pr.getName();
        term = pr.getTerm();
        int prevYear = 0;
        try {
            prevYear = Integer.parseInt(currentYear)-1;
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        desc.setText(prevYear+"/"+currentYear+" "+name);

        studentList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.students);
        adapter = new CourseRegAdapter(this,studentList,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        emptyText = findViewById(R.id.empty_text);

        getRegistration(RegYearList.classId,year,term,RegYearList.db);

    }

    private void getRegistration(String classId,String year,String term,String db){
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = Login.urlBase+"/getRegistrationDetails.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                dialog.dismiss();
                Random rnd = new Random();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int a=0;a<jsonArray.length();a++){
                        JSONObject jsonObject  = jsonArray.getJSONObject(a);
                        String name = jsonObject.getString("student_name");
                        String id = jsonObject.getString("student_id");
                        String count = jsonObject.getString("count");
                        StudentTable st = new StudentTable();
                        st.setStudentSurname(name);
                        st.setCourseCount(count);
                        st.setStudentId(id);
                        st.setShowText(true);
                        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        st.setColor(currentColor);

                        studentList.add(st);
                    }
                    adapter.notifyDataSetChanged();
                    if(studentList.isEmpty()){
                        emptyText.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                emptyText.setVisibility(View.VISIBLE);

                Toast.makeText(CourseRegistrationDetails.this,"Error "+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("year",year);
                param.put("class",classId);
                param.put("term",term);
                param.put("_db",db);
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onStudentSelect(int position) {
        StudentTable st = studentList.get(position);
        Intent intent = new Intent(this,StudentRegistrationDetails.class);
        intent.putExtra("id",st.getStudentId());
        intent.putExtra("name",st.getStudentSurname());
        startActivity(intent);
    }

    @Override
    public void onStudentLongClick(int position) {

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
