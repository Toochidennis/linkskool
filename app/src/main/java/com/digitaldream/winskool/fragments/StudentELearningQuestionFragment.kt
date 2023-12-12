package com.digitaldream.winskool.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.StudentELearningQuizDialogFragment
import com.digitaldream.winskool.models.MultiChoiceQuestion
import com.digitaldream.winskool.models.MultipleChoiceOption
import com.digitaldream.winskool.models.QuestionItem
import com.digitaldream.winskool.models.SectionModel
import com.digitaldream.winskool.models.ShortAnswerModel
import com.digitaldream.winskool.utils.FunctionUtils.formatDate2
import com.digitaldream.winskool.utils.FunctionUtils.getDate
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StudentELearningQuestionFragment : Fragment(R.layout.fragment_student_e_learning_question) {

    private lateinit var endDateTxt: TextView
    private lateinit var titleTxt: TextView
    private lateinit var questionCountTxt: TextView
    private lateinit var durationTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var startDateTxt: TextView
    private lateinit var warningTxt: TextView
    private lateinit var startBtn: Button

    private var questionList = mutableListOf<SectionModel>()

    private var jsonData: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var title: String? = null
    private var duration: String? = null
    private var description: String? = null
    private var questionCount: String? = null
    private var quizId: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String = "") =
            StudentELearningQuestionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        initializeQuestionsList()

    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            endDateTxt = findViewById(R.id.dateTxt)
            titleTxt = findViewById(R.id.titleTxt)
            questionCountTxt = findViewById(R.id.questionCountTxt)
            durationTxt = findViewById(R.id.durationTxt)
            descriptionTxt = findViewById(R.id.descriptionTxt)
            startDateTxt = findViewById(R.id.startDateTxt)
            warningTxt = findViewById(R.id.warningTxt)
            startBtn = findViewById(R.id.startBtn)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = "Quiz"
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun initializeQuestionsList() {
        questionList = returnQuestionList()

        questionCount = totalQuestionCount().toString()

        setTextOnDateFields()
        setTextOnOtherFields()
    }

    // Return list of questions
    private fun returnQuestionList(): MutableList<SectionModel> {
        val sectionItems = mutableListOf<SectionModel>()
        try {
            if (jsonData?.isNotBlank() == true) {
                val questionData = parseJsonObject(jsonData!!)

                questionData.run {
                    if (has("questions")) {
                        val questionsArray = getJSONArray("questions")

                        for (i in 0 until questionsArray.length()) {
                            with(questionsArray.getJSONObject(i)) {
                                val questionId = getString("question_id")
                                val questionTitle = getString("question_title")
                                var questionFileName: String
                                var questionImage: String?
                                var questionOldFileName: String

                                getJSONArray("question_files").let { filesArray ->
                                    filesArray.getJSONObject(0).let { filesObject ->
                                        questionFileName = filesObject.getString("file_name")
                                        questionOldFileName = filesObject.getString("old_file_name")
                                        questionImage =
                                            filesObject.getString("file").ifEmpty { null }
                                    }
                                }

                                when (val questionType = getString("question_type")) {
                                    // Handle different question types
                                    "section" -> {
                                        val section =
                                            SectionModel(
                                                questionId,
                                                questionTitle,
                                                null,
                                                questionType
                                            )
                                        sectionItems.add(section)
                                    }

                                    "multiple_choice" -> {
                                        val optionsArray = getJSONArray("options")
                                        val optionsList = mutableListOf<MultipleChoiceOption>()

                                        for (j in 0 until optionsArray.length()) {
                                            optionsArray.getJSONObject(j).let { option ->
                                                val order = option.getString("order")
                                                val text = option.getString("text")
                                                val fileName: String
                                                val oldFileName: String
                                                val file: String?

                                                with(option.getJSONArray("option_files")) {
                                                    getJSONObject(0).let { filesObject ->
                                                        fileName =
                                                            filesObject.getString("file_name")
                                                        oldFileName =
                                                            filesObject.getString("old_file_name")
                                                        file = filesObject.getString("file")
                                                            .ifEmpty { null }
                                                    }
                                                }

                                                val optionModel =
                                                    MultipleChoiceOption(
                                                        text,
                                                        order,
                                                        fileName,
                                                        file,
                                                        oldFileName
                                                    )

                                                optionsList.add(optionModel)
                                            }
                                        }

                                        getJSONObject("correct").let { answer ->
                                            val answerOrder = answer.getString("order")
                                            val correctAnswer = answer.getString("text")

                                            val multiChoiceQuestion =
                                                MultiChoiceQuestion(
                                                    questionId,
                                                    questionTitle,
                                                    questionFileName,
                                                    questionImage,
                                                    questionOldFileName,
                                                    optionsList,
                                                    answerOrder.toInt(),
                                                    correctAnswer
                                                )

                                            val questionSection =
                                                SectionModel(
                                                    questionId, "",
                                                    QuestionItem.MultiChoice(
                                                        multiChoiceQuestion
                                                    ),
                                                    questionType
                                                )

                                            sectionItems.add(questionSection)
                                        }
                                    }

                                    else -> {
                                        getJSONObject("correct").let { answer ->
                                            val correctAnswer = answer.getString("text")

                                            val shortAnswerModel =
                                                ShortAnswerModel(
                                                    questionId,
                                                    questionTitle,
                                                    questionFileName,
                                                    questionImage,
                                                    questionOldFileName,
                                                    correctAnswer
                                                )

                                            val questionSection =
                                                SectionModel(
                                                    questionId,
                                                    "",
                                                    QuestionItem.ShortAnswer(
                                                        shortAnswerModel
                                                    ),
                                                    questionType
                                                )

                                            sectionItems.add(questionSection)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return sectionItems
    }

    // Parse the JSON data from a given JSON string
    private fun parseJsonObject(json: String): JSONObject {
        return JSONObject().apply {
            JSONObject(json).let { jsonObject ->
                put("questions", parseQuestionJson(JSONArray(jsonObject.getString("q"))))
                parseSettingsJson(JSONObject(jsonObject.getString("e")))
            }
        }
    }

    // Parse the question JSON array
    private fun parseQuestionJson(jsonArray: JSONArray): JSONArray {
        return JSONArray().apply {
            jsonArray.getJSONArray(0).let { question ->
                for (i in 0 until question.length()) {
                    JSONObject().apply {
                        question.getJSONObject(i).let {
                            put("question_id", it.getString("id"))
                            put("question_title", it.getString("content"))
                            put("question_type", it.getString("type"))
                            put(
                                "question_files", parseFilesArray(
                                    JSONArray(it.getString("question_file"))
                                )
                            )

                            if (it.getString("answer") != "null") {
                                put(
                                    "options",
                                    parseOptionsJson(JSONArray(it.getString("answer")))
                                )
                            }

                            if (it.getString("correct") != "null")
                                put("correct", JSONObject(it.getString("correct")))
                        }
                    }.let {
                        put(it)
                    }
                }
            }
        }
    }

    private fun parseFilesArray(files: JSONArray): JSONArray {
        return JSONArray().apply {
            JSONObject().apply {
                files.getJSONObject(0).let {
                    put("file_name", trimText(it.getString("file_name")))
                    put("old_file_name", trimText(it.getString("file_name")))
                    put("type", it.getString("type"))
                    put("file", it.getString("file_name"))
                }
            }.let { jsonObject ->
                put(jsonObject)
            }
        }
    }

    // Remove a specific text from the file name
    private fun trimText(text: String): String {
        return text.replace("../assets/elearning/practice/", "").ifEmpty { "" }
    }

    // Parse the options JSON array
    private fun parseOptionsJson(jsonArray: JSONArray): JSONArray {
        return JSONArray().apply {
            for (i in 0 until jsonArray.length()) {
                JSONObject().apply {
                    jsonArray.getJSONObject(i).let {
                        put("order", it.getString("order"))
                        put("text", it.getString("text"))
                        put(
                            "option_files",
                            parseFilesArray(JSONArray(it.getString("option_files")))
                        )
                    }
                }.let {
                    put(it)
                }
            }
        }
    }

    // Parse the settings JSON object
    private fun parseSettingsJson(settings: JSONObject) {
        with(settings) {
            quizId = getString("id")
            title = getString("title")
            description = getString("description")
            duration = getString("objective")
            startDate = getString("start_date")
            endDate = getString("end_date")
        }
    }

    private fun totalQuestionCount(): Int {
        return questionList.count { it.questionItem != null }
    }

    private fun setTextOnDateFields() {
        try {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            disableStartBtn()

            val startDateFromServer = formatter.parse(startDate!!)
            val endDateFromServer = formatter.parse(endDate!!)
            val currentDate = formatter.parse(getCurrentDateString())

            var startDateText = ""
            var endDateText = "Due ${formatDate2(endDate!!, "date time")}"

            if (endDateFromServer != null && startDateFromServer != null && currentDate != null) {
                if (currentDate > startDateFromServer || currentDate == startDateFromServer) {
                    if (endDateFromServer > currentDate) {
                        startDateText = "Quiz will last for $duration minutes"
                    } else if (endDateFromServer < currentDate) {
                        startDateText = "You can no longer take this quiz"
                        warningTxt.isVisible = false
                    } else if (endDateFromServer == currentDate) {
                        endDateText = "Due Today"
                        startDateText = "Quiz will last for $duration minutes"
                    }

                    if (endDateFromServer >= currentDate) {
                        enableStartBtn()
                    } else {
                        disableStartBtn()
                    }

                    updateStartDateUI(startDateText)
                    updateEndDateUI(endDateText)
                } else {
                    startDateText =
                        if (endDateFromServer < currentDate)
                            "You can no longer take this quiz"
                        else "Quiz will be available from ${
                            formatDate2(startDate!!, "date time")
                        } to ${formatDate2(endDate!!, "date time")}"


                    updateStartDateUI(startDateText)
                    updateEndDateUI(endDateText)
                    warningTxt.isVisible = false

                    disableStartBtn()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCurrentDateString(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minutes = calendar[Calendar.MINUTE]
        val seconds = calendar[Calendar.SECOND]
        return "${getDate()} $hour:$minutes:$seconds"
    }

    private fun updateStartDateUI(text: String) {
        startDateTxt.text = text
    }

    private fun updateEndDateUI(text: String) {
        endDateTxt.text = text
    }

    private fun disableStartBtn() {
        startBtn.apply {
            isEnabled = false
            setBackgroundResource(R.drawable.ripple_effect6)
            setTextColor(Color.WHITE)
        }
    }

    private fun enableStartBtn() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.pulse)

        startBtn.apply {
            isEnabled = true
            setBackgroundResource(R.drawable.ripple_effect8)
            startAnimation(animation)

            setOnClickListener {
                parentFragmentManager.commit {
                    replace(
                        R.id.learningContainer,
                        StudentELearningQuizDialogFragment(
                            quizId ?: "",
                            duration ?: "",
                            questionList
                        )
                    )
                }
            }
        }
    }


    private fun setTextOnOtherFields() {
        titleTxt.text = title
        durationTxt.text = duration ?: "0"
        questionCountTxt.text = questionCount
        descriptionTxt.text = description
    }

}