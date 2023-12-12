package com.digitaldream.winskool.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.models.ClassNameTable;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.models.StudentTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AddStudent extends AppCompatActivity {
    private EditText surname, firstname, middlename, registrationNumber, guardianName, guardianAddress, guardianEmail, guardianPhone, birthday,levelEditext,classEdittext;
    private String surnameText, firstnameText, middlenameText, registrationNumberText, guardianNameText, guardianAddressText, guardianEmailText, guardianPhoneText, birthdayText, genderText, statOriginText, studentLevelText, studentClassText;
    private Spinner gender, state_of_origin, student_class, student_level;
    private Button submit;
    private DatePickerDialog datePickerDialog;
    private List<String> spinnerLevelList, spinnerClassList, genderList, states;
    private DatabaseHelper databaseHelper;
    private Dao<ClassNameTable, Long> classDao;
    private List<ClassNameTable> classList;
    private Dao<LevelTable, Long> levelDao;
    private List<LevelTable> levelList;
    private Dao<StudentTable, Long> studentDao;
    private List<StudentTable> student;
    private Toolbar toolbar;
    private String db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        surname = findViewById(R.id.new_student_name);
        firstname = findViewById(R.id.new_student_firstname);
        middlename = findViewById(R.id.new_student_middlename);
        birthday = findViewById(R.id.new_student_birthday);
        registrationNumber = findViewById(R.id.new_student_reg_no);
        guardianName = findViewById(R.id.new_student_guardian_name);
        guardianAddress = findViewById(R.id.new_student_guardian_address);
        guardianEmail = findViewById(R.id.new_student_guardian_email);
        guardianPhone = findViewById(R.id.new_student_guardian_phone_no);
        classEdittext = findViewById(R.id.new_student_class);
        levelEditext = findViewById(R.id.new_student_level);
        gender = findViewById(R.id.spinner_new_student_gender);
        state_of_origin = findViewById(R.id.spinner_level_student_state_origin);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Add New Student");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(view -> validateInput());



        databaseHelper = new DatabaseHelper(this);
        try {
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(), ClassNameTable.class);
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(), LevelTable.class);
            studentDao = DaoManager.createDao(databaseHelper.getConnectionSource(), StudentTable.class);
            QueryBuilder<LevelTable,Long> queryBuilder = levelDao.queryBuilder();
            queryBuilder.where().eq("levelId", StudentContacts.studentLevelId);
            levelList = queryBuilder.query();

            QueryBuilder<ClassNameTable,Long> queryBuilder1 = classDao.queryBuilder();
            queryBuilder1.where().eq("classId",StudentContacts.studentClass);
            classList = queryBuilder1.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        birthday.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePickerDialog = new DatePickerDialog(AddStudent.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    birthday.setText(i + "-" + (i1 + 1) + "-" + i2);
                }
            }, year, month, day);
            datePickerDialog.show();
        });

        spinnerLevelList = new ArrayList<>();
        spinnerClassList = new ArrayList<>();
        genderList = new ArrayList<>();
        states = new ArrayList<>();


        for (int a = 0; a < levelList.size(); a++) {
            spinnerLevelList.add(levelList.get(a).getLevelName().toUpperCase());
        }


        genderList.add("Select gender");
        genderList.add("Male");
        genderList.add("Female");

        states.add("Select state");
        states.add("Abia");
        states.add("Adamawa");
        states.add("Akwa Ibom");
        states.add("Anambra");
        states.add("Bauchi");
        states.add("Bayelsa");
        states.add("Benue");
        states.add("Borno");
        states.add("Cross River");
        states.add("Delta");
        states.add("Ebonyi");
        states.add("Enugu");
        states.add("Edo");
        states.add("Ekiti");
        states.add("Gombe");
        states.add("Imo");
        states.add("Jigawa");
        states.add("Kaduna");
        states.add("Kano");
        states.add("Katsina");
        states.add("Kebbi");
        states.add("Kogi");
        states.add("Kwara");
        states.add("Lagos");
        states.add("Nasarawa");
        states.add("Niger");
        states.add("Ogun");
        states.add("Ondo");
        states.add("Osun");
        states.add("Oyo");
        states.add("Plateau");
        states.add("Rivers");
        states.add("Sokoto");
        states.add("Taraba");
        states.add("Yobe");
        states.add("Zamfara");






        ArrayAdapter adapterGender = new ArrayAdapter(this, R.layout.spinner_item, genderList);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapterGender);

        ArrayAdapter adapterState = new ArrayAdapter(this, R.layout.spinner_item, states);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state_of_origin.setAdapter(adapterState);

        levelEditext.setText(levelList.get(0).getLevelName().toUpperCase());
        classEdittext.setText(classList.get(0).getClassName().toUpperCase());
        levelEditext.setEnabled(false);
        classEdittext.setEnabled(false);
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    private boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    private void callAddStudentApi() {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(AddStudent.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");
        String url = Login.urlBase+"/register.php?surname="+surnameText+"&first_name="+firstnameText+"&middle="+middlenameText+"&sex="+genderText+"&birthdate="+birthdayText+"&guardian_address="+guardianAddressText+"&state_origin="+statOriginText+"&guardian_phone_no="+guardianPhoneText+"&guardian_email="+guardianEmailText+"&guardian_name="+guardianNameText+"&registration_no="+registrationNumberText+"&student_class="+StudentContacts.studentClass+"&student_level="+StudentContacts.studentLevelId+"&_db="+db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog1.dismiss();
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("records");

                        String studentId = jsonObject1.getString("id");
                        String studentSurname = jsonObject1.getString("surname");
                        String studentFirstname = jsonObject1.getString("first_name");
                        String studentMiddleName = jsonObject1.getString("middle");
                        String studentGender = jsonObject1.getString("sex");
                        String studentBirthday = jsonObject1.getString("birthdate");
                        String student_reg_no = jsonObject1.getString("registration_no");
                        String guardianName = jsonObject1.getString("guardian_name");
                        String guardianAddress = jsonObject1.getString("guardian_address");
                        String guardianEmail = jsonObject1.getString("guardian_email");
                        String guardianPhone = jsonObject1.getString("guardian_phone_no");
                        String stateOrigin = jsonObject1.getString("state_origin");
                        String studentClass = jsonObject1.getString("student_class");
                        String studentLevel = jsonObject1.getString("student_level");

                        QueryBuilder<StudentTable, Long> queryBuilder = studentDao.queryBuilder();
                        queryBuilder.where().eq("studentId", studentId);
                        student = queryBuilder.query();

                        if (student.isEmpty()) {
                            StudentTable st = new StudentTable();
                            st.setStudentId(studentId);
                            st.setStudentSurname(studentSurname);
                            st.setStudentFirstname(studentFirstname);
                            st.setStudentMiddlename(studentMiddleName);
                            st.setStudentGender(studentGender);
                            st.setStudentReg_no(student_reg_no);
                            st.setStudentClass(studentClass);
                            st.setStudentLevel(studentLevel);
                            st.setGuardianName(guardianName);
                            st.setGuardianAddress(guardianAddress);
                            st.setGuardianEmail(guardianEmail);
                            st.setGuardianPhoneNo(guardianPhone);
                            st.setState_of_origin(stateOrigin);
                            st.setDate_of_birth(studentBirthday);
                            studentDao.create(st);
                            Toast.makeText(AddStudent.this,"Operation was successful",Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    } else if (status.equals("failed")) {
                        Toast.makeText(AddStudent.this, "Operation was unsuccessful", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception ignored) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }); /*{
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("_db","linkskoo_newoceans");
                params.put("surname", surnameText);
                params.put("first_name", firstnameText);
                params.put("sex", genderText);
                params.put("birthdate", birthdayText);
                params.put("registration_no", registrationNumberText);
                params.put("guardian_name", guardianNameText);
                params.put("guardian_email", guardianEmailText);
                params.put("guardian_address", guardianAddressText);
                params.put("guardian_phone_no", guardianPhoneText);
                params.put("state_origin", statOriginText);
                params.put("student_level", studentLevelText);
                params.put("student_class", studentClassText);
                return params;
            }*/



        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void validateInput() {
        surnameText = surname.getText().toString();
        firstnameText = firstname.getText().toString();
        middlenameText = middlename.getText().toString();
        registrationNumberText = registrationNumber.getText().toString();
        guardianAddressText = guardianAddress.getText().toString();
        guardianNameText = guardianName.getText().toString();
        guardianEmailText = guardianEmail.getText().toString();
        guardianPhoneText = guardianPhone.getText().toString();
        birthdayText = birthday.getText().toString();
        genderText = "";
        statOriginText = "";
        studentLevelText = "";
        studentClassText = "";
        if (gender.getSelectedItemPosition() != 0) {
            genderText = gender.getSelectedItem().toString();
        }
        if (state_of_origin.getSelectedItemPosition() != 0) {
            statOriginText = state_of_origin.getSelectedItem().toString();
        }

        if (surnameText.isEmpty() || firstnameText.isEmpty() || registrationNumberText.isEmpty() || guardianAddressText.isEmpty() ||
                guardianNameText.isEmpty() || guardianEmailText.isEmpty() || guardianPhoneText.isEmpty() || birthdayText.isEmpty() ||
                genderText.isEmpty() || statOriginText.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Fill out the form correctly");
            builder.setPositiveButton("OK", (dialogInterface, i) -> validateEditTextAndSpinner());
            builder.show();
        } else if (!isValid(guardianEmailText)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Email address is not valid");
            builder.setPositiveButton("OK", (dialogInterface, i) -> {

            });
            builder.show();
        } else {
            callAddStudentApi();
        }
    }

    private void setClassSpinnerItems(String levelId) {
        spinnerClassList.clear();
        try {
            QueryBuilder<ClassNameTable, Long> queryBuilder = classDao.queryBuilder();
            queryBuilder.where().eq("level", levelId);
            classList = queryBuilder.query();
            for (int i = 0; i < classList.size(); i++) {
                spinnerClassList.add(classList.get(i).getClassName());
            }

            ArrayAdapter adapterClass = new ArrayAdapter(this, R.layout.spinner_item, spinnerClassList);
            adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            student_class.setAdapter(adapterClass);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void validateEditTextAndSpinner() {
        EditText[] editTextCollection = {surname, firstname, middlename, registrationNumber, guardianName, guardianAddress, guardianEmail, guardianPhone};
        for (EditText anEditTextCollection : editTextCollection) {
            if (anEditTextCollection.getText().toString().equals("")) {
                anEditTextCollection.setError("Field is required");
            }
        }

        if (birthday.getText().toString().equals("")) {
            birthday.setError("Field is required");
        }
        //gender, state_of_origin, student_class, student_level;
        if (genderText.equals("")) {
            TextView genderText1 = (TextView) gender.getSelectedView();
            genderText1.setError("field is required");
        }

        if(statOriginText.equals("")){
            TextView stateOrigin = (TextView) state_of_origin.getSelectedView();
            stateOrigin.setError("field is required");
        }


    }
}