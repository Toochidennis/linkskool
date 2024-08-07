package com.digitaldream.linkskool.fragments;


import static android.content.Context.MODE_PRIVATE;
import static com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter;

import android.content.Context;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.activities.Login;
import com.digitaldream.linkskool.activities.StaffUtils;
import com.digitaldream.linkskool.adapters.StaffDashboardAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.AdminCommentsModel;
import com.digitaldream.linkskool.models.AdminDashboardModel;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.models.ExamType;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.models.NewsTable;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.models.TeachersTable;
import com.digitaldream.linkskool.models.VideoTable;
import com.digitaldream.linkskool.models.VideoUtilTable;
import com.digitaldream.linkskool.utils.FunctionUtils;
import com.digitaldream.linkskool.utils.QuestionBottomSheet;
import com.digitaldream.linkskool.utils.VolleyCallback;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
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


public class StaffDashboardFragment extends Fragment {

    private TextView classCountTxt, courseCountTxt, errorMessageTxt;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView questionRecyclerView;
    private CardView formClassBtn, courseBtn;
    private Button addQuestionBtn;
    private TextView usernameTxt;
    private ImageButton logoutBtn;
    ProgressBar progressBar;


    private DatabaseHelper databaseHelper;
    private StaffDashboardAdapter questionAdapter;
    public static QuestionBottomSheet questionBottomSheet = null;


    private List<CourseTable> courseList;
    List<AdminDashboardModel> questionList = new ArrayList<>();

    private static String json = "";
    public static boolean refresh = false;


    public StaffDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpViews(view);

        init();
    }

    private void setUpViews(View view) {
        classCountTxt = view.findViewById(R.id.noOfClassTxt);
        courseCountTxt = view.findViewById(R.id.noOfCourseTxt);
        courseBtn = view.findViewById(R.id.courseBtn);
        formClassBtn = view.findViewById(R.id.formClassBtn);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        errorMessageTxt = view.findViewById(R.id.errorMessageTxt);
        addQuestionBtn = view.findViewById(R.id.addQuestionBtn);
        questionRecyclerView = view.findViewById(R.id.questionRecyclerView);
        usernameTxt = view.findViewById(R.id.usernameTxt);
        logoutBtn = view.findViewById(R.id.logoutButton);
        progressBar = view.findViewById(R.id.progressBar);

        setUpToolbar();
    }


    private void setUpToolbar() {
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
        try {
            databaseHelper = new DatabaseHelper(requireActivity());

            Dao<CourseTable, Long> mCourseDao =
                    DaoManager.createDao(databaseHelper.getConnectionSource(),
                            CourseTable.class);
            Dao<ClassNameTable, Long> classDao =
                    DaoManager.createDao(databaseHelper.getConnectionSource(),
                            ClassNameTable.class);

            courseList = mCourseDao.queryForAll();
            List<ClassNameTable> classList = classDao.queryForAll();

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginDetail", MODE_PRIVATE);
            String username = sharedPreferences.getString("user", "");

            classCountTxt.setText(String.valueOf(classList.size()));
            courseCountTxt.setText(String.valueOf(courseList.size()));
            usernameTxt.setText(capitaliseFirstLetter(username));

            courseBtn.setOnClickListener(v1 -> {
                Intent intent = new Intent(getContext(), StaffUtils.class);
                intent.putExtra("from", "course");
                startActivity(intent);
            });

            formClassBtn.setOnClickListener(v12 -> {
                Intent intent = new Intent(getContext(), StaffUtils.class);
                intent.putExtra("from", "student");
                startActivity(intent);
            });


            addQuestionBtn.setOnClickListener(v13 -> {
                questionBottomSheet = new QuestionBottomSheet();
                questionBottomSheet.show(requireActivity().getSupportFragmentManager(),
                        "questionBottomSheet");
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        refreshFeeds();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpRecyclerView();

        if (json.isEmpty()) {
            getFeed();
        } else {
            if (refresh) {
                getFeed();
            } else {
                parseJSON(json);
            }
        }
    }

    private void getFeed() {
        String url = requireActivity().getString(R.string.base_url) + "/getFeed.php";
        HashMap<String, String> stringMap = new HashMap<>();
        stringMap.put("id", getClassObject());

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


    private String getClassObject() {
        JSONArray jsonArray = new JSONArray();

        for (int a = 0; a < courseList.size(); a++) {
            JSONObject staffObj = new JSONObject();
            try {
                staffObj.put("course", courseList.get(a).getCourseId());
                staffObj.put("course_name", courseList.get(a).getCourseName());
                staffObj.put("level", courseList.get(a).getLevelId());
                staffObj.put("level_name", courseList.get(a).getLevelName());
                jsonArray.put(staffObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonArray.toString();
    }


    private void parseJSON(String response) {
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
        questionAdapter = new StaffDashboardAdapter(questionList);
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


/*    @Override
    public void onQuestionClick(int position) {
        StaffDashboardModel object = questionList.get(position);

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


    private void logout() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginStatus", false);
        editor.putString("user", "");
        editor.putString("school_name", "");
        editor.apply();

        try {
            TableUtils.clearTable(databaseHelper.getConnectionSource(), StudentTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), TeachersTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), ClassNameTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), LevelTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), NewsTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), CourseTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), CourseOutlineTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), VideoTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), VideoUtilTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), ExamType.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(requireContext(), Login.class);
        startActivity(intent);
        requireActivity().finish();
    }

}