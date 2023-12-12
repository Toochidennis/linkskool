package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.StudentELearningStreamAdapter
import com.digitaldream.winskool.models.CommentDataModel
import com.digitaldream.winskool.models.ContentModel
import com.digitaldream.winskool.utils.FunctionUtils.formatDate2
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import org.json.JSONArray


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StudentELearningStreamFragment : Fragment() {

    private lateinit var streamRecyclerView: RecyclerView
    private lateinit var emptyTxt: TextView

    private lateinit var streamAdapter: StudentELearningStreamAdapter
    private var contentList = mutableListOf<ContentModel>()
    private var commentMap = hashMapOf<String, MutableList<CommentDataModel>>()

    private var jsonData: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_e_learning_stream, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(data: String, param2: String = "") =
            StudentELearningStreamFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, data)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        loadStreams()
    }

    private fun setUpViews(view: View) {
        view.apply {
            streamRecyclerView = findViewById(R.id.streamRecyclerView)
            emptyTxt = findViewById(R.id.emptyTxt)
        }
    }


    private fun loadStreams() {
        getContentComment()
    }

    private fun parseResponse(response: String) {
        try {
            with(JSONArray(response)) {
                for (i in 0 until length()) {
                    val contentObject = getJSONObject(i)

                    contentObject.let {
                        val id = it.getString("id")
                        val title = it.getString("title")
                        val courseId = it.getString("course_id")
                        val levelId = it.getString("level")
                        val term = it.getString("term")
                        val date = it.getString("upload_date")
                        val type = it.getString("type")
                        val category = it.getString("category")

                        when (it.getString("content_type")) {
                            "Assignment" -> {
                                val content = ContentModel(
                                    id, title,
                                    "Assignment:",
                                    courseId,
                                    levelId,
                                    "", "", date, term, type,
                                    "assignment",
                                    category
                                )

                                contentList.add(content)
                            }

                            "Material" -> {
                                val content = ContentModel(
                                    id, title,
                                    "Material:",
                                    courseId,
                                    levelId,
                                    "", "", date, term, type,
                                    "material",
                                    category
                                )

                                contentList.add(content)
                            }

                            "Quiz" -> {
                                val content = ContentModel(
                                    id, title,
                                    "Question:",
                                    courseId, levelId,
                                    "", "",
                                    date, term, type,
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

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getContentComment() {
        try {
            parseResponse(jsonData!!)
            val contentModel = contentList[0]

            val url = "${requireActivity().getString(R.string.base_url)}/getContentComment.php?" +
                    "level=${contentModel.levelId}" +
                    "&course=${contentModel.courseId}&term=${contentModel.term}"

            sendRequestToServer(Request.Method.GET, url, requireContext(), null, object
                : VolleyCallback {
                override fun onResponse(response: String) {
                    if (jsonData?.isNotBlank() == true) {
                        if (response != "[]") {
                            parseCommentResponse(response)
                            emptyTxt.isVisible = false
                        } else {
                            emptyTxt.isVisible = true
                        }
                    }
                }

                override fun onError(error: VolleyError) {
                    emptyTxt.isVisible = true
                }
            }, false)

        } catch (e: Exception) {
            e.printStackTrace()
            emptyTxt.isVisible = true
        }
    }

    private fun parseCommentResponse(response: String) {
        with(JSONArray(response)) {
            for (i in 0 until length()) {
                getJSONObject(i).let {
                    val commentId = it.getString("id")
                    val contentId = it.getString("content_id")
                    val comment = it.getString("body")
                    val userId = it.getString("author_id")
                    val userName = it.getString("author_name")
                    val date = it.getString("upload_date")

                    val formattedDate = formatDate2(date, "custom")

                    val commentModel =
                        CommentDataModel(
                            commentId, userId,
                            contentId, userName,
                            comment, formattedDate
                        )

                    updateCommentMap(commentModel)
                }
            }
        }

        filterContent()
    }

    private fun updateCommentMap(comment: CommentDataModel) {
        val newCommentList =
            mutableListOf<CommentDataModel>().apply {
                add(comment)
            }

        val previousCommentList = commentMap[comment.contentId]

        if (previousCommentList == null) {
            commentMap[comment.contentId] = newCommentList
        } else {
            previousCommentList.addAll(newCommentList)
        }

    }

    private fun filterContent() {
        val iterator = contentList.iterator()

        while (iterator.hasNext()) {
            val content = iterator.next()
            val comment = commentMap[content.id]

            if (comment == null) {
                iterator.remove()
            }
        }

        setUpContentRecyclerView()
    }

    private fun setUpContentRecyclerView() {
        streamAdapter = StudentELearningStreamAdapter(requireContext(),contentList, commentMap)

        streamRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = streamAdapter
        }
    }

}