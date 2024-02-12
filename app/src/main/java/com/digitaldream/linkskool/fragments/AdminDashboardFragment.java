package com.digitaldream.linkskool.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.activities.ClassListUtil;
import com.digitaldream.linkskool.activities.Settings;
import com.digitaldream.linkskool.activities.StudentContacts;
import com.digitaldream.linkskool.activities.TeacherContacts;
import com.digitaldream.linkskool.adapters.AdminDashboardAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.AdminDashboardModel;
import com.digitaldream.linkskool.models.AdminCommentsModel;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.models.TeachersTable;
import com.digitaldream.linkskool.utils.AddNewsBottomSheet;
import com.digitaldream.linkskool.utils.FunctionUtils;
import com.digitaldream.linkskool.utils.QuestionBottomSheet;
import com.digitaldream.linkskool.utils.VolleyCallback;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminDashboardFragment extends Fragment {

    private TextView noOfClassesTxt, noOfStudentsTxt, noOfTeachersTxt, errorMessageTxt;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CardView classBtn, studentBtn, teachersBtn;
    private Button addQuestionBtn;
    private RecyclerView questionRecyclerView;
    private ImageView settingsButton;
    ProgressBar progressBar;
    List<AdminDashboardModel> questionList = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private AdminDashboardAdapter questionAdapter;
    public static QuestionBottomSheet questionBottomSheet = null;


    public static String db;
    public static String json = "";
    public static boolean refresh = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpViews(view);

        init();

    }

    private void setUpViews(View view) {
        errorMessageTxt = view.findViewById(R.id.errorMessageTxt);
        settingsButton = view.findViewById(R.id.settingsButton);
        noOfStudentsTxt = view.findViewById(R.id.noOfStudentsTxt);
        noOfTeachersTxt = view.findViewById(R.id.noOfTeachersTxt);
        noOfClassesTxt = view.findViewById(R.id.noOfClassTxt);
        studentBtn = view.findViewById(R.id.studentBtn);
        teachersBtn = view.findViewById(R.id.teachersBtn);
        classBtn = view.findViewById(R.id.classBtn);
        questionRecyclerView = view.findViewById(R.id.questionRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        addQuestionBtn = view.findViewById(R.id.addQuestionBtn);
        progressBar = view.findViewById(R.id.progressBar);

        databaseHelper = new DatabaseHelper(requireContext());
    }

    private void handleClicks() {
        settingsButton.setOnClickListener(v -> startActivity(new Intent(requireContext(), Settings.class)));

        teachersBtn.setOnClickListener(v -> {
            Intent intent3 = new Intent(getContext(),
                    TeacherContacts.class);
            startActivity(intent3);
        });


        classBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ClassListUtil.class);
            startActivity(intent);
        });

        addQuestionBtn.setOnClickListener(v -> {
            AddNewsBottomSheet addNewsBottomSheet = new AddNewsBottomSheet();
            addNewsBottomSheet.show(getParentFragmentManager(), "newsBottomSheet");
        });

    }

    private void init() {
        try {
            Dao<StudentTable, Long> mStudentDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), StudentTable.class);
            Dao<TeachersTable, Long> mTeacherDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), TeachersTable.class);
            Dao<ClassNameTable, Long> mClassDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), ClassNameTable.class);
            List<StudentTable> mStudentList = mStudentDao.queryForAll();
            List<TeachersTable> mTeacherList = mTeacherDao.queryForAll();
            List<ClassNameTable> mClassList = mClassDao.queryForAll();

            noOfStudentsTxt.setText(String.valueOf(mStudentList.size()));
            noOfTeachersTxt.setText(String.valueOf(mTeacherList.size()));
            noOfClassesTxt.setText(String.valueOf(mClassList.size()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        studentBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StudentContacts.class);
            startActivity(intent);
        });


        refreshFeeds();

        handleClicks();
    }


    @Override
    public void onResume() {
        super.onResume();
        setUpRecyclerView();

        if (json.isEmpty()) {
            getFeeds();
        } else {
            if (refresh) {
                getFeeds();
            } else {
                parseJSON(json);
            }
        }
    }

/*    @Override
    public void onQuestionClick(int position) {
        QAObject object = list.get(position);
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

    }*/

    private void getFeeds() {
        String url = requireActivity().getString(R.string.base_url) + "/getFeed.php";
        HashMap<String, String> stringMap = new HashMap<>();
        stringMap.put("id", "0");
        stringMap.put("_db", db);
        stringMap.put("page", "1");

        progressBar.setVisibility(View.VISIBLE);
        errorMessageTxt.setVisibility(View.GONE);

        questionList.clear();

        FunctionUtils.sendRequestToServer(Request.Method.POST, url, requireContext(), stringMap,
                new VolleyCallback() {
                    @Override
                    public void onResponse(@NonNull String response) {
                        json = response;
                        parseJSON(response);
                    }

                    @Override
                    public void onError(@NonNull VolleyError error) {
                        errorMessageTxt.setVisibility(View.VISIBLE);
                        errorMessageTxt.setText(getString(R.string.no_internet));
                        progressBar.setVisibility(View.GONE);
                    }
                }, false);

    }

    private void parseJSON(String response) {
        try {
            HashMap<String, AdminDashboardModel> questionMap = new HashMap<>();
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject questionObject = jsonArray.getJSONObject(i);
                List<AdminCommentsModel> replyList = new ArrayList<>();

                String id = questionObject.getString("id");
                String title = questionObject.getString("title");
                String username = questionObject.getString("author_name");
                String date = questionObject.getString("upload_date");
                String commentsNo = questionObject.getString("no_of_comment");
                String shareNo = questionObject.getString("no_of_share");
                String likesNo = questionObject.getString("no_of_like");
                String type = questionObject.getString("type");
                String description = questionObject.getString("description");
                String body = questionObject.getString("body");
                String imageUrl = questionObject.getString("picref");

                if (!body.isEmpty()) {
                    JSONArray bodyArray = new JSONArray(body);

                    for (int j = 0; j < bodyArray.length(); j++) {
                        JSONObject bodyObject = bodyArray.getJSONObject(j);
                        String bodyType = bodyObject.getString("type");
                        String content = "";
                        String image = "";

                        if (bodyType.equalsIgnoreCase("text")) {
                            content = bodyObject.getString("content");
                        } else if (bodyType.equalsIgnoreCase("image")) {
                            image = bodyObject.getString("src");
                        }

                        replyList.add(new AdminCommentsModel(id, username, content, image, date));
                    }
                }

                AdminDashboardModel questionModel =
                        new AdminDashboardModel(
                                id, username, title,
                                commentsNo, likesNo, shareNo, type, date,imageUrl, replyList);

                AdminDashboardModel existingQuestionModel = questionMap.get(description.strip());

                if (existingQuestionModel != null) {
                    existingQuestionModel.getReplyList().addAll(replyList);
                } else {
                    if (!title.isEmpty()) {
                        questionMap.put(title.strip(), questionModel);
                    }
                }
            }

            for (Map.Entry<String, AdminDashboardModel> entry : questionMap.entrySet()) {
                questionList.add(entry.getValue());
            }

            Collections.reverse(questionList);

            progressBar.setVisibility(View.GONE);

            if (questionList.isEmpty()) {
                errorMessageTxt.setVisibility(View.VISIBLE);
                errorMessageTxt.setText(getString(R.string.there_is_no_feeds_yet));
            } else {
                errorMessageTxt.setVisibility(View.GONE);
            }

            questionAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
            errorMessageTxt.setVisibility(View.VISIBLE);
            errorMessageTxt.setText(getString(R.string.no_internet));
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setUpRecyclerView() {
        questionAdapter = new AdminDashboardAdapter(questionList);
        questionRecyclerView.hasFixedSize();
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        questionRecyclerView.setAdapter(questionAdapter);
    }

    private void refreshFeeds() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getFeeds();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

}