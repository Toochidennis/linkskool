package com.digitaldream.linkskool.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.activities.CourseAttendance;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.CourseTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class StaffCourseAttendanceFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SectionedRecyclerViewAdapter mAdapter;
    private List<CourseTable> mCourseTableList;
    private Dao<CourseTable, Long> mCourseTableDao;
    private RelativeLayout mRelativeLayout;
    private DatabaseHelper mDatabaseHelper;


    public StaffCourseAttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_staff_course_attendance,
                container, false);

        mRecyclerView = view.findViewById(R.id.staff_course_recycler);
        mRelativeLayout = view.findViewById(R.id.empty_state);

        mDatabaseHelper = new DatabaseHelper(getContext());

        mAdapter = new SectionedRecyclerViewAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        mCourseTableList.clear();
        mAdapter.removeAllSections();

        try {
            mCourseTableDao =
                    DaoManager.createDao(mDatabaseHelper.getConnectionSource(),
                            CourseTable.class);
            mCourseTableList = mCourseTableDao.queryForAll();


            List<String> stringList = new ArrayList<>();

            Collections.sort(mCourseTableList, (s1, s2) ->
                    s1.getCourseName().substring(0, 1)
                            .compareToIgnoreCase(
                                    s2.getCourseName().substring(0, 1)));

            for (int i = 0; i < mCourseTableList.size(); i++) {
                List<CourseTable> courseTable =
                        getCourseTableList(
                                mCourseTableList.get(i).getCourseName());
                //Log.i("dddd", mCourseTableList.get(i).getCourseName());
                if (!stringList.contains(
                        mCourseTableList.get(i).getCourseName())) {
                    mAdapter.addSection(new SectionAdapter(getContext(),
                            courseTable,
                            mCourseTableList.get(i).getCourseName()));
                    stringList.add(mCourseTableList.get(i).getCourseName());
                }


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        mRecyclerView.setAdapter(mAdapter);
        if (!mCourseTableList.isEmpty()) {
            mRelativeLayout.setVisibility(View.GONE);
        } else {
            mRelativeLayout.setVisibility(View.VISIBLE);
        }

    }

    public List<CourseTable> getCourseTableList(String sCourseName) throws SQLException {

        QueryBuilder<CourseTable, Long> queryBuilder =
                mCourseTableDao.queryBuilder();
        queryBuilder.where().eq("courseName", sCourseName);
        return queryBuilder.query();

    }

    public static class SectionAdapter extends Section {

        Context mContext;
        private final List<CourseTable> mCourseTableList;
        private final String headerTitle;

        public SectionAdapter(Context sContext,
                              List<CourseTable> sCourseTableList,
                              String sHeaderTitle) {
            super(SectionParameters.builder()
                    .itemResourceId(
                            R.layout.fragment_staff_course_attendance_item)
                    .headerResourceId(R.layout.head)
                    .build());
            mContext = sContext;
            mCourseTableList = sCourseTableList;
            headerTitle = sHeaderTitle;
        }

        @Override
        public int getContentItemsTotal() {
            return mCourseTableList.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder sViewHolder,
                                         int sI) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) sViewHolder;
            CourseTable courseTable = mCourseTableList.get(sI);

            itemViewHolder.mClassName.setText(courseTable.getClassName());

            GradientDrawable mutate =
                    (GradientDrawable) itemViewHolder.mLayout.getBackground().mutate();
            Random rnd = new Random();
            int currentColor = Color.argb(255, rnd.nextInt(256),
                    rnd.nextInt(256), rnd.nextInt(256));
            mutate.setColor(currentColor);

            itemViewHolder.mLayout.setBackground(mutate);

            itemViewHolder.itemView.setOnClickListener(sView -> {
                Intent intent = new Intent(mContext, CourseAttendance.class);
                intent.putExtra("levelId",
                        mCourseTableList.get(sI).getLevelId());
                intent.putExtra("classId",
                        mCourseTableList.get(sI).getClassId());
                intent.putExtra("courseId",
                        mCourseTableList.get(sI).getCourseId());
                intent.putExtra("from", "staff");
                mContext.startActivity(intent);


            });

        }


        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            String title =
                    headerTitle.substring(0, 1).toUpperCase() + ""
                            + headerTitle.substring(1).toLowerCase();
            headerViewHolder.mHeader.setText(title);
        }

        public static class ItemViewHolder extends RecyclerView.ViewHolder {

            private final TextView mClassName;
            private final LinearLayout mLayout;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);

                mClassName = itemView.findViewById(R.id.class_title);
                mLayout = itemView.findViewById(R.id.class_container);

            }


        }

        public static class HeaderViewHolder extends RecyclerView.ViewHolder {
            private final TextView mHeader;

            public HeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                mHeader = itemView.findViewById(R.id.course_name);
            }
        }


    }


}