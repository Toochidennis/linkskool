package com.digitaldream.linkskool.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.adapters.GenericAdapter2
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.LevelTable
import com.digitaldream.linkskool.models.RecentActivityModel
import com.digitaldream.linkskool.models.UpcomingQuizModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import kotlin.random.Random


class AdminELearningDashboardFragment : Fragment(R.layout.fragment_admin_e_learning_dashboard) {

    private lateinit var toolbar: Toolbar
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var recentActivityRecyclerView: RecyclerView
    private lateinit var levelRecyclerView: RecyclerView
    private lateinit var feedbackTextView: TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        initData()
    }

    private fun initViews(view: View) {
        view.apply {
            toolbar = findViewById(R.id.toolbar)
            viewPager2 = findViewById(R.id.viewPager)
            tabLayout = findViewById(R.id.tabLayout)
            recentActivityRecyclerView = findViewById(R.id.recentActivityRecyclerView)
            levelRecyclerView = findViewById(R.id.levelRecyclerView)
            feedbackTextView = findViewById(R.id.feedbackTextView)
        }
    }


    private fun initData() {
        toolbar.title = "E-learning"

        getLevels()

        setUpActivityAdapter()

        setupEventAdapter()
    }


    private fun setUpActivityAdapter() {
        val activityList = mutableListOf<RecentActivityModel>().apply {
            for (i in 1..10) {
                add(
                    RecentActivityModel(
                        username = getString(R.string.toochi_dennis),
                        description = "Posted a new material",
                        date = "7, Jan"
                    )
                )
            }
        }


        val activityAdapter = GenericAdapter2(
            itemResLayout = R.layout.item_admin_recent_activity,
            itemList = activityList,
            bindItem = { itemView, model, _ ->
                val usernameTxt: TextView = itemView.findViewById(R.id.usernameTxt)
                val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
                val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)

                usernameTxt.text = model.username
                dateTxt.text = model.date
                descriptionTxt.text = model.description
            }
        )

        recentActivityRecyclerView.apply {
            hasFixedSize()
            adapter = activityAdapter
        }
    }

    private fun getLevels() {
        try {
            val databaseHelper =
                DatabaseHelper(context)
            val mDao: Dao<LevelTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, LevelTable::class.java
            )

            val levelList = mDao.queryForAll()
            levelList.sortBy { it.levelName }
            println(levelList)

            if (levelList.isEmpty()) {
                feedbackTextView.isVisible = true
                levelRecyclerView.isVisible = false
            } else {
                setUpLevelAdapter(levelList)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpLevelAdapter(levelList: MutableList<LevelTable>) {
        val levelAdapter = GenericAdapter(
            itemResLayout = R.layout.item_result_level,
            itemList = levelList,
            bindItem = { itemView, model, _ ->
                val levelNameTextView: TextView = itemView.findViewById(R.id.levelNameTextView)
                val levelContainer: LinearLayout = itemView.findViewById(R.id.levelContainer)
                val levelImageView: ImageView = itemView.findViewById(R.id.levelImageView)

                levelNameTextView.text = model.levelName.uppercase()

                val randomDrawable = generateRandomDrawable()
                levelContainer.setBackgroundResource(randomDrawable)

                val randomTintedColor = generateRandomTintedColor()
                levelImageView.setColorFilter(randomTintedColor, PorterDuff.Mode.SRC_IN)
            }
        ) { position ->
            val level = levelList[position]

            findNavController().navigate(
                R.id.action_adminELearningDashboardFragment_to_adminELearningCourseBottomSheetFragment,
                bundleOf(
                    "levelName" to level.levelName,
                    "levelId" to level.levelId
                )
            )
        }

        levelRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = levelAdapter
        }
    }

    private fun generateRandomDrawable(): Int {
        val drawableResources = listOf(
            R.drawable.ripple_effect1,
            R.drawable.ripple_effect16,
            R.drawable.ripple_effect15
        )

        return drawableResources[Random.nextInt(drawableResources.size)]
    }

    private fun generateRandomTintedColor(): Int {
        val colorResources = listOf(
            R.color.tinted_1, R.color.tinted_color_2, R.color.tinted_color_1,
            R.color.tinted_color_icon, R.color.tinted_6, R.color.tinted_7
        )

        val randomColorResourceId = colorResources[Random.nextInt(colorResources.size)]

        return ContextCompat.getColor(requireContext(), randomColorResourceId)
    }

    private fun setupEventAdapter() {
        val events = mutableListOf<UpcomingQuizModel>().apply {
            for (i in 1..6) {
                add(UpcomingQuizModel("$i", "First C/A Test $i", "19th Feb. 2024", "Mathematics"))
            }
        }

        val eventAdapter = GenericAdapter2(
            itemList = events,
            itemResLayout = R.layout.item_admin_e_learning_events,
            bindItem = { itemView, model, _ ->
                val titleTextView: TextView = itemView.findViewById(R.id.title_txt)
                val courseNameTextView: TextView = itemView.findViewById(R.id.course_name_txt)
                val dateTextView: TextView = itemView.findViewById(R.id.date_txt)

                titleTextView.text = model.title
                courseNameTextView.text = model.type
                dateTextView.text = model.date
            }
        )


        viewPager2.adapter = eventAdapter

        TabLayoutMediator(tabLayout, viewPager2) { _, _ -> }.attach()
    }
}