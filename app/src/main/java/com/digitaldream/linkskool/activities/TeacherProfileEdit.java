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
import com.digitaldream.linkskool.models.TeachersTable;
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

public class TeacherProfileEdit extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText surname,firstname,middlename,birthdate,address,phone_no,email,staff_no;
    private Spinner gender,state_origin;
    private String surnameText,firstnameText,middlenameText,birthdateText,addressText,phoneNoText,emailText,staffNoText,genderText,stateOriginText;
    private Button saveBtn;
    private TeachersTable tch;
    private DatePickerDialog datePickerDialog;
    private List<String> genderList, states;
    private Dao<TeachersTable,Long> teacherDao;
    private DatabaseHelper databaseHelper;
    private List<TeachersTable> teacherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile_edit);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Edit");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        databaseHelper = new DatabaseHelper(this);
        Intent i = getIntent();
        tch = (TeachersTable) i.getSerializableExtra("teacherObject");
        try {
            teacherDao = DaoManager.createDao(databaseHelper.getConnectionSource(),TeachersTable.class);
            QueryBuilder<TeachersTable,Long> queryBuilder = teacherDao.queryBuilder();
            queryBuilder.where().eq("staffId",tch.getStaffId());
            teacherList = queryBuilder.query();

        } catch (SQLException e) {
            e.printStackTrace();
        }




        surname = findViewById(R.id.edit_teacher_name);
        firstname = findViewById(R.id.edit_teacher_firstname);
        middlename = findViewById(R.id.edit_teacher_middlename);
        birthdate = findViewById(R.id.edit_teacher_birthday);
        address = findViewById(R.id.edit_staff_address);
        phone_no =findViewById(R.id.edit_staff_phone_no);
        staff_no= findViewById(R.id.edit_staff_no);
        email = findViewById(R.id.edit_staff_email);
        gender = findViewById(R.id.spinner_edit_teacher_gender);
        state_origin = findViewById(R.id.edit_spinner_level_teacher_state_origin);
        saveBtn = findViewById(R.id.edit_teacher);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });

        if(!teacherList.get(0).getStaffSurname().isEmpty()) {
            surname.setText(teacherList.get(0).getStaffSurname().toUpperCase());
        }
        if(!teacherList.get(0).getStaffFirstname().isEmpty()) {
            firstname.setText(teacherList.get(0).getStaffFirstname().toUpperCase());
        }
        try {
            if (!teacherList.get(0).getStaffMiddlename().isEmpty()) {
                middlename.setText(teacherList.get(0).getStaffMiddlename().toUpperCase());
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }


        if(!teacherList.get(0).getStaffAddress().isEmpty()) {
            address.setText(teacherList.get(0).getStaffAddress().toUpperCase());
        }
        if(!teacherList.get(0).getStaffPhone().isEmpty()) {
            phone_no.setText(teacherList.get(0).getStaffPhone().toUpperCase());
        }
        if(!teacherList.get(0).getStaffNo().isEmpty()) {
            staff_no.setText(teacherList.get(0).getStaffNo().toUpperCase());
        }
        if(!teacherList.get(0).getStaffEmail().isEmpty()) {
            email.setText(teacherList.get(0).getStaffEmail().toUpperCase());
        }
        if(!teacherList.get(0).getStaffBirthday().isEmpty()) {
            birthdate.setText(teacherList.get(0).getStaffBirthday().toUpperCase());
        }
        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int day,month,year;
                String date = birthdate.getText().toString().trim();
                if (date != null && date.length() > 0) {

//if you  have previous set date
                    String data[] = date.split("-");
                    year = Integer.parseInt(data[0]);

                    month = Integer.parseInt(data[1]);
                    month = month - 1;
                    day = Integer.parseInt(data[2]);
                } else {

//if you dont have previous set date then display current date
                    year = calendar.get(Calendar.YEAR);
                    year = year;
                    month = calendar.get(Calendar.MONTH);

                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }
                //day = calendar.get(Calendar.DAY_OF_MONTH);
                //month = calendar.get(Calendar.MONTH);
                //year = calendar.get(Calendar.YEAR);


                datePickerDialog = new DatePickerDialog(TeacherProfileEdit.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        birthdate.setText( i+ "-" + (i1 + 1) + "-" + i2);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
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

        ArrayAdapter adapterGender = new ArrayAdapter(this, R.layout.spinner_item, genderList);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapterGender);
        String genderCompareValue = tch.getStaffGender();
        gender.setSelection(getIndex(gender, genderCompareValue));


        ArrayAdapter adapterState = new ArrayAdapter(this, R.layout.spinner_item, states);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state_origin.setAdapter(adapterState);
        String stateOriginCompareValue = tch.getStaffStateOrigin();
        state_origin.setSelection(getIndex(state_origin, stateOriginCompareValue));

    }

    private void validateInput() {
        surnameText = surname.getText().toString();
        firstnameText = firstname.getText().toString();
        middlenameText = middlename.getText().toString();
        staffNoText = staff_no.getText().toString();
        addressText = address.getText().toString();
        emailText = email.getText().toString();
        phoneNoText = phone_no.getText().toString();
        birthdateText = birthdate.getText().toString();
        genderText = "";
        stateOriginText = "";

        if (gender.getSelectedItemPosition() != 0) {
            genderText = gender.getSelectedItem().toString();
        }
        if (state_origin.getSelectedItemPosition() != 0) {
            stateOriginText = state_origin.getSelectedItem().toString();
        }


        if (surnameText.isEmpty() || firstnameText.isEmpty() || staffNoText.isEmpty() || addressText.isEmpty()
        || emailText.isEmpty() || phoneNoText.isEmpty() || birthdateText.isEmpty() ||
                genderText.isEmpty() || stateOriginText.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Fill out the form correctly");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    validateEditTextAndSpinner();
                }
            });
            builder.show();
        } else if (!isValid(emailText)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Email address is not valid");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        } else {
            callAddTeacherApi();
        }
    }

    private void callAddTeacherApi() {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(TeacherProfileEdit.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String db = sharedPreferences.getString("db","");
        String url = Login.urlBase+"/updateTeacher.php?_db="+db;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog1.dismiss();
                Log.i("response",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if(status.equals("success")){

                        UpdateBuilder<TeachersTable,Long> updateBuilder = teacherDao.updateBuilder();
                        updateBuilder.updateColumnValue("staffSurname",surnameText);
                        updateBuilder.updateColumnValue("staffFirstname",firstnameText);
                        updateBuilder.updateColumnValue("staffMiddlename",middlenameText);
                        updateBuilder.updateColumnValue("staffGender",genderText);
                        updateBuilder.updateColumnValue("staffAddress",addressText);
                        updateBuilder.updateColumnValue("staffPhone",phoneNoText);
                        updateBuilder.updateColumnValue("staffEmail",emailText);
                        updateBuilder.updateColumnValue("staffNo",staffNoText);
                        updateBuilder.updateColumnValue("staffStateOrigin",stateOriginText);
                        updateBuilder.updateColumnValue("staffBirthday",birthdateText);
                        updateBuilder.where().eq("staffId",tch.getStaffId());
                        updateBuilder.update();
                        onBackPressed();
                        Toast.makeText(TeacherProfileEdit.this,"Operation was Successful",Toast.LENGTH_SHORT).show();


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("response","Error "+error.getMessage());
                Toast.makeText(TeacherProfileEdit.this,"Error connecting to server",Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",tch.getStaffId());
                params.put("surname",surnameText);
                params.put("first_name",firstnameText);
                params.put("middle",middlenameText);
                params.put("sex",genderText);
                params.put("birthdate",birthdateText);
                params.put("staff_no",staffNoText);
                params.put("email",emailText);
                params.put("address",addressText);
                params.put("phone",phoneNoText);
                params.put("state_origin",stateOriginText);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void validateEditTextAndSpinner() {
        EditText[] editTextCollection = {surname, firstname, middlename, staff_no, address, email, phone_no};
        for (EditText anEditTextCollection : editTextCollection) {
            if (anEditTextCollection.getText().toString().equals("")) {
                anEditTextCollection.setError("Field is requred");
            }
        }

        if (birthdate.getText().toString().equals("")) {
            birthdate.setError("Field is required");
        }
        //gender, state_of_origin, student_class, student_level;
        if (genderText.equals("")) {
            TextView genderText1 = (TextView) gender.getSelectedView();
            genderText1.setError("field is required");
        }

        if (stateOriginText.equals("")) {
            TextView stateOrigin1 = (TextView) state_origin.getSelectedView();
            stateOrigin1.setError("field is required");
        }
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }
}
