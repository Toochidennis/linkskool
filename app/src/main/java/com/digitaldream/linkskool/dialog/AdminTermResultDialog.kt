package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.CourseResultActivity
import com.digitaldream.linkskool.activities.StaffUtils
import com.digitaldream.linkskool.activities.SubjectResultUtil
import com.digitaldream.linkskool.activities.ViewCompositeResultWebView

class AdminTermResultDialog(
    sContext: Context,
    private var classId: String,
    private var courseId: String?,
    private var year: String,
    private var sTerm: String,
    private var from: String,
) : Dialog(sContext) {

    private var term: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.windowAnimations = R.style.DialogAnimation
            attributes.gravity = Gravity.BOTTOM
        }

        setContentView(R.layout.dialog_admin_term_result)

        val courseResult: TextView = findViewById(R.id.course_result)
        val compositeResult: TextView = findViewById(R.id.composite_result)
        val viewCourseResult: TextView = findViewById(R.id.view_course_result)
        val editCourseResult: TextView = findViewById(R.id.edit_course_result)
        val skillsBehaviourBtn: TextView = findViewById(R.id.skillsBehaviourBtn)
        val commentBtn: TextView = findViewById(R.id.commentBtn)
        val skillsLayout: LinearLayout = findViewById(R.id.skillsLayout)
        val resultLayout: LinearLayout = findViewById(R.id.resultLayout)
        val courseLayout: LinearLayout = findViewById(R.id.courseLayout)

        term = when (sTerm.lowercase()) {
            "First Term".lowercase() -> "1"
            "Second Term".lowercase() -> "2"
            "Third Term".lowercase() -> "3"
            else -> sTerm
        }

        if (from == "course") {
            skillsLayout.isVisible = false
            resultLayout.isVisible = false
            courseLayout.isVisible = true
        } else {
            skillsLayout.isVisible = true
            resultLayout.isVisible = true
            courseLayout.isVisible = false
        }

        editCourseResult.setOnClickListener {
            launchActivity(SubjectResultUtil::class.java, "edit")
        }

        viewCourseResult.setOnClickListener {
            launchActivity(SubjectResultUtil::class.java, "view")
        }

        courseResult.setOnClickListener {
            launchActivity(CourseResultActivity::class.java)
        }

        compositeResult.setOnClickListener {
            launchActivity(ViewCompositeResultWebView::class.java)
            dismiss()
        }

        skillsBehaviourBtn.setOnClickListener {
            launchActivity(StaffUtils::class.java, "skills_behaviour")
        }

        commentBtn.setOnClickListener {
            launchActivity(StaffUtils::class.java, "staff_comment")
        }
    }


    private fun launchActivity(activity: Class<*>, from: String = "") {
        context.startActivity(
            Intent(context, activity)
                .putExtra("class_id", classId)
                .putExtra("course_id", courseId)
                .putExtra("year", year)
                .putExtra("term", term)
                .putExtra("from", from)
        )
        dismiss()
    }

}