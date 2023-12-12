package com.digitaldream.winskool.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.activities.ExamActivity;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.models.Exam;
import com.digitaldream.winskool.models.ExamQuestions;
import com.digitaldream.winskool.utils.AsyncTaskResult;
import com.digitaldream.winskool.utils.FunctionUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CBTConfirmationDialog extends Dialog {

    private List<Exam> mExamList;
    private Dao<Exam, Long> mExamDao;
    private Dao<ExamQuestions, Long> mExamQuestionsDao;
    private String mJson = "";
    private final String mCourseName;
    private final String mYear;

    Button mCancelBtn, mContinueBtn, mCloseBtn;
    private ImageView mErrorImage;
    private TextView mErrorMessage;
    private RelativeLayout mEmptyState, mUnEmptyState;

    public CBTConfirmationDialog(@NonNull Context context, String sCourseName
            , String sYear) {
        super(context);
        mCourseName = sCourseName;
        mYear = sYear;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_cbt_confirmation);

        TextView subject = findViewById(R.id.subject);
        TextView examYear = findViewById(R.id.year);
        TextView examName = findViewById(R.id.exam_type);
        mCancelBtn = findViewById(R.id.cancel_button);
        mContinueBtn = findViewById(R.id.continue_button);
        mCloseBtn = findViewById(R.id.close_button);
        mErrorImage = findViewById(R.id.error_image);
        mErrorMessage = findViewById(R.id.error_message);
        mEmptyState = findViewById(R.id.empty_state);
        mUnEmptyState = findViewById(R.id.un_empty_state);


        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

        SharedPreferences sharedPreferences = Objects.requireNonNull(
                getContext()).getSharedPreferences("exam", Context.MODE_PRIVATE);
        String examTypeName = sharedPreferences.getString("examName", "");

        try {
            mExamDao =
                    DaoManager.createDao(databaseHelper.getConnectionSource(), Exam.class);
            mExamQuestionsDao =
                    DaoManager.createDao(databaseHelper.getConnectionSource(),
                            ExamQuestions.class);
        } catch (SQLException sE) {
            sE.printStackTrace();
        }

        examName.setText(FunctionUtils.capitaliseFirstLetter(examTypeName));
        subject.setText(FunctionUtils.capitaliseFirstLetter(mCourseName));
        examYear.setText(mYear);

        loadQuestions();

        mCancelBtn.setOnClickListener(sView -> dismiss());

        if (mJson.isEmpty()) {
            new getCourse().execute(Integer.toString(mExamList.get(0).getYearId()));
            //  Log.i("status", "ran " + mJson + " execution");

        }

        mContinueBtn.setOnClickListener(sView -> {

            if (!mJson.isEmpty()) {
                Log.i("jsonResponse", mJson);
                getContext().startActivity(new Intent(getContext(),
                        ExamActivity.class)
                        .putExtra("Json", mJson)
                        .putExtra("course", mCourseName)
                        .putExtra("year", mYear)
                        .putExtra("from", "cbt"));
            } else {
                Toast.makeText(getContext(), "Something went wrong",
                        Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });

        mCloseBtn.setOnClickListener(sView -> dismiss());

    }


    public void loadQuestions() {
        try {
            mExamList = mExamDao.queryBuilder().where().eq("course",
                    mCourseName).and().eq("year", mYear).query();


            if (!mExamList.isEmpty()) {
                Exam exam = mExamList.get(0);

                List<ExamQuestions> examQuestionsList = mExamQuestionsDao.queryBuilder().where().eq(
                        "examId",
                        exam.getExamId()).query();
               // Log.i("questionsList", String.valueOf(exam.getExamId()));
                if (!examQuestionsList.isEmpty()) {
                    ExamQuestions examQuestions = examQuestionsList.get(0);
                    mJson = examQuestions.getJson();
                }
            }
        } catch (SQLException sE) {
            sE.printStackTrace();
            mUnEmptyState.setVisibility(View.GONE);
            mEmptyState.setVisibility(View.VISIBLE);
        }
        //Log.i("Status", "ran" + mJson);
    }

    private class getCourse extends AsyncTask<String, Void,
            AsyncTaskResult<String>> {

        HttpURLConnection urlConnection = null;
        BufferedReader returnedLogin = null;
        URL receiveCourse = null;


        @Override
        protected AsyncTaskResult<String> doInBackground(String... sStrings) {

            final String LOGIN_BASE_URL =
                    "http://www.cbtportal.linkskool.com/api";
            final String JSON = "json";
            final String EXAM = "exam";
            final String CODE = "appCode";
            final String PATH = "exam_json.php";
            String jsonString;

            Uri login = Uri.parse(LOGIN_BASE_URL).buildUpon()
                    .appendPath(PATH)
                    .appendQueryParameter(JSON, "1")
                    .appendQueryParameter(CODE, "VDOK-124-CAUCHY")
                    .appendQueryParameter(EXAM, sStrings[0])
                    .build();
            try {
                receiveCourse = new URL(login.toString());
                urlConnection = (HttpURLConnection) receiveCourse.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                returnedLogin = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = returnedLogin.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                if (stringBuilder.length() == 0) {
                    return null;
                }
                jsonString = stringBuilder.toString();

            } catch (IOException e) {
                mUnEmptyState.setVisibility(View.GONE);
                mEmptyState.setVisibility(View.VISIBLE);
                mErrorImage.setImageResource(R.drawable.no_internet);
                mErrorMessage.setText(R.string.no_internet);
                return new AsyncTaskResult<>(e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (returnedLogin != null) {
                    try {
                        returnedLogin.close();
                    } catch (IOException sE) {
                        new AsyncTaskResult<>(sE);
                    }
                }
            }

            return new AsyncTaskResult<>(jsonString);
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<String> sStringAsyncTaskResult) {

            if (sStringAsyncTaskResult.getException() ==
                    null && sStringAsyncTaskResult.getResult() != null) {

                try {
                    JSONObject obj = new JSONObject(sStringAsyncTaskResult.getResult());
                    JSONObject object = obj.getJSONObject("e");
                    String id = object.getString("id");
                    Log.i("id", id);
                    ExamQuestions examQuestions = new ExamQuestions();
                    examQuestions.setExamId(mExamList.get(0).getExamId());
                    examQuestions.setJson(sStringAsyncTaskResult.getResult());
                    mExamQuestionsDao.create(examQuestions);
                    loadQuestions();

                } catch (Exception e) {
                    mUnEmptyState.setVisibility(View.GONE);
                    mEmptyState.setVisibility(View.VISIBLE);
                    mErrorImage.setImageResource(R.drawable.no_internet);
                    mErrorMessage.setText(R.string.no_internet);

                    new AsyncTaskResult<>(e);
                }
            } else if (isCancelled()) {
                // getContext().startActivity(new Intent(getContext(), ELibraryFragment.class));
                dismiss();
            }

        }

    }

}

