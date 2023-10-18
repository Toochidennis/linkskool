package com.digitaldream.linkskool.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.adapters.FormCourseAdapter;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.StaffTableUtil;
import com.digitaldream.linkskool.models.TeacherCourseModel;
import com.digitaldream.linkskool.models.TeacherCourseModelCopy;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.TeachersTable;
import com.digitaldream.linkskool.dialog.AssignCourseDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class CoursesAssigned extends AppCompatActivity {
    public static RecyclerView recyclerView;
    private Toolbar toolbar;
    public static List<String> list;
    public static FrameLayout layout, emptyLayout;
    private FloatingActionButton fab1, fab2, save;
    List<TeachersTable> tch;
    private DatabaseHelper databaseHelper;
    private Dao<TeachersTable, Long> teacherDao;
    private String accessLevel;
    private ImageButton callIcon, smsIcon, emailIcon,
            whatsappIcon, classIcon, coursesIcon;
    private List<TeachersTable> staffList;
    public static String staffId;
    private RelativeLayout call, sms, email, whatsapp, courses, formClass;
    private TextView teacherName;
    public static FormCourseAdapter adapter;
    private Dao<ClassNameTable, Long> classDao;
    private Dao<CourseTable, Long> courseDao;
    private List<ClassNameTable> classList;
    private List<CourseTable> courseList;
    private Dao<StaffTableUtil, Long> staffUtilDao;
    private List<StaffTableUtil> staffUtilList;
    public static List<String> tcm;
    public static SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter;
    private List<String> class1, class2;
    public static List<TeacherCourseModel> clList;
    private JSONObject jsonObject;
    private String db;
    private Dao<TeacherCourseModel, Long> teacherCourseDao;
    private List<TeacherCourseModel> teacherCourseList;
    List<TeacherCourseModel> tcList = new ArrayList<>();
    private Dao<TeacherCourseModelCopy, Long> teacherCourseModelCopyDao;
    private List<TeacherCourseModelCopy> teacherCourseModelCopyList;
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_assigned);

        callIcon = findViewById(R.id.call_icon);
        smsIcon = findViewById(R.id.sms_icon);
        emailIcon = findViewById(R.id.email_icon);
        whatsappIcon = findViewById(R.id.whatsapp_icon);
        classIcon = findViewById(R.id.class_icon);
        coursesIcon = findViewById(R.id.courses_icon);
        toolbar = findViewById(R.id.toolbar);
        layout = findViewById(R.id.courseAssigned_unempty_state);
        emptyLayout = findViewById(R.id.course_assigned_empty_state);
        fab1 = findViewById(R.id.assign_course);
        fab2 = findViewById(R.id.assign_course2);
        save = findViewById(R.id.save_course);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Courses Assigned");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        Intent i = getIntent();
        staffId = i.getStringExtra("staffId");
        String name = i.getStringExtra("name");
        MySection.lists.clear();
        MySection.tag.clear();

        databaseHelper = new DatabaseHelper(this);
        try {
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(), ClassNameTable.class);
            courseDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseTable.class);
            teacherDao = DaoManager.createDao(databaseHelper.getConnectionSource(), TeachersTable.class);
            staffUtilDao = DaoManager.createDao(databaseHelper.getConnectionSource(), StaffTableUtil.class);
            teacherCourseDao = DaoManager.createDao(databaseHelper.getConnectionSource(), TeacherCourseModel.class);
            teacherCourseModelCopyDao = DaoManager.createDao(databaseHelper.getConnectionSource(), TeacherCourseModelCopy.class);
            QueryBuilder<TeacherCourseModel, Long> queryBuilder4 = teacherCourseDao.queryBuilder();
            queryBuilder4.where().eq("staffId", staffId);
            teacherCourseList = queryBuilder4.query();
            QueryBuilder<TeachersTable, Long> queryBuilder1 = teacherDao.queryBuilder();
            queryBuilder1.where().eq("staffId", staffId);
            tch = queryBuilder1.query();
            TableUtils.clearTable(databaseHelper.getConnectionSource(), TeacherCourseModelCopy.class);


        } catch (SQLException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");
        tcm = new ArrayList<>();
        sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();

        try {
            String courseName, courseId, className;

            for (int a = 0; a < teacherCourseList.size(); a++) {
                TeacherCourseModelCopy tmc = new TeacherCourseModelCopy();
                tmc.setClassId(teacherCourseList.get(a).getClassId());
                tmc.setClassName(teacherCourseList.get(a).getClassName());
                tmc.setCourseId(teacherCourseList.get(a).getCourseId());
                tmc.setCourseName(teacherCourseList.get(a).getCourseName());
                tmc.setId(teacherCourseList.get(a).getId());
                tmc.setStaffId(teacherCourseList.get(a).getStaffId());
                teacherCourseModelCopyDao.create(tmc);

            }

            teacherCourseModelCopyList = teacherCourseModelCopyDao.queryForAll();

            for (int b = 0; b < teacherCourseModelCopyList.size(); b++) {
                Log.i("size", String.valueOf(teacherCourseModelCopyList.size()));
                courseName = teacherCourseModelCopyList.get(b).getCourseName();
                className = teacherCourseModelCopyList.get(b).getClassName();
                String teacherId = teacherCourseModelCopyList.get(b).getStaffId();
                String classId = teacherCourseModelCopyList.get(b).getClassId();
                Log.i("response", courseName + " " + teacherId + " " + className + " " + teacherCourseModelCopyList.get(b).getCourseId());
                List<TeacherCourseModelCopy> classList = getTeacherCourseClasses(teacherCourseModelCopyList.get(b).getCourseId());
                sectionedRecyclerViewAdapter.addSection(courseName, new MySection(courseName, classList, this));
            }


        } catch (NullPointerException | SQLException e) {
            e.printStackTrace();
        }


        teacherName = findViewById(R.id.teacherName_profile);

        teacherName.setText(name);

        call = findViewById(R.id.call_teacher_profile);
        sms = findViewById(R.id.sms_teacher_profile);
        whatsapp = findViewById(R.id.whatsapp_teacher_profile);
        email = findViewById(R.id.email_teacher_profile);
        courses = findViewById(R.id.courses);
        formClass = findViewById(R.id.form_class);

        if (tch.get(0).getStaffEmail().isEmpty()) {
            email.setEnabled(false);
            emailIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }


        if (tch.get(0).getStaffPhone().isEmpty()) {
            call.setEnabled(false);
            callIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            sms.setEnabled(false);
            smsIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            whatsapp.setEnabled(false);
            whatsappIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);

        }

        call.setOnClickListener(view -> {
            if (!tch.get(0).getStaffPhone().isEmpty()) {
                Intent i1 = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + tch.get(0).getStaffPhone()));
                startActivity(i1);
            }
        });

        sms.setOnClickListener(view -> {
            if (!tch.get(0).getStaffPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:" + tch.get(0).getStaffPhone()));
                startActivity(intent);
            }
        });

        whatsapp.setOnClickListener(view -> {
            if (!tch.get(0).getStaffPhone().isEmpty()) {
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + "234" + tch.get(0).getStaffPhone() + "&text=" + "");
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(sendIntent);
            }
        });

        email.setOnClickListener(view -> {
            if (!tch.get(0).getStaffEmail().isEmpty()) {
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + "subject text" + "&body=" + "body text " + "&to=" + tch.get(0).getStaffEmail());
                emailIntent.setData(data);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });

        courses.setEnabled(false);
        coursesIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);

        formClass.setOnClickListener(sView -> {
            Intent intent = new Intent(CoursesAssigned.this, FormClass.class);
            intent.putExtra("staffId", staffId);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
        });


        recyclerView = findViewById(R.id.course_assigned_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        layout.setVisibility(View.VISIBLE);
        //adapter = new FormCourseAdapter(this, tcm,this);
        if (sectionedRecyclerViewAdapter.getItemCount() > 0) {
            emptyLayout.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(sectionedRecyclerViewAdapter);
        } else {
            layout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }


        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssignCourseDialog dialog = new AssignCourseDialog(CoursesAssigned.this);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssignCourseDialog dialog = new AssignCourseDialog(CoursesAssigned.this);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONArray jsonArray1 = new JSONArray();
                    jsonObject = new JSONObject();
                    teacherCourseModelCopyList = teacherCourseModelCopyDao.queryForAll();
                    for (int i = 0; i < teacherCourseModelCopyList.size(); i++) {
                        String courseId = teacherCourseModelCopyList.get(i).getCourseId();
                        String courseName = teacherCourseModelCopyList.get(i).getCourseName();
                        String classId = teacherCourseModelCopyList.get(i).getClassId();
                        String className = teacherCourseModelCopyList.get(i).getClassName();
                        String id = teacherCourseModelCopyList.get(i).getId();
                        String staffId = teacherCourseModelCopyList.get(i).getStaffId();

                        JSONObject object = new JSONObject();
                        object.put("id", id);
                        object.put("course", courseId);
                        object.put("class", classId);
                        object.put("ref_no", staffId);
                        jsonArray1.put(object);

                    }
                    jsonObject.put("teacherCourses", jsonArray1);
                    Log.i("json", jsonObject.toString());
                    saveTeacherCourseApiCall();

                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    private void addSectionToView() throws SQLException {
        sectionedRecyclerViewAdapter.removeAllSections();
        //sectionedRecyclerViewAdapter.notifyDataSetChanged();
        /*TableUtils.clearTable(databaseHelper.getConnectionSource(),TeacherCourseModelCopy.class);
        QueryBuilder<TeacherCourseModel,Long> queryBuilder4 = teacherCourseDao.queryBuilder();
        queryBuilder4.where().eq("staffId",staffId);
        teacherCourseList = queryBuilder4.query();
        try {
            String courseName,courseId,className;

            for (int a = 0; a < teacherCourseList.size(); a++) {
                TeacherCourseModelCopy tmc = new TeacherCourseModelCopy();
                tmc.setClassId(teacherCourseList.get(a).getClassId());
                tmc.setClassName(teacherCourseList.get(a).getClassName());
                tmc.setCourseId(teacherCourseList.get(a).getCourseId());
                tmc.setCourseName(teacherCourseList.get(a).getCourseName());
                tmc.setId(teacherCourseList.get(a).getId());
                tmc.setStaffId(teacherCourseList.get(a).getStaffId());
                teacherCourseModelCopyDao.create(tmc);
            }*/
        try {

            teacherCourseModelCopyList = teacherCourseModelCopyDao.queryForAll();
            String courseName, courseId, className;

            for (int b = 0; b < teacherCourseModelCopyList.size(); b++) {
                Log.i("size", String.valueOf(teacherCourseModelCopyList.size()));
                courseName = teacherCourseModelCopyList.get(b).getCourseName();
                className = teacherCourseModelCopyList.get(b).getClassName();
                String teacherId = teacherCourseModelCopyList.get(b).getStaffId();
                String classId = teacherCourseModelCopyList.get(b).getClassId();
                Log.i("response", courseName + " " + teacherId + " " + className + " " + teacherCourseModelCopyList.get(b).getCourseId());
                List<TeacherCourseModelCopy> classList = getTeacherCourseClasses(teacherCourseModelCopyList.get(b).getCourseId());
                sectionedRecyclerViewAdapter.addSection(courseName, new MySection(courseName, classList, this));
            }
            if (sectionedRecyclerViewAdapter.getItemCount() > 0) {
                emptyLayout.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(sectionedRecyclerViewAdapter);
                sectionedRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                layout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }

        } catch (NullPointerException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private List<TeacherCourseModelCopy> getTeacherCourseClasses(String courseId) {
        List<TeacherCourseModelCopy> classes = null;
        QueryBuilder<TeacherCourseModelCopy, Long> queryBuilder = teacherCourseModelCopyDao.queryBuilder();
        try {
            queryBuilder.where().eq("courseId", courseId);
            classes = queryBuilder.query();
            for (int a = 0; a < classes.size(); a++) {
                Log.i("clas", classes.get(a).getClassName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*try {
            teacherCourseModelCopyList = teacherCourseModelCopyDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for(int b=0;b<teacherCourseModelCopyList.size();b++){
            Log.i("size", String.valueOf(teacherCourseModelCopyList.size()));
            String courseName = teacherCourseModelCopyList.get(b).getCourseName();
            String className = teacherCourseModelCopyList.get(b).getClassName();
            String teacherId = teacherCourseModelCopyList.get(b).getStaffId();
            String classId = teacherCourseModelCopyList.get(b).getClassId();
            Log.i("response",courseName+ " "+teacherId+" "+className+" "+teacherCourseModelCopyList.get(b).getCourseId());
            List<TeacherCourseModelCopy> classList = getTeacherCourseClasses(teacherCourseModelCopyList.get(b).getCourseId());
            sectionedRecyclerViewAdapter.addSection(courseName,new MySection(courseName,classList,this));
        }
        if(sectionedRecyclerViewAdapter.getItemCount()>0) {
            emptyLayout.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(sectionedRecyclerViewAdapter);
            sectionedRecyclerViewAdapter.notifyDataSetChanged();
        }else{
            layout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }*/

    }


    private void deleteTeacherCourseApiCall(final String id, final Context c, String db) {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(c)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        Log.i("db", db);
        String url = Login.urlBase + "/deleteAssign.php?id=" + id + "&_db=" + db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                dialog1.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        databaseHelper = new DatabaseHelper(c);

                        teacherCourseDao = DaoManager.createDao(databaseHelper.getConnectionSource(), TeacherCourseModel.class);
                        DeleteBuilder<TeacherCourseModel, Long> deleteBuilder = teacherCourseDao.deleteBuilder();
                        deleteBuilder.where().eq("id", id);
                        deleteBuilder.delete();
                        teacherCourseModelCopyDao = DaoManager.createDao(databaseHelper.getConnectionSource(), TeacherCourseModelCopy.class);
                        DeleteBuilder<TeacherCourseModelCopy, Long> deleteBuilder1 = teacherCourseModelCopyDao.deleteBuilder();
                        deleteBuilder1.where().eq("id", id);
                        deleteBuilder.delete();
                        Toast.makeText(c, "Operation successful", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
                Log.i("error", "errorMessage" + error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);
    }


    private void saveTeacherCourseApiCall() {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = Login.urlBase + "/assignCourses.php?teacherCourses=" + jsonObject.toString() + "&_db=" + db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("jso", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        TableUtils.clearTable(databaseHelper.getConnectionSource(), TeacherCourseModelCopy.class);
                        JSONArray staffArray = jsonObject.getJSONArray("staffCourse");
                        for (int a = 0; a < staffArray.length(); a++) {
                            JSONObject jsonObject1 = staffArray.getJSONObject(a);
                            String id = jsonObject1.getString("id");
                            String staffId = jsonObject1.getString("ref_no");
                            String classId = jsonObject1.getString("class");
                            String courseId = jsonObject1.getString("course");
                            String courseName = jsonObject1.getString("course_name");
                            String className = jsonObject1.getString("class_name");
                            Log.i("response", id + " " + staffId + " " + courseName + " " + className);
                            QueryBuilder<TeacherCourseModel, Long> queryBuilder = teacherCourseDao.queryBuilder();
                            queryBuilder.where().eq("id", id);
                            List<TeacherCourseModel> teacherCourseModelList = queryBuilder.query();
                            if (teacherCourseModelList.isEmpty()) {
                                TeacherCourseModel tcm = new TeacherCourseModel();
                                tcm.setCourseName(courseName);
                                tcm.setClassName(className);
                                tcm.setId(id);
                                tcm.setStaffId(staffId);
                                tcm.setCourseId(courseId);
                                tcm.setClassId(classId);
                                teacherCourseDao.create(tcm);
                            } else {
                                UpdateBuilder<TeacherCourseModel, Long> updateBuilder = teacherCourseDao.updateBuilder();
                                updateBuilder.updateColumnValue("className", className);
                                updateBuilder.updateColumnValue("courseName", courseName);
                                updateBuilder.updateColumnValue("courseId", courseId);
                                updateBuilder.updateColumnValue("classId", classId);
                                updateBuilder.updateColumnValue("staffId", staffId);
                                updateBuilder.where().eq("id", id);
                                updateBuilder.update();
                            }
                            /*TeacherCourseModelCopy tcc = new TeacherCourseModelCopy();
                            tcc.setCourseName(courseName);
                            tcc.setClassName(className);
                            tcc.setId(id);
                            tcc.setStaffId(staffId);
                            tcc.setCourseId(courseId);
                            tcc.setClassId(classId);
                            teacherCourseModelCopyDao.create(tcc);*/
                        }
                        //sectionedRecyclerViewAdapter.removeAllSections();
                        dialog1.dismiss();
                        onBackPressed();
                        Toast.makeText(CoursesAssigned.this, "Operation successful", Toast.LENGTH_SHORT).show();

                        //addSectionToView();
                    } else if (status.equals("failed")) {
                        Toast.makeText(CoursesAssigned.this, "Operation failed", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                    dialog1.dismiss();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
                Log.i("error", "errorMessage " + error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(CoursesAssigned.this);
        requestQueue.add(stringRequest);
    }


    public static class MySection extends Section {
        String headerTitle;
        public List<TeacherCourseModelCopy> childList;

        public static List<TeacherCourseModel> lists = new ArrayList<>();
        public static List<String> tag = new ArrayList<>();
        OnClearBtnClickListener onClearBtnClickListener;
        Context context;

        public MySection(String headerTitle, List<TeacherCourseModelCopy> list, Context context) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.assigned_courses_item)
                    .headerResourceId(R.layout.header)
                    .build());
            this.headerTitle = headerTitle;
            this.childList = list;
            this.context = context;
            this.onClearBtnClickListener = onClearBtnClickListener;
            //tag.add(headerTitle);
            //lists.addAll(list);
            Log.i("respon", String.valueOf(childList.size()));
        }

        @Override
        public int getContentItemsTotal() {
            return childList.size();
        }

        public void addItem(String id, String className, String courseName, String courseId, String classId) {
            childList.add(new TeacherCourseModelCopy(id, className, courseName, courseId, classId));
        }

        public void removeItem(int position) {
            childList.remove(position);
        }

        public static List<TeacherCourseModel> teacherCourseModelList(int position) {
            return lists;
        }

        public List<TeacherCourseModelCopy> getChildList() {
            return childList;
        }


        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view, onClearBtnClickListener);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.classNames.setText(childList.get(position).getClassName().toUpperCase());
            itemHolder.clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!childList.get(position).getId().isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("DELETE ?");
                        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPreferences = context.getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
                                String db = sharedPreferences.getString("db", "");
                                new CoursesAssigned().deleteTeacherCourseApiCall(childList.get(position).getId(), context, db);
                                childList.remove(position);
                                if (sectionedRecyclerViewAdapter.getItemCount() > 0) {
                                    emptyLayout.setVisibility(View.GONE);
                                    layout.setVisibility(View.VISIBLE);
                                    recyclerView.setAdapter(sectionedRecyclerViewAdapter);
                                    sectionedRecyclerViewAdapter.notifyDataSetChanged();
                                } else {
                                    layout.setVisibility(View.GONE);
                                    emptyLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        builder.show();


                    } else {
                        childList.remove(position);
                        if (sectionedRecyclerViewAdapter.getItemCount() > 0) {
                            emptyLayout.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            recyclerView.setAdapter(sectionedRecyclerViewAdapter);
                            sectionedRecyclerViewAdapter.notifyDataSetChanged();
                        } else {
                            layout.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                        }

                    }
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            if (!childList.isEmpty()) {
                try {
                    headerHolder.headerText.setText(headerTitle.toUpperCase());
                    tag.add(headerTitle);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                headerHolder.itemView.setVisibility(View.GONE);
                tag.remove(headerTitle);
            }
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView headerText;

            public HeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                headerText = itemView.findViewById(R.id.coursename);
            }
        }

        private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView classNames;
            ImageView clearBtn;
            OnClearBtnClickListener onClearBtnClickListener;

            public ItemViewHolder(@NonNull View itemView, OnClearBtnClickListener onClearBtnClickListener) {
                super(itemView);
                classNames = itemView.findViewById(R.id.classnames);
                clearBtn = itemView.findViewById(R.id.course_clear);
                this.onClearBtnClickListener = onClearBtnClickListener;
                clearBtn.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onClearBtnClickListener.onClearBtnClick(getAdapterPosition());
            }
        }

    }

    public interface OnClearBtnClickListener {
        void onClearBtnClick(int position);
    }


}
