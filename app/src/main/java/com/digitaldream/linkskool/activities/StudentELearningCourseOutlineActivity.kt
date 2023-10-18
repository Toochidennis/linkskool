package com.digitaldream.linkskool.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.SectionPagerAdapter
import com.digitaldream.linkskool.fragments.StudentELearningCourseworkFragment
import com.digitaldream.linkskool.fragments.StudentELearningStreamFragment
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import com.google.android.material.tabs.TabLayout

class StudentELearningCourseOutlineActivity :
    AppCompatActivity(R.layout.activity_student_e_learning_course_outline) {

    private lateinit var courseViewPager: ViewPager
    private lateinit var courseTabLayout: TabLayout

    private var actionBar: ActionBar? = null
    private var levelId: String? = null
    private var courseId: String? = null
    private var term: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViews()

        loadOutline()
    }

    private fun setUpViews() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        courseTabLayout = findViewById(R.id.courseTabLayout)
        courseViewPager = findViewById(R.id.courseViewPager)

        setSupportActionBar(toolbar)
        actionBar = supportActionBar

        actionBar?.apply {
            title = ""
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val sharedPreferences = getSharedPreferences(
            "loginDetail", MODE_PRIVATE
        )
        levelId = sharedPreferences.getString("level", "")
        term = sharedPreferences.getString("term", "")
        courseId = sharedPreferences.getString("courseId", "")
    }

    private fun setUpViewPager(response: String) {
        val pagerAdapter = SectionPagerAdapter(supportFragmentManager).apply {
            addFragment(StudentELearningCourseworkFragment.newInstance(response), "Coursework")
            addFragment(StudentELearningStreamFragment.newInstance(response), "Stream")
        }

        courseViewPager.adapter = pagerAdapter
        courseTabLayout.setupWithViewPager(courseViewPager, true)
        courseTabLayout.getTabAt(0)?.setIcon(R.drawable.ic_assignment_black_24dp)
        courseTabLayout.getTabAt(1)?.setIcon(R.drawable.ic_forum_24)
    }


    private fun loadOutline() {
        val url = "${getString(R.string.base_url)}/getOutline.php?" +
                "course=$courseId&&level=$levelId&&term=$term"

        sendRequestToServer(
            Request.Method.GET,
            url,
            this,
            null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    setUpViewPager(response)
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(
                        this@StudentELearningCourseOutlineActivity,
                        getString(R.string.no_internet), Toast.LENGTH_SHORT
                    ).show()

                    onBackPressedDispatcher.onBackPressed()
                }
            }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return false
    }
}