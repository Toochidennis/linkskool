package com.digitaldream.winskool.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.GenericAdapter2
import com.digitaldream.winskool.models.TopicModel
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.FunctionUtils.smoothScrollEditText
import com.digitaldream.winskool.utils.VolleyCallback
import org.json.JSONArray
import org.json.JSONObject


class AdminELearningSelectTopicDialogFragment(
    private var courseId: String,
    private var levelId: String,
    private var courseName: String,
    private var selectedClass: HashMap<String, String>,
    private var existingTopic: String,
    private val isTopicSelected: (topicId: String, topic: String?) -> Unit
) : DialogFragment(R.layout.fragment_admin_e_learning_select_topic) {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var backBtn: ImageButton
    private lateinit var doneBtn: Button
    private lateinit var noTopicBtn: TextView
    private lateinit var newTopicEditText: EditText
    private lateinit var newObjectiveEditText: EditText
    private lateinit var objectiveSeparator: View
    private lateinit var topicRecyclerView: RecyclerView
    private lateinit var errorTxt: TextView

    private lateinit var topicAdapter: GenericAdapter2<TopicModel>
    private val topicList = mutableListOf<TopicModel>()

    private var userId: String? = null
    private var userName: String? = null
    private var term: String? = null
    private var year: String? = null
    private var selectedTopic: String? = null
    private var selectedTopicId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", "")
        userName = sharedPreferences.getString("user", "")
        term = sharedPreferences.getString("term", "")
        year = sharedPreferences.getString("school_year", "")

        selectedTopic = existingTopic

        setUpTopicAdapter()

        fetchTopics()

        backBtn.setOnClickListener {
            dismiss()
        }

        doneBtn.setOnClickListener {
            handleDoneButton()
        }

        handleNewTopicSelection()

        handleNoTopicSelection()

        smoothScrollEditText(newObjectiveEditText)

        refreshData()
    }

    private fun setUpViews(view: View) {
        view.apply {
            swipeRefreshLayout = findViewById(R.id.swipeRefresh)
            backBtn = findViewById(R.id.backBtn)
            doneBtn = findViewById(R.id.doneBtn)
            noTopicBtn = findViewById(R.id.noTopicBtn)
            newTopicEditText = findViewById(R.id.newTopicEditText)
            newObjectiveEditText = findViewById(R.id.newObjectiveEditText)
            objectiveSeparator = findViewById(R.id.separator3)
            topicRecyclerView = findViewById(R.id.topicRecyclerview)
            errorTxt = findViewById(R.id.errorTxt)
        }

        swipeRefreshLayout.setColorSchemeResources(R.color.test_color_1)
    }

    private fun checkEditText() {
        newTopicEditText.apply {
            isSelected = true
            setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_black),
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_),
                null
            )
        }

        newObjectiveEditText.isVisible = true
        objectiveSeparator.isVisible = true
    }

    private fun unCheckEditText() {
        newTopicEditText.apply {
            setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_black),
                null, null, null
            )
            isSelected = false

            closeKeyboard(this)
        }

        newObjectiveEditText.apply {
            isVisible = false
            closeKeyboard(this)
        }
        objectiveSeparator.isVisible = false
    }

    private fun setDrawableOnTextView(textView: TextView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            null, null,
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_),
            null
        )
    }

    private fun removeDrawableOnTextView(textView: TextView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            null, null, null, null
        )
    }

    private fun setUpTopicAdapter() {
        topicAdapter = GenericAdapter2(
            topicList,
            R.layout.item_fragment_select_topic,
            bindItem = { itemView, model, _ ->
                val topicTxt: TextView = itemView.findViewById(R.id.topicTxt)
                topicTxt.text = model.topicText

                val isSelected = selectedTopic == model.topicText
                itemView.isSelected = isSelected

                if (isSelected) {
                    setDrawableOnTextView(topicTxt)
                } else {
                    removeDrawableOnTextView(topicTxt)
                }

                itemView.setOnClickListener {
                    handleTopicSelection(model.topicId, model.topicText)
                }

            }
        )

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        topicRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = topicAdapter
        }
    }

    private fun handleTopicSelection(newSelectedId: String, newSelectedTopic: String) {
        if (newSelectedTopic != selectedTopic) {
            selectedTopic = newSelectedTopic
            selectedTopicId = newSelectedId
            removeDrawableOnTextView(noTopicBtn)
            unCheckEditText()
            topicAdapter.notifyDataSetChanged()
        }
    }

    private fun handleNewTopicSelection() {
        newTopicEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                removeDrawableOnTextView(noTopicBtn)
                selectedTopic = null
                checkEditText()
                topicAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun handleNoTopicSelection() {
        noTopicBtn.setOnClickListener {
            setDrawableOnTextView(noTopicBtn)
            selectedTopic = noTopicBtn.text.toString()
            unCheckEditText()
            topicAdapter.notifyDataSetChanged()
        }
    }

    private fun fetchTopics() {
        val url = "${getString(R.string.base_url)}/getOutline.php?" +
                "course=$courseId&level=$levelId&term=$term&type=4"

        sendRequestToServer(
            Request.Method.GET,
            url,
            requireContext(),
            null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        if (response != "[]") {
                            with(JSONArray(response)) {
                                for (i in 0 until length()) {

                                    getJSONObject(i).let {
                                        val id = it.getString("id")
                                        val title = it.getString("title")

                                        if (it.getString("type") == "4") {
                                            topicList.add(TopicModel(id, title))
                                        }
                                    }
                                }
                            }

                            setUpTopicAdapter()
                            errorTxt.isVisible = false
                        } else {
                            errorTxt.isVisible = true
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        errorTxt.isVisible = true

                    }
                }

                override fun onError(error: VolleyError) {
                    errorTxt.isVisible = true
                    "Something went wrong. Swipe to refresh".let { errorTxt.text = it }
                }
            }
        )
    }

    private fun closeKeyboard(editText: EditText) {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
        editText.clearFocus()
        editText.setText("")
    }

    private fun handleDoneButton() {
        if (newTopicEditText.isSelected && newTopicEditText.text.toString().isNotBlank()) {
            if (newObjectiveEditText.text.toString().isBlank()) {
                newObjectiveEditText.error = "Please provide objectives"
            } else {
                val objectives = newObjectiveEditText.text.toString().trim()
                selectedTopic = newTopicEditText.text.toString().trim()

                postTopic(objectives)
            }
        } else if (!selectedTopic.isNullOrEmpty()) {
            isTopicSelected(selectedTopicId ?: "0", selectedTopic)
            dismiss()
        } else {
            Toast.makeText(requireContext(), "Please select a topic", Toast.LENGTH_SHORT).show()
        }
    }

    private fun prepareTopic(objectives: String): HashMap<String, String> {
        val classArray = JSONArray()

        return HashMap<String, String>().apply {
            put("title", selectedTopic!!)
            put("type", "4")
            put("description", "")
            put("topic", "")
            put("objective", objectives)
            put("files", "")

            selectedClass.forEach { (key, value) ->
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
            put("level", levelId)
            put("course", courseId)
            put("course_name", courseName)
            put("start_date", "")
            put("end_date", "")
            put("author_id", userId!!)
            put("author_name", userName!!)
            put("year", year!!)
            put("term", term!!)
        }
    }

    private fun postTopic(objectives: String) {
        val url = "${getString(R.string.base_url)}/addContent.php"
        val hashMap = prepareTopic(objectives)

        sendRequestToServer(
            Request.Method.POST,
            url,
            requireContext(),
            hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        JSONObject(response).run {
                            val details = getJSONObject("details")
                            val topicId = details.getString("id")
                            val topicText = details.getString("title")

                            isTopicSelected(topicId, topicText)

                            dismiss()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(
                        requireContext(), "Something went wrong please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun refreshData() {
        swipeRefreshLayout.setOnRefreshListener {
            topicList.clear()
            fetchTopics()
            swipeRefreshLayout.isRefreshing = false
        }
    }
}