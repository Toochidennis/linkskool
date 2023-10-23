package com.digitaldream.linkskool.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.StaffELearningCourseWorkAdapter
import com.digitaldream.linkskool.dialog.StaffELearningCreateContentDialogFragment
import com.digitaldream.linkskool.models.ContentModel
import com.digitaldream.linkskool.utils.ItemTouchHelperCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StaffELearningCourseWorkFragment : Fragment() {

    private lateinit var contentRecyclerView: RecyclerView
    private lateinit var addContentButton: FloatingActionButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var contentAdapter: StaffELearningCourseWorkAdapter
    private var contentList = mutableListOf<ContentModel>()

    private var mLevelId: String? = null
    private var mCourseId: String? = null
    private var mCourseName: String? = null

    private var responseFromServer: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            responseFromServer = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff_e_learning_course_work, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StaffELearningCourseWorkFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        parseResponse()

        addContent()

        refresh()
    }

    private fun setUpViews(view: View) {
        view.apply {
            contentRecyclerView = findViewById(R.id.contentRecyclerView)
            addContentButton = findViewById(R.id.add_btn)
            swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        }

        val sharedPreferences = requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)

        with(sharedPreferences) {
            mLevelId = getString("level", "")
            mCourseId = getString("courseId", "")
            mCourseName = getString("course_name", "")
        }
    }

    private fun addContent() {
        addContentButton.setOnClickListener {
            StaffELearningCreateContentDialogFragment()
                .show(parentFragmentManager, "Create Content")
        }
    }

    private fun parseResponse() {
        responseFromServer?.let { response ->
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
                                    description, courseId,
                                    levelId, authorId,
                                    authorName, date,
                                    term, type,
                                    "material",
                                    category
                                )

                                contentList.add(content)
                            }

                            "Quiz" -> {
                                val content = ContentModel(
                                    id, title,
                                    description, courseId,
                                    levelId, authorId,
                                    authorName, date,
                                    term, type,
                                    "question",
                                    category
                                )

                                contentList.add(content)
                            }

                            else -> null
                        }
                    }
                }
            }

            setUpRecyclerView()
        }
    }

    private fun setUpRecyclerView() {
        sortDataList()

        contentAdapter = StaffELearningCourseWorkAdapter(contentList)

        contentRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contentAdapter
        }

        onTouchHelper()
    }

    private fun onTouchHelper() {
        val touchCallback = ItemTouchHelperCallback(contentAdapter)
        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(contentRecyclerView)
    }

    private fun sortDataList() {
        contentList.sortBy { it.category }
    }

    private fun refresh() {
        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.test_color_1)
            setOnRefreshListener {
                parseResponse()
                isRefreshing = false
            }
        }
    }
}