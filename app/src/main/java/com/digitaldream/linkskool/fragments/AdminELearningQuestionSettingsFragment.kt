package com.digitaldream.linkskool.fragments

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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionSettingsAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.dialog.AdminELearningDatePickerDialog
import com.digitaldream.linkskool.dialog.AdminELearningDurationPickerDialog
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils.compareJsonObjects
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.linkskool.utils.FunctionUtils.smoothScrollEditText
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import org.json.JSONArray
import org.json.JSONObject

private const val ARG_PARAM1 = "levelId"
private const val ARG_PARAM2 = "courseId"
private const val ARG_PARAM3 = "course name"
private const val ARG_PARAM4 = "from"
private const val ARG_PARAM5 = "settings object"

class AdminELearningQuestionSettingsFragment :
    Fragment(R.layout.fragment_admin_e_learning_question_settings) {

    private lateinit var mBackBtn: ImageButton
    private lateinit var mApplyBtn: Button
    private lateinit var mQuestionTitleEditText: EditText
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSelectAllBtn: Button
    private lateinit var mEmptyClassTxt: TextView
    private lateinit var mDescriptionEditText: EditText
    private lateinit var mDateBtn: RelativeLayout
    private lateinit var mStartDateTxt: TextView
    private lateinit var mEndDateTxt: TextView
    private lateinit var mStartDateBtn: ImageButton
    private lateinit var mEndDateBtn: ImageButton
    private lateinit var mTopicTxt: TextView
    private lateinit var mDurationTxt: TextView
    private lateinit var mDateSeparator: View

    private var mClassList = mutableListOf<ClassNameTable>()
    private val selectedItems = hashMapOf<String, String>()
    private val mTagList = mutableListOf<TagModel>()

    private var levelId: String? = null
    private var courseId: String? = null
    private var courseName: String? = null
    private var from: String? = null
    private var settingsObject: String? = null
    private var mQuestionTitle: String? = null
    private var mQuestionDescription: String? = null
    private var mStartDate: String? = null
    private var mEndDate: String? = null
    private var mQuestionTopic: String? = null
    private var topicId: String? = null
    private var titleText: String? = null
    private var descriptionText: String? = null
    private var topic: String? = null
    private var id: String? = null
    private var durationMinutes: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            levelId = it.getString(ARG_PARAM1)
            courseId = it.getString(ARG_PARAM2)
            courseName = it.getString(ARG_PARAM3)
            from = it.getString(ARG_PARAM4)
            settingsObject = it.getString(ARG_PARAM5)
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
            courseName: String,
            from: String = "",
            settingsObject: String = ""
        ) = AdminELearningQuestionSettingsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, levelId)
                putString(ARG_PARAM2, courseId)
                putString(ARG_PARAM3, courseName)
                putString(ARG_PARAM4, from)
                putString(ARG_PARAM5, settingsObject)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView(view)

        onEdit()

        setUpClassAdapter()

        setDate()

        showSoftInput(requireContext(), mQuestionTitleEditText)
        smoothScrollEditText(mQuestionTitleEditText)
        smoothScrollEditText(mDescriptionEditText)

        mApplyBtn.setOnClickListener {
            verifySettings()
        }

        mTopicTxt.setOnClickListener {
            selectTopic()
        }

        mBackBtn.setOnClickListener {
            onExit()
        }

        mDurationTxt.setOnClickListener {
            setDuration()
        }
    }

    private fun setUpView(view: View) {
        view.apply {
            mBackBtn = findViewById(R.id.close_btn)
            mApplyBtn = findViewById(R.id.apply_btn)
            mQuestionTitleEditText = findViewById(R.id.questionTitle)
            mRecyclerView = findViewById(R.id.class_recyclerview)
            mSelectAllBtn = findViewById(R.id.selectAllBtn)
            mEmptyClassTxt = findViewById(R.id.emptyClassTxt)
            mDescriptionEditText = findViewById(R.id.description)
            mDateBtn = findViewById(R.id.dateBtn)
            mStartDateTxt = findViewById(R.id.startDateTxt)
            mEndDateTxt = findViewById(R.id.endDateTxt)
            mStartDateBtn = findViewById(R.id.startDateBtn)
            mEndDateBtn = findViewById(R.id.endDateBtn)
            mTopicTxt = findViewById(R.id.topicBtn)
            mDurationTxt = findViewById(R.id.durationTxt)
            mDateSeparator = findViewById(R.id.separator)
        }
    }

    private fun setUpClassAdapter() {
        try {
            val mDatabaseHelper = DatabaseHelper(requireContext())
            val dao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                mDatabaseHelper.connectionSource, ClassNameTable::class.java
            )
            mClassList = dao.queryBuilder().where().eq("level", levelId).query()
            mClassList.sortBy { it.className }

            mClassList.forEach { item ->
                mTagList.add(TagModel(item.classId, item.className))
            }

            if (selectedItems.isNotEmpty()) {
                mTagList.forEach { tagModel ->
                    if (selectedItems[tagModel.tagId] == tagModel.tagName)
                        tagModel.isSelected = true
                }
            }

            if (mTagList.isEmpty()) {
                mRecyclerView.isVisible = false
                mSelectAllBtn.isVisible = false
                mEmptyClassTxt.isVisible = true
            } else {
                AdminELearningQuestionSettingsAdapter(
                    selectedItems,
                    mTagList,
                    mSelectAllBtn
                ).let {
                    mRecyclerView.apply {
                        hasFixedSize()
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        adapter = it

                        mSelectAllBtn.isVisible = true
                        mEmptyClassTxt.isVisible = false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDate() {
        mDateBtn.setOnClickListener {
            AdminELearningDatePickerDialog(requireContext())
            { startDate, endDate ->

                mStartDate = startDate
                mEndDate = endDate

                val start = "Start ${formatDate2(startDate, "custom1")}"
                val end = "Due ${formatDate2(endDate, "custom1")}"
                mStartDateTxt.text = start
                mEndDateTxt.text = end

                showDate()
            }.apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        mStartDateBtn.setOnClickListener {
            "Date".let { mStartDateTxt.text = it }
            mStartDateBtn.isVisible = false
            mDateSeparator.isVisible = false
        }

        mEndDateBtn.setOnClickListener {
            mEndDateTxt.isVisible = false
            mEndDateBtn.isVisible = false
            mDateSeparator.isVisible = false
        }
    }

    private fun showDate() {
        mStartDateBtn.isVisible = true
        mEndDateBtn.isVisible = true
        mEndDateTxt.isVisible = true
        mDateSeparator.isVisible = true
    }

    private fun selectTopic() = if (selectedItems.isEmpty()) {
        showToast("Please select a class")
    } else {
        AdminELearningSelectTopicDialogFragment(
            courseId = courseId!!,
            levelId = levelId!!,
            courseName = courseName!!,
            selectedClass = selectedItems,
            topic ?: ""
        ) { id, topicText ->
            topicId = id
            mTopicTxt.text = topicText

        }.show(parentFragmentManager, "")
    }

    private fun setDuration() {
        AdminELearningDurationPickerDialog(requireContext()) { duration ->
            durationMinutes = duration

            when (duration) {
                "0", "1" -> "$duration minute"
                else -> "$duration minutes"
            }.let {
                mDurationTxt.text = it
            }

        }.apply {
            show()
        }.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun verifySettings() {
        val titleText = mQuestionTitleEditText.text.toString().trim()
        val descriptionText = mDescriptionEditText.text.toString().trim()

        if (titleText.isEmpty()) {
            mQuestionTitleEditText.error = "Please enter question title"
        } else if (selectedItems.size == 0) {
            showToast("Please select a class")
        } else if (descriptionText.isEmpty()) {
            mDescriptionEditText.error = "Please enter a description"
        } else if (mStartDate.isNullOrEmpty() or mEndDate.isNullOrEmpty()) {
            showToast("Please set date")
        } else if (durationMinutes.isNullOrBlank()) {
            showToast("Please set duration")
        } else {
            val json = createSettingsJsonObject()

            parentFragmentManager.commit {
                replace(
                    R.id.learning_container,
                    AdminELearningQuestionFragment.newInstance(json.toString(), "settings")
                )
            }
        }
    }

    private fun createSettingsJsonObject(): JSONObject {
        getFieldsText()

        return JSONObject().apply {
            put("id", id ?: "")
            put("title", titleText!!)
            put("description", descriptionText!!)
            put("start_date", mStartDate)
            put("end_date", mEndDate)
            put("duration", durationMinutes)
            put("level", levelId)
            put("course", courseId)
            put("course_name", courseName)
            put("topic", if (topic == "Topic" || topic == "No topic") "" else topic)
            put("topic_id", topicId ?: "0")

            JSONArray().apply {
                selectedItems.forEach { (key, value) ->
                    if (key.isNotEmpty() && value.isNotEmpty()) {
                        JSONObject().apply {
                            put("id", key)
                            put("name", value)
                        }.let {
                            put(it)
                        }
                    }
                }
            }.let {
                put("class", it)
            }
        }
    }

    private fun onEdit() {
        try {
            if (from == "edit")
                if (!settingsObject.isNullOrBlank()) {
                    settingsObject?.let { json ->
                        JSONObject(json).run {
                            id = getString("id")
                            mQuestionTitle = getString("title")
                            mQuestionDescription = getString("description")
                            mStartDate = getString("start_date")
                            mEndDate = getString("end_date")
                            durationMinutes = getString("duration")
                            mQuestionTopic = getString("topic")
                            topicId = getString("topic_id")
                            levelId = getString("level")
                            courseId = getString("course")
                            courseName = getString("course_name")
                            val classArray = getJSONArray("class")

                            for (i in 0 until classArray.length()) {
                                classArray.getJSONObject(i).let {
                                    selectedItems[it.getString("id")] =
                                        it.getString("name")
                                }
                            }
                        }
                    }

                    mQuestionTitleEditText.setText(mQuestionTitle)
                    mDescriptionEditText.setText(mQuestionDescription)
                    mTopicTxt.text = mQuestionTopic.takeIf { !it.isNullOrBlank() } ?: "Topic"

                    val start = "Start ${formatDate2(mStartDate!!, "custom1")}"
                    val end = "Due ${formatDate2(mEndDate!!, "custom1")}"
                    mStartDateTxt.text = start
                    mEndDateTxt.text = end
                    mDurationTxt.text = durationMinutes

                    showDate()
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFieldsText() {
        titleText = mQuestionTitleEditText.text.toString().trim()
        descriptionText = mDescriptionEditText.text.toString().trim()
        topic = mTopicTxt.text.toString()
    }

    private fun onExit() {
        try {
            val json1 = createSettingsJsonObject()

            if (!settingsObject.isNullOrBlank() && json1.length() != 0) {
                val json2 = JSONObject(settingsObject!!)
                val areContentSame = compareJsonObjects(json1, json2)

                if (areContentSame) {
                    exitDestination()
                } else {
                    exitWithWarning()
                }

            } else if (json1.length() != 0) {
                exitWithWarning()
            } else {
                exitDestination()
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
                exitDestination()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }.create()
    }

    private fun exitDestination() {
        if (from == "edit") {
            parentFragmentManager.commit {
                replace(
                    R.id.learning_container,
                    AdminELearningQuestionFragment.newInstance(settingsObject!!, "settings")
                )
            }
        } else {
            onBackPressed()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun onBackPressed() {
        requireActivity().finish()
    }

}