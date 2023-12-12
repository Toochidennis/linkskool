package com.digitaldream.winskool.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.adapters.OptionsAdapter2;
import com.digitaldream.winskool.adapters.QuestionAdapter;
import com.digitaldream.winskool.adapters.QuestionAdapter2;
import com.digitaldream.winskool.adapters.TestSettingHeaderAdapter;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.CourseOutlineTable;
import com.digitaldream.winskool.models.OptionsModel;
import com.digitaldream.winskool.models.QuestionsModel;
import com.digitaldream.winskool.models.TestSettingModel;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.dialog.CustomDialog;
import com.digitaldream.winskool.utils.FileUtils;
import com.digitaldream.winskool.dialog.FlashCardDialog;
import com.digitaldream.winskool.dialog.FlashCardSettingsDialog;
import com.digitaldream.winskool.utils.FlashCardTagsSettings;
import com.digitaldream.winskool.utils.ItemMoveCallBack;
import com.digitaldream.winskool.dialog.QuestionDialog2;
import com.digitaldream.winskool.dialog.QuestionTypeDialog;
import com.digitaldream.winskool.dialog.SetTestDialog;
import com.digitaldream.winskool.utils.Util;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestUpload extends AppCompatActivity implements TestSettingHeaderAdapter.OnTestSettingHeaderClickListener, QuestionAdapter.OnQuestionClickListener, QuestionAdapter2.StartDragListener {
    public static RecyclerView recyclerView, questionRecycler;
    public static List<TestSettingModel> testHeaderList;
    public static TestSettingHeaderAdapter adapter;
    private Toolbar toolbar;
    public static List<QuestionsModel> questionsList;
    private LinearLayout addQuestionBtn, submitBtn, previewBtn;
    public static QuestionAdapter questionAdapter;
    public static QuestionAdapter2 questionAdapter2;
    public static JSONObject jsonObject1;
    public static String levelId, courseId, db, user_name, term, teacherId, assessmentId, questionId = "";
    private DatabaseHelper databaseHelper;
    private Dao<CourseOutlineTable, Long> courseOutlineDao;
    private CountDownTimer timer;
    private String from;
    public static Uri selecetedUri;
    public static String selectedFilePath;
    public static String courseName;
    public static String levelName;
    ItemTouchHelper touchHelper;
    private JSONObject jsonObject2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        jsonObject1 = new JSONObject();

        Intent i = getIntent();
        courseId = i.getStringExtra("courseId");
        levelId = i.getStringExtra("levelId");
        courseName = i.getStringExtra("courseName");
        levelName = i.getStringExtra("levelName");
        String json = i.getStringExtra("json");
        String week = i.getStringExtra("week");
        from = i.getStringExtra("from");
        assessmentId = i.getStringExtra("id");


        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        databaseHelper = new DatabaseHelper(this);
        try {
            courseOutlineDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseOutlineTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");
        user_name = sharedPreferences.getString("user", "Admin");
        term = sharedPreferences.getString("term", "");
        teacherId = sharedPreferences.getString("user_id", teacherId);

        recyclerView = findViewById(R.id.test_header_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        testHeaderList = new ArrayList<>();
        TestSettingModel tsm = new TestSettingModel();
        tsm.setCourse(CourseOutlines.courseName);
        tsm.setLevel(CourseOutlines.levelName);
        tsm.setLevelId(levelId);
        tsm.setCourseId(courseId);
        tsm.setTitle("");
        testHeaderList.add(tsm);

        adapter = new TestSettingHeaderAdapter(this, testHeaderList, this);
        recyclerView.setAdapter(adapter);
        if (from.equalsIgnoreCase("upload")) {
            getSupportActionBar().setTitle("Assignment");
            SetTestDialog dialog = new SetTestDialog(this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        } else if (from.equalsIgnoreCase("flashcard")) {
            getSupportActionBar().setTitle("Flashcard");

            FlashCardDialog dialog = new FlashCardDialog(this);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }


        questionRecycler = findViewById(R.id.question_recycler);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        questionRecycler.setLayoutManager(linearLayoutManager1);
        questionRecycler.setHasFixedSize(true);
        questionsList = new ArrayList<>();
        //questionAdapter = new QuestionAdapter(this,questionsList,this);
        //questionRecycler.setAdapter(questionAdapter);
        questionAdapter2 = new QuestionAdapter2(this, questionsList, this);
        questionRecycler.setAdapter(questionAdapter2);
        ItemTouchHelper.Callback callback =
                new ItemMoveCallBack(questionAdapter2);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(questionRecycler);

        addQuestionBtn = findViewById(R.id.add_question);
        addQuestionBtn.setOnClickListener(v -> {
            if (from.equalsIgnoreCase("upload") || from.equalsIgnoreCase("edit")) {
                QuestionTypeDialog dialog = new QuestionTypeDialog(TestUpload.this);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                addQuestionBtn.setEnabled(true);
            } else if (from.equalsIgnoreCase("flashcard")) {
                QuestionsModel qm = new QuestionsModel();
                qm.setQuestionType("flash_card");
                qm.setQuestion("");
                qm.setQuestionId("");
                TestUpload.questionsList.add(qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                TestUpload.questionRecycler.smoothScrollToPosition(TestUpload.questionAdapter2.getItemCount() - 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       /*QuestionDialog2 dialog2 = new QuestionDialog2(TestUpload.this,qm,TestUpload.questionAdapter2.getItemCount()-1, QuestionAdapter2.questionNumber);
                       dialog2.show();
                       Window window = dialog2.getWindow();
                       //window.setLayout(ViewGroup.LayoutParams
                       // .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                       window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                               WindowManager.LayoutParams.MATCH_PARENT);*/

                        FragmentActivity activityF =
                                TestUpload.this;
                        FragmentManager manager = activityF.getSupportFragmentManager();
                        QuestionDialog2 dialog2 =
                                new QuestionDialog2(TestUpload.this
                                        , qm,
                                        TestUpload.questionAdapter2.getItemCount() - 1,
                                        QuestionAdapter2.questionNumber);
                        dialog2.setCancelable(false);
                        dialog2.show(manager, "tag");

                    }
                }, 500);
            } else if (from.equals("flash_card")) {
                QuestionsModel qm = new QuestionsModel();
                qm.setQuestionType("flash_card");
                qm.setQuestion("");
                qm.setQuestionId("");
                TestUpload.questionsList.add(qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                TestUpload.questionRecycler.smoothScrollToPosition(TestUpload.questionAdapter2.getItemCount() - 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       /*QuestionDialog2 dialog2 = new QuestionDialog2(TestUpload.this,qm,TestUpload.questionAdapter2.getItemCount()-1, QuestionAdapter2.questionNumber);
                       dialog2.show();
                       Window window = dialog2.getWindow();
                       window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
*/

                        FragmentActivity activityF =
                                TestUpload.this;
                        FragmentManager manager = activityF.getSupportFragmentManager();
                        QuestionDialog2 dialog2 =
                                new QuestionDialog2(TestUpload.this
                                        , qm,
                                        TestUpload.questionAdapter2.getItemCount() - 1, QuestionAdapter2.questionNumber);
                        dialog2.setCancelable(false);
                        dialog2.show(manager, "tag");

                    }
                }, 500);
            }

        });
        submitBtn = findViewById(R.id.submit);

        submitBtn.setOnClickListener(v -> {
            submitAssignment2();

            // preview();

        });

        previewBtn = findViewById(R.id.preview_test);

        previewBtn.setOnClickListener(v -> {
            ExamActivity.ab = 0;

            if (testHeaderList.size() > 0 && !testHeaderList.get(0).getTitle().isEmpty() || !FlashCardSettingsDialog.title.isEmpty()) {
                JSONObject jsonObject = new JSONObject();

                try {
                    if (!from.equals("flash_card")) {
                        jsonObject.put("id", assessmentId);
                        jsonObject.put("week", testHeaderList.get(0).getWeek());
                        jsonObject.put("title", testHeaderList.get(0).getTitle());
                        if (from.equals("assessment")) {
                            jsonObject.put("description", testHeaderList.get(0).getDescription());
                        } else {
                            jsonObject.put("description", "");
                        }
                        jsonObject.put("start_date", testHeaderList.get(0).getStartDate());
                        jsonObject.put("end_date", testHeaderList.get(0).getEndDate());
                        jsonObject.put("duration", testHeaderList.get(0).getDuration());
                    }
                    jsonObject.put("level", levelId);
                    jsonObject.put("course", courseId);
                    jsonObject.put("term", term);
                    jsonObject.put("author_id", teacherId);
                    jsonObject.put("author_name", user_name);

                    JSONArray questionArray = new JSONArray();
                    for (int i1 = 0; i1 < QuestionAdapter2.list.size(); i1++) {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject q = new JSONObject();
                        QuestionsModel qm = QuestionAdapter2.list.get(i1);
                        String answer = "";


                        q.put("id", qm.getQuestionId());


                        //q.put("options", jsonArray);
                        switch (qm.getQuestionType()) {
                            case "short_answer":
                                q.put("content", qm.getQuestion());
                                q.put("type", "qs");
                                q.put("correct", qm.getAnswer());
                                if (qm.getQuestionImageUri() != null) {
                                    q.put("question_image", qm.getQuestionImageUri().toString());
                                    q.put("tag", "0");
                                } else if (qm.getQuestionImage() != null) {
                                    q.put("question_image", qm.getQuestionImage());
                                    q.put("tag", "1");
                                }
                                break;
                            case "long_answer":
                                q.put("type", "qt");
                                q.put("content", qm.getQuestion());
                                q.put("correct", qm.getAnswer());
                                if (qm.getQuestionImageUri() != null) {
                                    q.put("question_image", qm.getQuestionImageUri().toString());
                                    q.put("tag", "0");
                                } else if (qm.getQuestionImage() != null) {
                                    q.put("question_image", qm.getQuestionImage());
                                    q.put("tag", "1");
                                }
                                break;
                            case "multiple_choice":
                                q.put("type", "qo");
                                q.put("content", qm.getQuestion());
                                String optionsOrder = "";

                                for (int a = 0; a < QuestionAdapter2.list.get(i1).getOptions().size(); a++) {
                                    String option = QuestionAdapter2.list.get(i1).getOptions().get(a).getOptionText();


                                    if (QuestionAdapter2.list.get(i1).getOptions().get(a).isChecked()) {

                                        List<Integer> order =
                                                new ArrayList<>();
                                        order.add(0);
                                        order.add(1);
                                        order.add(2);
                                        order.add(3);
                                        order.add(4);

                                        for (int j = 0; j < order.size(); j++) {

                                            if (order.get(j) == a) {
                                                answer = QuestionAdapter2.list.get(i1).getOptions().get(a).getOptionText();
                                                optionsOrder = String.valueOf(order.get(j));
                                            }

                                        }


                                        JSONObject answerJson =
                                                new JSONObject();
                                        answerJson.put("order",
                                                optionsOrder);

                                        answerJson.put("text", answer);

                                        q.put("correct", answerJson);

                                    }

                                    /*if (QuestionAdapter2.list.get(i).getOptions().get(a).isChecked()) {




                                           // q.put("correct",answer);
                                        }*/
                                    //opts+=option+"||";
                                    JSONObject j = new JSONObject();

                                    j.put("text", option);
                                    if (qm.getOptions().get(a).getOptionsImage() != null && !qm.getOptions().get(a).getOptionsImage().toString().isEmpty()) {
                                        Uri imgUrl = qm.getOptions().get(a).getOptionsImage();
                                        j.put("filename", imgUrl);
                                        j.put("tag", "0");

                                    } else if (qm.getOptions().get(a).getOptionImageUrl() != null) {
                                        String filename = qm.getOptions().get(a).getOptionImageUrl();
                                        j.put("filename", filename);
                                        j.put("tag", "1");

                                    }

                                    jsonArray.put(j);

                                }
                                //opts = opts.substring(0,opts.length()-2);
                                q.put("answer", jsonArray);
                                if (qm.getQuestionImageUri() != null) {
                                    q.put("question_image", qm.getQuestionImageUri().toString());
                                    q.put("tag", "0");
                                } else if (qm.getQuestionImage() != null) {
                                    q.put("question_image", qm.getQuestionImage());
                                    q.put("tag", "1");
                                }
                                break;
                            case "email":
                                q.put("type", "em");
                                q.put("content", qm.getQuestion());
                                q.put("correct", qm.getAnswer());

                                break;
                            case "number":
                                q.put("type", "nb");
                                q.put("content", qm.getQuestion());
                                q.put("correct", qm.getAnswer());
                                break;
                            case "check_box":
                                q.put("type", "qm");
                                q.put("content", qm.getQuestion());
                                String opts1 = "";
                                for (int a = 0; a < QuestionAdapter2.list.get(i1).getOptions().size(); a++) {
                                    String option = QuestionAdapter2.list.get(i1).getOptions().get(a).getOptionText();

                                    if (QuestionAdapter2.list.get(i1).getOptions().get(a).isChecked()) {

                                        String ans = QuestionAdapter2.list.get(i1).getOptions().get(a).getOptionText();
                                        answer = answer + "" + ans + "||";

                                    }
                                    opts1 += option + "||";


                                    //jsonArray.put(option);

                                }
                                opts1 = opts1.substring(0, opts1.length() - 2);
                                answer = answer.substring(0, answer.length() - 2);
                                q.put("correct", answer);
                                q.put("answer", opts1);
                                //q.put("answer",answer);
                                break;
                            case "drop_down":
                                q.put("type", "sl");
                                q.put("content", qm.getQuestion());
                                String opts2 = "";
                                for (int a = 0; a < QuestionAdapter2.list.get(i1).getOptions().size(); a++) {
                                    String option = QuestionAdapter2.list.get(i1).getOptions().get(a).getOptionText();

                                    opts2 += option + "||";


                                    //jsonArray.put(option);

                                }
                                opts2 = opts2.substring(0, opts2.length() - 2);
                                q.put("answer", opts2);
                                q.put("correct", "");
                                break;
                            case "date":
                                q.put("type", "dt");
                                q.put("content", qm.getQuestion());
                                q.put("correct", qm.getAnswer());
                                break;
                            case "time":
                                q.put("type", "tm");
                                q.put("content", qm.getQuestion());
                                q.put("correct", qm.getAnswer());
                                break;
                            case "section":
                                q.put("type", "section");
                                q.put("content", qm.getSectionTitle());
                                q.put("correct", qm.getSectionDescription());
                                if (qm.getQuestionImageUri() != null) {
                                    q.put("question_image", qm.getQuestionImageUri().toString());
                                    q.put("tag", "0");
                                } else if (qm.getQuestionImage() != null) {
                                    q.put("question_image", qm.getQuestionImage());
                                    q.put("tag", "1");
                                }
                                break;
                            case "flash_card":
                                q.put("type", "flash_card");
                                q.put("content", qm.getQuestion());
                                q.put("correct", qm.getAnswer());
                                break;


                            default:
                                throw new IllegalStateException("Unexpected value: " + qm.getQuestionType());
                        }
                        //q.put("type", qm.getQuestionType());
                        q.put("parent", "");
                        questionArray.put(q);

                    }
                    JSONArray jsonArray = new JSONArray();
                    //jsonArray.put(0,questionArray);
                    //{"e":{"id":"102","title":"ggffff","description":"","course_id":"1","body":"ffgggg","url":"","course_name":"ENGLISH"},"q":[[{"id":"47","parent":"0","content":"how to","title":"","type":"qo","answer":"","correct":""}]]}
                    jsonObject1.put("q", jsonArray.put(0, questionArray));
                    jsonObject1.put("e", jsonObject);

                    if (questionArray.length() == 0) {
                        Toast.makeText(TestUpload.this, "There is no questions", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (from.equals("upload") || from.equals("edit")) {
                        Intent intent1 = new Intent(TestUpload.this, ExamActivity.class);
                        intent1.putExtra("course", "");
                        intent1.putExtra("Json", jsonObject1.toString());
                        intent1.putExtra("year", "");
                        intent1.putExtra("from", "preview");
                        Log.i("json Me and U", jsonObject1.toString());
                        startActivity(intent1);

                    } else if (from.equals("flashcard") || from.equals("flash_card")) {
                        Intent intent1 = new Intent(TestUpload.this, FlashView.class);
                        intent1.putExtra("course", "");
                        intent1.putExtra("Json", jsonObject1.toString());
                        intent1.putExtra("year", "");
                        intent1.putExtra("from", "assessment");
                        startActivity(intent1);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //assessmentApiCall();


            } else {
                Toast.makeText(TestUpload.this, "Title is empty", Toast.LENGTH_SHORT).show();

            }

        });

        if (from.equalsIgnoreCase("edit")) {
            testHeaderList.clear();
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject settingObject = jsonObject.getJSONObject("e");
                String title = settingObject.getString("title");
                String description = settingObject.getString("body");
                String duration = settingObject.optString("description");
                String startDate = settingObject.optString("start_date");
                String endDate = settingObject.optString("end_date");
                String access = settingObject.optString("category");
                Log.i("response", json);
                try {
                    String[] s = startDate.split(" ");
                    String[] e = endDate.split(" ");

                    TestSettingModel md = new TestSettingModel();
                    md.setTitle(title);
                    String[] wk = week.split(" ");
                    md.setWeek(wk[1]);
                    md.setDuration(duration);
                    md.setDescription(description);
                    md.setDuration(duration);
                    md.setAccess(access);
                    md.setEndDate(e[0]);
                    md.setStartDate(s[0]);
                    md.setStartTime(s[1]);
                    md.setEndTime(e[1]);
                    if (!s[0].isEmpty() || !s[1].isEmpty() || !e[0].isEmpty() || !e[1].isEmpty()) {
                        md.setChecked(true);
                    }
                    md.setType("assessment");
                    md.setCourse(courseName);
                    md.setLevel(levelName);
                    md.setCourseId(courseId);
                    md.setLevelId(levelId);
                    testHeaderList.add(md);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("q");
                    render(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                    try {
                        jsonObject2 = jsonObject.getJSONObject("q");
                        render(jsonObject2);
                    } catch (Exception e1) {
                        e1.printStackTrace();

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onTestSettingHeaderClick(int position) {
        recyclerView.setVisibility(View.GONE);

        Toast.makeText(this, testHeaderList.get(0).getType(),
                Toast.LENGTH_SHORT).show();
        /*   if(testHeaderList.get(0).getType().equals("assessment")) {*/
        if (true) {


            SetTestDialog dialog = new SetTestDialog(this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        } else {
            FlashCardDialog dialog = new FlashCardDialog(this);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

 /*   @Override
    protected void onResume() {
        super.onResume();

        if(ExamActivity.qPosition!=-1){
            showDialog(questionsList.get(ExamActivity.qPosition),ExamActivity.qPosition,0);
            ExamActivity.qPosition=-1;
        }
    }*/

    @Override
    public void onQuestionClick(int position) {
   /*     QuestionDialog2 dialog2 = new QuestionDialog2(this,questionsList.get(position),questionAdapter2.getItemCount()-1, QuestionAdapter2.questionNumber);
        dialog2.setCancelable(false);
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.show();
        Window window = dialog2.getWindow();
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT)*/
        ;

        FragmentActivity activityF =
                TestUpload.this;
        FragmentManager manager = activityF.getSupportFragmentManager();
        QuestionDialog2 dialog2 =
                new QuestionDialog2(TestUpload.this
                        , questionsList.get(position),
                        questionAdapter2.getItemCount() - 1, QuestionAdapter2.questionNumber);

        dialog2.setCancelable(false);
        dialog2.show(manager, "tag");


    }

    public void submitAssignment() {
        JSONObject jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();


        try {
            jsonObject.put("id", assessmentId);
            jsonObject.put("week", testHeaderList.get(0).getWeek());
            jsonObject.put("title", testHeaderList.get(0).getTitle());
            jsonObject.put("description", testHeaderList.get(0).getDescription());
            jsonObject.put("start_date", testHeaderList.get(0).getStartDate());
            jsonObject.put("end_date", testHeaderList.get(0).getEndDate());
            jsonObject.put("duration", testHeaderList.get(0).getDuration());
            jsonObject.put("level", levelId);
            jsonObject.put("course", courseId);
            jsonObject.put("term", term);
            jsonObject.put("author_id", teacherId);
            jsonObject.put("author_name", user_name);

            JSONArray questionArray = new JSONArray();
            for (int i = 0; i < questionsList.size(); i++) {
                JSONArray jsonArray = new JSONArray();
                JSONObject q = new JSONObject();
                String answer = "";
                for (int a = 0; a < questionsList.get(i).getOptions().size(); a++) {
                    String option = questionsList.get(i).getOptions().get(a).getOptionText();

                    if (questionsList.get(i).getOptions().get(a).isChecked()) {
                        answer = questionsList.get(i).getOptions().get(a).getOptionText();
                    }
                    jsonArray.put(option);

                }
                q.put("question_id", questionsList.get(i).getQuestionId());
                q.put("question", questionsList.get(i).getQuestion());
                q.put("correct", answer);
                q.put("options", jsonArray);
                q.put("type", "multiple_choice");
                q.put("parent", "");
                questionArray.put(q);

            }
            jsonObject1.put("questions", questionArray);
            jsonObject1.put("settings", jsonObject);
            if (questionArray.length() == 0) {
                Toast.makeText(TestUpload.this, "There is no questions", Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        assessmentApiCall();

    }

    public void submitAssignment2() {

        if (testHeaderList.size() > 0 && !testHeaderList.get(0).getTitle().isEmpty() || !FlashCardSettingsDialog.title.isEmpty()) {
            JSONObject jsonObject = new JSONObject();


            try {

                if (!from.equals("flash_card")) {
                    if (testHeaderList.get(0).getDuration() == null || testHeaderList.get(0).getDuration() != null && testHeaderList.get(0).getDuration().trim().isEmpty()) {
                        Toast.makeText(TestUpload.this, "Duration can't be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    TestSettingModel tsm = testHeaderList.get(0);
                    jsonObject.put("id", assessmentId);
                    jsonObject.put("week", tsm.getWeek());
                    jsonObject.put("title", testHeaderList.get(0).getTitle());
                    jsonObject.put("description", testHeaderList.get(0).getDescription());
                    jsonObject.put("start_date", testHeaderList.get(0).getStartDate());
                    jsonObject.put("end_date", testHeaderList.get(0).getEndDate());
                    jsonObject.put("duration", testHeaderList.get(0).getDuration());
                    jsonObject.put("start_time", testHeaderList.get(0).getStartTime());
                    jsonObject.put("end_time", testHeaderList.get(0).getEndTime());
                    jsonObject.put("category", testHeaderList.get(0).getAccess());
                    jsonObject.put("level", tsm.getLevelId());
                    jsonObject.put("course", tsm.getCourseId());
                    jsonObject.put("term", term);
                    jsonObject.put("author_id", teacherId);
                    jsonObject.put("author_name", user_name);
                    jsonObject.put("course_name", tsm.getCourse());
                    jsonObject.put("level_name", tsm.getLevel());

                }

                JSONArray questionArray = new JSONArray();
                for (int i = 0; i < QuestionAdapter2.list.size(); i++) {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject q = new JSONObject();
                    QuestionsModel qm = QuestionAdapter2.list.get(i);
                    String answer = "";
                    JSONObject answerJson = new JSONObject();
                    for (int a = 0; a < QuestionAdapter2.list.get(i).getOptions().size(); a++) {
                        String option = QuestionAdapter2.list.get(i).getOptions().get(a).getOptionText();
                        JSONObject j = new JSONObject();

                        String optionsOrder = "";

                        if (QuestionAdapter2.list.get(i).getOptions().get(a).isChecked()) {
                            if (qm.getQuestionType().equals("multiple_choice")) {

                                List<Integer> order =
                                        new ArrayList<>();
                                order.add(0);
                                order.add(1);
                                order.add(2);
                                order.add(3);
                                order.add(4);

                                for (int c = 0; c < order.size(); c++) {

                                    if (order.get(c) == a) {
                                        answer = QuestionAdapter2.list.get(i).getOptions().get(a).getOptionText();
                                        optionsOrder =
                                                String.valueOf(order.get(c));
                                    }

                                }

                                answerJson.put("order",
                                        optionsOrder);

                                answerJson.put("text", answer);


                               /* answer =
                                        QuestionAdapter2.list.get(i).getOptions().get(a).getOptionText();*/

                            } else if (qm.getQuestionType().equals("check_box")) {
                                String ans = QuestionAdapter2.list.get(i).getOptions().get(a).getOptionText();
                                answer = answer + "" + ans + "||";
                            }
                        }

                        j.put("text", option);
                        if (qm.getOptions().get(a).getOptionsImage() != null && !qm.getOptions().get(a).getOptionsImage().toString().isEmpty()) {
                            Uri imgUrl = qm.getOptions().get(a).getOptionsImage();
                            File file = new File(FileUtils.getPath(TestUpload.this, imgUrl));
                            j.put("image", getStringFile(file));
                            j.put("filename", file.getName());
                            j.put("oldfilename", "");
                            //Log.i("response","reaching "+file.getName());

                        } else if (qm.getOptions().get(a).getOptionImageUrl() != null) {
                            String filename = qm.getOptions().get(a).getOptionImageUrl();
                            j.put("image", "");
                            j.put("filename", filename);
                            j.put("oldfilename", filename);
                            // Log.i("response","reaching2 "+filename);

                        }

                        jsonArray.put(j);

                    }
                    if (qm.getQuestionType().equals("check_box")) {
                        try {
                            answer = answer.substring(0, answer.length() - 2);
                        } catch (StringIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                    if (qm.getQuestionType().equals("short_answer") || qm.getQuestionType().equals("long_answer")) {
                        answer = qm.getAnswer();
                    }

                    q.put("question_id", qm.getQuestionId());


                    if (qm.getQuestionImageUri() != null) {
                        Uri imgUrl = qm.getQuestionImageUri();
                        File file = new File(FileUtils.getPath(TestUpload.this, imgUrl));
                        q.put("question_image", new Util().getStringFile(file));
                        q.put("question_filename", file.getName());
                        q.put("question_oldfile", "");

                    } else if (qm.getQuestionImage() != null) {

                        String filename = qm.getQuestionImage();

                        q.put("question_image", "");
                        q.put("question_filename", filename);
                        q.put("question_oldfile", filename);

                    }
                    if (!qm.getQuestionType().equals("section")) {
                        q.put("question", qm.getQuestion());
                        if (qm.getQuestionType().equals("email") || qm.getQuestionType().equals("number") || qm.getQuestionType().equals("date") || qm.getQuestionType().equals("time")) {
                            q.put("correct", qm.getQuestion());

                        } else if (qm.getQuestionType().equals("flash_card")) {
                            q.put("correct", qm.getAnswer());
                        } else if (qm.getQuestionType().equals("multiple_choice")) {
                            q.put("correct", answerJson);
                        } else {
                            q.put("correct", answer);
                        }

                    } else if (qm.getQuestionType().equals("section")) {
                        q.put("question", qm.getSectionTitle());
                        q.put("correct", qm.getSectionDescription());
                    }

                    q.put("options", jsonArray);
                    q.put("type", qm.getQuestionType());
                    q.put("parent", "");
                    questionArray.put(q);

                }
                jsonObject1.put("questions", questionArray);
                jsonObject1.put("settings", jsonObject);
                Log.i("response4", jsonObject1.toString());
                if (!from.equals("flash_card")) {
                    if (questionArray.length() == 0) {
                        Toast.makeText(TestUpload.this, "There is no questions", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

            if (from.equalsIgnoreCase("upload") || from.equals("edit")) {
                assessmentApiCall();

            } else if (from.equalsIgnoreCase("flashcard")) {
                createFlashCardApi();
            } else if (from.equals("flash_card")) {
                try {
                    jsonObject.put("id", "");
                    jsonObject.put("week", "0");
                    jsonObject.put("title", FlashCardSettingsDialog.title);
                    jsonObject.put("description", "0");
                    jsonObject.put("start_date", "0");
                    jsonObject.put("end_date", "0");
                    jsonObject.put("duration", "0");
                    jsonObject.put("start_time", "0");
                    jsonObject.put("end_time", "0");
                    jsonObject.put("level", FlashCardTagsSettings.level);
                    jsonObject.put("course", FlashCardTagsSettings.course);
                    jsonObject.put("term", term);
                    jsonObject.put("author_id", teacherId);
                    jsonObject.put("author_name", user_name);
                    jsonObject1.put("settings", jsonObject);
                    createFlashCardApi();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        } else {
            Toast.makeText(TestUpload.this, "Title is empty", Toast.LENGTH_SHORT).show();
        }
        Log.i("json", jsonObject1.toString());
    }


    private void createFlashCardApi() {
        CustomDialog dialog = new CustomDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        String url = Login.urlBase + "/addFlashCard.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            dialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equalsIgnoreCase("success")) {
                    if (from.equals("flashcard")) {
                        JSONObject detailObj = jsonObject.getJSONObject("details");
                        String id = detailObj.getString("id");
                        String title = detailObj.getString("title");
                        String objective = detailObj.getString("body");
                        String week = detailObj.getString("rank");
                        String courseId = detailObj.getString("course_id");
                        String levelId = detailObj.getString("level");
                        String assessmentUrl = detailObj.getString("url");
                        String type = detailObj.getString("type");
                        String timeDuration = detailObj.optString("description");
                        String startDate = detailObj.optString("start_date");
                        String endDate = detailObj.optString("end_date");

                        QueryBuilder<CourseOutlineTable, Long> queryBuilder = courseOutlineDao.queryBuilder();
                        queryBuilder.where().eq("id", id);
                        List<CourseOutlineTable> cot = queryBuilder.query();
                        if (cot.isEmpty()) {
                            CourseOutlineTable ct = new CourseOutlineTable();
                            ct.setId(id);
                            ct.setWeek("Week " + week);
                            ct.setObjective(objective);
                            ct.setTitle(title);
                            ct.setCourseId(courseId);
                            ct.setLevelId(levelId);
                            ct.setType(type);
                            ct.setDuration(timeDuration);
                            ct.setAssessmentUrl(assessmentUrl);
                            ct.setStartDate(startDate);
                            ct.setEndDate(endDate);

                            courseOutlineDao.create(ct);
                        } else {
                            UpdateBuilder<CourseOutlineTable, Long> updateBuilder = courseOutlineDao.updateBuilder();
                            updateBuilder.updateColumnValue("week", "Week " + week);
                            updateBuilder.updateColumnValue("objective", objective);
                            updateBuilder.updateColumnValue("levelId", levelId);
                            updateBuilder.updateColumnValue("courseId", courseId);
                            updateBuilder.updateColumnValue("assessmentUrl", assessmentUrl);
                            updateBuilder.updateColumnValue("title", title);
                            updateBuilder.updateColumnValue("assessmentUrl", assessmentUrl);
                            updateBuilder.updateColumnValue("duration", timeDuration);
                            updateBuilder.updateColumnValue("startDate", startDate);
                            updateBuilder.updateColumnValue("endDate", endDate);
                            updateBuilder.where().eq("id", id);
                            updateBuilder.update();
                        }

                        onBackPressed();
                    } else {
                        if (StudentDashboardActivity.check != null) {
                            Intent intent = new Intent(TestUpload.this, StudentDashboardActivity.class);
                            intent.putExtra("from", "testupload");
                            startActivity(intent);

                        } else if (Dashboard.check != null) {
                            Intent intent = new Intent(TestUpload.this, Dashboard.class);
                            intent.putExtra("from", "testupload");
                            startActivity(intent);
                        } else if (StaffDashboardActivity.check != null) {
                            Intent intent = new Intent(TestUpload.this, StaffDashboardActivity.class);
                            intent.putExtra("from", "testupload");
                            startActivity(intent);
                        }
                    }
                    Toast.makeText(TestUpload.this, "Upload was successful", Toast.LENGTH_SHORT).show();

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("assessment", jsonObject1.toString());
                param.put("_db", db);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void assessmentApiCall() {

        CustomDialog dialog = new CustomDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        String url = Login.urlBase + "/addQuiz.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.i("JJJJJson", response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("success")) {
                        JSONObject detailObj = jsonObject.getJSONObject("details");
                        String id = detailObj.getString("id");
                        String title = detailObj.getString("title");
                        String objective = detailObj.getString("body");
                        String week = detailObj.getString("rank");
                        String courseId = detailObj.getString("course_id");
                        String levelId = detailObj.getString("level");
                        String assessmentUrl = detailObj.getString("url");
                        String type = detailObj.getString("type");
                        String timeDuration = detailObj.optString(
                                "description");
                        String startDate = detailObj.optString("start_date");
                        String endDate = detailObj.optString("end_date");

                        QueryBuilder<CourseOutlineTable, Long> queryBuilder = courseOutlineDao.queryBuilder();
                        queryBuilder.where().eq("id", id);
                        List<CourseOutlineTable> cot = queryBuilder.query();
                        if (cot.isEmpty()) {
                            CourseOutlineTable ct = new CourseOutlineTable();
                            ct.setId(id);
                            ct.setWeek("Week " + week);
                            ct.setObjective(objective);
                            ct.setTitle(title);
                            ct.setCourseId(courseId);
                            ct.setLevelId(levelId);
                            ct.setType("2");
                            ct.setDuration(timeDuration);
                            ct.setAssessmentUrl(assessmentUrl);
                            ct.setStartDate(startDate);
                            ct.setEndDate(endDate);

                            courseOutlineDao.create(ct);
                        } else {
                            UpdateBuilder<CourseOutlineTable, Long> updateBuilder = courseOutlineDao.updateBuilder();
                            updateBuilder.updateColumnValue("week", "Week " + week);
                            updateBuilder.updateColumnValue("objective", objective);
                            updateBuilder.updateColumnValue("levelId", levelId);
                            updateBuilder.updateColumnValue("courseId", courseId);
                            updateBuilder.updateColumnValue("assessmentUrl", assessmentUrl);
                            updateBuilder.updateColumnValue("title", title);
                            updateBuilder.updateColumnValue("assessmentUrl", assessmentUrl);
                            updateBuilder.updateColumnValue("duration", timeDuration);
                            updateBuilder.updateColumnValue("startDate", startDate);
                            updateBuilder.updateColumnValue("endDate", endDate);
                            updateBuilder.where().eq("id", id);
                            updateBuilder.update();
                        }

                        onBackPressed();

                        Log.i("Message", jsonObject1.toString());

                        Toast.makeText(TestUpload.this, "Upload was successful", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        }, error -> {
            dialog.dismiss();
            Toast.makeText(TestUpload.this, "Error submitting questions", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("assessment", jsonObject1.toString());
                params.put("_db", db);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == QuestionDialog2.SELECT_IMAGE) {
                selecetedUri = data.getData();
                //selectedFilePath = FilePath.getPath(this, selecetedUri);

                QuestionDialog2.pic.setImageURI(selecetedUri);
                QuestionDialog2.pic.setVisibility(View.VISIBLE);
                QuestionDialog2.imageContainer.setVisibility(View.VISIBLE);
                QuestionDialog2.qm.setQuestionImageUri(selecetedUri);
                if (QuestionDialog2.qNumberContainer != null) {
                    QuestionDialog2.qNumberContainer.setVisibility(View.GONE);
                }
                if (QuestionDialog2.addImgContainer != null) {
                    QuestionDialog2.addImgContainer.setVisibility(View.GONE);
                }
            } else if (requestCode == QuestionDialog2.SELECT_OPTION_IMAGE) {
                selecetedUri = data.getData();
                //selectedFilePath = FilePath.getPath(this, selecetedUri);
                if (QuestionDialog2.optionsList != null) {
                    QuestionDialog2.optionsList.get(OptionsAdapter2.optionPosition).setOptionsImage(selecetedUri);
                } else if (QuestionDialog2.optionsList1 != null) {
                    QuestionDialog2.optionsList1.get(OptionsAdapter2.optionPosition).setOptionsImage(selecetedUri);

                }
                QuestionDialog2.adapter.notifyDataSetChanged();
                TestUpload.questionAdapter2.notifyDataSetChanged();
            }
        }
        selecetedUri = null;

    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    public void preview() {
        JSONObject jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();

        try {

            jsonObject.put("title", testHeaderList.get(0).getTitle());
            jsonObject.put("description", testHeaderList.get(0).getDuration());


            JSONArray questionArray = new JSONArray();
            for (int i = 0; i < QuestionAdapter2.list.size(); i++) {
                JSONArray jsonArray = new JSONArray();
                JSONObject q = new JSONObject();
                QuestionsModel qm = QuestionAdapter2.list.get(i);
                String answer = "";


                q.put("id", qm.getQuestionId());
                q.put("obj", qm);


                //q.put("options", jsonArray);
                switch (qm.getQuestionType()) {
                    case "short_answer":
                        q.put("content", qm.getQuestion());
                        q.put("type", "qs");
                        q.put("correct", qm.getAnswer());
                        if (qm.getQuestionImageUri() != null) {
                            q.put("question_image", qm.getQuestionImageUri().toString());
                            q.put("tag", "0");
                        } else if (qm.getQuestionImage() != null) {
                            q.put("question_image", qm.getQuestionImage());
                            q.put("tag", "1");
                        }
                        break;
                    case "long_answer":
                        q.put("type", "qt");
                        q.put("content", qm.getQuestion());
                        q.put("correct", qm.getAnswer());
                        if (qm.getQuestionImageUri() != null) {
                            q.put("question_image", qm.getQuestionImageUri().toString());
                            q.put("tag", "0");
                        } else if (qm.getQuestionImage() != null) {
                            q.put("question_image", qm.getQuestionImage());
                            q.put("tag", "1");
                        }
                        break;
                    case "multiple_choice":
                        q.put("type", "qo");
                        q.put("content", qm.getQuestion());
                        String opts = "";

                        for (int a = 0; a < QuestionAdapter2.list.get(i).getOptions().size(); a++) {
                            String option = QuestionAdapter2.list.get(i).getOptions().get(a).getOptionText();

                            String optionsOrder = "";

                        /*    if (QuestionAdapter2.list.get(i).getOptions().get(a).isChecked()) {
                                answer = QuestionAdapter2.list.get(i).getOptions().get(a).getOptionText();
                                q.put("correct",answer);
                            }*/

                            if (QuestionAdapter2.list.get(i).getOptions().get(a).isChecked()) {

                                List<Integer> order =
                                        new ArrayList<>();
                                order.add(0);
                                order.add(1);
                                order.add(2);
                                order.add(3);
                                order.add(4);

                                for (int j = 0; j < order.size(); j++) {

                                    if (order.get(j) == a) {
                                        answer =
                                                QuestionAdapter2.list.get(i).getOptions().get(a).getOptionText();
                                        optionsOrder =
                                                String.valueOf(order.get(j));
                                    }

                                }


                                JSONObject answerJson =
                                        new JSONObject();
                                answerJson.put("order",
                                        optionsOrder);

                                answerJson.put("text", answer);

                                q.put("correct", answerJson);

                            }
                            opts += option + "||";

                        }
                        try {
                            opts = opts.substring(0, opts.length() - 2);
                            q.put("answer", opts);
                        } catch (StringIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                        if (qm.getQuestionImageUri() != null) {
                            q.put("question_image", qm.getQuestionImageUri().toString());
                            q.put("tag", "0");
                        } else if (qm.getQuestionImage() != null) {
                            q.put("question_image", qm.getQuestionImage());
                            q.put("tag", "1");
                        }
                        break;
                    case "email":
                        q.put("type", "em");
                        q.put("content", qm.getQuestion());
                        q.put("correct", qm.getAnswer());

                        break;
                    case "number":
                        q.put("type", "nb");
                        q.put("content", qm.getQuestion());
                        q.put("correct", qm.getAnswer());
                        break;
                    case "check_box":
                        q.put("type", "qm");
                        q.put("content", qm.getQuestion());
                        String opts1 = "";
                        for (int a = 0; a < QuestionAdapter2.list.get(i).getOptions().size(); a++) {
                            String option = QuestionAdapter2.list.get(i).getOptions().get(a).getOptionText();

                            if (QuestionAdapter2.list.get(i).getOptions().get(a).isChecked()) {

                                String ans = QuestionAdapter2.list.get(i).getOptions().get(a).getOptionText();
                                answer = answer + "" + ans + "||";
                            }
                            opts1 += option + "||";


                            //jsonArray.put(option);

                        }
                        opts1 = opts1.substring(0, opts1.length() - 2);
                        answer = answer.substring(0, answer.length() - 2);
                        q.put("correct", answer);
                        q.put("answer", opts1);
                        //q.put("answer",answer);
                        break;
                    case "drop_down":
                        q.put("type", "sl");
                        q.put("content", qm.getQuestion());
                        String opts2 = "";
                        for (int a = 0; a < QuestionAdapter2.list.get(i).getOptions().size(); a++) {
                            String option = QuestionAdapter2.list.get(i).getOptions().get(a).getOptionText();

                            opts2 += option + "||";


                            //jsonArray.put(option);

                        }
                        opts2 = opts2.substring(0, opts2.length() - 2);
                        q.put("answer", opts2);
                        q.put("correct", "");
                        break;
                    case "date":
                        q.put("type", "dt");
                        q.put("content", qm.getQuestion());
                        q.put("correct", qm.getAnswer());
                        break;
                    case "time":
                        q.put("type", "tm");
                        q.put("content", qm.getQuestion());
                        q.put("correct", qm.getAnswer());
                        break;
                    case "section":
                        q.put("type", "section");
                        q.put("content", qm.getSectionTitle());
                        q.put("correct", qm.getSectionDescription());
                        if (qm.getQuestionImageUri() != null) {
                            q.put("question_image", qm.getQuestionImageUri().toString());
                            q.put("tag", "0");
                        } else if (qm.getQuestionImage() != null) {
                            q.put("question_image", qm.getQuestionImage());
                            q.put("tag", "1");
                        }
                        break;
                    case "flash_card":
                        q.put("type", "flash_card");
                        q.put("content", qm.getQuestion());
                        q.put("correct", qm.getAnswer());
                        break;


                }
                //q.put("type", qm.getQuestionType());
                q.put("parent", "");
                questionArray.put(q);

            }
            JSONArray jsonArray = new JSONArray();
            //jsonArray.put(0,questionArray);
            //{"e":{"id":"102","title":"ggffff","description":"","course_id":"1","body":"ffgggg","url":"","course_name":"ENGLISH"},"q":[[{"id":"47","parent":"0","content":"how to","title":"","type":"qo","answer":"","correct":""}]]}
            jsonObject1.put("q", jsonArray.put(0, questionArray));
            jsonObject1.put("e", jsonObject);
            Log.i("response", "f " + jsonObject);
            Log.i("response", "b " + jsonObject1.toString());


            if (questionArray.length() == 0) {
                Toast.makeText(TestUpload.this, "There is no questions", Toast.LENGTH_SHORT).show();
                return;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        //assessmentApiCall();

    }


    private void renderQo(JSONArray jsonArray1, String correct, QuestionsModel qm) {

        List<OptionsModel> opt = new ArrayList<>();
        try {
            String d = correct.substring(0, 1).trim();
            String cl = correct.substring(0, 1);
            char cl1 = correct.charAt(1);
            if (cl.equalsIgnoreCase("A") && Character.isDigit(cl1) || cl.equalsIgnoreCase("R") && Character.isDigit(cl1)) {
                for (int c = 0; c < jsonArray1.length(); c++) {
                    JSONObject jsonObject = jsonArray1.getJSONObject(c);
                    String text = jsonObject.getString("text");
                    String image = jsonObject.optString("filename");
                    OptionsModel op = new OptionsModel();
                    op.setOptionText(text);
                    op.setOptionImageUrl(image);
                    op.setCorrectAnswer(correct);
                    if (!d.isEmpty()) {
                        try {

                            if (Integer.parseInt(d) == c + 1) {
                                op.setChecked(true);
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();

                        }
                    }
                    op.setType("multichoice");
                    opt.add(op);
                }
            } else {
                for (int c = 0; c < jsonArray1.length(); c++) {
                    JSONObject jsonObject = jsonArray1.getJSONObject(c);
                    String text = jsonObject.getString("text");
                    String image = jsonObject.optString("filename");
                    OptionsModel op = new OptionsModel();
                    op.setOptionText(text);
                    op.setOptionImageUrl(image);
                    //Log.i("response1","option "+a[c]+" "+" correct "+correct);

                    if (text.trim().equalsIgnoreCase(correct.trim())) {
                        op.setChecked(true);
                        op.setCorrectAnswer(correct);
                    }

                    op.setType("multichoice");
                    opt.add(op);
                }
            }
        } catch (StringIndexOutOfBoundsException | JSONException e) {
            e.printStackTrace();
        }


        qm.setQuestionType("multiple_choice");
        qm.setOptions(opt);
    }

    private void renderQo(String[] a, String correct, QuestionsModel qm) {

        List<OptionsModel> opt = new ArrayList<>();
        try {
            String d = correct.substring(1).trim();
            String cl = correct.substring(0, 1);
            char cl1 = correct.charAt(1);
            if (cl.equalsIgnoreCase("A") && Character.isDigit(cl1) || cl.equalsIgnoreCase("R") && Character.isDigit(cl1)) {
                for (int c = 0; c < a.length; c++) {
                    OptionsModel op = new OptionsModel();
                    op.setOptionText(a[c]);
                    op.setCorrectAnswer(correct);
                    if (d != null && !d.isEmpty()) {
                        try {

                            if (Integer.parseInt(d) == c + 1) {
                                op.setChecked(true);
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();

                        }
                    }
                    op.setType("multichoice");
                    opt.add(op);
                }
            } else {
                for (int c = 0; c < a.length; c++) {
                    OptionsModel op = new OptionsModel();
                    op.setOptionText(a[c]);
                    //Log.i("response1","option "+a[c]+" "+" correct "+correct);

                    if (a[c].trim().equalsIgnoreCase(correct.trim())) {
                        op.setChecked(true);
                        op.setCorrectAnswer(correct);
                    }

                    op.setType("multichoice");
                    opt.add(op);
                }
            }
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }


        qm.setQuestionType("multiple_choice");
        qm.setOptions(opt);
    }

    private void renderQm(String answer1, String correct, QuestionsModel qm) {
        String[] a1 = answer1.split("\\|\\|");
        String[] t = correct.split("\\|\\|");
        List<OptionsModel> opt1 = new ArrayList<>();
        for (int c = 0; c < a1.length; c++) {
            OptionsModel op = new OptionsModel();
            op.setOptionText(a1[c]);
            op.setCorrectAnswer(correct);
            op.setType("checkbox");
            for (int q = 0; q < t.length; q++) {
                if (a1[c].equalsIgnoreCase(t[q])) {
                    op.setChecked(true);
                }
            }
                                /*if(a1[c].equalsIgnoreCase(correct)){
                                    op.setChecked(true);
                                }*/
            opt1.add(op);
        }
        qm.setOptions(opt1);

    }

    public void render(JSONArray jsonArray) {
        try {
            JSONArray questionArray = jsonArray.getJSONArray(0);
            for (int b = 0; b < questionArray.length(); b++) {
                JSONObject object = questionArray.getJSONObject(b);
                String question = object.getString("content");
                //String answers = object.getString("answer");
                String type = object.getString("type").trim();
                String id = object.getString("id");
                String imageUrl = object.optString("question_image");
                QuestionsModel qm = new QuestionsModel();
                qm.setQuestion(question);
                qm.setQuestionId(id);
                qm.setQuestionImage(imageUrl);
                Log.i("type", "t " + type);

                String correct;
                //String[] a = answers.split("\\|\\|");
                switch (type) {
                    case "section":
                    case "qp":
                        correct = object.optString("correct");
                        qm.setSectionTitle(qm.getQuestion());
                        qm.setSectionDescription(correct);
                        qm.setQuestionType("section");
                        break;
                    case "qs":
                        //String answer = object.optString("answer");
                        correct = object.optString("correct");
                        qm.setAnswer(correct);
                        qm.setQuestionType("short_answer");
                        break;
                    case "qo":
                        String answer = object.optString("answer");
                        //correct = object.optString("correct").trim();

                        String correctValue = object.getString("correct");

                        JSONObject correctObject = new JSONObject(correctValue);

                        // String order = correctObject.optString("order");
                        correct = correctObject.optString("text");

                        Log.i("Correct Object ", correctObject.toString());


                        String d = null;
                        //String[] a = answer.split("\\|\\|");
                        if (answer.contains("||")) {
                            String[] a = answer.split("\\|\\|");
                            renderQo(a, correct, qm);

                        } else {
                            if (!answer.isEmpty()) {
                                JSONArray jsonArray1 = new JSONArray(answer);
                                renderQo(jsonArray1, correct, qm);
                            }
                        }
                        qm.setQuestionType("multiple_choice");

                        break;
                    case "qf":
                    case "af":
                        break;
                    case "qt":
                        correct = object.optString("correct");
                        qm.setAnswer(correct);
                        qm.setQuestionType("long_answer");
                        break;
                    case "em":
                        correct = object.optString("correct");
                        qm.setAnswer(correct);
                        qm.setQuestionType("email");
                        break;
                    case "nb":
                        correct = object.optString("correct");
                        qm.setAnswer(correct);
                        qm.setQuestionType("number");
                        break;
                    case "dt":
                        correct = object.optString("correct");
                        qm.setAnswer(correct);
                        qm.setQuestionType("date");
                        break;
                    case "tm":
                        correct = object.optString("correct");
                        qm.setAnswer(correct);
                        qm.setQuestionType("time");
                        break;
                    case "qm":
                        List<OptionsModel> opt = new ArrayList<>();
                        String answer1 = object.optString("answer");
                        correct = object.optString("correct");
                        if (answer1 != null && !answer1.isEmpty() && answer1.contains("||")) {
                            String[] a1 = answer1.split("\\|\\|");
                            String[] t = correct.split("\\|\\|");
                            List<OptionsModel> opt1 = new ArrayList<>();
                            for (int c = 0; c < a1.length; c++) {
                                OptionsModel op = new OptionsModel();
                                op.setOptionText(a1[c]);
                                op.setCorrectAnswer(correct);
                                op.setType("checkbox");
                                for (int q = 0; q < t.length; q++) {
                                    if (a1[c].equalsIgnoreCase(t[q])) {
                                        op.setChecked(true);
                                    }
                                }
                                /*if(a1[c].equalsIgnoreCase(correct)){
                                    op.setChecked(true);
                                }*/
                                opt1.add(op);
                            }
                            qm.setOptions(opt1);
                            qm.setQuestionType("check_box");
                        } else {
                            JSONArray jsonArray1 = new JSONArray(answer1);
                            for (int c = 0; c < jsonArray1.length(); c++) {
                                JSONObject jsonObject = jsonArray1.getJSONObject(c);
                                String text = jsonObject.getString("text");
                                String image = jsonObject.optString("filename");
                                OptionsModel op = new OptionsModel();
                                op.setOptionText(text);
                                op.setOptionImageUrl(image);
                                op.setCorrectAnswer(correct);
                                if (text.trim().equals(correct.trim())) {
                                    op.setChecked(true);
                                }
                                op.setType("checkbox");
                                opt.add(op);
                            }
                            qm.setOptions(opt);
                            qm.setQuestionType("check_box");
                        }
                        break;
                    case "sl":
                        qm.setQuestionType("drop_down");
                        String answer2 = object.optString("answer");
                        String[] a2 = answer2.split("\\|\\|");
                        List<OptionsModel> opt3 = new ArrayList<>();

                        for (int c = 0; c < a2.length; c++) {
                            OptionsModel op = new OptionsModel();
                            op.setOptionText(a2[c]);
                            op.setType("dropdown");
                            opt3.add(op);
                        }

                        qm.setOptions(opt3);
                        break;
                }
                    /*String[] a = answers.split("\\|\\|");
                    List<OptionsModel> opt = new ArrayList<>();
                    for(int c=0;c<a.length;c++){
                        OptionsModel op = new OptionsModel();
                        op.setOptionText(a[c]);
                        op.setCorrectAnswer(correct);
                        if(a[c].equalsIgnoreCase(correct)){
                            op.setChecked(true);
                        }
                        opt.add(op);
                    }
                    qm.setOptions(opt);*/
                questionsList.add(qm);
            }
            questionAdapter2.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void render(JSONObject jsonObject) {
        try {
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONArray questionArray = jsonObject.getJSONArray(key.trim());
                for (int b = 0; b < questionArray.length(); b++) {
                    JSONObject object = questionArray.getJSONObject(b);
                    String question = object.getString("content");
                    //String answers = object.getString("answer");
                    String type = object.getString("type").trim();
                    String id = object.getString("id");
                    String imageUrl = object.optString("question_image");
                    QuestionsModel qm = new QuestionsModel();
                    qm.setQuestion(question);
                    qm.setQuestionId(id);

                    qm.setQuestionImage(imageUrl);
                    String correct;
                    //String[] a = answers.split("\\|\\|");
                    switch (type) {
                        case "section":
                        case "qp":
                            correct = object.optString("correct");
                            qm.setSectionTitle(qm.getQuestion());
                            qm.setSectionDescription(correct);
                            qm.setQuestionType("section");
                            break;
                        case "qs":
                            //String answer = object.optString("answer");
                            correct = object.optString("correct");
                            qm.setAnswer(correct);
                            qm.setQuestionType("short_answer");
                            break;
                        case "qo":
                            String answer = object.optString("answer");
                            // correct = object.optString("correct").trim();
                            JSONObject correctObject = object.getJSONObject(
                                    "correct");

                            String order = correctObject.optString("order");
                            correct = correctObject.optString("text").trim();

                            //  || order.contains("||") || correct.contains("||")
                            String d = null;
                            //String[] a = answer.split("\\|\\|");
                            if (answer.contains("||")) {
                                String[] a = answer.split("\\|\\|");

                                switch (order) {
                                    case "0":
                                    case "1":
                                    case "2":
                                    case "3":
                                    case "4":
                                        renderQo(a, correct, qm);
                                        break;
                                    default:
                                        Log.i("Error", correct);
                                }
                            } else {
                                JSONArray jsonArray = new JSONArray(answer);
                                renderQo(jsonArray, correct, qm);
                            }
                            qm.setQuestionType("multiple_choice");
                            break;
                        case "qf":
                        case "af":
                            break;
                        case "qt":
                            correct = object.optString("correct");
                            qm.setAnswer(correct);
                            qm.setQuestionType("long_answer");
                            break;
                        case "em":
                            correct = object.optString("correct");
                            qm.setAnswer(correct);
                            qm.setQuestionType("email");
                            break;
                        case "nb":
                            correct = object.optString("correct");
                            qm.setAnswer(correct);
                            qm.setQuestionType("number");
                            break;
                        case "dt":
                            correct = object.optString("correct");
                            qm.setAnswer(correct);
                            qm.setQuestionType("date");
                            break;
                        case "tm":
                            correct = object.optString("correct");
                            qm.setAnswer(correct);
                            qm.setQuestionType("time");
                            break;
                        case "qm":
                            String answer1 = object.optString("answer");
                            correct = object.optString("correct");
                            String[] a1 = answer1.split("\\|\\|");
                            String[] t = correct.split("\\|\\|");
                            List<OptionsModel> opt1 = new ArrayList<>();
                            for (int c = 0; c < a1.length; c++) {
                                OptionsModel op = new OptionsModel();
                                op.setOptionText(a1[c]);
                                op.setCorrectAnswer(correct);
                                op.setType("checkbox");
                                for (int q = 0; q < t.length; q++) {
                                    if (a1[c].equalsIgnoreCase(t[q])) {
                                        op.setChecked(true);
                                    }
                                }
                                /*if(a1[c].equalsIgnoreCase(correct)){
                                    op.setChecked(true);
                                }*/
                                opt1.add(op);
                            }
                            qm.setOptions(opt1);
                            qm.setQuestionType("check_box");
                            break;
                        case "sl":
                            qm.setQuestionType("drop_down");
                            String answer2 = object.optString("answer");
                            String[] a2 = answer2.split("\\|\\|");
                            List<OptionsModel> opt3 = new ArrayList<>();

                            for (int c = 0; c < a2.length; c++) {
                                OptionsModel op = new OptionsModel();
                                op.setOptionText(a2[c]);
                                op.setType("dropdown");
                                opt3.add(op);
                            }

                            qm.setOptions(opt3);
                            break;
                    }
                    /*String[] a = answers.split("\\|\\|");
                    List<OptionsModel> opt = new ArrayList<>();
                    for(int c=0;c<a.length;c++){
                        OptionsModel op = new OptionsModel();
                        op.setOptionText(a[c]);
                        op.setCorrectAnswer(correct);
                        if(a[c].equalsIgnoreCase(correct)){
                            op.setChecked(true);
                        }
                        opt.add(op);
                    }
                    qm.setOptions(opt);*/
                    questionsList.add(qm);
                }
                questionAdapter2.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile = "", lastVal;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile = output.toString();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }

    private void showDialog(QuestionsModel qm, int position, int questionNo) {
      /*  QuestionDialog2 dialog2 = new QuestionDialog2(TestUpload.this,qm,position,0);
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.show();
        Window window = dialog2.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);*/

        FragmentActivity fragmentActivity = TestUpload.this;
        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
        QuestionDialog2 dialog2 =
                new QuestionDialog2(TestUpload.this, qm,
                        TestUpload.questionAdapter2.getItemCount() - 1, QuestionAdapter2.questionNumber);

        dialog2.setCancelable(false);
        dialog2.show(manager, "tag");

    }
}


