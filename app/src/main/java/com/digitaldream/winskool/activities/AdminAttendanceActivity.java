package com.digitaldream.winskool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.digitaldream.winskool.R;
import com.digitaldream.winskool.adapters.SectionPagerAdapter;
import com.digitaldream.winskool.fragments.AdminClassAttendanceFragment;
import com.digitaldream.winskool.fragments.AdminCourseAttendanceFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;


public class AdminAttendanceActivity extends AppCompatActivity {
    String mStudentLevelId, mStudentClassId, mClassName, from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Attendance");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        mStudentLevelId = intent.getStringExtra("levelId");
        mStudentClassId = intent.getStringExtra("classId");
        mClassName = intent.getStringExtra("className");
        from = intent.getStringExtra("from");

        TextView name = findViewById(R.id.class_name);
        TextView classYear = findViewById(R.id.class_term);
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.container);

        SharedPreferences sharedPreferences = getSharedPreferences(
                "loginDetail", Context.MODE_PRIVATE);
        String term = sharedPreferences.getString("term", "");
        String year = sharedPreferences.getString("school_year", "");

        try {
            int previousYear = Integer.parseInt(year) - 1;

            String termText = "";
            switch (term) {
                case "1":
                    termText = "First Term";
                    break;
                case "2":
                    termText = "Second Term";
                    break;
                case "3":
                    termText = "Third Term";
                    break;
            }

            SectionPagerAdapter adapter =
                    new SectionPagerAdapter(getSupportFragmentManager());

            if (from.equals("result")) {

                name.setText(mClassName);
                classYear.setText(
                        String.format(Locale.getDefault(), "%d/%s %s",
                                previousYear, year, termText));

                adapter.addFragment(AdminClassAttendanceFragment.newInstance(
                        mStudentLevelId, mStudentClassId, mClassName), "Class");
                adapter.addFragment(AdminCourseAttendanceFragment.newInstance(mStudentClassId), "Course");

            }

            viewPager.setAdapter(adapter);

            tabLayout.setupWithViewPager(viewPager, true);

        } catch (NumberFormatException sE) {
            sE.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


}