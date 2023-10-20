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
import com.digitaldream.linkskool.utils.FunctionUtils
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

    private var mCourseName: String? = null
    private var mCourseId: String? = null
    private var mLevelName: String? = null
    private var mLevelId: String? = null
    private var mTerm: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViews()

    }


    private fun setUpViews() {
        setContentView(R.layout.activity_staff_e_learning_course_outline)

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


        mTerm = getSharedPreferences("loginDetail", MODE_PRIVATE)
            .getString("term", "")
    }


    private fun getCourseOutline() {
        val url = "${getString(R.string.base_url)}/getOutlineList" +
                ".php?course=$mCourseId&level=$mLevelId&term=$mTerm"

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
                val courseName: TextView = itemView.findViewById(R.id.courseNameTxt)
                val levelName: TextView = itemView.findViewById(R.id.levelNameTxt)
                val teacherName: TextView = itemView.findViewById(R.id.teacherNameTxt)

                courseName.text = FunctionUtils.capitaliseFirstLetter(model.title)
                teacherName.text = FunctionUtils.capitaliseFirstLetter(model.teacherName)
            }
        ) {
            startActivity(
                Intent(this, StaffELearningActivity::class.java)
                    .putExtra("from", "view_post")
                    .putExtra("levelId", mLevelId)
                    .putExtra("courseId", mCourseId)
                    .putExtra("courseName", mCourseName)
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
                mLevelId ?: "",
                mCourseName ?: "",
                mCourseId ?: ""
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