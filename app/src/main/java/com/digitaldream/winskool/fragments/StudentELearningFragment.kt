package com.digitaldream.winskool.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
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
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.StudentELearningActivity
import com.digitaldream.winskool.activities.StudentELearningCourseOutlineActivity
import com.digitaldream.winskool.adapters.GenericAdapter
import com.digitaldream.winskool.models.CourseOutlineTable
import com.digitaldream.winskool.models.RecentActivityModel
import com.digitaldream.winskool.models.UpcomingQuizModel
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.formatDate2
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * ### Student E-Learning Fragment Documentation
 *
 * #### Overview
 *
 * The `StudentELearningFragment` class is a Fragment in an Android application designed for student e-learning experiences. It displays information such as upcoming quizzes, recent activities, and available courses for a specific class and term.
 *
 * #### Class Structure
 *
 * The class is structured into several sections, each responsible for a specific functionality:
 *
 * 1. **Properties:**
 *    - `courseRecyclerView`: Displays a grid of available courses.
 *    - `upcomingQuizViewPager`: Utilized for displaying upcoming quizzes in a swipe-able format.
 *    - `recentActivityRecyclerView`: Shows recent activities in a horizontal scrollable layout.
 *    - `upcomingQuizTabLayout`: Provides tabs for the upcoming quizzes.
 *    - `emptyCourseTxt`: A TextView to display a message when no courses are available.
 *    - `courseAdapter`, `upcomingQuizAdapter`, `recentActivityAdapter`: Adapters for RecyclerViews to handle data binding.
 *    - `sharedPreferences`: Used for storing and retrieving user-specific data.
 *    - `autoSlidingJob`: Manages the automatic sliding of upcoming quizzes.
 *    - `delayMillis`: The delay in milliseconds for the auto-sliding feature.
 *    - Lists to store data for upcoming quizzes, recent activities, and course outlines.
 *    - `levelId`, `classId`, `term`: Variables to store user-specific information.
 *
 * 2. **Fragment Lifecycle Methods:**
 *    - `onCreateView`: Inflates the layout for the fragment.
 *    - `onViewCreated`: Initializes views and triggers the retrieval of home content.
 *
 * 3. **View Setup:**
 *    - `setUpViews`: Initializes UI components like the toolbar, RecyclerViews, and TabLayout.
 *
 * 4. **Auto Sliding Feature:**
 *    - `startAutoSliding`: Initiates a coroutine job for auto-sliding through upcoming quizzes.
 *    - `stopAutoSliding`: Cancels the auto-sliding job.
 *
 * 5. **Network Request and Data Parsing:**
 *    - `getHomeContents`: Sends a network request to fetch home content (quizzes, comments, courses).
 *    - `parseUpcomingQuizJson`, `parseRecentActivityJson`, `parseCourseJson`: Parse JSON responses and populate respective data lists.
 *
 * 6. **Adapter Setup:**
 *    - `setUpCourseAdapter`, `setUpRecentActivityAdapter`, `setUpUpcomingQuizAdapter`: Set up adapters for RecyclerViews.
 *
 * 7. **RecyclerView Setup:**
 *    - `setUpRecentActivityRecyclerView`, `setUpCourseRecyclerView`: Configures RecyclerView layouts and adapters.
 *
 * 8. **Content Request and Activity Launch:**
 *    - `sendContentRequest`: Sends a network request for specific content.
 *    - `launchActivity`: Launches a new activity with content received from the server.
 *    - `handleQuizRecentActivityClick`: Handles user clicks on quizzes or recent activities.
 *
 * 9. **URL Generation:**
 *    - `getUrl`: Generates a URL for fetching specific content based on ID and type.
 *
 * 10. **Lifecycle Management:**
 *     - `onResume`: Invoked when the fragment is resumed, starts the auto-sliding feature.
 *     - `onPause`: Invoked when the fragment is paused, stops the auto-sliding feature.
 *
 * #### Usage
 *
 * To use this fragment, include it in an activity or navigation component. Ensure that the necessary layout resources and dependencies are configured. Additionally, manage the data source and handle any specific actions for user interactions.
 *
 * #### Dependencies
 *
 * This class relies on Android standard libraries and Volley for network requests.
 *
 * #### Notes
 *
 * Ensure that the required permissions and internet connectivity are available for proper functionality.
 *
 * #### Conclusion
 *
 * The `StudentELearningFragment` enhances the student e-learning experience by providing a user-friendly interface to access upcoming quizzes, recent activities, and available courses.
 */


class StudentELearningFragment : Fragment() {

    private lateinit var courseRecyclerView: RecyclerView
    private lateinit var upcomingQuizViewPager: ViewPager2
    private lateinit var recentActivityRecyclerView: RecyclerView
    private lateinit var upcomingQuizTabLayout: TabLayout
    private lateinit var emptyCourseTxt: TextView

    private lateinit var courseAdapter: GenericAdapter<CourseOutlineTable>
    private lateinit var upcomingQuizAdapter: GenericAdapter<UpcomingQuizModel>
    private lateinit var recentActivityAdapter: GenericAdapter<RecentActivityModel>

    private lateinit var sharedPreferences: SharedPreferences
    private var autoSlidingJob: Job? = null
    private val delayMillis = 3000L

    private val upcomingQuizList = mutableListOf<UpcomingQuizModel>()
    private val recentActivityList = mutableListOf<RecentActivityModel>()

    private var outlineTableList = mutableListOf<CourseOutlineTable>()
    private var levelId: String? = null
    private var classId: String? = null
    private var term: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_e_learning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        getHomeContents()
    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = view.findViewById(R.id.toolbar)
            upcomingQuizViewPager = findViewById(R.id.quizViewPager)
            upcomingQuizTabLayout = findViewById(R.id.quizTabLayout)
            recentActivityRecyclerView = findViewById(R.id.recentActivityRecyclerView)
            courseRecyclerView = view.findViewById(R.id.courseRecyclerView)
            emptyCourseTxt = view.findViewById(R.id.emptyCourseTxt)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = "Classroom"
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener {
                @Suppress("DEPRECATION")
                requireActivity().onBackPressed()
            }
        }

        sharedPreferences = requireActivity().getSharedPreferences(
            "loginDetail", MODE_PRIVATE
        )

        levelId = sharedPreferences.getString("level", "")
        classId = sharedPreferences.getString("classId", "")
        term = sharedPreferences.getString("term", "")
    }


    private fun startAutoSliding() {
        autoSlidingJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(delayMillis)

                withContext(Dispatchers.Main) {
                    // Upcoming quiz
                    if (upcomingQuizViewPager.currentItem < upcomingQuizList.size - 1) {
                        upcomingQuizViewPager.currentItem++
                    } else {
                        upcomingQuizViewPager.currentItem = 0
                    }

                    val position = upcomingQuizViewPager.currentItem
                    upcomingQuizTabLayout.selectTab(upcomingQuizTabLayout.getTabAt(position))
                }
            }
        }
    }

    private fun stopAutoSliding() {
        autoSlidingJob?.cancel()
        autoSlidingJob = null
    }

    private fun getHomeContents() {
        val url = "${requireActivity().getString(R.string.base_url)}/getLearningHome.php?" +
                "level=$levelId&class=$classId&term=$term"

        sendRequestToServer(
            Request.Method.GET,
            url,
            requireContext(),
            null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        with(JSONObject(response)) {
                            val quiz = getString("quiz")
                            val comment = getString("comment")
                            val course = getString("courses")

                            if (quiz != "[]") {
                                parseUpcomingQuizJson(JSONArray(quiz))
                            }

                            if (comment != "[]") {
                                parseRecentActivityJson(JSONArray(comment))
                            }

                            if (course != "[]") {
                                parseCourseJson(JSONArray(course))
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: VolleyError) {
                }
            })
    }


    private fun parseUpcomingQuizJson(quiz: JSONArray) {
        with(quiz) {
            for (i in 0 until length()) {
                getJSONObject(i).let {
                    val id = it.getString("id")
                    val title = it.getString("title")
                    val courseId = it.getString("course_id")
                    val courseName = it.getString("course_name")
                    val date = it.getString("end_date")

                    val upcomingQuizModel =
                        UpcomingQuizModel(
                            id, title, date, "2", courseName,
                            courseId, levelId ?: "", term ?: ""
                        )

                    upcomingQuizList.add(upcomingQuizModel)
                }
            }
        }

        setUpUpcomingQuizAdapter()
    }

    private fun parseRecentActivityJson(recentActivity: JSONArray) {
        with(recentActivity) {
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

                        recentActivityList.add(recentActivityModel)
                    }
                }
            }
        }

        setUpRecentActivityAdapter()
    }

    private fun parseCourseJson(course: JSONArray) {
        with(course) {
            for (i in 0 until length()) {
                getJSONObject(i).let {
                    val id = it.getString("course_id")
                    val name = it.getString("course_name")

                    val outlineTable = CourseOutlineTable().apply {
                        courseName = name
                        courseId = id
                    }

                    outlineTableList.add(outlineTable)
                }
            }
        }

        setUpCourseAdapter()
    }

    private fun setUpCourseAdapter() {
        val colors = intArrayOf(
            R.color.test_color_2, R.color.color_1, R.color.test_color_1,
            R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_6, R.color.color_8, R.color.test_color_5,
            R.color.test_color_3
        )

        courseAdapter = GenericAdapter(
            outlineTableList,
            R.layout.item_student_e_learning_course_layout,
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
                putString("course_name", outlineTableList[position].courseName)
                putString("courseId", outlineTableList[position].courseId)
            }.apply()

            startActivity(
                Intent(requireActivity(), StudentELearningCourseOutlineActivity::class.java)
            )
        }

        setUpCourseRecyclerView()
    }

    private fun setUpRecentActivityAdapter() {
        recentActivityAdapter = GenericAdapter(
            recentActivityList,
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
            val recentItem = recentActivityList[position]

            handleQuizRecentActivityClick(getUrl(recentItem.id, recentItem.type), "material")
        }

        setUpRecentActivityRecyclerView()
    }


    private fun setUpUpcomingQuizAdapter() {
        upcomingQuizAdapter = GenericAdapter(
            upcomingQuizList,
            R.layout.item_up_coming_quiz_layout,
            bindItem = { itemView, model, _ ->
                val courseNameTxt: TextView = itemView.findViewById(R.id.courseNameTxt)
                val quizTitleTxt: TextView = itemView.findViewById(R.id.titleTxt)
                val dueDateTxt: TextView = itemView.findViewById(R.id.dateTxt)

                courseNameTxt.text = capitaliseFirstLetter(model.courseName)
                quizTitleTxt.text = model.title
                dueDateTxt.text = formatDate2(model.date, "date time")
            }
        ) { position ->
            val quizItem = upcomingQuizList[position]

            handleQuizRecentActivityClick(getUrl(quizItem.id, quizItem.type), "question")
        }

        upcomingQuizViewPager.adapter = upcomingQuizAdapter

        TabLayoutMediator(upcomingQuizTabLayout, upcomingQuizViewPager) { _, _ -> }.attach()
    }

    private fun setUpRecentActivityRecyclerView() {
        recentActivityRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = recentActivityAdapter
        }
    }

    private fun setUpCourseRecyclerView() {
        courseRecyclerView.hasFixedSize()
        courseRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        courseRecyclerView.adapter = courseAdapter
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

    private fun handleQuizRecentActivityClick(url: String, from: String) {
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