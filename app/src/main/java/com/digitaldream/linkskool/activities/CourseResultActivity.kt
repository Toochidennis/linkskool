package com.digitaldream.linkskool.activities

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.CourseResultAdapter
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.dialog.TermResultDialog
import com.digitaldream.linkskool.models.ChartModel
import com.digitaldream.linkskool.models.CourseResultModel
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.achartengine.GraphicalView
import org.json.JSONObject
import java.util.*

class CourseResultActivity : AppCompatActivity(R.layout.activity_course_result),
    OnItemClickListener {


    private lateinit var mCourseView: NestedScrollView
    private lateinit var mCourseErrorView: LinearLayout
    private lateinit var mAverageScore: TextView
    private lateinit var mAverageGraph: LinearLayout
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: CourseResultAdapter
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button

    private val mCourseList = mutableListOf<CourseResultModel>()
    private val mGraphList = arrayListOf<ChartModel>()
    private var mGraphicalView: GraphicalView? = null
    private var mClassId: String? = null
    private var mCourseId: String? = null
    private var mYear: String? = null
    private var mTerm: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        mCourseView = findViewById(R.id.course_view)
        mCourseErrorView = findViewById(R.id.course_error_view)
        mAverageScore = findViewById(R.id.average_score)
        mAverageGraph = findViewById(R.id.chart)
        mRecyclerView = findViewById(R.id.course_recycler)
        mErrorView = findViewById(R.id.error_view)
        mRefreshBtn = findViewById(R.id.refresh_btn)

        mClassId = intent.getStringExtra("class_id")
        mCourseId = intent.getStringExtra("courseId")
        mYear = intent.getStringExtra("session")
        mTerm = intent.getStringExtra("term")

        val previousYear = mYear!!.toInt() - 1
        val session = String.format(Locale.getDefault(), "%d/%s", previousYear, mYear)

        toolbar.apply {
            title = "Course Result $session"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }

        mRecyclerView.run {
            hasFixedSize()
            layoutManager = LinearLayoutManager(this@CourseResultActivity)
            isAnimating
        }

        if (mGraphicalView == null) {
            mGraphicalView = FunctionUtils.plotLineChart(
                mGraphList,
                this,
                "Average",
                "Month/Year"
            )
            mAverageGraph.addView(mGraphicalView)
        } else {
            mGraphicalView!!.repaint()
        }

        getCourseResults()

        refresh()
    }

    private fun refresh() {
        mRefreshBtn.setOnClickListener {
            getCourseResults()
        }
    }

    private fun getCourseResults() {

        val url = "${getString(R.string.base_url)}/classCourse" +
                ".php?class=$mClassId&&year=$mYear&&term=$mTerm"
        val hashMap = hashMapOf<String, String>()

        sendRequestToServer(Request.Method.GET, url, this, hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        mCourseList.clear()
                        if (response != "null") {
                            JSONObject(response).also {
                                for (i in it.keys()) {
                                    val jsonObject = it.getJSONObject(i)
                                    val courseName = jsonObject.getString("course_name")
                                    val courseId = jsonObject.getString("id")
                                    val classId = jsonObject.getString("class")

                                    mCourseList.add(
                                        CourseResultModel(
                                            courseName,
                                            courseId,
                                            classId
                                        )
                                    )
                                    mCourseList.sortBy { v -> v.courseName }

                                }

                                mAdapter =
                                    CourseResultAdapter(mCourseList, this@CourseResultActivity)
                                mRecyclerView.adapter = mAdapter

                                mRecyclerView.isVisible = true
                                mCourseView.isVisible = true
                                mErrorView.isVisible = false
                                mCourseErrorView.isVisible = false
                            }
                        } else {
                            mCourseErrorView.isVisible = true
                            mRecyclerView.isVisible = false
                        }

                    } catch (sE: Exception) {
                        sE.printStackTrace()
                    }

                }

                override fun onError(error: VolleyError) {
                    mCourseView.isVisible = false
                    mErrorView.isVisible = true
                }
            })
    }

    override fun onItemClick(position: Int) {
        val model = mCourseList[position]

        TermResultDialog(
            this, mClassId!!,
            model.courseId, mYear!!,
            mTerm!!, "course"
        )
            .apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
    }
}