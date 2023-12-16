package com.digitaldream.linkskool.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.adapters.StaffDashboardResultAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.models.StaffResultModel;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


public class StaffResultDashboardFragment extends Fragment {
    private RecyclerView resultRecyclerView;
    private TextView emptyTxt;
    private Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff_result_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpViews(view);

        init();
    }

    private void setUpViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        resultRecyclerView = view.findViewById(R.id.resultRecyclerView);
        emptyTxt = view.findViewById(R.id.emptyTxt);
    }


    private void init() {
        ((AppCompatActivity) (requireActivity())).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) (requireActivity())).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Courses");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> requireActivity().onBackPressed());

        loadResults();
    }


    private void loadResults() {
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());

            Dao<CourseTable, Long> mCourseDao =
                    DaoManager.createDao(databaseHelper.getConnectionSource(), CourseTable.class);
            List<CourseTable> courseList = mCourseDao.queryForAll();

            if (!courseList.isEmpty()) {
                setUpResultAdapter(courseList);
            } else {
                emptyTxt.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setUpResultAdapter(List<CourseTable> courseList) {
        List<StaffResultModel> resultModel = new ArrayList<>();
        HashMap<String, List<CourseTable>> courseHashmap = sortCourseList(courseList);

        for (Map.Entry<String, List<CourseTable>> entry : courseHashmap.entrySet()) {
            resultModel.add(new StaffResultModel(entry.getKey(), entry.getValue()));
        }

        Collections.sort(resultModel, (s1, s2) ->
                s1.getCourseName().compareToIgnoreCase(s2.getCourseName()));

        StaffDashboardResultAdapter mResultAdapter =
                new StaffDashboardResultAdapter(getParentFragmentManager(), resultModel);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        resultRecyclerView.setHasFixedSize(true);
        resultRecyclerView.setAdapter(mResultAdapter);
    }

    private HashMap<String, List<CourseTable>> sortCourseList(List<CourseTable> courseList) {
        HashMap<String, List<CourseTable>> courseHashmap = new HashMap<>();

        for (CourseTable itemModel : courseList) {
            String courseName = itemModel.getCourseName();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<CourseTable> courseTableList =
                        courseHashmap.getOrDefault(courseName, new ArrayList<>());
                assert courseTableList != null;
                courseTableList.add(itemModel);
                courseHashmap.put(courseName, courseTableList);
            }
        }

        return courseHashmap;
    }

}