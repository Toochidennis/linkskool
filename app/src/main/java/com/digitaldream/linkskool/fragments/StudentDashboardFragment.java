package com.digitaldream.linkskool.fragments;


import static android.content.Context.MODE_PRIVATE;
import static com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.activities.Login;
import com.digitaldream.linkskool.adapters.StudentDashboardAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.AdminCommentsModel;
import com.digitaldream.linkskool.models.AdminDashboardModel;
import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.digitaldream.linkskool.models.ExamType;
import com.digitaldream.linkskool.models.NewsTable;
import com.digitaldream.linkskool.models.StudentCourses;
import com.digitaldream.linkskool.models.StudentResultDownloadTable;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.models.VideoTable;
import com.digitaldream.linkskool.models.VideoUtilTable;
import com.digitaldream.linkskool.utils.FunctionUtils;
import com.digitaldream.linkskool.utils.QuestionBottomSheet;
import com.digitaldream.linkskool.utils.VolleyCallback;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StudentDashboardFragment extends Fragment {


    private TextView errorMessageTxt;
    private RecyclerView questionRecyclerView;
    private Button addQuestionBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView usernameTxt;
    private ImageButton logoutBtn;
    private ProgressBar progressBar;


    List<AdminDashboardModel> questionList = new ArrayList<>();
    private StudentDashboardAdapter questionAdapter;
    private DatabaseHelper databaseHelper;

    public static QuestionBottomSheet questionBottomSheet = null;

    private static String json = "";
    public static String levelId;

    public static boolean refresh = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpViews(view);

        init();
    }

    private void setUpViews(View view) {
        questionRecyclerView = view.findViewById(R.id.questionRecyclerView);
        errorMessageTxt = view.findViewById(R.id.errorMessageTxt);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        addQuestionBtn = view.findViewById(R.id.addQuestionBtn);
        usernameTxt = view.findViewById(R.id.usernameTxt);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        progressBar = view.findViewById(R.id.progressBar);

        databaseHelper = new DatabaseHelper(requireContext());

        setUpToolBar();
    }


    private void setUpToolBar() {
        SharedPreferences sharedPreferences =
                requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE);
        levelId = sharedPreferences.getString("level", "");
        String username = sharedPreferences.getString("user", "");
        usernameTxt.setText(capitaliseFirstLetter(username));


        logoutBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Continue to logout?");
            builder.setNegativeButton("Cancel", (dialog, which) -> {
            });

            builder.setPositiveButton("Logout", (dialog, which) -> logout());
            builder.show();
        });
    }

    private void init() {
        addQuestionBtn.setOnClickListener(v -> {
            questionBottomSheet = new QuestionBottomSheet();
            questionBottomSheet.show(getParentFragmentManager(), "questionBottomSheet");
        });


        refreshFeeds();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpRecyclerView();

        if (!json.isEmpty()) {
            if (refresh) {
                getFeed();
            } else {
                buildJSON(json);
            }
        } else {
            getFeed();
        }
    }


    public void getFeed() {
        String url = requireActivity().getString(R.string.base_url) + "/getFeed.php";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", levelId);

        progressBar.setVisibility(View.VISIBLE);
        errorMessageTxt.setVisibility(View.GONE);

        questionList.clear();

        FunctionUtils.sendRequestToServer(Request.Method.POST, url, requireContext(), hashMap,
                new VolleyCallback() {
                    @Override
                    public void onResponse(@NonNull String response) {
                        json = response;
                        buildJSON(response);
                    }

                    @Override
                    public void onError(@NonNull VolleyError error) {
                        errorMessageTxt.setVisibility(View.VISIBLE);
                        errorMessageTxt.setText(getString(R.string.no_internet));
                        progressBar.setVisibility(View.GONE);
                    }
                }, false);
    }


    private void buildJSON(String response) {
        try {
            questionList.clear();
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
                        new AdminDashboardModel(id, username, title,
                                commentsNo, likesNo, shareNo, type, date, "",replyList);

                AdminDashboardModel existingQuestionModel =
                        questionMap.get(description.strip());

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
        questionAdapter = new StudentDashboardAdapter(questionList);
        questionRecyclerView.hasFixedSize();
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        questionRecyclerView.setAdapter(questionAdapter);
    }

    private void refreshFeeds() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getFeed();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void logout() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(
                "loginDetail", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginStatus", false);
        editor.putString("user", "");
        editor.putString("school_name", "");
        editor.putString("attachment", "");
        editor.apply();

        try {
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    StudentTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    NewsTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    StudentCourses.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    StudentResultDownloadTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    VideoTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    VideoUtilTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    CourseOutlineTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    ExamType.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(requireContext(), Login.class);
        startActivity(intent);
        requireActivity().finish();
    }
}