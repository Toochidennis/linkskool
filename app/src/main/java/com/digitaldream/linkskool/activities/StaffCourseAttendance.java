package com.digitaldream.linkskool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.adapters.StaffCourseAttendanceAdapter;
import com.digitaldream.linkskool.models.StudentTable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class StaffCourseAttendance extends AppCompatActivity implements StaffCourseAttendanceAdapter.OnStudentClickListener {

    private String mStudentClassId;
    private String courseId;
    private String year;
    private String term;
    private String db;
    private String staffId;
    private String responseId;
    private List<StudentTable> mStudentTableList;
    private StaffCourseAttendanceAdapter mStaffCourseAttendanceAdapter;
    private TextView errorMessage;
    private ImageView errorImage;
    private RelativeLayout mEmptyLayout;
    private FloatingActionButton mSendBtn;
    private ActionBar mActionBar;
    private Menu mMenu;
    private boolean isSelectAll = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_course_staff);

        TextView className1 = findViewById(R.id.class_title);
        TextView courseName1 = findViewById(R.id.course_name);
        RecyclerView recyclerView = findViewById(R.id.students_recycler);
        mSendBtn = findViewById(R.id.submit_btn);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        mEmptyLayout = findViewById(R.id.empty_state);
        errorMessage = findViewById(R.id.error_message);
        errorImage = findViewById(R.id.image);

        Intent intent = getIntent();
        mStudentClassId = intent.getStringExtra("classId");
        courseId = intent.getStringExtra("courseId");
        responseId = intent.getStringExtra("responseId");
        String className = intent.getStringExtra("class_name");
        String courseName = intent.getStringExtra("course_name");


        SharedPreferences sharedPreferences = getSharedPreferences(
                "loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");
        term = sharedPreferences.getString("term", "");
        staffId = sharedPreferences.getString("user_id", "");
        year = sharedPreferences.getString("school_year", "");

        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setTitle("Course Attendance");
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.arrow_left);

        className1.setText(className);
        courseName1.setText(courseName);

        mStudentTableList = new ArrayList<>();
        mStaffCourseAttendanceAdapter = new StaffCourseAttendanceAdapter(this,
                mStudentTableList,
                this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mStaffCourseAttendanceAdapter);

        mSendBtn.setOnClickListener(sView -> {
            try {
                JSONArray jsonArray = new JSONArray();
                for (StudentTable student : mStudentTableList) {
                    if (student.isSelected()) {
                        String name = student.getStudentFullName();
                        String id = student.getStudentId();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", id);
                        jsonObject.put("name", name);
                        jsonArray.put(jsonObject);
                        Log.i("attendance", jsonArray.toString());
                    }

                }

                takeClassAttendance(mStudentClassId, staffId, courseId,
                        jsonArray.toString(), year, term, db,
                        (String) mActionBar.getTitle());

            } catch (JSONException sE) {
                sE.printStackTrace();
            }

        });

        getCourseRegistration(mStudentClassId, term, year, courseId, db);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.check_all, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.check_all:
                if (isSelectAll) {
                    mMenu.getItem(0).setVisible(false);
                    selectAll(false);
                    isSelectAll = false;
                } else {
                    mMenu.getItem(0).setIcon(ContextCompat.getDrawable(this,
                            R.drawable.ic_check));
                    selectAll(true);
                    isSelectAll = true;
                }

                if (getSelectedCount() == 0) {
                    mActionBar.setTitle("Course Attendance");
                } else {
                    mActionBar.setTitle("" + getSelectedCount());
                }

                mStaffCourseAttendanceAdapter.notifyDataSetChanged();

                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onStudentClick(int position) {
        StudentTable studentTable = mStudentTableList.get(position);
        if (studentTable.isSelected()) {
            studentTable.setSelected(false);
        } else {
            mMenu.getItem(0).setVisible(false);
            studentTable.setSelected(true);
        }

        if (getSelectedCount() == 0) {
            mActionBar.setTitle("Course Attendance");
        } else {
            mActionBar.setTitle("" + getSelectedCount());
        }
        mStaffCourseAttendanceAdapter.notifyDataSetChanged();
        invalidateOptionsMenu();
        isSelectAll = false;
        showHideFab();

    }

    private int getSelectedCount() {
        int a = 0;
        for (StudentTable item : mStudentTableList) {
            if (item.isSelected()) {
                a++;
            }
        }
        return a;
    }

    public void showHideFab() {
        boolean show = false;
        for (StudentTable item : mStudentTableList) {
            if (item.isSelected()) {
                show = true;
                break;
            }
        }
        if (show) {
            mSendBtn.setVisibility(View.VISIBLE);
        } else {
            mSendBtn.setVisibility(View.GONE);
        }
    }

    private void selectAll(Boolean sBoolean) {
        boolean show = false;

        for (StudentTable student : mStudentTableList) {
            student.setSelected(sBoolean);
            show = student.isSelected();
        }
        if (show) {
            mSendBtn.setVisibility(View.VISIBLE);
        } else {
            mSendBtn.setVisibility(View.GONE);
        }
    }


    private void takeClassAttendance(String sClassId, String sStaffId,
                                     String sCourseId,
                                     String sStudents, String sYear,
                                     String sTerm, String sDb, String sCount) {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = getString(R.string.base_url)+ "/setAttendance.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                response -> {
                    Log.i("response", response);
                    dialog1.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(this, "Operation was successful",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Something " +
                                            "went wrong!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException sE) {
                        sE.printStackTrace();
                    }

                }, error -> {
            error.printStackTrace();
            dialog1.dismiss();

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("id",
                        Objects.requireNonNullElse(responseId, "0"));
                stringMap.put("class", sClassId);
                stringMap.put("staff", sStaffId);
                stringMap.put("year", sYear);
                stringMap.put("term", sTerm);
                stringMap.put("course", sCourseId);
                stringMap.put("register", sStudents);
                stringMap.put("count", sCount);
                stringMap.put("date", CourseAttendance.getDate());
                stringMap.put("_db", sDb);
                return stringMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    public void getCourseRegistration(String sClassId, String sTerm,
                                      String sYear, String sCourseId,
                                      String sDb) {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = getString(R.string.base_url) + "/getCourseRegistration.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                response -> {
                    Log.i("Response", "response" + response);
                    dialog1.dismiss();
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        String student = responseObject.getString("student");
                        String attendance = responseObject.getString(
                                "attendance");
                        JSONArray studentArray = new JSONArray(student);
                        for (int i = 0; i < studentArray.length(); i++) {
                            JSONObject object = studentArray.getJSONObject(i);
                            String name = object.getString("student_name");
                            String id = object.getString("student_id");
                            Log.i("nameId", name + id);
                            StudentTable studentTable = new StudentTable();
                            studentTable.setStudentFullName(name);
                            studentTable.setStudentId(id);
                            mStudentTableList.add(studentTable);
                        }

                        JSONArray attendanceArray = new JSONArray(attendance);
                        for (int i = 0; i < attendanceArray.length(); i++) {
                            JSONObject object = attendanceArray.getJSONObject(
                                    i);
                            String register = object.getString("register");

                            JSONArray registerArray = new JSONArray(register);
                            for (int j = 0; j < registerArray.length(); j++) {
                                JSONObject object1 =
                                        registerArray.getJSONObject(
                                                j);
                                String studentId = object1.getString("id");
                                Log.i("studentId", studentId);

                                getStudentList(mStudentTableList, studentId);
                            }
                        }

                    } catch (JSONException sE) {
                        sE.printStackTrace();
                    }
                    mStaffCourseAttendanceAdapter.notifyDataSetChanged();
                    mActionBar.setTitle("" + getSelectedCount());
                    showHideFab();
                    if (!mStudentTableList.isEmpty()) {
                        mEmptyLayout.setVisibility(View.GONE);
                    } else {
                        mEmptyLayout.setVisibility(View.VISIBLE);
                    }

                }, error -> {
            error.printStackTrace();
            dialog1.dismiss();
            Toast.makeText(this, "Something went wrong!",
                    Toast.LENGTH_SHORT).show();
            mEmptyLayout.setVisibility(View.VISIBLE);
            errorImage.setImageResource(R.drawable.no_internet);
            errorMessage.setText(R.string.failed_internet);

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("class", sClassId);
                stringMap.put("term", sTerm);
                stringMap.put("year", sYear);
                stringMap.put("course", sCourseId);
                stringMap.put("attendance", "1");
                stringMap.put("date", CourseAttendance.getDate());
                stringMap.put("_db", sDb);
                return stringMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    public void getStudentList(List<StudentTable> sStudentTableList,
                               String sId) {
        for (StudentTable student : sStudentTableList) {
            if (sId.equals(student.getStudentId())) {
                student.setSelected(true);
                Log.i("selected", String.valueOf(student.isSelected()));
            }


        }

    }
}
