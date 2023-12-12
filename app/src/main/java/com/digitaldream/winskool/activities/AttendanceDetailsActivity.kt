package com.digitaldream.winskool.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.GenericAdapter2
import com.digitaldream.winskool.models.TagModel
import com.digitaldream.winskool.utils.FunctionUtils.formatDate2
import com.digitaldream.winskool.utils.FunctionUtils.getRandomColor
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import org.json.JSONArray

class AttendanceDetailsActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var dateTxt: TextView
    private lateinit var detailsRecyclerView: RecyclerView
    private lateinit var errorMessageTxt: TextView

    private lateinit var detailsAdapter: GenericAdapter2<TagModel>
    private var studentTableList = mutableListOf<TagModel>()

    private var classId: String? = null
    private var courseId: String? = null
    private var className: String? = null
    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_attendance)

        initViews()

        attendanceDetails()

        refreshData()
    }

    private fun initViews() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        dateTxt = findViewById(R.id.dateTextView)
        detailsRecyclerView = findViewById(R.id.detailsRecyclerView)
        errorMessageTxt = findViewById(R.id.errorMessageTxt)


        classId = intent.getStringExtra("class_id")
        courseId = intent.getStringExtra("course_id")
        date = intent.getStringExtra("date")
        className = intent.getStringExtra("class_name")

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "$className"
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        dateTxt.text = formatDate2(date ?: "", "full")
    }

    private fun attendanceDetails() {
        val url = getString(R.string.base_url) + "/getAttendance.php"
        val hashMap = hashMapOf<String, String>().apply {
            put("class", classId ?: "")
            put("date", date ?: "")
            put("course", courseId ?: "")
        }

        sendRequestToServer(Request.Method.POST, url, this, hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    errorMessageTxt.isVisible = false
                    parseResponse(response)
                }

                override fun onError(error: VolleyError) {
                    errorMessageTxt.isVisible = true
                    Toast.makeText(
                        this@AttendanceDetailsActivity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun parseResponse(response: String) {
        try {
            with(JSONArray(response)) {
                getJSONObject(0).let { jsonObject ->
                    val register = jsonObject.getString("register")

                    JSONArray(register).run {
                        for (i in 0 until length()) {
                            getJSONObject(i).let {
                                studentTableList.add(
                                    TagModel(
                                        it.getString("id"),
                                        it.getString("name")
                                    )
                                )
                            }
                        }
                    }
                }
            }

            setUpAdapter()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpAdapter() {
        detailsAdapter = GenericAdapter2(
            studentTableList,
            R.layout.item_details_attendance,
            bindItem = { itemView, model, _ ->
                val initialLayout: LinearLayout = itemView.findViewById(R.id.initialContainer)
                val initialTxt: TextView = itemView.findViewById(R.id.initialTxt)
                val studentNameTxt: TextView = itemView.findViewById(R.id.studentNameTxt)

                initialTxt.text = model.tagName.substring(0, 1).uppercase()
                studentNameTxt.text = model.tagName

                getRandomColor(initialLayout)
            }
        )

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        detailsRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(this@AttendanceDetailsActivity)
            adapter = detailsAdapter
        }
    }

    private fun refreshData() {
        swipeRefreshLayout.setOnRefreshListener {
            studentTableList.clear()
            attendanceDetails()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return false
    }
}