package com.digitaldream.linkskool.fragments

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.AssessmentSetting
import com.digitaldream.linkskool.activities.GradeSettings
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.dialog.ResultClassDialog
import com.digitaldream.linkskool.models.LevelTable
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import kotlin.random.Random


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminResultDashboardFragment : Fragment(R.layout.fragment_admin_result_dashboard) {


    private lateinit var imageSlider: ImageSlider
    private lateinit var skillsAndBehaviourButton: CardView
    private lateinit var gradeButton: CardView
    private lateinit var assessmentButton: CardView
    private lateinit var toolsButton: CardView
    private lateinit var levelRecyclerView: RecyclerView
    private lateinit var feedbackTextView: TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        initData()
    }


    private fun initViews(view: View) {
        view.apply {
            imageSlider = findViewById(R.id.imageSlider)
            skillsAndBehaviourButton = findViewById(R.id.skillsAndBehaviourButton)
            gradeButton = findViewById(R.id.gradeButton)
            assessmentButton = findViewById(R.id.assessmentButton)
            toolsButton = findViewById(R.id.toolsButton)
            levelRecyclerView = findViewById(R.id.levelRecyclerView)
            feedbackTextView = findViewById(R.id.feedbackTextView)
        }
    }

    private fun initData() {
        handleViewClicks()
        setUpImageAdapter()
        getLevels()
    }


    private fun handleViewClicks() {
        assessmentButton.setOnClickListener {
            startActivity(Intent(requireContext(), AssessmentSetting::class.java))
        }

        gradeButton.setOnClickListener {
            startActivity(Intent(requireContext(), GradeSettings::class.java))
        }
    }

    private fun setUpImageAdapter() {
        mutableListOf<SlideModel>().apply {
            add(SlideModel(R.drawable.result_illustration_2))
            add(SlideModel(R.drawable.result_illustration_4))
            add(SlideModel(R.drawable.result_illustration_3))
            add(SlideModel(R.drawable.result_illustration_1))
        }.also {
            imageSlider.setImageList(imageList = it)
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
            openClassDialog(level.levelId)
        }

        levelRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = levelAdapter
        }
    }

    private fun openClassDialog(levelId: String) {
        ResultClassDialog(requireContext(), levelId).run {
            show()
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
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
            R.color.tinted_color_icon,  R.color.tinted_6, R.color.tinted_7
        )

        val randomColorResourceId = colorResources[Random.nextInt(colorResources.size)]

        return ContextCompat.getColor(requireContext(), randomColorResourceId)
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminResultDashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}