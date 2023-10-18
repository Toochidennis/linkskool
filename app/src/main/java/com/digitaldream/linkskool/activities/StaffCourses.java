package com.digitaldream.linkskool.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class StaffCourses extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Dao<CourseTable,Long> courseDao;
    private List<CourseTable> courseList;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private SectionedRecyclerViewAdapter adapter;
    private LinearLayout emptyState;
    private TextView staffName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_courses);

        toolbar = findViewById(R.id.toolbar);
        staffName = findViewById(R.id.staff_name);

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        Objects.requireNonNull(getSupportActionBar()).setTitle("Courses");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        databaseHelper = new DatabaseHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String user_name = sharedPreferences.getString("user","");
        String[] split = user_name.toLowerCase().split(" ");

        StringBuilder builder = new StringBuilder();
        for(String s : split){
            try {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap).append(" ");

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        staffName.setText( builder.toString());


        recyclerView = findViewById(R.id.all_staff_course_recycler);
        emptyState = findViewById(R.id.staff_course_empty_state);

        adapter = new SectionedRecyclerViewAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

//        courseList.clear();
        adapter.removeAllSections();

        try {
            courseDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseTable.class);
            courseList = courseDao.queryForAll();


            List<String> stringList = new ArrayList<>();

            Collections.sort(courseList, (s1, s2) ->
                    s1.getCourseName().substring(0, 1)
                            .compareToIgnoreCase(s2.getCourseName().substring(0, 1)));

            for (int i = 0; i < courseList.size(); i++) {
                List<CourseTable> courseTable =
                        getCourseTableList(courseList.get(i).getCourseName());
                if(!stringList.contains(courseList.get(i).getCourseName())){
                    adapter.addSection(new SectionAdapter(courseTable,
                            courseList.get(i).getCourseName()));
                    stringList.add(courseList.get(i).getCourseName());
                }


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }



        if(!courseList.isEmpty()) {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
        }else{
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }
    }

    public  List<CourseTable> getCourseTableList(String sCourseName) throws SQLException {

        QueryBuilder<CourseTable, Long> queryBuilder =
                courseDao.queryBuilder();
        queryBuilder.where().eq("courseName", sCourseName);
        return queryBuilder.query();

    }


    public static class SectionAdapter extends Section {

        private final List<CourseTable> mCourseTableList;
        private final String headerTitle;

        public SectionAdapter(List<CourseTable> sCourseTableList,
                              String sHeaderTitle) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.staff_course_item)
                    .headerResourceId(R.layout.head)
                    .build());
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
        public void onBindItemViewHolder(RecyclerView.ViewHolder sViewHolder, int sI) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) sViewHolder;
            CourseTable courseTable = mCourseTableList.get(sI);

            itemViewHolder.mClassName.setText(courseTable.getClassName());

            GradientDrawable mutate =
                    (GradientDrawable) itemViewHolder.mLayout.getBackground().mutate();
            Random rnd = new Random();
            int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            mutate.setColor(currentColor);

            itemViewHolder.mLayout.setBackground(mutate);

        }


        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            String title =
                    headerTitle.substring(0, 1).toUpperCase() + "" + headerTitle.substring(1).toLowerCase();
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
