package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.BuildConfig
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.activities.PaystackPaymentActivity
import com.digitaldream.winskool.adapters.StudentPaymentAdapter
import com.digitaldream.winskool.adapters.StudentPaymentSliderAdapter
import com.digitaldream.winskool.dialog.OnInputListener
import com.digitaldream.winskool.dialog.PaymentEmailDialog
import com.digitaldream.winskool.models.StudentPaymentModel
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class StudentPaymentFragment : Fragment(), OnInputListener,
    StudentPaymentAdapter.OnHistoryClickListener,
    StudentPaymentSliderAdapter.OnCardClickListener {

    private lateinit var mMainView: LinearLayout
    private lateinit var mPaidSate: LinearLayout
    private lateinit var mTermView: RelativeLayout
    private lateinit var mHistoryRecyclerView: RecyclerView
    private lateinit var mHistoryMessage: TextView
    private lateinit var mHistoryImage: ImageView
    private lateinit var mErrorMessage: TextView
    private lateinit var mRefreshBtn: Button
    private lateinit var mViewPager: ViewPager
    private lateinit var mTabLayout: TabLayout

    private val mHistoryList = mutableListOf<StudentPaymentModel>()
    private val mCardList = mutableListOf<StudentPaymentModel>()
    private lateinit var mHistoryAdapter: StudentPaymentAdapter
    private lateinit var mCardAdapter: StudentPaymentSliderAdapter
    private var autoSlideJob: Job? = null

    private var mStudentId: String? = null
    private var mInvoiceId: String? = null
    private var mAmount: String? = null
    private var mStudentEmail: String? = null
    private var mSession: String? = null
    private var mYear: String? = null
    private var mTerm: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mMainView = view.findViewById(R.id.main_layout)
        mPaidSate = view.findViewById(R.id.paid_state)
        mTermView = view.findViewById(R.id.slider_view)
        mHistoryRecyclerView = view.findViewById(R.id.history_recycler)
        mHistoryMessage = view.findViewById(R.id.history_error_message)
        mHistoryImage = view.findViewById(R.id.error_image)
        mErrorMessage = view.findViewById(R.id.error_message)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)
        mViewPager = view.findViewById(R.id.card_pager)
        mTabLayout = view.findViewById(R.id.card_tab)

        val sharedPreferences = requireContext().getSharedPreferences(
            "loginDetail", Context
                .MODE_PRIVATE
        )
        mStudentId = sharedPreferences.getString("user_id", "")
        mStudentEmail = sharedPreferences.getString("student_email", "")

        toolbar.apply {
            title = "Payment"
            setNavigationIcon(R.drawable.arrow_left)

            setNavigationOnClickListener { requireActivity().onBackPressed() }
        }

        mHistoryAdapter = StudentPaymentAdapter(mHistoryList, this)
        mHistoryRecyclerView.hasFixedSize()
        mHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mHistoryRecyclerView.adapter = mHistoryAdapter

        mCardAdapter = StudentPaymentSliderAdapter(requireContext(), mCardList, this)
        mViewPager.adapter = mCardAdapter
        mTabLayout.setupWithViewPager(mViewPager, true)

        refreshData()
    }

    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            paymentHistory()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestURL(sStudentEmail: String, sAmount: Long) {
        val builder = StringBuilder()
        val json = JSONObject()
            .put("email", sStudentEmail)
            .put("amount", "${sAmount * 100}")
        val url = "https://api.paystack.co/transaction/initialize"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val mEntity = StringEntity(json.toString())
                val httpClient: HttpClient = HttpClientBuilder.create().build()
                val post = HttpPost(url)
                post.apply {
                    entity = mEntity
                    addHeader("Content-type", "application/json")
                    addHeader(
                        "Authorization", BuildConfig.PSTK_SECRET_KEY
                    )
                }

                val response = httpClient.execute(post)
                val reader = BufferedReader(InputStreamReader(response.entity.content))

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                }

                val jsonObject = JSONObject(builder.toString())
                val status = jsonObject.getString("status")
                val data = jsonObject.getJSONObject("data")
                val authorizationURL = data.getString("authorization_url")
                val reference = data.getString("reference")

                when (status) {
                    "true" -> {
                        startActivity(
                            Intent(activity, PaystackPaymentActivity::class.java)
                                .putExtra("url", authorizationURL)
                                .putExtra("reference", reference)
                                .putExtra("transaction_id", mInvoiceId)
                                .putExtra("session", mSession)
                                .putExtra("term", mTerm)
                                .putExtra("year", mYear)
                                .putExtra("amount", mAmount)
                        )
                    }

                    else -> throw Exception("Can't generate url")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun paymentHistory() {
        val url = Login.urlBase + "/manageReceipts.php?list=$mStudentId"
        val hashMap = hashMapOf<String, String>()

        sendRequestToServer(Request.Method.GET, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response == "[]") {
                        mErrorMessage.isVisible = true
                        "Fees not set yet. Check back later!".also { mErrorMessage.text = it }
                    } else {
                        try {
                            val jsonObject = JSONObject(response)
                            if (jsonObject.has("invoice")) {
                                val invoiceArray = jsonObject.getJSONArray("invoice")
                                for (i in 0 until invoiceArray.length()) {
                                    val invoiceObjects = invoiceArray.getJSONObject(i)
                                    val invoiceId = invoiceObjects.getString("tid")
                                    val amount =
                                        invoiceObjects.getString("amount").replace(".00", "")
                                    val year = invoiceObjects.getString("year")
                                    val term = invoiceObjects.getString("term")
                                    val previousYear = year.toInt() - 1
                                    val session =
                                        String.format(
                                            Locale.getDefault(),
                                            "%d/%s",
                                            previousYear,
                                            year
                                        )

                                    val paymentModel = StudentPaymentModel()
                                    paymentModel.setInvoiceId(invoiceId)
                                    paymentModel.setAmount(amount)
                                    paymentModel.setAmountT(amount)
                                    paymentModel.setSession(session)
                                    paymentModel.setTerm(term)
                                    mCardList.add(paymentModel)
                                }
                            }

                            if (mCardList.isEmpty()) {
                                mTermView.isVisible = false
                                mPaidSate.isVisible = true
                            } else {
                                mTermView.isVisible = true
                                mPaidSate.isVisible = false
                            }
                            mCardAdapter.notifyDataSetChanged()

                            if (jsonObject.has("receipts")) {
                                val receiptsArray = jsonObject.getJSONArray("receipts")
                                for (i in 0 until receiptsArray.length()) {
                                    val receiptsObjects = receiptsArray.getJSONObject(i)
                                    val name = receiptsObjects.getString("name")
                                    val reference = receiptsObjects.getString("reference")
                                    val amount = receiptsObjects.getString("amount")
                                    val date = receiptsObjects.getString("date")
                                    val year = receiptsObjects.getString("year")
                                    val term = when (receiptsObjects.getString("term")) {
                                        "1" -> "First Term Fees"
                                        "2" -> "Second Term Fees"
                                        else -> "Third Term Fees"
                                    }
                                    requireContext().getSharedPreferences(
                                        "loginDetail",
                                        Context.MODE_PRIVATE
                                    ).edit()
                                        .putString(
                                            "level_name",
                                            receiptsObjects.getString("level_name")
                                        )
                                        .apply()
                                    val previousYear = year.toInt() - 1
                                    val session =
                                        String.format(
                                            Locale.getDefault(),
                                            "%d/%s",
                                            previousYear,
                                            year
                                        )
                                    val formattedAmount =
                                        currencyFormat(amount.toDouble())

                                    val paymentModel = StudentPaymentModel()
                                    paymentModel.setName(name)
                                    paymentModel.setAmount(
                                        String.format(
                                            Locale.getDefault(), "%s%s",
                                            getString(R.string.naira), formattedAmount
                                        )
                                    )
                                    paymentModel.setSession(session)
                                    paymentModel.setTerm(term)
                                    paymentModel.setDate(date)
                                    paymentModel.setReferenceNumber(reference)
                                    paymentModel.setStatus("Success")

                                    mHistoryList.add(paymentModel)
                                    mHistoryList.sortByDescending { it.getDate() }
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (mHistoryList.isEmpty()) {
                            mHistoryMessage.isVisible = true
                            mHistoryImage.isVisible = true
                            mHistoryMessage.text = getString(R.string.no_history)
                            mMainView.isVisible = true
                            mErrorMessage.isVisible = false
                            mRefreshBtn.isVisible = false
                        } else {
                            mHistoryMessage.isVisible = false
                            mHistoryImage.isVisible = false
                            mMainView.isVisible = true
                            mErrorMessage.isVisible = false
                            mRefreshBtn.isVisible = false
                        }

                        mHistoryAdapter.notifyDataSetChanged()
                    }
                }

                override fun onError(error: VolleyError) {
                    mMainView.isVisible = false
                    mErrorMessage.isVisible = true
                    mErrorMessage.text = getString(R.string.can_not_retrieve)
                    mRefreshBtn.isVisible = true
                }
            })
    }

    override fun onHistoryClick(position: Int) {
        val paymentModel = mHistoryList[position]
        val amount = paymentModel.getAmount()
        val reference = paymentModel.getReferenceNumber()
        val status = paymentModel.getStatus()
        val session = paymentModel.getSession()
        val term = paymentModel.getTerm()
        val date = paymentModel.getDate()

        startActivity(
            Intent(requireContext(), PaymentActivity::class.java)
                .putExtra("amount", amount)
                .putExtra("reference", reference)
                .putExtra("status", status)
                .putExtra("session", session)
                .putExtra("term", term)
                .putExtra("date", date)
                .putExtra("from", "student_receipt")
        )
    }

    override fun sendInput(input: String) {
        requireContext().getSharedPreferences(
            "loginDetail",
            Context.MODE_PRIVATE
        ).edit()
            .putString("student_email", input)
            .apply()

        requestURL(input, mAmount!!.toLong())
    }

    override fun sendId(levelId: String) {

    }

    override fun onResume() {
        super.onResume()
        mHistoryList.clear()
        mCardList.clear()
        mCardAdapter.notifyDataSetChanged()
        paymentHistory()

        startAutoSliding()
    }

    override fun onPause() {
        super.onPause()
        stopAutoSliding()
    }

    override fun viewDetails(position: Int) {
        val paymentModel = mCardList[position]
        mTerm = paymentModel.getTerm()

        startActivity(
            Intent(activity, PaymentActivity::class.java)
                .putExtra("from", "fee_details")
                .putExtra("term", mTerm)
        )

    }

    override fun makePayment(position: Int) {
        val paymentModel = mCardList[position]
        mSession = paymentModel.getSession()
        mTerm = paymentModel.getTerm()
        mAmount = paymentModel.getAmountT()
        mInvoiceId = paymentModel.getInvoiceId()

        if (mStudentEmail!!.isNotBlank()) {
            requestURL(mStudentEmail!!, mAmount!!.toLong())
        } else {
            PaymentEmailDialog(requireContext(), this)
                .apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

    }

    private fun startAutoSliding() {
        autoSlideJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(3000L)

                withContext(Dispatchers.Main) {
                    if (mCardList.isNotEmpty() && mViewPager.currentItem < mCardList.size - 1) {
                        mViewPager.currentItem++
                    } else {
                        mViewPager.currentItem = 0
                    }
                }
            }
        }
    }

    private fun stopAutoSliding() {
        autoSlideJob?.cancel()
        autoSlideJob = null
    }
}