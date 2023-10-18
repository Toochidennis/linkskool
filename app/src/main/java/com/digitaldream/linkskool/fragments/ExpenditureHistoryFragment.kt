package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.PaymentActivity
import com.digitaldream.linkskool.adapters.ExpenditureHistoryAdapter
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.dialog.ExpenditureTimeFrameBottomSheet
import com.digitaldream.linkskool.dialog.TermSessionPickerBottomSheet
import com.digitaldream.linkskool.models.ChartModel
import com.digitaldream.linkskool.models.ExpenditureHistoryModel
import com.digitaldream.linkskool.models.TimeFrameDataModel
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.linkskool.utils.FunctionUtils.getDate
import com.digitaldream.linkskool.utils.FunctionUtils.plotLineChart
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenditureHistoryFragment : Fragment(R.layout.fragment_history_expenditure),
    OnItemClickListener {

    private lateinit var mExpenditureView: NestedScrollView
    private lateinit var mExpenditureChart: LinearLayout
    private lateinit var mExpenditureSum: TextView
    private lateinit var mExpenditureCount: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mExpenditureMessage: TextView
    private lateinit var mExpenditureImage: ImageView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button
    private lateinit var mTimeFrameBtn: Button
    private lateinit var mTermBtn: Button
    private lateinit var mOpenBtn: FloatingActionButton
    private lateinit var mAddExpenditureBtn1: Button
    private lateinit var mAddExpenditureBtn2: ImageButton
    private lateinit var mSetupReportBtn1: Button
    private lateinit var mSetupReportBtn2: ImageButton
    private lateinit var mSetupLayout: LinearLayout
    private lateinit var mExpenditureLayout: LinearLayout
    private lateinit var mErrorMessage: TextView


    private lateinit var mExpenditureList: MutableList<ExpenditureHistoryModel>
    private lateinit var mGraphList: MutableList<ChartModel>
    private lateinit var timeFrameDataModel: TimeFrameDataModel

    private var hashMap = hashMapOf<String, String>()

    private var isOpen = false
    private var mTerm: String? = null
    private var mYear: String? = null
    private var mUrl: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find child views in the inflated layout by their IDs
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            mExpenditureView = findViewById(R.id.expenditure_view)
            mExpenditureChart = findViewById(R.id.expenditure_chart)
            mExpenditureSum = findViewById(R.id.expenditure_sum)
            mExpenditureCount = findViewById(R.id.expenditure_count)
            mRecyclerView = findViewById(R.id.expenditure_recycler)
            mExpenditureMessage = findViewById(R.id.expenditure_error_message)
            mExpenditureImage = findViewById(R.id.error_image)
            mErrorView = findViewById(R.id.error_view)
            mRefreshBtn = findViewById(R.id.refresh_btn)
            mTimeFrameBtn = findViewById(R.id.time_frame_btn)
            mTermBtn = findViewById(R.id.term_btn)
            mOpenBtn = findViewById(R.id.open_btn)
            mAddExpenditureBtn1 = findViewById(R.id.add_expenditure_btn1)
            mAddExpenditureBtn2 = findViewById(R.id.add_expenditure_btn2)
            mSetupReportBtn1 = findViewById(R.id.setup_btn1)
            mSetupReportBtn2 = findViewById(R.id.setup_btn2)
            mSetupLayout = findViewById(R.id.setup_layout)
            mExpenditureLayout = findViewById(R.id.add_expenditure_layout)
            mErrorMessage = findViewById(R.id.error_message)


            toolbar.apply {
                title = "Expenditures"
                setNavigationIcon(R.drawable.arrow_left)
                setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            }

        }

        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        mTerm = sharedPreferences.getString("term", "")
        mYear = sharedPreferences.getString("school_year", "")

        mUrl = "${getString(R.string.base_url)}/manageTransactions.php"


        timeFrameDataModel = TimeFrameDataModel { filterTimeFrameData() }

        performButtonsClick()

        getExpenditure()

        refreshData()

        timeFrameTitle(getDate())
        termTitle(mTerm!!, mYear!!)

        mTermBtn.isEnabled = false

    }


    private fun getExpenditure() {

        hashMap.apply {
            put("type", "expenditure")
            put("term", mTerm!!)
            put("year", mYear!!)
            put("startDate", "this month")
            put("endDate", "")
            put("grouping", "")
            put("filter", "")
        }


        sendRequestToServer(Request.Method.POST, mUrl!!, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        initialiseList()

                        JSONObject(response).run {

                            getJSONArray("expenditure").run {
                                getJSONObject(0).run {
                                    val sum = getString("sum")
                                    val count = getString("count")

                                    mExpenditureCount.text = count ?: "0"

                                    setSum(sum)
                                }

                            }


                            if (has("graph")) {
                                val graphArray = getJSONArray("graph")

                                for (i in 0 until graphArray.length()) {
                                    val graphObject = graphArray.getJSONObject(i)
                                    val graphAmount = graphObject.getString("amount")
                                    val graphDate = graphObject.getString("label")

                                    mGraphList.add(ChartModel(graphAmount, graphDate))
                                }

                                mGraphList.sortBy { it.label }
                                setChartData(mGraphList)

                            } else {
                                setChartData(mGraphList)
                            }


                            if (has("transactions")) {
                                val transactionsArray = getJSONArray("transactions")
                                for (i in 0 until transactionsArray.length()) {
                                    val transactionObject = transactionsArray.getJSONObject(i)

                                    transactionObject.let {
                                        val transactionType = it.getString("description")
                                        val reference = it.getString("reference")
                                        val vendorName = it.getString("name")
                                        val telephone = it.getString("reg_no")
                                        val expenditureAmount = it.getString("amount")
                                        val date = it.getString("date")
                                        val receiptTerm = when (it.getString("term")) {
                                            "1" -> "First Term Fees"
                                            "2" -> "Second Term Fees"
                                            else -> "Third Term Fees"
                                        }
                                        val receiptYear = transactionObject.getString("year")

                                        val session = "${receiptYear.toInt() - 1}/$receiptYear"

                                        ExpenditureHistoryModel().apply {
                                            this.vendorName = vendorName
                                            this.amount = expenditureAmount
                                            this.date = date
                                            this.session = session
                                            this.type = transactionType
                                            this.referenceNumber = reference
                                            this.term = receiptTerm
                                            this.phone = telephone
                                        }.let { model ->
                                            mExpenditureList.add(model)
                                        }

                                    }

                                }

                                mExpenditureList.sortByDescending { it.date }

                                ExpenditureHistoryAdapter(
                                    requireContext(),
                                    mExpenditureList,
                                    null,
                                    this@ExpenditureHistoryFragment
                                ).let {
                                    mRecyclerView.apply {
                                        isAnimating
                                        isVisible = true
                                        layoutManager = LinearLayoutManager(requireContext())
                                        adapter = it
                                        hasFixedSize()
                                    }
                                    showRootView()
                                }

                            } else {
                                hideRecyclerView()
                            }

                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                        mExpenditureView.isVisible = false
                        mErrorView.isVisible = true
                        mRefreshBtn.isVisible = false
                        mErrorMessage.text = getString(R.string.contact_developer)
                        mOpenBtn.isVisible = false
                    }
                }


                override fun onError(error: VolleyError) {
                    mExpenditureView.isVisible = false
                    mErrorView.isVisible = true
                    mOpenBtn.isVisible = false
                }
            }
        )

        hashMap = hashMapOf()

    }


    private fun filterTimeFrameData() {

        timeFrameTitle(timeFrameDataModel.startDate ?: getDate())
        termTitle("${timeFrameDataModel.term ?: mTerm}", "${timeFrameDataModel.year ?: mYear}")
        mTermBtn.isEnabled = true

        hashMap.apply {
            put("type", "expenditure")
            put("term", "${timeFrameDataModel.term ?: mTerm}")
            put("year", "${timeFrameDataModel.year ?: mYear}")
            put("startDate", "${timeFrameDataModel.startDate ?: timeFrameDataModel.duration}")
            put("endDate", timeFrameDataModel.endDate ?: "")
            put("grouping", timeFrameDataModel.grouping ?: "")
            put("filter", timeFrameDataModel.filter ?: "")
        }

        when {
            timeFrameDataModel.grouping != null -> {
                sendRequestToServer(Request.Method.POST, mUrl!!, requireContext(), hashMap,
                    object : VolleyCallback {
                        override fun onResponse(response: String) {
                            try {
                                initialiseList()

                                if (response != "[]") {
                                    JSONObject(response).run {

                                        getJSONArray("expenditure").run {
                                            getJSONObject(0).run {
                                                val sum = getString("sum")
                                                val count = getString("count")
                                                mExpenditureCount.text = count ?: "0"

                                                setSum(sum)
                                            }
                                        }


                                        if (has("graph")) {
                                            getJSONArray("graph").run {
                                                for (i in 0 until length()) {
                                                    getJSONObject(i).run {
                                                        val amount = getString("amount")
                                                        val label = getString("label")

                                                        mGraphList.add(
                                                            ChartModel(
                                                                amount,
                                                                label
                                                            )
                                                        )

                                                    }
                                                }

                                                mGraphList.sortBy { it.label }
                                                setChartData(mGraphList)

                                                ExpenditureHistoryAdapter(
                                                    requireContext(),
                                                    null,
                                                    mGraphList,
                                                    null
                                                ).let {
                                                    mRecyclerView.apply {
                                                        isAnimating
                                                        isVisible = true
                                                        layoutManager =
                                                            LinearLayoutManager(requireContext())
                                                        adapter = it
                                                        hasFixedSize()
                                                    }
                                                    showRootView()
                                                }

                                            }

                                        } else {
                                            hideRecyclerView()
                                        }

                                    }

                                } else {
                                    hideRecyclerView()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                                mExpenditureView.isVisible = false
                                mErrorView.isVisible = true
                                mErrorMessage.text = getString(R.string.contact_developer)
                                mRefreshBtn.isVisible = false
                                mOpenBtn.isVisible = false
                            }

                        }

                        override fun onError(error: VolleyError) {
                            mExpenditureView.isVisible = false
                            mErrorView.isVisible = true
                            mOpenBtn.isVisible = false
                        }
                    }
                )

            }

            else -> {
                sendRequestToServer(Request.Method.POST, mUrl!!, requireContext(), hashMap,
                    object : VolleyCallback {
                        override fun onResponse(response: String) {
                            try {
                                initialiseList()

                                JSONObject(response).run {
                                    getJSONArray("expenditure").run {
                                        getJSONObject(0).run {
                                            val sum = getString("sum")
                                            val count = getString("count")

                                            mExpenditureCount.text = count ?: "0"

                                            setSum(sum)
                                        }
                                    }


                                    if (has("graph")) {
                                        val graphArray = getJSONArray("graph")

                                        for (i in 0 until graphArray.length()) {
                                            val graphObject = graphArray.getJSONObject(i)
                                            val graphAmount = graphObject.getString("amount")
                                            val label = graphObject.getString("label")

                                            mGraphList.add(
                                                ChartModel(
                                                    graphAmount,
                                                    FunctionUtils.formatDate2(label)
                                                )
                                            )

                                        }

                                        mGraphList.sortBy { it.label }

                                        setChartData(mGraphList)

                                    } else {
                                        setChartData(mGraphList)
                                    }

                                    if (has("transactions")) {

                                        val transactionsArray = getJSONArray("transactions")

                                        for (i in 0 until transactionsArray.length()) {
                                            val transactionObject =
                                                transactionsArray.getJSONObject(i)

                                            transactionObject.let {
                                                val transactionType = it.getString("description")
                                                val reference = it.getString("reference")
                                                val vendorName = it.getString("name")
                                                val telephone = it.getString("reg_no")
                                                val expenditureAmount = it.getString("amount")
                                                val date = it.getString("date")
                                                val receiptTerm = when (it.getString("term")) {
                                                    "1" -> "First Term Fees"
                                                    "2" -> "Second Term Fees"
                                                    else -> "Third Term Fees"
                                                }
                                                val receiptYear =
                                                    transactionObject.getString("year")

                                                val session =
                                                    "${receiptYear.toInt() - 1}/$receiptYear"

                                                ExpenditureHistoryModel().apply {
                                                    this.vendorName = vendorName
                                                    this.amount = expenditureAmount
                                                    this.date = date
                                                    this.session = session
                                                    this.type = transactionType
                                                    this.referenceNumber = reference
                                                    this.term = receiptTerm
                                                    this.phone = telephone
                                                }.let { model ->
                                                    mExpenditureList.add(model)
                                                }

                                            }

                                        }

                                        mExpenditureList.sortByDescending {
                                            it.date
                                        }

                                        ExpenditureHistoryAdapter(
                                            requireContext(),
                                            mExpenditureList,
                                            null,
                                            this@ExpenditureHistoryFragment
                                        ).let {
                                            mRecyclerView.apply {
                                                isAnimating
                                                isVisible = true
                                                layoutManager =
                                                    LinearLayoutManager(requireContext())
                                                adapter = it
                                                hasFixedSize()
                                            }
                                            showRootView()
                                        }

                                    } else {
                                        hideRecyclerView()
                                    }

                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                                mExpenditureView.isVisible = false
                                mErrorView.isVisible = true
                                mErrorMessage.text = getString(R.string.contact_developer)
                                mRefreshBtn.isVisible = false
                                mOpenBtn.isVisible = false
                            }
                        }

                        override fun onError(error: VolleyError) {
                            mExpenditureView.isVisible = false
                            mErrorView.isVisible = true
                            mOpenBtn.isVisible = false
                        }
                    }
                )
            }
        }

        hashMap = hashMapOf()

    }

    private fun initialiseList() {
        mExpenditureList = mutableListOf()
        mGraphList = mutableListOf()
    }


    private fun setSum(sum: String) {
        if (sum == "null" || sum.isBlank()) {
            mExpenditureSum.text = getString(R.string.zero_balance)
        } else {
            String.format(
                Locale.getDefault(),
                "%s%s",
                getString(R.string.naira),
                currencyFormat(sum.toDouble())
            ).also { mExpenditureSum.text = it }
        }
    }


    private fun termTitle(term: String, year: String) {
        val session = "${year.toInt() - 1}/$year"
        mTermBtn.text = when (term) {
            "1" -> "$session 1st Term"
            "2" -> "$session 2nd Term"
            else -> "$session 3rd Term"
        }

    }

    private fun timeFrameTitle(date: String) {
        try {
            val simpleDateFormat = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

            val parseDate = simpleDateFormat.parse(date)!!
            val sdf = SimpleDateFormat("MMMM, yyyy", Locale.getDefault())
            mTimeFrameBtn.text = sdf.format(parseDate)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setChartData(items: MutableList<ChartModel>) {
        mExpenditureChart.removeAllViews()

        val graphicalView =
            plotLineChart(
                items, requireContext(), "Expenses", "Label"
            )
        graphicalView.repaint()
        mExpenditureChart.addView(graphicalView)

    }


    private fun showRootView() {
        mExpenditureView.isVisible = true
        mErrorView.isVisible = false
        mOpenBtn.isVisible = true
        mExpenditureMessage.isVisible = false
    }

    private fun hideRecyclerView() {
        mExpenditureView.isVisible = true
        mExpenditureMessage.isVisible = true
        mRecyclerView.isVisible = false
        mErrorView.isVisible = false
        mOpenBtn.isVisible = true
    }


    private fun performButtonsClick() {

        val btnOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        val btnClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)

        val rotateForward = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_forward)
        val rotateBackward = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_backward)

        val arrayList = arrayListOf(rotateBackward, btnClose)

        mOpenBtn.setOnClickListener {

            if (isOpen) {
                closeBtnAnimation(arrayList)

            } else {
                mOpenBtn.startAnimation(rotateForward)
                mExpenditureLayout.startAnimation(btnOpen)
                mSetupLayout.startAnimation(btnOpen)

                mAddExpenditureBtn1.isClickable = true
                mAddExpenditureBtn2.isClickable = true

                mSetupReportBtn1.isClickable = true
                mSetupReportBtn2.isClickable = true

                mExpenditureLayout.isVisible = true
                mSetupLayout.isVisible = true

                isOpen = true
            }
        }


        //open expenditure fragment
        mAddExpenditureBtn1.setOnClickListener {
            startActivity(
                Intent(context, PaymentActivity::class.java)
                    .putExtra("from", "add_expenditure")
            )
            closeBtnAnimation(arrayList)
        }


        //open expenditure fragment
        mAddExpenditureBtn2.setOnClickListener {
            startActivity(
                Intent(context, PaymentActivity::class.java)
                    .putExtra("from", "add_expenditure")
            )
            closeBtnAnimation(arrayList)
        }


        //open time frame dialog
        mSetupReportBtn1.setOnClickListener {
            timeFrameDialog()

            closeBtnAnimation(arrayList)
        }


        //open time frame dialog
        mSetupReportBtn2.setOnClickListener {
            timeFrameDialog()

            closeBtnAnimation(arrayList)
        }


        //open time frame dialog
        mTimeFrameBtn.setOnClickListener {
            timeFrameDialog()
        }


        //open term /session dialog
        mTermBtn.setOnClickListener {
            TermSessionPickerBottomSheet(timeFrameDataModel).show(
                requireActivity().supportFragmentManager,
                "Term & Session"
            )
        }

    }


    private fun timeFrameDialog() {
        ExpenditureTimeFrameBottomSheet(timeFrameDataModel).show(
            childFragmentManager, "time frame"
        )
    }


    private fun closeBtnAnimation(arrayList: ArrayList<Animation>) {
        mOpenBtn.startAnimation(arrayList[0])
        mExpenditureLayout.startAnimation(arrayList[1])
        mSetupLayout.startAnimation(arrayList[1])

        mAddExpenditureBtn1.isClickable = false
        mAddExpenditureBtn2.isClickable = false

        mSetupReportBtn1.isClickable = false
        mSetupReportBtn2.isClickable = false

        isOpen = false
    }


    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            getExpenditure()
        }
    }


    override fun onItemClick(position: Int) {
        val model = mExpenditureList[position]

        startActivity(
            Intent(requireContext(), PaymentActivity::class.java)
                .putExtra("amount", model.amount)
                .putExtra("vendor_name", model.vendorName)
                .putExtra("vendor_phone", model.phone)
                .putExtra("reference", model.referenceNumber)
                .putExtra("session", model.session)
                .putExtra("term", model.term)
                .putExtra("date", model.date)
                .putExtra("description", model.type)
                .putExtra("from", "admin_expenditure")
        )

    }

}