package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionAnswersAdapter
import com.digitaldream.linkskool.models.StudentResponseModel
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningQuestionAnswersFragment :
    Fragment(R.layout.fragment_admin_e_learning_question_answers) {

    private lateinit var assignedCountTxt: TextView
    private lateinit var handedCountTxt: TextView
    private lateinit var answerRecyclerView: RecyclerView

    private lateinit var answersAdapter: AdminELearningQuestionAnswersAdapter
    private var answerList = mutableListOf<StudentResponseModel>()

    private var jsonData: String? = null
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
        fun newInstance(param1: String, param2: String) =
            AdminELearningQuestionAnswersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        loadResponses()
    }

    private fun setUpViews(view: View) {
        view.apply {
            assignedCountTxt = findViewById(R.id.assignedCountTxt)
            handedCountTxt = findViewById(R.id.handedCountTxt)
            answerRecyclerView = findViewById(R.id.answerRecyclerView)
        }
    }

    private fun loadResponses() {
        jsonData?.let {
            val contentId = JSONObject(
                JSONObject(jsonData!!)
                    .getString("e")
            ).getString("id")

            getAnswers(contentId)
        }
    }

    private fun getAnswers(contentId: String) {
        val url =
            "${requireActivity().getString(R.string.base_url)}/getResponses.php?id=$contentId"

        sendRequestToServer(
            Request.Method.GET,
            url,
            requireContext(),
            null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response != "[]") {
                        parseResponse(response)
                    }
                }

                override fun onError(error: VolleyError) {

                }
            },
            false
        )

    }

    private fun parseResponse(response: String) {
        try {
            with(JSONArray(response)) {
                for (i in 0 until length()) {
                    getJSONObject(i).let {
                        val id = it.getString("response_id")
                        val examId = it.getString("exam")
                        val score = it.getString("score")
                        val studentId = it.getString("student")
                        val studentName = it.getString("student_name")
                        val term = it.getString("term")
                        val date = it.getString("date")

                        val answerModel =
                            StudentResponseModel(
                                id,
                                examId,
                                score,
                                studentId,
                                studentName,
                                term,
                                date
                            )

                        answerList.add(answerModel)
                    }
                }
            }

            setUpRecyclerView()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpRecyclerView() {
        answersAdapter = AdminELearningQuestionAnswersAdapter(
            parentFragmentManager, answerList,
            questionData = jsonData ?: ""
        )

        answerRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = answersAdapter
        }
    }

}