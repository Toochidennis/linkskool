package com.digitaldream.winskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.AdminELearningQuizSummaryAdapter
import com.digitaldream.winskool.models.MultiChoiceQuestion
import com.digitaldream.winskool.models.MultipleChoiceOption
import com.digitaldream.winskool.models.QuestionItem
import com.digitaldream.winskool.models.SectionModel
import com.digitaldream.winskool.models.ShortAnswerModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.util.Locale


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningQuizSummaryFragment :
    Fragment(R.layout.fragment_admin_e_learning_quiz_summary) {

    private lateinit var scoreTxt: TextView
    private lateinit var summaryRecyclerView: RecyclerView

    private lateinit var summaryAdapter: AdminELearningQuizSummaryAdapter

    private lateinit var sectionItems: MutableList<SectionModel>
    private lateinit var userResponses: MutableMap<String, String>

    private var sectionData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sectionData = it.getString(ARG_PARAM1)
            @Suppress("DEPRECATION")
            userResponses = it.getSerializable(ARG_PARAM2) as MutableMap<String, String>
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }


    companion object {

        @JvmStatic
        fun newInstance(
            sectionData: String,
            userResponses: MutableMap<String, String>
        ) = AdminELearningQuizSummaryFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, sectionData)
                putSerializable(ARG_PARAM2, userResponses as Serializable)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        initialiseDataset()

        setScore()
    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            scoreTxt = findViewById(R.id.scoreTxt)
            summaryRecyclerView = findViewById(R.id.summaryRecyclerview)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = "Quiz summary"
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener { requireActivity().finish() }
        }
    }

    private fun initialiseDataset() {
        sectionItems = returnQuestionList()

        if (::sectionItems.isInitialized && ::userResponses.isInitialized) {

            if (sectionItems.isNotEmpty()) {
                val questionItem = mutableListOf<QuestionItem?>()

                sectionItems.forEach { sectionModel ->
                    if (sectionModel.questionItem != null) {
                        questionItem.add(sectionModel.questionItem)
                    }
                }

                setUpAdapterAndRecyclerView(questionItem)
            }

        }
    }

    private fun setScore() {
        val percentage = (getScore().toDouble() / totalQuestionCount().toDouble()) * 100
        val roundedPercentage  = String.format(Locale.getDefault(), "%.2f",percentage)
        val score = "$roundedPercentage%"
        scoreTxt.text = score
    }


    private fun setUpAdapterAndRecyclerView(questionItems: MutableList<QuestionItem?>) {
        summaryAdapter = AdminELearningQuizSummaryAdapter(questionItems, userResponses)

        summaryRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = summaryAdapter
        }
    }

    private fun getScore(): Int {
        return userResponses.count { (questionId, userAnswer) ->
            val section = sectionItems.find {
                it.questionItem?.let { questionItem ->
                    when (questionItem) {
                        is QuestionItem.MultiChoice -> questionItem.question.questionId
                        is QuestionItem.ShortAnswer -> questionItem.question.questionId
                    }
                } == questionId
            }

            val correctAnswer = section?.questionItem?.let { questionItem ->
                when (questionItem) {
                    is QuestionItem.MultiChoice -> questionItem.question.correctAnswer
                    is QuestionItem.ShortAnswer -> questionItem.question.answerText
                }
            }

            userAnswer.isNotBlank() && userAnswer.equals(correctAnswer, true)
        }
    }

    private fun totalQuestionCount(): Int {
        return sectionItems.count { it.questionItem != null }
    }

    // Return list of questions
    private fun returnQuestionList(): MutableList<SectionModel> {
        val sectionItems = mutableListOf<SectionModel>()
        try {
            if (sectionData?.isNotBlank() == true) {
                val questionData = parseJsonObject(sectionData!!)

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


    // Parse the JSON data from a given JSON string
    private fun parseJsonObject(json: String): JSONObject {
        return JSONObject().apply {
            put("questions", parseQuestionJson(JSONArray(JSONObject(json).getString("q"))))
        }
    }


}