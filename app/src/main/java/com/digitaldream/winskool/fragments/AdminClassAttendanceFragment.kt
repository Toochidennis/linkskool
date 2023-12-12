package com.digitaldream.winskool.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.AttendanceDetailsActivity
import com.digitaldream.winskool.activities.ClassAttendanceActivity
import com.digitaldream.winskool.adapters.GenericAdapter2
import com.digitaldream.winskool.models.StaffAttendanceModel
import com.digitaldream.winskool.utils.FunctionUtils
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import java.util.Calendar


private const val ARG_PARAM1 = "level_id"
private const val ARG_PARAM2 = "class_id"
private const val ARG_PARAM3 = "class_name"

class AdminClassAttendanceFragment : Fragment() {

    private lateinit var attendanceRecyclerView: RecyclerView
    private lateinit var errorMessageTxt: TextView
    private lateinit var attendanceBtn: FloatingActionButton
    private lateinit var takeAttendanceBtn: FloatingActionButton
    private lateinit var filterAttendanceBtn: FloatingActionButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var attendanceAdapter: GenericAdapter2<StaffAttendanceModel>
    private val attendanceList = mutableListOf<StaffAttendanceModel>()

    private var levelId: String? = null
    private var classId: String? = null
    private var className: String? = null
    private var isOpen = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            levelId = it.getString(ARG_PARAM1)
            classId = it.getString(ARG_PARAM2)
            className = it.getString(ARG_PARAM3)
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(levelId: String, classId: String, className: String) =
            AdminClassAttendanceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, levelId)
                    putString(ARG_PARAM2, classId)
                    putString(ARG_PARAM3, className)
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_admin_class_attendance,
            container, false
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)
        initData()
    }

    private fun setUpViews(view: View) {
        view.apply {
            attendanceRecyclerView = findViewById(R.id.attendanceRecyclerView)
            attendanceBtn = findViewById(R.id.attendanceBtn)
            takeAttendanceBtn = findViewById(R.id.takeAttendanceBtn)
            filterAttendanceBtn = findViewById(R.id.filterAttendanceBtn)
            swipeRefreshLayout = findViewById(R.id.swipeRefresh)
            errorMessageTxt = findViewById(R.id.errorMessageTxt)

        }

    }

    private fun initData() {
        getAttendance()
        handleBtnActions()
        refreshAttendance()
    }


    private fun getAttendance() {
        val sharedPreferences = requireContext().getSharedPreferences(
            "loginDetail",
            Context.MODE_PRIVATE
        )
        val term = sharedPreferences.getString("term", "")
        val year = sharedPreferences.getString("school_year", "")

        val url = "${requireActivity().getString(R.string.base_url)}/getAttendanceList.php"

        val hashMap = hashMapOf<String, String>().apply {
            put("course", "0")
            put("term", term ?: "")
            put("class", classId ?: "")
            put("year", year ?: "")
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

                setUpAdapter()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpAdapter() {
        attendanceAdapter = GenericAdapter2(
            attendanceList,
            R.layout.item_staff_class_attendance,
            bindItem = { itemView, model, _ ->
                val countHolder: LinearLayout = itemView.findViewById(R.id.countHolder)
                val countTxt: TextView = itemView.findViewById(R.id.countTxt)
                val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)

                countTxt.text = model.attendanceCount
                dateTxt.text = FunctionUtils.formatDate2(model.attendanceDate, "full")

                FunctionUtils.getRandomColor(countHolder)

                itemView.setOnClickListener {
                    launchActivity(model.attendanceDate, AttendanceDetailsActivity::class.java)
                }
            }
        )

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        attendanceRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = attendanceAdapter
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
            launchActivity("", ClassAttendanceActivity::class.java)
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

                launchActivity(currentDate, AttendanceDetailsActivity::class.java)
            },
            year, month, day
        ).show()
    }


    private fun launchActivity(date: String, activity: Class<*>) {
        startActivity(
            Intent(requireContext(), activity)
                .putExtra("from", "class")
                .putExtra("class_name", className)
                .putExtra("course_id", "0")
                .putExtra("class_id", classId)
                .putExtra("level_id", levelId)
                .putExtra("date", date)
        )
    }

    private fun refreshAttendance() {
        swipeRefreshLayout.apply {
            setOnRefreshListener {
                getAttendance()
                isRefreshing = false
            }
        }
    }
}