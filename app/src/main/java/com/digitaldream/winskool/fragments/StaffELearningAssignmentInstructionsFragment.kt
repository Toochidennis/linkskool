package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.AdminELearningCommentAdapter
import com.digitaldream.winskool.adapters.AdminELearningFilesAdapter
import com.digitaldream.winskool.models.AttachmentModel
import com.digitaldream.winskool.models.CommentDataModel
import com.digitaldream.winskool.utils.FileViewModel
import com.digitaldream.winskool.utils.FunctionUtils
import com.digitaldream.winskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StaffELearningAssignmentInstructionsFragment : Fragment() {

    // Define UI elements
    private lateinit var dueDateTxt: TextView
    private lateinit var titleTxt: TextView
    private lateinit var gradeTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var attachmentTxt: TextView
    private lateinit var attachmentRecyclerView: RecyclerView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var addCommentTxt: TextView
    private lateinit var commentTitleTxt: TextView
    private lateinit var commentInput: TextInputLayout
    private lateinit var editTextLayout: RelativeLayout
    private lateinit var sendBtn: ImageButton

    private lateinit var commentAdapter: AdminELearningCommentAdapter
    private lateinit var filesAdapter: AdminELearningFilesAdapter

    private val commentList = mutableListOf<CommentDataModel>()
    private var fileList = mutableListOf<AttachmentModel>()

    private lateinit var fileViewModel: FileViewModel
    private var refreshJob: Job? = null

    private var assignmentData: String? = null
    private var taskType: String? = null
    private var contentTitle: String? = null
    private var dueDate: String? = null
    private var description: String? = null
    private var grade: String? = null
    private var year: String? = null
    private var term: String? = null
    private var courseId: String? = null
    private var contentId: String? = null
    private var levelId: String? = null
    private var courseName: String? = null
    private var userName: String? = null
    private var userId: String? = null
    private var contentType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            assignmentData = it.getString(ARG_PARAM1)
            taskType = it.getString(ARG_PARAM2)
        }

        fileViewModel = ViewModelProvider(this)[FileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_staff_e_learning_assignment_instructions,
            container,
            false
        )
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StaffELearningAssignmentInstructionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        parseAttachmentJson()

        loadContentComment()

    }

    private fun setUpViews(view: View) {
        view.apply {
            dueDateTxt = findViewById(R.id.assignmentDueDateTxt)
            titleTxt = findViewById(R.id.assignmentTitleTxt)
            gradeTxt = findViewById(R.id.assignmentGradeTxt)
            descriptionTxt = findViewById(R.id.assignmentDescriptionTxt)
            attachmentTxt = findViewById(R.id.attachmentTxt)
            attachmentRecyclerView = findViewById(R.id.attachmentRecyclerView)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            addCommentTxt = findViewById(R.id.addCommentTxt)
            commentTitleTxt = findViewById(R.id.commentTitleTxt)
            commentInput = findViewById(R.id.commentInputText)
            editTextLayout = findViewById(R.id.editTextLayout)
            sendBtn = findViewById(R.id.sendBtn)
        }

        val sharedPreferences = requireActivity().getSharedPreferences(
            "loginDetail",
            MODE_PRIVATE
        )

        with(sharedPreferences) {
            userId = getString("user_id", "")
            userName = getString("user", "")
            year = getString("school_year", "")
        }
    }

    private fun setUpFilesRecyclerView() {
        filesAdapter = AdminELearningFilesAdapter(parentFragmentManager, fileList, fileViewModel)

        attachmentRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = filesAdapter
        }
    }


    private fun parseAttachmentJson() {
        try {
            if (assignmentData?.isNotBlank() == true) {
                assignmentData?.let { json ->
                    JSONObject(json).let {
                        contentId = it.getString("id")
                        contentTitle = it.getString("title")
                        grade = it.getString("objective")
                        description = it.getString("description")
                        levelId = it.getString("level")
                        courseId = it.getString("course_id")
                        courseName = it.getString("course_name")
                        contentType = it.getString("type")
                        term = it.getString("term")

                        dueDate = FunctionUtils.formatDate2(it.getString("end_date"), "date time")

                        parseFilesArray(JSONArray(it.getString("picref")))
                    }
                }

                setTextOnViews()

                setUpFilesRecyclerView()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTextOnViews() {
        if (dueDate?.isNotBlank() == true) {
            "Due $dueDate".let { dueDateTxt.text = it }
        }

        if (grade?.isNotBlank() == true) {
            "$grade points".let { gradeTxt.text = it }
        }

        titleTxt.text = contentTitle
        descriptionTxt.text = description
    }

    private fun parseFilesArray(files: JSONArray) {
        for (i in 0 until files.length()) {
            files.getJSONObject(i).let {
                val fileName = trimText(it.getString("file_name"))
                val oldFileName = trimText(it.getString("file_name"))
                val type = it.getString("type")
                val uri = it.getString("file_name")

                val attachmentModel = AttachmentModel(fileName, oldFileName, type, uri)

                fileList.add(attachmentModel)
            }
        }
    }

    private fun trimText(text: String): String {
        return text.replace("../assets/elearning/practice/", "").ifEmpty { "" }
    }

    private fun loadContentComment() {
        setUpCommentRecyclerView()
        commentClick()
        sendComment()
        onWatchEditText()
        startPeriodicRefresh()
    }

    private fun getContentComment() {
        commentList.clear()

        val url =
            "${requireActivity().getString(R.string.base_url)}/getContentComment.php?" +
                    "content_id=$contentId&level=$levelId&course=$courseId&term=$term"

        FunctionUtils.sendRequestToServer(
            Request.Method.GET, url, requireContext(), null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response != "[]") {
                        parseCommentResponse(response)
                        updateViewVisibility()
                    }
                }

                override fun onError(error: VolleyError) {

                }
            }, false
        )
    }


    private fun parseCommentResponse(response: String) {
        try {
            with(JSONArray(response)) {
                for (i in 0 until length()) {
                    getJSONObject(i).let {
                        val commentId = it.getString("id")
                        val contentId = it.getString("content_id")
                        val comment = it.getString("body")
                        val userId = it.getString("author_id")
                        val userName = it.getString("author_name")
                        val date = it.getString("upload_date")

                        val commentModel =
                            CommentDataModel(
                                commentId, userId,
                                contentId, userName,
                                comment, date
                            )

                        commentList.add(commentModel)
                    }
                }
            }

            commentList.sortBy { it.date }

            commentAdapter.notifyDataSetChanged()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpCommentRecyclerView() {
        commentAdapter = AdminELearningCommentAdapter(commentList)

        commentRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }
    }

    private fun updateViewVisibility() {
        commentTitleTxt.isVisible = commentList.isNotEmpty()
    }

    private fun commentClick() {
        addCommentTxt.setOnClickListener {
            it.isVisible = false
            editTextLayout.isVisible = true

            commentInput.editText?.let { editText ->
                showSoftInput(requireContext(), editText)
            }
        }
    }

    private fun prepareComment() {
        val comment = commentInput.editText?.text.toString().trim()

        val commentDataModel =
            CommentDataModel(
                "",
                userId ?: "",
                contentId ?: "",
                userName ?: "",
                comment,
                FunctionUtils.getDate()
            )

        val newCommentList = mutableListOf<CommentDataModel>().apply {
            add(commentDataModel)
        }

        commentList.add(commentDataModel)

        editTextLayout.isVisible = false
        addCommentTxt.isVisible = true
        updateViewVisibility()

        commentInput.editText?.let { hideKeyboard(it) }

        postComment(newCommentList)

        commentAdapter.notifyDataSetChanged()
    }

    private fun sendComment() {
        sendBtn.setOnClickListener {
            prepareComment()
        }
    }

    private fun prepareCommentJson(newCommentList: MutableList<CommentDataModel>) =
        HashMap<String, String>().apply {
            put("content_id", contentId ?: "")
            put("author_id", userId ?: "")
            put("author_name", userName ?: "")

            newCommentList.forEach { commentData ->
                put("comment", commentData.comment)
            }

            put("content_title", contentTitle ?: "")
            put("level", levelId ?: "")
            put("course", courseId ?: "")
            put("course_name", courseName ?: "")
            put("term", term ?: "")
            put("year", year ?: "")
            put("content_type", contentType ?: "")
        }

    private fun postComment(newCommentList: MutableList<CommentDataModel>) {
        val commentHashMap = prepareCommentJson(newCommentList)
        val url = "${requireActivity().getString(R.string.base_url)}/addContentComment.php"

        FunctionUtils.sendRequestToServer(
            Request.Method.POST, url, requireContext(), commentHashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {

                }

                override fun onError(error: VolleyError) {

                }
            }, false
        )
    }

    private fun startPeriodicRefresh() {
        val delayMillis = 60000L // 1 minute
        refreshJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                withContext(Dispatchers.Main) {
                    getContentComment()
                }

                delay(delayMillis)
            }
        }
    }

    private fun hideKeyboard(editText: EditText) {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
        editText.clearFocus()
        editText.setText("")
    }

    private fun onWatchEditText() {
        sendBtn.isEnabled = false

        commentInput.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                sendBtn.isEnabled = s.toString().isNotBlank()

                if (sendBtn.isEnabled) {
                    sendBtn.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(), R.color.black
                        ),
                        PorterDuff.Mode.SRC_IN
                    )
                } else {
                    sendBtn.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(), R.color.test_color_7
                        ), PorterDuff.Mode.SRC_IN
                    )
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        refreshJob?.cancel()
    }
}