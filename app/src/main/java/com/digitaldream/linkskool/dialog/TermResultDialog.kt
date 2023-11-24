package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.CourseResultActivity
import com.digitaldream.linkskool.activities.SubjectResultUtil
import com.digitaldream.linkskool.activities.ViewClassResultWebview

class TermResultDialog(
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
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.attributes.gravity = Gravity.BOTTOM
        setContentView(R.layout.dialog_term_result)

        val courseResult: CardView = findViewById(R.id.course_result)
        val compositeResult: CardView = findViewById(R.id.composite_result)
        val viewCourseResult: CardView = findViewById(R.id.view_course_result)
        val editCourseResult: CardView = findViewById(R.id.edit_course_result)

        term = when (sTerm.lowercase()) {
            "First Term".lowercase() -> "1"
            "Second Term".lowercase() -> "2"
            "Third Term".lowercase() -> "3"
            else -> ""
        }

        if (from == "course") {
            courseResult.isVisible = false
            compositeResult.isVisible = false
            viewCourseResult.isVisible = true
            editCourseResult.isVisible = true
        } else {
            courseResult.isVisible = true
            compositeResult.isVisible = true
            viewCourseResult.isVisible = false
            editCourseResult.isVisible = false
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
            launchActivity(ViewClassResultWebview::class.java)
            dismiss()
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