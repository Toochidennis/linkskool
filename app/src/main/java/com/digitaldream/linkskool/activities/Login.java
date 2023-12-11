package com.digitaldream.linkskool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.AssessmentModel;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.models.FormClassModel;
import com.digitaldream.linkskool.models.GeneralSettingModel;
import com.digitaldream.linkskool.models.GradeModel;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.models.NewsTable;
import com.digitaldream.linkskool.models.StaffTableUtil;
import com.digitaldream.linkskool.models.StudentCourses;
import com.digitaldream.linkskool.models.StudentResultDownloadTable;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.models.TeacherCourseModel;
import com.digitaldream.linkskool.models.TeachersTable;
import com.digitaldream.linkskool.utils.FunctionUtils;
import com.digitaldream.linkskool.utils.VolleyCallback;
import com.google.android.material.textfield.TextInputLayout;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
public class Login extends AppCompatActivity {
    private TextInputLayout username, password, code;
    private Button login;
    String usernameText, passwordText, pinText;
    private DatabaseHelper databaseHelper;
    private Dao<StudentTable, Long> studentDao;
    private Dao<TeachersTable, Long> teacherDao;
    private Dao<ClassNameTable, Long> classDao;
    private Dao<LevelTable, Long> levelDao;
    private List<StudentTable> student;
    private List<TeachersTable> teacher;
    private List<ClassNameTable> classnames;
    private List<LevelTable> levelNames;
    private Dao<NewsTable, Long> newsDao;
    private List<NewsTable> newsList;
    private Dao<CourseTable, Long> courseDao;
    private List<CourseTable> courseList;
    private Dao<StudentResultDownloadTable, Long> studentResultDao;
    private Dao<StudentCourses, Long> studentCourseDao;
    private Dao<GradeModel, Long> gradeDao;
    private List<GradeModel> gradeList;
    private Dao<GeneralSettingModel, Long> generalSettingDao;
    private Dao<AssessmentModel, Long> assessmentDao;
    private List<AssessmentModel> assessmentList;
    public static String urlBase = "http://linkskool.com/developmentportal/api";
    public static String fileBaseUrl = "https://linkskool.com/developmentportal";
    private Dao<StaffTableUtil, Long> staffUtilDao;
    private Dao<TeacherCourseModel, Long> teacherCourseDao;
    private Dao<FormClassModel, Long> formClassDao;
    private Dao<CourseOutlineTable, Long> courseOutlineDao;
    private boolean fromLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username_layout);
        password = findViewById(R.id.password_layout);
        code = findViewById(R.id.code_layout);
        login = findViewById(R.id.login);

        databaseHelper = new DatabaseHelper(this);
        try {
            studentDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), StudentTable.class);
            teacherDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), TeachersTable.class);
            classDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), ClassNameTable.class);
            levelDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), LevelTable.class);
            newsDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    NewsTable.class);
            courseDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), CourseTable.class);
            studentResultDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(),
                    StudentResultDownloadTable.class);
            studentCourseDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), StudentCourses.class);
            gradeDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), GradeModel.class);
            generalSettingDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(),
                    GeneralSettingModel.class);
            assessmentDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(),
                    AssessmentModel.class);
            staffUtilDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), StaffTableUtil.class);
            teacherCourseDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(),
                    TeacherCourseModel.class);
            formClassDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), FormClassModel.class);
            courseOutlineDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(),
                    CourseOutlineTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        login.setOnClickListener(view -> {

            if (validateLoginForm()) {
                loginApiCall();
            }

        });
    }

    private boolean validateLoginForm() {
        usernameText = Objects.requireNonNull(username.getEditText()).getText().toString().trim();
        passwordText = Objects.requireNonNull(password.getEditText()).getText().toString().trim();
        pinText = Objects.requireNonNull(code.getEditText()).getText().toString().trim();
        //  "5416";// pin.getText().toString().trim();"7755" 5416 2271
        // 9814

        if (usernameText.isEmpty()) {
            username.getEditText().setError("Required");
            return false;
        } else if (passwordText.isEmpty()) {
            password.getEditText().setError("Required");
            return false;
        } else if (pinText.isEmpty()) {
            code.getEditText().setError("Required");
            return false;
        } else {
            username.getEditText().setError(null);
            password.getEditText().setError(null);
            code.getEditText().setError(null);
            return true;
        }
    }

    private void loginApiCall() {
        String url = urlBase + "/login.php";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", pinText);
        hashMap.put("username", usernameText);
        hashMap.put("password", passwordText);

        FunctionUtils.sendRequestToServer(Request.Method.POST, url, this, hashMap,
                new VolleyCallback() {
                    @Override
                    public void onResponse(@NonNull String response) {
                        parseJSON(response);
                    }

                    @Override
                    public void onError(@NonNull VolleyError error) {
                        Toast.makeText(Login.this, error+ "Something went wrong, " +
                                "Please try again", Toast.LENGTH_SHORT).show();
                    }
                }, true);

    }

    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }

    private int getIndex(JSONArray columnArr, String field) throws JSONException {
        List<String> columnList = new ArrayList<>();
        for (int a = 0; a < columnArr.length(); a++) {
            columnList.add(columnArr.getString(a));
        }
        return columnList.indexOf(field);
    }

    private void parseJSON(String response) {
        if (!response.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equals(
                        "failed") || jsonObject.getString("status")
                        .equals("error")) {
                    Toast.makeText(Login.this, "Invalid Login details",
                            Toast.LENGTH_SHORT).show();

                } else if (jsonObject.getString("status").equals("success")) {
                    if (jsonObject.getJSONObject("profile").getString(
                            "access_level").equals("2")) {

                        if (jsonObject.has("allStudentRecords")) {

                            JSONObject object = jsonObject.getJSONObject(
                                    "allStudentRecords");
                            JSONArray jsonArray = object.getJSONArray("rows");
                            JSONArray columnArr1 = object.getJSONArray(
                                    "columns");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONArray jsonArray1 = jsonArray.getJSONArray(
                                        i);

                                String id = jsonArray1.getString(
                                        getIndex(columnArr1, "id"));
                                String studentSurname = jsonArray1.getString(
                                        getIndex(columnArr1, "surname"));
                                String studentFirstname = jsonArray1.getString(
                                        getIndex(columnArr1, "first_name"));
                                String studentMiddlename = jsonArray1.getString(
                                        getIndex(columnArr1, "middle"));
                                String studentGender = null;
                                if (jsonArray1.getString(
                                        getIndex(columnArr1, "sex")).equals(
                                        "m")) {
                                    studentGender = "Male";
                                } else if (jsonArray1.getString(
                                        getIndex(columnArr1, "sex")).equals(
                                        "f")) {
                                    studentGender = "Female";

                                }
                                String studentReg_no = jsonArray1.getString(
                                        getIndex(columnArr1,
                                                "registration_no"));
                                String studentClass = jsonArray1.getString(
                                        getIndex(columnArr1, "student_class"));
                                String studentLevel = jsonArray1.getString(
                                        getIndex(columnArr1, "student_level"));
                                String studentDOB = jsonArray1.getString(
                                        getIndex(columnArr1, "birthdate"));
                                String guardianName = jsonArray1.getString(
                                        getIndex(columnArr1, "guardian_name"));
                                String guardianAddress = jsonArray1.getString(
                                        getIndex(columnArr1, "address"));
                                String guardianEmail = jsonArray1.getString(
                                        getIndex(columnArr1, "guardian_email"));
                                String guardianPhoneNo = jsonArray1.getString(
                                        getIndex(columnArr1,
                                                "guardian_phone_no"));
                                String lga = jsonArray1.getString(
                                        getIndex(columnArr1,
                                                "local_government_origin"));
                                String state_of_origin = jsonArray1.getString(
                                        getIndex(columnArr1, "state_origin"));
                                String nationality = jsonArray1.getString(
                                        getIndex(columnArr1, "nationality"));
                                String date_admitted = jsonArray1.getString(
                                        getIndex(columnArr1, "date_admitted"));
                                //String class_name = jsonArray1.getString(28);
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
                            JSONObject object1 = jsonObject.getJSONObject(
                                    "allStaffRecords");
                            JSONArray jsonArray2 = object1.getJSONArray("rows");
                            JSONArray columnArr = object1.getJSONArray(
                                    "columns");
                            List<String> columnList = new ArrayList<>();
                            for (int a = 0; a < columnArr.length(); a++) {
                                columnList.add(columnArr.getString(a));
                            }


                            int teacherCounter = 0;
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                JSONArray jsonArray3 = jsonArray2.getJSONArray(
                                        i);
                                String id = jsonArray3.getString(
                                        columnList.indexOf("id"));
                                String staffFirstName = jsonArray3.getString(
                                        columnList.indexOf("first_name"));
                                String staffSurname = jsonArray3.getString(
                                        columnList.indexOf("surname"));
                                String staffMiddlename = jsonArray3.getString(
                                        columnList.indexOf("middle"));
                                String staffGender = jsonArray3.getString(
                                        columnList.indexOf("sex"));
                                String staffAddress = jsonArray3.getString(
                                        columnList.indexOf("address"));
                                String nationality = jsonArray3.getString(
                                        columnList.indexOf("nationality"));
                                String phone = jsonArray3.getString(
                                        getIndex(columnArr, "phone"));
                                String staffEmail = jsonArray3.getString(
                                        getIndex(columnArr, "email"));
                                String birthdate = jsonArray3.getString(
                                        getIndex(columnArr, "birthdate"));
                                String staffNo = jsonArray3.getString(
                                        getIndex(columnArr, "staff_no"));
                                String staffLga = jsonArray3.getString(
                                        getIndex(columnArr,
                                                "local_government_origin"));
                                String staffStateOrigin = jsonArray3.getString(
                                        getIndex(columnArr, "state_origin"));
                                String staffAccessLevel = jsonArray3.getString(
                                        getIndex(columnArr, "access_level"));
                                String password = jsonArray3.getString(
                                        columnList.indexOf("password"));
                                QueryBuilder<TeachersTable, Long> queryBuilder =
                                        teacherDao.queryBuilder();
                                queryBuilder.where().eq("staffId", id);

                                teacher = queryBuilder.query();
                                if (teacher.isEmpty()) {
                                    teacherCounter++;
                                    TeachersTable tch = new TeachersTable();
                                    tch.setStaffId(id);
                                    tch.setStaffFirstname(staffFirstName);
                                    tch.setStaffSurname(staffSurname);
                                    tch.setStaffMiddlename(staffMiddlename);
                                    tch.setStaffGender(staffGender);
                                    tch.setStaffAddress(staffAddress);
                                    tch.setStaffNationality(nationality);
                                    tch.setStaffBirthday(birthdate);
                                    tch.setStaffPhone(phone);
                                    tch.setStaffEmail(staffEmail);
                                    tch.setStaffNo(staffNo);
                                    tch.setStaffLGA(staffLga);
                                    tch.setPassword(password);
                                    tch.setStaffStateOrigin(staffStateOrigin);
                                    tch.setStaffAccessLevel(staffAccessLevel);
                                    tch.setId(teacherCounter);
                                    teacherDao.create(tch);
                                }
                            }

                            JSONObject jsonObject3 = jsonObject.getJSONObject(
                                    "className");
                            JSONArray jsonArray4 = jsonObject3.getJSONArray(
                                    "rows");
                            JSONArray columnArr2 = jsonObject3.getJSONArray(
                                    "columns");
                            for (int i = 0; i < jsonArray4.length(); i++) {
                                JSONArray jsonArray5 = jsonArray4.getJSONArray(
                                        i);
                                String classId = jsonArray5.getString(
                                        getIndex(columnArr2, "id"));
                                String className = jsonArray5.getString(
                                        getIndex(columnArr2, "class_name"));
                                String level = jsonArray5.getString(
                                        getIndex(columnArr2, "level"));
                                QueryBuilder<ClassNameTable, Long> queryBuilder =
                                        classDao.queryBuilder();
                                queryBuilder.where().eq("classId", classId);
                                classnames = queryBuilder.query();
                                if (classnames.isEmpty()) {
                                    ClassNameTable cn = new ClassNameTable();
                                    cn.setClassId(classId);
                                    cn.setClassName(className);
                                    cn.setLevel(level);
                                    classDao.create(cn);
                                }

                            }

                            JSONObject jsonObject6 = jsonObject.getJSONObject(
                                    "levelName");
                            JSONArray jsonArray7 = jsonObject6.getJSONArray(
                                    "rows");
                            JSONArray columnArr3 = jsonObject6.getJSONArray(
                                    "columns");

                            for (int i = 0; i < jsonArray7.length(); i++) {
                                JSONArray jsonArray8 = jsonArray7.getJSONArray(
                                        i);
                                String levelId = jsonArray8.getString(
                                        getIndex(columnArr3, "id"));
                                String levelName = jsonArray8.getString(
                                        getIndex(columnArr3, "level_name"));
                                String schoolType = jsonArray8.getString(
                                        getIndex(columnArr3, "school_type"));
                                String rank = jsonArray8.getString(
                                        getIndex(columnArr3, "rank"));
                                QueryBuilder<LevelTable, Long> queryBuilder =
                                        levelDao.queryBuilder();
                                queryBuilder.where().eq("levelId", levelId);
                                levelNames = queryBuilder.query();
                                if (levelNames.isEmpty()) {
                                    LevelTable lt = new LevelTable();
                                    lt.setLevelId(levelId);
                                    lt.setLevelName(levelName);
                                    lt.setSchoolType(schoolType);
                                    lt.setRank(rank);
                                    levelDao.create(lt);
                                }

                            }

                            JSONObject jsonObject8 = jsonObject.getJSONObject(
                                    "news");
                            JSONArray jsonArray9 = jsonObject8.getJSONArray(
                                    "rows");
                            JSONArray columnArr4 = jsonObject8.getJSONArray(
                                    "columns");
                            for (int i = 0; i < jsonArray9.length(); i++) {
                                JSONArray jsonArray10 = jsonArray9.getJSONArray(
                                        i);
                                String newsId = jsonArray10.getString(
                                        getIndex(columnArr4, "id"));
                                String newsSubject = jsonArray10.getString(
                                        getIndex(columnArr4, "subject"));
                                String newsContent = jsonArray10.getString(
                                        getIndex(columnArr4, "content"));
                                String datePosted = jsonArray10.getString(
                                        getIndex(columnArr4, "date_posted"));
                                String newsImageUrl = jsonArray10.getString(
                                        getIndex(columnArr4, "pic_ref"));
                                QueryBuilder<NewsTable, Long> queryBuilder =
                                        newsDao.queryBuilder();
                                queryBuilder.where().eq("newsId", newsId);
                                newsList = queryBuilder.query();
                                if (newsList.isEmpty()) {
                                    NewsTable nt = new NewsTable();
                                    nt.setNewsId(newsId);
                                    nt.setNewsSubject(stripHtml(newsSubject));
                                    nt.setNewsContent(stripHtml(newsContent));
                                    nt.setDatePosted(stripHtml(datePosted));
                                    nt.setNewsImageUrl(stripHtml(newsImageUrl));
                                    newsDao.create(nt);
                                }
                            }
                            if (jsonObject.has("courses")) {
                                JSONObject jsonObject11;

                                jsonObject11 = jsonObject.getJSONObject(
                                        "courses");
                                Log.i("response", jsonObject11.toString());
                                JSONArray jsonArray11 =
                                        jsonObject11.getJSONArray(
                                                "rows");
                                JSONArray columnArr5 =
                                        jsonObject11.getJSONArray(
                                                "columns");

                                for (int b = 0; b < jsonArray11.length(); b++) {
                                    JSONArray coursesArray =
                                            jsonArray11.getJSONArray(
                                                    b);
                                    String courseId =
                                            coursesArray.getString(
                                                    getIndex(columnArr5, "id"));
                                    String courseName =
                                            coursesArray.getString(
                                                    getIndex(columnArr5,
                                                            "course_name"));
                                    String courseCode =
                                            coursesArray.getString(
                                                    getIndex(columnArr5,
                                                            "course_code"));

                                    QueryBuilder<CourseTable, Long> queryBuilder =
                                            courseDao.queryBuilder();
                                    queryBuilder.where().eq("courseId",
                                            courseId);
                                    courseList = queryBuilder.query();
                                    if (courseList.isEmpty()) {
                                        CourseTable ct = new CourseTable();
                                        ct.setCourseId(courseId);
                                        ct.setCourseName(courseName);
                                        ct.setCourseCode(courseCode);
                                        courseDao.create(ct);
                                    }

                                }

                            }


                            JSONObject jsonObject12 = jsonObject.getJSONObject(
                                    "gradeSettings");
                            JSONArray jsonArray12 = jsonObject12.getJSONArray(
                                    "rows");
                            JSONArray columnArr6 = jsonObject12.getJSONArray(
                                    "columns");
                            for (int a = 0; a < jsonArray12.length(); a++) {
                                JSONArray jsonArray13 =
                                        jsonArray12.getJSONArray(
                                                a);
                                String gradeId = jsonArray13.getString(
                                        getIndex(columnArr6, "id"));
                                String gradeName = jsonArray13.getString(
                                        getIndex(columnArr6, "grade_symbol"));
                                String gradeMinimum = jsonArray13.getString(
                                        getIndex(columnArr6, "start"));
                                String gradeRemark = jsonArray13.getString(
                                        getIndex(columnArr6, "remark"));
                                QueryBuilder<GradeModel, Long> queryBuilder =
                                        gradeDao.queryBuilder();
                                queryBuilder.where().eq("gradeId", gradeId);
                                gradeList = queryBuilder.query();
                                if (gradeList.isEmpty()) {
                                    GradeModel gm = new GradeModel();
                                    gm.setGradeId(gradeId);
                                    gm.setGradeMinimuim(gradeMinimum);
                                    gm.setGradeRemark(gradeRemark);
                                    gm.setGradeName(gradeName);
                                    gradeDao.create(gm);
                                }


                            }

                            JSONObject jsonObject15 = jsonObject.getJSONObject(
                                    "assessment");
                            JSONArray jsonArray15 = jsonObject15.getJSONArray(
                                    "rows");
                            JSONArray columnArr7 = jsonObject15.getJSONArray(
                                    "columns");
                            for (int c = 0; c < jsonArray15.length(); c++) {
                                JSONArray jsonArray16 =
                                        jsonArray15.getJSONArray(
                                                c);
                                String assessmentId = jsonArray16.getString(
                                        getIndex(columnArr7, "id"));
                                String assessmentName = jsonArray16.getString(
                                        getIndex(columnArr7, "assesment_name"));
                                String maxScore = jsonArray16.getString(
                                        getIndex(columnArr7, "max_score"));
                                String levelId = jsonArray16.getString(
                                        getIndex(columnArr7, "level"));
                                QueryBuilder<AssessmentModel, Long> queryBuilder =
                                        assessmentDao.queryBuilder();
                                queryBuilder.where().eq("assessmentId",
                                        assessmentId);
                                assessmentList = queryBuilder.query();
                                if (assessmentList.isEmpty()) {
                                    AssessmentModel as = new AssessmentModel();
                                    as.setAssessmentId(assessmentId);
                                    as.setAssessmentName(assessmentName);
                                    as.setLevel(levelId);
                                    as.setMaxScore(maxScore);
                                    assessmentDao.create(as);
                                }

                            }

                            JSONArray contentArray = jsonObject.getJSONArray(
                                    "content");

                            for (int a = 0; a < contentArray.length(); a++) {
                                JSONObject contentObj =
                                        contentArray.getJSONObject(
                                                a);
                                String id = contentObj.getString("id");
                                String week = contentObj.getString("rank");
                                String title = contentObj.getString("title");
                                String objective = contentObj.getString(
                                        "description");
                                String noteMaterialPath = contentObj.getString(
                                        "url");
                                String otherMatherialPath =
                                        contentObj.getString(
                                                "picref");
                                String levelID = contentObj.getString("level");
                                String courseID = contentObj.getString(
                                        "course_id");
                                String courseName = contentObj.getString(
                                        "course_name");
                                String levelName = contentObj.getString(
                                        "level_name");
                                String type = contentObj.getString("type");
                                String body = contentObj.getString("body");
                                String startDate = contentObj.optString(
                                        "start_date");
                                String endDate = contentObj.optString(
                                        "end_date");

                                CourseOutlineTable cot =
                                        new CourseOutlineTable();
                                cot.setLevelId(levelID);
                                cot.setCourseId(courseID);
                                cot.setTitle(title);
                                cot.setObjective(objective);
                                cot.setWeek("Week " + week);
                                cot.setId(id);
                                cot.setCourseName(courseName);
                                cot.setAssessmentUrl(noteMaterialPath);
                                cot.setLevelName(levelName);
                                cot.setNoteMaterialPath(noteMaterialPath);
                                cot.setOtherMatherialPath(otherMatherialPath);
                                cot.setType(type);
                                cot.setStartDate(startDate);
                                cot.setEndDate(endDate);
                                if (type.equals("2")) {
                                    cot.setDuration(objective);
                                    cot.setObjective(body);
                                }
                                courseOutlineDao.create(cot);

                            }

                            JSONObject jsonObject30 = jsonObject.getJSONObject(
                                    "teachersCourseClass");
                            JSONArray jsonArray30 = jsonObject30.getJSONArray(
                                    "rows");
                            JSONArray columnArr8 = jsonObject30.getJSONArray(
                                    "columns");
                            for (int s = 0; s < jsonArray30.length(); s++) {
                                JSONArray jsonArray31 =
                                        jsonArray30.getJSONArray(
                                                s);
                                String id = jsonArray31.getString(
                                        getIndex(columnArr8, "id"));
                                String courseId = jsonArray31.getString(
                                        getIndex(columnArr8, "course"));
                                String classId = jsonArray31.getString(
                                        getIndex(columnArr8, "class"));
                                String staffId = jsonArray31.getString(
                                        getIndex(columnArr8, "ref_no"));
                                String className = jsonArray31.getString(
                                        getIndex(columnArr8, "class_name"));
                                String courseName = jsonArray31.getString(
                                        getIndex(columnArr8, "course_name"));

                                TeacherCourseModel tcm =
                                        new TeacherCourseModel();
                                tcm.setClassId(classId);
                                tcm.setCourseId(courseId);
                                tcm.setStaffId(staffId);
                                tcm.setId(id);
                                tcm.setClassName(className);
                                tcm.setCourseName(courseName);
                                teacherCourseDao.create(tcm);
                            }

                            JSONObject jsonObject130 = jsonObject.getJSONObject(
                                    "formClasses");
                            JSONArray jsonArray130 = jsonObject130.getJSONArray(
                                    "rows");
                            JSONArray columnArr9 = jsonObject130.getJSONArray(
                                    "columns");
                            for (int a = 0; a < jsonArray130.length(); a++) {
                                JSONArray jsonArray1 =
                                        jsonArray130.getJSONArray(
                                                a);
                                String className = jsonArray1.getString(
                                        getIndex(columnArr9, "class_name"));
                                String staffId = jsonArray1.getString(
                                        getIndex(columnArr9, "form_teacher"));
                                FormClassModel fcm = new FormClassModel();
                                fcm.setClassName(className);
                                fcm.setStaffId(staffId);
                                formClassDao.create(fcm);
                            }


                            JSONObject jsonObject20 = jsonObject.getJSONObject(
                                    "schoolProfile");
                            JSONArray jsonArray20 = jsonObject20.getJSONArray(
                                    "rows");

                            Log.i("assessment", jsonObject20.toString());

                            JSONArray columnArr10 = jsonObject20.getJSONArray(
                                    "columns");
                            JSONArray schlProfileObject =
                                    jsonArray20.getJSONArray(
                                            getIndex(columnArr10, "id"));
                            String school_Name = schlProfileObject.getString(
                                    getIndex(columnArr10, "name"));
                            String schoolYear = schlProfileObject.getString(
                                    getIndex(columnArr10, "year"));
                            String term = schlProfileObject.getString(
                                    getIndex(columnArr10, "term"));
                            String website = schlProfileObject.getString(
                                    getIndex(columnArr10, "website"));
                            String schoolShortName =
                                    schlProfileObject.getString(
                                            getIndex(columnArr10, "short_name"));
                            String schoolAddress = schlProfileObject.getString(
                                    getIndex(columnArr10, "address"));
                            String city = schlProfileObject.getString(
                                    getIndex(columnArr10, "city"));
                            String state = schlProfileObject.getString(
                                    getIndex(columnArr10, "state"));
                            String country = schlProfileObject.getString(
                                    getIndex(columnArr10, "country"));
                            String schoolEmail = schlProfileObject.getString(
                                    getIndex(columnArr10, "email"));
                            String schoolPhoneNumber =
                                    schlProfileObject.getString(
                                            getIndex(columnArr10, "phone"));
                            String studentPrefix = schlProfileObject.getString(
                                    getIndex(columnArr10, "student_prefix"));
                            String staffPrefix = schlProfileObject.getString(
                                    getIndex(columnArr10, "staff_prefix"));
                            String alumniPrefix = schlProfileObject.getString(
                                    getIndex(columnArr10, "alumni_prefix"));
                            GeneralSettingModel gm = new GeneralSettingModel();
                            gm.setSchoolName(school_Name);
                            gm.setSchoolYear(schoolYear);
                            gm.setSchoolTerm(term);
                            gm.setWebsite(website);
                            gm.setShcoolShortName(schoolShortName);
                            gm.setSchoolAddress(schoolAddress);
                            gm.setCity(city);
                            gm.setState(state);
                            gm.setCountry(country);
                            gm.setSchoolEmail(schoolEmail);
                            gm.setSchoolPhone(schoolPhoneNumber);
                            gm.setStudentPrefix(studentPrefix);
                            gm.setStaffPrefix(staffPrefix);
                            gm.setAlumniPrefix(alumniPrefix);
                            generalSettingDao.create(gm);


                            JSONObject jsonObject10 = jsonObject.getJSONObject(
                                    "profile");

                            String user = jsonObject10.getString("name");
                            String schoolName = jsonObject10.getString(
                                    "school_name");
                            String userId = jsonObject10.getString("id");
                            String accessLevel = jsonObject10.getString(
                                    "access_level");
                            String db = jsonObject10.getString("_db");
                            SharedPreferences sharedPreferences =
                                    getSharedPreferences(
                                            "loginDetail", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor =
                                    sharedPreferences.edit();
                            editor.putBoolean("loginStatus", true);
                            editor.putString("user", user);
                            editor.putString("school_name", schoolName);
                            editor.putString("user_id", userId);
                            editor.putString("username", usernameText);
                            editor.putString("userpassword", passwordText);
                            editor.putString("schoolcode", pinText);
                            editor.putString("school_year", schoolYear);
                            editor.putString("access_level", accessLevel);
                            editor.putString("who", "admin");
                            editor.putString("term", term);
                            editor.putBoolean("hide", false);
                            editor.putString("db", db);
                            Log.i("response_db", db);
                            editor.apply();
                            Intent intent = new Intent(Login.this,
                                    Dashboard.class);
                            intent.putExtra("isFromLogin", true);
                            startActivity(intent);
                            finish();

                        }

                    } else if (jsonObject.getJSONObject("profile").getString(
                            "access_level").equals(
                            "3") || jsonObject.getJSONObject(
                            "profile").getString("access_level").equals("1")) {
                        if (jsonObject.has("teacherStudents")) {

                            JSONObject object = jsonObject.getJSONObject(
                                    "teacherStudents");
                            JSONArray jsonArray = object.getJSONArray("rows");
                            JSONArray columnArr11 = object.getJSONArray(
                                    "columns");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONArray jsonArray1 = jsonArray.getJSONArray(
                                        i);
                                String id = jsonArray1.getString(
                                        getIndex(columnArr11, "id"));
                                String studentSurname = jsonArray1.getString(
                                        getIndex(columnArr11, "surname"));
                                String studentFirstname =
                                        jsonArray1.getString(
                                                getIndex(columnArr11,
                                                        "first_name"));
                                String studentMiddlename = jsonArray1.getString(
                                        getIndex(columnArr11, "middle"));
                                String studentGender;
                                if (jsonArray1.getString(
                                        getIndex(columnArr11, "sex")).equals(
                                        "m")) {
                                    studentGender = "Male";
                                } else if (jsonArray1.getString(
                                        getIndex(columnArr11, "sex")).equals(
                                        "f")) {
                                    studentGender = "Female";

                                } else {
                                    studentGender = jsonArray1.getString(
                                            getIndex(columnArr11, "sex"));
                                }
                                String studentReg_no = jsonArray1.getString(
                                        getIndex(columnArr11,
                                                "registration_no"));
                                String studentClass = jsonArray1.getString(
                                        getIndex(columnArr11, "student_class"));
                                String studentLevel = jsonArray1.getString(
                                        getIndex(columnArr11, "student_level"));
                                String studentDOB = jsonArray1.getString(
                                        getIndex(columnArr11, "birthdate"));
                                String guardianName = jsonArray1.getString(
                                        getIndex(columnArr11, "guardian_name"));
                                String guardianAddress = jsonArray1.getString(
                                        getIndex(columnArr11,
                                                "guardian_address"));
                                String guardianEmail = jsonArray1.getString(
                                        getIndex(columnArr11,
                                                "guardian_email"));
                                String guardianPhoneNo = jsonArray1.getString(
                                        getIndex(columnArr11,
                                                "guardian_phone_no"));
                                String lga = jsonArray1.getString(
                                        getIndex(columnArr11,
                                                "local_government_origin"));
                                String state_of_origin = jsonArray1.getString(
                                        getIndex(columnArr11, "state_origin"));
                                String nationality = jsonArray1.getString(
                                        getIndex(columnArr11, "nationality"));
                                String date_admitted = jsonArray1.getString(
                                        getIndex(columnArr11, "date_admitted"));
                                //String class_name = jsonArray1.getString(28);
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


                            JSONObject jsonObject3 = jsonObject.getJSONObject(
                                    "className");
                            JSONArray jsonArray4 = jsonObject3.getJSONArray(
                                    "rows");
                            JSONArray columnArr12 = jsonObject3.getJSONArray(
                                    "columns");

                            for (int i = 0; i < jsonArray4.length(); i++) {
                                JSONArray jsonArray5 = jsonArray4.getJSONArray(
                                        i);
                                String classId = jsonArray5.getString(
                                        getIndex(columnArr12, "id"));
                                String className = jsonArray5.getString(
                                        getIndex(columnArr12, "class_name"));
                                String level = jsonArray5.getString(
                                        getIndex(columnArr12, "level"));
                                QueryBuilder<ClassNameTable, Long> queryBuilder =
                                        classDao.queryBuilder();
                                queryBuilder.where().eq("classId", classId);
                                classnames = queryBuilder.query();
                                if (classnames.isEmpty()) {
                                    ClassNameTable cn = new ClassNameTable();
                                    cn.setClassId(classId);
                                    cn.setClassName(className);
                                    cn.setLevel(level);
                                    classDao.create(cn);
                                }

                            }

                            JSONObject jsonObject6 = jsonObject.getJSONObject(
                                    "levelName");
                            JSONArray jsonArray7 = jsonObject6.getJSONArray(
                                    "rows");
                            JSONArray columnArr13 = jsonObject6.getJSONArray(
                                    "columns");

                            for (int i = 0; i < jsonArray7.length(); i++) {
                                JSONArray jsonArray8 = jsonArray7.getJSONArray(
                                        i);
                                String levelId = jsonArray8.getString(
                                        getIndex(columnArr13, "id"));
                                String levelName = jsonArray8.getString(
                                        getIndex(columnArr13, "level_name"));
                                String schoolType = jsonArray8.getString(
                                        getIndex(columnArr13, "school_type"));
                                QueryBuilder<LevelTable, Long> queryBuilder =
                                        levelDao.queryBuilder();
                                queryBuilder.where().eq("levelId", levelId);
                                levelNames = queryBuilder.query();
                                if (levelNames.isEmpty()) {
                                    LevelTable lt = new LevelTable();
                                    lt.setLevelId(levelId);
                                    lt.setLevelName(levelName);
                                    lt.setSchoolType(schoolType);
                                    levelDao.create(lt);
                                }

                            }

                            JSONObject jsonObject8 = jsonObject.getJSONObject(
                                    "news");
                            JSONArray jsonArray9 = jsonObject8.getJSONArray(
                                    "rows");
                            JSONArray columnArr14 = jsonObject8.getJSONArray(
                                    "columns");

                            for (int i = 0; i < jsonArray9.length(); i++) {
                                JSONArray jsonArray10 = jsonArray9.getJSONArray(
                                        i);
                                String newsId = jsonArray10.getString(
                                        getIndex(columnArr14, "id"));
                                String newsSubject = jsonArray10.getString(
                                        getIndex(columnArr14, "subject"));
                                String newsContent = jsonArray10.getString(
                                        getIndex(columnArr14, "content"));
                                String datePosted = jsonArray10.getString(
                                        getIndex(columnArr14, "date_posted"));
                                String newsImageUrl = jsonArray10.getString(
                                        getIndex(columnArr14, "pic_ref"));
                                QueryBuilder<NewsTable, Long> queryBuilder =
                                        newsDao.queryBuilder();
                                queryBuilder.where().eq("newsId", newsId);
                                newsList = queryBuilder.query();
                                if (newsList.isEmpty()) {
                                    NewsTable nt = new NewsTable();
                                    nt.setNewsId(newsId);
                                    nt.setNewsSubject(stripHtml(newsSubject));
                                    nt.setNewsContent(stripHtml(newsContent));
                                    nt.setDatePosted(stripHtml(datePosted));
                                    nt.setNewsImageUrl(stripHtml(newsImageUrl));
                                    newsDao.create(nt);
                                }
                            }


                            JSONObject jsonObject10 = jsonObject.getJSONObject(
                                    "profile");
                            String user = jsonObject10.getString("name");
                            String userId = jsonObject10.getString("id");
                            String accessLevel = jsonObject10.getString(
                                    "access_level");
                            String db = jsonObject10.getString("_db");
                            SharedPreferences sharedPreferences =
                                    getSharedPreferences(
                                            "loginDetail", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor =
                                    sharedPreferences.edit();
                            editor.putBoolean("loginStatus", true);
                            editor.putString("user", user);
                            editor.putString("user_id", userId);
                            editor.putString("username", usernameText);
                            editor.putString("userpassword", passwordText);
                            editor.putString("schoolcode", pinText);
                            editor.putString("access_level", accessLevel);
                            editor.putString("who", "staff");
                            editor.putString("db", db);


                            JSONObject jsonObject11 = jsonObject.getJSONObject(
                                    "schoolProfile");
                            JSONArray jsonArray11 = jsonObject11.getJSONArray(
                                    "rows");
                            JSONArray columnArr15 = jsonObject11.getJSONArray(
                                    "columns");
                            String schoolYear = jsonArray11.getJSONArray(
                                    0).getString(getIndex(columnArr15, "year"));
                            String currentTerm = jsonArray11.getJSONArray(
                                    0).getString(getIndex(columnArr15, "term"));
                            String schoolName = jsonArray11.getJSONArray(
                                    0).getString(getIndex(columnArr15, "name"));
                            editor.putString("school_year", schoolYear);
                            editor.putString("school_name", schoolName);
                            editor.putString("term", currentTerm);
                            editor.apply();

                            JSONArray jsonArray1 = jsonObject.getJSONArray(
                                    "staffCourse");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject1 =
                                        jsonArray1.getJSONObject(
                                                i);
                                String courseId = jsonObject1.getString("id");
                                String staffNo = jsonObject1.getString(
                                        "ref_no");
                                String classId = jsonObject1.getString("class");
                                String year = jsonObject1.getString("year");
                                String term = jsonObject1.getString("term");
                                String levelId = jsonObject1.getString("level");
                                String courseName = jsonObject1.getString(
                                        "course_name");
                                String className = jsonObject1.getString(
                                        "class_name");
                                String levelName = jsonObject1.getString(
                                        "level_name");
                                CourseTable ct = new CourseTable();
                                ct.setClassId(classId);
                                ct.setCourseId(courseId);
                                ct.setCourseName(courseName);
                                ct.setCourseTerm(term);
                                ct.setCourseYear(year);
                                ct.setStaffNo(staffNo);
                                ct.setClassName(className);
                                ct.setLevelName(levelName);
                                ct.setLevelId(levelId);
                                courseDao.create(ct);
                            }

                            JSONArray staffContentArray =
                                    jsonObject.getJSONArray(
                                            "staffContent");
                            for (int a = 0; a < staffContentArray.length(); a++) {
                                JSONObject contentObj =
                                        staffContentArray.getJSONObject(
                                                a);
                                String id = contentObj.getString("id");
                                String week = contentObj.getString("rank");
                                String title = contentObj.getString("title");
                                String objective = contentObj.getString(
                                        "description");
                                String noteMaterialPath = contentObj.getString(
                                        "url");
                                String otherMatherialPath =
                                        contentObj.getString(
                                                "picref");
                                String levelID = contentObj.getString("level");
                                String courseID = contentObj.getString(
                                        "course_id");
                                String type = contentObj.getString("type");
                                String startDate = contentObj.optString(
                                        "start_date");
                                String endDate = contentObj.optString(
                                        "end_date");

                                CourseOutlineTable cot =
                                        new CourseOutlineTable();
                                cot.setLevelId(levelID);
                                cot.setCourseId(courseID);
                                cot.setTitle(title);
                                cot.setObjective(objective);
                                cot.setWeek("Week " + week);
                                cot.setId(id);
                                cot.setType(type);
                                cot.setStartDate(startDate);
                                cot.setEndDate(endDate);
                                cot.setAssessmentUrl(noteMaterialPath);
                                cot.setNoteMaterialPath(noteMaterialPath);
                                cot.setOtherMatherialPath(otherMatherialPath);
                                courseOutlineDao.create(cot);

                            }
                            Intent intent = new Intent(Login.this,
                                    StaffDashboardActivity.class);
                            intent.putExtra("isFromLogin", true);
                            startActivity(intent);
                            finish();

                        }

                    } else if (jsonObject.getJSONObject("profile").getString(
                            "access_level").equals("-1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject(
                                "profile");
                        String user = jsonObject1.getString("name");
                        String schoolName = jsonObject1.getString(
                                "school_name");
                        String userId = jsonObject1.getString("id");
                        String accessLevel = jsonObject1.getString(
                                "access_level");
                        String db = jsonObject1.getString("_db");
                        String studentClass = jsonObject1.getString(
                                "class_name");
                        String level = jsonObject1.getString("level");
                        String classId1 = jsonObject1.getString("student_class");
                        String studentRegNo = jsonObject1.getString("registration_no");
                        String studentEmail = jsonObject.optString("guardian_email", "");


                        SharedPreferences sharedPreferences = getSharedPreferences(
                                "loginDetail", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("loginStatus", true);
                        editor.putString("user", user);
                        editor.putString("school_name", schoolName);
                        editor.putString("user_id", userId);
                        editor.putString("username", usernameText);
                        editor.putString("userpassword", passwordText);
                        editor.putString("schoolcode", pinText);
                        editor.putString("access_level", accessLevel);
                        editor.putString("who", "student");
                        editor.putString("db", db);
                        editor.putString("student_class", studentClass);
                        editor.putString("level", level);
                        editor.putString("classId", classId1);
                        editor.putString("student_reg_no", studentRegNo);
                        editor.putString("student_email", studentEmail);

                        Object object = jsonObject.get("result");
                        if (object instanceof JSONObject) {
                            JSONObject jsonObject2 = jsonObject.getJSONObject("result");
                            Iterator<String> keys = jsonObject2.keys();

                            while (keys.hasNext()) {
                                String key = keys.next();
                                if (jsonObject2.get(key) instanceof JSONObject) {
                                    // do something with jsonObject here
                                    JSONObject object1 = jsonObject2.getJSONObject(
                                            key);
                                    String className = object1.getString(
                                            "class_name");
                                    String levelID = object1.getString("level");
                                    JSONObject termObject = object1.getJSONObject(
                                            "terms");
                                    String first_term = "";
                                    String second_term = "";
                                    String third_term = "";

                                    if (termObject.has("1")) {
                                        first_term = "1st";
                                    }
                                    if (termObject.has("2")) {
                                        second_term = "2nd";
                                    }
                                    if (termObject.has("3")) {
                                        third_term = "3rd";
                                    }


                                    StudentResultDownloadTable st =
                                            new StudentResultDownloadTable();
                                    st.setFirstTerm(first_term);
                                    st.setSecondTerm(second_term);
                                    st.setThirdTerm(third_term);
                                    st.setLevel(levelID);
                                    st.setStudentId(userId);
                                    st.setLevelName(className);
                                    st.setSchoolYear(key);
                                    studentResultDao.create(st);
                                }
                            }
                        }


                        JSONObject jsonObject21 = jsonObject.getJSONObject(
                                "news");
                        JSONArray newsArray = jsonObject21.getJSONArray("rows");
                        JSONArray columnArr15 = jsonObject21.getJSONArray(
                                "columns");
                        for (int e = 0; e < newsArray.length(); e++) {
                            JSONArray jsonArray21 = newsArray.getJSONArray(e);
                            String newsId = jsonArray21.getString(
                                    getIndex(columnArr15, "id"));
                            String newsSubject = jsonArray21.getString(
                                    getIndex(columnArr15, "subject"));
                            String newsContent = jsonArray21.getString(
                                    getIndex(columnArr15, "content"));
                            String datePosted = jsonArray21.getString(
                                    getIndex(columnArr15, "date_posted"));
                            String newsImageUrl = jsonArray21.getString(
                                    getIndex(columnArr15, "pic_ref"));
                            QueryBuilder<NewsTable, Long> queryBuilder = newsDao.queryBuilder();
                            queryBuilder.where().eq("newsId", newsId);
                            newsList = queryBuilder.query();
                            if (newsList.isEmpty()) {
                                NewsTable nt = new NewsTable();
                                nt.setNewsId(newsId);
                                nt.setNewsSubject(stripHtml(newsSubject));
                                nt.setNewsContent(stripHtml(newsContent));
                                nt.setDatePosted(stripHtml(datePosted));
                                nt.setNewsImageUrl(stripHtml(newsImageUrl));
                                newsDao.create(nt);
                            }

                        }

                        JSONArray jsonArray1 = jsonObject.getJSONArray(
                                "courses");
                        for (int a = 0; a < jsonArray1.length(); a++) {
                            JSONObject jsonObject3 = jsonArray1.getJSONObject(
                                    a);
                            String courseId = jsonObject3.getString("course");
                            String classId = jsonObject3.getString("class");
                            String courseName = jsonObject3.getString(
                                    "course_name");
                            StudentCourses sc = new StudentCourses();
                            sc.setClassId(classId);
                            sc.setCourseId(courseId);
                            sc.setCourseName(courseName);
                            studentCourseDao.create(sc);
                        }

                        JSONArray contentArray = jsonObject.getJSONArray(
                                "content");
                        for (int a = 0; a < contentArray.length(); a++) {
                            JSONObject contentObj = contentArray.getJSONObject(
                                    a);
                            String id = contentObj.getString("id");
                            String week = contentObj.getString("rank");
                            String title = contentObj.getString("title");
                            String objective = contentObj.getString(
                                    "description");
                            String noteMaterialPath = contentObj.getString(
                                    "url");
                            String otherMatherialPath = contentObj.getString(
                                    "picref");
                            String levelID = contentObj.getString("level");
                            String courseID = contentObj.getString("course_id");
                            String courseName = contentObj.getString(
                                    "course_name");
                            String levelName = contentObj.getString(
                                    "level_name");
                            String type = contentObj.getString("type");
                            String startDate = contentObj.optString(
                                    "start_date");
                            String endDate = contentObj.optString("end_date");

                            CourseOutlineTable cot = new CourseOutlineTable();
                            cot.setLevelId(levelID);
                            cot.setCourseId(courseID);
                            cot.setTitle(title);
                            cot.setObjective(objective);
                            cot.setWeek("Week " + week);
                            cot.setId(id);
                            cot.setEndDate(endDate);
                            cot.setStartDate(startDate);
                            cot.setCourseName(courseName);
                            cot.setLevelName(levelName);
                            cot.setNoteMaterialPath(noteMaterialPath);
                            cot.setOtherMatherialPath(otherMatherialPath);
                            cot.setAssessmentUrl(noteMaterialPath);
                            cot.setType(type);
                            courseOutlineDao.create(cot);

                        }

                        JSONObject settingsObject = jsonObject.getJSONObject("settings");
                        String studentYear = settingsObject.getString("year");
                        String studentTerm = settingsObject.getString("term");
                        editor.putString("year", studentYear);
                        editor.putString("term", studentTerm);
                        editor.apply();


                        Intent intent = new Intent(Login.this,
                                StudentDashboardActivity.class);
                        intent.putExtra("isFromLogin", true);
                        startActivity(intent);
                        finish();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Login.this, "Error fetching data from server",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
}