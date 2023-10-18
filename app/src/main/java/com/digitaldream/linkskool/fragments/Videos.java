package com.digitaldream.linkskool.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.adapters.VideoAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.VideoPlayerRecyclerView;
import com.digitaldream.linkskool.models.VideoTable;
import com.digitaldream.linkskool.activities.YoutubePlayerActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;


/**
 * A simple {@link Fragment} subclass.
 */
public class Videos extends Fragment implements VideoAdapter.OnVideoItemClickListener {
    private VideoPlayerRecyclerView mRecyclerView;
    private List<VideoTable> videoList;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Dao<VideoTable,Long> videoDao;
    private DatabaseHelper databaseHelper;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_videos, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        toolbar = view.findViewById(R.id.toolbar);
        Intent i = getActivity().getIntent();
        String videoId = i.getStringExtra("videoId");
        try {
            databaseHelper = new DatabaseHelper(getContext());
            videoDao = DaoManager.createDao(databaseHelper.getConnectionSource(),VideoTable.class);
            QueryBuilder<VideoTable,Long> queryBuilder = videoDao.queryBuilder();
            queryBuilder.where().eq("videoId",videoId);
            videoList = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("Videos");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        VideoAdapter adapter = new VideoAdapter(getContext(),videoList,this);
        recyclerView.setAdapter(adapter);

        //initRecyclerView();
        return view;
    }

    @Override
    public void onVideoItemClick(int position) {
        //GiraffePlayer.play(getContext(),new VideoInfo("https://content.jwplatform.com/manifests/yp34SRmf.m3u8"));
        Intent intent = new Intent(getContext(), YoutubePlayerActivity.class);
        VideoTable vt = new VideoTable();
        vt.setVideoTitle(videoList.get(position).getVideoTitle());
        vt.setVideoUrl(videoList.get(position).getVideoUrl());
        vt.setThumbnail(videoList.get(position).getThumbnail());
        intent.putExtra("video_object",vt);
        startActivity(intent);

    }



   private void getVideos(){
       final ACProgressFlower dialog1 = new ACProgressFlower.Builder(getContext())
               .direction(ACProgressConstant.DIRECT_CLOCKWISE)
               .textMarginTop(10)
               .fadeColor(Color.DKGRAY).build();
       dialog1.setCanceledOnTouchOutside(false);
       dialog1.show();
       String url = "http://linkskool.com/cbt/api/getVideo.php";
       StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
           @Override
           public void onResponse(String response) {
               dialog1.dismiss();
               Log.i("videos",response);

               try {
                   JSONArray jsonArray1 = new JSONArray(response);
                   for(int a =0;a<jsonArray1.length();a++){
                       JSONObject jsonObject = jsonArray1.getJSONObject(a);
                       String videoCategory = jsonObject.getString("name");
                       JSONArray categoryArray = jsonObject.getJSONArray("category");
                       for(int i=0;i<categoryArray.length();i++){
                           JSONObject jsonObject1 = categoryArray.getJSONObject(i);
                           JSONArray videosArray = jsonObject1.getJSONArray("videos");
                           for(int b=0;b<videosArray.length();b++){
                               JSONObject jsonObject2 = videosArray.getJSONObject(b);
                               String videoTitle =jsonObject2.getString("title");
                               String videoUrl = jsonObject2.getString("url");
                               String videoThumnail = jsonObject2.getString("thumbnail");
                               Log.i("videoitems",videoTitle +" "+videoUrl+" "+videoThumnail);
                               VideoTable vt = new VideoTable();
                               vt.setVideoTitle(videoTitle);
                               vt.setThumbnail(videoThumnail);
                               vt.setVideoUrl(videoUrl);
                               vt.setVideoSubject(videoCategory);
                               videoDao.create(vt);

                           }
                       }
                   }
                   videoList = videoDao.queryForAll();
                   VideoAdapter adapter = new VideoAdapter(getContext(),videoList, Videos.this);
                   recyclerView.setAdapter(adapter);
               } catch (JSONException e) {
                   e.printStackTrace();
               }catch (SQLException e){
                   e.printStackTrace();
               }
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {

           }
       });

       RequestQueue requestQueue = Volley.newRequestQueue(getContext());
       requestQueue.add(stringRequest);
   }
}
