package com.digitaldream.winskool.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.adapters.ReceiptStudentBudgetAdapter
import com.digitaldream.winskool.adapters.StudentFeesDetailsAdapter
import com.digitaldream.winskool.dialog.AddReceiptDialog
import com.digitaldream.winskool.models.StudentPaymentModel
import com.digitaldream.winskool.models.TermFeesDataModel
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class ReceiptStudentBudgetActivity : AppCompatActivity(R.layout.activity_receipt_student_budget),
    OnItemClickListener {

    private lateinit var mMainView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mErrorMessage: TextView
    private lateinit var mErrorImage: ImageView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button
    private lateinit var mAddBudgetBtn: FloatingActionButton
    private lateinit var mAdapter: ReceiptStudentBudgetAdapter

    private lateinit var mDetailsView: NestedScrollView
    private lateinit var mPaymentLayout: RelativeLayout
    private lateinit var mDetailsRecyclerView: RecyclerView
    private lateinit var mFeeTotal1: TextView
    private lateinit var mFeeTotal2: TextView
    private lateinit var mSchoolName: TextView
    private lateinit var mTitle: TextView
    private lateinit var mPayBtn: Button
    private lateinit var mBackBtn: ImageView
    private lateinit var mDetailsAdapter: StudentFeesDetailsAdapter

    private var levelId: String? = null
    private var levelName: String? = null
    private var studentName: String? = null
    private var classId: String? = null
    private var studentId: String? = null
    private var regNo: String? = null
    private var mTerm: String? = null
    private var mYear: String? = null
    private var mSession: String? = null
    private var mInvoiceId: String? = null
    private var mAmount: String? = null

    private val mBudgetList = mutableListOf<StudentPaymentModel>()
    private val mFeeList = mutableListOf<TermFeesDataModel>()
    private val dataModel = TermFeesDataModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        levelId = intent.getStringExtra("levelId")
        classId = intent.getStringExtra("classId")
        studentId = intent.getStringExtra("studentId")
        regNo = intent.getStringExtra("reg_no")
        levelName = intent.getStringExtra("level_name")
        studentName = intent.getStringExtra("student_name")


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        mMainView = findViewById(R.id.main_view)
        mRecyclerView = findViewById(R.id.budget_recycler)
        mErrorView = findViewById(R.id.error_view)
        mErrorMessage = findViewById(R.id.budget_error_message)
        mRefreshBtn = findViewById(R.id.refresh_btn)
        mAddBudgetBtn = findViewById(R.id.add_receipt)
        mErrorImage = findViewById(R.id.error_image)

        mDetailsView = findViewById(R.id.details_view)
        mPaymentLayout = findViewById(R.id.payment_layout)
        mDetailsRecyclerView = findViewById(R.id.details_recycler)
        mFeeTotal1 = findViewById(R.id.fee_total)
        mFeeTotal2 = findViewById(R.id.total2)
        mSchoolName = findViewById(R.id.school_name)
        mTitle = findViewById(R.id.title)
        mPayBtn = findViewById(R.id.pay_btn)
        mBackBtn = findViewById(R.id.back_btn)


        toolbar.apply {
            title = "Select Term Fee"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        val sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        val mNameSchool = sharedPreferences.getString("school_name", "")
        mSchoolName.text = mNameSchool

        mAdapter = ReceiptStudentBudgetAdapter(this, mBudgetList, this)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter

        mDetailsAdapter = StudentFeesDetailsAdapter(this, mFeeList)
        mDetailsRecyclerView.hasFixedSize()
        mDetailsRecyclerView.layoutManager = LinearLayoutManager(this)
        mDetailsRecyclerView.adapter = mDetailsAdapter

        mAddBudgetBtn.setOnClickListener {
            startActivity(
                Intent(this, TermlyFeeSetupActivity()::class.java)
                    .putExtra("level_name", levelName)
                    .putExtra("level_id", levelId)
            )
        }

        refreshData()
        makePayment()
        backClick()
    }


    private fun getFees() {
        val url = getString(R.string.base_url) + "/manageReceipts.php?list=$studentId"
        val hashMap = hashMapOf<String, String>()

        sendRequestToServer(Request.Method.GET, url, this, hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response == "[]") {
                        mMainView.isVisible = true
                        mErrorMessage.isVisible = true
                        mAddBudgetBtn.isVisible = true
                        "Fees not set yet. Use the button below to set!".also {
                            mErrorMessage.text = it
                        }
                    } else {
                        try {
                            val jsonObject = JSONObject(response)
                            if (jsonObject.has("invoice")) {
                                val invoiceArray = jsonObject.getJSONArray("invoice")

                                for (i in 0 until invoiceArray.length()) {
                                    val invoiceObjects = invoiceArray.getJSONObject(i)
                                    val descriptionArray =
                                        invoiceObjects.getJSONArray("description")
                                    mInvoiceId = invoiceObjects.getString("tid")
                                    mAmount =
                                        invoiceObjects.getString("amount")
                                    mYear = invoiceObjects.getString("year")
                                    mTerm = invoiceObjects.getString("term")

                                    val previousYear = mYear!!.toInt() - 1
                                    mSession =
                                        String.format(
                                            Locale.getDefault(),
                                            "%d/%s",
                                            previousYear,
                                            mYear!!
                                        )

                                    val paymentModel = StudentPaymentModel()
                                    paymentModel.setInvoiceId(mInvoiceId!!)
                                    paymentModel.setAmount(mAmount!!)
                                    paymentModel.setSession(mSession!!)
                                    paymentModel.setTerm(mTerm!!)
                                    paymentModel.setYear(mYear!!)
                                    paymentModel.setJson(descriptionArray.toString())
                                    mBudgetList.add(paymentModel)

                                    if (invoiceArray.length() == 1) {
                                        for (j in 0 until descriptionArray.length()) {
                                            val descriptionObject = descriptionArray
                                                .getJSONObject(j)
                                            val feeName = descriptionObject.getString("fee_name")
                                            val feeAmount =
                                                descriptionObject.getString("fee_amount")

                                            val termText = when (mTerm) {
                                                "1" -> "First Term School Fee Charges for"
                                                "2" -> "Second Term School Fee Charges for"
                                                else -> "Third Term School Fee Charges for"
                                            }

                                            mTitle.text = String.format(
                                                Locale.getDefault(),
                                                "%s %s %s", termText, mSession, "session"
                                            )

                                            mFeeTotal1.text = String.format(
                                                Locale.getDefault(),
                                                "%s%s",
                                                getString(R.string.naira),
                                                currencyFormat(mAmount!!.toDouble())
                                            )

                                            mFeeTotal2.text = String.format(
                                                Locale.getDefault(),
                                                "%s%s",
                                                getString(R.string.naira),
                                                currencyFormat(mAmount!!.toDouble())
                                            )

                                            val termFeesDataModel = TermFeesDataModel().apply {
                                                mFeeName = feeName
                                                mFeeAmount = feeAmount
                                            }

                                            mFeeList.add(termFeesDataModel)
                                            mFeeList.sortBy { it.mFeeName }
                                        }

                                        if (mFeeList.isNotEmpty()) {
                                            mDetailsView.isVisible = true
                                            mPaymentLayout.isVisible = true
                                            mMainView.isVisible = false
                                            mBackBtn.isVisible = false
                                        }
                                        mDetailsAdapter.notifyItemChanged(mFeeList.size - 1)

                                    } else {

                                        if (mBudgetList.isEmpty()) {
                                            mErrorView.isVisible = false
                                            mDetailsView.isVisible = false
                                            mMainView.isVisible = true
                                            mErrorMessage.isVisible = true
                                            mAddBudgetBtn.isVisible = true
                                            mPaymentLayout.isVisible = false
                                        } else {
                                            mDetailsView.isVisible = false
                                            mErrorView.isVisible = false
                                            mMainView.isVisible = true
                                            mErrorMessage.isVisible = false
                                            mAddBudgetBtn.isVisible = false
                                            mPaymentLayout.isVisible = false
                                        }
                                        mAdapter.notifyItemChanged(mBudgetList.size - 1)
                                    }

                                }
                            } else {
                                mMainView.isVisible = true
                                mErrorMessage.isVisible = true
                                mAddBudgetBtn.isVisible = false
                                mPaymentLayout.isVisible = false
                                "$studentName have paid!".also {
                                    mErrorMessage
                                        .text = it
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onError(error: VolleyError) {
                    mMainView.isVisible = false
                    mErrorView.isVisible = true
                    mAddBudgetBtn.isVisible = false
                    mPaymentLayout.isVisible = false
                }
            })
    }


    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            mBudgetList.clear()
            mFeeList.clear()
            getFees()
        }
    }

    override fun onResume() {
        super.onResume()
        mBudgetList.clear()
        mFeeList.clear()
        getFees()
    }

    override fun onItemClick(position: Int) {
        val modelList = mBudgetList[position]
        val term = modelList.getTerm()
        val jsonArray = JSONArray(modelList.getJson())
        val amount = modelList.getAmount()

        dataModel.apply {
            mTerm = term
            mYear = modelList.getYear()
            mAmount = amount
            mInvoiceId = modelList.getInvoiceId()
        }

        mFeeList.clear()

        for (j in 0 until jsonArray.length()) {
            val descriptionObject = jsonArray.getJSONObject(j)
            val feeName = descriptionObject.getString("fee_name")
            val feeAmount = descriptionObject.getString("fee_amount")

            val termText = when (term) {
                "1" -> "First Term School Fee Charges for"
                "2" -> "Second Term School Fee Charges for"
                else -> "Third Term School Fee Charges for"
            }

            mTitle.text = String.format(
                Locale.getDefault(),
                "%s %s %s", termText, mSession, "session"
            )

            mFeeTotal1.text = String.format(
                Locale.getDefault(),
                "%s%s",
                getString(R.string.naira),
                currencyFormat(amount!!.toDouble())
            )

            mFeeTotal2.text = String.format(
                Locale.getDefault(),
                "%s%s",
                getString(R.string.naira),
                currencyFormat(amount.toDouble())
            )

            val termFeesDataModel = TermFeesDataModel().apply {
                mFeeName = feeName
                mFeeAmount = feeAmount
            }

            mFeeList.add(termFeesDataModel)
            mFeeList.sortBy { it.mFeeName }

        }
        mDetailsAdapter.notifyDataSetChanged()

        if (mFeeList.isNotEmpty()) {
            mDetailsView.isVisible = true
            mPaymentLayout.isVisible = true
            mMainView.isVisible = false
        }
    }

    private fun makePayment() {

        mPayBtn.setOnClickListener {
            val invoiceId = dataModel.mInvoiceId
            val amount = dataModel.mAmount
            val term = dataModel.mTerm
            val year = dataModel.mYear

            if (invoiceId != null) {
                openDialog(invoiceId, amount!!, term!!, year!!)
            } else {
                openDialog(mInvoiceId!!, mAmount!!, mTerm!!, mYear!!)
            }
        }
    }

    private fun openDialog(
        sInvoiceId: String,
        sAmount: String,
        sTerm: String,
        sYear: String,
    ) {
        AddReceiptDialog(
            this, sInvoiceId,
            studentId!!,
            classId!!,
            levelId!!,
            regNo!!,
            studentName!!,
            sAmount,
            sYear,
            sTerm,
            object : OnItemClickListener {
                override fun onItemClick(position: Int) {
                    SystemClock.sleep(1000)
                    finish()
                }
            }
        ).apply {
            setCancelable(true)
            show()
        }.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun backClick() {
        mBackBtn.setOnClickListener {
            mBudgetList.clear()
            mFeeList.clear()
            getFees()
        }
    }

}