package com.digitaldream.winskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.AdminELearningActivity
import com.digitaldream.winskool.utils.FunctionUtils.formatDate2
import org.json.JSONObject
import java.util.Locale


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StaffELearningQuestionDetailsFragment : Fragment() {

    // Define UI elements
    private lateinit var dueDateTxt: TextView
    private lateinit var titleTxt: TextView
    private lateinit var durationTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var viewQuestionBtn: Button

    // Variables to store data
    private var taskType: String? = null
    private var title: String? = null
    private var description: String? = null
    private var dueDate: String? = null
    private var duration: String? = null
    private var questionData: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questionData = it.getString(ARG_PARAM1)
            taskType = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_staff_e_learning_question_details,
            container,
            false
        )
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StaffELearningQuestionDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        viewQuestions()

        parseQuestionJson()
    }

    private fun setUpViews(view: View) {
        view.apply {
            dueDateTxt = findViewById(R.id.questionDueDateTxt)
            titleTxt = findViewById(R.id.questionTitleTxt)
            durationTxt = findViewById(R.id.questionDurationTxt)
            descriptionTxt = findViewById(R.id.descriptionTxt)
            viewQuestionBtn = findViewById(R.id.viewQuestionsButton)

        }
    }

    private fun viewQuestions() {
        viewQuestionBtn.setOnClickListener {
            startActivity(
                Intent(requireContext(), AdminELearningActivity::class.java)
                    .putExtra("from", "question_test")
                    .putExtra("json", questionData)
            )
        }
    }

    private fun parseQuestionJson() {
        with(JSONObject(JSONObject(questionData!!).getString("e"))) {
            title = getString("title")
            description = getString("description")
            duration = getString("objective")
            dueDate = getString("end_date")
        }

        updateViewsText()
    }

    private fun updateViewsText() {
        if (title?.isNotBlank() == true) {
            titleTxt.text = title
        }

        if (description?.isNotBlank() == true) {
            descriptionTxt.text = description
        }

        if (duration?.isNotBlank() == true) {
            val durationString = String.format(Locale.getDefault(), "%s minutes", duration)
            durationTxt.text = durationString
        }

        if (dueDate?.isNotBlank() == true) {
            val date = formatDate2(dueDate!!, "date time")
            dueDateTxt.text = date
        }
    }

}