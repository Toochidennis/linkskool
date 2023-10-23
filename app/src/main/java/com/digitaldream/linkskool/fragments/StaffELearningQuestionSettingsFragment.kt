package com.digitaldream.linkskool.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
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
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionSettingsAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.dialog.AdminELearningDatePickerDialog
import com.digitaldream.linkskool.dialog.AdminELearningDurationPickerDialog
import com.digitaldream.linkskool.models.CourseTable
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.linkskool.utils.FunctionUtils.smoothScrollEditText
import com.j256.ormlite.dao.DaoManager
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"


class StaffELearningQuestionSettingsFragment : Fragment() {

    private lateinit var backBtn: ImageButton
    private lateinit var applyBtn: Button
    private lateinit var questionTitleEditText: EditText
    private lateinit var classRecyclerView: RecyclerView
    private lateinit var selectAllBtn: Button
    private lateinit var emptyClassTxt: TextView
    private lateinit var descriptionEditText: EditText
    private lateinit var dateBtn: RelativeLayout
    private lateinit var mStartDateTxt: TextView
    private lateinit var endDateTxt: TextView
    private lateinit var startDateBtn: ImageButton
    private lateinit var endDateBtn: ImageButton
    private lateinit var topicTxt: TextView
    private lateinit var durationTxt: TextView
    private lateinit var dateSeparator: View

    private lateinit var classAdapter: AdminELearningQuestionSettingsAdapter

    private var classList = mutableListOf<CourseTable>()
    private val selectedItems = hashMapOf<String, String>()
    private val tagClassList = mutableListOf<TagModel>()

    private var taskType: String? = null
    private var levelId: String? = null
    private var courseId: String? = null
    private var courseName: String? = null
    private var settingsObject: String? = null
    private var questionTitle: String? = null
    private var questionDescription: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var questionTopic: String? = null
    private var topicId: String? = null
    private var titleText: String? = null
    private var descriptionText: String? = null
    private var topic: String? = null
    private var settingsId: String? = null
    private var durationMinutes: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskType = it.getString(ARG_PARAM1)
        }

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onExitSettings()
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
            R.layout.fragment_staff_e_learning_question_settings,
            container,
            false
        )
    }

    companion object {

        @JvmStatic
        fun newInstance(task: String) =
            StaffELearningQuestionSettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, task)
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
            applyBtn = findViewById(R.id.apply_btn)
            questionTitleEditText = findViewById(R.id.questionTitle)
            classRecyclerView = findViewById(R.id.class_recyclerview)
            selectAllBtn = findViewById(R.id.selectAllBtn)
            emptyClassTxt = findViewById(R.id.emptyClassTxt)
            descriptionEditText = findViewById(R.id.description)
            dateBtn = findViewById(R.id.dateBtn)
            mStartDateTxt = findViewById(R.id.startDateTxt)
            endDateTxt = findViewById(R.id.endDateTxt)
            startDateBtn = findViewById(R.id.startDateBtn)
            endDateBtn = findViewById(R.id.endDateBtn)
            topicTxt = findViewById(R.id.topicBtn)
            durationTxt = findViewById(R.id.durationTxt)
            dateSeparator = findViewById(R.id.separator)
        }

        val sharedPreferences = requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)

        with(sharedPreferences) {
            levelId = getString("level", "")
            courseId = getString("courseId", "")
            courseName = getString("course_name", "")
        }
    }

    private fun init() {
        onEditSettings()

        setUpClassAdapter()

        setUpDate()

        showSoftInput(requireContext(), questionTitleEditText)
        smoothScrollEditText(questionTitleEditText)
        smoothScrollEditText(descriptionEditText)

        applyBtn.setOnClickListener {
            verifySettings()
        }

        topicTxt.setOnClickListener {
            setUpTopic()
        }

        backBtn.setOnClickListener {
            onExitSettings()
        }

        durationTxt.setOnClickListener {
            setUpDuration()
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

    private fun setUpDate() {
        dateBtn.setOnClickListener {
            AdminELearningDatePickerDialog(requireContext())
            { start, end ->

                startDate = start
                endDate = end

                "Start ${formatDate2(start, "date time")}".let { mStartDateTxt.text = it }
                "Due ${formatDate2(end, "date time")}".let { endDateTxt.text = it }

                showDate()

            }.apply {
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        startDateBtn.setOnClickListener {
            "Date".let { mStartDateTxt.text = it }
            startDateBtn.isVisible = false
            dateSeparator.isVisible = false
        }

        endDateBtn.setOnClickListener {
            endDateTxt.isVisible = false
            endDateBtn.isVisible = false
            dateSeparator.isVisible = false
        }
    }

    private fun showDate() {
        startDateBtn.isVisible = true
        endDateBtn.isVisible = true
        endDateTxt.isVisible = true
        dateSeparator.isVisible = true
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
        ) { id, topicText ->
            topicId = id
            topicTxt.text = topicText

        }.show(parentFragmentManager, "")
    }


    private fun setUpDuration() {
        AdminELearningDurationPickerDialog(requireContext()) { duration ->
            durationMinutes = duration

            when (duration) {
                "0", "1" -> "$duration minute"
                else -> "$duration minutes"
            }.let {
                durationTxt.text = it
            }

        }.apply {
            show()
        }.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun verifySettings() {
        val titleText = questionTitleEditText.text.toString().trim()
        val descriptionText = descriptionEditText.text.toString().trim()

        if (titleText.isEmpty()) {
            questionTitleEditText.error = "Please enter question title"
        } else if (selectedItems.size == 0) {
            showToast("Please select a class")
        } else if (descriptionText.isEmpty()) {
            descriptionEditText.error = "Please enter a description"
        } else if (startDate.isNullOrEmpty() or endDate.isNullOrEmpty()) {
            showToast("Please set date")
        } else if (durationMinutes.isNullOrBlank()) {
            showToast("Please set duration")
        } else {
            val json = createSettingsJsonObject()

            parentFragmentManager.commit {
                replace(
                    R.id.learning_container,
                    AdminELearningCreateQuestionFragment.newInstance(json.toString(), "settings")
                )
            }
        }
    }

    private fun createSettingsJsonObject(): JSONObject {
        getFieldsText()

        return JSONObject().apply {
            put("id", settingsId ?: "")
            put("title", titleText!!)
            put("description", descriptionText!!)
            put("start_date", startDate)
            put("end_date", endDate)
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

    private fun onEditSettings() {
        try {
            if (taskType == "edit")
                if (!settingsObject.isNullOrBlank()) {
                    settingsObject?.let { json ->
                        JSONObject(json).run {
                            settingsId = getString("id")
                            questionTitle = getString("title")
                            questionDescription = getString("description")
                            startDate = getString("start_date")
                            endDate = getString("end_date")
                            durationMinutes = getString("duration")
                            questionTopic = getString("topic")
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

                    questionTitleEditText.setText(questionTitle)
                    descriptionEditText.setText(questionDescription)
                    topicTxt.text = questionTopic.takeIf { !it.isNullOrBlank() } ?: "Topic"

                    "Start ${formatDate2(startDate ?: "", "date time")}".let {
                        mStartDateTxt.text = it
                    }

                    "Due ${formatDate2(endDate ?: "", "date time")}".let {
                        endDateTxt.text = it
                    }

                    durationTxt.text = durationMinutes

                    showDate()
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFieldsText() {
        titleText = questionTitleEditText.text.toString().trim()
        descriptionText = descriptionEditText.text.toString().trim()
        topic = topicTxt.text.toString()
    }


    private fun onExitSettings() {
        try {
            val json1 = createSettingsJsonObject()

            if (!settingsObject.isNullOrBlank() && json1.length() != 0) {
                val json2 = JSONObject(settingsObject!!)
                val areContentSame = FunctionUtils.compareJsonObjects(json1, json2)

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
        if (taskType == "edit") {
            parentFragmentManager.commit {
                replace(
                    R.id.learning_container,
                    AdminELearningCreateQuestionFragment.newInstance(
                        settingsObject ?: "",
                        "settings"
                    )
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