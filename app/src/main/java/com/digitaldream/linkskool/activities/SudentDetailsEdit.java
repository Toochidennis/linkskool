package com.digitaldream.linkskool.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class SudentDetailsEdit extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText surname, firstname, middlename, birthday, reg_no, guardianName, guardianAddress, guardianEmail, guardianPhoneNo;
    private String surnameText,firstnameText,middlenameText,registrationNumberText,guardianNameText,guardianAddressText,guardianEmailText,guardianPhoneText,birthdayText,genderText,statOriginText,studentLevelText,studentClassText;
    private Spinner gender, stateOrigin, studentLevel, studentClass;
    private List<String> spinnerLevelList, spinnerClassList, genderList, states;
    private DatabaseHelper databaseHelper;
    private Dao<ClassNameTable, Long> classDao;
    private List<ClassNameTable> classList;
    private Dao<LevelTable, Long> levelDao;
    private List<LevelTable> levelList;
    private Button saveBtn;
    private Dao<StudentTable,Long> studentDao;
    private List<StudentTable> student;
    private StudentTable st;
    private DatePickerDialog datePickerDialog;
    private String db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudent_details_edit);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Edit");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        Intent i = getIntent();
        st = (StudentTable) i.getSerializableExtra("studentObject");
        try {
            databaseHelper = new DatabaseHelper(this);
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(), ClassNameTable.class);
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(), LevelTable.class);
            studentDao = DaoManager.createDao(databaseHelper.getConnectionSource(),StudentTable.class);
            classList = classDao.queryForAll();
            levelList = levelDao.queryForAll();
            QueryBuilder<StudentTable,Long> queryBuilder = studentDao.queryBuilder();
            queryBuilder.where().eq("studentId",st.getStudentId());
            student = queryBuilder.query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");



        surname = findViewById(R.id.edit_student_name);
        firstname = findViewById(R.id.edit_student_firstname);
        middlename = findViewById(R.id.edit_student_middlename);
        birthday = findViewById(R.id.edit_student_birthday);
        reg_no = findViewById(R.id.edit_student_reg_no);
        guardianName = findViewById(R.id.edit_student_guardian_name);
        guardianAddress = findViewById(R.id.edit_student_guardian_address);
        guardianEmail = findViewById(R.id.edit_student_guardian_email);
        guardianPhoneNo = findViewById(R.id.edit_student_guardian_phone_no);
        gender = findViewById(R.id.spinner_edit_student_gender);
        stateOrigin = findViewById(R.id.edit_spinner_level_student_state_origin);
        studentClass = findViewById(R.id.edit_spinner_level_student_class);
        studentLevel = findViewById(R.id.edit_spinner_level_student_level);
        saveBtn = findViewById(R.id.edit_submit);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });

        if(!student.get(0).getStudentSurname().isEmpty()) {
            surname.setText(student.get(0).getStudentSurname().toUpperCase());
        }
        if(!student.get(0).getStudentFirstname().isEmpty()) {
            firstname.setText(student.get(0).getStudentFirstname().toUpperCase());
        }
        if(!student.get(0).getStudentMiddlename().isEmpty()) {
            middlename.setText(student.get(0).getStudentMiddlename().toUpperCase());
        }
        if(!student.get(0).getStudentReg_no().isEmpty()) {
            reg_no.setText(student.get(0).getStudentReg_no().toUpperCase());
        }
        if(!student.get(0).getGuardianName().isEmpty()) {
            guardianName.setText(student.get(0).getGuardianName().toUpperCase());
        }
        if(!student.get(0).getGuardianAddress().isEmpty()) {
            guardianAddress.setText(student.get(0).getGuardianAddress().toUpperCase());
        }
        if(!student.get(0).getGuardianEmail().isEmpty()) {
            guardianEmail.setText(student.get(0).getGuardianEmail().toUpperCase());
        }
        if(!student.get(0).getGuardianPhoneNo().isEmpty()) {
            guardianPhoneNo.setText(student.get(0).getGuardianPhoneNo().toUpperCase());
        }
        if(!student.get(0).getDate_of_birth().isEmpty()) {
            birthday.setText(student.get(0).getDate_of_birth().toUpperCase());
        }
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Calendar calendar = Calendar.getInstance();
                int day,month,year;
                String date = birthday.getText().toString().trim();
                if (date != null && date.length() > 0) {

                    String data[] = date.split("-");
                    year = Integer.parseInt(data[0]);

                    month = Integer.parseInt(data[1]);
                    month = month - 1;
                    day = Integer.parseInt(data[2]);
                } else {

                    year = calendar.get(Calendar.YEAR);
                    year = year;
                    month = calendar.get(Calendar.MONTH);

                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }



                datePickerDialog = new DatePickerDialog(SudentDetailsEdit.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        birthday.setText( i+ "-" + (i1 + 1) + "-" + i2);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        spinnerLevelList = new ArrayList<>();
        spinnerClassList = new ArrayList<>();
        genderList = new ArrayList<>();
        states = new ArrayList<>();



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


        for (int a = 0; a < classList.size(); a++) {
            spinnerClassList.add(classList.get(a).getClassName().toUpperCase());
        }

        for (int a = 0; a < levelList.size(); a++) {
            spinnerLevelList.add(levelList.get(a).getLevelName().toUpperCase());
        }

        ArrayAdapter adapterGender = new ArrayAdapter(this, R.layout.spinner_item, genderList);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapterGender);
        String genderCompareValue = student.get(0).getStudentGender();
        gender.setSelection(getIndex(gender, genderCompareValue));


        ArrayAdapter adapterState = new ArrayAdapter(this, R.layout.spinner_item, states);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateOrigin.setAdapter(adapterState);
        String stateOriginCompareValue = st.getState_of_origin();
        stateOrigin.setSelection(getIndex(stateOrigin, stateOriginCompareValue));

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, spinnerLevelList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentLevel.setAdapter(adapter);
        String studentLevelCompareValue = st.getStudentLevel();
        String value="";
        for(int a=0;a<levelList.size();a++){
            if(studentLevelCompareValue.equals(levelList.get(a).getLevelId())) {
                value = levelList.get(a).getLevelName();
            }
        }
        studentLevel.setSelection(getIndex(studentLevel, value));

        studentLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String level_id = levelList.get(i).getLevelId();
                setClassSpinnerItems(level_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    private void validateInput() {
        surnameText = surname.getText().toString();
        firstnameText = firstname.getText().toString();
        middlenameText = middlename.getText().toString();
        registrationNumberText = reg_no.getText().toString();
        guardianAddressText = guardianAddress.getText().toString();
        guardianNameText = guardianName.getText().toString();
        guardianEmailText = guardianEmail.getText().toString();
        guardianPhoneText = guardianPhoneNo.getText().toString();
        birthdayText = birthday.getText().toString();
        genderText = "";
        statOriginText = "";
        studentLevelText = "";
        studentClassText = "";
        if (gender.getSelectedItemPosition() != 0) {
            genderText = gender.getSelectedItem().toString();
        }
        if (stateOrigin.getSelectedItemPosition() != 0) {
            statOriginText = stateOrigin.getSelectedItem().toString();
        }
            //studentClassText = student_class.getSelectedItem().toString();
            int position = studentClass.getSelectedItemPosition();
            studentClassText = classList.get(position).getClassId();


            //studentLevelText = student_level.getSelectedItem().toString();
            int position1 = studentLevel.getSelectedItemPosition();
            studentLevelText = levelList.get(position1).getLevelId();


        if (surnameText.isEmpty() || firstnameText.isEmpty() || registrationNumberText.isEmpty() || guardianAddressText.isEmpty() ||
                guardianNameText.isEmpty() || guardianEmailText.isEmpty() || guardianPhoneText.isEmpty() || birthdayText.isEmpty() ||
                genderText.isEmpty() || statOriginText.isEmpty() || studentClassText.isEmpty() || studentLevelText.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Fill out the form correctly");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    validateEditTextAndSpinner();
                }
            });
            builder.show();
        } else if (!isValid(guardianEmailText)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Email address is not valid");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        } else {
            callAddStudentApi();
        }
    }



    private boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    private void callAddStudentApi(){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(SudentDetailsEdit.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        String url = Login.urlBase+"/update.php?_db="+db;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog1.dismiss();
                Log.i("response",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if(status.equals("success")){

                        UpdateBuilder<StudentTable,Long> updateBuilder = studentDao.updateBuilder();
                        updateBuilder.updateColumnValue("studentSurname",surnameText);
                        updateBuilder.updateColumnValue("studentFirstname",firstnameText);
                        updateBuilder.updateColumnValue("studentMiddlename",middlenameText);
                        updateBuilder.updateColumnValue("studentGender",genderText);
                        updateBuilder.updateColumnValue("studentReg_no",registrationNumberText);
                        updateBuilder.updateColumnValue("studentClass",studentClassText);
                        updateBuilder.updateColumnValue("studentLevel",studentLevelText);
                        updateBuilder.updateColumnValue("guardianName",guardianNameText);
                        updateBuilder.updateColumnValue("guardianAddress",guardianAddressText);
                        updateBuilder.updateColumnValue("guardianEmail",guardianEmailText);
                        updateBuilder.updateColumnValue("guardianPhoneNo",guardianPhoneText);
                        updateBuilder.updateColumnValue("state_of_origin",statOriginText);
                        updateBuilder.updateColumnValue("date_of_birth",birthdayText);
                        updateBuilder.where().eq("studentId",st.getStudentId());
                        updateBuilder.update();
                        onBackPressed();
                        Toast.makeText(SudentDetailsEdit.this,"Operation was Successful",Toast.LENGTH_SHORT).show();

                            }

                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("response","Error: "+error.getMessage());
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",st.getStudentId());
                params.put("surname",surnameText);
                params.put("first_name",firstnameText);
                params.put("sex",genderText);
                params.put("birthdate",birthdayText);
                params.put("registration_no",registrationNumberText);
                params.put("guardian_name",guardianNameText);
                params.put("guardian_email",guardianEmailText);
                params.put("guardian_address",guardianAddressText);
                params.put("guardian_phone_no",guardianPhoneText);
                params.put("state_origin",statOriginText);
                params.put("student_level",studentLevelText);
                params.put("student_class",studentClassText);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setClassSpinnerItems(String levelId){
        spinnerClassList.clear();
        try {
            QueryBuilder<ClassNameTable,Long> queryBuilder = classDao.queryBuilder();
            queryBuilder.where().eq("level",levelId);
            classList = queryBuilder.query();
            for(int i =0;i<classList.size();i++){
                spinnerClassList.add(classList.get(i).getClassName().toUpperCase());
            }

            ArrayAdapter adapterClass = new ArrayAdapter(this,R.layout.spinner_item, spinnerClassList);
            adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            studentClass.setAdapter(adapterClass);
            String studentClassCompareValue = st.getStudentClass();
            String value1="";
            for(int a=0;a<classList.size();a++){
                if(studentClassCompareValue.equals(classList.get(a).getClassId())) {
                    value1 = classList.get(a).getClassName();
                    studentClass.setSelection(getIndex(studentClass, value1));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void validateEditTextAndSpinner() {
        EditText[] editTextCollection = {surname, firstname, middlename, reg_no, guardianName, guardianAddress, guardianEmail, guardianPhoneNo};
        for (EditText anEditTextCollection : editTextCollection) {
            if (anEditTextCollection.getText().toString().equals("")) {
                anEditTextCollection.setError("Field is requred");
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
            TextView stateOrigin1 = (TextView) stateOrigin.getSelectedView();
            stateOrigin1.setError("field is required");
        }

        if(studentLevelText.equals("")){
            TextView studentLevel1 = (TextView) studentLevel.getSelectedView();
            studentLevel1.setError("field is required");
        }

        if(studentClassText.equals("")){
            TextView studentClas = (TextView) studentClass.getSelectedView();
            studentClas.setError("field is required");
        }
    }
}
