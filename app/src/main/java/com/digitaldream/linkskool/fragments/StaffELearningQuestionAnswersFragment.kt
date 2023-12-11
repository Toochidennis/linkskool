package com.digitaldream.linkskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionAnswersAdapter
import com.digitaldream.linkskool.models.StudentResponseModel
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StaffELearningQuestionAnswersFragment : Fragment() {

    private lateinit var answerRecyclerView: RecyclerView

    private lateinit var answersAdapter: AdminELearningQuestionAnswersAdapter
    private var answerList = mutableListOf<StudentResponseModel>()

    private var questionData: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questionData = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_staff_e_learning_question_answers,
            container,
            false
        )
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StaffELearningQuestionAnswersFragment().apply {
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
            answerRecyclerView = findViewById(R.id.answerRecyclerView)
        }
    }

    private fun loadResponses() {
        questionData?.let {
            val contentId = JSONObject(
                JSONObject(questionData ?: "")
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
            questionData = questionData ?: ""
        )

        answerRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = answersAdapter
        }
    }
}