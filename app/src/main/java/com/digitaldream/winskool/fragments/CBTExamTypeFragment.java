package com.digitaldream.winskool.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.activities.StaffUtils;
import com.digitaldream.winskool.adapters.CBTExamTypeAdapter;
import com.digitaldream.winskool.models.ExamType;
import com.digitaldream.winskool.utils.AsyncTaskResult;
import com.digitaldream.winskool.utils.FunctionUtils;
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
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;


public class CBTExamTypeFragment extends Fragment implements CBTExamTypeAdapter.OnExamClickListener {

    private RecyclerView mRecyclerView;
    private RelativeLayout mEmptyState;
    private ImageView mErrorImage;
    private TextView mErrorMessage;

    ProgressBar mAverageProgressBar, mMathsProgressBar, mEnglishProgressBar,
            mPhysicsProgressBar, mBiologyProgressBar, mChemistryProgressBar;

    private TextView mAverageScore, mMathsScore, mEnglishScore, mPhysicsScore
            , mBiologyScore, mChemistryScore;

    private List<ExamType> mExamTypeList;
    private Dao<ExamType, Long> mDao;

    ACProgressFlower progressFlower;

    public CBTExamTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_c_b_t_exam_type,
                container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        mRecyclerView = view.findViewById(R.id.exam_recycler);
        mEmptyState = view.findViewById(R.id.empty_state);
        mErrorImage = view.findViewById(R.id.error_image);
        mErrorMessage = view.findViewById(R.id.error_message);


        // score progress bar
        mAverageProgressBar = view.findViewById(R.id.progress_bar);
        mMathsProgressBar = view.findViewById(R.id.maths_progress_bar);
        mEnglishProgressBar = view.findViewById(R.id.english_progress_bar);
        mPhysicsProgressBar = view.findViewById(R.id.physics_progress_bar);
        mBiologyProgressBar = view.findViewById(R.id.biology_progress_bar);
        mChemistryProgressBar = view.findViewById(R.id.chemistry_progress_bar);

        //score text
        mAverageScore = view.findViewById(R.id.progress_text);
        mMathsScore = view.findViewById(R.id.maths_progress_text);
        mEnglishScore = view.findViewById(R.id.english_progress_text);
        mPhysicsScore = view.findViewById(R.id.physics_progress_text);
        mBiologyScore = view.findViewById(R.id.biology_progress_text);
        mChemistryScore = view.findViewById(R.id.chemistry_progress_text);


        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

        try {
            mDao = DaoManager.createDao(databaseHelper.getConnectionSource()
                    , ExamType.class);
        } catch (SQLException sE) {
            sE.printStackTrace();
        }

        ((AppCompatActivity) (requireActivity())).setSupportActionBar(
                toolbar);
        ActionBar actionBar =
                ((AppCompatActivity) requireActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Computer Based Test");
        toolbar.setNavigationOnClickListener(
                v -> requireActivity().onBackPressed());

        displayProgress();

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
    public void onExamClick(int position) {

        ExamType examType = mExamTypeList.get(position);
        SharedPreferences sharedPreferences =
                requireContext().getSharedPreferences(
                        "exam",
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("examTypeId", examType.getExamTypeId());
        editor.putString("examName", examType.getExamName());
        editor.apply();

        startActivity(
                new Intent(getContext(), StaffUtils.class).putExtra("from",
                        "exam_type"));
    }

    public void displayProgress() {
        FunctionUtils.animateObject(mAverageProgressBar, mAverageScore, 10);
        FunctionUtils.animateObject(mMathsProgressBar, mMathsScore, 0);
        FunctionUtils.animateObject(mEnglishProgressBar, mEnglishScore, 0);
        FunctionUtils.animateObject(mPhysicsProgressBar, mPhysicsScore, 0);
        FunctionUtils.animateObject(mBiologyProgressBar, mBiologyScore, 0);
        FunctionUtils.animateObject(mChemistryProgressBar, mChemistryScore, 0);
    }


    @Override
    public void onResume() {
        super.onResume();
        loadExam();
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

    private void loadExam() {
        try {
            mExamTypeList = mDao.queryForAll();

            Log.d("ExamList", mExamTypeList.toString());

            if (!mExamTypeList.isEmpty()) {

                CBTExamTypeAdapter adapter = new CBTExamTypeAdapter(
                        getContext(), mExamTypeList, this);
                GridLayoutManager manager =
                        new GridLayoutManager(getContext(), 2);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(manager);
                mRecyclerView.setAdapter(adapter);

                if (adapter.getItemCount() == 0)
                    mEmptyState.setVisibility(View.VISIBLE);

                adapter.notifyDataSetChanged();

            } else {
                new loadExamType().execute();
            }
        } catch (SQLException sE) {
            sE.printStackTrace();
        }

    }

    public void checkExam(JSONObject sJSONObject) {

        try {
            String title = sJSONObject.getString("t");
            String link = sJSONObject.getString("p");
            String status = sJSONObject.getString("s");
            String category = sJSONObject.getString("t");
            String id = sJSONObject.getString("i");
            QueryBuilder<ExamType, Long> queryBuilder = mDao.queryBuilder();
            queryBuilder.where().eq("examName", title);
            mExamTypeList = queryBuilder.query();

            if (mExamTypeList.isEmpty()) {
                ExamType examType = new ExamType();
                examType.setExamName(title);
                examType.setStatus(status);
                examType.setExamTypeId(Integer.parseInt(id));
                examType.setCategory(category);
                examType.setExamLogo(link);
                mDao.create(examType);
                loadExam();
            }
        } catch (SQLException | JSONException sE) {
            sE.printStackTrace();
        }
    }

    private class loadExamType extends AsyncTask<String, Void,
            AsyncTaskResult<String>> {

        HttpURLConnection urlConnection = null;
        BufferedReader returnedLogin = null;


        @Override
        protected AsyncTaskResult<String> doInBackground(String... sStrings) {

            final String EXAM_BASE_URL =
                    "http://www.cbtportal.linkskool.com/api/get_course" +
                            ".php?json";
            String jsonString;
            Uri login = Uri.parse(EXAM_BASE_URL).buildUpon().build();
            URL sendLogin;
            try {
                sendLogin = new URL(login.toString());
                urlConnection = (HttpURLConnection) sendLogin.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setUseCaches(false);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                returnedLogin = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = returnedLogin.readLine()) != null) {
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

                if (returnedLogin != null) {
                    try {
                        returnedLogin.close();
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
                    JSONArray examArray = new JSONArray(
                            sStringAsyncTaskResult.getResult());
                    for (int i = 0; i < examArray.length(); i++) {
                        JSONObject examObject = examArray.getJSONObject(i);
                        checkExam(examObject);
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
                        new Intent(getContext(), ELibraryFragment.class));

            } else {
                progressFlower.dismiss();
                mEmptyState.setVisibility(View.VISIBLE);
            }
        }
    }
}
