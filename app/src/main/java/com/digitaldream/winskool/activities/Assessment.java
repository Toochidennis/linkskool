package com.digitaldream.winskool.activities;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.CourseOutlineTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.dialog.CustomDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Assessment extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView assementWeek,assessmentCourse,totalQuestions,assessmentDuration;
    private Button startTest;
    private String db;
    private String id,courseId,levelId;
    private DatabaseHelper databaseHelper;
    private Dao<CourseOutlineTable,Long> courseOutlineDao;
    private List<CourseOutlineTable> list;
    private String  courseName;
    private String accessLevel;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Assessment");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        databaseHelper = new DatabaseHelper(this);
        try {
            courseOutlineDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseOutlineTable.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");
        accessLevel = sharedPreferences.getString("access_level","");
        userId = sharedPreferences.getString("user_id","");
        Intent intent = getIntent();
        String week =intent.getStringExtra("week");
        final String topic = intent.getStringExtra("topic");
        String description = intent.getStringExtra("objective");
        String url = intent.getStringExtra("url");
        String duration = intent.getStringExtra("duration");
        String endDate = intent.getStringExtra("end_date");
        String startDate = intent.getStringExtra("start_date");
        courseName = intent.getStringExtra("course");
        courseId = intent.getStringExtra("courseId");
        levelId = intent.getStringExtra("levelId");
        id = intent.getStringExtra("id");
        assementWeek = findViewById(R.id.week);
        assessmentCourse = findViewById(R.id.course);
        totalQuestions = findViewById(R.id.question_total);


        assementWeek.setText(week+" Assessment");
        try {
            String[] questions = url.split(",");
                assessmentCourse.setText(topic.toUpperCase() + " Q1 - " + questions.length);
            totalQuestions.setText(""+questions.length);

        }catch (Exception e){
            e.printStackTrace();
        }

        LinearLayout textDate = findViewById(R.id.text_available_desc);
        startTest = findViewById(R.id.start_test);


        if(accessLevel.equals("-1")) {
            if (startDate != null && endDate != null && !startDate.equals("0000-00-00 00:00:00") && !endDate.equals("0000-00-00 00:00:00")) {
                if (!startDate.isEmpty() && !endDate.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());
                    Date endDat = null;
                    try {
                        endDat = sdf.parse(endDate);
                        Date startDat = sdf.parse(startDate);
                        Date nowDate = sdf.parse(currentDateandTime);
                        if (!nowDate.before(startDat) && !nowDate.after(endDat)) {

                        } else {
                            startTest.setVisibility(View.GONE);
                            textDate.setVisibility(View.VISIBLE);
                            TextView textDate1 = findViewById(R.id.text);
                            TextView textView = findViewById(R.id.text1);
                            textDate1.setText("This Test is not available");
                            textView.setText("Assessment is available from " + startDate + " to " + endDate);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        TextView durationText = findViewById(R.id.assessment_duration);
        if(duration==null||duration.isEmpty()){
            durationText.setText("No time");

        }else {
            durationText.setText(duration);
        }

        startTest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                QueryBuilder<CourseOutlineTable,Long> queryBuilder = courseOutlineDao.queryBuilder();

                try {
                    queryBuilder.where().eq("id",id);
                    List<CourseOutlineTable> list = queryBuilder.query();
                    if(list.get(0).getJson()!=null ){
                        String json = list.get(0).getJson();
                        Log.i("response from json",json);
                        String category = list.get(0).getBody();
                        Intent intent1 = new Intent(Assessment.this, ExamActivity.class);
                        intent1.putExtra("course",courseName);
                        intent1.putExtra("Json",json);
                        intent1.putExtra("year","");
                        intent1.putExtra("from","assessment");
                        intent1.putExtra("exam_id",id);
                        intent1.putExtra("level",levelId);
                        intent1.putExtra("course_id",courseId);
                        startActivity(intent1);
                        Log.i("response",json);

                    }else {
                        startAssessmentApiCall();
                    }
                SharedPreferences sharedPreferences = getSharedPreferences(
                        "exam", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("examName",topic).apply();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }


    private void startAssessmentApiCall(){
        final CustomDialog dialog = new CustomDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String url = Login.urlBase+"/get_exam.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                if(!response.isEmpty()){
                    response = response.replace("'","''");
                    UpdateBuilder<CourseOutlineTable,Long> updateBuilder = courseOutlineDao.updateBuilder();
                    try {
                        updateBuilder.updateColumnValue("json",response).where().eq("id",id);
                        updateBuilder.update();


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                        Intent intent1 = new Intent(Assessment.this, ExamActivity.class);
                        intent1.putExtra("course",courseName);
                        intent1.putExtra("Json",response);
                        intent1.putExtra("year","");
                        intent1.putExtra("from","assessment");
                        intent1.putExtra("exam_id",id);
                        intent1.putExtra("level",levelId);
                        intent1.putExtra("course_id",courseId);
                        startActivity(intent1);
                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(Assessment.this,error.toString(),Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("_db",db);
                param.put("id",id);
                param.put("user_id",userId);
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



}
