package com.digitaldream.linkskool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.digitaldream.linkskool.adapters.CustomSpinnerAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.activities.ExamActivity;
import com.digitaldream.linkskool.models.Exam;
import com.digitaldream.linkskool.models.ExamQuestions;
import com.digitaldream.linkskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomCBTDialog extends Dialog {
    public Activity c;
    public Dialog dialog;
    Button loadBtn;
    String exam_type_id;
    int exam_id;
    public String json;
    public String spinnerValue;
    public String spinnerValue1;
    List<Exam> examList;
    Dao<Exam, Long> dao;
    DatabaseHelper dbService;
    List<ExamQuestions> examQuestionsList;
    Dao<ExamQuestions, Long> dao1;
    public Spinner courseDropdown;
    public Spinner yearDropdown;
    public LinearLayout linearLayout1;
    public LinearLayout linearLayout2;
    private LinearLayout linearLayout3;
    public ProgressBar progressBar;
    public Button load;
    public Button loadingButton;
    public TextView loadintText;

    public CustomCBTDialog(Activity context) {
        super(context);
        this.c = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom_cbtdialog);
        try {
            json = "";
            courseDropdown = findViewById(R.id.course_spinner);
            yearDropdown = findViewById(R.id.year_spinner);
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("exam", Context.MODE_PRIVATE);
            exam_type_id = Integer.toString(sharedPreferences.getInt("examTypeId", 1));
            dbService = new DatabaseHelper(getContext());
            dao = DaoManager.createDao(dbService.getConnectionSource(), Exam.class);
            dao1 = DaoManager.createDao(dbService.getConnectionSource(), ExamQuestions.class);
            linearLayout1 = findViewById(R.id.loadingPanel1);
            progressBar = findViewById(R.id.progressbar3);
            loadintText = findViewById(R.id.loadingText);
            load = findViewById(R.id.load);
            linearLayout2 = findViewById(R.id.loadingPanel2);
            linearLayout3 = findViewById(R.id.loadingpanel3);
            loadingButton = findViewById(R.id.loadingButton);

            loadingButton.setOnClickListener(view -> {
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.VISIBLE);
                queryDatabase();
            });

            load.setOnClickListener(view -> {
                linearLayout2.setVisibility(View.GONE);
                linearLayout3.setVisibility(View.VISIBLE);
                if (!spinnerValue1.equals("-- Select Year --")) {
                    Log.i("response", "respo " + json);
                    getContext().startActivity(new Intent(getContext(), ExamActivity.class).putExtra("Json", json)
                            .putExtra("course", spinnerValue)
                            .putExtra("year", spinnerValue1).putExtra("from", "cbt"));
                } else {
                    Toast.makeText(getContext(), "Select Year", Toast.LENGTH_SHORT).show();
                }
            });
            //dialog.setContentView(contentView);
            //dialog.setCancelable(false);
            //dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            checkDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void checkDatabase() {
        try {
            QueryBuilder<Exam, Long> queryBuilder = dao.queryBuilder().distinct().selectColumns("course");
            queryBuilder.where().eq("examTypeId", exam_type_id);
            examList = queryBuilder.query();
            //util.courseDropdown.setPrompt("Select Course");
            if (!examList.isEmpty()) {
                ArrayList<String> courses = new ArrayList<String>();
                courses.add("-- Select Course --");
                for (Exam item : examList) {
                    courses.add(item.getCourse());
                }
                CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(getContext(), courses);
                courseDropdown.setAdapter(customSpinnerAdapter);
                courseDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spinnerValue = adapterView.getItemAtPosition(i).toString();
                        dataWork(spinnerValue);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            } else {
                new CustomCBTDialog.fetchExamTask().execute();
            }
        } catch (Exception e) {
        }
    }

    private class fetchExamTask extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader returnedRegister = null;

        @Override
        protected String doInBackground(String... params) {

            final String SIGNUP_BASE_URL =
                    "http://www.cbtportal.linkskool.com/api/get_course.php?json";
            String jsonString = null;

            Uri login = Uri.parse(SIGNUP_BASE_URL).buildUpon()
                    .build();

            URL sendRegister = null;
            try {
                sendRegister = new URL(login.toString());
                urlConnection = (HttpURLConnection) sendRegister.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                returnedRegister = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = returnedRegister.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                jsonString = buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();

                if (returnedRegister != null) {
                    try {
                        returnedRegister.close();
                    } catch (final IOException e) {

                    }
                }
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("response", result);

            if (result != null) {
                try {
                    JSONArray examListObject = new JSONArray(result);
                    try {
                        for (int i = 0; i < examListObject.length(); i++) {
                            JSONObject exam = examListObject.getJSONObject(i);
                            String subjectId = exam.getString("i");
                            String examYearArray = exam.getString("d");
                            JSONArray examYear = new JSONArray(examYearArray);
                            if (subjectId.equals(exam_type_id)) {
                                for (int j = 0; j < examYear.length(); j++) {
                                    JSONObject exam1 = examYear.getJSONObject(j);
                                    String examSubject = exam1.getString("c");
                                    String examId = exam1.getString("i");
                                    String YearsArray = exam1.getString("y");
                                    JSONArray yearArray = new JSONArray(YearsArray);
                                    for (int k = 0; k < yearArray.length(); k++) {
                                        JSONObject exam2 = yearArray.getJSONObject(k);
                                        String yearId = exam2.getString("i");
                                        String year = exam2.getString("d");
                                        insertExam(examSubject, examId, year, yearId);
                                    }
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                } catch (JSONException ignored) {

                }
            }
        }
    }

    public void insertExam(String subject, String subjectId, String year, String yearId) {
        try {
            Exam exam = new Exam();
            exam.setCourse(subject);
            exam.setExamId(Integer.parseInt(subjectId));
            exam.setYear(year);
            exam.setYearId(Integer.parseInt(yearId));
            exam.setExamTypeId(Integer.parseInt(exam_type_id));
            dao.create(exam);
            checkDatabase();
        } catch (Exception ignored) {
        }
    }

    public void dataWork(String subject) {
        try {
            QueryBuilder<Exam, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq("course", subject);
            examList = queryBuilder.query();
            if (!examList.isEmpty()) {
                ArrayList<String> courses = new ArrayList<String>();
                courses.add("-- Select Year --");
                for (Exam item : examList) {
                    courses.add(item.getYear());
                }
                CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(getContext(), courses);
                yearDropdown.setAdapter(customSpinnerAdapter);
                yearDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spinnerValue1 = adapterView.getItemAtPosition(i).toString();
                        if (i != 0) {
                            loadBtn.setEnabled(true);
                            loadBtn.setBackground(getContext().getDrawable(R.drawable.button1));
                        } else {
                            loadBtn.setEnabled(false);
                            loadBtn.setBackgroundColor(getContext().getResources().getColor(R.color.grey));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        } catch (Exception e) {
        }
    }

    public void queryDatabase() {
        try {
            QueryBuilder<Exam, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq("course", spinnerValue).and().eq("year", spinnerValue1);
            examList = queryBuilder.query();

            if (!examList.isEmpty()) {
                Exam exam = examList.get(0);
                exam_id = exam.getId();

                QueryBuilder<ExamQuestions, Long> queryBuilder1 = dao1.queryBuilder();
                queryBuilder1.where().eq("examId", exam.getId());
                examQuestionsList = queryBuilder1.query();
                if (!examQuestionsList.isEmpty()) {
                    try {
                        ExamQuestions examQuestions = examQuestionsList.get(0);
                        json = examQuestions.getJson();
                        progressBar.setVisibility(View.GONE);
                        loadintText.setText("Loading Completed!!");
                        load.setVisibility(View.VISIBLE);
                    } finally {
                        //cursor1.close();
                    }
                } else {
                    new CustomCBTDialog.getCourse().execute(Integer.toString(exam.getYearId()));
                }
            }
        } catch (Exception e) {
        }
    }

    private class getCourse extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader returnedLogin = null;
        URL receiveCourse = null;

        @Override
        protected String doInBackground(String... params) {
            final String LOGIN_BASE_URL =
                    "http://www.cbtportal.linkskool.com/api";
            final String JSON = "json";
            final String EXAM = "exam";
            final String CODE = "appCode";
            final String PATH = "exam_json.php";
            String jsonString = null;

            Uri login = Uri.parse(LOGIN_BASE_URL).buildUpon()
                    .appendPath(PATH)
                    .appendQueryParameter(JSON, "1")
                    .appendQueryParameter(CODE, "VDOK-124-CAUCHY")
                    .appendQueryParameter(EXAM, params[0])
                    .build();
            try {
                receiveCourse = new URL(login.toString());
                urlConnection = (HttpURLConnection) receiveCourse.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                returnedLogin = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = returnedLogin.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonString = buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {

            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();

                if (returnedLogin != null) {
                    try {
                        returnedLogin.close();
                    } catch (final IOException e) {

                    }
                }
            }

            return jsonString;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (result != null) {
                    json = result;
                    progressBar.setVisibility(View.GONE);
                    loadintText.setText("Loading Completed!!");
                    load.setVisibility(View.VISIBLE);
                    JSONObject obj = new JSONObject(result);
                    JSONObject object = obj.getJSONObject("e");
                    String id = object.getString("id");
                    ExamQuestions examQuestions = new ExamQuestions();
                    examQuestions.setExamId(Integer.parseInt(id));
                    examQuestions.setJson(result);
                    dao1.create(examQuestions);
                    //startDisplay(result);
                } else {
                    queryDatabase();
                }
            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
                loadintText.setText("Error Occurred!!!");
                //util.load.setVisibility(View.VISIBLE);
            }
        }
    }

}
