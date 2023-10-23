package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionSettingsAdapter
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.dialog.AdminELearningAssignmentDateDialog
import com.digitaldream.linkskool.dialog.AdminELearningAssignmentGradeDialog
import com.digitaldream.linkskool.dialog.AdminELearningAttachmentDialog
import com.digitaldream.linkskool.models.AttachmentModel
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils.compareJsonObjects
import com.digitaldream.linkskool.utils.FunctionUtils.encodeUriOrFileToBase64
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.linkskool.utils.VolleyCallback
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"
private const val ARG_PARAM5 = "param5"

class AdminELearningCreateAssignmentFragment :
    Fragment(R.layout.fragment_admin_e_learning_create_assignment) {

    private lateinit var mBackBtn: ImageButton
    private lateinit var mAssignBtn: Button
    private lateinit var mAssignmentTitleEditText: EditText
    private lateinit var mClassRecyclerView: RecyclerView
    private lateinit var mSelectAllBtn: Button
    private lateinit var mEmptyClassTxt: TextView
    private lateinit var mDescriptionEditText: EditText
    private lateinit var mAttachmentTxt: TextView
    private lateinit var mAttachmentBtn: RelativeLayout
    private lateinit var mAttachmentRecyclerView: RecyclerView
    private lateinit var mAddAttachmentBtn: TextView
    private lateinit var mGradeBtn: RelativeLayout
    private lateinit var mGradeTxt: TextView
    private lateinit var mResetGradeBtn: ImageButton
    private lateinit var dueDateBtn: RelativeLayout
    private lateinit var dueDateTxt: TextView
    private lateinit var clearDateBtn: ImageButton
    private lateinit var mTopicTxt: TextView

    private var mClassList = mutableListOf<ClassNameTable>()
    private val selectedClassItems = hashMapOf<String, String>()
    private val mTagList = mutableListOf<TagModel>()

    private val mFileList = mutableListOf<AttachmentModel>()
    private val mDeletedFileList = mutableListOf<AttachmentModel>()
    private lateinit var mFileAdapter: GenericAdapter<AttachmentModel>

    private var mLevelId: String? = null
    private var mCourseId: String? = null
    private var dueDate: String? = null
    private var mEndDate: String? = null
    private var json: String? = null
    private var mCourseName: String? = null
    private var mFrom: String? = null
    private var year: String? = null
    private var term: String? = null
    private var userId: String? = null
    private var id: String? = null
    private var userName: String? = null

    private var topic: String? = null
    private var topicId: String? = null
    private var descriptionText: String? = null
    private var titleText: String? = null
    private var gradeText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mLevelId = it.getString(ARG_PARAM1)
            mCourseId = it.getString(ARG_PARAM2)
            json = it.getString(ARG_PARAM3)
            mCourseName = it.getString(ARG_PARAM4)
            mFrom = it.getString(ARG_PARAM5)
        }

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onExit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)
    }

    companion object {

        @JvmStatic
        fun newInstance(
            levelId: String,
            courseId: String,
            json: String,
            courseName: String,
            from: String
        ) = AdminELearningCreateAssignmentFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, levelId)
                putString(ARG_PARAM2, courseId)
                putString(ARG_PARAM3, json)
                putString(ARG_PARAM4, courseName)
                putString(ARG_PARAM5, from)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        val sharedPreferences =
            requireActivity().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        year = sharedPreferences.getString("school_year", "")
        term = sharedPreferences.getString("term", "")
        userId = sharedPreferences.getString("user_id", "")
        userName = sharedPreferences.getString("user", "")

        if (json.isNullOrBlank()) {
            setUpClassAdapter()
        } else {
            onEditAssignment()
        }

        setUpDate()

        setUpGrade()

        fileAttachment(mAttachmentBtn)
        fileAttachment(mAddAttachmentBtn)

        showSoftInput(requireContext(), mAssignmentTitleEditText)

        mAssignBtn.setOnClickListener {
            it.isEnabled = false
            assignAssignment()
        }

        mBackBtn.setOnClickListener {
            onExit()
        }

        mTopicTxt.setOnClickListener {
            selectTopic()
        }
    }

    private fun setUpViews(view: View) {
        view.apply {
            mBackBtn = findViewById(R.id.close_btn)
            mAssignBtn = findViewById(R.id.assignBtn)
            mAssignmentTitleEditText = findViewById(R.id.assignmentTitle)
            mClassRecyclerView = findViewById(R.id.class_recyclerview)
            mSelectAllBtn = findViewById(R.id.selectAllBtn)
            mEmptyClassTxt = findViewById(R.id.emptyClassTxt)
            mDescriptionEditText = findViewById(R.id.description)
            mAttachmentTxt = findViewById(R.id.attachmentTxt)
            mAttachmentBtn = findViewById(R.id.attachmentBtn)
            mAttachmentRecyclerView = findViewById(R.id.attachment_recyclerview)
            mAddAttachmentBtn = findViewById(R.id.addAttachmentButton)
            mGradeBtn = findViewById(R.id.gradeBtn)
            mGradeTxt = findViewById(R.id.gradeTxt)
            mResetGradeBtn = findViewById(R.id.resetGradingBtn)
            dueDateBtn = findViewById(R.id.dueDateBtn)
            dueDateTxt = findViewById(R.id.startDateTxt)
            clearDateBtn = findViewById(R.id.clearDateBtn)
            mTopicTxt = findViewById(R.id.topicBtn)
        }

    }

    private fun setUpClassAdapter() {
        try {
            val mDatabaseHelper = DatabaseHelper(requireContext())
            val dao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                mDatabaseHelper.connectionSource, ClassNameTable::class.java
            )
            mClassList = dao.queryBuilder().where().eq("level", mLevelId).query()
            mClassList.sortBy { it.className }

            mClassList.forEach { item ->
                mTagList.add(TagModel(item.classId, item.className))
            }

            if (selectedClassItems.isNotEmpty()) {
                mTagList.forEach { tagModel ->
                    if (selectedClassItems[tagModel.tagId] == tagModel.tagName)
                        tagModel.isSelected = true
                }
            }

            if (mTagList.isEmpty()) {
                mClassRecyclerView.isVisible = false
                mSelectAllBtn.isVisible = false
                mEmptyClassTxt.isVisible = true
            } else {
                AdminELearningQuestionSettingsAdapter(
                    selectedClassItems,
                    mTagList,
                    mSelectAllBtn
                ).let {
                    mClassRecyclerView.apply {
                        hasFixedSize()
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        adapter = it
                        isVisible = true

                        mSelectAllBtn.isVisible = true
                        mEmptyClassTxt.isVisible = false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpDate() {
        dueDateBtn.setOnClickListener {
            AdminELearningAssignmentDateDialog(requireContext())
            { date ->
                dueDate = date

                showDate()
            }.apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        clearDateBtn.setOnClickListener {
            "Due date".let { dueDateTxt.text = it }
            clearDateBtn.isVisible = false
        }

    }

    private fun showDate() {
        "Due ${formatDate2(dueDate ?: "", "date time")}".let { dueDateTxt.text = it  }
        clearDateBtn.isVisible = true
    }
    private fun setUpGrade() {
        mGradeBtn.setOnClickListener {
            AdminELearningAssignmentGradeDialog(requireContext()) { point ->

                gradeText = point
                setGradeText()

            }.apply {
                show()
                setCancelable(true)
            }.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        mResetGradeBtn.setOnClickListener {
            mResetGradeBtn.isVisible = false
            "Unmarked".let { mGradeTxt.text = it }
        }
    }

    private fun setGradeText() {
        when (gradeText) {
            "Unmarked" -> gradeText
            "1" -> "$gradeText point"
            else -> "$gradeText points"
        }.let {
            mGradeTxt.text = it
        }

        mResetGradeBtn.isVisible = true
    }

    private fun fileAttachment(button: View) {
        button.setOnClickListener {
            AdminELearningAttachmentDialog { type: String, name: String, uri: Any? ->
                mFileList.add(AttachmentModel(name, "", type, uri))
                setUpFilesAdapter()

            }.show(parentFragmentManager, "")
        }
    }

    private fun setUpFilesAdapter() {
        try {
            if (mFileList.isNotEmpty()) {
                mFileAdapter = GenericAdapter(
                    mFileList,
                    R.layout.fragment_admin_e_learning_assigment_attachment_item,
                    bindItem = { itemView, model, position ->
                        val itemTxt: TextView = itemView.findViewById(R.id.itemTxt)
                        val deleteButton: ImageButton =
                            itemView.findViewById(R.id.deleteButton)

                        itemTxt.text = model.name

                        setCompoundDrawable(itemTxt, model.type)

                        deleteAttachment(deleteButton, position)

                    }, onItemClick = { position: Int ->
                        val itemPosition = mFileList[position]

                        previewAttachment(itemPosition.type, itemPosition.uri)
                    }
                )

               setUpFileRecyclerView()

            } else {
                mAttachmentTxt.isVisible = true
                mAddAttachmentBtn.isVisible = false
                mAttachmentBtn.isClickable = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpFileRecyclerView() {
        mAttachmentRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mFileAdapter
            smoothScrollToPosition(mFileList.size - 1)

            mAttachmentTxt.isVisible = false
            mAddAttachmentBtn.isVisible = true
            mAttachmentBtn.isClickable = false
        }
    }


    private fun previewAttachment(type: String, uri: Any?) {
        val fileUri = when (uri) {
            is File -> {
                val file = File(uri.absolutePath)
                FileProvider.getUriForFile(
                    requireContext(),
                    "${requireActivity().packageName}.provider",
                    file
                )
            }

            is String -> Uri.parse(uri)

            else -> uri
        }

        when (type) {
            "image" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri as Uri?, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }

            "video" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri as Uri?, "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }

            "url" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUri.toString()))
                startActivity(intent)
            }

            "pdf", "excel", "word" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri as Uri?, "application/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }

            else -> {
                showToast("Can't open file")
            }
        }
    }

    private fun deleteAttachment(deleteButton: ImageButton, position: Int) {
        deleteButton.setOnClickListener {
            if (mFrom == "edit") {
                val deletedAttachmentModel = mFileList[position]
                deletedAttachmentModel.name = ""
                mDeletedFileList.add(deletedAttachmentModel)
            }

            mFileList.removeAt(position)
            if (mFileList.isEmpty()) {
                mAttachmentTxt.isVisible = true
                mAddAttachmentBtn.isVisible = false
                mAttachmentBtn.isClickable = true
            }
            mFileAdapter.notifyDataSetChanged()
        }
    }

    private fun setCompoundDrawable(textView: TextView, type: String) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            when (type) {
                "image" -> R.drawable.ic_image24
                "video" -> R.drawable.ic_video24
                "pdf" -> R.drawable.ic_pdf24
                "unknown" -> R.drawable.ic_unknown_document24
                "url" -> R.drawable.ic_link
                else -> R.drawable.ic_document24
            }.let {
                ContextCompat.getDrawable(requireContext(), it)
            },
            null, null, null
        )
    }

    private fun assignAssignment() {
        getFieldsText()

        if (titleText.isNullOrBlank()) {
            mAssignmentTitleEditText.error = "Please enter assignment title"
        } else if (selectedClassItems.size == 0) {
            showToast("Please select a class")
        } else if (descriptionText.isNullOrBlank()) {
            mDescriptionEditText.error = "Please enter a description"
        } else if (dueDate.isNullOrEmpty() && mEndDate.isNullOrEmpty()) {
            showToast("Please set date")
        } else {
            postAssignment()
        }
    }

    private fun createAssignmentObject(): HashMap<String, String> {
        val filesArray = JSONArray()
        val classArray = JSONArray()

        getFieldsText()

        if (mDeletedFileList.isNotEmpty()){
            mFileList.addAll(mDeletedFileList)
        }

        return HashMap<String, String>().apply {
            put("id", id ?: "")
            put("title", titleText!!)
            put("type", "3")
            put("description", descriptionText!!)
            put("topic", if (topic == "Topic" || topic == "No topic") "" else topic!!)
            put("topic_id", topicId ?: "0")
            put("objective", gradeText!!)

            mFileList.isNotEmpty().let { isTrue ->
                if (isTrue) {
                    mFileList.forEach { attachment ->
                        JSONObject().apply {
                            put("file_name", attachment.name)
                            put("old_file_name", attachment.oldName)
                            put("type", attachment.type)

                            val file = convertFileOrUriToBase64(attachment.uri)
                            put("file", file)

                        }.let {
                            filesArray.put(it)
                        }
                    }
                }
            }

            put("files", filesArray.toString())

            selectedClassItems.forEach { (key, value) ->
                if (key.isNotEmpty() and value.isNotEmpty()) {
                    JSONObject().apply {
                        put("id", key)
                        put("name", value)
                    }.let {
                        classArray.put(it)
                    }
                }
            }

            put("class", classArray.toString())
            put("level", mLevelId!!)
            put("course", mCourseId!!)
            put("course_name", mCourseName!!)
            put("start_date", dueDate ?: "")
            put("end_date", mEndDate ?: "")
            put("author_id", userId!!)
            put("author_name", userName!!)
            put("year", year!!)
            put("term", term!!)
        }
    }

    private fun convertFileOrUriToBase64(fileUri: Any?): Any? {
        return if (fileUri is String) {
            ""
        } else {
            runBlocking(Dispatchers.IO) {
                encodeUriOrFileToBase64(
                    fileUri,
                    requireContext()
                )
            }
        }
    }

    private fun getFieldsText() {
        titleText = mAssignmentTitleEditText.text.toString().trim()
        descriptionText = mDescriptionEditText.text.toString().trim()
        topic = mTopicTxt.text.toString()
        gradeText = mGradeTxt.text.toString().replace(" points", "")
    }

    private fun postAssignment() {
        val url = "${getString(R.string.base_url)}/addContent.php"
        val hashMap = createAssignmentObject()

        sendRequestToServer(
            Request.Method.POST,
            url,
            requireContext(),
            hashMap,
            object
                : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        JSONObject(response).run {
                            if (getString("status") == "success") {
                                if (mFrom != "edit") {
                                    showToast("Assignment added")
                                    finishActivity()
                                } else {
                                    finishActivity()
                                }
                            } else {
                                showToast("Failed. Please check your connection and try again")
                            }
                        }
                        mAssignBtn.isEnabled = true
                    } catch (e: Exception) {
                        mAssignBtn.isEnabled = true
                        e.printStackTrace()
                    }
                }

                override fun onError(error: VolleyError) {
                    mAssignBtn.isEnabled = true
                    showToast("Something went wrong please try again")
                }
            })
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun finishActivity() {
        GlobalScope.launch {
            delay(50L)
            onBackPressed()
        }
    }

    private fun selectTopic() = if (selectedClassItems.isEmpty()) {
        showToast("Please select a class")
    } else {
        AdminELearningSelectTopicDialogFragment(
            courseId = mCourseId!!,
            levelId = mLevelId!!,
            courseName = mCourseName!!,
            selectedClass = selectedClassItems,
            topic ?: ""
        ) { id, topicText ->
            topicId = id
            mTopicTxt.text = topicText

        }.show(parentFragmentManager, "")
    }

    private fun onEditAssignment() {
        try {
            val assignmentJsonObject = parseJsonObject(json!!)

            with(assignmentJsonObject) {
                id = getString("id")
                titleText = getString("title")
                descriptionText = getString("description")
                topic = getString("topic")
                topicId = getString("topic_id")
                gradeText = getString("objective")

                getJSONArray("files").let { jsonArray ->
                    for (i in 0 until jsonArray.length()) {
                        jsonArray.getJSONObject(i).let { fileJson ->
                            val attachmentModel = AttachmentModel(
                                fileJson.getString("file_name"),
                                fileJson.getString("old_file_name"),
                                fileJson.getString("type"),
                                fileJson.getString("file")
                            )

                            mFileList.add(attachmentModel)
                        }
                    }
                }

                getJSONArray("class").let { jsonArray ->
                    for (i in 0 until jsonArray.length()) {
                        jsonArray.getJSONObject(i).let {
                            selectedClassItems[it.getString("id")] =
                                it.getString("name")
                        }
                    }
                }

                mCourseId = getString("course")
                mCourseName = getString("course_name")
                mLevelId = getString("level")
                dueDate = getString("start_date")
                mEndDate = getString("end_date")
            }

            setUpClassAdapter()
            setUpFilesAdapter()
            setTextOnViews()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTextOnViews() {
        mAssignmentTitleEditText.setText(titleText)
        mDescriptionEditText.setText(descriptionText)
        mTopicTxt.text = topic

        if (!gradeText.isNullOrBlank()) {
            setGradeText()
        }

        if (!dueDate.isNullOrBlank() && !mEndDate.isNullOrBlank()) {
            showDate()
        }
    }


    private fun parseJsonObject(json: String): JSONObject {
        return JSONObject().apply {
            JSONObject(json).let {
                put("id", it.getString("id"))
                put("title", it.getString("title"))
                put("type", it.getString("type"))
                put("description", it.getString("description"))
                put("topic", it.getString("category"))
                put("topic_id", it.getString("parent"))
                put("objective", it.getString("objective"))
                put("files", parseFilesArray(JSONArray(it.getString("picref"))))
                put("class", parseClassArray(JSONArray(it.getString("class"))))
                put("level", it.getString("level"))
                put("course", it.getString("course_id"))
                put("course_name", it.getString("course_name"))
                put("start_date", it.getString("start_date"))
                put("end_date", it.getString("end_date"))
                put("author_id", it.getString("author_id"))
                put("author_name", it.getString("author_name"))
                put("year", it.getString("term"))
                put("term", it.getString("term"))
            }
        }
    }

    private fun parseFilesArray(files: JSONArray): JSONArray {
        return JSONArray().apply {
            val jsonObject = JSONObject()
            for (i in 0 until files.length()) {
                files.getJSONObject(i).let {
                    jsonObject.apply {
                        put("file_name", trimText(it.getString("file_name")))
                        put("old_file_name", trimText(it.getString("file_name")))
                        put("type", it.getString("type"))
                        put("file", it.getString("file_name"))
                    }

                    put(jsonObject)
                }
            }
        }
    }

    private fun trimText(text: String): String {
        return text.replace("../assets/elearning/practice/", "").ifEmpty { "" }
    }

    private fun parseClassArray(classArray: JSONArray): JSONArray {
        return JSONArray().apply {
            for (i in 0 until classArray.length()) {
                classArray.getJSONObject(i).let {
                    JSONObject().apply {
                        put("id", it.getString("id"))
                        put("name", it.getString("name"))
                    }.let { jsonObject ->
                        put(jsonObject)
                    }
                }
            }
        }
    }

    private fun onExit() {
        try {
            val json1 = JSONObject(createAssignmentObject().toMap())

            if (!json.isNullOrBlank() && json1.length() != 0) {
                val json2 = parseJsonObject(json!!)
                val areContentSame = compareJsonObjects(json1, json2)

                if (areContentSame) {
                    onBackPressed()
                } else {
                    exitWithWarning()
                }
            } else if (json1.length() != 0) {
                exitWithWarning()
            } else {
                onBackPressed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun exitWithWarning() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Are you sure to exit?")
            setMessage("Your unsaved changes will be lost")
            setPositiveButton("Yes") { _, _ ->
                onBackPressed()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }.create()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun onBackPressed() {
        requireActivity().finish()
    }

}