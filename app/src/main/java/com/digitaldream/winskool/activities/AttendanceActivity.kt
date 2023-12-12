package com.digitaldream.winskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.digitaldream.winskool.R
import com.digitaldream.winskool.fragments.CourseAttendanceFragment
import com.digitaldream.winskool.fragments.StaffClassAttendanceFragment

class AttendanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        val levelId = intent.getStringExtra("level_id") ?: ""
        val className = intent.getStringExtra("class_name") ?: ""
        val classId = intent.getStringExtra("class_id") ?: ""
        val courseId = intent.getStringExtra("course_id") ?: ""
        val courseName = intent.getStringExtra("course_name") ?: ""

        when (intent.getStringExtra("from")) {
            "course_attendance" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staff_attendance_container,
                        CourseAttendanceFragment.newInstance(courseId, classId, courseName)
                    )
                }
            }

            "class_attendance" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staff_attendance_container,
                        StaffClassAttendanceFragment.newInstance(levelId, classId, className)
                    )
                }
            }
        }

    }
}