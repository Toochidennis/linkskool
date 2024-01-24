package com.digitaldream.linkskool.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.CourseTable
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import kotlin.random.Random

class AdminELearningCourseBottomSheetFragment :
    BottomSheetDialogFragment(R.layout.fragment_admin_e_learning_course_bottom_sheet) {

    private lateinit var courseRecyclerView: RecyclerView
    private lateinit var errorMessage: TextView
    private lateinit var backBtn: ImageView

    private var levelName: String? = null
    private var levelId: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            levelName = it.getString("levelName")
            levelId = it.getString("levelId")
        }

        initViews(view)

        backBtn.setOnClickListener {
            dismiss()
        }

        getCourses()
    }

    private fun initViews(view: View) {
        view.apply {
            courseRecyclerView = findViewById(R.id.course_recycler)
            errorMessage = findViewById(R.id.error_message)
            backBtn = findViewById(R.id.back_btn)
        }
    }


    private fun getCourses() {
        try {
            val databaseHelper =
                DatabaseHelper(context)
            val mDao: Dao<CourseTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, CourseTable::class.java
            )
            val courseList = mDao.queryForAll()
            courseList.sortBy { it.courseName }

            if (courseList.isEmpty()) {
                errorMessage.isVisible = true
                courseRecyclerView.isVisible = false
            } else {
                setUpCourseAdapter(courseList)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpCourseAdapter(courseList: MutableList<CourseTable>) {
        val courseAdapter = GenericAdapter(
            courseList,
            R.layout.item_e_learning_course,
            bindItem = { itemView, model, _ ->
                val courseName: TextView = itemView.findViewById(R.id.course_name)
                val courseInitials: TextView = itemView.findViewById(R.id.course_initials)
                val linearLayout: LinearLayout = itemView.findViewById(R.id.initials_bg)

                courseName.text = capitaliseFirstLetter(model.courseName)
                courseInitials.text = model.courseName.substring(0, 1).uppercase()

                val gradientDrawable = linearLayout.background.mutate() as GradientDrawable
                val randomTintedColor = generateRandomTintedColor()
                gradientDrawable.setColor(randomTintedColor)
                linearLayout.background = gradientDrawable
            }
        ) { position ->
            val courseModel = courseList[position]

            findNavController().navigate(
                R.id.action_adminELearningCourseBottomSheetFragment_to_adminELearningCourseOutlineFragment,
                bundleOf(
                    "courseName" to courseModel.courseName,
                    "courseId" to courseModel.courseId,
                    "levelName" to levelName,
                    "levelId" to levelId
                )
            )

        }


        courseRecyclerView.apply {
            hasFixedSize()
            adapter = courseAdapter
        }

    }


    private fun generateRandomTintedColor(): Int {
        val colorResources = listOf(
            R.color.tinted_color_icon_5,
            R.color.tinted_0, R.color.tinted_1, R.color.tinted_2, R.color.tinted_3, R.color
                .tinted_4, R.color.tinted_5, R.color.tinted_6, R.color.tinted_7
        )

        val randomColorResourceId = colorResources[Random.nextInt(colorResources.size)]

        return ContextCompat.getColor(requireContext(), randomColorResourceId)
    }

}