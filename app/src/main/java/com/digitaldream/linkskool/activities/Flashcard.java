package com.digitaldream.linkskool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.digitaldream.linkskool.dialog.CustomDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Flashcard extends AppCompatActivity {
    private String id;
    private String db;
    private DatabaseHelper databaseHelper;
    private Dao<CourseOutlineTable, Long> courseOutlineDao;
    private String courseName;
    private String accessLevel;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FlashCard");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(
                R.drawable.ic_arrow_back_black_24dp);

        SharedPreferences sharedPreferences = getSharedPreferences(
                "loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");
        accessLevel = sharedPreferences.getString("access_level", "");

        databaseHelper = new DatabaseHelper(this);
        try {
            courseOutlineDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(),
                    CourseOutlineTable.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent i = getIntent();
        id = i.getStringExtra("id");
        String title = i.getStringExtra("title");
        from = i.getStringExtra("from");
        String count = i.getStringExtra("count");
        String date = i.getStringExtra("date");
        try {
            title = title.substring(0, 1).toUpperCase() + title.substring(
                    1).toLowerCase();
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        String week = i.getStringExtra("week");
        TextView weekTxt = findViewById(R.id.week);
        TextView titleText = findViewById(R.id.debt_fee_title);
        TextView cardNo = findViewById(R.id.count);
        TextView dateText = findViewById(R.id.date_layout);
        cardNo.setText(count);
        dateText.setText(date);
        Button startBtn = findViewById(R.id.start_flashcard);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    QueryBuilder<CourseOutlineTable, Long> queryBuilder =
                            courseOutlineDao.queryBuilder();
                    queryBuilder.where().eq("id", id);
                    List<CourseOutlineTable> list = queryBuilder.query();
                    if (list != null && list.get(0).getJson() != null) {
                        String json = list.get(0).getJson();
                        Intent intent1 = new Intent(Flashcard.this,
                                FlashView.class);
                        intent1.putExtra("Json", json);
                        startActivity(intent1);

                    } else {
                        getFlashCard();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e) {
                    getFlashCard();
                }
            }
        });

        weekTxt.setText(week);
        titleText.setText(title);
    }

    private void getFlashCard() {
        final CustomDialog dialog = new CustomDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        String url = Login.urlBase + "/get_exam.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                if (!response.isEmpty()) {
                    if (from != null && !from.equals("flash_list")) {
                        Intent intent = new Intent(Flashcard.this,
                                FlashView.class);
                        intent.putExtra("Json", response);
                        startActivity(intent);

                        response = response.replace("'", "''");
                        UpdateBuilder<CourseOutlineTable, Long> updateBuilder = courseOutlineDao.updateBuilder();
                        try {
                            updateBuilder.updateColumnValue("json",
                                    response).where().eq("id", id);
                            updateBuilder.update();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Intent intent1 = new Intent(Flashcard.this,
                                FlashView.class);
                        intent1.putExtra("Json", response);
                        startActivity(intent1);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(Flashcard.this, error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("_db", db);
                param.put("id", id);
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;

    }
}
