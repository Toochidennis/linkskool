package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.ViewStudentResultActivity
import com.digitaldream.linkskool.adapters.AdminStudentResultAdapter
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.models.AdminStudentResultFragmentModel
import com.digitaldream.linkskool.models.ChartValue
import com.digitaldream.linkskool.utils.ColumnChart
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import org.json.JSONObject
import java.util.*

class StudentResultFragment : Fragment(), OnItemClickListener {

    //declare variables
    private lateinit var mRefreshBtn: Button
    private lateinit var mAdapter: SectionedRecyclerViewAdapter
    private val mTermList = mutableListOf<AdminStudentResultFragmentModel>()

    private var mStudentId: String? = null
    private var mRegistrationNumber: String? = null
    private var mStudentName: String? = null
    private var mClassName: String? = null
    private var mClassId: String? = null
    private var mLevelId: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_student_result, container, false)

        view.findViewById<Toolbar?>(R.id.toolbar).apply {
            title = "Result"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener {
                @Suppress("DEPRECATION")
                requireActivity().onBackPressed()
            }
            setTitleTextColor(Color.WHITE)
        }
        mRefreshBtn = view.findViewById(R.id.refresh_btn)


        val sharedPreferences = requireActivity().getSharedPreferences(
            "loginDetail", Context.MODE_PRIVATE
        )
        mStudentId = sharedPreferences.getString("user_id", "")
        mRegistrationNumber = sharedPreferences.getString("student_reg_no", "")
        mStudentName = sharedPreferences.getString("user", "")
        mClassName = sharedPreferences.getString("student_class", "")
        mClassId = sharedPreferences.getString("classId", "")
        mLevelId = sharedPreferences.getString("level", "")


        //refresh recyclerview
        refresh(view)

        //provides info about student
        studentProfile(view)

        //initialise adapter
        mAdapter = SectionedRecyclerViewAdapter()

        return view
    }


    private fun studentProfile(sView: View) {
       // val profileImage = sView.findViewById<ImageView>(R.id.profile_image)
        val studentName = sView.findViewById<TextView>(R.id.student_name)
        val studentClass = sView.findViewById<TextView>(R.id.student_class)
        val studentReg = sView.findViewById<TextView>(R.id.student_id)

        studentName.text = capitaliseFirstLetter(mStudentName!!)
        studentClass.text = mClassName
        studentReg.text = mRegistrationNumber

        getTerms(sView)
    }


    private fun getTerms(sView: View) {
        val recyclerView = sView.findViewById<RecyclerView>(R.id.term_recycler)
        val errorView = sView.findViewById<LinearLayout>(R.id.error_view)
        val errorMessage = sView.findViewById<TextView>(R.id.term_error_message)
        val errorImage = sView.findViewById<ImageView>(R.id.error_image)
        val chartView = sView.findViewById<CardView>(R.id.chartView)

        val url = "${getString(R.string.base_url)}/studentTerms.php"
        val hashMap = hashMapOf<String, String>()
        hashMap["id"] = mStudentId!!

        //make request to server
        sendRequestToServer(Request.Method.POST, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        val graphList = arrayListOf<ChartValue>()
                        mAdapter.removeAllSections()
                        mTermList.clear()
                        graphList.clear()

                        JSONObject(response).also {

                            val termObject = it.getJSONObject("terms")
                            val key = termObject.keys()

                            //iterate through the json
                            while (key.hasNext()) {
                                val year = key.next()
                                val sessionObject = termObject.getJSONObject(year)

                                val previousYear = year.toInt() - 1
                                val session = String.format(
                                    Locale.getDefault(),
                                    "%d/%s",
                                    previousYear,
                                    year
                                )

                                //iterate through sessionObject
                                for (i in sessionObject.keys()) {
                                    val term = when (i) {
                                        "1" -> "First Term"
                                        "2" -> "Second Term"
                                        "3" -> "Third Term"
                                        else -> ""
                                    }

                                    //add term values to the list
                                    mTermList.add(
                                        AdminStudentResultFragmentModel(
                                            term, year,
                                            sessionObject.getString(i)
                                        )
                                    )

                                    //add graph values to list
                                    graphList.add(
                                        ChartValue(
                                            term,
                                            sessionObject.getString(i).toDouble()
                                        )
                                    )
                                }

                                //sort data
                                mTermList.sortBy { t -> t.term }
                                graphList.sortBy { v -> v.y }

                                //add term data to adapter
                                mAdapter.addSection(
                                    AdminStudentResultAdapter(
                                        mTermList,
                                        "$session Session", this@StudentResultFragment
                                    )
                                )

                            }

                            // set data to chart
                            graph(sView, graphList)


                            //set adapter to recyclerview
                            recyclerView.apply {
                                hasFixedSize()
                                isAnimating
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = mAdapter
                            }

                            recyclerView.isVisible = true
                            errorMessage.isVisible = false
                            errorImage.isVisible = false
                            mRefreshBtn.isVisible = false
                            errorView.isVisible = false
                            chartView.isVisible = true

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        recyclerView.isVisible = false
                        errorMessage.isVisible = true
                        errorImage.isVisible = true
                        errorView.isVisible = true
                        mRefreshBtn.isVisible = false
                        chartView.isVisible = false
                    }

                }

                override fun onError(error: VolleyError) {
                    recyclerView.isVisible = false
                    errorMessage.isVisible = true
                    errorView.isVisible = true
                    errorMessage.text = getString(R.string.can_not_retrieve)
                    errorImage.isVisible = true
                    mRefreshBtn.isVisible = true
                    chartView.isVisible = false
                }
            })
    }

    private fun graph(sView: View, data: ArrayList<ChartValue>) {
        val webView: ColumnChart = sView.findViewById(R.id.chart)
        webView.setChartData(data)
    }


    private fun refresh(sView: View) {
        mRefreshBtn.setOnClickListener {
            studentProfile(sView)
        }
    }


    override fun onItemClick(position: Int) {
        val termList = mTermList[position - 1]


        //view result on termClick
        startActivity(
            Intent(requireContext(), ViewStudentResultActivity::class.java)
                .putExtra("level", mLevelId)
                .putExtra("term", termList.term)
                .putExtra("studentId", mStudentId)
                .putExtra("year", termList.year)
                .putExtra("classId", mClassId)
        )
    }
}
