package com.digitaldream.linkskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.AdminELearningActivity
import com.digitaldream.linkskool.adapters.SectionPagerAdapter
import com.digitaldream.linkskool.models.SharedViewModel
import com.digitaldream.linkskool.utils.CustomViewPager2
import com.google.android.material.tabs.TabLayout


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminELearningQuestionDetailsFragment :
    Fragment(R.layout.fragment_admin_e_learning_question_details) {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: CustomViewPager2
    private lateinit var menuHost: MenuHost

    private lateinit var sharedViewModel: SharedViewModel

    private var actionBar: ActionBar? = null
    private var customActionBarView: View? = null
    private var isCustomBarShowing = false

    private var jsonData: String? = null
    private var taskType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
            taskType = it.getString(ARG_PARAM2)
        }

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }


    companion object {

        @JvmStatic
        fun newInstance(jsonData: String, taskType: String) =
            AdminELearningQuestionDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, jsonData)
                    putString(ARG_PARAM2, taskType)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView(view)

        setUpMenu()

        setUpViewPager()

        observeCustomBar()
    }

    private fun setUpView(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            tabLayout = findViewById(R.id.tabLayout)
            viewPager = findViewById(R.id.viewPager)

            menuHost = requireActivity()
            (activity as AppCompatActivity).setSupportActionBar(toolbar)

            hideCustomActionBar()
        }
    }

    private fun setUpViewPager() {
        SectionPagerAdapter(childFragmentManager).apply {
            addFragment(
                AdminELearningQuestionViewFragment.newInstance(jsonData ?: "", taskType ?: ""),
                "Question"
            )
            addFragment(
                AdminELearningQuestionAnswersFragment
                    .newInstance(jsonData ?: "", ""),
                "Student answers"
            )
        }.let {
            viewPager.apply {
                adapter = it
                currentItem = 1
                tabLayout.setupWithViewPager(viewPager, true)
            }
        }
    }

    private fun observeCustomBar() {
        sharedViewModel.customActionBarVisible.observe(requireActivity()) { showCustomActionBar ->
            viewPager.setCustomBarVisibility(showCustomActionBar)

            for (i in 0 until tabLayout.tabCount) {
                tabLayout.getTabAt(i)?.view?.isClickable = !showCustomActionBar
            }

            if (showCustomActionBar) {
                showCustomActionBar()
            } else {
                hideCustomActionBar()
            }
        }
    }

    private fun hideCustomActionBar() {
        actionBar = (activity as AppCompatActivity).supportActionBar
        isCustomBarShowing = false
        requireActivity().invalidateOptionsMenu()

        actionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_TITLE
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = "Question"
            customView = null
        }

    }

    private fun showCustomActionBar() {
        customActionBarView = layoutInflater.inflate(R.layout.custom_action_bar_layout, null)
        isCustomBarShowing = true

        requireActivity().invalidateOptionsMenu()

        actionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            customView = customActionBarView
        }

    }

    private fun setUpMenu() {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                if (isCustomBarShowing) {
                    menu.clear()
                }
                super.onPrepareMenu(menu)
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_e_learning_details, menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.refresh -> {
                        // setUpPager()

                        true
                    }

                    R.id.edit -> {
                        startActivity(
                            Intent(requireContext(), AdminELearningActivity::class.java)
                                .putExtra("from", "question")
                                .putExtra("task", "edit")
                                .putExtra("json", jsonData)
                        )
                        true
                    }

                    R.id.delete -> {
                        true
                    }

                    else -> {
                        requireActivity().finish()
                        true
                    }

                }
            }
        })
    }
}