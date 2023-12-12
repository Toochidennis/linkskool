package com.digitaldream.winskool.activities;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.digitaldream.winskool.adapters.VideoCourseAdapter;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.VideoTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.utils.VideoUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;

public class VideoCourses extends AppCompatActivity implements VideoCourseAdapter.OnVideoCourseClickListener {
    RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private Dao<VideoTable,Long> videoTableDao;
    private List<VideoTable> videoList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_courses);
        recyclerView = findViewById(R.id.course_video);
        String levelId = getIntent().getStringExtra("levelId");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Courses");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        databaseHelper = new DatabaseHelper(this);
        try {
            videoTableDao = DaoManager.createDao(databaseHelper.getConnectionSource(),VideoTable.class);
            videoList = videoTableDao.queryBuilder().groupBy("videoSubject").where().eq("levelId",levelId).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        VideoCourseAdapter adapter = new VideoCourseAdapter(this,videoList,this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onVideoCourseClick(int position) {
        Intent intent = new Intent(this, VideoUtils.class);
        intent.putExtra("videoId",videoList.get(position).getVideoId());
        intent.putExtra("levelId",videoList.get(position).getLevelId());
        startActivity(intent);
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
