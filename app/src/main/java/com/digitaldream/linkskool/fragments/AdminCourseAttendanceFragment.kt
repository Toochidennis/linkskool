package com.digitaldream.linkskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.AttendanceActivity
import com.digitaldream.linkskool.adapters.GenericAdapter2
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.CourseTable
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.linkskool.utils.FunctionUtils.getRandomColor
import com.j256.ormlite.dao.DaoManager
import java.sql.SQLException

class AdminCourseAttendanceFragment : Fragment() {


    private lateinit var courseRecyclerView: RecyclerView
    private lateinit var errorMessageTxt: TextView

    private lateinit var courseAdapter: GenericAdapter2<CourseTable>
    private var courseList = mutableListOf<CourseTable>()

    private var classId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            classId = it.getString(ARG_PARAM1)
        }
    }

    companion object {
        private const val ARG_PARAM1 = "1"

        @JvmStatic
        fun newInstance(classId: String) = AdminCourseAttendanceFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, classId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_admin_course_attendance,
            container, false
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        initData
    }

    private fun initViews(view: View) {
        view.apply {
            courseRecyclerView = findViewById(R.id.courseRecyclerView)
            errorMessageTxt = findViewById(R.id.errorMessageTxt)
        }
    }

    private val initData: Unit
        get() {
            val databaseHelper = DatabaseHelper(requireContext())
            try {
                // query courses from database
                val courseDao =
                    DaoManager.createDao(databaseHelper.connectionSource, CourseTable::class.java)
                courseList = courseDao.queryForAll()

                setUpAdapter

            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }


    private val setUpAdapter: Unit
        get() {
            if (courseList.isEmpty()) {
                errorMessageTxt.isVisible = true
            } else {
                courseAdapter = GenericAdapter2(
                    courseList,
                    R.layout.item_admin_course_attendance,
                    bindItem = { itemView, model, _ ->
                        val initialContainer: LinearLayout =
                            itemView.findViewById(R.id.initialContainer)
                        val initialTxt: TextView = itemView.findViewById(R.id.initialTxt)
                        val courseNameTxt: TextView = itemView.findViewById(R.id.courseNameTxt)

                        getRandomColor(initialContainer)

                        initialTxt.text = model.courseName.substring(0, 1).uppercase()
                        courseNameTxt.text = capitaliseFirstLetter(model.courseName)

                        itemView.setOnClickListener {
                            launchActivity(model.courseId, model.courseName)
                        }

                    }
                )

                setUpRecyclerView
                errorMessageTxt.isVisible = false
            }
        }

    private val setUpRecyclerView: Unit
        get() {
            courseRecyclerView.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(requireContext())
                adapter = courseAdapter
            }
        }

    private fun launchActivity(courseId: String, courseName: String) {
        startActivity(
            Intent(requireContext(), AttendanceActivity::class.java)
                .putExtra("from", "course_attendance")
                .putExtra("class_id", classId)
                .putExtra("course_id", courseId)
                .putExtra("course_name", courseName)
        )
    }
}