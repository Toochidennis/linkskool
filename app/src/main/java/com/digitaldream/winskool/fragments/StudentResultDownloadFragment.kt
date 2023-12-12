package com.digitaldream.winskool.fragments

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
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.ViewStudentResultActivity
import com.digitaldream.winskool.adapters.AdminStudentResultAdapter
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.models.AdminStudentResultFragmentModel
import com.digitaldream.winskool.models.ChartValue
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.utils.ColumnChart
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import org.json.JSONObject
import java.util.*

private const val ARG_PARAM1 = "name"
private const val ARG_PARAM2 = "studentId"
private const val ARG_PARAM3 = "levelId"
private const val ARG_PARAM4 = "classId"
private const val ARG_PARAM5 = "reg_number"


class StudentResultDownloadFragment() : Fragment(), OnItemClickListener {

    //declare variables
    private lateinit var mRefreshBtn: Button
    private lateinit var mAdapter: SectionedRecyclerViewAdapter
    private val mTermList = mutableListOf<AdminStudentResultFragmentModel>()

    // initialise variables
    private var mStudentId: String? = null
    private var mRegistrationNumber: String? = null
    private var mStudentName: String? = null
    private var mClassId: String? = null
    private var mLevelId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get arguments
        arguments?.let {
            mStudentName = it.getString(ARG_PARAM1)
            mStudentId = it.getString(ARG_PARAM2)
            mLevelId = it.getString(ARG_PARAM3)
            mClassId = it.getString(ARG_PARAM4)
            mRegistrationNumber = it.getString(ARG_PARAM5)
        }
    }

    companion object {

        // static constructor instance
        @JvmStatic
        fun newInstance(
            studentName: String,
            studentId: String,
            levelId: String,
            classId: String,
            regNo: String,
        ) = StudentResultDownloadFragment().apply {

            // set arguments
            arguments = Bundle().apply {
                putString(ARG_PARAM1, studentName)
                putString(ARG_PARAM2, studentId)
                putString(ARG_PARAM3, levelId)
                putString(ARG_PARAM4, classId)
                putString(ARG_PARAM5, regNo)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // inflate layout view required
        val view = inflater.inflate(R.layout.fragment_student_result_download, container, false)

        view.findViewById<Toolbar?>(R.id.toolbar).apply {
            title = "Result"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            setTitleTextColor(Color.WHITE)
        }

        mRefreshBtn = view.findViewById(R.id.refresh_btn)

        //refresh recyclerview
        refresh(view)

        //provides info about student
        studentProfile(view)

        //initialise adapter
        mAdapter = SectionedRecyclerViewAdapter()


        return view
    }

    private fun studentProfile(sView: View) {
        val profileImage = sView.findViewById<ImageView>(R.id.profile_image)
        val studentName = sView.findViewById<TextView>(R.id.student_name)
        val studentClass = sView.findViewById<TextView>(R.id.student_class)
        val studentReg = sView.findViewById<TextView>(R.id.student_id)

        try {
            val databaseHelper = DatabaseHelper(requireContext())
            val classDao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, ClassNameTable::class.java
            )
            val classList = classDao.queryBuilder().where().eq(
                "classId", mClassId!!
            ).query()

            studentName.text = capitaliseFirstLetter(mStudentName!!)
            studentClass.text = classList[0].className
            studentReg.text = mRegistrationNumber

            getTerms(sView)

        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                                        "$session Session", this@StudentResultDownloadFragment
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


/*  private void getStudentPreviousResult(final String id){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(StudentResultDownload.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String db = sharedPreferences.getString("db","");
        String url = Login.urlBase+"/jsonResult.php?id="+id+"&_db="+db;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            dialog1.dismiss();
            Log.i("result",response);
            student_name.setText(builder.toString());
            emptyState.setVisibility(View.VISIBLE);
            try {
                JSONArray jsonArray = new JSONArray(response);
                    JSONObject object = jsonArray.getJSONObject(0);
                Iterator<String> keys = object.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String schoolYear = key;
                    if (object.get(key) instanceof JSONObject) {
                        // do something with jsonObject here
                        JSONObject object1 = object.getJSONObject(key);
                        String className = object1.getString("class_name");
                        String levelID = object1.getString("level");
                        String classId = object1.getString("class_id");
                        JSONObject termObject = object1.getJSONObject("terms");
                        String first_term = "";
                        String second_term = "";
                        String third_term = "";

                        if (termObject.has("1")) {
                            first_term = termObject.getString("1");
                            first_term = "1st";

                        }
                        if (termObject.has("2")) {

                            second_term = termObject.getString("2");
                            second_term = "2nd";

                        }
                        if (termObject.has("3")) {
                            third_term = termObject.getString("3");
                            third_term = "3rd";

                        }
                            QueryBuilder<StudentResultDownloadTable, Long> queryBuilder = studentResultDao.queryBuilder();
                            queryBuilder.where().eq("level", levelID);
                            List<StudentResultDownloadTable> levelList = queryBuilder.query();
                            if(levelList.isEmpty()) {

                                StudentResultDownloadTable st = new StudentResultDownloadTable();

                                st.setFirstTerm(first_term);
                                st.setSecondTerm(second_term);
                                st.setThirdTerm(third_term);
                                st.setLevel(levelID);
                                st.setStudentId(studentId);
                                //st.setLevelName(levelList.get(0).getLevelName());
                                st.setLevelName(className);
                                st.setSchoolYear(schoolYear);
                                st.setClassId(classId);

                                studentResultDao.create(st);
                            }

                        }

                }

                studentResultDownloadList = studentResultDao.queryForAll();
                if (!studentResultDownloadList.isEmpty()) {
                    emptyState.setVisibility(View.GONE);
                    unemptyState.setVisibility(View.VISIBLE);
                    StudentResultDownloadAdapter studentResultDownloadAdapter = new StudentResultDownloadAdapter(StudentResultDownload.this, studentResultDownloadList, StudentResultDownload.this);
                    recyclerView.setAdapter(studentResultDownloadAdapter);
                }


            } catch (JSONException | SQLException e) {
                e.printStackTrace();
            }
        }, error -> {
            dialog1.dismiss();
            Toast.makeText(StudentResultDownload.this,"something went wrong",Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
*/
