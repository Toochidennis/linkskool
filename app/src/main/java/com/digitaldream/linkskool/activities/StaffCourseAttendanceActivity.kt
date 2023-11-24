package com.digitaldream.linkskool.activities


import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.RelativeLayout
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
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.StaffCourseAttendanceAdapter
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils.getDate
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject

class StaffCourseAttendanceActivity : AppCompatActivity(),
    StaffCourseAttendanceAdapter.AttendanceUpdateListener {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var studentsRecyclerView: RecyclerView
    private lateinit var selectAllLayout: RelativeLayout
    private lateinit var imageView: ImageView
    private lateinit var studentCountTxt: TextView
    private lateinit var allTitleTxt: TextView
    private lateinit var errorMessageTxt: TextView
    private lateinit var submitBtn: FloatingActionButton

    private var studentList = mutableListOf<TagModel>()
    private val selectedItems = hashMapOf<String, String>()

    private var responseId: String? = null
    private var userId: String? = null
    private var courseId: String? = null
    private var classId: String? = null
    private var year: String? = null
    private var term: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_course_attendance)

        initViews()
        initData()
    }


    private fun initViews() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView)
        selectAllLayout = findViewById(R.id.selectAllLayout)
        studentCountTxt = findViewById(R.id.countTxt)
        allTitleTxt = findViewById(R.id.allTitleTxt)
        imageView = findViewById(R.id.imageView)
        errorMessageTxt = findViewById(R.id.errorMessageTxt)
        submitBtn = findViewById(R.id.submitBtn)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Take attendance"
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        val sharedPreferences = getSharedPreferences("loginDetail", MODE_PRIVATE)

        with(sharedPreferences) {
            term = getString("term", "")
            userId = getString("user_id", "")
            year = getString("school_year", "")
        }

        classId = intent.getStringExtra("class_id")
        courseId = intent.getStringExtra("course_id")
    }

    private fun initData() {
        getStudents()

        submitBtn.setOnClickListener {
            submitAttendance()
        }

        refreshData()
    }

    private fun studentsParameters(): HashMap<String, String> {
        return HashMap<String, String>().apply {
            put("class", classId ?: "")
            put("term", term ?: "")
            put("year", year ?: "")
            put("course", courseId ?: "")
            put("attendance", "1")
            put("date", "${getDate()}: 00:00:00")
        }
    }

    private fun getStudents() {
        val url = getString(R.string.base_url) + "/getCourseRegistration.php"

        sendRequestToServer(Request.Method.POST, url, this, studentsParameters(),
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    parseStudentsResponse(response)
                }

                override fun onError(error: VolleyError) {
                    showToast(getString(R.string.no_internet))
                }
            })
    }

    private fun parseStudentsResponse(response: String) {
        try {
            with(JSONObject(response)) {
                val studentArray = getString("student")
                val attendanceArray = getString("attendance")

                if (studentArray != "[]") {
                    JSONArray(studentArray).run {
                        for (i in 0 until length()) {
                            getJSONObject(i).let {
                                studentList.add(
                                    TagModel(
                                        it.getString("student_id"),
                                        it.getString("student_name")
                                    )
                                )
                            }
                        }
                    }

                    if (attendanceArray != "[]") {
                        JSONArray(attendanceArray).run {
                            responseId = getJSONObject(0).getString("id")
                            studentCountTxt.text = getJSONObject(0).getString("count")
                            studentCountTxt.isVisible = true
                            imageView.isVisible = false

                            JSONArray(getJSONObject(0).getString("register")).let { register ->
                                for (i in 0 until register.length()) {
                                    register.getJSONObject(i).let {
                                        selectedItems[it.getString("id")] = it.getString("name")
                                    }
                                }
                            }
                        }
                    }

                    sortData()
                    errorMessageTxt.isVisible = false
                } else {
                    errorMessageTxt.isVisible = true
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun sortData() {
        if (studentList.isEmpty()) {
            errorMessageTxt.isVisible = true
        } else {
            studentList.forEach { student ->
                if (selectedItems[student.tagId] != null) {
                    student.isSelected = true
                }
            }

            errorMessageTxt.isVisible = false

            setUpRecyclerView()
        }
    }

    private fun setUpRecyclerView() {
        val attendanceAdapter = StaffCourseAttendanceAdapter(
            studentList, selectedItems, selectAllLayout,
            allTitleTxt, this
        )

        studentsRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(this@StaffCourseAttendanceActivity)
            adapter = attendanceAdapter
        }
    }

    private fun prepareAttendanceData() = JSONArray().apply {
        selectedItems.forEach { (key, value) ->
            JSONObject().apply {
                put("id", key)
                put("name", value)
            }.let {
                put(it)
            }
        }
    }.toString()

    private fun attendanceParameters() = HashMap<String, String>().apply {
        put("id", responseId ?: "")
        put("class", classId ?: "")
        put("staff", userId ?: "")
        put("year", year ?: "")
        put("term", term ?: "")
        put("course", courseId ?: "")
        put("register", prepareAttendanceData())
        put("count", studentCountTxt.text.toString())
        put("date", "${getDate()}: 00:00:00")
    }

    private fun submitAttendance() {
        val url = getString(R.string.base_url) + "/setAttendance.php"

        sendRequestToServer(Request.Method.POST, url, this, attendanceParameters(),
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        val status = JSONObject(response).getString("status")
                        if (status == "success") {
                            showToast("Operation was successful")
                            finish()
                        } else {
                            showToast(getString(R.string.no_internet))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: VolleyError) {
                    showToast(getString(R.string.no_internet))
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun refreshData() {
        swipeRefreshLayout.setOnRefreshListener {
            studentList.clear()
            selectedItems.clear()
            getStudents()
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

    override fun onAttendanceUpdate(hasChanges: Boolean, selectedItemsCount: Int) {
        if (hasChanges && selectedItemsCount > 0) {
            imageView.isVisible = false
            studentCountTxt.isVisible = true
            studentCountTxt.text = selectedItems.size.toString()

            // Show the submit button only if items are selected
            submitBtn.show()
        } else {
            imageView.isVisible = true
            studentCountTxt.isVisible = false

            // Hide the submit button if no items are selected
            submitBtn.hide()
        }
    }

}