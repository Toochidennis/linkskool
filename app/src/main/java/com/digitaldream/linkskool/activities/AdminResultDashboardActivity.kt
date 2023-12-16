package com.digitaldream.linkskool.activities

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminResultDashboardAdapter
import com.digitaldream.linkskool.dialog.AdminClassesDialog
import com.digitaldream.linkskool.dialog.AdminResultStudentNamesDialog
import com.digitaldream.linkskool.interfaces.ResultListener
import com.digitaldream.linkskool.models.AdminResultDashboardModel
import com.digitaldream.linkskool.models.AdminResultTermModel
import com.digitaldream.linkskool.models.ChartModel
import com.digitaldream.linkskool.utils.FunctionUtils.plotLineChart
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.achartengine.GraphicalView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class AdminResultDashboardActivity : AppCompatActivity(R.layout.activity_admin_result_dashboard) {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var backBtn: ImageButton
    private lateinit var titleTxt: TextView
    private lateinit var classBtn: Button
    private lateinit var rootView: LinearLayout
    private lateinit var averageScoreTxt: TextView
    private lateinit var averageGraph: LinearLayout
    private lateinit var resultRecyclerView: RecyclerView
    private lateinit var studentResultCardView: CardView
    private lateinit var courseRegistrationCardView: CardView
    private lateinit var attendanceCardView: CardView
    private lateinit var errorMessageTxt: TextView
    private lateinit var termErrorMessageTxt: TextView

    private val sessionList = mutableListOf<AdminResultDashboardModel>()
    private val mGraphList = arrayListOf<ChartModel>()
    private var graphicalView: GraphicalView? = null
    private var classId: String? = null
    private var className: String? = null
    private var levelId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
    }

    private fun initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        backBtn = findViewById(R.id.back_btn)
        titleTxt = findViewById(R.id.toolbar_text)
        classBtn = findViewById(R.id.class_name)
        rootView = findViewById(R.id.result_view)
        averageScoreTxt = findViewById(R.id.average_score)
        averageGraph = findViewById(R.id.chart)
        resultRecyclerView = findViewById(R.id.term_recycler)
        studentResultCardView = findViewById(R.id.student_result)
        courseRegistrationCardView = findViewById(R.id.course_registration)
        attendanceCardView = findViewById(R.id.attendance)
        termErrorMessageTxt = findViewById(R.id.term_error_message_txt)
        errorMessageTxt = findViewById(R.id.error_message_txt)

        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("class_name")
        levelId = intent.getStringExtra("levelId")

        "Result".also { titleTxt.text = it }
        classBtn.text = className

        onCardClick()

        changeClass()

        getResults()

        refreshData()

        if (graphicalView == null) {
            graphicalView = plotLineChart(
                mGraphList,
                this,
                "Average",
                "Month/Year"
            )
            averageGraph.addView(graphicalView)
        } else {
            graphicalView!!.repaint()
        }

    }

    private fun onCardClick() {
        backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        attendanceCardView.setOnClickListener {
            startActivity(
                Intent(this, AdminAttendanceActivity::class.java)
                    .putExtra("levelId", levelId)
                    .putExtra("classId", classId)
                    .putExtra("className", className)
                    .putExtra("from", "result")
            )
        }

        courseRegistrationCardView.setOnClickListener {
            startActivity(
                Intent(this, RegYearList::class.java)
                    .putExtra("levelId", levelId)
                    .putExtra("classId", classId)
            )
        }

        studentResultCardView.setOnClickListener {
            AdminResultStudentNamesDialog(
                this, classId ?: "", "",
                null
            ).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun setClassName() {
        classBtn.text = className
    }

    private fun changeClass() {
        classBtn.setOnClickListener {
            AdminClassesDialog(this, "result", "changeLevel",
                object : ResultListener {
                    override fun sendClassName(sName: String) {
                        className = sName
                        setClassName()
                    }

                    override fun sendLevelId(sLevelId: String) {
                        levelId = sLevelId
                    }

                    override fun sendClassId(sClassId: String) {
                        classId = sClassId
                        getResults()
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

    private fun getResults() {
        val url = "${getString(R.string.base_url)}/jsonTerms.php?class=$classId"

        sendRequestToServer(Request.Method.GET, url, this, null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response != "[]") {
                        parseResponse(response)
                        termErrorMessageTxt.isVisible = false
                        errorMessageTxt.isVisible = false
                    } else {
                        termErrorMessageTxt.isVisible = true
                    }
                }

                override fun onError(error: VolleyError) {
                    rootView.isVisible = false
                    errorMessageTxt.isVisible = true
                    Toast.makeText(
                        this@AdminResultDashboardActivity, getString(
                            R.string
                                .no_internet
                        ), Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun parseResponse(response: String) {
        try {
            sessionList.clear()

            with(JSONArray(response)) {
                getJSONObject(0).run {
                    for (year in keys()) {
                        if (year == "0000") continue

                        val termList = mutableListOf<AdminResultTermModel>()

                        JSONObject(getString(year)).let { terms ->
                            val termsObject = terms.get("terms")
                            if (termsObject is JSONObject) {
                                termsObject.let {
                                    for (termKey in it.keys()) {
                                        val term = it.getString(termKey)
                                        termList.add(AdminResultTermModel(term, year))
                                    }
                                }
                            } else if (termsObject is JSONArray) {
                                termsObject.let {
                                    for (termKey in 0 until it.length()) {
                                        val term = it.getString(termKey)
                                        if (term != "null")
                                            termList.add(AdminResultTermModel(term, year))
                                    }
                                }
                            }
                        }

                        val previousYear = year.toInt() - 1
                        val session = String.format(
                            Locale.getDefault(), "%d/%s",
                            previousYear, year
                        )

                        sessionList.add(AdminResultDashboardModel(session, termList))
                    }
                }
            }

            setUpAdapter()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpAdapter() {
        val resultAdapter = AdminResultDashboardAdapter(itemList = sessionList, classId ?: "")

        resultRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(this@AdminResultDashboardActivity)
            adapter = resultAdapter
        }

    }

    private fun refreshData() {
        swipeRefreshLayout.setOnRefreshListener {
            getResults()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    /*   private fun callDeleteClassApi() {

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