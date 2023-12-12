package com.digitaldream.winskool.dialog

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.AdminELearningCommentAdapter
import com.digitaldream.winskool.adapters.StudentELearningAssignmentSubmissionAdapter
import com.digitaldream.winskool.models.AttachmentModel
import com.digitaldream.winskool.models.CommentDataModel
import com.digitaldream.winskool.utils.FunctionUtils.getDate
import com.digitaldream.winskool.utils.FunctionUtils.isBased64
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.winskool.utils.StudentFileViewModel
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream


class StudentELearningAssignmentSubmissionDialogFragment :
    DialogFragment(R.layout.fragment_student_e_learning_assignment_submission) {

    // Declared UI elements
    private lateinit var backBtn: ImageButton
    private lateinit var dateTxt: TextView
    private lateinit var attachmentRecyclerView: RecyclerView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var addCommentTxt: TextView
    private lateinit var commentInput: TextInputLayout
    private lateinit var editTextLayout: RelativeLayout
    private lateinit var sendBtn: ImageButton
    private lateinit var addWorkBtn: Button
    private lateinit var handInBtn: Button

    private val commentList = mutableListOf<CommentDataModel>()
    private val fileList = mutableListOf<AttachmentModel>()

    private lateinit var commentAdapter: AdminELearningCommentAdapter
    private lateinit var fileAdapter: StudentELearningAssignmentSubmissionAdapter

    private lateinit var studentFileViewModel: StudentFileViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private var coroutineScope = CoroutineScope(Dispatchers.Main)

    // Variables to save data
    private var savedAttachmentJson: String? = null
    private var contentId: String? = null
    private var contentTitle: String? = null
    private var levelId: String? = null
    private var courseId: String? = null
    private var classId: String? = null
    private var className: String? = null
    private var year: String? = null
    private var term: String? = null
    private var userId: String? = null
    private var userName: String? = null
    private var courseName: String? = null
    private var contentType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        studentFileViewModel =
            ViewModelProvider(requireActivity())[StudentFileViewModel::class.java]
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        loadContentComment()

        loadAttachment()

        handInBtn.setOnClickListener {
            postAssignment()
        }
    }

    private fun setUpViews(view: View) {
        view.apply {
            backBtn = findViewById(R.id.backBtn)
            dateTxt = findViewById(R.id.dateTxt)
            attachmentRecyclerView = findViewById(R.id.attachmentRecyclerView)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            addCommentTxt = findViewById(R.id.addCommentTxt)
            commentInput = findViewById(R.id.commentInputText)
            editTextLayout = findViewById(R.id.editTextLayout)
            sendBtn = findViewById(R.id.sendBtn)
            addWorkBtn = findViewById(R.id.addWorkBtn)
            handInBtn = findViewById(R.id.handInBtn)
        }

        sharedPreferences = requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)

        try {
            with(sharedPreferences) {
                savedAttachmentJson = getString("attachment", "")
                levelId = getString("level", "")
                courseId = getString("courseId", "")
                classId = getString("classId", "")
                className = getString("student_class", "")
                contentId = getString("content_id", "")
                userName = getString("user", "")
                userId = getString("user_id", "")
                term = getString("term", "")
                year = getString("school_year", "")
                courseName = getString("course_name", "")
                contentType = getString("content_type","")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

        sendRequestToServer(
            Request.Method.GET, url, requireContext(), null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response != "[]") {
                        parseCommentResponse(response)
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
                getDate()
            )

        val newCommentList = mutableListOf<CommentDataModel>().apply {
            add(commentDataModel)
        }

        commentList.add(commentDataModel)

        editTextLayout.isVisible = false
        addCommentTxt.isVisible = true

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
            put("content_type",contentType?:"")
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
        coroutineScope.launch {
            while (true) {
                getContentComment()

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

    private fun loadAttachment() {
        setUpFileRecyclerView()
        uploadAttachment()
    }

    private fun uploadAttachment() {
        addWorkBtn.setOnClickListener {
            AdminELearningAttachmentDialog("student") { type: String, name: String, uri: Any? ->

                when (type) {
                    "image", "pdf", "excel", "word" -> {
                        val byteArray = convertUriToByteArray(uri)
                        Timber.tag("response").d("$byteArray")
                        fileList.add(AttachmentModel(name, "", type, byteArray, true))

                        saveFile()

                        fileAdapter.notifyDataSetChanged()
                    }

                    else -> Toast.makeText(
                        requireContext(),
                        "File type not supported",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }.show(parentFragmentManager, "")
        }
    }

    private fun setUpFileRecyclerView() {
        readSavedFile()

        fileAdapter = StudentELearningAssignmentSubmissionAdapter(
            fileList,
            studentFileViewModel,
            parentFragmentManager,
            viewLifecycleOwner
        )

        attachmentRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = fileAdapter
        }
    }

    private fun convertUriToByteArray(uri: Any?): Any? {
        try {
            val inputStream = when (uri) {
                is File -> FileInputStream(uri)
                is Uri -> requireActivity().contentResolver.openInputStream(uri)
                else -> null
            }

            inputStream.use { input ->
                val outputStream = ByteArrayOutputStream()
                val bufferedInput = BufferedInputStream(input)
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (bufferedInput.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                return outputStream.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun saveFile() {
        val fileArray = JSONArray()

        fileList.forEach { file ->
            JSONObject().apply {
                put("name", file.name)
                put("type", file.type)

                val encodedFile = Base64.encodeToString(file.uri as ByteArray, Base64.DEFAULT)
                put("uri", encodedFile)
            }.let {
                fileArray.put(it)
            }
        }

        sharedPreferences.edit().putString("attachment", fileArray.toString()).apply()
    }

    private fun readSavedFile() {
        if (savedAttachmentJson?.isNotBlank() == true)
            with(JSONArray(savedAttachmentJson)) {
                for (i in 0 until length()) {
                    getJSONObject(i).let {
                        val name = it.getString("name")
                        val type = it.getString("type")
                        val uri = it.getString("uri")

                        val isBase64 = isBased64(uri)

                        if (isBase64) {
                            val decodedByte = Base64.decode(uri, Base64.DEFAULT)

                            fileList.add(AttachmentModel(name, "", type, decodedByte))
                        }
                    }
                }
            }
    }

    private fun prepareAttachmentJson() = JSONArray().apply {
        fileList.forEach { attachment ->
            JSONObject().apply {
                put("file_name", attachment.name)
                put("type", attachment.type)
                put("file", encodeFileToBase64(attachment.uri))
            }.let {
                put(it)
            }
        }
    }

    private fun encodeFileToBase64(fileByte: Any?): Any? {
        return try {
            Base64.encode(fileByte as ByteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun prepareAssignment() = HashMap<String, String>().apply {
        val assignmentData = prepareAttachmentJson()
        put("exam_id", contentId ?: "")
        put("data", assignmentData.toString())
        put("course", courseId ?: "")
        put("course_name", courseName ?: "")
        put("user_id", userId ?: "")
        put("user_name", userName ?: "")
        put("level", levelId ?: "")
        put("classId", classId ?: "")
        put("class_name", className ?: "")
        put("type", "0")
    }


    private fun postAssignment() {
        val hashMap = prepareAssignment()
        val url = "${requireActivity().getString(R.string.base_url)}/submitAssignment.php"

        sendRequestToServer(Request.Method.POST, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {

                }

                override fun onError(error: VolleyError) {

                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

}