package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.StudentELearningActivity
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.dialog.StaffELearningLevelBottomSheet
import com.digitaldream.linkskool.models.CourseTable
import com.digitaldream.linkskool.models.RecentActivityModel
import com.digitaldream.linkskool.models.UpcomingQuizModel
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.j256.ormlite.dao.DaoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class StaffELearningFragment : Fragment() {

    private lateinit var submissionViewPager: ViewPager2
    private lateinit var submissionTabLayout: TabLayout
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var courseRecyclerView: RecyclerView

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var courseAdapter: GenericAdapter<CourseTable>
    private lateinit var commentAdapter: GenericAdapter<RecentActivityModel>
    private lateinit var submissionAdapter: GenericAdapter<UpcomingQuizModel>

    private var courseTable = mutableListOf<CourseTable>()
    private val commentList = mutableListOf<RecentActivityModel>()
    private val submissionList = mutableListOf<UpcomingQuizModel>()

    private var userId: String? = null
    private var term: String? = null
    private var year: String? = null
    private var autoSlidingJob: Job? = null
    private val delayMillis = 3000L

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

        getHomeContents()
    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            submissionViewPager = findViewById(R.id.submissionViewPager)
            submissionTabLayout = findViewById(R.id.submissionTabLayout)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            courseRecyclerView = findViewById(R.id.courseRecyclerView)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = "Classroom"
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

            databaseHelper = DatabaseHelper(requireContext())

            val sharedPreferences = requireActivity().getSharedPreferences(
                "loginDetail", Context.MODE_PRIVATE
            )

            with(sharedPreferences) {
                term = getString("term", "")
                year = getString("school_year", "")
                userId = getString("user_id", "")
            }
        }
    }

    private fun getHomeContents() {
        val url = "${requireActivity().getString(R.string.base_url)}/getStaffLearning.php?" +
                "id=$userId&year=$year&term=$term"

        sendRequestToServer(
            Request.Method.GET,
            url,
            requireContext(),
            null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        with(JSONObject(response)) {
                            val comment = getString("comment")

                            if (comment != "[]") {
                                parseCommentJson(JSONArray(comment))
                            }

                            parseCourseJson()

                            setUpSubmissionAdapter()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: VolleyError) {
                }
            })
    }

    private fun parseCommentJson(comments: JSONArray) {
        with(comments) {
            for (i in 0 until length()) {
                getJSONObject(i).let {
                    val id = it.getString("content_id")
                    val userName = it.getString("author_name")
                    val date = it.getString("upload_date")
                    val description = it.getString("content_title")
                    val contentType = it.getString("content_type")
                    val courseName = it.getString("course_name")

                    if (contentType.isNotEmpty()) {
                        val recentActivityModel = RecentActivityModel(
                            id, userName, description, courseName,
                            date, contentType
                        )

                        commentList.add(recentActivityModel)
                    }
                }
            }
        }

        setUpCommentAdapter()
    }

    private fun parseCourseJson() {
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


    private fun setUpSubmissionAdapter() {
        submissionList.apply {
            add(
                UpcomingQuizModel(
                    "id",
                    "Assignment submitted",
                    "24 Oct, 2023",
                    "Somto Obi",
                    "English language",
                    "",
                    "",
                    ""
                )
            )

            add(
                UpcomingQuizModel(
                    "id",
                    "Assignment submitted",
                    "24 Oct, 2023",
                    "Toochi Obi",
                    "Maths",
                    "",
                    "",
                    ""
                )
            )
        }


        submissionAdapter = GenericAdapter(
            submissionList,
            R.layout.item_submission_layout,
            bindItem = { itemView, model, _ ->
                val titleTxt: TextView = itemView.findViewById(R.id.titleTxt)
                val studentNameTxt: TextView = itemView.findViewById(R.id.studentNameTxt)
                val submissionDateTxt: TextView = itemView.findViewById(R.id.submissionDateTxt)
                val courseNameTxt: TextView = itemView.findViewById(R.id.courseNameTxt)

                titleTxt.text = model.title
                studentNameTxt.text = model.type
                courseNameTxt.text = model.courseName
                submissionDateTxt.text = model.date
            }
        ) { position ->
            val submissionItem = submissionList[position]

        }

        submissionViewPager.adapter = submissionAdapter

        TabLayoutMediator(submissionTabLayout, submissionViewPager) { _, _ -> }.attach()
    }


    private fun startAutoSliding() {
        autoSlidingJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(delayMillis)

                withContext(Dispatchers.Main) {
                    // Upcoming quiz
                    if (submissionViewPager.currentItem < submissionList.size - 1) {
                        submissionViewPager.currentItem++
                    } else {
                        submissionViewPager.currentItem = 0
                    }

                    val position = submissionViewPager.currentItem
                    submissionTabLayout.selectTab(submissionTabLayout.getTabAt(position))
                }
            }
        }
    }

    private fun stopAutoSliding() {
        autoSlidingJob?.cancel()
        autoSlidingJob = null
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
            StaffELearningLevelBottomSheet
                .newInstance(courseTable[position].courseId)
                .show(parentFragmentManager, "")
        }

        setUpCourseRecyclerView()
    }

    private fun setUpCommentAdapter() {
        commentAdapter = GenericAdapter(
            commentList,
            R.layout.item_recent_activity_layout,
            bindItem = { itemView, model, _ ->
                val userNameTxt: TextView = itemView.findViewById(R.id.userNameTxt)
                val commentTxt: TextView = itemView.findViewById(R.id.commentTxt)
                val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)

                userNameTxt.text = model.userName
                dateTxt.text = formatDate2(model.date, "custom")
                "Commented on ${model.description}".let { commentTxt.text = it }
            }
        ) { position ->
            val recentItem = commentList[position]

            /*  handleSubmissionAndCommentClick(getUrl(recentItem.id, recentItem.type), "material")*/
        }

        setUpCommentRecyclerView()
    }


    private fun setUpCourseRecyclerView() {
        courseRecyclerView.hasFixedSize()
        courseRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        courseRecyclerView.adapter = courseAdapter
    }

    private fun setUpCommentRecyclerView() {
        commentRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = commentAdapter
        }
    }

    private fun sendContentRequest(url: String, onResponse: (String) -> Unit) {
        sendRequestToServer(Request.Method.GET, url, requireContext(), null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    onResponse(response)
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(
                        context, "Something went wrong please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun launchActivity(from: String, response: String) {
        startActivity(
            Intent(requireActivity(), StudentELearningActivity::class.java)
                .putExtra("from", from)
                .putExtra("json", response)
        )
    }

    private fun handleSubmissionAndCommentClick(url: String, from: String) {
        sendContentRequest(url) { response ->
            launchActivity(from, response)
        }
    }

    private fun getUrl(id: String, type: String) =
        "${requireActivity().getString(R.string.base_url)}/getContent.php?id=$id&type=$type"


    override fun onResume() {
        super.onResume()
        startAutoSliding()
    }

    override fun onPause() {
        super.onPause()
        stopAutoSliding()
    }

}
