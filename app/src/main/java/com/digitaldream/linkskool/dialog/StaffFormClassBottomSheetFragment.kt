package com.digitaldream.linkskool.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.AttendanceActivity
import com.digitaldream.linkskool.activities.StaffUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class StaffFormClassBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var commentBtn: TextView
    private lateinit var skillsBehaviourBtn: TextView
    private lateinit var studentBtn: TextView
    private lateinit var attendanceBtn: TextView

    private var classId: String? = null
    private var className: String? = null
    private var levelId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            classId = it.getString(ARG_PARAM1)
            className = it.getString(ARG_PARAM2)
            levelId = it.getString(ARG_PARAM3)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(classId: String, className: String, levelId: String) =
            StaffFormClassBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, classId)
                    putString(ARG_PARAM2, className)
                    putString(ARG_PARAM3, levelId)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_staff_form_class, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        btnActions()
    }

    private fun setUpViews(view: View) {
        view.apply {
            commentBtn = findViewById(R.id.commentBtn)
            skillsBehaviourBtn = findViewById(R.id.skillsBehaviourBtn)
            studentBtn = findViewById(R.id.studentBtn)
            attendanceBtn = findViewById(R.id.attendanceBtn)
        }
    }

    private fun btnActions() {
        commentBtn.setOnClickListener {
            launchActivity("staff_comment", StaffUtils::class.java)
        }

        skillsBehaviourBtn.setOnClickListener {
            launchActivity("skills_behaviour", StaffUtils::class.java)
        }

        studentBtn.setOnClickListener {
            launchActivity("view_students", StaffUtils::class.java)
        }

        attendanceBtn.setOnClickListener {

            launchActivity("class_attendance", AttendanceActivity::class.java)
        }

    }

    private fun launchActivity(from: String, activity: Class<*>) {
        startActivity(
            Intent(requireContext(), activity)
                .putExtra("from", from)
                .putExtra("class_id", classId)
                .putExtra("class_name", className)
                .putExtra("level_id", levelId)
        )

        dismiss()
    }

}