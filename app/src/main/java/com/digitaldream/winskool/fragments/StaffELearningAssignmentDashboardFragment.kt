package com.digitaldream.winskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.StaffELearningActivity
import com.digitaldream.winskool.adapters.SectionPagerAdapter
import com.digitaldream.winskool.utils.CustomViewPager2
import com.google.android.material.tabs.TabLayout


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StaffELearningAssignmentDashboardFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: CustomViewPager2
    private lateinit var menuHost: MenuHost


    private var assignmentData: String? = null
    private var taskType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            assignmentData = it.getString(ARG_PARAM1)
            taskType = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_staff_e_learning_assignment_dashboard,
            container,
            false
        )
    }

    companion object {

        @JvmStatic
        fun newInstance(data: String, param2: String) =
            StaffELearningAssignmentDashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, data)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        setUpMenu()

        setUpViewPager()
    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            tabLayout = findViewById(R.id.tabLayout)
            viewPager = findViewById(R.id.viewPager)

            menuHost = requireActivity()
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (activity as AppCompatActivity).supportActionBar

            actionBar?.apply {
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
                title = "Assignment"
            }

        }
    }

    private fun setUpViewPager() {
        SectionPagerAdapter(parentFragmentManager).apply {
            addFragment(
                StaffELearningAssignmentInstructionsFragment.newInstance(
                    assignmentData ?: "",
                    taskType ?: ""
                ), "Instructions"
            )

            addFragment(
                StaffELearningAssignmentStudentWorkFragment
                    .newInstance(assignmentData ?: "", ""), "Student work"
            )
        }.let {
            viewPager.apply {
                adapter = it
                currentItem = 1
                tabLayout.setupWithViewPager(viewPager, true)
            }
        }
    }

    private fun setUpMenu() {
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_e_learning_details, menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.refresh -> {
                        true
                    }

                    R.id.edit -> {
                        startActivity(
                            Intent(requireContext(), StaffELearningActivity::class.java)
                                .putExtra("from", "create_assignment")
                                .putExtra("task", "edit")
                                .putExtra("json", assignmentData)
                        )
                        true
                    }

                    R.id.delete -> {
                        true
                    }

                    else -> {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        false
                    }

                }
            }
        })
    }


}