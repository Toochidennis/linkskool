package com.digitaldream.linkskool.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningClassAdapter
import com.digitaldream.linkskool.dialog.AdminELearningCreateContentDialog
import com.digitaldream.linkskool.models.ContentModel
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.linkskool.utils.ItemTouchHelperCallback
import com.digitaldream.linkskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StaffELearningCourseWorkFragment : Fragment() {

    private lateinit var contentRecyclerView: RecyclerView
    private lateinit var addContentButton: FloatingActionButton

    private lateinit var contentAdapter: AdminELearningClassAdapter
    private var contentList = mutableListOf<ContentModel>()

    private var mLevelId: String? = null
    private var mCourseId: String? = null
    private var mCourseName: String? = null


    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
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

    }

    private fun setUpViews(view: View) {
        view.apply {
            contentRecyclerView = findViewById(R.id.contentRecyclerView)
            addContentButton = findViewById(R.id.add_btn)
        }
    }

    private fun addContent() {
        addContentButton.setOnClickListener {
            AdminELearningCreateContentDialog(
                requireContext(), mLevelId!!,
                mCourseId!!, mCourseName!!
            ).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun getCourseOutline() {
        val currentTerm = requireActivity()
            .getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
            .getString("term", "")

        val url = "${getString(R.string.base_url)}/getOutline.php?" +
                "course=$mCourseId&&level=$mLevelId&&term=$currentTerm"

        FunctionUtils.sendRequestToServer(
            Request.Method.GET,
            url,
            requireContext(),
            null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        if (response != "[]") {
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

                                            else -> {
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
                                        }
                                    }
                                }
                            }

                            sortDataList()

                            println(contentList)

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: VolleyError) {

                }
            }
        )
    }

    private fun setUpRecyclerView() {
        contentAdapter = AdminELearningClassAdapter(contentList)

        contentRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contentAdapter
        }
    }

    private fun onTouchHelper() {
        val touchCallback = ItemTouchHelperCallback(contentAdapter)
        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(contentRecyclerView)
    }

    private fun sortDataList() {
        contentList.sortBy { it.category }

        contentAdapter.notifyDataSetChanged()
    }
}