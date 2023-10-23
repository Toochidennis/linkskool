package com.digitaldream.linkskool.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.dialog.StaffELearningCreateCourseOutlineDialogFragment
import com.digitaldream.linkskool.models.CourseOutlineModel
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray

class StaffElearningCourseOutlineActivity : AppCompatActivity() {

    private lateinit var mAddCourseOutlineBtn: FloatingActionButton
    private lateinit var outlineRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var emptyTxt: TextView

    private lateinit var outlineAdapter: GenericAdapter<CourseOutlineModel>
    private var outlineList = mutableListOf<CourseOutlineModel>()

    private var courseName: String? = null
    private var courseId: String? = null
    private var levelId: String? = null
    private var term: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_e_learning_course_outline)

        setUpViews()

        getCourseOutline()

        createCourseOutline()

        refresh()
    }


    private fun setUpViews() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        outlineRecyclerView = findViewById(R.id.outlineRecyclerView)
        mAddCourseOutlineBtn = findViewById(R.id.addCourseLineButton)
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        emptyTxt = findViewById(R.id.emptyTxt)

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "Course outline"
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val sharedPreferences = getSharedPreferences("loginDetail", MODE_PRIVATE)

        with(sharedPreferences) {
            term = getString("term", "")
            courseId = getString("courseId", "")
            courseName = getString("course_name", "")
            levelId = getString("level", "")
        }
    }


    private fun getCourseOutline() {
        val url = "${getString(R.string.base_url)}/getOutlineList" +
                ".php?course=$courseId&level=$levelId&term=$term"

        sendRequestToServer(Request.Method.GET, url, this, null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response != "[]") {
                        parseResponseJson(response)

                        hideEmptyTxt()
                    } else {
                        showEmptyTxt("Outline not created yet. Use the button below to start")
                    }
                }

                override fun onError(error: VolleyError) {
                    showEmptyTxt("Something went wrong, please try again")
                }
            })
    }

    private fun showEmptyTxt(message: String) {
        emptyTxt.isVisible = true
        emptyTxt.text = message
    }

    private fun hideEmptyTxt() {
        emptyTxt.isVisible = false
    }

    private fun parseResponseJson(response: String) {
        try {
            outlineList.clear()

            with(JSONArray(response)) {
                for (i in 0 until length()) {
                    getJSONObject(i).let {
                        val id = it.getString("id")
                        val title = it.getString("title")
                        val description = it.getString("body")
                        val courseId = it.getString("course_id")
                        val levelId = it.getString("level")
                        val teacherName = it.getString("author_name")
                        val term = it.getString("term")

                        val outlineModel = CourseOutlineModel(
                            id,
                            title,
                            description,
                            courseId,
                            levelId,
                            teacherName,
                            term
                        )

                        outlineList.add(outlineModel)
                    }
                }
            }

            setUpOutlineAdapter()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpOutlineAdapter() {
        outlineAdapter = GenericAdapter(
            outlineList,
            R.layout.item_course_outline_layout,
            bindItem = { itemView, model, _ ->
                val outlineTitle: TextView = itemView.findViewById(R.id.courseNameTxt)
                val levelName: TextView = itemView.findViewById(R.id.levelNameTxt)
                val teacherName: TextView = itemView.findViewById(R.id.teacherNameTxt)

                outlineTitle.text = capitaliseFirstLetter(model.title)
                teacherName.text = capitaliseFirstLetter(model.teacherName)
            }
        ) {
            val itemPosition = outlineList[it]

            startActivity(
                Intent(this, StaffELearningActivity::class.java)
                    .putExtra("from", "content_dashboard")
                    .putExtra("title", itemPosition.title)
            )
        }

        setUpRecyclerView()
    }


    private fun setUpRecyclerView() {
        outlineRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(this@StaffElearningCourseOutlineActivity)
            adapter = outlineAdapter
        }
    }

    private fun createCourseOutline() {
        mAddCourseOutlineBtn.setOnClickListener {
            StaffELearningCreateCourseOutlineDialogFragment(
                levelId ?: "",
                courseName ?: "",
                courseId ?: ""
            ) {
                getCourseOutline()
            }.show(supportFragmentManager, "")
        }

    }

    private fun refresh() {
        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.test_color_1)
            setOnRefreshListener {
                getCourseOutline()
                isRefreshing = false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            true
        } else {
            false
        }
    }
}