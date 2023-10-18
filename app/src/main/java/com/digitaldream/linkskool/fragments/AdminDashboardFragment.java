package com.digitaldream.linkskool.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.adapters.NewsAdapter;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.activities.AnswerView;
import com.digitaldream.linkskool.activities.ClassListUtil;
import com.digitaldream.linkskool.activities.NewsView;
import com.digitaldream.linkskool.activities.QuestionView;
import com.digitaldream.linkskool.activities.StudentContacts;
import com.digitaldream.linkskool.activities.TeacherContacts;
import com.digitaldream.linkskool.adapters.QAAdapter;
import com.digitaldream.linkskool.dialog.CustomDialog;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.GeneralSettingModel;
import com.digitaldream.linkskool.models.NewsTable;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.models.TeachersTable;
import com.digitaldream.linkskool.utils.AddNewsBottomSheet;
import com.digitaldream.linkskool.utils.QuestionBottomSheet;
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
import java.util.Map;
import java.util.Objects;


public class AdminDashboardFragment extends Fragment implements NewsAdapter.OnNewsClickListener,
        QAAdapter.OnQuestionClickListener {
    private TextView userName;
    private TextView school_Name;
    private TextView classesCount;
    private List<NewsTable> newsTitleList;
    private Dao<NewsTable, Long> newsDao;
    private DatabaseHelper databaseHelper;
    private Dao<StudentTable, Long> studentDao;
    private Dao<TeachersTable, Long> teacherDao;
    private Dao<ClassNameTable, Long> classDao;
    private List<StudentTable> studentList;
    private List<TeachersTable> teacherList;
    private List<ClassNameTable> classList;
    private RecyclerView qaRecycler;
    private LinearLayout studentContainer, teacherContainer,
            classesContainer;
    private Dao<GeneralSettingModel, Long> generalSettingDao;
    private List<GeneralSettingModel> generalSettingsList;
    public static String db;
    private String school_name;
    private String userId;
    private TextView errorMessage;
    private boolean fromLogin = false;
    private boolean isFirstTime = false;
    private LinearLayout emptyState;
    private QAAdapter.QAObject feed;
    List<QAAdapter.QAObject> list;
    private QAAdapter adapter;
    public static QuestionBottomSheet questionBottomSheet = null;
    public static String json = "";
    public static boolean refresh = false;
    private LinearLayoutManager layoutManager;
    private final String LIST_KEY = "2";
    private Parcelable listState;
    private Bundle bundle;
    private int currentPage = 1;
    private ProgressBar progressBar;
    private MenuHost menuHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_dashbord,
                container, false);

        databaseHelper = new DatabaseHelper(getContext());
        emptyState = view.findViewById(R.id.qa_empty_state);
        errorMessage = view.findViewById(R.id.error_message);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TextView studentCount = view.findViewById(R.id.no_of_student_txt1);
        TextView teacherCount = view.findViewById(R.id.no_of_teacher_txt1);
        classesCount = view.findViewById(R.id.no_of_classes);
        studentContainer = view.findViewById(R.id.student_no_cont);
        teacherContainer = view.findViewById(R.id.teacher_no_container);
        classesContainer = view.findViewById(R.id.class_no_container);
        userName = view.findViewById(R.id.user_name);
        school_Name = view.findViewById(R.id.school_name_1);

        try {
            newsDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    NewsTable.class);
            studentDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), StudentTable.class);
            teacherDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), TeachersTable.class);
            classDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), ClassNameTable.class);
            generalSettingDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(),
                    GeneralSettingModel.class);
            studentList = studentDao.queryForAll();
            teacherList = teacherDao.queryForAll();
            newsTitleList = newsDao.queryForAll();
            classList = classDao.queryForAll();
            generalSettingsList = generalSettingDao.queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        fromLogin = requireActivity().getIntent().getBooleanExtra("isFromLogin",
                false);
        if (fromLogin == true) {
            isFirstTime = true;
        }

        try {
            studentCount.setText(String.valueOf(studentList.size()));
            teacherCount.setText(String.valueOf(teacherList.size()));
            classesCount.setText(String.valueOf(classList.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((AppCompatActivity) (requireActivity())).setSupportActionBar(toolbar);
        ActionBar actionBar =
                ((AppCompatActivity) (requireActivity())).getSupportActionBar();

        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        menuHost = requireActivity();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(
                "loginDetail", Context.MODE_PRIVATE);
        school_name = generalSettingsList.get(0).getSchoolName().toLowerCase();
        userId = sharedPreferences.getString("user_id", "");
        String user_name = sharedPreferences.getString("user", "User ID: " + userId);
        db = sharedPreferences.getString("db", "");
        SharedPreferences.Editor editor =
                sharedPreferences.edit();
        editor.putString("school_name", school_name);
        editor.apply();


        String[] strArray = school_name.split(" ");
        StringBuilder builder = new StringBuilder();
        try {
            for (String s : strArray) {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap).append(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        school_Name.setText(builder.toString());


        studentContainer.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StudentContacts.class);
            startActivity(intent);
        });

        teacherContainer.setOnClickListener(v -> {
            Intent intent3 = new Intent(getContext(),
                    TeacherContacts.class);
            startActivity(intent3);
        });


        classesContainer.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ClassListUtil.class);
            startActivity(intent);
        });

        qaRecycler = view.findViewById(R.id.qa_recycler);

        qaRecycler.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(getContext());
        qaRecycler.setLayoutManager(layoutManager);
        list = new ArrayList<>();

        adapter = new QAAdapter(getContext(), list, this);
        qaRecycler.setAdapter(adapter);

        FloatingActionButton addQuestionBtn = view.findViewById(
                R.id.add_question);

        addQuestionBtn.setOnClickListener(v -> {
            FragmentTransaction transaction =
                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction();

            AddNewsBottomSheet addNewsBottomSheet = new AddNewsBottomSheet();
            addNewsBottomSheet.show(transaction, "newsBottomSheet");
            //Intent intent = new Intent(getContext(), AddNews.class);
            //startActivity(intent);
        });

        if (json.isEmpty()) {
            getFeed();
        } else {
            if (refresh) {
                getFeed();
            } else {
                parseJSON(json);
            }
        }
        progressBar = view.findViewById(R.id.progress_bar);
        NestedScrollView nestedScrollView = view.findViewById(R.id.scroll_view);
        nestedScrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX,
                                                           oldScrollY) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    if (scrollY == v.getChildAt(
                            0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        Toast.makeText(getContext(), "bottom",
                                Toast.LENGTH_SHORT).show();
                    }
                });


        return view;
    }

    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }

    @Override
    public void onNewsClick(int position) {

    }

    @Override
    public void onPause() {
        super.onPause();
        bundle = new Bundle();
        listState = Objects.requireNonNull(
                qaRecycler.getLayoutManager()).onSaveInstanceState();
        bundle.putParcelable(LIST_KEY, listState);
    }

    @Override
    public void onResume() {
        super.onResume();
        qaRecycler.smoothScrollToPosition(5);
        setUpMenu();

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

    private void getFeed() {
        CustomDialog dialog = new CustomDialog(getActivity());
        dialog.show();

        String url = getString(R.string.base_url) + "/getFeed.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, response -> {
            Log.i("response", "cn" + response);
            dialog.dismiss();
            json = response;
            parseJSON(response);

        }, error -> {
            dialog.dismiss();
           /* Toast.makeText(getContext(), "Something went wrong!",
                    Toast.LENGTH_SHORT).show();*/
            qaRecycler.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            errorMessage.setText("Failed to load News, please try again!");
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", "0");
                params.put("_db", db);
                params.put("page", String.valueOf(currentPage));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    private void parseJSON(String response) {
        try {
            list.clear();
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

                String body;
                body = object.optString("body");


                feed = new QAAdapter.QAObject();
                feed.setUser(user);
                feed.setId(id);

                feed.setQuestionId(id);
                if (title.isEmpty()) {
                    feed.setQuestion(desc);

                } else {
                    feed.setQuestion(title);
                }

                if (!body.isEmpty()) {

                    Object json = new JSONTokener(body).nextValue();

                    if (json instanceof JSONArray) {

                        JSONArray answer = new JSONArray(body);
                        boolean checktext = true;
                        boolean checkImage = true;
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

    private void setUpMenu() {
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.settings_menu, menu);

                menu.getItem(0).setVisible(true);
                menu.getItem(2).setVisible(true);
                menu.getItem(1).setVisible(false);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });
    }
}