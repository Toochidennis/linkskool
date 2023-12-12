package com.digitaldream.winskool.fragments

import android.app.DatePickerDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.AttendanceDetailsActivity
import com.digitaldream.winskool.activities.StaffCourseAttendanceActivity
import com.digitaldream.winskool.adapters.GenericAdapter2
import com.digitaldream.winskool.models.StaffAttendanceModel
import com.digitaldream.winskool.utils.FunctionUtils
import com.digitaldream.winskool.utils.FunctionUtils.formatDate2
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import java.util.Calendar


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class CourseAttendanceFragment : Fragment() {


    private lateinit var courseAttendanceRecyclerView: RecyclerView
    private lateinit var errorMessageTxt: TextView
    private lateinit var attendanceBtn: FloatingActionButton
    private lateinit var takeAttendanceBtn: FloatingActionButton
    private lateinit var filterAttendanceBtn: FloatingActionButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var attendanceAdapter: GenericAdapter2<StaffAttendanceModel>
    private val attendanceList = mutableListOf<StaffAttendanceModel>()

    private var classId: String? = null
    private var courseId: String? = null
    private var courseName: String? = null
    private var isOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            courseId = it.getString(ARG_PARAM1)
            classId = it.getString(ARG_PARAM2)
            courseName = it.getString(ARG_PARAM3)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(courseId: String, classId: String, courseName: String) =
            CourseAttendanceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, courseId)
                    putString(ARG_PARAM2, classId)
                    putString(ARG_PARAM3, courseName)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_course_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        initData()
    }

    private fun initViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            courseAttendanceRecyclerView = findViewById(R.id.courseAttendanceRecyclerView)
            attendanceBtn = findViewById(R.id.attendanceBtn)
            takeAttendanceBtn = findViewById(R.id.takeAttendanceBtn)
            filterAttendanceBtn = findViewById(R.id.filterAttendanceBtn)
            swipeRefreshLayout = findViewById(R.id.swipeRefresh)
            errorMessageTxt = findViewById(R.id.errorMessageTxt)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = "$courseName"
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun initData() {
        getAttendance()
        handleBtnActions()
        refreshAttendance()
    }


    private fun getAttendance() {
        val url = "${requireContext().getString(R.string.base_url)}/getAttendanceList.php"
        val hashMap = hashMapOf<String, String>()

        val sharedPreferences = requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)

        with(sharedPreferences) {
            val term = getString("term", "")
            val year = getString("school_year", "")

            hashMap.apply {
                put("year", year ?: "")
                put("class", classId ?: "")
                put("course", courseId ?: "")
                put("term", term ?: "")
            }
        }

        sendRequestToServer(Request.Method.POST, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response != "[]") {
                        errorMessageTxt.isVisible = false
                        parseResponse(response)
                    } else {
                        errorMessageTxt.isVisible = true
                    }
                }

                override fun onError(error: VolleyError) {
                    errorMessageTxt.apply {
                        text = requireActivity().getString(R.string.no_internet)
                        isVisible = true
                    }
                }
            })
    }

    private fun parseResponse(response: String) {
        try {
            attendanceList.clear()

            with(JSONArray(response)) {
                for (i in 0 until length()) {
                    getJSONObject(i).let {
                        attendanceList.add(
                            StaffAttendanceModel(
                                it.getString("date"),
                                it.getString("count")
                            )
                        )
                    }
                }
            }

            setUpAdapter()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpAdapter() {
        attendanceAdapter = GenericAdapter2(
            attendanceList,
            R.layout.item_course_attendance,
            bindItem = { itemView, model, _ ->
                val countHolder: LinearLayout = itemView.findViewById(R.id.countHolder)
                val countTxt: TextView = itemView.findViewById(R.id.countTxt)
                val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)

                countTxt.text = model.attendanceCount
                dateTxt.text = formatDate2(model.attendanceDate, "full")

                FunctionUtils.getRandomColor(countHolder)

                itemView.setOnClickListener {
                    launchActivity(AttendanceDetailsActivity::class.java, model.attendanceDate)
                }
            }
        )

        setUpRecyclerView()
    }

    private fun launchActivity(activity: Class<*>, attendanceDate: String = "") {
        startActivity(
            Intent(requireContext(), activity)
                .putExtra("class_name", courseName)
                .putExtra("course_id", courseId)
                .putExtra("class_id", classId)
                .putExtra("date", attendanceDate)
        )
    }

    private fun setUpRecyclerView() {
        courseAttendanceRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = attendanceAdapter
        }
    }


    private fun refreshAttendance() {
        swipeRefreshLayout.apply {
            setOnRefreshListener {
                getAttendance()
                isRefreshing = false
            }
        }
    }

    private fun handleBtnAnimation() {
        val fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        val fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)
        val rotateForward = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_forward)
        val rotateBackward = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_backward)

        if (isOpen) {
            attendanceBtn.startAnimation(rotateBackward)
            setVisibilityAndClickable(takeAttendanceBtn, fabClose, false)
            setVisibilityAndClickable(filterAttendanceBtn, fabClose, false)
        } else {
            attendanceBtn.startAnimation(rotateForward)
            setVisibilityAndClickable(takeAttendanceBtn, fabOpen, true)
            setVisibilityAndClickable(filterAttendanceBtn, fabOpen, true)
        }

        isOpen = !isOpen
    }

    private fun setVisibilityAndClickable(view: View, animation: Animation, isClickable: Boolean) {
        view.apply {
            startAnimation(animation)
            isVisible = true
            this.isClickable = isClickable
        }

    }

    private fun handleBtnActions() {
        attendanceBtn.setOnClickListener { handleBtnAnimation() }

        filterAttendanceBtn.setOnClickListener {
            showDateDialog()
        }

        takeAttendanceBtn.setOnClickListener {
            launchActivity(StaffCourseAttendanceActivity::class.java)
        }
    }

    private fun showDateDialog() {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]

        DatePickerDialog(
            requireContext(),
            { _, sYear, sMonth, dayOfMonth ->
                val currentDate = "$sYear-${sMonth + 1}-$dayOfMonth:00:00:00"

                launchActivity(AttendanceDetailsActivity::class.java, currentDate)
            },
            year, month, day
        ).show()
    }

}

/* public void getPreviousCourseRegistration() {

    String url = getString(R.string.base_url) + "/getCourseRegistration.php";
    StringRequest stringRequest = new StringRequest(Request.Method.POST,
            url,
            response -> {
                Timber.tag("response").d(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String attendance = jsonObject.getString("attendance");
                    JSONArray jsonArray = new JSONArray(attendance);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String responseId = object.getString("id");
                        mList.setResponseId(responseId);
                    }

                } catch (JSONException sE) {
                    sE.printStackTrace();
                }

            }, error -> {
        error.printStackTrace();
        Toast.makeText(this, "Something went wrong!",
                Toast.LENGTH_SHORT).show();

    }) {
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> stringMap = new HashMap<>();
            stringMap.put("class", mStudentClassId);
            stringMap.put("course", mCourseId);
            stringMap.put("attendance", "1");
            stringMap.put("date", getDate());
            stringMap.put("_db", db);
            return stringMap;
        }
    };

    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}*/
