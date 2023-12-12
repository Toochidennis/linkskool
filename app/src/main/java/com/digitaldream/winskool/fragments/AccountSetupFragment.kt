package com.digitaldream.winskool.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.AccountSetupAdapter
import com.digitaldream.winskool.dialog.AccountSetupDialog
import com.digitaldream.winskool.dialog.OnInputListener
import com.digitaldream.winskool.models.AccountSetupDataModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONException

class AccountSetupFragment : Fragment(), OnInputListener {

    private var mDb: String? = null

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AccountSetupAdapter
    private lateinit var mAccountList: MutableList<AccountSetupDataModel>
    private lateinit var mErrorMessage: TextView
    private lateinit var mAddAccountBtn: FloatingActionButton
    private lateinit var mRefreshBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account_setup, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mAddAccountBtn = view.findViewById(R.id.add_account_btn)
        mRecyclerView = view.findViewById(R.id.account_recycler)
        mErrorMessage = view.findViewById(R.id.error_message)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)

        ((activity as AppCompatActivity)).setSupportActionBar(toolbar)
        val actionBar = ((activity as AppCompatActivity)).supportActionBar

        actionBar!!.apply {
            setHomeButtonEnabled(true)
            title = "Account Settings"
            setHomeAsUpIndicator(R.drawable.arrow_left)
            setDisplayHomeAsUpEnabled(true)
        }


        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val sharedPreferences = requireActivity().getSharedPreferences(
            "loginDetail",
            Context.MODE_PRIVATE
        )
        mDb = sharedPreferences.getString("db", "")

        mAccountList = arrayListOf()
        mAdapter = AccountSetupAdapter(requireContext(), mAccountList, mErrorMessage)
        mRecyclerView.hasFixedSize()
        val manager = LinearLayoutManager(requireContext())
        mRecyclerView.layoutManager = manager
        mRecyclerView.adapter = mAdapter

        getAccount() //get Accounts
        openDialog()

        mRefreshBtn.setOnClickListener {
            refreshData() // refresh accounts
        }

        return view
    }

    private fun refreshData() {
        mAccountList.clear()
        getAccount()
    }

    private fun openDialog() {
        mAddAccountBtn.setOnClickListener {
            val accountDialog = AccountSetupDialog(
                requireContext(),
                "add", "",
                "",
                "",
                "",
                this
            ).apply {
                setCancelable(true)
                show()
            }
            val window = accountDialog.window
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

    }

    private fun getAccount() {
        val progressFlower = ACProgressFlower.Builder(requireContext())
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .textMarginTop(10)
            .fadeColor(ContextCompat.getColor((context as AppCompatActivity), R.color.color_5))
            .build()
        progressFlower.setCancelable(false)
        progressFlower.setCanceledOnTouchOutside(false)
        progressFlower.show()
        val url = "${getString(R.string.base_url)}/manageAccount.php?list=1&&_db=$mDb"
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            { response: String ->
                Log.i("response", response)
                progressFlower.dismiss()
                try {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getString("id")
                        val accountName = jsonObject.getString("account_name")
                        val accountId = jsonObject.getString("account_id")
                        val accountType = jsonObject.getString("account_type")

                        val accountModel = AccountSetupDataModel().apply {
                            mId = id
                            mAccountId = accountId
                            mAccountName = accountName
                            mAccountType = accountType
                        }

                        mAccountList.add(accountModel)
                        mAccountList.sortBy { it.mAccountId }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (mAccountList.isEmpty()) {
                    mErrorMessage.isVisible = true
                    mErrorMessage.text = getString(R.string.no_data)
                    mRecyclerView.isGone = true
                    mRefreshBtn.isVisible = false
                } else {
                    mRecyclerView.isGone = false
                    mErrorMessage.isVisible = false
                    mRefreshBtn.isVisible = false
                }
                mAdapter.notifyDataSetChanged()

            }, { error: VolleyError ->
                error.printStackTrace()
                mErrorMessage.isVisible = true
                mRecyclerView.isVisible = false
                mRefreshBtn.isVisible = true
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

    override fun sendInput(input: String) {
        if (input == "refresh") mRefreshBtn.isVisible = true
    }

    override fun sendId(levelId: String) {

    }

}

