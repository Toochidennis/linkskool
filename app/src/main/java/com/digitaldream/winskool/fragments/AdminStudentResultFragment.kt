package com.digitaldream.winskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
import com.digitaldream.winskool.dialog.AdminResultStudentNamesDialog
import com.digitaldream.winskool.dialog.OnInputListener
import com.digitaldream.winskool.models.AdminStudentResultFragmentModel
import com.digitaldream.winskool.models.ChartValue
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.StudentTable
import com.digitaldream.winskool.utils.ColumnChart
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import org.json.JSONObject
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminStudentResultFragment : Fragment(), OnItemClickListener {

    private lateinit var mRefreshBtn: Button
    private lateinit var mStudentNameBtn: Button
    private lateinit var mAdapter: SectionedRecyclerViewAdapter

    private var mStudentList = mutableListOf<StudentTable>()
    private var mTermList = mutableListOf<AdminStudentResultFragmentModel>()
    private var mStudentId: String? = null
    private var mClassId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mStudentId = it.getString(ARG_PARAM1)
            mClassId = it.getString(ARG_PARAM2)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminStudentResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_student_result, container, false)

        val backBtn: ImageButton = view.findViewById(R.id.back_btn)
        val title: TextView = view.findViewById(R.id.toolbar_text)
        mStudentNameBtn = view.findViewById(R.id.student_name_btn)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)

        "Student Result".also { title.text = it }

        backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        mAdapter = SectionedRecyclerViewAdapter()

        studentProfile(view, mStudentId!!)

        changeStudent(view)
        refresh(view)

        return view
    }

    private fun changeStudent(sView: View) {
        mStudentNameBtn.setOnClickListener {
            AdminResultStudentNamesDialog(requireContext(), mClassId!!, "student_result", object :
                OnInputListener {
                override fun sendInput(input: String) {
                    studentProfile(sView, input)
                }

                override fun sendId(levelId: String) {

                }
            }).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun studentProfile(sView: View, sStudentId: String) {
        val profileImage: ImageView = sView.findViewById(R.id.profile_image)
        val studentName: TextView = sView.findViewById(R.id.student_name)
        val studentClass: TextView = sView.findViewById(R.id.student_class)
        val studentId: TextView = sView.findViewById(R.id.student_id)

        try {
            mStudentList.clear()
            val databaseHelper = DatabaseHelper(requireContext())
            val studentDao: Dao<StudentTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, StudentTable::class.java
            )
            mStudentList = studentDao.queryBuilder().where().eq("studentId", sStudentId).query()

            val classDao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, ClassNameTable::class.java
            )
            val classList = classDao.queryBuilder().where().eq(
                "classId", mStudentList[0].studentClass
            ).query()


            if (mStudentList.isNotEmpty() && classList.isNotEmpty()) {
                val model = mStudentList[0]
                val surName = model.studentSurname
                val middleName = model.studentMiddlename
                val firstName = model.studentFirstname
                val name = "$surName $middleName $firstName"

                studentName.text = capitaliseFirstLetter(name)
                studentId.text = model.studentReg_no
                studentClass.text = classList[0].className
                mStudentNameBtn.text = classList[0].className
            }

            getTerms(sView, sStudentId)


        } catch (sE: java.lang.Exception) {
            sE.printStackTrace()
        }

    }

    private fun getTerms(sView: View, sStudentId: String) {
        val recyclerView: RecyclerView = sView.findViewById(R.id.term_recycler)
        val errorMessage: TextView = sView.findViewById(R.id.term_error_message)
        val errorImage: ImageView = sView.findViewById(R.id.error_image)

        val url = "${getString(R.string.base_url)}/studentTerms.php"
        val hashMap = hashMapOf<String, String>()
        hashMap["id"] = sStudentId

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
                            while (key.hasNext()) {
                                val year = key.next()
                                val sessionObject = termObject.getJSONObject(year)

                                val previousYear = year.toInt() - 1
                                val session = String.format(
                                    Locale.getDefault(), "%d/%s",
                                    previousYear, year
                                )

                                for (i in sessionObject.keys()) {
                                    val term = when (i) {
                                        "1" -> "First Term"
                                        "2" -> "Second Term"
                                        "3" -> "Third Term"
                                        else -> ""
                                    }
                                    mTermList.add(
                                        AdminStudentResultFragmentModel(
                                            term, year,
                                            sessionObject.getString(i)
                                        )
                                    )

                                    graphList.add(
                                        ChartValue(
                                            term,
                                            sessionObject.getString(i).toDouble()
                                        )
                                    )
                                }

                                mTermList.sortBy { t -> t.term }
                                graphList.sortBy { v -> v.y }

                                mAdapter.addSection(
                                    AdminStudentResultAdapter(
                                        mTermList,
                                        "$session Session", this@AdminStudentResultFragment
                                    )
                                )

                            }

                            graph(sView, graphList)


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

                        }

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        recyclerView.isVisible = false
                        errorMessage.isVisible = true
                        errorImage.isVisible = true
                        mRefreshBtn.isVisible = false
                    }

                }

                override fun onError(error: VolleyError) {
                    recyclerView.isVisible = false
                    errorMessage.isVisible = true
                    errorMessage.text = getString(R.string.can_not_retrieve)
                    errorImage.isVisible = true
                    mRefreshBtn.isVisible = true
                }
            })

    }


    private fun graph(sView: View, data: ArrayList<ChartValue>) {
        val webView: ColumnChart = sView.findViewById(R.id.chart)
        webView.setChartData(data)
    }


    private fun refresh(sView: View) {
        mRefreshBtn.setOnClickListener {
            studentProfile(sView, mStudentId!!)
        }
    }


    override fun onItemClick(position: Int) {
        val studentList = mStudentList[0]
        val termList = mTermList[position - 1]

        startActivity(
            Intent(requireContext(), ViewStudentResultActivity::class.java)
                .putExtra("level", studentList.studentLevel)
                .putExtra("term", termList.term)
                .putExtra("studentId", studentList.studentId)
                .putExtra("year", termList.year)
                .putExtra("classId", studentList.studentClass)
        )
    }

}


/*  @SuppressLint("SetJavaScriptEnabled")
    private fun testWebChart(
        sView: View,
    ) {
        val webView: WebView = sView.findViewById(R.id.chart)

        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
        }

        val assetManager = requireActivity().assets
        val inputStream = assetManager.open("linksool.html")
        val bytes: ByteArray = readHtmlFile(inputStream)
        val content = String(bytes, Charset.forName("UTF-8"))

        // val formattedContent = String.format("%s, %d, %d, %d", content, 50, 60, 55)
        webView.loadDataWithBaseURL(
            ASSET_PATH, content, "text/html", "utf-8",
            null
        )
        // webView.loadUrl("file:///android_asset/linkskool_chart.html")
        webView.requestFocusFromTouch()

    }

     private fun Date.convertCurrentDateToChartDate(dataFormat:String): String {
        var day: String
        Calendar.getInstance().apply {
            time = this@convertCurrentDateToChartDate
            day = SimpleDateFormat(dataFormat, Locale.ENGLISH).apply {
                timeZone = TimeZone.getDefault()
            }.format(time)
        }
        return day
    }


    private fun readHtmlFile(inputStream: InputStream): ByteArray {
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var count: Int
        while (inputStream.read(buffer).also { count = it } != -1) {
            out.write(buffer, 0, count)
        }
        return out.toByteArray()
    }

*/
