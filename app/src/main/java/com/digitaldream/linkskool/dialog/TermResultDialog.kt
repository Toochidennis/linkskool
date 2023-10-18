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
    private var sClassId: String,
    private var sCourseId: String?,
    private var sYear: String,
    private var sTerm: String,
    private var sFrom: String,
) : Dialog(sContext) {

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

        val term = when (sTerm.lowercase()) {
            "First Term".lowercase() -> "1"
            "Second Term".lowercase() -> "2"
            "Third Term".lowercase() -> "3"
            else -> ""
        }

        if (sFrom == "course") {
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
            context.startActivity(
                Intent(context, SubjectResultUtil::class.java)
                    .putExtra("classId", sClassId)
                    .putExtra("courseId", sCourseId)
                    .putExtra("year", sYear)
                    .putExtra("term", sTerm)
                    .putExtra("from", "edit")
            )
            dismiss()
        }

        viewCourseResult.setOnClickListener {
            context.startActivity(
                Intent(context, SubjectResultUtil::class.java)
                    .putExtra("classId", sClassId)
                    .putExtra("courseId", sCourseId)
                    .putExtra("year", sYear)
                    .putExtra("term", sTerm)
                    .putExtra("from", "view")
            )
            dismiss()
        }

        courseResult.setOnClickListener {
            context.startActivity(
                Intent(context, CourseResultActivity::class.java)
                    .putExtra("class_id", sClassId)
                    .putExtra("session", sYear)
                    .putExtra("term", term)
            )
            dismiss()
        }

        compositeResult.setOnClickListener {
            context.startActivity(
                Intent(context, ViewClassResultWebview::class.java)
                    .putExtra("class_Id", sClassId)
                    .putExtra("session", sYear)
                    .putExtra("term", term)
            )
            dismiss()
        }

    }
}