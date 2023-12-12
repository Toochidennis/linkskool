package com.digitaldream.winskool.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.digitaldream.winskool.adapters.CustomSpinnerAdapter;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.activities.ExamActivity;
import com.digitaldream.winskool.models.Exam;
import com.digitaldream.winskool.models.ExamQuestions;
import com.digitaldream.winskool.R;
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
import java.util.ArrayList;
import java.util.List;


public class BlankFragment extends BottomSheetDialogFragment {
    String exam_type_id;
    int exam_id;
    public String json;
    public String spinnerValue;
    public  String spinnerValue1;
    List<Exam> examList;
    Dao<Exam,Long> dao;
    DatabaseHelper dbService;
    List<ExamQuestions> examQuestionsList;
    Dao<ExamQuestions,Long> dao1;
    public Spinner courseDropdown;
    public Spinner yearDropdown;
    public LinearLayout linearLayout1;
    public LinearLayout linearLayout2;
    public ProgressBar progressBar;
    public Button load;
    public Button loadingButton;
    public TextView loadintText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View contentView = View.inflate(getContext(), R.layout.login_bottom_sheet, container);

        try {
            json = "";
            courseDropdown = contentView.findViewById(R.id.course_spinner);
            yearDropdown = contentView.findViewById(R.id.year_spinner);
            exam_type_id = Integer.toString(new ExamFragment().getPref().getInt("examTypeId", 1));
            dbService = new DatabaseHelper(getContext());
            dao = DaoManager.createDao(dbService.getConnectionSource(), Exam.class);
            dao1 = DaoManager.createDao(dbService.getConnectionSource(), ExamQuestions.class);
            linearLayout1 = contentView.findViewById(R.id.loadingPanel1);
            progressBar = contentView.findViewById(R.id.progressbar3);
            loadintText = contentView.findViewById(R.id.loadingText);
            load = contentView.findViewById(R.id.load);
            linearLayout2 = contentView.findViewById(R.id.loadingPanel2);
            loadingButton = contentView.findViewById(R.id.loadingButton);
            loadingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linearLayout1.setVisibility(View.GONE);
                    linearLayout2.setVisibility(View.VISIBLE);
                    queryDatabase();
                }
            });
            load.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!spinnerValue1.equals("-- Select Year --")) {
                        startActivity(new Intent(getContext(), ExamActivity.class).putExtra("Json", json)
                                .putExtra("course", spinnerValue)
                                .putExtra("year", spinnerValue1));
                    }
                    else{
                        Toast.makeText(getContext(), "Select Year", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //dialog.setContentView(contentView);
            //dialog.setCancelable(false);
            //dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            checkDatabase();
        }catch(Exception e){}
        return contentView;
    }




    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        try {
            View contentView = View.inflate(getContext(), R.layout.login_bottom_sheet, null);
            Toast.makeText(getContext(),"i am ",Toast.LENGTH_SHORT).show();

            json = "";
            courseDropdown = contentView.findViewById(R.id.course_spinner);
            yearDropdown = contentView.findViewById(R.id.year_spinner);
            exam_type_id = Integer.toString(new ExamFragment().getPref().getInt("examTypeId", 1));
            dbService = new DatabaseHelper(getContext());
            dao = DaoManager.createDao(dbService.getConnectionSource(), Exam.class);
            dao1 = DaoManager.createDao(dbService.getConnectionSource(), ExamQuestions.class);
            linearLayout1 = contentView.findViewById(R.id.loadingPanel1);
            progressBar = contentView.findViewById(R.id.progressbar3);
            loadintText = contentView.findViewById(R.id.loadingText);
            load = contentView.findViewById(R.id.load);
            linearLayout2 = contentView.findViewById(R.id.loadingPanel2);
            loadingButton = contentView.findViewById(R.id.loadingButton);
            loadingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linearLayout1.setVisibility(View.GONE);
                    linearLayout2.setVisibility(View.VISIBLE);
                    queryDatabase();
                }
            });
            load.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!spinnerValue1.equals("-- Select Year --")) {
                        startActivity(new Intent(getContext(), ExamActivity.class).putExtra("Json", json)
                                .putExtra("course", spinnerValue)
                                .putExtra("year", spinnerValue1));
                    }
                    else{
                        Toast.makeText(getContext(), "Select Year", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.setContentView(contentView);
            //dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            checkDatabase();
        }catch(Exception e){}
    }


    public void checkDatabase(){
        try {
            QueryBuilder<Exam, Long> queryBuilder = dao.queryBuilder().distinct().selectColumns("course");
            queryBuilder.where().eq("examTypeId", exam_type_id);
            examList = queryBuilder.query();
            //util.courseDropdown.setPrompt("Select Course");
            if (!examList.isEmpty()) {
                ArrayList<String> courses = new ArrayList<String>();
                courses.add("-- Select Course --");
                for (Exam item:examList) {
                    courses.add(item.getCourse());
                }
                CustomSpinnerAdapter customSpinnerAdapter=new CustomSpinnerAdapter(getContext(),courses);
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
                new fetchExamTask().execute();
            }
        }catch(Exception e){}
    }
    private class fetchExamTask extends AsyncTask<String,Void,String> {
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
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
            }finally {
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
            if(result!=null){
                try {
                    JSONArray examListObject = new JSONArray(result);
                    try{
                        for (int i = 0; i<examListObject.length(); i++) {
                            JSONObject exam = examListObject.getJSONObject(i);
                            String subjectId = exam.getString("i");
                            String examYearArray = exam.getString("d");
                            JSONArray examYear = new JSONArray(examYearArray);
                            if(subjectId.equals(exam_type_id)) {
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
                    }
                    catch(Exception e){
                    }
                }catch(JSONException e) {

                }
            }
        }
    }
    public void insertExam(String subject,String subjectId,String year,String yearId){
        try {
            Exam exam = new Exam();
            exam.setCourse(subject);
            exam.setExamId(Integer.parseInt(subjectId));
            exam.setYear(year);
            exam.setYearId(Integer.parseInt(yearId));
            exam.setExamTypeId(Integer.parseInt(exam_type_id));
            dao.create(exam);
            checkDatabase();
        }catch(Exception e){}
    }
    public void dataWork(String subject) {
        try {
            QueryBuilder<Exam, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq("course", subject);
            examList = queryBuilder.query();
            if(!examList.isEmpty())
            {
                ArrayList<String> courses = new ArrayList<String>();
                courses.add("-- Select Year --");
                for (Exam item:examList) {
                    courses.add(item.getYear());
                }
                CustomSpinnerAdapter customSpinnerAdapter=new CustomSpinnerAdapter(getContext(),courses);
                yearDropdown.setAdapter(customSpinnerAdapter);
                yearDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spinnerValue1 = adapterView.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        }catch(Exception e){}
    }
    public void queryDatabase(){
        try {
            QueryBuilder<Exam, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq("course", spinnerValue).and().eq("year", spinnerValue1);
            examList = queryBuilder.query();
            if (!examList.isEmpty()){
                Exam exam = examList.get(0);
                exam_id = exam.getExamId();
                QueryBuilder<ExamQuestions, Long> queryBuilder1 = dao1.queryBuilder();
                queryBuilder1.where().eq("examId", exam.getExamId());
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
                    new getCourse().execute(Integer.toString(exam.getYearId()));
                }
            }
        }catch(Exception e){}
    }
    private class getCourse extends AsyncTask<String,Void,String> {
        HttpURLConnection urlConnection = null;
        BufferedReader returnedLogin = null;
        URL receiveCourse = null;
        @Override
        protected String doInBackground(String... params) {
            final String LOGIN_BASE_URL =
                    "http://www.cbtportal.linkskool.com/api";
            final String JSON = "json";
            final String EXAM = "exam";
            final String CODE="appCode";
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

            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {

            }finally {
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
                    ExamQuestions  examQuestions= new ExamQuestions();
                    examQuestions.setExamId(exam_id);
                    examQuestions.setJson(result);
                    dao1.create(examQuestions);
                    //startDisplay(result);
                } else {
                    queryDatabase();
                }
            }catch(Exception e){progressBar.setVisibility(View.GONE);
                loadintText.setText("Error Occurred!!!");
                //util.load.setVisibility(View.VISIBLE);
            }
        }
    }
}
