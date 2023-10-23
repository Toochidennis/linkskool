package com.digitaldream.linkskool.dialog

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.StaffElearningCourseOutlineActivity
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.CourseTable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.j256.ormlite.dao.DaoManager

private const val ARG_PARAM1 = "param1"

class StaffELearningLevelBottomSheet : BottomSheetDialogFragment() {

    private lateinit var backBtn: ImageButton
    private lateinit var levelRecyclerView: RecyclerView

    private lateinit var levelAdapter: GenericAdapter<CourseTable>
    private lateinit var databaseHelper: DatabaseHelper

    private var levelTable = mutableListOf<CourseTable>()

    private var courseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseId = it.getString(ARG_PARAM1)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(courseId: String) = StaffELearningLevelBottomSheet().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, courseId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_staff_e_learning_level, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        loadLevels()
    }

    private fun setUpViews(view: View) {
        view.apply {
            backBtn = findViewById(R.id.backBtn)
            levelRecyclerView = findViewById(R.id.levelRecyclerView)
        }

        databaseHelper = DatabaseHelper(requireContext())

        backBtn.setOnClickListener { dismiss() }
    }

    private fun loadLevels() {
        try {
            val levelDao =
                DaoManager.createDao(databaseHelper.connectionSource, CourseTable::class.java)
            levelTable = levelDao.queryBuilder().where().eq("courseId", courseId ?: "").query()

            setUpLevelAdapter()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpLevelAdapter() {
        levelAdapter = GenericAdapter(
            levelTable,
            R.layout.item_staff_e_learning_level_layout,
            bindItem = { itemView, model, _ ->
                val levelNameTxt: TextView = itemView.findViewById(R.id.levelNameTxt)

                levelNameTxt.text = model.levelName
            }
        ) {
            val itemPosition = levelTable[it]

            requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)
                .edit().apply {
                    putString("course_name", itemPosition.courseName)
                    putString("courseId", itemPosition.courseId)
                    putString("level", itemPosition.levelId)
                }.apply()

            startActivity(Intent(requireContext(), StaffElearningCourseOutlineActivity::class.java))

            dismiss()
        }

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        levelRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = levelAdapter
        }
    }

}