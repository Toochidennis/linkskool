package com.digitaldream.linkskool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.config.YoutubeConfig;
import com.digitaldream.linkskool.models.VideoTable;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayerActivity extends YouTubeBaseActivity {
    YouTubePlayerView mYoutubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    private String url;
    TextView videoTitle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);
        mYoutubePlayerView = findViewById(R.id.youtube_player);
        videoTitle = findViewById(R.id.video_title);
        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        Intent i =getIntent();
        VideoTable vt = (VideoTable) i.getSerializableExtra("video_object");
        videoTitle.setText(vt.getVideoTitle());
        getActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>LinkSkool </font>"));
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        url = vt.getVideoUrl();
        Log.i("response",url);
        url = url.substring(30,url.indexOf("?"));
        Log.i("response1",url);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(url);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        mYoutubePlayerView.initialize(YoutubeConfig.getYoutubeApiKey(),onInitializedListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
}
