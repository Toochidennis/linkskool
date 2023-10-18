package com.digitaldream.linkskool.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.adapters.ElearningLevelAdapter;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.models.LevelTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ELearning extends AppCompatActivity implements ElearningLevelAdapter.OnLevelClickListener {
    RecyclerView recyclerView;
    private List<CourseTable> coursesList;
    DatabaseHelper databaseHelper;
    Dao<CourseTable,Long> courseTableDao;
    private Toolbar toolbar;
    private List<LevelTable> levelList;
    private List<Object> list = new ArrayList<>();
    private List<CourseTable> courseLists= new ArrayList<>();
    private List<CourseTable> courseLists1= new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elearning);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Levels");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        recyclerView = findViewById(R.id.e_learning_level_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        databaseHelper = new DatabaseHelper(this);
        try {
            courseTableDao = DaoManager.createDao(databaseHelper.getConnectionSource(),CourseTable.class);
            coursesList = courseTableDao.queryBuilder().groupBy("levelId").query();
            //coursesList = courseTableDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*for(int i =0;i<coursesList.size();i++){
            if(!list.contains(coursesList.get(i).getLevelName())){
                CourseTable ct = new CourseTable();
                ct.setLevelName(coursesList.get(i).getLevelName());
                ct.setLevelId(coursesList.get(i).getLevelId());
                courseLists.add(ct);
                list.add(coursesList.get(i).getLevelName());
            }
        }
        courseLists1.addAll(courseLists);*/

        ElearningLevelAdapter adapter = new ElearningLevelAdapter(this,coursesList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

    }

    @Override
    public void onLevelClick(int position) {
        Intent intent = new Intent(this, ElearningCourses.class);
        intent.putExtra("levelId",coursesList.get(position).getLevelId());
        intent.putExtra("levelName",coursesList.get(position).getLevelName());
        Log.i("levelname",coursesList.get(position).getLevelName());
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
}
