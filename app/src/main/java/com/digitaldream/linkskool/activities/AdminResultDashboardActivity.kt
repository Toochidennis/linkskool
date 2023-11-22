package com.digitaldream.linkskool.activities

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminResultDashboardAdapter
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.dialog.AdminClassesDialog
import com.digitaldream.linkskool.dialog.AdminResultStudentNamesDialog
import com.digitaldream.linkskool.dialog.TermResultDialog
import com.digitaldream.linkskool.interfaces.ResultListener
import com.digitaldream.linkskool.models.AdminResultDashboardModel
import com.digitaldream.linkskool.models.ChartModel
import com.digitaldream.linkskool.utils.FunctionUtils.plotLineChart
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import org.achartengine.GraphicalView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class AdminResultDashboardActivity : AppCompatActivity(R.layout.activity_admin_result_dashboard),
    OnItemClickListener {

    private lateinit var mBackBtn: ImageView
    private lateinit var mTitle: TextView
    private lateinit var mClassBtn: Button
    private lateinit var mRootView: NestedScrollView
    private lateinit var mTermErrorView: LinearLayout
    private lateinit var mAverageScore: TextView
    private lateinit var mAverageGraph: LinearLayout
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: SectionedRecyclerViewAdapter
    private lateinit var mStudentResult: CardView
    private lateinit var mCourseRegistration: CardView
    private lateinit var mAttendance: CardView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button

    private val mTermList = mutableListOf<AdminResultDashboardModel>()
    private val mGraphList = arrayListOf<ChartModel>()
    private var mGraphicalView: GraphicalView? = null
    private var mClassId: String? = null
    private var mClassName: String? = null
    private var mLevelId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBackBtn = findViewById(R.id.back_btn)
        mTitle = findViewById(R.id.toolbar_text)
        mClassBtn = findViewById(R.id.class_name)
        mRootView = findViewById(R.id.result_view)
        mTermErrorView = findViewById(R.id.term_error_view)
        mAverageScore = findViewById(R.id.average_score)
        mAverageGraph = findViewById(R.id.chart)
        mRecyclerView = findViewById(R.id.term_recycler)
        mStudentResult = findViewById(R.id.student_result)
        mCourseRegistration = findViewById(R.id.course_registration)
        mAttendance = findViewById(R.id.attendance)
        mErrorView = findViewById(R.id.error_view)
        mRefreshBtn = findViewById(R.id.refresh_btn)

        mClassId = intent.getStringExtra("classId")
        mClassName = intent.getStringExtra("class_name")
        mLevelId = intent.getStringExtra("levelId")

        mBackBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        "Result".also { mTitle.text = it }
        mClassBtn.text = mClassName

        mAdapter = SectionedRecyclerViewAdapter()
        mRecyclerView.run {
            hasFixedSize()
            layoutManager = LinearLayoutManager(this@AdminResultDashboardActivity)
            isAnimating
        }


        onCardClick()

        changeClass()

        getTerms(mClassId!!)

        if (mGraphicalView == null) {
            mGraphicalView = plotLineChart(
                mGraphList,
                this,
                "Average",
                "Month/Year"
            )
            mAverageGraph.addView(mGraphicalView)
        } else {
            mGraphicalView!!.repaint()
        }

        refresh()

    }

    private fun onCardClick() {

        mAttendance.setOnClickListener {
            startActivity(
                Intent(this, AdminAttendanceActivity::class.java)
                    .putExtra("levelId", mLevelId)
                    .putExtra("classId", mClassId)
                    .putExtra("className", mClassName)
                    .putExtra("from", "result")
            )
        }

        mCourseRegistration.setOnClickListener {
            startActivity(
                Intent(this, RegYearList::class.java)
                    .putExtra("levelId", mLevelId)
                    .putExtra("classId", mClassId)
            )
        }

        mStudentResult.setOnClickListener {
            AdminResultStudentNamesDialog(this, mClassId!!, "",
                null).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

    }

    private fun setClassName() {
        mClassBtn.text = mClassName
    }

    private fun changeClass() {
        mClassBtn.setOnClickListener {
            AdminClassesDialog(this, "result","changeLevel",
                object : ResultListener {
                    override fun sendClassName(sName: String) {
                        mClassName = sName
                        setClassName()
                    }

                    override fun sendLevelId(sLevelId: String) {
                        mLevelId = sLevelId
                    }

                    override fun sendClassId(sClassId: String) {
                        mClassId = sClassId
                        getTerms(sClassId)
                    }
                }).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

    }

    private fun getTerms(sClassId: String) {
        val url = "${getString(R.string.base_url)}/jsonTerms.php?class=$sClassId"
        val hashMap = HashMap<String, String>()

        sendRequestToServer(Request.Method.GET, url, this, hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        mAdapter.removeAllSections()
                        mTermList.clear()

                        JSONArray(response).also {
                            if (it.get(0) is JSONObject) {
                                val jsonObject = it.getJSONObject(0)
                                val key = jsonObject.keys()
                                while (key.hasNext()) {
                                    val year = key.next()
                                    if (year == "0000") continue
                                    val sessionObject = jsonObject.getJSONObject(year)
                                    val termObject = sessionObject.getJSONObject("terms")

                                    val previousYear = year.toInt() - 1
                                    val session = String.format(
                                        Locale.getDefault(), "%d/%s",
                                        previousYear, year
                                    )


                                    for (i in termObject.keys()) {
                                        mTermList.add(
                                            AdminResultDashboardModel(
                                                termObject.getString(i),
                                                year
                                            )
                                        )
                                    }

                                    mAdapter.addSection(
                                        AdminResultDashboardAdapter(
                                            mTermList,
                                            "$session Session",
                                            this@AdminResultDashboardActivity
                                        )
                                    )

                                }

                                mRecyclerView.adapter = mAdapter
                                mRecyclerView.isVisible = true
                                mRootView.isVisible = true
                                mErrorView.isVisible = false
                                mTermErrorView.isVisible = false

                            } else {
                                mTermErrorView.isVisible = true
                                mRecyclerView.isVisible = false
                                mErrorView.isVisible = false
                            }

                        }

                    } catch (sE: Exception) {
                        sE.printStackTrace()
                    }

                }

                override fun onError(error: VolleyError) {
                    mRootView.isVisible = false
                    mErrorView.isVisible = true
                }
            })

    }

    private fun refresh(){
        mRefreshBtn.setOnClickListener {
            getTerms(mClassId!!)
        }
    }


    override fun onItemClick(position: Int) {
        val model = mTermList[position - 1]

        TermResultDialog(this, mClassId!!, null, model.session, model.term, "")
            .apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
    }


    /*   private fun callDeleteClassApi() {
           val dialog1 = ACProgressFlower.Builder(this)
               .direction(ACProgressConstant.DIRECT_CLOCKWISE)
               .textMarginTop(10)
               .fadeColor(Color.DKGRAY).build()
           dialog1.setCanceledOnTouchOutside(false)
           dialog1.show()
           val url = "http://linkskool.com/newportal/api/deleteClass.php?id=" + classId + "&_db=" + db
           val stringRequest = StringRequest(Request.Method.GET, url, { response ->
               dialog1.dismiss()
               Log.i("response", response!!)
               try {
                   val jsonObject = JSONObject(response)
                   val status = jsonObject.getString("status")
                   if (status == "success") {
                       val deleteBuilder = classDao!!.deleteBuilder()
                       deleteBuilder.where().eq("classId", classId)
                       deleteBuilder.delete()
                       onBackPressed()
                       Toast.makeText(
                           this@AdminResultDashboardActivity,
                           "Operation was successful",
                           Toast.LENGTH_SHORT
                       ).show()
                   } else if (status == "failed") {
                       Toast.makeText(
                           this@AdminResultDashboardActivity,
                           "Operation failed",
                           Toast.LENGTH_SHORT
                       ).show()
                   }
               } catch (e: JSONException) {
                   e.printStackTrace()
               } catch (e: SQLException) {
                   e.printStackTrace()
               }
           }) {
               dialog1.dismiss()
               Toast.makeText(
                   this@AdminResultDashboardActivity,
                   "Error connecting to server",
                   Toast.LENGTH_SHORT
               ).show()
           }
           val requestQueue = Volley.newRequestQueue(this@AdminResultDashboardActivity)
           requestQueue.add(stringRequest)
       }
       */

}