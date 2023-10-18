package com.digitaldream.linkskool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.adapters.StudentContactAdapter;
import com.digitaldream.linkskool.models.StudentTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentContacts extends AppCompatActivity implements StudentContactAdapter.OnStudentContactClickListener {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<StudentTable> studentContactList;
    private Spinner level, classes;
    private List<String> spinnerLevelList, spinnerClassList;
    private Dao<StudentTable, Long> studentDao;
    private DatabaseHelper databaseHelper;
    StudentContactAdapter studentContactAdapter;
    private List<ClassNameTable> classnames;
    private List<LevelTable> levelNames;
    private Dao<ClassNameTable, Long> classDao;
    private Dao<LevelTable, Long> levelDao;
    public static String studentLevelId;
    private FloatingActionButton addStudent, addStudent1;
    private SwipeRefreshLayout swipeRefreshLayout, swipeRefreshLayout2;
    private FrameLayout layout, empty_state;
    private List<StudentTable> student;
    private Menu myMenu;
    private String accessLevel;
    private TextView studentTotal, studentText;
    private String db;
    public static String studentClass;
    public static String levelName;
    public static String className, from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_contacts);
        toolbar = findViewById(R.id.toolbar);
        databaseHelper = new DatabaseHelper(this);
        try {
            studentDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    StudentTable.class);
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    ClassNameTable.class);
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(), LevelTable.class);
            studentContactList = studentDao.queryForAll();
            classnames = classDao.queryForAll();
            levelNames = levelDao.queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Student Contacts");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");
        accessLevel = sharedPreferences.getString("access_level", "");
        empty_state = findViewById(R.id.studentContact_empty_state);
        layout = findViewById(R.id.framelayout_container);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout2 = findViewById(R.id.swipeRefresh2);
        studentTotal = findViewById(R.id.student_total);
        studentText = findViewById(R.id.studentText);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.ligth_blue));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStudentList();
            }
        });
        swipeRefreshLayout2.setColorSchemeColors(getResources().getColor(R.color.ligth_blue));
        swipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStudentList();
            }
        });


        addStudent = findViewById(R.id.fab_student);
        addStudent1 = findViewById(R.id.fab_student1);
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(StudentContacts.this, AddStudent.class);
                startActivity(intent);

            }
        });
        addStudent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentContacts.this, AddStudent.class);
                startActivity(intent);

            }
        });
        level = findViewById(R.id.spinner_level_student_contacts);
        classes = findViewById(R.id.spinner_class_student_contact);
        spinnerLevelList = new ArrayList<>();
        spinnerClassList = new ArrayList<>();

        Intent i = getIntent();
        studentLevelId = i.getStringExtra("levelId");
        studentClass = i.getStringExtra("classId");
        from = i.getStringExtra("class_detail");
        String levelValue = "";
        Log.i("response", "level " + studentLevelId + " class " + studentClass);
        for (int a = 0; a < levelNames.size(); a++) {
            try {
                String level = levelNames.get(a).getLevelName().toUpperCase();
                String levelId = levelNames.get(a).getLevelId();
                if (levelId.equals(studentLevelId)) {
                    levelValue = level;
                }
                spinnerLevelList.add(level);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i("response", "level " + levelValue);


        recyclerView = findViewById(R.id.student_contact_recycler);


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
                spinnerLevelList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level.setAdapter(adapter);
        level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerClassList.clear();
                if (from != null && from.equals("class_detail")) {
                    try {
                        setSpinnerClass(studentLevelId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {

                    String levelSelected = adapterView.getItemAtPosition(i).toString();
                    studentLevelId = levelNames.get(i).getLevelId();
                    levelName = levelNames.get(i).getLevelName();
                    try {
                        setSpinnerClass(studentLevelId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                //getStudentByLevel(studentLevelId);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        level.setSelection(getIndex(level, levelValue.trim()));

        ArrayAdapter adapterClass = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
                spinnerClassList);
        adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classes.setAdapter(adapterClass);

        classes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                QueryBuilder<StudentTable, Long> queryBuilder = studentDao.queryBuilder();
                studentClass = "";
                if (classnames.size() > 0) {
                    studentClass = classnames.get(i).getClassId();
                    className = classnames.get(i).getClassName();
                }

                try {
                    queryBuilder.where().eq("studentLevel", studentLevelId).and().eq("studentClass",
                            studentClass);
                    studentContactList = queryBuilder.query();
                    layout.setVisibility(View.VISIBLE);
                    empty_state.setVisibility(View.GONE);
                    if (!studentContactList.isEmpty()) {
                        studentContactAdapter = new StudentContactAdapter(StudentContacts.this,
                                studentContactList, StudentContacts.this);
                        recyclerView.setAdapter(studentContactAdapter);
                        studentContactAdapter.notifyDataSetChanged();
                        studentTotal.setText(String.valueOf(studentContactList.size()));
                        studentText.setText("Students");
                        if (studentContactList.size() < 2) {
                            studentText.setText("Student");
                        }

                    } else {
                        studentTotal.setText(String.valueOf(studentContactList.size()));
                        studentText.setText("Student");
                        layout.setVisibility(View.GONE);
                        empty_state.setVisibility(View.VISIBLE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        for (int a = 0; a < spinnerClassList.size(); a++) {

        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myMenu = menu;
        if (studentContactList.size() == StudentContactAdapter.guardianPhones.size()) {
            getMenuInflater().inflate(R.menu.selection_menu, menu);
            menu.findItem(R.id.select_all).setVisible(false);

        } else if (StudentContactAdapter.guardianPhones.size() > 0) {
            getMenuInflater().inflate(R.menu.selection_menu, menu);
        } else {
            getSupportActionBar().setTitle("Student Contacts");
        }
        return true;

    }

    @Override
    protected void onResume() {
        super.onResume();

        QueryBuilder<StudentTable, Long> queryBuilder = studentDao.queryBuilder();

        try {
            queryBuilder.where().eq("studentLevel", studentLevelId).and().eq("studentClass",
                    studentClass);
            studentContactList = queryBuilder.query();
            layout.setVisibility(View.VISIBLE);
            empty_state.setVisibility(View.GONE);
            if (!studentContactList.isEmpty()) {
                studentContactAdapter = new StudentContactAdapter(StudentContacts.this,
                        studentContactList, StudentContacts.this);
                recyclerView.setAdapter(studentContactAdapter);
                studentContactAdapter.notifyDataSetChanged();
                studentTotal.setText(String.valueOf(studentContactList.size()));
                studentText.setText("Students");
                if (studentContactList.size() < 2) {
                    studentText.setText("Student");
                }

            } else {
                studentTotal.setText(String.valueOf(studentContactList.size()));
                studentText.setText("Student");
                layout.setVisibility(View.GONE);
                empty_state.setVisibility(View.VISIBLE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String emails = "";
        String phoneNumbers = "";
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.email_selection:
                if (!StudentContactAdapter.guardianEmails.isEmpty()) {
                    Intent emailIntent = new Intent(Intent.ACTION_VIEW);

                    for (int i = 0; i < StudentContactAdapter.guardianEmails.size(); i++) {
                        if (StudentContactAdapter.guardianEmails.get(i).isEmpty()) {
                            continue;
                        }
                        emails = emails + "" + StudentContactAdapter.guardianEmails.get(i) + ",";
                    }
                    try {
                        emails = emails.substring(0, emails.length() - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Uri data = Uri.parse(
                            "mailto:?subject=" + "subject text" + "&body=" + "body text " + "&to" +
                                    "=" + emails);
                    emailIntent.setData(data);
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } else {
                    Toast.makeText(StudentContacts.this, "There are no email address available",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sms_selection:
                if (!StudentContactAdapter.guardianPhones.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    for (int i = 0; i < StudentContactAdapter.guardianPhones.size(); i++) {
                        if (StudentContactAdapter.guardianPhones.get(i).isEmpty()) {
                            continue;
                        }
                        phoneNumbers = phoneNumbers + "" + StudentContactAdapter.guardianPhones.get(
                                i) + ",";
                    }
                    try {
                        phoneNumbers = phoneNumbers.substring(0, phoneNumbers.length() - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intent.setData(Uri.parse("smsto:" + phoneNumbers));
                    startActivity(intent);
                } else {
                    Toast.makeText(StudentContacts.this, "There are no phone numbers available",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.select_all:
                StudentContactAdapter.flagValue = true;

                myMenu.findItem(R.id.select_all).setVisible(false);
                studentContactAdapter = new StudentContactAdapter(StudentContacts.this,
                        studentContactList, StudentContacts.this);
                recyclerView.setAdapter(studentContactAdapter);
                studentContactAdapter.notifyDataSetChanged();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStudentContactClick(int position) {
        Intent intent = new Intent(this, StudentProfile.class);
        StudentTable st = new StudentTable();
        st.setStudentSurname(studentContactList.get(position).getStudentSurname());
        st.setStudentFirstname(studentContactList.get(position).getStudentFirstname());
        st.setStudentMiddlename(studentContactList.get(position).getStudentMiddlename());
        st.setStudentLevel(studentContactList.get(position).getStudentLevel());
        st.setGuardianName(studentContactList.get(position).getGuardianName());
        st.setGuardianPhoneNo(studentContactList.get(position).getGuardianPhoneNo());
        st.setGuardianEmail(studentContactList.get(position).getGuardianEmail());
        st.setGuardianAddress(studentContactList.get(position).getGuardianAddress());
        st.setStudentGender(studentContactList.get(position).getStudentGender());
        st.setStudentReg_no(studentContactList.get(position).getStudentReg_no());
        st.setStudentLevel(studentContactList.get(position).getStudentLevel());
        st.setStudentClass(studentContactList.get(position).getStudentClass());
        st.setState_of_origin(studentContactList.get(position).getState_of_origin());
        st.setDate_of_birth(studentContactList.get(position).getDate_of_birth());
        st.setStudentId(studentContactList.get(position).getStudentId());
        intent.putExtra("studentObject", st);
        startActivity(intent);
    }


    public void setSpinnerClass(String a) throws SQLException {
        String classNameValue = null;
        QueryBuilder<ClassNameTable, Long> queryBuilder = classDao.queryBuilder();
        queryBuilder.where().eq("level", a);
        classnames = queryBuilder.query();
        Collections.reverse(classnames);
        if (!classnames.isEmpty()) {
            for (int i = 0; i < classnames.size(); i++) {
                try {
                    String classname = classnames.get(i).getClassName().toUpperCase();
                    spinnerClassList.add(classname);
                    if (classnames.get(i).getClassId().equals(studentClass)) {
                        classNameValue = classnames.get(i).getClassName();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ArrayAdapter adapterClass = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
                    spinnerClassList);
            adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            classes.setAdapter(adapterClass);
        } else {

            spinnerClassList.clear();
            spinnerClassList.add("");
            ArrayAdapter adapterClass = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
                    spinnerClassList);
            adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            classes.setAdapter(adapterClass);

        }
        if (classNameValue != null && !classNameValue.isEmpty()) {
            classes.setSelection(getIndex(classes, classNameValue));
        }

    }

    @Override
    public void onBackPressed() {
        if (StudentContactAdapter.flagValue) {
            StudentContactAdapter.guardianPhones.clear();
            StudentContactAdapter.guardianEmails.clear();
            studentContactAdapter.notifyDataSetChanged();
            invalidateOptionsMenu();
            StudentContactAdapter.flagValue = false;

        } else if (StudentContactAdapter.selectedCounter > 0) {
            StudentContactAdapter.guardianPhones.clear();
            StudentContactAdapter.guardianEmails.clear();
            studentContactAdapter.notifyDataSetChanged();
            StudentContactAdapter.selectedCounter = 0;
            invalidateOptionsMenu();
            StudentContactAdapter.selectedCounter = 0;
        } else {
            super.onBackPressed();
        }

    }


    private void refreshStudentList() {

        String login_url = getString(R.string.base_url) + "/allStudents.php?_db=" + db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, login_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response", response);

                        try {

                            JSONObject jsonObject = new JSONObject(response.toLowerCase());

                            if (jsonObject.has("allstudentrecords")) {

                                JSONObject object = jsonObject.getJSONObject("allstudentrecords");
                                JSONArray jsonArray = object.getJSONArray("rows");
                                if (jsonArray.length() > 0) {
                                    TableUtils.clearTable(databaseHelper.getConnectionSource(),
                                            StudentTable.class);

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONArray jsonArray1 = jsonArray.getJSONArray(i);

                                        String id = jsonArray1.getString(0);
                                        String studentSurname = jsonArray1.getString(2);
                                        String studentFirstname = jsonArray1.getString(3);
                                        String studentMiddlename = jsonArray1.getString(4);
                                        String studentGender = null;
                                        if (jsonArray1.getString(5).equals("m")) {
                                            studentGender = "Male";
                                        } else if (jsonArray1.getString(5).equals("f")) {
                                            studentGender = "Female";

                                        }
                                        String studentReg_no = jsonArray1.getString(12);
                                        String studentClass = jsonArray1.getString(26);
                                        String studentLevel = jsonArray1.getString(27);
                                        String studentDOB = jsonArray1.getString(6);
                                        String guardianName = jsonArray1.getString(14);
                                        String guardianAddress = jsonArray1.getString(15);
                                        String guardianEmail = jsonArray1.getString(16);
                                        String guardianPhoneNo = jsonArray1.getString(17);
                                        String lga = jsonArray1.getString(18);
                                        String state_of_origin = jsonArray1.getString(19);
                                        String nationality = jsonArray1.getString(20);
                                        String date_admitted = jsonArray1.getString(22);
                                        String class_name = jsonArray1.getString(28);
                                        QueryBuilder<StudentTable, Long> queryBuilder =
                                                studentDao.queryBuilder();
                                        queryBuilder.where().eq("studentId", id);
                                        student = queryBuilder.query();

                                        if (student.isEmpty()) {
                                            StudentTable st = new StudentTable();
                                            st.setStudentId(id);
                                            st.setStudentSurname(studentSurname);
                                            st.setStudentFirstname(studentFirstname);
                                            st.setStudentMiddlename(studentMiddlename);
                                            st.setStudentGender(studentGender);
                                            st.setStudentReg_no(studentReg_no);
                                            st.setStudentClass(studentClass);
                                            st.setStudentLevel(studentLevel);
                                            st.setGuardianName(guardianName);
                                            st.setGuardianAddress(guardianAddress);
                                            st.setGuardianEmail(guardianEmail);
                                            st.setGuardianPhoneNo(guardianPhoneNo);
                                            st.setLga(lga);
                                            st.setState_of_origin(state_of_origin);
                                            st.setNationality(nationality);
                                            st.setDate_admitted(date_admitted);
                                            st.setDate_of_birth(studentDOB);
                                            studentDao.create(st);
                                        }
                                    }
                                    QueryBuilder<StudentTable, Long> queryBuilder =
                                            studentDao.queryBuilder();
                                    queryBuilder.where().eq("studentLevel",
                                            studentLevelId).and().eq("studentClass", studentClass);
                                    studentContactList = queryBuilder.query();
                                    layout.setVisibility(View.VISIBLE);
                                    empty_state.setVisibility(View.GONE);

                                    if (!studentContactList.isEmpty()) {
                                        studentContactAdapter = new StudentContactAdapter(
                                                StudentContacts.this, studentContactList,
                                                StudentContacts.this);

                                        recyclerView.setAdapter(studentContactAdapter);
                                        studentContactAdapter.notifyDataSetChanged();

                                        studentTotal.setText(
                                                String.valueOf(studentContactList.size()));

                                        studentText.setText("Students");

                                        if (studentContactList.size() < 2) {
                                            studentText.setText("Student");
                                        }

                                    } else {
                                        studentTotal.setText(
                                                String.valueOf(studentContactList.size()));
                                        studentText.setText("Student");
                                        layout.setVisibility(View.GONE);
                                        empty_state.setVisibility(View.VISIBLE);
                                    }
                                    Toast.makeText(StudentContacts.this, "List updated",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout2.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setClassSpinner() {


    }

}
