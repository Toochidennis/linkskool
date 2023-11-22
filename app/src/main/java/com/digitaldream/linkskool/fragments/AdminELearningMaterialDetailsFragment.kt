package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.AdminELearningActivity
import com.digitaldream.linkskool.adapters.AdminELearningCommentAdapter
import com.digitaldream.linkskool.adapters.AdminELearningFilesAdapter
import com.digitaldream.linkskool.models.AttachmentModel
import com.digitaldream.linkskool.models.CommentDataModel
import com.digitaldream.linkskool.utils.FileViewModel
import com.digitaldream.linkskool.utils.FunctionUtils.getDate
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningMaterialDetailsFragment :
    Fragment(R.layout.fragment_admin_e_learning_material_details) {


    private lateinit var titleTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var attachmentTxt: TextView
    private lateinit var attachmentRecyclerView: RecyclerView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentGuide: View
    private lateinit var commentTitleTxt: TextView
    private lateinit var commentEditText: EditText
    private lateinit var sendCommentBtn: ImageButton

    private lateinit var commentAdapter: AdminELearningCommentAdapter
    private lateinit var filesAdapter: AdminELearningFilesAdapter
    private val commentList = mutableListOf<CommentDataModel>()
    private var fileList = mutableListOf<AttachmentModel>()

    private lateinit var fileViewModel: FileViewModel
    private lateinit var menuHost: MenuHost
    private  var refreshJob: Job? = null

    private var jsonData: String? = null
    private var taskType: String? = null
    private var contentTitle: String? = null
    private var contentId: String? = null
    private var levelId: String? = null
    private var courseId: String? = null
    private var term: String? = null
    private var year: String? = null
    private var courseName: String? = null
    private var userName: String? = null
    private var userId: String? = null
    private var contentType: String? = null
    private var description: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
            taskType = it.getString(ARG_PARAM2)
        }

        fileViewModel = ViewModelProvider(this)[FileViewModel::class.java]

    }


    companion object {

        @JvmStatic
        fun newInstance(data: String, task: String) =
            AdminELearningMaterialDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, data)
                    putString(ARG_PARAM2, task)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        setUpMenu()

        parseAttachmentJson()

        loadComment()

    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            titleTxt = findViewById(R.id.titleTxt)
            descriptionTxt = findViewById(R.id.descriptionTxt)
            attachmentTxt = findViewById(R.id.attachmentTxt)
            attachmentRecyclerView = findViewById(R.id.attachmentRecyclerView)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            commentGuide = findViewById(R.id.commentGuide)
            commentTitleTxt = findViewById(R.id.commentTitleTxt)
            commentEditText = findViewById(R.id.commentEditText)
            sendCommentBtn = findViewById(R.id.sendMessageBtn)

            menuHost = requireActivity()
            (requireContext() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireContext() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = "Material"
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }
        }

        val sharedPreferences = requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)

        with(sharedPreferences) {
            userId = getString("user_id", "")
            userName = getString("user", "")
            year = getString("school_year","")
        }
    }

    private fun parseAttachmentJson() {
        try {
            if (jsonData?.isNotBlank() == true) {
                JSONObject(jsonData!!).let {
                    contentId = it.getString("id")
                    contentTitle = it.getString("title")
                    description = it.getString("description")
                    courseId = it.getString("course_id")
                    levelId = it.getString("level")
                    courseName = it.getString("course_name")
                    term = it.getString("term")
                    contentType = it.getString("type")
                    parseFilesArray(JSONArray(it.getString("picref")))
                }

                setTextOnViews()

                setUpFilesRecyclerView()

                attachmentTxt.isVisible = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setTextOnViews() {
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

    private fun setUpFilesRecyclerView() {
        filesAdapter = AdminELearningFilesAdapter(parentFragmentManager, fileList, fileViewModel)

        attachmentRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = filesAdapter
        }

    }

    private fun loadComment() {
        setUpCommentRecyclerView()
        sendComment()
        onWatchEditText()
        startPeriodicRefresh()
    }

    private fun getContentComment() {
        commentList.clear()

        val url =
            "${requireActivity().getString(R.string.base_url)}/getContentComment.php?" +
                    "content_id=$contentId&level=$levelId&course=$courseId&term=$term"
        Timber.tag("response").d(url)

        sendRequestToServer(
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

    private fun updateViewVisibility() {
        commentTitleTxt.isVisible = commentList.isNotEmpty()
        commentGuide.isVisible = commentList.isNotEmpty()
    }

    private fun setUpCommentRecyclerView() {
        commentAdapter = AdminELearningCommentAdapter(commentList)

        commentRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }

    }


    private fun onWatchEditText() {
        sendCommentBtn.isEnabled = false

        commentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                sendCommentBtn.isEnabled = s.toString().isNotBlank()

                if (sendCommentBtn.isEnabled) {
                    sendCommentBtn.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(), R.color.black
                        ),
                        PorterDuff.Mode.SRC_IN
                    )
                } else {
                    sendCommentBtn.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(), R.color.test_color_7
                        ), PorterDuff.Mode.SRC_IN
                    )
                }
            }
        })
    }

    private fun prepareComment() {
        val comment = commentEditText.text.toString().trim()

        val commentDataModel =
            CommentDataModel(
                "",
                userId ?: "",
                contentId ?: "",
                userName ?: "",
                comment,
                getDate()
            )

        val newCommentList = mutableListOf<CommentDataModel>().apply {
            add(commentDataModel)
        }

        commentList.add(commentDataModel)

        updateViewVisibility()

        hideKeyboard(commentEditText)

        commentAdapter.notifyDataSetChanged()

        postComment(newCommentList)
    }

    private fun sendComment() {
        sendCommentBtn.setOnClickListener {
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

        sendRequestToServer(
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
        refreshJob = CoroutineScope(Dispatchers.Default).launch {
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

    private fun setUpMenu() {
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_e_learning_details, menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.refresh -> {

                        true
                    }

                    R.id.edit -> {
                        startActivity(
                            Intent(requireContext(), AdminELearningActivity::class.java)
                                .putExtra("from", "material")
                                .putExtra("task", "edit")
                                .putExtra("json", jsonData)
                        )

                        true
                    }

                    R.id.delete -> {
                        true
                    }

                    else -> {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        false
                    }

                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        refreshJob?.cancel()
    }

}