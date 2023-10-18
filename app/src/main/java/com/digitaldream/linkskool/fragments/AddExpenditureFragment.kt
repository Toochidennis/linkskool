package com.digitaldream.linkskool.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.AccountSetupDataModel
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"


class AddExpenditureFragment : Fragment() {

    private lateinit var mCustomerName: TextView
    private lateinit var mCustomerPhone: TextView
    private lateinit var mExpenditureAmount: EditText
    private lateinit var mDate: EditText
    private lateinit var mRefNo: EditText
    private lateinit var mReferenceSpinner: Spinner
    private lateinit var mPurpose: EditText
    private lateinit var mAddBtn: Button
    private lateinit var mRefreshBtn: ImageView

    private var customerName: String? = null
    private var customerPhone: String? = null
    private var customerReference: String? = null
    private var customerId: String? = null
    private var accountName: String? = null
    private var accountId: String? = null
    private var mSpinnerList = mutableListOf<AccountSetupDataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customerName = it.getString(ARG_PARAM1)
            customerReference = it.getString(ARG_PARAM2)
            customerPhone = it.getString(ARG_PARAM3)
            customerId = it.getString(ARG_PARAM4)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(name: String, reference: String, phone: String, id: String) =
            AddExpenditureFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, name)
                    putString(ARG_PARAM2, reference)
                    putString(ARG_PARAM3, phone)
                    putString(ARG_PARAM4, id)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_add_expenditure, container,
            false
        )

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mCustomerName = view.findViewById(R.id.customer_name)
        mCustomerPhone = view.findViewById(R.id.customer_phone)
        mRefNo = view.findViewById(R.id.reference_number_input)
        mExpenditureAmount = view.findViewById(R.id.expenditure_amount_input)
        mDate = view.findViewById(R.id.date_input)
        mReferenceSpinner = view.findViewById(R.id.spinner_account_reference)
        mPurpose = view.findViewById(R.id.purpose_input)
        mAddBtn = view.findViewById(R.id.add_expenditure_btn)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Record Expenditure"
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }

        mCustomerName.text = customerName
        mCustomerPhone.text = customerPhone

        setDate()

        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("account_reference", "")
        if (json!!.isNotEmpty()) {
            setSpinnerItem(json, "")
        } else {
            getAccountName()
        }

        mAddBtn.setOnClickListener {
            validateInput()
        }

        refreshSpinner()

        onSelectSpinnerItem()

        return view
    }

    private fun refreshSpinner() {
        mRefreshBtn.setOnClickListener {
            mSpinnerList.clear()
            getAccountName()
        }
    }

    private fun validateInput() {
        val amount = mExpenditureAmount.text.toString().trim()
        val date = mDate.text.toString().trim()
        val reference = mRefNo.text.toString().trim()
        val purpose = mPurpose.text.toString().trim()

        if (amount.isEmpty() || date.isEmpty() || reference.isEmpty() || purpose.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Provide all fields", Toast.LENGTH_SHORT).show()
        } else {
            val jsonObject = JSONObject().apply {
                put("amount", amount)
                put("description", purpose)
            }

            recordExpenditure(reference, date, purpose, jsonObject.toString(), amount)
            SystemClock.sleep(1000)
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getAccountName() {
        val url = getString(R.string.base_url) + "/manageAccount.php?list=1"
        val hashMap = hashMapOf<String, String>()

        sendRequestToServer(
            Request.Method.GET, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) = setSpinnerItem(response, "save")

                override fun onError(error: VolleyError) {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            })

    }

    private fun onSelectSpinnerItem() {
        mReferenceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                accountName = mSpinnerList[position].mAccountName
                accountId = mSpinnerList[position].mAccountId
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

    private fun setSpinnerItem(response: String, from: String) {
        try {
            val spinnerList = arrayListOf<String>()
            var spinnerAdapter: ArrayAdapter<String>? = null
            val jsonArray = JSONArray()
            JSONArray(response).also {
                for (i in 0 until it.length()) {
                    val jsonObject = it.getJSONObject(i)
                    val accountName = jsonObject.getString("account_name")
                    val accountId = jsonObject.getString("account_id")
                    val spinnerObject = JSONObject().apply {
                        put("account_name", accountName)
                        put("account_id", accountId)
                    }

                    jsonArray.put(spinnerObject)

                    val accountModel = AccountSetupDataModel().apply {
                        mAccountName = accountName
                        mAccountId = accountId
                    }

                    mSpinnerList.add(accountModel)
                    mSpinnerList.sortBy(AccountSetupDataModel::mAccountName)
                }
            }
            if (from == "save") {
                requireContext().getSharedPreferences(
                    "loginDetail",
                    Context.MODE_PRIVATE
                ).edit()
                    .putString("account_reference", jsonArray.toString())
                    .apply()
            }
            for (names in mSpinnerList) {
                spinnerList.add(names.mAccountName!!)
                spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    spinnerList
                )
            }
            mReferenceSpinner.adapter = spinnerAdapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDate() {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]

        mDate.setOnClickListener {

            DatePickerDialog(
                requireContext(),
                { _: DatePicker?, sYear: Int, sMonth: Int, sDayOfMonth: Int ->
                    val mont = sMonth + 1
                    val currentDate = "$sYear-$mont-$sDayOfMonth"
                    mDate.setText(currentDate)
                }, year, month, day
            ).show()
        }
    }

    private fun recordExpenditure(
        reference: String,
        date: String,
        description: String,
        json: String,
        amount: String,
    ) {

        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        val term = sharedPreferences.getString("term", "")
        val year = sharedPreferences.getString("school_year", "")

        val url = getString(R.string.base_url) + "/manageTransactions.php"
        val hashMap = hashMapOf<String, String>()
        hashMap.apply {
            put("transaction_type", "expenditure")
            put("customer_type", "2")
            put("customer_id", customerId!!)
            put("customer_name", customerName!!)
            put("customer_reference", customerReference!!)
            put("reference", reference)
            put("memo", description)
            put("description", json)
            put("date", date)
            put("amount", amount)
            put("account_name", accountName!!)
            put("account_number", accountId!!)
            put("year", year!!)
            put("term", term!!)
        }

        sendRequestToServer(Request.Method.POST, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

}