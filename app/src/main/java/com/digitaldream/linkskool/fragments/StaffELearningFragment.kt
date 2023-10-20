package com.digitaldream.linkskool.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.dialog.StaffELearningLevelBottomSheet
import com.digitaldream.linkskool.models.CourseTable
import com.j256.ormlite.dao.DaoManager

class StaffELearningFragment : Fragment() {

    private lateinit var submissionRecyclerView: RecyclerView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var courseRecyclerView: RecyclerView

    private lateinit var menuHost: MenuHost
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var courseAdapter: GenericAdapter<CourseTable>
    private lateinit var sharedPreferences: SharedPreferences

    private var courseTable = mutableListOf<CourseTable>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff_elearning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)

        setUpMenu()

        loadCourses()

    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            submissionRecyclerView = findViewById(R.id.submissionRecyclerView)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            courseRecyclerView = findViewById(R.id.courseRecyclerView)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
            menuHost = requireActivity()

            actionBar?.apply {
                title = "Classroom"
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

            databaseHelper = DatabaseHelper(requireContext())
            sharedPreferences = requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)

        }
    }


    private fun loadCourses() {
        try {
            val courseDao =
                DaoManager.createDao(databaseHelper.connectionSource, CourseTable::class.java)
            courseTable = courseDao.queryBuilder().groupBy("courseId").query()
            courseTable.sortBy { it.courseName }

            setUpCourseAdapter()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setUpCourseAdapter() {
        val colors = intArrayOf(
            R.color.test_color_2, R.color.color_1, R.color.test_color_1,
            R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_6, R.color.color_8, R.color.test_color_5,
            R.color.test_color_3
        )

        courseAdapter = GenericAdapter(
            courseTable,
            R.layout.item_staff_e_learning_course_layout,
            bindItem = { itemView, model, position ->
                val courseCardView: CardView = itemView.findViewById(R.id.courseCardView)
                val initialTxt: TextView = itemView.findViewById(R.id.initialTxt)
                val courseNameTxt: TextView = itemView.findViewById(R.id.courseNameTxt)

                courseNameTxt.text = model.courseName
                val initial = model.courseName.substring(0, 1).uppercase()
                initialTxt.text = initial

                courseCardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        colors[position % colors.size]
                    )
                )
            }
        ) { position ->
            sharedPreferences.edit().apply {
                putString("course_name", courseTable[position].courseName)
                putString("courseId", courseTable[position].courseId)
            }.apply()

            StaffELearningLevelBottomSheet().show(parentFragmentManager, "")
        }

        setUpCourseRecyclerView()
    }


    private fun setUpCourseRecyclerView() {
        courseRecyclerView.hasFixedSize()
        courseRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        courseRecyclerView.adapter = courseAdapter
    }

    private fun setUpMenu() {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        })
    }
}
