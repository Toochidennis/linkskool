package com.digitaldream.winskool.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.activities.Login;
import com.digitaldream.winskool.activities.NewsPage;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.NewsTable;
import com.digitaldream.winskool.adapters.NewsAdapter;
import com.digitaldream.winskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentNews extends Fragment implements NewsAdapter.OnNewsClickListener {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private Dao<NewsTable,Long> newsDao;
    private List<NewsTable> newsList;
    private SwipeRefreshLayout newsRefresh;
    private String db;
    private LinearLayout emptylayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_games, container, false);
        setHasOptionsMenu(true);
        newsRefresh = view.findViewById(R.id.swipeRefresh_news);
        emptylayout = view.findViewById(R.id.news_empty_state);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");

        databaseHelper = new DatabaseHelper(getContext());
        try {
            newsDao = DaoManager.createDao(databaseHelper.getConnectionSource(),NewsTable.class);
            newsList = newsDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("News");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        recyclerView = view.findViewById(R.id.all_news_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        if(!newsList.isEmpty()) {
            emptylayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            NewsAdapter newsAdapter = new NewsAdapter(getContext(), newsList, this);
            recyclerView.setAdapter(newsAdapter);
        }else{
            recyclerView.setVisibility(View.GONE);
            emptylayout.setVisibility(View.VISIBLE);
        }

        newsRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNewsFeed();
            }
        });


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onNewsClick(int position) {
        Intent intent = new Intent(getContext(), NewsPage.class);
        intent.putExtra("newsId",newsList.get(position).getNewsId());
        startActivity(intent);
    }

    private void refreshNewsFeed(){
        String url= Login.urlBase+"/allNews.php?_db="+db;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONObject jsonObject8 = jsonObject.getJSONObject("allNews");
                    JSONArray newsArray = jsonObject8.getJSONArray("rows");
                    if (newsArray.length() > 0) {
                        TableUtils.clearTable(databaseHelper.getConnectionSource(),NewsTable.class);
                        for (int i = 0; i < newsArray.length(); i++) {
                            JSONArray jsonArray10 = newsArray.getJSONArray(i);
                            String newsId = jsonArray10.getString(0);
                            String newsSubject = jsonArray10.getString(1);
                            String newsContent = jsonArray10.getString(2);
                            String datePosted = jsonArray10.getString(3);
                            String newsImageUrl = jsonArray10.getString(8);
                            QueryBuilder<NewsTable, Long> queryBuilder = newsDao.queryBuilder();
                            queryBuilder.where().eq("newsId", newsId);
                            newsList = queryBuilder.query();
                            if (newsList.isEmpty()) {
                                NewsTable nt = new NewsTable();
                                nt.setNewsId(newsId);
                                nt.setNewsSubject(stripHtml(newsSubject));
                                nt.setNewsContent(stripHtml(newsContent));
                                nt.setDatePosted(stripHtml(datePosted));
                                nt.setNewsImageUrl(stripHtml(newsImageUrl));
                                newsDao.create(nt);
                            }
                            newsList = newsDao.queryForAll();
                            if(!newsList.isEmpty()) {
                                emptylayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(linearLayoutManager);
                                recyclerView.setHasFixedSize(true);
                                NewsAdapter newsAdapter = new NewsAdapter(getContext(), newsList, StudentNews.this);
                                recyclerView.setAdapter(newsAdapter);
                                recyclerView.setNestedScrollingEnabled(false);
                            }else{
                                recyclerView.setVisibility(View.GONE);
                                emptylayout.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                    newsRefresh.setRefreshing(false);


                } catch(JSONException | SQLException e){
                    e.printStackTrace();
                }

                Toast.makeText(getContext(),"News updated",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newsRefresh.setRefreshing(false);
                Toast.makeText(getContext(),"Error connecting to database",Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }
}
