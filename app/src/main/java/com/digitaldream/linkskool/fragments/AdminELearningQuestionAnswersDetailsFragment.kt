package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuizSummaryAdapter
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.MultipleChoiceOption
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.ShortAnswerModel
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningQuestionAnswersDetailsFragment : DialogFragment() {

    private lateinit var detailsRecyclerView: RecyclerView

    private lateinit var summaryAdapter: AdminELearningQuizSummaryAdapter
    private var questionItems = mutableListOf<QuestionItem?>()
    private var userResponse = hashMapOf<String, String>()

    private var menuHost: MenuHost? = null
    private var questionData: String? = null
    private var answerId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        arguments?.let {
            questionData = it.getString(ARG_PARAM1)
            answerId = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_admin_e_learning_question_answers_details,
            container,
            false
        )
    }

    companion object {

        @JvmStatic
        fun newInstance(question: String, responseId: String) =
            AdminELearningQuestionAnswersDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, question)
                    putString(ARG_PARAM2, responseId)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        setUpMenu()

        getAnswerDetails()
    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            detailsRecyclerView = findViewById(R.id.detailsRecyclerView)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
            menuHost = requireActivity()

            actionBar?.apply {
                title = ""
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener {  dismiss() }
        }
    }


    private fun getAnswerDetails() {
        val url =
            "${requireActivity().getString(R.string.base_url)}/getResponseDetails.php?id=$answerId"

        sendRequestToServer(
            Request.Method.GET, url, requireContext(), null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response != "{}") {
                        parseAnswerResponse(response)
                    }
                }

                override fun onError(error: VolleyError) {

                }
            }, false
        )
    }

    private fun parseAnswerResponse(response: String) {
        try {
            JSONObject(response).run {
                val responseArray = getString("response")

                with(JSONArray(responseArray)) {
                    for (i in 0 until length()) {
                        getJSONObject(i).let {
                            userResponse[it.getString("id")] = it.getString("answer")
                        }
                    }
                }
            }

            parseQuestionData()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // Parse the JSON data from a given JSON string
    private fun parseQuestionData() {
        questionData?.let {
            parseQuestionJson(JSONArray(JSONObject(it).getString("q")))
        }
    }

    // Parse the question JSON array
    private fun parseQuestionJson(questionArray: JSONArray) {
        with(questionArray.getJSONArray(0)) {
            for (i in 0 until length()) {
                getJSONObject(i).let { question ->
                    val questionId = question.getString("id")
                    val questionTitle = question.getString("content")
                    var questionFileName: String
                    var questionFile: String?

                    JSONArray(question.getString("question_file"))
                        .getJSONObject(0)
                        .let {
                            questionFileName = trimText(it.getString("file_name"))
                            questionFile = it.getString("file_name").ifEmpty { null }
                        }

                    when (question.getString("type")) {
                        "multiple_choice" -> {
                            val optionsList = mutableListOf<MultipleChoiceOption>()

                            JSONArray(question.getString("answer")).run {
                                for (j in 0 until length()) {
                                    getJSONObject(j).let { option ->
                                        val order = option.getString("order")
                                        val text = option.getString("text")
                                        val fileName: String
                                        val optionFile: String?

                                        JSONArray(option.getString("option_files"))
                                            .getJSONObject(0)
                                            .let {
                                                fileName = trimText(it.getString("file_name"))
                                                optionFile =
                                                    it.getString("file_name").ifEmpty { null }
                                            }

                                        val optionModel =
                                            MultipleChoiceOption(
                                                text,
                                                order,
                                                fileName,
                                                optionFile,
                                                "oldFileName"
                                            )

                                        optionsList.add(optionModel)
                                    }
                                }
                            }

                            JSONObject(question.getString("correct")).let {
                                val answerOrder = it.getString("order")
                                val correctAnswer = it.getString("text")

                                val multiChoiceQuestion =
                                    MultiChoiceQuestion(
                                        questionId,
                                        questionTitle,
                                        questionFileName,
                                        questionFile,
                                        "",
                                        optionsList,
                                        answerOrder.toInt(),
                                        correctAnswer
                                    )

                                questionItems.add(QuestionItem.MultiChoice(multiChoiceQuestion))
                            }
                        }

                        "short_answer" -> {
                            val correctAnswer =
                                JSONObject(question.getString("correct")).getString("text")

                            val shortAnswerModel =
                                ShortAnswerModel(
                                    questionId,
                                    questionTitle,
                                    questionFileName,
                                    questionFile,
                                    "",
                                    correctAnswer
                                )

                            questionItems.add(QuestionItem.ShortAnswer(shortAnswerModel))
                        }

                        else -> null
                    }
                }
            }
        }

        setUpRecyclerView()
    }

    private fun trimText(text: String): String {
        return text.replace("../assets/elearning/practice/", "").ifEmpty { "" }
    }

    private fun setUpRecyclerView() {
        summaryAdapter = AdminELearningQuizSummaryAdapter(questionItems, userResponse)

        detailsRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = summaryAdapter
        }

    }


    private fun setUpMenu() {
        menuHost?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        })
    }
}