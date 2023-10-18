package com.digitaldream.linkskool.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.digitaldream.linkskool.adapters.AllTeacherAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.TeachersTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;

public class AllTeachers extends AppCompatActivity implements AllTeacherAdapter.OnTeacherClickListener {
    private DatabaseHelper databaseHelper;
    private Dao<TeachersTable,Long> teacherDao;
    private List<TeachersTable> teachersList;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    public static String teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_teachers);
        toolbar =findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Teachers");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        databaseHelper = new DatabaseHelper(this);
        try {
            teacherDao = DaoManager.createDao(databaseHelper.getConnectionSource(),TeachersTable.class);
            teachersList = teacherDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        recyclerView = findViewById(R.id.all_teacher_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        AllTeacherAdapter adapter = new AllTeacherAdapter(this,teachersList,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onTeacherClick(int position) {
        Intent intent = new Intent();
        intent.putExtra("teacherId", teachersList.get(position).getStaffId());
        setResult(RESULT_OK, intent);
        finish();

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
}
