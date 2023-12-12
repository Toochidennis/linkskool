package com.digitaldream.winskool.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.SectionPagerAdapter
import com.digitaldream.winskool.fragments.StaffELearningCourseWorkFragment
import com.digitaldream.winskool.fragments.StaffELearningStreamFragment
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.tabs.TabLayout

class StaffELearningContentDashboardActivity : AppCompatActivity() {

    private lateinit var courseViewPager: ViewPager
    private lateinit var courseTabLayout: TabLayout

    private var courseName: String? = null
    private var courseId: String? = null
    private var levelId: String? = null
    private var term: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_e_learning_content_dashboard)

        setUpViews()

        loadOutline()
    }


    private fun setUpViews() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        courseTabLayout = findViewById(R.id.courseTabLayout)
        courseViewPager = findViewById(R.id.courseViewPager)

        val sharedPreferences = getSharedPreferences("loginDetail", MODE_PRIVATE)

        with(sharedPreferences) {
            levelId = getString("level", "")
            term = getString("term", "")
            courseId = getString("courseId", "")
            courseName = getString("course_name", "")
        }

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = capitaliseFirstLetter(courseName ?: "")
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

    }

    private fun setUpViewPager(response: String) {
        val pagerAdapter = SectionPagerAdapter(supportFragmentManager).apply {
            addFragment(StaffELearningStreamFragment.newInstance(response, ""), "Stream")
            addFragment(StaffELearningCourseWorkFragment.newInstance(response, ""), "Coursework")
        }

        courseViewPager.adapter = pagerAdapter
        courseTabLayout.setupWithViewPager(courseViewPager, true)
        courseTabLayout.getTabAt(0)?.setIcon(R.drawable.ic_forum_24)
        courseTabLayout.getTabAt(1)?.setIcon(R.drawable.ic_assignment_black_24dp)
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
                        this@StaffELearningContentDashboardActivity,
                        getString(R.string.no_internet), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            true
        } else {
            false
        }
    }

    /*  private fun parseResponseJson(response: String) {
      try {
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
                  }
              }
          }

      } catch (e: Exception) {
          e.printStackTrace()
      }
  }
*/

}