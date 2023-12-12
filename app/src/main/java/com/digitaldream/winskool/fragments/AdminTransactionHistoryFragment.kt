package com.digitaldream.winskool.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.AdminTransactionHistoryAdapter
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.models.AdminPaymentModel
import org.json.JSONObject


class AdminTransactionHistoryFragment : Fragment(), OnItemClickListener {

    private lateinit var mMainLayout: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mErrorMessage: TextView
    private lateinit var mRefreshBtn: Button

    private val mTransactionList = mutableListOf<AdminPaymentModel>()
    private lateinit var mAdapter: AdminTransactionHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_admin_transaction_history, container,
            false
        )

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mMainLayout = view.findViewById(R.id.transaction_layout)
        mRecyclerView = view.findViewById(R.id.transaction_recycler)
        mErrorMessage = view.findViewById(R.id.error_message)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)

        toolbar.apply {
            title = "Transaction History"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }

        mAdapter = AdminTransactionHistoryAdapter(requireContext(), mTransactionList, this)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

        refreshData()

        return view
    }

    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            mTransactionList.clear()
            getDashboardDetails()
        }
    }

    private fun getDashboardDetails() {
        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        val db = sharedPreferences.getString("db", "")
        val term = sharedPreferences.getString("term", "")
        val year = sharedPreferences.getString("school_year", "")

        val progressFlower = ACProgressFlower.Builder(context)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .textMarginTop(10)
            .fadeColor(ContextCompat.getColor((context as AppCompatActivity), R.color.color_5))
            .build()
        progressFlower.setCancelable(false)
        progressFlower.setCanceledOnTouchOutside(false)
        progressFlower.show()

        val url = "${getString(R.string.base_url)}/manageTransactions" +
                ".php?dashboard=1&&term=$term&&year=$year&&_db=$db"
        val stringRequest = object : StringRequest(
            Method.GET,
            url,
            { response: String ->
                Log.i("response", response)
                progressFlower.dismiss()

                try {
                    val jsonObject = JSONObject(response)
                    val transactionsArray = jsonObject.getJSONArray("transactions")

                    for (i in 0 until transactionsArray.length()) {
                        val transactionsObject = transactionsArray.getJSONObject(i)
                        val transactionType = transactionsObject.getString("trans_type")
                        //val reference = transactionsObject.getString("reference")
                        val description = transactionsObject.getString("description")
                        val amount = transactionsObject.getString("amount")
                        val date = transactionsObject.getString("date")

                        val adminModel = AdminPaymentModel()
                        adminModel.mTransactionName = transactionType
                        adminModel.mDescription = description
                        adminModel.mReceivedAmount = amount
                        adminModel.mTransactionDate = date
                        mTransactionList.add(adminModel)
                        mTransactionList.sortByDescending { it.mTransactionDate }
                    }

                    if (mTransactionList.isEmpty()) {
                        mMainLayout.isVisible = false
                        mErrorMessage.isVisible = true
                        mErrorMessage.text = getString(R.string.nothing_to_show)
                        mRefreshBtn.isVisible = false
                    } else {
                        mMainLayout.isVisible = true
                        mErrorMessage.isVisible = false
                        mRefreshBtn.isVisible = false
                    }
                    mAdapter.notifyItemChanged(mTransactionList.size - 1)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, { error: VolleyError ->
                error.printStackTrace()
                progressFlower.dismiss()
                mMainLayout.isVisible = false
                mErrorMessage.isVisible = true
                mRefreshBtn.isVisible = true
                mErrorMessage.text = getString(R.string.can_not_retrieve)
            }) {

        }

        Volley.newRequestQueue(requireContext()).add(stringRequest)

    }

    override fun onItemClick(position: Int) {
        Toast.makeText(requireContext(), ":)", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        mTransactionList.clear()
        getDashboardDetails()
    }

}