package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningAssignmentStudentWorkAdapter
import com.digitaldream.linkskool.models.StudentResponseModel
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StaffELearningAssignmentStudentWorkFragment : Fragment() {

    private lateinit var studentWorkRecyclerView: RecyclerView
    private lateinit var studentWorkAdapter: AdminELearningAssignmentStudentWorkAdapter
    private var studentResponseList = mutableListOf<StudentResponseModel>()

    private var assignmentData: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            assignmentData = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_staff_e_learning_assignment_answers,
            container,
            false
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StaffELearningAssignmentStudentWorkFragment().apply {
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
            studentWorkRecyclerView = findViewById(R.id.studentWorkRecyclerView)
        }

    }

    private fun loadResponses() {
        try {
            assignmentData?.let {
                val contentId = JSONObject(assignmentData ?: "").getString("id")
                getResponses(contentId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getResponses(contentId: String) {
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

                        studentResponseList.add(answerModel)
                    }
                }
            }

            setUpRecyclerView()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpRecyclerView() {
        studentWorkAdapter = AdminELearningAssignmentStudentWorkAdapter(studentResponseList)

        studentWorkRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentWorkAdapter
        }
    }
}