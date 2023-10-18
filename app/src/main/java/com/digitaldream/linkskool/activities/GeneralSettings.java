package com.digitaldream.linkskool.activities;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.GeneralSettingModel;
import com.digitaldream.linkskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class GeneralSettings extends AppCompatActivity {
    private Toolbar toolbar;
    private Dao<GeneralSettingModel,Long> generalSettingsDao;
    private List<GeneralSettingModel> generalSettingList;
    private DatabaseHelper databaseHelper;
    private String termValue;
    private Spinner term,schoolYear;
    private List<String> termSpinnerList,schoolYearSpinnerList;
    private EditText schoolName,website,shortName,address,city,state,country,email,phone,studentPrefix,staffPrefix,alumniPrefix;
    private Button saveBtn;
    String school_name,school_year,school_term,school_website,school_shortName,school_address,school_city,school_state,school_country,
    school_email,school_phone,school_studentPrefix,school_staffPrefix,school_alumniPrefix;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);
        databaseHelper = new DatabaseHelper(this);
        try {
            generalSettingsDao = DaoManager.createDao(databaseHelper.getConnectionSource(),GeneralSettingModel.class);
            generalSettingList = generalSettingsDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("General Settings");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        schoolName = findViewById(R.id.sch_name);
        schoolYear = findViewById(R.id.sch_year);
        term = findViewById(R.id.sch_term);
        website = findViewById(R.id.sch_website);
        shortName = findViewById(R.id.sch_short_name);
        address = findViewById(R.id.sch_address);
        city = findViewById(R.id.sch_city);
        state = findViewById(R.id.sch_state);
        country = findViewById(R.id.sch_country);
        email = findViewById(R.id.sch_email);
        phone = findViewById(R.id.sch_phone);
        studentPrefix = findViewById(R.id.sch_student_prefix);
        staffPrefix = findViewById(R.id.sch_staff_prefix);
        alumniPrefix = findViewById(R.id.sch_alumni_prefix);
        saveBtn = findViewById(R.id.save_general_settings);

        schoolName.setText(generalSettingList.get(0).getSchoolName().toUpperCase());
        website.setText(generalSettingList.get(0).getWebsite().toUpperCase());
        shortName.setText(generalSettingList.get(0).getShcoolShortName().toUpperCase());
        address.setText(generalSettingList.get(0).getSchoolAddress().toUpperCase());
        city.setText(generalSettingList.get(0).getCity().toUpperCase());
        state.setText(generalSettingList.get(0).getState().toUpperCase());
        country.setText(generalSettingList.get(0).getCountry().toUpperCase());
        email.setText(generalSettingList.get(0).getSchoolEmail().toUpperCase());
        phone.setText(generalSettingList.get(0).getSchoolPhone().toUpperCase());
        studentPrefix.setText(generalSettingList.get(0).getStudentPrefix().toUpperCase());
        staffPrefix.setText(generalSettingList.get(0).getStaffPrefix().toUpperCase());
        alumniPrefix.setText(generalSettingList.get(0).getAlumniPrefix().toUpperCase());

        termSpinnerList = new ArrayList<>();
        schoolYearSpinnerList = new ArrayList<>();
        termSpinnerList.add("SELECT TERM");
        termSpinnerList.add("1st TERM");
        termSpinnerList.add("2nd TERM");
        termSpinnerList.add("3rd TERM");

        schoolYearSpinnerList.add("SCHOOL YEAR");
        int startYear = Calendar.getInstance().get(Calendar.YEAR)-5;
        int endYear = Calendar.getInstance().get(Calendar.YEAR)+5;
        for(int z= startYear;z<endYear;z++){
            schoolYearSpinnerList.add(String.valueOf(z-1)+"/"+String.valueOf(z));
        }



        ArrayAdapter adapterTerm = new ArrayAdapter(this, R.layout.spinner_item, termSpinnerList);
        adapterTerm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        term.setAdapter(adapterTerm);
        String termCompareValue = generalSettingList.get(0).getSchoolTerm();
        if(termCompareValue.equals("1")){
            termValue ="1st TERM";
        }else if(termCompareValue.equals("2")){
            termValue = "2nd TERM";
        }else if(termCompareValue.equals("3")){
            termValue = "3rd TERM";
        }
        term.setSelection(getIndex(term, termValue));

        ArrayAdapter adapterSchoolYear = new ArrayAdapter(this, R.layout.spinner_item, schoolYearSpinnerList);
        adapterSchoolYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolYear.setAdapter(adapterSchoolYear);
        String schoolYr = generalSettingList.get(0).getSchoolYear();
        try {
            int previousYear = Integer.parseInt(schoolYr) - 1;
            String schoolYearCompareValue = String.valueOf(previousYear)+"/"+schoolYr;
            schoolYear.setSelection(getIndex(schoolYear, schoolYearCompareValue));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(term.getSelectedItem().toString().equals("1st TERM")){
                    termValue ="1";
                }else if(term.getSelectedItem().toString().equals("2nd TERM")){
                    termValue = "2";
                }else if(term.getSelectedItem().toString().equals("3rd TERM")){
                    termValue = "3";
                }
                school_name = schoolName.getText().toString();
                school_year = schoolYear.getSelectedItem().toString();
                school_year = school_year.substring(5);
                school_address = address.getText().toString();
                school_term = termValue;
                school_website = website.getText().toString();
                school_shortName = shortName.getText().toString();
                school_address =address.getText().toString();
                school_city = city.getText().toString();
                school_state = state.getText().toString();
                school_country = country.getText().toString();
                school_email = email.getText().toString();
                school_phone = phone.getText().toString();
                school_studentPrefix = studentPrefix.getText().toString();
                school_staffPrefix = staffPrefix.getText().toString();
                school_alumniPrefix = alumniPrefix.getText().toString();
                saveGeneralSettingApiCall();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    private void saveGeneralSettingApiCall(){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        final String db = sharedPreferences.getString("db","");
        String url = Login.urlBase+"/schoolSettings.php?name="+school_name+"&year="+school_year+"&term="+school_term
                +"&website="+school_website+"&short_name="+school_shortName+"&address="+school_address+"&city="+school_city
                +"&state="+school_state+"&country="+school_country+"&email="+school_email+"&phone="+school_phone+"&student_prefix="+school_studentPrefix
                +"&staff_prefix="+school_staffPrefix+"&alumni_prefix="+school_alumniPrefix+"&_db="+db;
        Log.i("das",db);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog1.dismiss();
                Log.i("response",response);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if(status.equals("success")){
                        JSONObject recordObject = object.getJSONObject("record");
                        String id = recordObject.getString("id");
                        UpdateBuilder<GeneralSettingModel,Long> updateBuilder = generalSettingsDao.updateBuilder();
                        updateBuilder.updateColumnValue("schoolName",school_name);
                        updateBuilder.updateColumnValue("schoolYear",school_year);
                        updateBuilder.updateColumnValue("schoolTerm",school_term);
                        updateBuilder.updateColumnValue("website",school_website);
                        updateBuilder.updateColumnValue("shcoolShortName",school_shortName);
                        updateBuilder.updateColumnValue("schoolAddress",school_address);
                        updateBuilder.updateColumnValue("city",school_city);
                        updateBuilder.updateColumnValue("state",school_state);
                        updateBuilder.updateColumnValue("country",school_country);
                        updateBuilder.updateColumnValue("schoolEmail",school_email);
                        updateBuilder.updateColumnValue("schoolPhone",school_phone);
                        updateBuilder.updateColumnValue("studentPrefix",school_studentPrefix);
                        updateBuilder.updateColumnValue("staffPrefix",school_staffPrefix);
                        updateBuilder.updateColumnValue("alumniPrefix",school_alumniPrefix);
                        updateBuilder.update();
                        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("term",school_term);
                        editor.apply();
                        Toast.makeText(GeneralSettings.this,"Settings saved successfully",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(GeneralSettings.this,GeneralSettings.class);
                        finish();
                        startActivity(intent);
                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
                Toast.makeText(GeneralSettings.this,"Error while saving...Try again",Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
