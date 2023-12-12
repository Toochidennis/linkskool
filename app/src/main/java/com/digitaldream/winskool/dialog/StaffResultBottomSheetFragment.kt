package com.digitaldream.winskool.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.AttendanceActivity
import com.digitaldream.winskool.activities.StaffEnterResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"


class StaffResultBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var inputResultBtn: TextView
    private lateinit var viewResultBtn: TextView
    private lateinit var attendanceBtn: TextView

    private var courseId: String? = null
    private var levelId: String? = null
    private var classId: String? = null
    private var courseName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseId = it.getString(ARG_PARAM1)
            levelId = it.getString(ARG_PARAM2)
            classId = it.getString(ARG_PARAM3)
            courseName = it.getString(ARG_PARAM4)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottom_sheet_fragment_staff_result, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(levelId: String, courseId: String, classId: String, courseName: String) =
            StaffResultBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, levelId)
                    putString(ARG_PARAM2, courseId)
                    putString(ARG_PARAM3, classId)
                    putString(ARG_PARAM4, courseName)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        btnActions()
    }

    private fun setUpViews(view: View) {
        view.apply {
            inputResultBtn = findViewById(R.id.inputResultBtn)
            viewResultBtn = findViewById(R.id.viewResultBtn)
            attendanceBtn = findViewById(R.id.attendanceBtn)
        }
    }

    private fun btnActions() {
        inputResultBtn.setOnClickListener {
            launchActivity("add_result", StaffEnterResult::class.java)
        }

        viewResultBtn.setOnClickListener {
            launchActivity("view_result", StaffEnterResult::class.java)
        }

        attendanceBtn.setOnClickListener {
            launchActivity("course_attendance", AttendanceActivity::class.java)
        }

    }

    private fun launchActivity(from: String, activity: Class<*>) {
        startActivity(
            Intent(requireContext(), activity)
                .putExtra("from", from)
                .putExtra("course_id", courseId ?: "")
                .putExtra("level_id", levelId ?: "")
                .putExtra("class_id", classId ?: "")
                .putExtra("course_name", courseName ?: "")
        )

        dismiss()
    }
}