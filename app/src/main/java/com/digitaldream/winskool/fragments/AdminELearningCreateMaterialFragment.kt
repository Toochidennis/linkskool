package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.AdminELearningQuestionSettingsAdapter
import com.digitaldream.winskool.adapters.GenericAdapter
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.dialog.AdminELearningAttachmentDialog
import com.digitaldream.winskool.models.AttachmentModel
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.TagModel
import com.digitaldream.winskool.utils.FunctionUtils.compareJsonObjects
import com.digitaldream.winskool.utils.FunctionUtils.encodeUriOrFileToBase64
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.winskool.utils.VolleyCallback
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


class AdminELearningCreateMaterialFragment :
    Fragment(R.layout.fragment_admin_e_learning_create_material) {

    private lateinit var mBackBtn: ImageButton
    private lateinit var mPostBtn: Button
    private lateinit var mMaterialTitleEditText: EditText
    private lateinit var mClassRecyclerView: RecyclerView
    private lateinit var mSelectAllBtn: Button
    private lateinit var mEmptyClassTxt: TextView
    private lateinit var mDescriptionEditText: EditText
    private lateinit var mAttachmentTxt: TextView
    private lateinit var mAttachmentBtn: RelativeLayout
    private lateinit var mAttachmentRecyclerView: RecyclerView
    private lateinit var mAddAttachmentBtn: TextView
    private lateinit var mTopicTxt: TextView

    private var mClassList = mutableListOf<ClassNameTable>()
    private val selectedClassItems = hashMapOf<String, String>()
    private val mTagList = mutableListOf<TagModel>()

    private val mFileList = mutableListOf<AttachmentModel>()
    private val mDeletedFileList = mutableListOf<AttachmentModel>()
    private lateinit var mFileAdapter: GenericAdapter<AttachmentModel>

    private var mLevelId: String? = null
    private var mCourseId: String? = null
    private var mCourseName: String? = null
    private var mFrom: String? = null
    private var json: String? = null
    private var year: String? = null
    private var term: String? = null
    private var userId: String? = null
    private var userName: String? = null
    private var topicId: String? = null
    private var titleText: String? = null
    private var descriptionText: String? = null
    private var topic: String? = null
    private var id: String? = null

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
        ) = AdminELearningCreateMaterialFragment().apply {
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
            onEditMaterial()
        }

        fileAttachment(mAttachmentBtn)
        fileAttachment(mAddAttachmentBtn)

        showSoftInput(requireContext(), mMaterialTitleEditText)

        mPostBtn.setOnClickListener {
            mPostBtn.isEnabled = false
            verifyMaterial()
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
            mBackBtn = findViewById(R.id.backBtn)
            mPostBtn = findViewById(R.id.postBtn)
            mMaterialTitleEditText = findViewById(R.id.materialTitle)
            mClassRecyclerView = findViewById(R.id.classRecyclerView)
            mSelectAllBtn = findViewById(R.id.selectAllBtn)
            mEmptyClassTxt = findViewById(R.id.emptyClassTxt)
            mDescriptionEditText = findViewById(R.id.descriptionEditText)
            mAttachmentTxt = findViewById(R.id.attachmentTxt)
            mAttachmentBtn = findViewById(R.id.attachmentBtn)
            mAttachmentRecyclerView = findViewById(R.id.attachmentRecyclerView)
            mAddAttachmentBtn = findViewById(R.id.addAttachmentButton)
            mTopicTxt = findViewById(R.id.topicTxt)
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
                    R.layout.item_e_learning_assignment_attachment,
                    bindItem = { itemView, model, position ->
                        val itemTxt: TextView = itemView.findViewById(R.id.itemTxt)
                        val deleteButton: ImageView =
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

    private fun deleteAttachment(deleteButton: ImageView, position: Int) {
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

    private fun verifyMaterial() {
        val titleText = mMaterialTitleEditText.text.toString().trim()
        val descriptionText = mDescriptionEditText.text.toString().trim()

        if (titleText.isEmpty()) {
            mMaterialTitleEditText.error = "Please enter material title"
        } else if (selectedClassItems.size == 0) {
            showToast("Please select a class")
        } else if (descriptionText.isEmpty()) {
            mDescriptionEditText.error = "Please enter a description"
        } else {
            postMaterial()
        }
    }

    private fun createMaterialObject(): HashMap<String, String> {
        val filesArray = JSONArray()
        val classArray = JSONArray()

        getFieldsText()

        if (mDeletedFileList.isNotEmpty()){
            mFileList.addAll(mDeletedFileList)
        }

        return HashMap<String, String>().apply {
            put("id", id ?: "")
            put("title", titleText!!)
            put("type", "1")
            put("description", descriptionText!!)
            put("topic", if (topic == "Topic" || topic == "No topic") "" else topic!!)
            put("topic_id", topicId ?: "")
            put("objective", "")

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
            put("start_date", "")
            put("end_date", "")
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
        titleText = mMaterialTitleEditText.text.toString().trim()
        descriptionText = mDescriptionEditText.text.toString().trim()
        topic = mTopicTxt.text.toString()
    }

    private fun postMaterial() {
        val url = "${getString(R.string.base_url)}/addContent.php"
        val hashMap = createMaterialObject()

        sendRequestToServer(Request.Method.POST, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        JSONObject(response).run {
                            if (getString("status") == "success") {
                                if (mFrom != "edit") {
                                    showToast("Material added")
                                    finishActivity()
                                } else {
                                    finishActivity()
                                }
                            } else {
                                showToast("Failed. Please check your connection and try again")
                            }
                        }
                        mPostBtn.isEnabled = true
                    } catch (e: Exception) {
                        mPostBtn.isEnabled = true
                        e.printStackTrace()
                    }

                }

                override fun onError(error: VolleyError) {
                    mPostBtn.isEnabled = true
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

    private fun onEditMaterial() {
        try {
            val assignmentJsonObject = parseJsonObject(json!!)
            with(assignmentJsonObject) {
                id = getString("id")
                titleText = getString("title")
                descriptionText = getString("description")
                topic = getString("topic")
                topicId = getString("topic_id")

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

            }

            setUpClassAdapter()
            setUpFilesAdapter()
            setTextOnViews()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTextOnViews() {
        mMaterialTitleEditText.setText(titleText)
        mDescriptionEditText.setText(descriptionText)
        mTopicTxt.text = topic
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
                put("start_date", "")
                put("end_date", "")
                put("author_id", it.getString("author_id"))
                put("author_name", it.getString("author_name"))
                put("year", it.getString("term"))
                put("term", it.getString("term"))
            }
        }
    }

    private fun parseFilesArray(files: JSONArray): JSONArray {
        val jsonArray = JSONArray()

        for (i in 0 until files.length()) {
            JSONObject().apply {
                files.getJSONObject(i).let {
                    val fileName = it.getString("file_name")
                    put("file_name", trimText(fileName))
                    put("old_file_name", trimText(fileName))
                    put("type", it.getString("type"))
                    put("file", fileName)
                }
            }.let {
                jsonArray.put(it)
            }
        }

        return jsonArray
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


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun onExit() {
        try {
            val json1 = JSONObject(createMaterialObject().toMap())

            if (!json.isNullOrEmpty() && json1.length() != 0) {
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

    private fun onBackPressed() {
        requireActivity().finish()
    }

}