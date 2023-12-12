package com.digitaldream.winskool.activities

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
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.ClassAttendanceAdapter
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.models.StudentTable
import com.digitaldream.winskool.models.TagModel
import com.digitaldream.winskool.utils.FunctionUtils.getDate
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.j256.ormlite.dao.DaoManager
import org.json.JSONArray
import org.json.JSONObject


class ClassAttendanceActivity : AppCompatActivity(),
    ClassAttendanceAdapter.AttendanceUpdateListener {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var studentsRecyclerView: RecyclerView
    private lateinit var selectAllLayout: RelativeLayout
    private lateinit var imageView: ImageView
    private lateinit var studentCountTxt: TextView
    private lateinit var allTitleTxt: TextView
    private lateinit var errorMessageTxt: TextView
    private lateinit var submitBtn: FloatingActionButton

    private var studentList = mutableListOf<StudentTable>()
    private val tagList = mutableListOf<TagModel>()
    private val selectedItems = hashMapOf<String, String>()
    private var databaseHelper: DatabaseHelper? = null

    private var responseId: String? = null
    private var userId: String? = null
    private var levelId: String? = null
    private var classId: String? = null
    private var year: String? = null
    private var term: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_attendance)

        setUpViews()
        initData()
    }

    private fun setUpViews() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView)
        selectAllLayout = findViewById(R.id.selectAllLayout)
        studentCountTxt = findViewById(R.id.studentCountTxt)
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
            year = getString("school_year", "")
            userId = getString("user_id", "")
        }

        levelId = intent.getStringExtra("level_id")
        classId = intent.getStringExtra("class_id")

    }


    private fun initData() {
        databaseHelper = DatabaseHelper(this)

        try {
            val studentDao = DaoManager.createDao(
                databaseHelper?.connectionSource,
                StudentTable::class.java
            )
            studentList = studentDao.queryBuilder()
                .where().eq("studentLevel", levelId)
                .and().eq("studentClass", classId).query()

            previousAttendance()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        submitBtn.setOnClickListener {
            takeAttendance()
        }

        refreshData()
    }


    private fun previousAttendance() {
        val url = getString(R.string.base_url) + "/getAttendance.php"
        val hashMap = hashMapOf<String, String>().apply {
            put("class", classId ?: "")
            put("date", getDate())
            put("course", "0")
        }

        sendRequestToServer(
            Request.Method.POST, url, this, hashMap,
            object
                : VolleyCallback {
                override fun onResponse(response: String) {
                    parsePreviousAttendanceResponse(response)
                }

                override fun onError(error: VolleyError) {
                    showToast(getString(R.string.no_internet))
                }
            }, false
        )
    }

    private fun parsePreviousAttendanceResponse(response: String) {
        try {
            if (response != "[]") {
                with(JSONArray(response)) {
                    getJSONObject(0).let {
                        responseId = it.getString("id")
                        studentCountTxt.text = it.getString("count")
                        studentCountTxt.isVisible = true
                        imageView.isVisible = false

                        val attendanceArray = it.getString("register")

                        JSONArray(attendanceArray).let { array ->
                            for (i in 0 until array.length()) {
                                array.getJSONObject(i).let { attendance ->
                                    selectedItems[attendance.getString("id")] =
                                        attendance.getString("name")
                                }
                            }
                        }
                    }
                }
            }

            sortData()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sortData() {
        if (studentList.isEmpty()) {
            errorMessageTxt.isVisible = true
        } else {
            studentList.forEach { studentTable ->
                val name = selectedItems[studentTable.studentId]
                val studentName =
                    "${studentTable.studentSurname} ${studentTable.studentMiddlename} ${studentTable.studentFirstname}"

                if (name != null) {
                    tagList.add(TagModel(studentTable.studentId, studentName, true))
                } else {
                    tagList.add(TagModel(studentTable.studentId, studentName, false))
                }
            }

            errorMessageTxt.isVisible = false

            setUpRecyclerView()
        }
    }

    private fun setUpRecyclerView() {
        val attendanceAdapter = ClassAttendanceAdapter(
            tagList, selectedItems, selectAllLayout,
            allTitleTxt, this

        )

        studentsRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(this@ClassAttendanceActivity)
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

    private fun getParameters() = HashMap<String, String>().apply {
        put("id", responseId ?: "0")
        put("staff", userId ?: "")
        put("course", "0")
        put("date", "${getDate()}: 00:00:00")
        put("count", studentCountTxt.text.toString())
        put("register", prepareAttendanceData())
        put("class", classId ?: "")
        put("year", year ?: "")
        put("term", term ?: "")
    }


    private fun takeAttendance() {
        val url = getString(R.string.base_url) + "/setAttendance.php"

        sendRequestToServer(Request.Method.POST, url, this, getParameters(),
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
            tagList.clear()
            selectedItems.clear()
            previousAttendance()
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

    override fun onDestroy() {
        super.onDestroy()
        databaseHelper?.close()
    }
}