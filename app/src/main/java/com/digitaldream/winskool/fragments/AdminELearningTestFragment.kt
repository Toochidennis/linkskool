package com.digitaldream.winskool.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.AdminELearningTestAdapter
import com.digitaldream.winskool.dialog.AdminELearningQuestionTestIntroDialogFragment
import com.digitaldream.winskool.models.MultiChoiceQuestion
import com.digitaldream.winskool.models.MultipleChoiceOption
import com.digitaldream.winskool.models.QuestionItem
import com.digitaldream.winskool.models.SectionModel
import com.digitaldream.winskool.models.ShortAnswerModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale


private const val ARG_PARAM1 = "param1"

class AdminELearningTestFragment :
    Fragment(R.layout.fragment_admin_e_learning_test),
    AdminELearningTestAdapter.UserResponse {

    private lateinit var dismissBtn: ImageButton
    private lateinit var countDownTxt: TextView
    private lateinit var questionViewPager: ViewPager2
    private lateinit var previousBtn: ImageButton
    private lateinit var nextBtn: ImageButton
    private lateinit var submitQuestionBtn: Button

    // Initialise section items
    private lateinit var quizItems: MutableList<SectionModel>
    private lateinit var countDownJob: Job
    private lateinit var quizAdapter: AdminELearningTestAdapter
    private var userResponses = mutableMapOf<String, String>()

    // Variables to store data
    private var jsonData: String? = null
    private var duration: String? = null
    private var settingsData = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            AdminELearningTestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        initialiseSectionItem()

        nextBtn.setOnClickListener {
            showNextQuestion()
        }

        showPreviousQuestion()

        submitQuestionBtn.setOnClickListener {
            submitTest()
        }

    }

    private fun setUpViews(view: View) {
        view.apply {
            dismissBtn = findViewById(R.id.dismissBtn)
            countDownTxt = findViewById(R.id.durationTxt)
            questionViewPager = findViewById(R.id.questionViewPager)
            submitQuestionBtn = findViewById(R.id.submitQuestionBtn)
            previousBtn = findViewById(R.id.prevBtn)
            nextBtn = findViewById(R.id.nextBtn)
        }
    }


    private fun initialiseSectionItem() {
        quizItems = returnQuestionList()

        disableSubmitBtn()

        if (::quizItems.isInitialized && quizItems.isNotEmpty()) {
            introDialog()
        }
    }


    private fun showQuestion() {
        quizAdapter = AdminELearningTestAdapter(quizItems, userResponses, this)
        questionViewPager.adapter = quizAdapter

        questionViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                updateNavigationButtons()
            }
        })
    }


    private fun updateNavigationButtons() =
        if (quizItems.size == 1) {
            disableNextButton()
            disablePreviousButton()
            enableSubmitBtn()
        } else if (questionViewPager.currentItem == 0) {
            enableNextButton()
            disablePreviousButton()
        } else if (questionViewPager.currentItem == quizItems.size - 1) {
            disableNextButton()
            enablePreviousButton()
            enableSubmitBtn()
        } else {
            enableNextButton()
            enablePreviousButton()
        }

    private fun showNextQuestion() {
        if (questionViewPager.currentItem < quizItems.size - 1) {
            questionViewPager.currentItem++
        }
    }

    private fun showPreviousQuestion() {
        previousBtn.setOnClickListener {
            if (questionViewPager.currentItem > 0) {
                questionViewPager.currentItem--
            }
        }
    }


    private fun disablePreviousButton() {
        // Disable the previous button
        previousBtn.isEnabled = false
    }

    private fun enablePreviousButton() {
        // Enable the previous button
        previousBtn.isEnabled = true
    }

    private fun disableNextButton() {
        // Disable the next button
        nextBtn.isEnabled = false
    }

    private fun enableNextButton() {
        // Enable the next button
        nextBtn.isEnabled = true
    }

    private fun enableSubmitBtn() {
        submitQuestionBtn.isEnabled = true
    }

    private fun disableSubmitBtn() {
        submitQuestionBtn.isEnabled = false
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
    private fun parseSettingsJson(settings: JSONObject): JSONObject {
        return JSONObject().apply {
            settings.let {
                put("title", it.getString("title"))
                put("description", it.getString("description"))
                put("duration", it.getString("objective"))
                put("start_date", it.getString("start_date"))
                put("end_date", it.getString("end_date"))
            }
        }
    }


    // Parse the JSON data from a given JSON string
    private fun parseJsonObject(json: String): JSONObject {
        return JSONObject().apply {
            JSONObject(json).let { jsonObject ->
                put("settings", parseSettingsJson(JSONObject(jsonObject.getString("e"))))
                put("questions", parseQuestionJson(JSONArray(jsonObject.getString("q"))))
            }
        }
    }


    // Return list of questions
    private fun returnQuestionList(): MutableList<SectionModel> {
        val sectionItems = mutableListOf<SectionModel>()
        try {
            if (jsonData?.isNotBlank() == true) {
                val questionData = parseJsonObject(jsonData!!)

                questionData.run {
                    if (has("questions")) {
                        settingsData = getJSONObject("settings")

                        settingsData.let {
                            duration = it.getString("duration")
                        }

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
                                                    questionId, null,
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
                                                    null,
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

    private fun introDialog() {
        AdminELearningQuestionTestIntroDialogFragment(
            jsonData = settingsData.toString()
        ) { status ->

            if (status == "start") {
                showQuestion()
                countDownTimer()

            } else {
                requireActivity().finish()
            }

        }.show(parentFragmentManager, "Intro")
    }

    private fun countDownTimer() {
        if (duration.isNullOrBlank()) {
            return
        }

        val countDownIntervalMillis = 1000L
        val quizDurationMillis = duration?.toLong()?.times(60)?.times(countDownIntervalMillis)
        val threshold = (quizDurationMillis?.times(0.10))?.toLong()

        countDownJob = CoroutineScope(Dispatchers.Default).launch {
            var remainingTimeMillis = quizDurationMillis

            if (remainingTimeMillis != null) {
                while (remainingTimeMillis > 0) {
                    val minutes = remainingTimeMillis / 1000 / 60
                    val seconds = (remainingTimeMillis / 1000) % 60
                    val timeString =
                        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                    withContext(Dispatchers.Main) {
                        countDownTxt.text = timeString

                        if (remainingTimeMillis <= threshold!!) {
                            countDownTxt.setTextColor(Color.RED)
                        }
                    }

                    delay(countDownIntervalMillis)
                    remainingTimeMillis -= countDownIntervalMillis
                }
            }

            submitTest()

            withContext(Dispatchers.Main) {
                "00:00".let { countDownTxt.text = it }
            }
        }
    }

    private fun submitTest() {
        parentFragmentManager.commit {
            replace(
                R.id.learning_container,
                AdminELearningQuizSummaryFragment.newInstance(jsonData ?: "", userResponses)
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onOptionSelected(questionId: String, selectedOption: String) {
        userResponses[questionId] = selectedOption

        GlobalScope.launch {
            delay(1000L)

            withContext(Dispatchers.Main) {
                showNextQuestion()
            }
        }
    }

    override fun setTypedAnswer(questionId: String, typedAnswer: String) {
        userResponses[questionId] = typedAnswer
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::countDownJob.isInitialized)
            countDownJob.cancel()
    }
}