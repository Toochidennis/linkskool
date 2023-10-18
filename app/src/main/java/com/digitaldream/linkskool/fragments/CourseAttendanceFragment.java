package com.digitaldream.linkskool.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.activities.CourseAttendance;
import com.digitaldream.linkskool.adapters.CourseAttendanceFragmentAdapter;
import com.digitaldream.linkskool.models.CourseTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;


public class CourseAttendanceFragment extends Fragment implements CourseAttendanceFragmentAdapter.OnCourseClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private CourseAttendanceFragmentAdapter mCourseAttendanceFragmentAdapter;
    private Dao<CourseTable, Long> mCourseDao;
    private List<CourseTable> mCourseList;
    DatabaseHelper mDatabaseHelper;
    private RecyclerView mRecyclerView;
    private RelativeLayout empty;


    private String mStudentClassId;
    private String mStudentLevelId;


    public CourseAttendanceFragment() {
        // Required empty public constructor
    }


    public static CourseAttendanceFragment newInstance(String param1,
                                                       String param2) {
        CourseAttendanceFragment fragment = new CourseAttendanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStudentClassId = getArguments().getString(ARG_PARAM1);
            mStudentLevelId = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_attendance,
                container, false);

        empty = view.findViewById(R.id.empty_state);
        mRecyclerView = view.findViewById(R.id.attendance_recycler);

        // Instantiating database connection
        mDatabaseHelper = new DatabaseHelper(getContext());
        try {
            // query courses from database
            mCourseDao =
                    DaoManager.createDao(mDatabaseHelper.getConnectionSource(),
                            CourseTable.class);
            mCourseList = mCourseDao.queryForAll();

            Random rnd = new Random();
            for (CourseTable ct : mCourseList) {
                int currentColor = Color.argb(255, rnd.nextInt(256),
                        rnd.nextInt(256), rnd.nextInt(256));
                ct.setColor(currentColor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        mCourseAttendanceFragmentAdapter = new CourseAttendanceFragmentAdapter(
                getContext(),
                mCourseList, this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mCourseAttendanceFragmentAdapter);


        if (!mCourseList.isEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);

        } else {
            empty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        return view;

    }

    @Override
    public void onCourseClick(int position) {

        Intent newIntent = new Intent(getContext(),
                CourseAttendance.class);
        newIntent.putExtra("levelId", mStudentLevelId);
        newIntent.putExtra("classId", mStudentClassId);
        newIntent.putExtra("courseId", mCourseList.get(position)
                .getCourseId());
        newIntent.putExtra("from", "admin");
        startActivity(newIntent);
    }


    @Override
    public void onResume() {
        super.onResume();

        mCourseList.clear();

        try {
            // query courses from database
            mCourseDao =
                    DaoManager.createDao(mDatabaseHelper.getConnectionSource(),
                            CourseTable.class);
            mCourseList = mCourseDao.queryForAll();

            Random rnd = new Random();
            for (CourseTable ct : mCourseList) {
                int currentColor = Color.argb(255, rnd.nextInt(256),
                        rnd.nextInt(256), rnd.nextInt(256));
                ct.setColor(currentColor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        mCourseAttendanceFragmentAdapter = new CourseAttendanceFragmentAdapter(
                getContext(),
                mCourseList, this);
        mRecyclerView.setAdapter(mCourseAttendanceFragmentAdapter);

        if (!mCourseList.isEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);

        } else {
            empty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

    }
}