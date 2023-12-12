package com.digitaldream.winskool.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.activities.StaffUtils;
import com.digitaldream.winskool.adapters.CBTCoursesAdapter;
import com.digitaldream.winskool.models.Exam;
import com.digitaldream.winskool.utils.AsyncTaskResult;
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
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class CBTCoursesFragment extends Fragment implements CBTCoursesAdapter.OnCourseClickListener {

    private RecyclerView mRecyclerView;
    private RelativeLayout mEmptyState;
    private ImageView mErrorImage;
    private TextView mErrorMessage;

    private static String mExamTypeId;
    private static List<Exam> mExamList;
    private static Dao<Exam, Long> mDao;


    ACProgressFlower progressFlower;

    public CBTCoursesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_c_b_t_courses, container,
                false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        mRecyclerView = view.findViewById(R.id.cbt_courses_recycler);
        mEmptyState = view.findViewById(R.id.empty_state);
        mErrorImage = view.findViewById(R.id.error_image);
        mErrorMessage = view.findViewById(R.id.error_message);


        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("exam",
                    Context.MODE_PRIVATE);
            mExamTypeId = Integer.toString(sharedPreferences.getInt("examTypeId", 1));

            mDao = DaoManager.createDao(databaseHelper.getConnectionSource(), Exam.class);
        } catch (Exception sE) {
            sE.printStackTrace();
        }

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Choose subject");
        setHasOptionsMenu(true);

        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        progressFlower = new ACProgressFlower.Builder(
                getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.WHITE)
                .bgColor(R.color.bg_color)
                .bgAlpha(0.5f)
                .build();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu,
                                    @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onCourseClick(int position) {
        Exam exam = mExamList.get(position);
        Intent intent = new Intent(getContext(), StaffUtils.class);
        intent.putExtra("course_name", exam.getCourse());
        intent.putExtra("from", "cbt_course");
        startActivity(intent);

    }


    @Override
    public void onResume() {
        super.onResume();

        loadCourses();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressFlower != null && progressFlower.isShowing()) {
            progressFlower.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressFlower != null && progressFlower.isShowing()) {
            progressFlower.dismiss();
        }
    }

    public void loadCourses() {
        try {
            QueryBuilder<Exam, Long> queryBuilder =
                    mDao.queryBuilder().distinct().selectColumns("course");
            queryBuilder.where().eq("examTypeId", mExamTypeId);
            mExamList = queryBuilder.query();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(mExamList,
                        Comparator.comparing(Exam::getCourse));
            }

            if (!mExamList.isEmpty()) {

                CBTCoursesAdapter adapter = new CBTCoursesAdapter(getContext(),
                        mExamList, this);
                LinearLayoutManager manager =
                        new LinearLayoutManager(getContext());
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(manager);
                mRecyclerView.setAdapter(adapter);

                if (adapter.getItemCount() == 0)
                    mEmptyState.setVisibility(View.VISIBLE);

                adapter.notifyDataSetChanged();

            } else {
                new loadExam().execute();
            }

        } catch (SQLException sE) {
            sE.printStackTrace();
        }

    }

    public void insertExam(String subject, String subjectId, String year,
                           String yearId) {
        try {
            Exam exam = new Exam();
            exam.setCourse(subject);
            exam.setExamId(Integer.parseInt(subjectId));
            exam.setYear(year);
            exam.setYearId(Integer.parseInt(yearId));
            exam.setExamTypeId(Integer.parseInt(mExamTypeId));
            mDao.create(exam);
            loadCourses();
        } catch (Exception ignored) {
        }
    }

    private class loadExam extends AsyncTask<String, Void,
            AsyncTaskResult<String>> {

        HttpURLConnection urlConnection = null;
        BufferedReader returnedRegister = null;


        @Override
        protected AsyncTaskResult<String> doInBackground(String... sStrings) {

            final String SIGNUP_BASE_URL =
                    "http://www.cbtportal.linkskool.com/api/get_course" +
                            ".php?json";
            String jsonString;
            Uri login = Uri.parse(SIGNUP_BASE_URL).buildUpon().build();
            URL sendRegister;

            try {

                sendRegister = new URL(login.toString());
                urlConnection =
                        (HttpURLConnection) sendRegister.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }
                returnedRegister = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = returnedRegister.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                if (stringBuilder.length() == 0) {
                    return null;
                }
                jsonString = stringBuilder.toString();


            } catch (IOException sE) {
                mEmptyState.setVisibility(View.VISIBLE);
                mErrorImage.setImageResource(R.drawable.no_internet);
                mErrorMessage.setText(R.string.no_internet);
                return new AsyncTaskResult<>(sE);
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();

                if (returnedRegister != null) {
                    try {
                        returnedRegister.close();
                    } catch (final IOException e) {
                        new AsyncTaskResult<>(e);

                    }
                }
            }

            return new AsyncTaskResult<>(jsonString);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressFlower.setCancelable(false);
            progressFlower.setCanceledOnTouchOutside(false);
            progressFlower.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressFlower.setCancelable(false);
            progressFlower.setCanceledOnTouchOutside(false);
            progressFlower.show();
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<String> sStringAsyncTaskResult) {

            if (sStringAsyncTaskResult.getException() ==
                    null && sStringAsyncTaskResult.getResult() != null) {

                try {

                    JSONArray examListObject = new JSONArray(
                            sStringAsyncTaskResult.getResult());

                    for (int i = 0; i < examListObject.length(); i++) {
                        JSONObject exam = examListObject.getJSONObject(i);
                        String subjectId = exam.getString("i");
                        String examYearArray = exam.getString("d");
                        JSONArray examYear = new JSONArray(examYearArray);

                        if (subjectId.equals(mExamTypeId)) {
                            for (int j = 0; j < examYear.length(); j++) {
                                JSONObject exam1 = examYear.getJSONObject(j);
                                String examSubject = exam1.getString("c");
                                String examId = exam1.getString("i");
                                String YearsArray = exam1.getString("y");
                                JSONArray yearArray = new JSONArray(YearsArray);
                                for (int k = 0; k < yearArray.length(); k++) {
                                    JSONObject exam2 = yearArray.getJSONObject(
                                            k);
                                    String yearId = exam2.getString("i");
                                    String year = exam2.getString("d");
                                    insertExam(examSubject, examId, year,
                                            yearId);
                                }
                            }
                        }
                    }
                } catch (JSONException sE) {

                    mEmptyState.setVisibility(View.VISIBLE);
                    mErrorImage.setImageResource(R.drawable.no_internet);
                    mErrorMessage.setText(R.string.no_internet);

                    new AsyncTaskResult<>(sE);
                }

                progressFlower.dismiss();

            } else if (isCancelled()) {
                startActivity(
                        new Intent(getContext(), CBTExamTypeFragment.class));

            } else {
                progressFlower.dismiss();
                mEmptyState.setVisibility(View.VISIBLE);
            }
        }
    }
}