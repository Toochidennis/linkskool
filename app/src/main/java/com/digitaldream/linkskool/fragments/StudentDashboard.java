package com.digitaldream.linkskool.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.activities.AnswerView;
import com.digitaldream.linkskool.activities.Login;
import com.digitaldream.linkskool.activities.NewsView;
import com.digitaldream.linkskool.activities.QuestionView;
import com.digitaldream.linkskool.adapters.QAAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.NewsTable;
import com.digitaldream.linkskool.models.StudentCourses;
import com.digitaldream.linkskool.utils.FunctionUtils;
import com.digitaldream.linkskool.utils.QuestionAccessViewSheet;
import com.digitaldream.linkskool.utils.QuestionBottomSheet;
import com.digitaldream.linkskool.utils.VolleyCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class StudentDashboard extends Fragment implements QAAdapter.OnQuestionClickListener,
        QuestionAccessViewSheet.OnQuestionSubmitListener {

    private Toolbar toolbar;
    private TextView username, student_class, studentInitials;
    private RecyclerView qaRecycler;
    private Dao<NewsTable, Long> newsDao;
    private List<NewsTable> newsTitleList = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private Dao<StudentCourses, Long> studentCoursesDao;
    private List<StudentCourses> studentCourses;

    public static String level, db;
    List<QAAdapter.QAObject> list;
    private QAAdapter adapter;
    private LinearLayout emptyState;
    private QAAdapter.QAObject feed;
    public static QuestionBottomSheet questionBottomSheet = null;
    private static String json = "";
    private boolean showDialog = true;
    private boolean allowRefresh = false;
    public static boolean refresh = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_dashboard,
                container, false);
        try {
            databaseHelper = new DatabaseHelper(getContext());
            newsDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    NewsTable.class);
            studentCoursesDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), StudentCourses.class);
            newsTitleList = newsDao.queryForAll();
            studentCourses = studentCoursesDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toolbar = view.findViewById(R.id.toolbar);
        qaRecycler = view.findViewById(R.id.qa_recycler);
        student_class = view.findViewById(R.id.student_class);
        studentInitials = view.findViewById(R.id.initials_student);
        emptyState = view.findViewById(R.id.qa_empty_state);
        username = view.findViewById(R.id.student_user);

        qaRecycler.setNestedScrollingEnabled(false);

        ((AppCompatActivity) (requireActivity())).setSupportActionBar(toolbar);
        ActionBar actionBar =
                ((AppCompatActivity) (requireActivity())).getSupportActionBar();


        SharedPreferences sharedPreferences =
                requireActivity().getSharedPreferences(
                        "loginDetail", Context.MODE_PRIVATE);
        String studentClass = sharedPreferences.getString("student_class", "");
        level = sharedPreferences.getString("level", "");
        db = sharedPreferences.getString("db", "");
        student_class.setText(studentClass.toUpperCase());

        String user = sharedPreferences.getString("user", "");
        String capUser = FunctionUtils.capitaliseFirstLetter(user);

        username.setText(capUser);
        assert actionBar != null;
        actionBar.setTitle(getString(R.string.app_name));
        String student_initial = user.substring(0, 1).toUpperCase();
        studentInitials.setText(student_initial);

        list = new ArrayList<>();
        qaRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new QAAdapter(getContext(), list, this);
        qaRecycler.setAdapter(adapter);


        FloatingActionButton addQuestionBtn = view.findViewById(
                R.id.add_question);
        addQuestionBtn.setOnClickListener(v -> {
            FragmentTransaction transaction =
                    ((FragmentActivity) requireContext())
                            .getSupportFragmentManager()
                            .beginTransaction();
            questionBottomSheet = new QuestionBottomSheet();
            questionBottomSheet.show(transaction, "questionBottomSheet");

        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!json.isEmpty()) {
            if (refresh) {
                getFeed(level);
            } else {
                buildJSON(json);
            }
        } else {
            getFeed(level);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onQuestionClick(int position) {
        QAAdapter.QAObject object = list.get(position);
        if (object.getFeedType().equals("20")) {
            Intent intent = new Intent(getContext(), QuestionView.class);
            intent.putExtra("feed", object);
            startActivity(intent);
        } else if (object.getFeedType().equals("21")) {
            Intent intent = new Intent(getContext(), AnswerView.class);
            intent.putExtra("feed", object);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getContext(), NewsView.class);
            intent.putExtra("feed", object);
            startActivity(intent);
        }
    }


    public void getFeed(String levelId) {

        String url = requireActivity().getString(R.string.base_url) + "/getFeed.php";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", levelId);

        FunctionUtils.sendRequestToServer(Request.Method.POST, url, requireContext(), hashMap,
                new VolleyCallback() {
                    @Override
                    public void onResponse(@NonNull String response) {
                        json = response;
                        list.clear();
                        buildJSON(response);
                    }

                    @Override
                    public void onError(@NonNull VolleyError error) {

                    }
                }, true);
    }

    private void buildJSON(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject object = jsonArray.getJSONObject(a);
                String id = object.getString("id");
                String title = object.getString("title");
                String user = object.getString("author_name");
                String date = object.getString("upload_date");
                String commentsNo = object.getString("no_of_comment");
                String shareCount = object.getString("no_of_share");
                String upvotes = object.getString("no_of_like");
                String parent = object.getString("parent");
                String desc = object.getString("description");
                String type1 = object.getString("type");
                String body = "";
                body = object.optString("body");

                feed = new QAAdapter.QAObject();
                feed.setUser(user);

                feed.setQuestionId(id);
                if (title.isEmpty()) {
                    feed.setQuestion(desc);

                } else {
                    feed.setQuestion(title);
                }
                feed.setAnswer("");
                feed.setPicUrl("");
                feed.setId(id);
                feed.setAnswerId(id);
                if (!body.isEmpty()) {

                    Object json = new JSONTokener(body).nextValue();

                    if (json instanceof JSONArray) {

                        JSONArray answer = new JSONArray(body);
                        boolean checktext = true; boolean checkImage = true;
                        for (int c = 0; c < answer.length(); c++) {
                            JSONObject object1 = answer.optJSONObject(c);
                            String type = object1.optString("type").trim();

                            if (type.equalsIgnoreCase("text") && checktext) {
                                String content = object1.optString("content");

                                feed.setPreText(content);
                                checktext = false;
                            }
                            if (type.equalsIgnoreCase("image") && checkImage) {
                                String content = object1.optString("src");
                                feed.setPicUrl(content);
                                checkImage = false;
                            }
                        }
                    }
                    feed.setAnswer(body);

                    //feed.setQuestion(desc);
                }

                feed.setDate(date);
                feed.setCommentNo(commentsNo);
                feed.setLikesNo(upvotes);
                feed.setShareNo(shareCount);
                feed.setFeedType(type1);
                list.add(feed);

            }

            Collections.reverse(list);
            if (list.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);

            } else {
                emptyState.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSubmit() {
    }
}