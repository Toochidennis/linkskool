package com.digitaldream.winskool.fragments;


import static android.content.Context.MODE_PRIVATE;

import static com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.activities.Login;
import com.digitaldream.winskool.activities.StaffUtils;
import com.digitaldream.winskool.adapters.StaffDashboardAdapter;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.dialog.ContactUsDialog;
import com.digitaldream.winskool.models.ClassNameTable;
import com.digitaldream.winskool.models.CourseOutlineTable;
import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.models.ExamType;
import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.models.NewsTable;
import com.digitaldream.winskool.models.StaffQuestionModel;
import com.digitaldream.winskool.models.StaffReplyModel;
import com.digitaldream.winskool.models.StudentTable;
import com.digitaldream.winskool.models.TeachersTable;
import com.digitaldream.winskool.models.VideoTable;
import com.digitaldream.winskool.models.VideoUtilTable;
import com.digitaldream.winskool.utils.FunctionUtils;
import com.digitaldream.winskool.utils.QuestionBottomSheet;
import com.digitaldream.winskool.utils.VolleyCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton addQuestionBtn;
    private TextView usernameTxt;


    private DatabaseHelper databaseHelper;
    private Toolbar toolbar;
    private StaffDashboardAdapter questionAdapter;
    public static QuestionBottomSheet questionBottomSheet = null;


    private List<CourseTable> courseList;
    List<StaffQuestionModel> questionList = new ArrayList<>();

    private static String json = "";
    public static boolean refresh = false;


    public StaffDashboardFragment() {
        // Required empty public constructor
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
        toolbar = view.findViewById(R.id.toolbar);
        classCountTxt = view.findViewById(R.id.noOfClassTxt);
        courseCountTxt = view.findViewById(R.id.noOfCourseTxt);
        courseBtn = view.findViewById(R.id.courseBtn);
        formClassBtn = view.findViewById(R.id.formClassBtn);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        errorMessageTxt = view.findViewById(R.id.errorMessageTxt);
        addQuestionBtn = view.findViewById(R.id.addQuestionBtn);
        questionRecyclerView = view.findViewById(R.id.questionRecyclerView);
        usernameTxt = view.findViewById(R.id.usernameTxt);

        setUpToolbar();
    }


    private void setUpToolbar() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("");
        MenuHost mMenuHost = requireActivity();

        mMenuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.staff_logout_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.staff_logout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setMessage("Continue to logout?");
                        builder.setNegativeButton("Cancel", (dialog, which) -> {
                        });

                        builder.setPositiveButton("Logout", (dialog, which) -> logout());
                        builder.show();
                        return true;

                    case R.id.info:
                        ContactUsDialog dialog = new ContactUsDialog(requireActivity());
                        dialog.show();
                        Window window = dialog.getWindow();
                        assert window != null;
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        return true;

                    default:
                        return false;

                }
            }
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
            HashMap<String, StaffQuestionModel> questionMap = new HashMap<>();

            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject questionObject = jsonArray.getJSONObject(i);
                List<StaffReplyModel> replyList = new ArrayList<>();

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

                        replyList.add(new StaffReplyModel(id, username, content, image, date));
                    }
                }

                StaffQuestionModel questionModel =
                        new StaffQuestionModel(id, username, title,
                                commentsNo, likesNo, shareNo, type, date, replyList);

                StaffQuestionModel existingQuestionModel =
                        questionMap.get(description.strip());

                if (existingQuestionModel != null) {
                    existingQuestionModel.getReplyList().addAll(replyList);
                } else {
                    if (!title.isEmpty()) {
                        questionMap.put(title.strip(), questionModel);
                    }
                }
            }

            for (Map.Entry<String, StaffQuestionModel> entry : questionMap.entrySet()) {
                questionList.add(entry.getValue());
            }


            Collections.reverse(questionList);

            questionAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setUpRecyclerView() {
        questionAdapter = new StaffDashboardAdapter(questionList);
        questionRecyclerView.hasFixedSize();
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        questionRecyclerView.setAdapter(questionAdapter);

       /* if (questionList.isEmpty()) {
            errorMessageTxt.setVisibility(View.VISIBLE);
        } else {
            errorMessageTxt.setVisibility(View.GONE);
        }*/
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