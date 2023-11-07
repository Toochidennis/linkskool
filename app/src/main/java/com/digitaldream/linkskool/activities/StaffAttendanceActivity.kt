package com.digitaldream.linkskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.fragments.StaffClassAttendanceFragment
import com.digitaldream.linkskool.fragments.StaffCourseAttendanceFragment

class StaffAttendanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_attendance)

        when (intent.getStringExtra("from")) {
            "course_attendance" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staff_attendance_container,
                        StaffCourseAttendanceFragment()
                    )
                }
            }

            "class_attendance" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staff_attendance_container,
                        StaffClassAttendanceFragment()
                    )
                }
            }
        }

    }
}