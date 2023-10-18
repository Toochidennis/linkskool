package com.digitaldream.linkskool.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;

import com.digitaldream.linkskool.adapters.ElearningCourseAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ElearningCourses extends AppCompatActivity implements ElearningCourseAdapter.OnCourseClickListener {
    RecyclerView recyclerView;
    private List<CourseTable> coursesList;
    DatabaseHelper databaseHelper;
    Dao<CourseTable,Long> courseTableDao;
    String levelId;
    private Toolbar toolbar;
    private List<String> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elearning_courses);
        recyclerView = findViewById(R.id.e_learning_course_recycler);
        String levelName = getIntent().getStringExtra("levelName");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle(levelName.toUpperCase()+" Courses");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        levelId = getIntent().getStringExtra("levelId");
        databaseHelper = new DatabaseHelper(this);
        try {
            courseTableDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseTable.class);
            QueryBuilder<CourseTable,Long> queryBuilder = courseTableDao.queryBuilder();
            queryBuilder.groupBy("courseId").where().eq("levelId",levelId);
            coursesList = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ElearningCourseAdapter adapter = new ElearningCourseAdapter(this,coursesList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onCourseClick(int position) {

        Intent intent = new Intent(this,CourseOutlines.class);
        intent.putExtra("levelId",coursesList.get(position).getLevelId());
        intent.putExtra("courseName",coursesList.get(position).getCourseName());
        intent.putExtra("courseId",coursesList.get(position).getCourseId());
        startActivity(intent);
        //Toast.makeText(this,"this",Toast.LENGTH_SHORT).show();
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
