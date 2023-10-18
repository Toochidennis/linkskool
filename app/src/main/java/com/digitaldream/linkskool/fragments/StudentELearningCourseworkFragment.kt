package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.StudentELearningCourseWorkAdapter
import com.digitaldream.linkskool.models.ContentModel
import org.json.JSONArray


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StudentELearningCourseworkFragment :
    Fragment(R.layout.fragment_student_e_learning_course_work) {

    private lateinit var contentRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var emptyTxt: TextView

    private lateinit var classWorkAdapter: StudentELearningCourseWorkAdapter
    private var contentList = mutableListOf<ContentModel>()

    private var jsonData: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
            //term = it.getString(ARG_PARAM2)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(data: String, param2: String = "") =
            StudentELearningCourseworkFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, data)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        loadCourseWork()
    }

    private fun setUpViews(view: View) {
        view.apply {
            contentRecyclerView = findViewById(R.id.classWorkRecyclerView)
            swipeRefreshLayout = findViewById(R.id.swipeRefresh)
            emptyTxt = findViewById(R.id.emptyTxt)
        }
    }

    private fun loadCourseWork() {
        if (jsonData?.isNotBlank() == true) {
            if (jsonData != "[]") {
                parseResponse(jsonData!!)
                emptyTxt.isVisible = false
            } else {
                emptyTxt.isVisible = true
            }
        }
    }


    private fun parseResponse(response: String) {
        try {
            with(JSONArray(response)) {
                for (i in 0 until length()) {
                    val contentObject = getJSONObject(i)

                    contentObject.let {
                        val id = it.getString("id")
                        val title = it.getString("title")
                        val description = it.getString("body")
                        val courseId = it.getString("course_id")
                        val levelId = it.getString("level")
                        val authorId = it.getString("author_id")
                        val authorName = it.getString("author_name")
                        val term = it.getString("term")
                        val date = it.getString("upload_date")
                        val type = it.getString("type")
                        val category = it.getString("category")

                        when (it.getString("content_type")) {
                            "Topic" -> {
                                val content = ContentModel(
                                    id, title,
                                    description,
                                    courseId,
                                    levelId,
                                    authorId, authorName, date, term, type,
                                    "topic",
                                    title
                                )

                                contentList.add(content)
                            }

                            "Assignment" -> {
                                val content = ContentModel(
                                    id, title,
                                    description,
                                    courseId,
                                    levelId,
                                    authorId, authorName, date, term, type,
                                    "assignment",
                                    category
                                )

                                contentList.add(content)

                            }

                            "Material" -> {
                                val content = ContentModel(
                                    id, title,
                                    description,
                                    courseId,
                                    levelId,
                                    authorId, authorName, date, term, type,
                                    "material",
                                    category
                                )

                                contentList.add(content)
                            }

                            "Quiz" -> {
                                val content = ContentModel(
                                    id, title,
                                    description,
                                    courseId,
                                    levelId,
                                    authorId, authorName, date, term, type,
                                    "question",
                                    category
                                )

                                contentList.add(content)
                            }

                            else -> null
                        }
                    }
                }

                sortDataList()
            }
            setUpRecyclerView()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpRecyclerView() {
        classWorkAdapter = StudentELearningCourseWorkAdapter(contentList)

        contentRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = classWorkAdapter
        }
    }

    private fun sortDataList() {
        contentList.sortBy { it.category }
    }

}