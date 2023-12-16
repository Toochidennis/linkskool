package com.digitaldream.linkskool.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.adapters.PrevYrsAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.GeneralSettingModel;
import com.digitaldream.linkskool.models.PrevYrModel;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.dialog.CustomDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RegYearList extends AppCompatActivity implements PrevYrsAdapter.OnYearClickListener {
    private List<PrevYrModel> list;
    private PrevYrsAdapter adapter;
    public static String classId, level, db, term, year;
    private DatabaseHelper databaseHelper;
    private Dao<GeneralSettingModel, Long> schoolDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_year_list);
        databaseHelper = new DatabaseHelper(this);
        List<GeneralSettingModel> list1 = null;

        try {
            schoolDao = DaoManager.createDao(databaseHelper.getConnectionSource(), GeneralSettingModel.class);
            list1 = schoolDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        year = "";

        if (list1 != null) {
            year = list1.get(0).getSchoolYear();
        }

        Intent i = getIntent();
        classId = i.getStringExtra("classId");
        level = i.getStringExtra("levelId");
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");
        term = sharedPreferences.getString("term", "");
        RecyclerView recyclerView = findViewById(R.id.past_yrs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        adapter = new PrevYrsAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
        getPrevRegistration(classId, db);

        FloatingActionButton addBtn = findViewById(R.id.add);
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegYearList.this, CourseRegistration.class);
            intent.putExtra("classId", classId);
            intent.putExtra("levelId", level);
            startActivity(intent);
        });

        TextView className = findViewById(R.id.class_name);
//        className.setText(AdminResultDashboardActivity.class_name);

        RelativeLayout rl = findViewById(R.id.course_registration);
        rl.setOnClickListener(v -> {
            Intent intent = new Intent(RegYearList.this, Courses.class);
            intent.putExtra("class", classId);
            intent.putExtra("level", level);
            intent.putExtra("from", "bulk");
            startActivity(intent);
        });

        RelativeLayout rl2 = findViewById(R.id.copy_reg);
        rl2.setOnClickListener(v -> {
            // Toast.makeText(RegYearList.this,"Clicked",Toast.LENGTH_SHORT).show();
            copyCourseRegistration();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
    }

    private void getPrevRegistration(String classId, String db) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = getString(R.string.base_url) + "/getClassRegistration.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.i("response", "response " + response);
            dialog.dismiss();

            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    String year = jsonObject.getString("year");
                    JSONArray termsArray = new JSONArray(jsonObject.getString("terms"));

                    for (int i = 0; i < termsArray.length(); i++) {
                        JSONObject termsObject = termsArray.getJSONObject(i);
                        String term = termsObject.getString("term");
                        String termName = termsObject.getString("name");

                        if (!termName.equals("null")) {
                            PrevYrModel previousYearModel = new PrevYrModel();
                            previousYearModel.setYear(year);
                            previousYearModel.setName(termName);
                            previousYearModel.setTerm(term);

                            list.add(previousYearModel);
                        }
                    }
                }

                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            dialog.dismiss();
            error.printStackTrace();
            Toast.makeText(RegYearList.this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("class", classId);
                param.put("_db", db);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(RegYearList.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onYearClick(int position) {
        PrevYrModel pr = list.get(position);
        String schoolYear = pr.getYear();
        String term1 = pr.getTerm();
        Log.i("response", schoolYear + " current " + year);
        Intent intent;
        if (year.equalsIgnoreCase(schoolYear) && term.equals(term1)) {
            intent = new Intent(RegYearList.this, CourseRegistration.class);
            intent.putExtra("classId", classId);
            intent.putExtra("levelId", level);
        } else {
            intent = new Intent(RegYearList.this, CourseRegistrationDetails.class);
            intent.putExtra("object", pr);
        }
        startActivity(intent);
    }

    private void copyCourseRegistration() {
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = getString(R.string.base_url) + "/copyRegistration.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.i("response", response);
                if (response != null && response.equals("1")) {
                    Toast.makeText(RegYearList.this, "Copied Previous registration successfully ", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    startActivity(intent);
                    finish();
                } else if (response != null && response.equals("0")) {
                    Toast.makeText(RegYearList.this, "Operation failed ", Toast.LENGTH_SHORT).show();

                }
            }
        }, error -> {
            dialog.dismiss();
            error.printStackTrace();
            Toast.makeText(RegYearList.this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("class", classId);
                param.put("year", year);
                param.put("term", term);
                param.put("_db", db);
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
