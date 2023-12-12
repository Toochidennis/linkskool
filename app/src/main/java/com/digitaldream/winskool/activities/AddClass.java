package com.digitaldream.winskool.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.models.ClassNameTable;
import com.digitaldream.winskool.fragments.ClassResultFragment;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.models.TeachersTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AddClass extends AppCompatActivity {
    private Toolbar toolbar;
    private Button addClass;
    private EditText classLevel;
    private Dao<TeachersTable, Long> teacherDao;
    private Dao<LevelTable, Long> levelDao;
    private DatabaseHelper databaseHelper;
    private List<String> levelList, teacherList;
    private List<TeachersTable> tchList;
    private List<LevelTable> lvlList;
    private Button addBtn;
    private String levelId;
    private EditText className, formteacher;
    private String classNameValue, formTeacherId;
    private Dao<ClassNameTable, Long> classDao;
    private List<ClassNameTable> classnames;
    public static String teacherId;
    String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        toolbar = findViewById(R.id.toolbar);
        className = findViewById(R.id.class_name_addclass);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Class");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        classLevel = findViewById(R.id.addclass_level);
        formteacher = findViewById(R.id.form_teacher);

        databaseHelper = new DatabaseHelper(this);
        try {
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(), LevelTable.class);
            teacherDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    TeachersTable.class);
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    ClassNameTable.class);
            tchList = teacherDao.queryForAll();
            lvlList = levelDao.queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        levelList = new ArrayList<>();
        teacherList = new ArrayList<>();

        for (int i = 0; i < lvlList.size(); i++) {
            levelList.add(lvlList.get(i).getLevelName());
        }

        for (int a = 0; a < tchList.size(); a++) {
            teacherList.add(
                    tchList.get(a).getStaffSurname() + " " + tchList.get(a).getStaffFirstname());
        }


        QueryBuilder<LevelTable, Long> queryBuilder = levelDao.queryBuilder();
        try {
            queryBuilder.where().eq("levelId", ClassResultFragment.levelId);
            List<LevelTable> levels = queryBuilder.query();
            classLevel.setText(levels.get(0).getLevelName().toUpperCase());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        classLevel.setEnabled(false);


        addClass = findViewById(R.id.add_class);
        addClass.setOnClickListener(v -> {
            classNameValue = className.getText().toString();
            validateInput();
        });


        formteacher.setOnClickListener(v -> {
            Intent intent = new Intent(AddClass.this, AllTeachers.class);
            startActivityForResult(intent, 1);
        });

        if (getIntent().getStringExtra("from").equals("classEdit")) {
            getSupportActionBar().setTitle("Edit Class");
            classId = getIntent().getStringExtra("class_id");
            QueryBuilder<ClassNameTable, Long> queryBuilder1 = classDao.queryBuilder();
            try {
                queryBuilder1.where().eq("classId", classId);
                List<ClassNameTable> classList = queryBuilder1.query();
                className.setText(classList.get(0).getClassName().toUpperCase());

                QueryBuilder<LevelTable, Long> queryBuilder2 = levelDao.queryBuilder();

                queryBuilder2.where().eq("levelId", classList.get(0).getLevel());
                List<LevelTable> levels = queryBuilder2.query();
                classLevel.setText(levels.get(0).getLevelName().toUpperCase());
                classLevel.setEnabled(false);

                QueryBuilder<TeachersTable, Long> queryBuilder3 = teacherDao.queryBuilder();
                queryBuilder3.where().eq("staffId", classList.get(0).getFormTeacher());
                List<TeachersTable> teacherList = queryBuilder3.query();
                if (teacherList.size() > 0) {
                    formteacher.setText(teacherList.get(
                            0).getStaffSurname().toUpperCase() + " " + teacherList.get(
                            0).getStaffFirstname().toUpperCase());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                teacherId = data.getStringExtra("teacherId");
                QueryBuilder<TeachersTable, Long> queryBuilder = teacherDao.queryBuilder();
                try {
                    queryBuilder.where().eq("staffId", teacherId);
                    List<TeachersTable> teacherList = queryBuilder.query();
                    String teacherName = teacherList.get(
                            0).getStaffSurname() + " " + teacherList.get(0).getStaffFirstname();
                    formteacher.setText(teacherName.toUpperCase());

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }

    private void validateInput() {
        if (formteacher.getText().toString().isEmpty() || className.getText().toString().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("All fields are required");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        } else {
            if (getIntent().getStringExtra("from").equals("classEdit")) {
                callEditClassApi();
            } else {
                getAddClassApiCall();
            }
        }
    }

    private void getAddClassApiCall() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        final String db = sharedPreferences.getString("db", "");
        //String url = "http://linkskool.com/newportal/api/addClass.php?class="+classNameValue+"&level="+levelId+"&form_teacher="+formTeacherId;
        String url =
                getString(R.string.base_url) + "/addClass.php?class_name=" + classNameValue + "&level=" + ClassResultFragment.levelId + "&form_teacher=" + teacherId + "&_db=" + db;
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.i("response", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        if (status.equals("success")) {
                            JSONObject recordObject = object.getJSONObject("records");
                            String classId = recordObject.getString("id");
                            String className = recordObject.getString("class_name");
                            String levelId = recordObject.getString("level");
                            String staffId = recordObject.getString("form_teacher");
                            QueryBuilder<ClassNameTable, Long> queryBuilder =
                                    classDao.queryBuilder();
                            queryBuilder.where().eq("classId", classId);
                            classnames = queryBuilder.query();
                            if (classnames.isEmpty()) {
                                ClassNameTable ct = new ClassNameTable();
                                ct.setClassId(classId);
                                ct.setClassName(className);
                                ct.setLevel(levelId);
                                ct.setFormTeacher(staffId);
                                classDao.create(ct);
                            }
                            dialog1.dismiss();
                            onBackPressed();
                            Toast.makeText(AddClass.this, "Class added successfully",
                                    Toast.LENGTH_SHORT).show();


                        } else if (status.equals("failed")) {
                            dialog1.dismiss();
                            Toast.makeText(AddClass.this, "Class already added",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
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
    protected void onResume() {
        super.onResume();
    }

    private void callEditClassApi() {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        final String db = sharedPreferences.getString("db", "");


        String url =
                getString(
                        R.string.base_url) + "/addClass.php?id=" + classId + "&class_name=" + classNameValue +
                        "&level=" + ClassResultFragment.levelId + "&form_teacher=" + teacherId +
                        "&_db=" + db;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    dialog1.dismiss();
                    try {
                        Log.i("response", response);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            JSONObject recordsObject = jsonObject.getJSONObject("records");
                            String classId = recordsObject.getString("id");
                            String className = recordsObject.getString("class_name");
                            String level = recordsObject.getString("level");
                            String formTeacher = recordsObject.getString("form_teacher");
                            UpdateBuilder<ClassNameTable, Long> updateBuilder =
                                    classDao.updateBuilder();
                            updateBuilder.updateColumnValue("className", className);
                            updateBuilder.updateColumnValue("formTeacher", formTeacher);
                            updateBuilder.updateColumnValue("level", level);
                            updateBuilder.where().eq("classId", classId);
                            updateBuilder.update();


                            Toast.makeText(AddClass.this, "Class updated successfully",
                                    Toast.LENGTH_SHORT).show();
                            onBackPressed();

                        } else if (status.equals("failed")) {
                            Toast.makeText(AddClass.this, "Class already exists",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | SQLException e) {
                        e.printStackTrace();
                    }

                }, error -> dialog1.dismiss());

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
