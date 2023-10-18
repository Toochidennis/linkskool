package com.digitaldream.linkskool.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.adapters.AssessmentSettingsAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.models.AssessmentModel;
import com.digitaldream.linkskool.models.AssessmentModelCopy;
import com.digitaldream.linkskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AssessmentSetting extends AppCompatActivity implements AssessmentSettingsAdapter.OnClearBtnClickListener {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private List<AssessmentModel> assessmentModelList;
    private List<AssessmentModelCopy> assessmentModelCopyList;
    private Dao<AssessmentModel,Long> assessmentDao;
    private FloatingActionButton addAssessment,saveAssessment,addAssessment2;
    private Spinner levelSelector;
    private Dao<LevelTable,Long> levelDao;
    private List<LevelTable> levelList;
    private List<String> spinnerLevelList;
    AssessmentSettingsAdapter adapter1;
    private FrameLayout unempty_state,empty_state;
    private JSONObject jsonObject;
    private String levelId;
    String db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_setting);
        unempty_state = findViewById(R.id.assessment_list_unempty_state);
        empty_state = findViewById(R.id.assessment_setting_empty_state);
        databaseHelper = new DatabaseHelper(this);
        try {
            assessmentDao = DaoManager.createDao(databaseHelper.getConnectionSource(),AssessmentModel.class);
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(),LevelTable.class);
            levelList = levelDao.queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        assessmentModelCopyList = new ArrayList<>();



        spinnerLevelList = new ArrayList<>();
        spinnerLevelList.add("SELECT LEVEL");
        for(int a = 0;a<levelList.size();a++){
            spinnerLevelList.add(levelList.get(a).getLevelName().toUpperCase());
        }

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");

        toolbar = findViewById(R.id.toolbar);
        levelSelector = findViewById(R.id.level_assessment);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, spinnerLevelList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSelector.setAdapter(adapter);
        levelSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    levelId ="";
                }else {
                    levelId = levelList.get(position-1).getLevelId();
                }
                Log.i("resp",levelId+" "+position);
                getAssessmentByLevel(levelId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Assessment Setting");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        recyclerView = findViewById(R.id.recycler_assessment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        AssessmentSettingsAdapter adapter1 = new AssessmentSettingsAdapter(this,assessmentModelCopyList,this);
        recyclerView.setAdapter(adapter1);

        addAssessment = findViewById(R.id.add_assessment);
        saveAssessment = findViewById(R.id.save_assessment);
        addAssessment2 = findViewById(R.id.fab_assessment2);
        addAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAssessmentView();
            }
        });

        saveAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonObject = new JSONObject();

                JSONArray jsonArray = new JSONArray();
                for(int a = 0;a<assessmentModelCopyList.size();a++){
                    JSONObject object = new JSONObject();

                    try {
                        object.put("id",assessmentModelCopyList.get(a).getAssessmentId());
                        object.put("assessment_name",assessmentModelCopyList.get(a).getAssessmentName());
                        object.put("max_score",assessmentModelCopyList.get(a).getMaxScore());
                        object.put("level",levelId);
                        jsonArray.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                try {
                    jsonObject.put("assessment",jsonArray);
                    Log.i("response",jsonObject.toString());
                    validateInput();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        addAssessment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAssessmentView();
            }
        });

    }

    private void getAssessmentByLevel(String levelId) {
        //assessmentModelList.clear();
        assessmentModelCopyList.clear();
        QueryBuilder<AssessmentModel,Long> queryBuilder = assessmentDao.queryBuilder();
        try {
            queryBuilder.where().eq("level",levelId);
            assessmentModelList =queryBuilder.query();
            for (int a=0;a<assessmentModelList.size();a++){
                assessmentModelCopyList.add(new AssessmentModelCopy(assessmentModelList.get(a).getAssessmentId(),assessmentModelList.get(a).getAssessmentName(),
                        assessmentModelList.get(a).getMaxScore(),assessmentModelList.get(a).getLevel()));
            }
            if(!assessmentModelCopyList.isEmpty()) {
                unempty_state.setVisibility(View.VISIBLE);
                empty_state.setVisibility(View.GONE);
                adapter1 = new AssessmentSettingsAdapter(this, assessmentModelCopyList, this);
                recyclerView.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
            }else{
                unempty_state.setVisibility(View.GONE);
                empty_state.setVisibility(View.VISIBLE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClearBtnClick(int position) {
        deleteGradeApiCall(assessmentModelCopyList.get(position).getAssessmentId(),position);
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

    private void addAssessmentView(){
        unempty_state.setVisibility(View.VISIBLE);
        assessmentModelCopyList.add(new AssessmentModelCopy("","","",""));


        AssessmentSettingsAdapter adapter = new AssessmentSettingsAdapter(AssessmentSetting.this,assessmentModelCopyList,AssessmentSetting.this);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);
    }

    private void addAssessmentApiCall(){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        String url = Login.urlBase+"/addAssessment.php?assessment="+jsonObject.toString()+"&_db="+db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog1.dismiss();
                Log.i("response",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("success")){
                        JSONArray assessmentArray = jsonObject.getJSONArray("assessment");
                        for (int a=0;a<assessmentArray.length();a++){
                            JSONObject assessmentObject = assessmentArray.getJSONObject(a);
                            String id = assessmentObject.getString("id");
                            String assessmentName = assessmentObject.getString("assesment_name");
                            String maxScore = assessmentObject.getString("max_score");
                            String level = assessmentObject.getString("level");
                            QueryBuilder<AssessmentModel,Long> queryBuilder = assessmentDao.queryBuilder();
                            queryBuilder.where().eq("assessmentId",id);
                            List<AssessmentModel> assessmentList = queryBuilder.query();
                            if(assessmentList.isEmpty()){
                                AssessmentModel am = new AssessmentModel();
                                am.setMaxScore(maxScore);
                                am.setAssessmentName(assessmentName);
                                am.setAssessmentId(id);
                                if(level.equals("0")){
                                    am.setLevel("");
                                }else{
                                    am.setLevel(level);
                                }
                                assessmentDao.create(am);

                            }else{
                                UpdateBuilder<AssessmentModel,Long> updateBuilder = assessmentDao.updateBuilder();
                                updateBuilder.updateColumnValue("assessmentName",assessmentName);
                                updateBuilder.updateColumnValue("maxScore",maxScore);
                                if(level.equals("0")){
                                    updateBuilder.updateColumnValue("level","");
                                }else{
                                    updateBuilder.updateColumnValue("level",level);
                                }
                                updateBuilder.where().eq("assessmentId",id);
                                updateBuilder.update();
                            }
                        }

                        getAssessmentByLevel(levelId);
                        Toast.makeText(AssessmentSetting.this, "Operation successful", Toast.LENGTH_SHORT).show();
                    }else if(status.equals("failed")){
                        Toast.makeText(AssessmentSetting.this, "Operation failed", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
                Log.i("error","errorMessage"+error.getMessage());
            }
        });/*{
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("grades",jsonObject.toString() );
                return params;
            }
        };*/

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void validateInput(){
        String value="";
        for(int a =0;a<AssessmentSettingsAdapter.assessmentModelCopyList.size();a++){
            if(AssessmentSettingsAdapter.assessmentModelCopyList.get(a).getAssessmentName().isEmpty()|| AssessmentSettingsAdapter.assessmentModelCopyList.get(a).getMaxScore().isEmpty()){
                value = "empty";
                break;
            }
        }
        if(value.equals("empty")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("All fields are required");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }else if(value.equals("")){
            addAssessmentApiCall();
        }
    }

    private void deleteGradeApiCall(final String assessmentId, final int position){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = Login.urlBase+"/deleteAssessment.php?id="+assessmentId+"&_db="+db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog1.dismiss();
                Log.i("response",response);
                try {
                    JSONObject jsonObject =new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("success")){
                        DeleteBuilder<AssessmentModel,Long> deleteBuilder = assessmentDao.deleteBuilder();
                        deleteBuilder.where().eq("assessmentId",assessmentId);
                        deleteBuilder.delete();
                        assessmentModelCopyList.remove(position);
                        if(!assessmentModelCopyList.isEmpty()) {
                            empty_state.setVisibility(View.GONE);
                            unempty_state.setVisibility(View.VISIBLE);
                            AssessmentSettingsAdapter adapter = new AssessmentSettingsAdapter(AssessmentSetting.this, assessmentModelCopyList, AssessmentSetting.this);
                            recyclerView.setAdapter(adapter);
                        }else {
                            unempty_state.setVisibility(View.GONE);
                            empty_state.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(AssessmentSetting.this,"Operation was successful",Toast.LENGTH_SHORT).show();
                    }else if(status.equals("failed")){
                        Toast.makeText(AssessmentSetting.this,"Operation failed",Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
                Toast.makeText(AssessmentSetting.this,"Error connecting to server",Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(AssessmentSetting.this);
        requestQueue.add(stringRequest);
    }
}
