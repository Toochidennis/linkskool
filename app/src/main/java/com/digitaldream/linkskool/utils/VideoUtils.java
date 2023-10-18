package com.digitaldream.linkskool.utils;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.VideoPlayerRecyclerView;
import com.digitaldream.linkskool.activities.YoutubePlayerActivity;
import com.digitaldream.linkskool.adapters.VideoAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.VideoTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class VideoUtils extends AppCompatActivity implements VideoAdapter.OnVideoItemClickListener {
    private VideoPlayerRecyclerView mRecyclerView;
    private List<VideoTable> videoList;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Dao<VideoTable,Long> videoDao;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_utils);
        recyclerView = findViewById(R.id.recycler_view);
        toolbar = findViewById(R.id.toolbar);
        Intent i = getIntent();
        String videoId = i.getStringExtra("videoId");
        String levelId = i.getStringExtra("levelId");
        try {
            databaseHelper = new DatabaseHelper(this);
            videoDao = DaoManager.createDao(databaseHelper.getConnectionSource(),VideoTable.class);
            QueryBuilder<VideoTable,Long> queryBuilder = videoDao.queryBuilder();
            queryBuilder.where().eq("videoId",videoId).and().eq("levelId",levelId);
            videoList = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Videos");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        VideoAdapter adapter = new VideoAdapter(VideoUtils.this,videoList,this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onVideoItemClick(int position) {
        Intent intent = new Intent(this, YoutubePlayerActivity.class);
        VideoTable vt = new VideoTable();
        vt.setVideoTitle(videoList.get(position).getVideoTitle());
        vt.setVideoUrl(videoList.get(position).getVideoUrl());
        vt.setThumbnail(videoList.get(position).getThumbnail());
        intent.putExtra("video_object",vt);
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
