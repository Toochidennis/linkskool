package com.digitaldream.linkskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.SectionPagerAdapter
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import com.google.android.material.tabs.TabLayout


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StaffELearningContentDashboardFragment : Fragment() {

    private lateinit var courseViewPager: ViewPager
    private lateinit var courseTabLayout: TabLayout

    private var actionBar: ActionBar? = null
    private var levelId: String? = null
    private var courseId: String? = null
    private var term: String? = null

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff_e_learning_content_dashboard, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StaffELearningContentDashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        loadOutline()
    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            courseTabLayout = findViewById(R.id.courseTabLayout)
            courseViewPager = findViewById(R.id.courseViewPager)

            (requireContext() as AppCompatActivity).setSupportActionBar(toolbar)
            actionBar = (requireContext() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = ""
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

            val sharedPreferences = requireActivity().getSharedPreferences(
                "loginDetail", AppCompatActivity.MODE_PRIVATE
            )

            with(sharedPreferences) {
                levelId = getString("level", "")
                term = getString("term", "")
                courseId = getString("courseId", "")
            }

        }
    }

    private fun setUpViewPager(response: String) {
        val pagerAdapter = SectionPagerAdapter(parentFragmentManager).apply {
            addFragment(StaffELearningStreamFragment.newInstance(response, ""), "Stream")
            addFragment(StaffELearningCourseWorkFragment.newInstance(response, ""), "Coursework")
        }

        courseViewPager.adapter = pagerAdapter
        courseTabLayout.setupWithViewPager(courseViewPager, true)
        courseTabLayout.getTabAt(0)?.setIcon(R.drawable.ic_forum_24)
        courseTabLayout.getTabAt(1)?.setIcon(R.drawable.ic_assignment_black_24dp)
    }


    private fun loadOutline() {
        val url = "${requireContext().getString(R.string.base_url)}/getOutline.php?" +
                "course=$courseId&&level=$levelId&&term=$term"

        sendRequestToServer(
            Request.Method.GET,
            url,
            requireContext(),
            null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    setUpViewPager(response)
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_internet), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}