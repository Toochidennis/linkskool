package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.BuildConfig
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import com.digitaldream.winskool.activities.PaystackPaymentActivity
import com.digitaldream.winskool.adapters.StudentFeesDetailsAdapter
import com.digitaldream.winskool.dialog.OnInputListener
import com.digitaldream.winskool.dialog.PaymentEmailDialog
import com.digitaldream.winskool.models.TermFeesDataModel
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

private const val ARG_TERM = "term"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class StudentFeesDetailsFragment : Fragment(), OnInputListener {

    private lateinit var mCardView: CardView
    private lateinit var mSchoolName: TextView
    private lateinit var mTermTitle: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPaymentLayout: RelativeLayout
    private lateinit var mFeeTotal: TextView
    private lateinit var mFeeTotal2: TextView
    private lateinit var mPayBtn: Button
    private lateinit var mErrorMessage: TextView
    private lateinit var mRefreshBtn: Button

    private var mSession: String? = null
    private var mTerm: String? = null
    private var mNameSchool: String? = null
    private var mLevelId: String? = null
    private var mStudentId: String? = null
    private var mInvoiceId: String? = null
    private var mYear: String? = null
    private var mDb: String? = null
    private var mTotal: String? = null
    private var mFeesList: MutableList<TermFeesDataModel> = arrayListOf()
    private lateinit var mAdapter: StudentFeesDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mTerm = it.getString(ARG_TERM)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(term: String) = StudentFeesDetailsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TERM, term)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_student_fees_details, container,
            false
        )

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)
        mCardView = view.findViewById(R.id.card_view)
        mSchoolName = view.findViewById(R.id.school_name)
        mTermTitle = view.findViewById(R.id.debt_fee_title)
        mRecyclerView = view.findViewById(R.id.details_recycler)
        mPaymentLayout = view.findViewById(R.id.payment_layout)
        mFeeTotal = view.findViewById(R.id.fee_total)
        mFeeTotal2 = view.findViewById(R.id.total2)
        mPayBtn = view.findViewById(R.id.pay_btn)
        mErrorMessage = view.findViewById(R.id.error_message)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Fee Details"
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher
                    .onBackPressed()
            }
        }

        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        mNameSchool = sharedPreferences.getString("school_name", "")
        mLevelId = sharedPreferences.getString("level", "")
        mStudentId = sharedPreferences.getString("user_id", "")
        val mStudentEmail = sharedPreferences.getString("student_email", "")
        mDb = sharedPreferences.getString("db", "")

        mSchoolName.text = mNameSchool

        mAdapter = StudentFeesDetailsAdapter(requireContext(), mFeesList)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

        mRefreshBtn.setOnClickListener {
            getTermFees()
        }
        makePayment(mStudentEmail!!)

        return view
    }

    private fun makePayment(studentEmail: String) {
        mPayBtn.setOnClickListener {
            if (studentEmail.isNotBlank()) {
                requestURL(studentEmail)
            } else {
                val emailDialog = PaymentEmailDialog(requireContext(), this)
                    .apply {
                        setCancelable(true)
                        show()
                    }
                val window = emailDialog.window
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

        }

    }

    private fun getTermFees() {
        val progressFlower = ACProgressFlower.Builder(requireContext())
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .textMarginTop(10)
            .fadeColor(ContextCompat.getColor((context as AppCompatActivity), R.color.color_5))
            .build()
        progressFlower.setCancelable(false)
        progressFlower.setCanceledOnTouchOutside(false)
        progressFlower.show()
        val url = Login.urlBase + "/manageReceipts.php?list=$mStudentId"
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            { response: String ->
                Log.i("response", response)
                progressFlower.dismiss()
                if (response == "[]") {
                    mErrorMessage.isVisible = true
                    "You've have paid for this term".also { mErrorMessage.text = it }
                } else {
                    try {
                        val jsonObjects = JSONObject(response)
                        val invoiceArray = jsonObjects.getJSONArray("invoice")

                        for (i in 0 until invoiceArray.length()) {
                            val invoiceObject = invoiceArray.getJSONObject(i)
                            mInvoiceId = invoiceObject.getString("tid")
                            mYear = invoiceObject.getString("year")
                            val term = invoiceObject.getString("term")
                            mTotal = invoiceObject.getString("amount")
                                .replace(".00", "")
                            val descriptionArray = invoiceObject.getJSONArray("description")

                            for (j in 0 until descriptionArray.length()) {
                                val descriptionObject = descriptionArray.getJSONObject(j)
                                val feeName = descriptionObject.getString("fee_name")
                                val feeAmount = descriptionObject.getString("fee_amount")

                                val termFeesDataModel = TermFeesDataModel()
                                if (mTerm == term) {
                                    val previousYear = mYear!!.toInt() - 1
                                    mSession =
                                        String.format(
                                            Locale.getDefault(),
                                            "%d/%s",
                                            previousYear,
                                            mYear
                                        )

                                    termFeesDataModel.apply {
                                        mFeeName = feeName
                                        mFeeAmount = feeAmount

                                    }
                                    mFeesList.add(termFeesDataModel)
                                    mFeesList.sortBy { it.mFeeName }

                                    val termText = when (mTerm) {
                                        "1" -> "First Term School Fee Charges for"
                                        "2" -> "Second Term School Fee Charges for"
                                        else -> "Third Term School Fee Charges for"
                                    }

                                    mTermTitle.text = String.format(
                                        Locale.getDefault(),
                                        "%s %s %s", termText, mSession, "session"
                                    )

                                    mFeeTotal.text = String.format(
                                        Locale.getDefault(),
                                        "%s%s",
                                        requireActivity().getString(R.string.naira),
                                        currencyFormat(mTotal!!.toDouble())
                                    )

                                    mFeeTotal2.text = String.format(
                                        Locale.getDefault(),
                                        "%s%s",
                                        requireActivity().getString(R.string.naira),
                                        currencyFormat(mTotal!!.toDouble())
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (mFeesList.isEmpty()) {
                    mErrorMessage.isVisible = true
                    mErrorMessage.text = getString(R.string.no_data)
                    mCardView.isGone = true
                    mPaymentLayout.isGone = true
                } else {
                    mCardView.isGone = false
                    mPaymentLayout.isGone = false
                    mErrorMessage.isVisible = false
                    mRefreshBtn.isVisible = false
                }
                mAdapter.notifyDataSetChanged()

            }, { error: VolleyError ->
                error.printStackTrace()
                mErrorMessage.isVisible = true
                mRefreshBtn.isVisible = true
                mCardView.isGone = true
                mPaymentLayout.isGone = true
                mErrorMessage.text = getString(R.string.can_not_retrieve)
                progressFlower.dismiss()
            }) {
            override fun getParams(): Map<String, String> {
                val stringMap: MutableMap<String, String> = HashMap()
                stringMap["_db"] = mDb!!
                return stringMap
            }
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    private fun requestURL(sStudentEmail: String) {
        val builder = StringBuilder()
        val json = JSONObject()
            .put("email", sStudentEmail)
            .put("amount", "${mTotal!!.toLong() * 100}")
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
                val objects = jsonObject.getJSONObject("data")
                val authorizationURL = objects.getString("authorization_url")
                val reference = objects.getString("reference")
                println("url: $authorizationURL  $status  $reference")

                when (status) {
                    "true" -> {
                        val intent = Intent(activity, PaystackPaymentActivity::class.java)
                        intent.putExtra("url", authorizationURL)
                        intent.putExtra("reference", reference)
                        intent.putExtra("transaction_id", mInvoiceId)
                        intent.putExtra("amount", mTotal)
                        intent.putExtra("session", mSession)
                        intent.putExtra("term", mTerm)
                        intent.putExtra("year", mSession)
                        startActivity(intent)
                    }
                    else -> throw Exception("Can't generate url")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun sendInput(input: String) {
        requireContext().getSharedPreferences(
            "loginDetail",
            Context.MODE_PRIVATE
        ).edit()
            .putString("student_email", input)
            .apply()

        requestURL(input)
    }

    override fun sendId(levelId: String) {

    }

    override fun onResume() {
        super.onResume()
        mFeesList.clear()
        getTermFees()
    }
}