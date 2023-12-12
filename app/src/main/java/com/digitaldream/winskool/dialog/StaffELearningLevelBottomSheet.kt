package com.digitaldream.winskool.dialog

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.StaffELearningContentDashboardActivity
import com.digitaldream.winskool.adapters.GenericAdapter
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.models.CourseTable
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.j256.ormlite.dao.DaoManager

private const val ARG_PARAM1 = "param1"

class StaffELearningLevelBottomSheet : BottomSheetDialogFragment() {

    private lateinit var backBtn: ImageButton
    private lateinit var levelRecyclerView: RecyclerView

    private lateinit var levelAdapter: GenericAdapter<CourseTable>
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

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

        sharedPreferences = requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)

        backBtn.setOnClickListener { dismiss() }
    }

    private fun loadLevels() {
        try {
            val levelDao =
                DaoManager.createDao(databaseHelper.connectionSource, CourseTable::class.java)
            levelTable = levelDao.queryBuilder().where().eq("courseId", courseId ?: "").query()
            levelTable.sortBy { it.levelName }

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

            sharedPreferences.edit().apply {
                putString("course_name", itemPosition.courseName)
                putString("courseId", itemPosition.courseId)
                putString("level", itemPosition.levelId)
            }.apply()

            getCourseOutline(itemPosition.levelId, itemPosition.courseName)
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

    private fun getCourseOutline(levelId: String, courseName: String) {
        val term = sharedPreferences.getString("term", "")

        val url = "${getString(R.string.base_url)}/getOutlineList" +
                ".php?course=$courseId&level=$levelId&term=$term"

        sendRequestToServer(
            Request.Method.GET, url, requireContext(), null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    parseResponseJson(response, levelId, courseName)
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(
                        requireContext(), "Oops! Something went wrong. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun parseResponseJson(response: String, levelId: String, courseName: String) {
        if (response != "[]") {
            sharedPreferences.edit().putString("outline", response).apply()
            startActivity(Intent(requireContext(), StaffELearningContentDashboardActivity::class.java))
            dismiss()
        } else {
            StaffELearningCreateCourseOutlineDialogFragment{
                getCourseOutline(levelId, courseName)
            }.show(parentFragmentManager, "")
        }
    }
}