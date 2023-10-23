package com.digitaldream.linkskool.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionSettingsAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.CourseTable
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.linkskool.utils.VolleyCallback
import com.j256.ormlite.dao.DaoManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StaffELearningCreateTopicFragment : Fragment() {

    private lateinit var backBtn: ImageButton
    private lateinit var topicEditText: EditText
    private lateinit var classRecyclerView: RecyclerView
    private lateinit var selectAllBtn: Button
    private lateinit var emptyClassTxt: TextView
    private lateinit var objectivesEditText: EditText
    private lateinit var createTopicBtn: Button

    private lateinit var classAdapter: AdminELearningQuestionSettingsAdapter

    private var classList = mutableListOf<CourseTable>()
    private val selectedClassItems = hashMapOf<String, String>()
    private var existingClassItems = hashMapOf<String, String>()
    private val tagClassList = mutableListOf<TagModel>()

    private var courseId: String? = null
    private var levelId: String? = null
    private var courseName: String? = null
    private var existingTopic: String? = null
    private var existingObjectives: String? = null
    private var year: String? = null
    private var term: String? = null
    private var userId: String? = null
    private var userName: String? = null
    private var id: String? = null
    private var topic: String? = null
    private var objectives: String? = null
    private var topicData: String? = null
    private var taskType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            topicData = it.getString(ARG_PARAM1)
            taskType = it.getString(ARG_PARAM2)
        }

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onExitTopic()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff_e_learning_create_topic, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(data: String, task: String) =
            StaffELearningCreateTopicFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, data)
                    putString(ARG_PARAM2, task)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView(view)
        init()
    }

    private fun setUpView(view: View) {
        view.apply {
            backBtn = findViewById(R.id.backBtn)
            topicEditText = findViewById(R.id.topicEditText)
            classRecyclerView = findViewById(R.id.classRecyclerView)
            selectAllBtn = findViewById(R.id.selectAllBtn)
            emptyClassTxt = findViewById(R.id.emptyClassTxt)
            objectivesEditText = findViewById(R.id.objectivesEditText)
            createTopicBtn = findViewById(R.id.createTopicButton)
        }

        val sharedPreferences =
            requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)

        with(sharedPreferences) {
            year = getString("school_year", "")
            term = getString("term", "")
            userId = getString("user_id", "")
            userName = getString("user", "")
            courseId = getString("courseId", "")
            courseName = getString("course_name", "")
            levelId = getString("level", "")
        }
    }

    private fun init() {
        if (taskType == "edit" && !topicData.isNullOrBlank()) {
            parseJsonObject(topicData ?: "")
        }

        showSoftInput(requireContext(), topicEditText)

        setUpClassAdapter()

        createTopicBtn.setOnClickListener {
            verifyTopic()
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
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

            if (selectedClassItems.isNotEmpty()) {
                tagClassList.forEach { tagModel ->
                    if (selectedClassItems[tagModel.tagId] == tagModel.tagName)
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
                selectedClassItems,
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

    private fun verifyTopic() {
        topic = topicEditText.text.toString().trim()
        objectives = objectivesEditText.text.toString().trim()

        if (topic.isNullOrBlank()) {
            topicEditText.error = "Please provide a topic"
        } else if (objectives.isNullOrBlank()) {
            objectivesEditText.error = "Please provide objectives"
        } else if (selectedClassItems.size == 0) {
            Toast.makeText(requireContext(), "Please select a class", Toast.LENGTH_SHORT).show()
        } else {
            postTopic()
        }
    }

    private fun prepareTopic(): HashMap<String, String> {
        val classArray = JSONArray()

        return HashMap<String, String>().apply {
            put("id", id ?: "")
            put("title", topic?:"")
            put("type", "4")
            put("description", "")
            put("topic", "")
            put("objective", objectives?:"")
            put("files", "")

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
            put("level", levelId?:"")
            put("course", courseId?:"")
            put("course_name", courseName?:"")
            put("start_date", "")
            put("end_date", "")
            put("author_id", userId?:"")
            put("author_name", userName?:"")
            put("year", year?:"")
            put("term", term?:"")
        }
    }

    private fun postTopic() {
        val url = "${getString(R.string.base_url)}/addContent.php"
        val hashMap = prepareTopic()

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
                                if (taskType != "edit") {
                                    showToast("Topic created")
                                    finishActivity()
                                } else {
                                    finishActivity()
                                }
                            } else {
                                showToast("Failed. Please check your connection and try again")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: VolleyError) {
                    showToast("Something went wrong please try again")
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun finishActivity() {
        GlobalScope.launch {
            delay(50L)
            onBackPressed()
        }
    }

    private fun parseJsonObject(json: String) {
        try {
            JSONObject(json).let {
                id = it.getString("id")
                topic = it.getString("title")
                objectives = it.getString("objective")
                levelId = it.getString("level")
                courseId = it.getString("course_id")
                courseName = it.getString("course_name")
                parseClassArray(JSONArray(it.getString("class")))
                userId = it.getString("author_id")
                userName = it.getString("author_name")
                term = it.getString("term")
            }

            existingTopic = topic
            existingObjectives = objectives

            topicEditText.setText(topic)
            objectivesEditText.setText(objectives)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseClassArray(classArray: JSONArray) {
        for (i in 0 until classArray.length()) {
            classArray.getJSONObject(i).let {
                selectedClassItems[it.getString("id")] = it.getString("name")
            }
        }
        existingClassItems = selectedClassItems
    }

    private fun onExitTopic() {
        topic = topicEditText.text.toString().trim()
        objectives = objectivesEditText.text.toString().trim()

        val shouldExit = when {
            topic != existingTopic && !existingTopic.isNullOrBlank() -> true
            objectives != existingObjectives && !existingObjectives.isNullOrBlank() -> true
            selectedClassItems != existingClassItems && existingClassItems.isNotEmpty() -> true
            else -> false
        }

        if (shouldExit) {
            exitWithWarning()
        } else {
            onBackPressed()
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