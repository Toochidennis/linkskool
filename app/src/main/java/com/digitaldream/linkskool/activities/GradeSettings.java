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
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.adapters.GradeSettingsAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.GradeModel;
import com.digitaldream.linkskool.models.GradeModelCopy;
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

public class GradeSettings extends AppCompatActivity implements GradeSettingsAdapter.OnClearBtnClickListener {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<GradeModel> gradeList;
    private Dao<GradeModel,Long> gradeDao;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton fab,fab2,fab3;
    private List<GradeModelCopy> gradeModelCopyList;
    private JSONObject jsonObject;
    String db;
    private List<GradeModelCopy> firstList;
    private List<GradeModelCopy> secondList;
    private FrameLayout layout,emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_settings);

        databaseHelper = new DatabaseHelper(this);
        try {
            gradeDao = DaoManager.createDao(databaseHelper.getConnectionSource(),GradeModel.class);
            gradeList = gradeDao.queryForAll();
            Log.i("response", String.valueOf(gradeList.size()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        gradeModelCopyList = new ArrayList<>();

        for(int a = 0; a<gradeList.size();a++){
            gradeModelCopyList.add(new GradeModelCopy(gradeList.get(a).getGradeId(),gradeList.get(a).getGradeName(),gradeList.get(a).getGradeMinimuim(),gradeList.get(a).getGradeRemark()));
        }

        layout = findViewById(R.id.grade_unempty_state);
        emptyLayout = findViewById(R.id.grade_empty_state);

        recyclerView = findViewById(R.id.grade_settings_recycler);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Grade Settings");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
         db = sharedPreferences.getString("db","");


         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
         recyclerView.setLayoutManager(linearLayoutManager);
         recyclerView.setHasFixedSize(true);

        fab = findViewById(R.id.add_grade);
        fab2 = findViewById(R.id.save_grade);
        fab3 = findViewById(R.id.fab_grade1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 gradeModelCopyList.add(new GradeModelCopy("","","",""));


                    GradeSettingsAdapter adapter = new GradeSettingsAdapter(GradeSettings.this,gradeModelCopyList,GradeSettings.this);

                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);

            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.VISIBLE);
                gradeModelCopyList.add(new GradeModelCopy("","","",""));

                GradeSettingsAdapter adapter = new GradeSettingsAdapter(GradeSettings.this,gradeModelCopyList,GradeSettings.this);

                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);

            }
        });


        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                jsonObject = new JSONObject();

                JSONArray jsonArray = new JSONArray();
                for(int a = 0;a<gradeModelCopyList.size();a++) {
                    //secondList.add(new GradeModelCopy(gradeList.get(a).getGradeId(), gradeList.get(a).getGradeName(), gradeList.get(a).getGradeMinimuim(), gradeList.get(a).getGradeRemark()));



                    JSONObject object = new JSONObject();

                    try {
                        object.put("id",gradeModelCopyList.get(a).getGradeId());
                        object.put("grade_symbol",gradeModelCopyList.get(a).getGradeName());
                        object.put("grade_start",gradeModelCopyList.get(a).getGradeMinimuim());
                        object.put("grade_remark",gradeModelCopyList.get(a).getGradeRemark());
                        jsonArray.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                try {
                    jsonObject.put("grades",jsonArray);
                    Log.i("response",jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                validateInput();
            }
        });


        if(!gradeModelCopyList.isEmpty()) {
            emptyLayout.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            GradeSettingsAdapter adapter = new GradeSettingsAdapter(this, gradeModelCopyList, GradeSettings.this);
            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setAdapter(adapter);
        }else {
            layout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onClearBtnClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete ?");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteGradeApiCall(gradeModelCopyList.get(position).getGradeId(),position);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();


    }



    private void validateInput(){
        String value="";
        for(int a =0;a<GradeSettingsAdapter.gradeList.size();a++){
            if(GradeSettingsAdapter.gradeList.get(a).getGradeName().isEmpty()|| GradeSettingsAdapter.gradeList.get(a).getGradeMinimuim().isEmpty()|| GradeSettingsAdapter.gradeList.get(a).getGradeRemark().isEmpty()){
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
            addGradeApi();
        }
    }
    private void addGradeApi(){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        Log.i("nevermind","database "+db);

        String url = Login.urlBase+"/addGrade.php?grades="+jsonObject.toString()+"&_db="+db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog1.dismiss();
                Log.i("dumping",response);
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if(status.equals("success")){
                        JSONArray gradeArray = object.getJSONArray("grades");
                        for(int a=0;a<gradeArray.length();a++){
                            JSONObject jsonObject = gradeArray.getJSONObject(a);
                            String id = jsonObject.getString("id");
                            String gradeSymbol = jsonObject.getString("grade_symbol");
                            String start = jsonObject.getString("start");
                            String remark = jsonObject.getString("remark");
                            QueryBuilder<GradeModel,Long> queryBuilder = gradeDao.queryBuilder();
                            queryBuilder.where().eq("gradeId",id);
                            List<GradeModel> gradeList = queryBuilder.query();
                            if(gradeList.isEmpty()){
                                GradeModel gm = new GradeModel();
                                gm.setGradeName(gradeSymbol);
                                gm.setGradeRemark(remark);
                                gm.setGradeMinimuim(start);
                                gm.setGradeId(id);
                                gradeDao.create(gm);
                            }else{
                                UpdateBuilder<GradeModel,Long> updateBuilder = gradeDao.updateBuilder();
                                updateBuilder.updateColumnValue("gradeName",gradeSymbol);
                                updateBuilder.updateColumnValue("gradeMinimuim",start);
                                updateBuilder.updateColumnValue("gradeRemark",remark);
                                updateBuilder.where().eq("gradeId",id);
                                updateBuilder.update();
                            }

                        }

                        gradeList = gradeDao.queryForAll();
                        gradeModelCopyList.clear();
                        for(int a = 0; a<gradeList.size();a++){
                            gradeModelCopyList.add(new GradeModelCopy(gradeList.get(a).getGradeId(),gradeList.get(a).getGradeName(),gradeList.get(a).getGradeMinimuim(),gradeList.get(a).getGradeRemark()));
                        }
                        if(!gradeModelCopyList.isEmpty()) {
                            emptyLayout.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            GradeSettingsAdapter adapter = new GradeSettingsAdapter(GradeSettings.this, gradeModelCopyList, GradeSettings.this);
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                        }else{
                            layout.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(GradeSettings.this, "Operation successful", Toast.LENGTH_SHORT).show();

                    }else if(status.equals("failed")){
                        Toast.makeText(GradeSettings.this, "Operation failed", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
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
                params.put("_db",db);
                return params;
            }
        };*/

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void deleteGradeApiCall(final String gradeId, final int position){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = Login.urlBase+"/deleteGrade.php?id="+gradeId+"&_db="+db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog1.dismiss();
                Log.i("response",response);
                try {
                    JSONObject jsonObject =new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("success")){
                        DeleteBuilder<GradeModel,Long> deleteBuilder = gradeDao.deleteBuilder();
                        deleteBuilder.where().eq("gradeId",gradeId);
                        deleteBuilder.delete();
                        gradeModelCopyList.remove(position);
                        if(!gradeModelCopyList.isEmpty()) {
                            emptyLayout.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            GradeSettingsAdapter adapter = new GradeSettingsAdapter(GradeSettings.this, gradeModelCopyList, GradeSettings.this);
                            recyclerView.setAdapter(adapter);
                        }else {
                            layout.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                        }
                       Toast.makeText(GradeSettings.this,"Operation was successful",Toast.LENGTH_SHORT).show();
                    }else if(status.equals("failed")){
                        Toast.makeText(GradeSettings.this,"Operation failed",Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
                Toast.makeText(GradeSettings.this,"Error connecting to server",Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(GradeSettings.this);
        requestQueue.add(stringRequest);
    }

}
