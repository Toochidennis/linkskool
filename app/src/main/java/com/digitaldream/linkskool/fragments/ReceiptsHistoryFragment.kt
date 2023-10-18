package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.adapters.ReceiptsHistoryAdapter
import com.digitaldream.linkskool.dialog.ReceiptTimeFrameBottomSheet
import com.digitaldream.linkskool.dialog.TermFeeDialog
import com.digitaldream.linkskool.dialog.TermSessionPickerBottomSheet
import com.digitaldream.linkskool.models.AdminPaymentModel
import com.digitaldream.linkskool.models.ChartModel
import com.digitaldream.linkskool.models.TimeFrameDataModel
import com.digitaldream.linkskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.getDate
import com.digitaldream.linkskool.utils.FunctionUtils.plotLineChart
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale


class ReceiptsHistoryFragment : Fragment(R.layout.fragment_receipts_history), OnItemClickListener {

    private lateinit var mReceiptView: NestedScrollView
    private lateinit var mReceiptChart: LinearLayout
    private lateinit var mReceiptSum: TextView
    private lateinit var mReceiptCount: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mReceiptMessage: TextView
    private lateinit var mReceiptImage: ImageView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button
    private lateinit var mOpenBtn: FloatingActionButton
    private lateinit var mAddReceiptBtn1: Button
    private lateinit var mAddReceiptBtn2: ImageButton
    private lateinit var mSetupReportBtn1: Button
    private lateinit var mSetupReportBtn2: ImageButton
    private lateinit var mSetupLayout: LinearLayout
    private lateinit var mReceiptLayout: LinearLayout
    private lateinit var mTimeFrameBtn: Button
    private lateinit var mTermBtn: Button
    private lateinit var mErrorMessage: TextView

    private lateinit var mReceiptList: MutableList<AdminPaymentModel>
    private lateinit var mGraphList: MutableList<ChartModel>
    private lateinit var timeFrameDataModel: TimeFrameDataModel

    private var hashMap = hashMapOf<String, String>()

    private var isOpen = false
    private var mTerm: String? = null
    private var mYear: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            mReceiptView = findViewById(R.id.receipt_view)
            mReceiptChart = findViewById(R.id.chart)
            mReceiptSum = findViewById(R.id.receipt_sum)
            mReceiptCount = findViewById(R.id.receipt_count)
            mRecyclerView = findViewById(R.id.receipt_recycler)
            mReceiptMessage = findViewById(R.id.receipt_error_message)
            mReceiptImage = findViewById(R.id.error_image)
            mErrorView = findViewById(R.id.error_view)
            mRefreshBtn = findViewById(R.id.refresh_btn)
            mOpenBtn = findViewById(R.id.open_btn)
            mAddReceiptBtn1 = findViewById(R.id.add_receipt_btn1)
            mAddReceiptBtn2 = findViewById(R.id.add_receipt_btn2)
            mSetupReportBtn1 = findViewById(R.id.setup_btn1)
            mSetupReportBtn2 = findViewById(R.id.setup_btn2)
            mSetupLayout = findViewById(R.id.setup_layout)
            mReceiptLayout = findViewById(R.id.add_receipt_layout)
            mTimeFrameBtn = findViewById(R.id.time_frame_btn)
            mTermBtn = findViewById(R.id.term_btn)
            mErrorMessage = findViewById(R.id.error_message)


            toolbar.apply {
                title = "Receipts"
                this.setNavigationIcon(R.drawable.arrow_left)
                setNavigationOnClickListener {
                    requireActivity().onBackPressedDispatcher
                        .onBackPressed()
                }
            }

        }


        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        mTerm = sharedPreferences.getString("term", "")
        mYear = sharedPreferences.getString("school_year", "")


        getReceipts()

        refreshData()

        timeFrameDataModel = TimeFrameDataModel { filterTimeFrameData() }

        timeFrameTitle(getDate())
        termTitle(mTerm!!, mYear!!)

        mTermBtn.isEnabled = false

        performButtonClick()

    }


    private fun getReceipts() {

        val url = "${getString(R.string.base_url)}/manageTransactions.php"

        hashMap.apply {
            put("type", "receipts")
            put("term", mTerm!!)
            put("year", mYear!!)
            put("startDate", "this month")
            put("endDate", "")
            put("grouping", "")
            put("filter", "")
        }

        sendRequestToServer(Request.Method.POST, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        initialiseList()

                        JSONObject(response).run {
                            val receiptsArray = getJSONArray("receipts")
                            val receiptsObject = receiptsArray.getJSONObject(0)
                            val receiptSum = receiptsObject.getString("sum")
                            val receiptCount = receiptsObject.getString("count")

                            mReceiptCount.text = receiptCount ?: "0"

                            setSum(receiptSum)

                            if (has("graph")) {
                                val graphArray = getJSONArray("graph")

                                for (i in 0 until graphArray.length()) {
                                    val graphObject = graphArray.getJSONObject(i)
                                    val graphAmount = graphObject.getString("amount")
                                    val label = graphObject.getString("label")

                                    mGraphList.add(ChartModel(graphAmount, formatDate2(label)))
                                }

                                mGraphList.sortBy { it.label }
                                setGraphData(mGraphList)

                            } else {
                                setGraphData(mGraphList)
                            }

                            if (has("transactions")) {

                                val transactionsArray = getJSONArray("transactions")

                                for (i in 0 until transactionsArray.length()) {
                                    val transactionObject = transactionsArray.getJSONObject(i)

                                    transactionObject.let {
                                        val transactionType = it.getString("trans_type")
                                        val reference = it.getString("reference")
                                        val registrationNo = it.getString("reg_no")
                                        val studentName = it.getString("name")
                                        val receiptAmount = it.getString("amount")
                                        val date = it.getString("date")
                                        val receiptTerm =
                                            when (it.getString("term")) {
                                                "1" -> "First Term Fees"
                                                "2" -> "Second Term Fees"
                                                else -> "Third Term Fees"
                                            }

                                        val receiptYear = it.getString("year")
                                        val levelName = it.getString("level_name")
                                        val className = it.getString("class_name")

                                        val session = "${receiptYear.toInt() - 1}/$receiptYear"

                                        AdminPaymentModel().apply {
                                            mStudentName = studentName
                                            mTransactionName = transactionType
                                            mReferenceNumber = reference
                                            mRegistrationNumber = registrationNo
                                            mReceivedAmount = receiptAmount
                                            mTransactionDate = date
                                            mTerm = receiptTerm
                                            mSession = session
                                            mLevelName = levelName
                                            mClassName = className
                                        }.let { model ->
                                            mReceiptList.add(model)
                                        }

                                    }

                                }

                                mReceiptList.sortByDescending { sort ->
                                    sort.mTransactionDate
                                }

                                ReceiptsHistoryAdapter(
                                    requireContext(),
                                    mReceiptList,
                                    null,
                                    this@ReceiptsHistoryFragment
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
                        mReceiptView.isVisible = false
                        mErrorView.isVisible = true
                        mRefreshBtn.isVisible = false
                        mErrorMessage.text = getString(R.string.contact_developer)
                        mOpenBtn.isVisible = false
                    }

                }

                override fun onError(error: VolleyError) {
                    mReceiptView.isVisible = false
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

        val url = "${getString(R.string.base_url)}/manageTransactions.php"

        hashMap.apply {
            put("type", "receipts")
            put("term", "${timeFrameDataModel.term ?: mTerm}")
            put("year", "${timeFrameDataModel.year ?: mYear}")
            put("startDate", "${timeFrameDataModel.startDate ?: timeFrameDataModel.duration}")
            put("endDate", timeFrameDataModel.endDate ?: "")
            put("grouping", timeFrameDataModel.grouping ?: "")
            put("filter", timeFrameDataModel.filter ?: "")

        }

        when {
            timeFrameDataModel.grouping != null -> {
                sendRequestToServer(Request.Method.POST, url, requireContext(), hashMap,
                    object : VolleyCallback {
                        override fun onResponse(response: String) {
                            try {
                                initialiseList()

                                if (response != "[]") {
                                    JSONObject(response).run {

                                        getJSONArray("receipts").run {
                                            getJSONObject(0).run {
                                                val sum = getString("sum")
                                                val count = getString("count")

                                                mReceiptCount.text = count ?: "0"
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
                                                setGraphData(mGraphList)

                                                ReceiptsHistoryAdapter(
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
                                mReceiptView.isVisible = false
                                mErrorView.isVisible = true
                                mErrorMessage.text = getString(R.string.contact_developer)
                                mRefreshBtn.isVisible = false
                                mOpenBtn.isVisible = false
                            }

                        }

                        override fun onError(error: VolleyError) {
                            mReceiptView.isVisible = false
                            mErrorView.isVisible = true
                            mOpenBtn.isVisible = false
                        }
                    }
                )

            }

            else -> {
                sendRequestToServer(Request.Method.POST, url, requireContext(), hashMap,
                    object : VolleyCallback {
                        override fun onResponse(response: String) {
                            try {
                                initialiseList()

                                JSONObject(response).run {

                                    getJSONArray("receipts").run {
                                        getJSONObject(0).run {
                                            val sum = getString("sum")
                                            val count = getString("count")

                                            mReceiptCount.text = count ?: "0"
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
                                                    formatDate2(label)
                                                )
                                            )

                                        }

                                        mGraphList.sortBy { it.label }

                                        setGraphData(mGraphList)

                                    } else {
                                        setGraphData(mGraphList)
                                    }

                                    if (has("transactions")) {

                                        val transactionsArray = getJSONArray("transactions")

                                        for (i in 0 until transactionsArray.length()) {
                                            val transactionObject =
                                                transactionsArray.getJSONObject(i)

                                            transactionObject.let {
                                                val transactionType = it.getString("trans_type")
                                                val reference = it.getString("reference")
                                                val registrationNo = it.getString("reg_no")
                                                val studentName = it.getString("name")
                                                val receiptAmount = it.getString("amount")
                                                val date = it.getString("date")
                                                val receiptTerm =
                                                    when (it.getString("term")) {
                                                        "1" -> "First Term Fees"
                                                        "2" -> "Second Term Fees"
                                                        else -> "Third Term Fees"
                                                    }

                                                val receiptYear = it.getString("year")
                                                val levelName = it.getString("level_name")
                                                val className = it.getString("class_name")

                                                val session =
                                                    "${receiptYear.toInt() - 1}/$receiptYear"

                                                AdminPaymentModel().apply {
                                                    mStudentName = studentName
                                                    mTransactionName = transactionType
                                                    mReferenceNumber = reference
                                                    mRegistrationNumber = registrationNo
                                                    mReceivedAmount = receiptAmount
                                                    mTransactionDate = date
                                                    mTerm = receiptTerm
                                                    mSession = session
                                                    mLevelName = levelName
                                                    mClassName = className
                                                }.let { model ->
                                                    mReceiptList.add(model)
                                                }

                                            }

                                        }

                                        mReceiptList.sortByDescending { sort ->
                                            sort.mTransactionDate
                                        }

                                        ReceiptsHistoryAdapter(
                                            requireContext(),
                                            mReceiptList,
                                            null,
                                            this@ReceiptsHistoryFragment
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
                                mReceiptView.isVisible = false
                                mErrorView.isVisible = true
                                mErrorMessage.text = getString(R.string.contact_developer)
                                mRefreshBtn.isVisible = false
                                mOpenBtn.isVisible = false
                            }
                        }

                        override fun onError(error: VolleyError) {
                            mReceiptView.isVisible = false
                            mErrorView.isVisible = true
                            mOpenBtn.isVisible = false
                        }
                    }
                )
            }
        }

        hashMap = hashMapOf()

    }

    private fun hideRecyclerView() {
        mReceiptView.isVisible = true
        mReceiptMessage.isVisible = true
        mRecyclerView.isVisible = false
        mErrorView.isVisible = false
        mOpenBtn.isVisible = true
    }

    private fun showRootView() {
        mReceiptView.isVisible = true
        mErrorView.isVisible = false
        mOpenBtn.isVisible = true
        mReceiptMessage.isVisible = false
    }

    private fun initialiseList() {
        mReceiptList = mutableListOf()
        mGraphList = mutableListOf()
    }


    private fun setGraphData(items: MutableList<ChartModel>) {
        mReceiptChart.removeAllViews()

        val graphicalView =
            plotLineChart(
                items, requireContext(), "Received", "Label"
            )
        graphicalView.repaint()
        mReceiptChart.addView(graphicalView)

    }


    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            getReceipts()
        }
    }


    private fun setSum(sum: String) {
        if (sum == "null" || sum.isBlank()) {
            mReceiptSum.text = getString(R.string.zero_balance)
        } else {
            String.format(
                Locale.getDefault(),
                "%s%s",
                getString(R.string.naira),
                currencyFormat(sum.toDouble())
            ).also { mReceiptSum.text = it }
        }
    }

    private fun performButtonClick() {

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
                mReceiptLayout.startAnimation(btnOpen)
                mSetupLayout.startAnimation(btnOpen)

                mAddReceiptBtn1.isClickable = true
                mAddReceiptBtn2.isClickable = true

                mSetupReportBtn1.isClickable = true
                mSetupReportBtn2.isClickable = true

                mReceiptLayout.isVisible = true
                mSetupLayout.isVisible = true

                isOpen = true
            }
        }


        //open receipt dialog
        mAddReceiptBtn1.setOnClickListener {
            TermFeeDialog(requireContext(), "receipts", null)
                .apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            closeBtnAnimation(arrayList)
        }


        //open receipt dialog
        mAddReceiptBtn2.setOnClickListener {
            TermFeeDialog(requireContext(), "receipts", null)
                .apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
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


        mTimeFrameBtn.setOnClickListener {
            timeFrameDialog()
        }


        mTermBtn.setOnClickListener {
            TermSessionPickerBottomSheet(timeFrameDataModel).show(
                requireActivity().supportFragmentManager,
                "Term & Session"
            )
        }

    }

    private fun closeBtnAnimation(arrayList: ArrayList<Animation>) {
        mOpenBtn.startAnimation(arrayList[0])
        mReceiptLayout.startAnimation(arrayList[1])
        mSetupLayout.startAnimation(arrayList[1])

        mAddReceiptBtn1.isClickable = false
        mAddReceiptBtn2.isClickable = false

        mSetupReportBtn1.isClickable = false
        mSetupReportBtn2.isClickable = false

        isOpen = false
    }


    private fun timeFrameDialog() {
        ReceiptTimeFrameBottomSheet(
            timeFrameDataModel
        ).show(requireActivity().supportFragmentManager, "Time Frame")
    }

    private fun termTitle(term: String, year: String) {
        val session = "${year.toInt() - 1}/$year"
        mTermBtn.text = when (term) {
            "1" -> "$session 1st Term"
            "2" -> "$session 2nd Term"
            else -> "$session 3rd Term"
        }

    }


    /*  private fun setUpMenu() {
          mMenuHost.addMenuProvider(object : MenuProvider {
              override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                  menuInflater.inflate(R.menu.search_menu, menu)

                  val searchItem = menu.findItem(R.id.search)
                  val searchView = searchItem.actionView as SearchView

                  searchView.imeOptions = EditorInfo.IME_ACTION_DONE

                  searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                      override fun onQueryTextSubmit(query: String?): Boolean {
                          return false
                      }

                      override fun onQueryTextChange(newText: String?): Boolean {
                          mAdapter.filter.filter(newText)
                          return false
                      }
                  })
              }

              override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                  return when (menuItem.itemId) {
                      android.R.id.home -> {
                          requireActivity().onBackPressedDispatcher
                              .onBackPressed()
                          return true
                      }

                      else -> false
                  }
              }
          })
      }*/


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


    override fun onItemClick(position: Int) {
        val paymentModel = mReceiptList[position]

        startActivity(
            Intent(requireContext(), PaymentActivity::class.java)
                .putExtra("amount", paymentModel.mReceivedAmount)
                .putExtra("name", paymentModel.mStudentName)
                .putExtra("level_name", paymentModel.mLevelName)
                .putExtra("class_name", paymentModel.mClassName)
                .putExtra("reg_no", paymentModel.mRegistrationNumber)
                .putExtra("reference", paymentModel.mReferenceNumber)
                .putExtra("session", paymentModel.mSession)
                .putExtra("term", paymentModel.mTerm)
                .putExtra("date", paymentModel.mTransactionDate)
                .putExtra("from", "admin_receipt")
        )

    }

}