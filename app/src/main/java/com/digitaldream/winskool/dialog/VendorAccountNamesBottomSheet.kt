package com.digitaldream.winskool.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.VendorAccountNamesAdapter
import com.digitaldream.winskool.interfaces.OnVendorAccountClickListener
import com.digitaldream.winskool.models.AccountSetupDataModel
import com.digitaldream.winskool.models.TimeFrameDataModel
import com.digitaldream.winskool.models.VendorModel
import com.digitaldream.winskool.utils.FunctionUtils.getSelectedItem
import com.digitaldream.winskool.utils.FunctionUtils.onItemClick
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONArray

class VendorAccountNamesBottomSheet(
    private val sTimeFrameDataModel: TimeFrameDataModel,
    private val sFrom: String,
    private val sDismiss: () -> Unit
) : BottomSheetDialogFragment(), OnVendorAccountClickListener {

    private lateinit var mErrorMessage: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTitle: TextView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button
    private lateinit var mDoneBtn: Button
    private lateinit var mDismissBtn: ImageView

    private val mVendorList = mutableListOf<VendorModel>()
    private val mAccountList = mutableListOf<AccountSetupDataModel>()

    private val selectedItems = hashMapOf<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_vendor_account_names, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            mDismissBtn = findViewById(R.id.close_btn)
            mErrorMessage = findViewById(R.id.error_message)
            mRecyclerView = findViewById(R.id.recycler_view)
            mTitle = findViewById(R.id.title)
            mErrorView = findViewById(R.id.error_view)
            mRefreshBtn = findViewById(R.id.refresh_btn)
            mDoneBtn = findViewById(R.id.done_btn)
        }


        if (sFrom == "vendor") {
            vendorNames()
        } else {
            accountNames()
        }

        mDismissBtn.setOnClickListener { dismiss() }


    }

    private fun accountNames() {
        val url = "${getString(R.string.base_url)}/manageAccount.php?list=1"
        val hashMap = hashMapOf<String, String>()

        "Select Account".let { mTitle.text = it }
        mAccountList.clear()

        sendRequestToServer(Request.Method.GET, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        if (response != "[]") {
                            JSONArray(response).run {
                                for (i in 0 until length()) {
                                    val jsonObject = getJSONObject(i)
                                    val id = jsonObject.getString("id")
                                    val accountName = jsonObject.getString("account_name")

                                    AccountSetupDataModel().apply {
                                        mId = id
                                        mAccountName = accountName
                                    }.let {
                                        mAccountList.apply {
                                            add(it)
                                            sortBy { sort -> sort.mAccountName }
                                        }
                                    }
                                }
                            }

                            VendorAccountNamesAdapter(
                                mAccountList,
                                null,
                                this@VendorAccountNamesBottomSheet
                            ).run {
                                mRecyclerView.apply {
                                    isAnimating
                                    isVisible = true
                                    hasFixedSize()
                                    layoutManager = LinearLayoutManager(requireContext())
                                    adapter = this@run
                                }
                            }

                            mErrorView.isVisible = false

                        } else {
                            mRecyclerView.isVisible = false
                            mErrorView.isVisible = true
                            mRefreshBtn.isVisible = false
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                        mRecyclerView.isVisible = false
                        mErrorView.isVisible = true
                        mRefreshBtn.isVisible = false
                        mErrorMessage.text = getString(R.string.contact_developer)
                    }

                }

                override fun onError(error: VolleyError) {
                    mRecyclerView.isVisible = false
                    mErrorView.isVisible = true
                    mErrorMessage.text = getString(R.string.can_not_retrieve)
                }
            })


    }

    private fun vendorNames() {
        mVendorList.clear()
        val url = "${getString(R.string.base_url)}/manageVendor.php?list=2"
        val hashMap = hashMapOf<String, String>()

        "Select Vendor".let { mTitle.text = it }

        sendRequestToServer(Request.Method.GET, url, requireActivity(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        if (response != "[]") {
                            JSONArray(response).run {
                                for (i in 0 until length()) {
                                    val jsonObject = getJSONObject(i)
                                    val id = jsonObject.getString("id")
                                    val vendorName = jsonObject.getString("customername")

                                    VendorModel().apply {
                                        this.id = id
                                        this.customerName = vendorName
                                    }.let {
                                        mVendorList.apply {
                                            add(it)
                                            sortBy { sort -> sort.customerName }
                                        }
                                    }
                                }
                            }

                            VendorAccountNamesAdapter(
                                null,
                                mVendorList,
                                this@VendorAccountNamesBottomSheet
                            ).run {
                                mRecyclerView.apply {
                                    isAnimating
                                    isVisible = true
                                    hasFixedSize()
                                    layoutManager = LinearLayoutManager(requireContext())
                                    adapter = this@run
                                }
                            }

                            mErrorView.isVisible = false

                        } else {
                            mRecyclerView.isVisible = false
                            mErrorView.isVisible = true
                            mRefreshBtn.isVisible = false
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        mRecyclerView.isVisible = false
                        mErrorView.isVisible = true
                        mRefreshBtn.isVisible = false
                        mErrorMessage.text = getString(R.string.contact_developer)
                    }
                }

                override fun onError(error: VolleyError) {
                    mRecyclerView.isVisible = false
                    mErrorView.isVisible = true
                    mErrorMessage.text = getString(R.string.can_not_retrieve)
                }
            })
    }


    override fun onNameClick(holder: VendorAccountNamesAdapter.ViewHolder) {

        holder.itemView.setOnClickListener {

            if (mAccountList.isNotEmpty()) {
               val  accountItemPosition = mAccountList[holder.adapterPosition]

                onItemClick(
                    requireContext(),
                    accountItemPosition,
                    selectedItems,
                    holder.itemTextLayout,
                    holder.itemImageLayout,
                    buttonView = mDoneBtn,
                    dismissView = mDismissBtn
                )
            } else {
                val vendorItemPosition = mVendorList[holder.adapterPosition]

                onItemClick(
                    requireContext(),
                    vendorItemPosition,
                    selectedItems,
                    holder.itemTextLayout,
                    holder.itemImageLayout,
                    buttonView = mDoneBtn,
                    dismissView = mDismissBtn
                )
            }

        }

        performButtonClick(selectedItems)

    }


    private fun performButtonClick(selectedItem: HashMap<String, String>) {

        mDoneBtn.setOnClickListener {

            if (mAccountList.isNotEmpty()) {
                sTimeFrameDataModel.account = getSelectedItem(selectedItem, "account")
            } else {
                sTimeFrameDataModel.vendor = getSelectedItem(selectedItem, "cid")
            }

            dismiss()
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        sDismiss()
    }

}

