package com.digitaldream.linkskool.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.adapters.LevelSetingsAdapter;
import com.digitaldream.linkskool.models.LevelTable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class LevelSettings extends AppCompatActivity implements LevelSetingsAdapter.OnEditBtnClickListener {
    private Toolbar toolbar;
    private List<LevelTable> levelList;
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private Dao<LevelTable, Long> levelDao;
    private FloatingActionButton fab, fab2;
    LevelSetingsAdapter adapter;
    private JSONObject jsonObject;
    private String db;
    private FrameLayout layout, emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_settings);
        toolbar = findViewById(R.id.toolbar);
        layout = findViewById(R.id.level_setting_unempty_state);
        emptyLayout = findViewById(R.id.level_setting_empty_state);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Level Settings");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        SharedPreferences sharedPreferences = getSharedPreferences(
                "loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");

        recyclerView = findViewById(R.id.level_settings_recycler);
        LevelSetingsAdapter.selectedId.clear();

        databaseHelper = new DatabaseHelper(this);
        try {
            levelDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), LevelTable.class);
            levelList = levelDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        if (!levelList.isEmpty()) {
            emptyLayout.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            adapter = new LevelSetingsAdapter(this, levelList, this);
            recyclerView.setAdapter(adapter);
        } else {
            layout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }

        fab = findViewById(R.id.fab_level);
        fab2 = findViewById(R.id.fab_level1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelSettings.this, AddLevel.class);
                intent.putExtra("from", "addBtn");
                startActivity(intent);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelSettings.this, AddLevel.class);
                intent.putExtra("from", "addBtn");
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (LevelSetingsAdapter.selectedId.size() > 0) {
            getMenuInflater().inflate(R.menu.delete_selection_menu, menu);
        } else {
            getSupportActionBar().setTitle("Level Settings");
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            levelList = levelDao.queryForAll();
            if (!levelList.isEmpty()) {
                emptyLayout.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                adapter = new LevelSetingsAdapter(this, levelList,
                        LevelSettings.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                layout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        if (LevelSetingsAdapter.selectedId.size() > 0) {
            LevelSetingsAdapter.selectedId.clear();
            adapter.notifyDataSetChanged();
            invalidateOptionsMenu();

        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.delete_menu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete ?");
                builder.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                jsonObject = new JSONObject();

                                JSONArray jsonArray = new JSONArray();
                                for (int a = 0; a < LevelSetingsAdapter.selectedId.size(); a++) {

                                    JSONObject object = new JSONObject();

                                    try {
                                        object.put("id",
                                                LevelSetingsAdapter.selectedId.get(
                                                        a));
                                        jsonArray.put(object);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                try {
                                    jsonObject.put("deleteLevel", jsonArray);
                                    Log.i("response", jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                deleteLevelApiCall();

                            }
                        });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                            }
                        });
                builder.show();
                return true;

        }
        return false;
    }


    @Override
    public void onEditBtnClick(int position) {
        Intent intent = new Intent(LevelSettings.this, AddLevel.class);
        intent.putExtra("level_name", levelList.get(position).getLevelName());
        intent.putExtra("school_type", levelList.get(position).getSchoolType());
        intent.putExtra("id", levelList.get(position).getLevelId());
        intent.putExtra("rank", levelList.get(position).getRank());
        intent.putExtra("from", "editBtn");
        startActivity(intent);
    }

    private void deleteLevelApiCall() {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        String url =
                Login.urlBase + "/deleteLevel.php?deleteLevel=" + jsonObject.toString() + "&_db=" + db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog1.dismiss();
                        Log.i("response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                for (int a = 0; a < LevelSetingsAdapter.selectedId.size(); a++) {
                                    DeleteBuilder<LevelTable, Long> deleteBuilder = levelDao.deleteBuilder();
                                    deleteBuilder.where().eq("levelId",
                                            LevelSetingsAdapter.selectedId.get(
                                                    a));
                                    deleteBuilder.delete();
                                }
                                levelList = levelDao.queryForAll();
                                adapter = new LevelSetingsAdapter(
                                        LevelSettings.this, levelList,
                                        LevelSettings.this);

                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(LevelSettings.this,
                                        "Operation was successful",
                                        Toast.LENGTH_SHORT).show();
                            } else if (status.equals("failed")) {
                                Toast.makeText(LevelSettings.this,
                                        "Operation failed",
                                        Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException | SQLException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
                Toast.makeText(LevelSettings.this, "Error connecting to server",
                        Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(LevelSettings.this);
        requestQueue.add(stringRequest);
    }

}