package com.digitaldream.winskool.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.adapters.VendorFragmentAdapter
import com.digitaldream.winskool.dialog.VendorDialog
import com.digitaldream.winskool.models.VendorModel
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import java.util.*


class VendorFragment : Fragment(), OnItemClickListener {

    private lateinit var mView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mErrorMessage: TextView
    private lateinit var mErrorMessage2: TextView
    private lateinit var mAddBtn: FloatingActionButton
    private lateinit var mRefreshBtn: Button
    private lateinit var mSearchInput: EditText
    private lateinit var mAdapter: VendorFragmentAdapter

    private val mVendorList = mutableListOf<VendorModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_vendor, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mView = view.findViewById(R.id.vendor_view)
        mRecyclerView = view.findViewById(R.id.vendor_recycler)
        mErrorView = view.findViewById(R.id.error_view)
        mErrorMessage = view.findViewById(R.id.vendor_error_message)
        mErrorMessage2 = view.findViewById(R.id.error_message)
        mAddBtn = view.findViewById(R.id.add_vendor)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)
        mSearchInput = view.findViewById(R.id.search_bar)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Select a vendor"
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher
                    .onBackPressed()
            }
        }


        mAddBtn.setOnClickListener {
            VendorDialog(requireContext(),
                object : OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        if (position == 0) {
                            mErrorView.isVisible = true
                            mErrorMessage2.isVisible = false
                        }
                    }
                }).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        mAdapter = VendorFragmentAdapter(mVendorList, this)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

        mSearchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                filterVendorName(s.toString())
            }
        })

        getVendor()

        refresh()

        return view
    }

    private fun filterVendorName(sName: String) {
        val filteredList = mutableListOf<VendorModel>()

        mVendorList.forEach {
            if (it.customerName.lowercase(Locale.getDefault())
                    .contains(
                        sName.lowercase(Locale.getDefault())
                    )
            ) {
                filteredList.add(it)
            }
        }
        mAdapter.filterList(filteredList)
    }

    private fun getVendor() {
        mVendorList.clear()
        val url = "${getString(R.string.base_url)}/manageVendor.php?list=2"
        val hashMap = hashMapOf<String, String>()

        sendRequestToServer(Request.Method.GET, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) = try {
                    JSONArray(response).run {
                        for (i in 0 until length()) {
                            val jsonObject = getJSONObject(i)
                            val id = jsonObject.getString("id")
                            val vendorName = jsonObject.getString("customername")
                            val vendorId = jsonObject.getString("customerid")
                            val telephone = jsonObject.getString("telephone")

                            val vendorModel = VendorModel()
                            vendorModel.customerName = vendorName
                            vendorModel.customerPhone = telephone
                            vendorModel.customerId = vendorId
                            vendorModel.id = id

                            mVendorList.run {
                                add(vendorModel)
                                sortBy { it.customerName }
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged()

                    if (mVendorList.isEmpty()) {
                        mAddBtn.isVisible = true
                        mErrorMessage.isVisible = true
                        mView.isVisible = true
                        mErrorView.isVisible = false
                        mSearchInput.isVisible = false
                    } else {
                        mAddBtn.isVisible = true
                        mErrorMessage.isVisible = false
                        mView.isVisible = true
                        mErrorView.isVisible = false
                        mSearchInput.isVisible = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                override fun onError(error: VolleyError) {
                    mAddBtn.isVisible = false
                    mView.isVisible = false
                    mErrorView.isVisible = true
                    mSearchInput.isVisible = false
                }
            }
        )
    }

    private fun refresh() {
        mRefreshBtn.setOnClickListener { getVendor() }
    }

    override fun onItemClick(position: Int) {
        val vendorModel = mVendorList[position]

        startActivity(
            Intent(requireContext(), PaymentActivity().javaClass)
                .putExtra("from", "vendor")
                .putExtra("vendor_name", vendorModel.customerName)
                .putExtra("vendorId", vendorModel.customerId)
                .putExtra("vendor_phone", vendorModel.customerPhone)
                .putExtra("id", vendorModel.id)
        )
    }

}