package com.digitaldream.winskool.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AddLevel extends AppCompatActivity {
    private Toolbar toolbar;
    private Spinner schoolType;
    private EditText rank,levelName;
    private Button saveBtn;
    private String schoolTypeValue;
    private String rankValue;
    private String levelValue;
    private DatabaseHelper databaseHelper;
    private Dao<LevelTable,Long> levelDao;
    private List<LevelTable> levelNames;
    private String school_type_id;
    private String levelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_level);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Level");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);


        databaseHelper = new DatabaseHelper(this);
        try {
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(),LevelTable.class);
            levelNames = levelDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        schoolType = findViewById(R.id.school_type);
        rank = findViewById(R.id.rank);
        levelName = findViewById(R.id.addLevel_name);


        if(getIntent().getStringExtra("from").equals("editBtn")) {
            Intent i = getIntent();
            schoolTypeValue = i.getStringExtra("school_type").toUpperCase();
            String level_name = i.getStringExtra("level_name").toUpperCase();
//            rankValue = i.getStringExtra("rank").toUpperCase();
            if (schoolTypeValue.equals("1")){
                school_type_id = "Nursery School";
            }else if(schoolTypeValue.equals("2")){
                school_type_id = "Primary School";

            }else if(schoolTypeValue.equals("3")){
                school_type_id = "Secondary School";
                Log.i("sec",schoolTypeValue);

            }
            getSupportActionBar().setTitle("Edit Level");
            levelId = i.getStringExtra("id");
            levelName.setText(level_name);
            rank.setText(rankValue);
            selectSpinnerValue(schoolType,school_type_id);
        }
        saveBtn = findViewById(R.id.save_level);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 rankValue = rank.getText().toString().trim();
                levelValue = levelName.getText().toString().trim();
                if(rankValue.isEmpty() || levelValue.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddLevel.this);
                    builder.setMessage("All fields are required");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }else {
                    if(getIntent().getStringExtra("from").equals("editBtn")) {
                        callEditLevelApi();
                    }else {
                        callAddLevelApi();
                    }
                }
            }
        });
        schoolType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        schoolTypeValue ="1".trim();
                        break;
                    case 1:
                        schoolTypeValue ="2".trim();
                        break;
                    case  2:
                        schoolTypeValue ="3".trim();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

    private void callAddLevelApi(){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        Log.i("respon",rankValue+" "+levelValue+" "+schoolTypeValue);
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        final String db = sharedPreferences.getString("db","");
        Log.i("db",db);

        String url = Login.urlBase+"/addLevel.php?rank="+rankValue+"&school_type="+schoolTypeValue+"&level_name="+levelValue+"&_db="+db;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                dialog1.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("success")){
                        JSONObject recordsObject = jsonObject.getJSONObject("records");
                        String levelId = recordsObject.getString("id");
                        String levelName = recordsObject.getString("level_name");
                        String schoolType =recordsObject.getString("school_type");
                        QueryBuilder<LevelTable, Long> queryBuilder = levelDao.queryBuilder();
                        queryBuilder.where().eq("levelId", levelId);
                        levelNames = queryBuilder.query();
                        if (levelNames.isEmpty()) {
                            LevelTable lt = new LevelTable();
                            lt.setLevelId(levelId);
                            lt.setLevelName(levelName);
                            lt.setSchoolType(schoolType);
                            levelDao.create(lt);
                        }

                        Toast.makeText(AddLevel.this,"Level added successfully",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        finish();
                    }else if(status.equals("failed")){
                        Toast.makeText(AddLevel.this,"Level already exists",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }

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

    private void callEditLevelApi(){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        final String db = sharedPreferences.getString("db","");
        Log.i("db",db);


        String url = Login.urlBase+"/addLevel.php?id="+levelId+"&rank="+rankValue+"&school_type="+schoolTypeValue+"&level_name="+levelValue+"&_db="+db;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog1.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("success")){
                        JSONObject recordsObject = jsonObject.getJSONObject("records");
                        String levelId = recordsObject.getString("id");
                        String levelName = recordsObject.getString("level_name");
                        String schoolType =recordsObject.getString("school_type");
                        UpdateBuilder<LevelTable,Long> updateBuilder = levelDao.updateBuilder();
                        updateBuilder.updateColumnValue("levelName",levelName);
                        updateBuilder.updateColumnValue("schoolType",schoolType);
                        updateBuilder.where().eq("levelId",levelId);
                        updateBuilder.update();


                        Toast.makeText(AddLevel.this,"Level updated successfully",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        finish();
                    }else if(status.equals("failed")){
                        Toast.makeText(AddLevel.this,"Level already exists",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }

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
    private void selectSpinnerValue(Spinner spinner, String myString)
    {
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(myString)){
                spinner.setSelection(i);
                break;
            }
        }
    }
}
