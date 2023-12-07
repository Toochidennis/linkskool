package com.digitaldream.linkskool.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.fragments.AdminClassAttendanceFragment;
import com.digitaldream.linkskool.fragments.CBTCoursesFragment;
import com.digitaldream.linkskool.fragments.CBTExamTypeFragment;
import com.digitaldream.linkskool.fragments.CBTYearFragment;
import com.digitaldream.linkskool.fragments.StaffFormClassFragment;
import com.digitaldream.linkskool.fragments.LibraryGamesFragment;
import com.digitaldream.linkskool.fragments.LibraryVideosFragment;
import com.digitaldream.linkskool.fragments.StaffResultDashboardFragment;
import com.digitaldream.linkskool.fragments.StaffFormClassStudentsFragment;
import com.digitaldream.linkskool.fragments.StaffResultCommentFragment;
import com.digitaldream.linkskool.fragments.StaffSkillsBehaviourFragment;

public class StaffUtils extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_utils);

        Intent i = getIntent();
        String classId = i.getStringExtra("class_id");
        String levelId = i.getStringExtra("level_id");
        String className = i.getStringExtra("class_name");
        String courseName = i.getStringExtra("course_name");

        switch (i.getStringExtra("from")) {

            case "result":

            case "course":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container, new StaffResultDashboardFragment()).commit();
                break;

            case "student":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container, new StaffFormClassFragment()).commit();
                break;

            case "cbt":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,
                        new CBTExamTypeFragment()).commit();
                break;

            case "exam_type":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,
                        new CBTCoursesFragment()).commit();
                break;

            case "cbt_course":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,
                        CBTYearFragment.newInstance(courseName, "")).commit();
                break;

            case "videos":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,
                        new LibraryVideosFragment()).commit();
                break;

            case "games":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,
                        new LibraryGamesFragment()).commit();
                break;

            case "view_students":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,
                        StaffFormClassStudentsFragment.newInstance(classId)).commit();
                break;

            case "staff_comment":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,
                        StaffResultCommentFragment.newInstance(classId)).commit();
                break;

            case "skills_behaviour":
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragment_container,
                        StaffSkillsBehaviourFragment.newInstance(classId)).commit();
                break;

        }


    }
}
