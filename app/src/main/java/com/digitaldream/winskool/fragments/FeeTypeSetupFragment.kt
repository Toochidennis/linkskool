package com.digitaldream.winskool.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.FeeTypeAdapter
import com.digitaldream.winskool.dialog.FeeTypeDialog
import com.digitaldream.winskool.models.FeeTypeModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONException


class FeeTypeSetupFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mErrorMessage: TextView
    private lateinit var mRefresh: SwipeRefreshLayout
    private lateinit var mAddBtn: FloatingActionButton
    private lateinit var mAdapter: FeeTypeAdapter
    private lateinit var mFeeList: MutableList<FeeTypeModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_fee_type_setup, container,
            false
        )

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        mRecyclerView = view.findViewById(R.id.fee_recycler)
        mAddBtn = view.findViewById(R.id.add_feeBtn)
        mErrorMessage = view.findViewById(R.id.error_message)
        mRefresh = view.findViewById(R.id.swipeRefresh)

        ((activity as AppCompatActivity)).setSupportActionBar(toolbar)
        val actionBar = ((activity as AppCompatActivity)).supportActionBar

        actionBar!!.apply {
            setHomeButtonEnabled(true)
            title = "Fee Settings"
            setHomeAsUpIndicator(R.drawable.arrow_left)
            setDisplayHomeAsUpEnabled(true)
        }

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        openDialog()

        mRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.color_5))
        mRefresh.setOnRefreshListener {
            refreshData()
            mRefresh.isRefreshing = false
        }

        mFeeList = arrayListOf()
        mAdapter = FeeTypeAdapter(requireContext(), mFeeList, mErrorMessage)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

        getFeeName()

        return view
    }

    private fun refreshData() {
        mFeeList.clear()
        getFeeName()
    }

    private fun openDialog() {
        mAddBtn.setOnClickListener {
            val feeTypeDialog = FeeTypeDialog(
                requireContext(), "add", "",
                "", 0
            )
            feeTypeDialog.setCancelable(true)
            feeTypeDialog.show()
            val window = feeTypeDialog.window
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun getFeeName() {

        val sharedPreferences = requireContext().getSharedPreferences(
            "loginDetail", Context.MODE_PRIVATE
        )
        val db = sharedPreferences.getString("db", "")

        val progressFlower = ACProgressFlower.Builder(context)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .textMarginTop(10)
            .fadeColor(ContextCompat.getColor((context as AppCompatActivity), R.color.color_5))
            .build()
        progressFlower.setCancelable(false)
        progressFlower.setCanceledOnTouchOutside(false)
        progressFlower.show()
        val url = getString(R.string.base_url) + "/manageFees.php?list=1&&_db=$db"
        val stringRequest: StringRequest = object : StringRequest(Method.GET,
            url,
            { response: String ->
                Log.i("response", response)
                progressFlower.dismiss()

                try {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val feeId = jsonObject.getString("id")
                        val feeName = jsonObject.getString("fee_name")
                        val mandatory = jsonObject.getString("mandatory")

                        println("FeeId: $feeId and FeName: $feeName")

                        val feeTypeModel = FeeTypeModel()
                        feeTypeModel.setFeeId(feeId.toInt())
                        feeTypeModel.setFeeName(feeName)
                        feeTypeModel.setMandatory(mandatory)

                        mFeeList.add(feeTypeModel)
                        mFeeList.sortBy { it.getFeeName() }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }


                if (mFeeList.isEmpty()) {
                    mErrorMessage.isVisible = true
                    mErrorMessage.text = getString(R.string.no_data)
                    mRecyclerView.isGone = true
                } else {
                    mRecyclerView.isGone = false
                    mErrorMessage.isVisible = false
                }
                mAdapter.notifyDataSetChanged()

            }, { error: VolleyError ->
                error.printStackTrace()
                mErrorMessage.isVisible = true
                mRecyclerView.isVisible = false
                mErrorMessage.text = getString(R.string.can_not_retrieve)
                progressFlower.dismiss()
            }) {
            override fun getParams(): Map<String, String> {
                val stringMap: MutableMap<String, String> = HashMap()
                stringMap["_db"] = db!!
                return stringMap
            }
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

}