package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.digitaldream.linkskool.models.CourseTable
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.linkskool.utils.VolleyCallback
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


class StaffELearningCreateAssignmentFragment : Fragment() {

    private lateinit var backBtn: ImageButton
    private lateinit var assignBtn: Button
    private lateinit var assignmentTitleEditText: EditText
    private lateinit var classRecyclerView: RecyclerView
    private lateinit var selectAllBtn: Button
    private lateinit var emptyClassTxt: TextView
    private lateinit var descriptionEditText: EditText
    private lateinit var attachmentTxt: TextView
    private lateinit var attachmentBtn: RelativeLayout
    private lateinit var attachmentRecyclerView: RecyclerView
    private lateinit var addAttachmentBtn: TextView
    private lateinit var gradeBtn: RelativeLayout
    private lateinit var gradeTxt: TextView
    private lateinit var resetGradeBtn: ImageButton
    private lateinit var dueDateBtn: RelativeLayout
    private lateinit var dueDateTxt: TextView
    private lateinit var clearDateBtn: ImageButton
    private lateinit var topicTxt: TextView


    private var classList = mutableListOf<CourseTable>()
    private val selectedItems = hashMapOf<String, String>()
    private val tagClassList = mutableListOf<TagModel>()

    private val fileList = mutableListOf<AttachmentModel>()
    private val deletedFileList = mutableListOf<AttachmentModel>()
    private lateinit var attachmentAdapter: GenericAdapter<AttachmentModel>
    private lateinit var classAdapter: AdminELearningQuestionSettingsAdapter

    private var levelId: String? = null
    private var courseId: String? = null
    private var dueDate: String? = null
    private var courseName: String? = null
    private var year: String? = null
    private var term: String? = null
    private var userId: String? = null
    private var assignmentId: String? = null
    private var userName: String? = null
    private var topic: String? = null
    private var topicId: String? = null
    private var descriptionText: String? = null
    private var titleText: String? = null
    private var gradeText: String? = null
    private var assignmentData: String? = null
    private var taskType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            assignmentData = it.getString(ARG_PARAM1)
            taskType = it.getString(ARG_PARAM2)
        }

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onExit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_staff_e_learning_create_assignment,
            container,
            false
        )
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StaffELearningCreateAssignmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        init()
    }

    private fun setUpViews(view: View) {
        view.apply {
            backBtn = findViewById(R.id.close_btn)
            assignBtn = findViewById(R.id.assignBtn)
            assignmentTitleEditText = findViewById(R.id.assignmentTitle)
            classRecyclerView = findViewById(R.id.class_recyclerview)
            selectAllBtn = findViewById(R.id.selectAllBtn)
            emptyClassTxt = findViewById(R.id.emptyClassTxt)
            descriptionEditText = findViewById(R.id.description)
            attachmentTxt = findViewById(R.id.attachmentTxt)
            attachmentBtn = findViewById(R.id.attachmentBtn)
            attachmentRecyclerView = findViewById(R.id.attachment_recyclerview)
            addAttachmentBtn = findViewById(R.id.addAttachmentButton)
            gradeBtn = findViewById(R.id.gradeBtn)
            gradeTxt = findViewById(R.id.gradeTxt)
            resetGradeBtn = findViewById(R.id.resetGradingBtn)
            dueDateBtn = findViewById(R.id.dueDateBtn)
            dueDateTxt = findViewById(R.id.dueDateTxt)
            clearDateBtn = findViewById(R.id.clearDateBtn)
            topicTxt = findViewById(R.id.topicBtn)
        }

        val sharedPreferences =
            requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)

        with(sharedPreferences){
            year = getString("school_year", "")
            term = getString("term", "")
            userId = getString("user_id", "")
            userName = getString("user", "")
            courseId = getString("courseId","")
            courseName = getString("course_name","")
            levelId = getString("level","")
        }

    }

    private fun init() {
        if (assignmentData.isNullOrBlank()) {
            setUpClassAdapter()
        } else {
            onEditAssignment()
        }

        setUpDate()

        setUpGrade()

        attachFile(attachmentBtn)
        attachFile(addAttachmentBtn)

        showSoftInput(requireContext(), assignmentTitleEditText)

        assignBtn.setOnClickListener {
            it.isEnabled = false
            assignAssignment()
        }

        backBtn.setOnClickListener {
            onExit()
        }

        topicTxt.setOnClickListener {
            setUpTopic()
        }
    }


    private fun setUpClassAdapter() {
        try {
            val databaseHelper = DatabaseHelper(requireContext())
            val dao = DaoManager.createDao(
                databaseHelper.connectionSource, CourseTable::class.java
            )
            classList = dao.queryBuilder().groupBy("classId")
                .where().eq("levelId", levelId).query()

            classList.sortBy { it.className }

            classList.forEach { item ->
                tagClassList.add(TagModel(item.classId, item.className))
            }

            if (selectedItems.isNotEmpty()) {
                tagClassList.forEach { tagModel ->
                    if (selectedItems[tagModel.tagId] == tagModel.tagName)
                        tagModel.isSelected = true
                }
            }

            if (tagClassList.isEmpty()) {
                classRecyclerView.isVisible = false
                selectAllBtn.isVisible = false
                emptyClassTxt.isVisible = true
            } else {
                setUpClassRecyclerView()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpClassRecyclerView() {
        classAdapter =
            AdminELearningQuestionSettingsAdapter(
                selectedItems,
                tagClassList,
                selectAllBtn
            )

        classRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = classAdapter

            selectAllBtn.isVisible = true
            emptyClassTxt.isVisible = false
        }

    }

    private fun attachFile(button: View) {
        button.setOnClickListener {
            AdminELearningAttachmentDialog { type: String, name: String, uri: Any? ->
                fileList.add(AttachmentModel(name, "", type, uri))
                setUpFilesAdapter()
            }.show(parentFragmentManager, "")
        }
    }

    private fun setUpFilesAdapter() {
        try {
            if (fileList.isNotEmpty()) {
                attachmentAdapter = GenericAdapter(
                    fileList,
                    R.layout.fragment_admin_e_learning_assigment_attachment_item,
                    bindItem = { itemView, model, position ->
                        val itemTxt: TextView = itemView.findViewById(R.id.itemTxt)
                        val deleteButton: ImageView =
                            itemView.findViewById(R.id.deleteButton)

                        itemTxt.text = model.name

                        setCompoundDrawable(itemTxt, model.type)

                        deleteAttachment(deleteButton, position)

                    }, onItemClick = { position: Int ->
                        val itemPosition = fileList[position]

                        previewAttachment(itemPosition.type, itemPosition.uri)
                    }
                )

                setUpFileRecyclerView()

            } else {
                attachmentTxt.isVisible = true
                addAttachmentBtn.isVisible = false
                attachmentBtn.isClickable = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpFileRecyclerView() {
        attachmentRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = attachmentAdapter
            smoothScrollToPosition(fileList.size - 1)

            attachmentTxt.isVisible = false
            addAttachmentBtn.isVisible = true
            attachmentBtn.isClickable = false
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
            if (taskType == "edit") {
                val deletedAttachmentModel = fileList[position]
                deletedAttachmentModel.name = ""
                deletedFileList.add(deletedAttachmentModel)
            }

            fileList.removeAt(position)
            if (fileList.isEmpty()) {
                attachmentTxt.isVisible = true
                addAttachmentBtn.isVisible = false
                attachmentBtn.isClickable = true
            }
            attachmentAdapter.notifyDataSetChanged()
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

    private fun setUpTopic() = if (selectedItems.isEmpty()) {
        showToast("Please select a class")
    } else {
        AdminELearningSelectTopicDialogFragment(
            courseId = courseId ?: "",
            levelId = levelId ?: "",
            courseName = courseName ?: "",
            selectedClass = selectedItems,
            topic ?: ""
        ) { id, topic ->
            topicId = id
            topicTxt.text = topic
        }.show(parentFragmentManager, "")
    }

    private fun setUpGrade() {
        gradeBtn.setOnClickListener {
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

        resetGradeBtn.setOnClickListener {
            resetGradeBtn.isVisible = false
            "Unmarked".let { gradeTxt.text = it }
        }
    }

    private fun setGradeText() {
        when (gradeText) {
            "Unmarked" -> gradeText
            "1" -> "$gradeText point"
            else -> "$gradeText points"
        }.let {
            gradeTxt.text = it
        }

        resetGradeBtn.isVisible = true
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
        "Due ${formatDate2(dueDate ?: "", "date time")}".let { dueDateTxt.text = it }
        clearDateBtn.isVisible = true
    }

    private fun assignAssignment() {
        getFieldsText()

        if (titleText.isNullOrBlank()) {
            assignmentTitleEditText.error = "Please enter assignment title"
        } else if (selectedItems.size == 0) {
            showToast("Please select a class")
        } else if (descriptionText.isNullOrBlank()) {
            descriptionEditText.error = "Please enter a description"
        } else if (dueDate.isNullOrEmpty()) {
            showToast("Please set date")
        } else {
            postAssignment()
        }
    }

    private fun createAssignmentObject(): HashMap<String, String> {
        val filesArray = JSONArray()
        val classArray = JSONArray()

        getFieldsText()

        if (deletedFileList.isNotEmpty()) {
            fileList.addAll(deletedFileList)
        }

        return HashMap<String, String>().apply {
            put("id", assignmentId ?: "")
            put("title", titleText ?: "")
            put("type", "3")
            put("description", descriptionText!!)
            put("topic", if (topic == "Topic" || topic == "No topic") "" else topic ?: "")
            put("topic_id", topicId ?: "0")
            put("objective", gradeText!!)

            fileList.isNotEmpty().let { isTrue ->
                if (isTrue) {
                    fileList.forEach { attachment ->
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

            selectedItems.forEach { (key, value) ->
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
            put("level", levelId ?: "")
            put("course", courseId ?: "")
            put("course_name", courseName ?: "")
            put("start_date", dueDate ?: "")
            put("end_date", "")
            put("author_id", userId ?: "")
            put("author_name", userName ?: "")
            put("year", year ?: "")
            put("term", term ?: "")
        }
    }

    private fun convertFileOrUriToBase64(fileUri: Any?): Any? {
        return if (fileUri is String) {
            ""
        } else {
            runBlocking(Dispatchers.IO) {
                FunctionUtils.encodeUriOrFileToBase64(
                    fileUri,
                    requireContext()
                )
            }
        }
    }

    private fun getFieldsText() {
        titleText = assignmentTitleEditText.text.toString().trim()
        descriptionText = descriptionEditText.text.toString().trim()
        topic = topicTxt.text.toString()
        gradeText = gradeTxt.text.toString().replace(" points", "")
    }

    private fun postAssignment() {
        val url = "${getString(R.string.base_url)}/addContent.php"
        val hashMap = createAssignmentObject()

        FunctionUtils.sendRequestToServer(
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
                                if (taskType != "edit") {
                                    showToast("Assignment added")
                                    finishActivity()
                                } else {
                                    finishActivity()
                                }
                            } else {
                                showToast("Failed. Please check your connection and try again")
                            }
                        }
                        assignBtn.isEnabled = true
                    } catch (e: Exception) {
                        assignBtn.isEnabled = true
                        e.printStackTrace()
                    }
                }

                override fun onError(error: VolleyError) {
                    assignBtn.isEnabled = true
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


    private fun onEditAssignment() {
        try {
            val assignmentJsonObject = parseJsonObject(assignmentData ?: "")

            with(assignmentJsonObject) {
                assignmentId = getString("id")
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

                            fileList.add(attachmentModel)
                        }
                    }
                }

                getJSONArray("class").let { jsonArray ->
                    for (i in 0 until jsonArray.length()) {
                        jsonArray.getJSONObject(i).let {
                            selectedItems[it.getString("id")] =
                                it.getString("name")
                        }
                    }
                }

                courseId = getString("course")
                courseName = getString("course_name")
                levelId = getString("level")
                dueDate = getString("start_date")
            }

            setUpClassAdapter()
            setUpFilesAdapter()
            setTextOnViews()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTextOnViews() {
        assignmentTitleEditText.setText(titleText)
        descriptionEditText.setText(descriptionText)
        topicTxt.text = topic

        if (!gradeText.isNullOrBlank()) {
            setGradeText()
        }

        if (!dueDate.isNullOrBlank()) {
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

            if (!assignmentData.isNullOrBlank() && json1.length() != 0) {
                val json2 = parseJsonObject(assignmentData!!)
                val areContentSame = FunctionUtils.compareJsonObjects(json1, json2)

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