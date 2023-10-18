package com.digitaldream.linkskool.fragments;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.activities.VideoCourses;
import com.digitaldream.linkskool.adapters.VideoCategoryAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.VideoUtilTable;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.VideoTable;
import com.digitaldream.linkskool.dialog.CustomDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoCategories extends Fragment implements VideoCategoryAdapter.OnVideoTitleClickListener {

    private List<VideoTable> videoList;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Dao<VideoTable,Long> videoDao;
    private DatabaseHelper databaseHelper;
    private Dao<VideoUtilTable,Long> videoUtilDao;
    private List<VideoTable> videoUtilTableList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_categories, container, false);
        setHasOptionsMenu(true);
        databaseHelper = new DatabaseHelper(getContext());
        try {
            videoDao = DaoManager.createDao(databaseHelper.getConnectionSource(),VideoTable.class);
            videoUtilDao = DaoManager.createDao(databaseHelper.getConnectionSource(),VideoUtilTable.class);
            videoUtilTableList=videoDao.queryBuilder().groupBy("levelId").query();
            videoList = videoDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Video Tutorials");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        recyclerView = view.findViewById(R.id.video_categories);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        if(videoList.isEmpty()){
            getVideos();
        }else{
            VideoCategoryAdapter adapter = new VideoCategoryAdapter(getContext(),videoUtilTableList,this);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    private void getVideos(){
        final CustomDialog dialog = new CustomDialog(getActivity());
        dialog.show();
        String url = "http://linkskool.com/cbt/api/getVideo.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();

                try {
                    JSONArray jsonArray1 = new JSONArray(response);
                    for(int a =0;a<jsonArray1.length();a++){
                        JSONObject jsonObject = jsonArray1.getJSONObject(a);
                        String videoCategory = jsonObject.getString("name");
                        String videoId = jsonObject.getString("id");


                        JSONArray categoryArray = jsonObject.getJSONArray("category");
                        for(int i=0;i<categoryArray.length();i++){
                            JSONObject jsonObject1 = categoryArray.getJSONObject(i);
                            JSONArray videosArray = jsonObject1.getJSONArray("videos");
                            String levelId = jsonObject1.getString("level");
                            String levelName = jsonObject1.getString("level_name");
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
                                vt.setVideoId(videoId);
                                vt.setVideoSubject(videoCategory);
                                vt.setLevelId(levelId);
                                vt.setLevelName(levelName);
                                videoDao.create(vt);
                                }

                        }

                        VideoUtilTable vut= new VideoUtilTable();
                        vut.setVideoCatId(videoId);
                        vut.setVideoCatTitle(videoCategory);
                        videoUtilDao.create(vut);

                    }
                    videoList = videoDao.queryForAll();
                    //videoUtilTableList=videoDao.queryForAll();
                    videoUtilTableList=videoDao.queryBuilder().groupBy("levelId").query();
                    VideoCategoryAdapter adapter = new VideoCategoryAdapter(getContext(),videoUtilTableList, VideoCategories.this);
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
                dialog.dismiss();
                Toast.makeText(getContext(), "Can't fetch video at the moment", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onVideoClick(int position) {
        Intent intent = new Intent(getContext(), VideoCourses.class);
        intent.putExtra("levelId",videoUtilTableList.get(position).getLevelId());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
