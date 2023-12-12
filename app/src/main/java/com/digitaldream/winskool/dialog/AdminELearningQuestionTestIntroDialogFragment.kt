package com.digitaldream.winskool.dialog

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.utils.FunctionUtils.formatDate2
import org.json.JSONObject
import java.util.Locale

class AdminELearningQuestionTestIntroDialogFragment(
    private val jsonData: String,
    private val onStart: (status: String) -> Unit
) : DialogFragment(R.layout.fragment_admin_e_learning_question_test_intro) {

    // Define UI elements
    private lateinit var dismissBtn: ImageButton
    private lateinit var titleTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var dateTxt: TextView
    private lateinit var durationTxt: TextView
    private lateinit var startBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
        isCancelable = false
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)

        pulsateButton()

        parseJson()

        dismissBtn.setOnClickListener {
            onStart("exit")
            dismiss()
        }
    }

    private fun setUpViews(view: View) {
        view.apply {
            dismissBtn = findViewById(R.id.dismissBtn)
            titleTxt = findViewById(R.id.titleTxt)
            descriptionTxt = findViewById(R.id.descriptionTxt)
            dateTxt = findViewById(R.id.dateTxt)
            durationTxt = findViewById(R.id.durationTxt)
            startBtn = findViewById(R.id.startQuizButton)
        }

    }


    private fun pulsateButton() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.pulse)

        startBtn.apply {
            startAnimation(animation)

            setOnClickListener {
                onStart("start")
                dismiss()
            }
        }
    }

    // Parse JSON data from question settings
    private fun parseJson() {
        try {
            JSONObject(jsonData).run {
                val questionTitle = getString("title")
                val questionDescription = getString("description")
                val startDate = formatDate2(getString("start_date"), "custom1")
                val endDate = formatDate2(getString("end_date"), "custom1")
                val durationMinutes = getString("duration")

                titleTxt.text = questionTitle
                descriptionTxt.text = questionDescription

                val date = String.format(
                    Locale.getDefault(), "%s %s %s %s",
                    "Starts", startDate,
                    "and ends", endDate
                )

                dateTxt.text = date

                if (durationMinutes.isNotBlank()) {
                    val durationString = String.format(
                        Locale.getDefault(),
                        "Duration: %s minutes",
                        durationMinutes
                    )

                    durationTxt.text = durationString
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}