package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.PaymentActivity
import com.digitaldream.linkskool.adapters.AdminPaymentDashboardAdapter
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.dialog.AdminClassesDialog
import com.digitaldream.linkskool.models.AdminPaymentModel
import com.digitaldream.linkskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.json.JSONObject
import java.util.Locale


class AdminPaymentDashboardFragment : Fragment(R.layout.fragment_dashboard_payment_admin),
    OnItemClickListener {

    private lateinit var settingsButton: ImageButton
    private lateinit var mMainLayout: LinearLayout
    private lateinit var mExpectedAmount: TextView
    private lateinit var mReceivedBtn: LinearLayout
    private lateinit var mDebtBtn: LinearLayout
    private lateinit var mDebtTxt: TextView
    private lateinit var mReceivedTxt: TextView
    private lateinit var mReceiptBtn: CardView
    private lateinit var mExpenditureBtn: CardView
    private lateinit var mRecyclerLayout: CardView
    private lateinit var mSeeAllBtn: CardView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTransactionMessage: TextView
    private lateinit var mErrorMessage: TextView
    private lateinit var mTransactionImage: ImageView
    private lateinit var mHideAndSee: ImageView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button

    private val mTransactionList = mutableListOf<AdminPaymentModel>()
    private lateinit var mAdapter: AdminPaymentDashboardAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            settingsButton = findViewById(R.id.settingsButton)
            mMainLayout = findViewById(R.id.dashboard_view)
            mReceivedBtn = findViewById(R.id.received_btn)
            mRecyclerView = findViewById(R.id.transaction_recycler)
            mDebtBtn = findViewById(R.id.debt_btn)
            mDebtTxt = findViewById(R.id.debt_txt)
            mReceivedTxt = findViewById(R.id.received_txt)
            mTransactionImage = findViewById(R.id.error_image)
            mTransactionMessage = findViewById(R.id.transaction_error_message)
            mErrorMessage = findViewById(R.id.error_message)
            mHideAndSee = findViewById(R.id.hide_and_See)
            mExpenditureBtn = findViewById(R.id.expenditure_btn)
            mReceiptBtn = findViewById(R.id.receipt_btn)
            mRecyclerLayout = findViewById(R.id.recycler_layout)
            mExpectedAmount = findViewById(R.id.expected_revenue)
            mSeeAllBtn = findViewById(R.id.see_all_btn)
            mErrorView = findViewById(R.id.error_view)
            mRefreshBtn = findViewById(R.id.refresh_btn)
        }


        mAdapter = AdminPaymentDashboardAdapter(requireContext(), mTransactionList, this)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

        refreshData()

        handleViewsClick()
    }


    private fun getDashboardDetails() {
        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        val term = sharedPreferences.getString("term", "")
        val year = sharedPreferences.getString("school_year", "")
        val isHide = sharedPreferences.getBoolean("hide", false)

        val url = "${getString(R.string.base_url)}/manageTransactions.php"
        val hashMap = hashMapOf<String, String>()

        hashMap.apply {
            put("dashboard", "1")
            put("term", term!!)
            put("year", year!!)
        }


        sendRequestToServer(Request.Method.POST, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {

                        JSONObject(response).run {
                            val receiptsSum = getJSONArray("receipts")
                                .getJSONObject(0)
                                .getString("sum")
                                .replace(".00", "")

                            val invoiceSum = getJSONArray("invoice")
                                .getJSONObject(0)
                                .getString("sum")
                                .replace(".00", "")

                            hideAndShowBalance(isHide, invoiceSum, receiptsSum)

                            if (has("transactions")) {
                                val transactionsArray = getJSONArray("transactions")

                                for (i in 0 until transactionsArray.length()) {
                                    val transactionsObject = transactionsArray.getJSONObject(i)
                                    val transactionType = transactionsObject.getString("trans_type")
                                    //  val reference = transactionsObject.getString("reference")
                                    val description = transactionsObject.getString("description")
                                    val amount = transactionsObject.getString("amount")
                                    val date = transactionsObject.getString("date")

                                    AdminPaymentModel().apply {
                                        mTransactionName = transactionType
                                        mDescription = description
                                        mReceivedAmount = amount
                                        mTransactionDate = date
                                    }.let { model ->
                                        mTransactionList.add(model)
                                    }

                                }

                                mTransactionList.sortByDescending { it.mTransactionDate }

                                mMainLayout.isVisible = true
                                mTransactionMessage.isVisible = false
                                mTransactionImage.isVisible = false
                                mErrorView.isVisible = false
                                mRecyclerLayout.isVisible = true

                                mAdapter.notifyDataSetChanged()

                            } else {
                                mMainLayout.isVisible = true
                                mTransactionMessage.isVisible = true
                                mTransactionImage.isVisible = true
                                mErrorView.isVisible = false
                                mRecyclerLayout.isVisible = false
                            }

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        mMainLayout.isVisible = false
                        mErrorView.isVisible = true
                        mRefreshBtn.isVisible = false
                        mErrorMessage.text = getString(R.string.contact_developer)

                    }
                }

                override fun onError(error: VolleyError) {
                    mMainLayout.isVisible = false
                    mErrorView.isVisible = true
                }
            }
        )
    }


    private fun hideAndShowBalance(isHide: Boolean, balance: String, paid: String) {

        try {
            val notPaid = balance.toDouble() - paid.toDouble()

            val debt = if (notPaid == 0.0 || paid == "null") {
                getString(R.string.zero_balance)
            } else {
                String.format(
                    Locale.getDefault(), "%s%s", getString(R.string.naira),
                    currencyFormat(notPaid)
                )
            }

            val amount = if (balance == "null") {
                getString(R.string.zero_balance)
            } else {
                String.format(
                    Locale.getDefault(), "%s%s", getString(R.string.naira),
                    currencyFormat(balance.toDouble())
                )
            }

            val income = if (paid == "null") {
                getString(R.string.zero_balance)
            } else {
                String.format(
                    Locale.getDefault(), "%s%s", getString(R.string.naira),
                    currencyFormat(paid.toDouble())
                )
            }

            if (isHide) {
                mHideAndSee.setImageResource(R.drawable.ic_visibility_off)
                mExpectedAmount.text = getString(R.string.hide)
                mDebtTxt.text = getString(R.string.hide)
                mReceivedTxt.text = getString(R.string.hide)
            } else {
                mHideAndSee.setImageResource(R.drawable.ic_eye_view)
                mExpectedAmount.text = amount
                mDebtTxt.text = debt
                mReceivedTxt.text = income
            }

            var mHide = isHide

            mHideAndSee.setOnClickListener {

                if (mHide) {
                    mExpectedAmount.text = amount
                    mDebtTxt.text = debt
                    mReceivedTxt.text = income
                    mHideAndSee.setImageResource(R.drawable.ic_eye_view)
                } else {
                    mExpectedAmount.text = getString(R.string.hide)
                    mDebtTxt.text = getString(R.string.hide)
                    mReceivedTxt.text = getString(R.string.hide)
                    mHideAndSee.setImageResource(R.drawable.ic_visibility_off)
                }
                mHide = !mHide

                requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE).edit()
                    .putBoolean("hide", mHide).apply()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun handleViewsClick() {
        mExpenditureBtn.setOnClickListener {
            startActivity(
                Intent(activity, PaymentActivity().javaClass)
                    .putExtra("from", "expenditure")
            )
        }

        mReceiptBtn.setOnClickListener {
            startActivity(
                Intent(activity, PaymentActivity().javaClass)
                    .putExtra("from", "receipt")
            )
        }

        mSeeAllBtn.setOnClickListener {
            startActivity(
                Intent(activity, PaymentActivity().javaClass)
                    .putExtra("from", "see_all")
            )
        }

        mReceivedBtn.setOnClickListener {
            AdminClassesDialog(requireContext(), "received", "", null)
                .apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

        mDebtBtn.setOnClickListener {
            AdminClassesDialog(requireContext(), "debt", "", null)
                .apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

        settingsButton.setOnClickListener {
            startActivity(
                Intent(requireContext(), PaymentActivity::class.java)
                    .putExtra("from", "settings")
            )
        }

    }


    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            mTransactionList.clear()
            getDashboardDetails()
        }
    }

    override fun onResume() {
        super.onResume()
        mTransactionList.clear()
        getDashboardDetails()
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(requireContext(), ":)", Toast.LENGTH_SHORT).show()
    }
}

//keytool -exportcert -rfc -alias key1 -file upload_certificate.pem -keystore upload_key.jks