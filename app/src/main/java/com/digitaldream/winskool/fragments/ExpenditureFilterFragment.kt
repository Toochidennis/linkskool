package com.digitaldream.winskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.VendorAccountNamesBottomSheet
import com.digitaldream.winskool.models.TimeFrameDataModel
import com.digitaldream.winskool.utils.FunctionUtils.parseFilterJson
import com.digitaldream.winskool.utils.FunctionUtils.selectDeselectButton
import org.json.JSONObject


class ExpenditureFilterFragment(
    private val sTimeFrameDataModel: TimeFrameDataModel
) : Fragment(R.layout.fragment_expenditure_filter) {

    private lateinit var mVendorBtn: Button
    private lateinit var mAccountBtn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mVendorBtn = view.findViewById(R.id.vendor_btn)
        mAccountBtn = view.findViewById(R.id.account_btn)

        mVendorBtn.setOnClickListener { onClick(it) }
        mAccountBtn.setOnClickListener { onClick(it) }

        selectDeselectedButton()

    }


    private fun onClick(view: View) {

        when (view.id) {
            R.id.vendor_btn -> {
                if (!mVendorBtn.isSelected) {
                    selectDeselectButton(mVendorBtn, "selected")

                    VendorAccountNamesBottomSheet(
                        sTimeFrameDataModel,
                        "vendor"
                    ) { setSelectedName() }
                        .show(childFragmentManager, "vendor")

                } else {
                    selectDeselectButton(mVendorBtn, "deselected")
                    sTimeFrameDataModel.vendor = null
                    sTimeFrameDataModel.filter = sTimeFrameDataModel.account
                    "Vendor".let { mVendorBtn.text = it }
                }
            }

            R.id.account_btn -> {
                if (!mAccountBtn.isSelected) {

                    selectDeselectButton(mAccountBtn, "selected")

                    VendorAccountNamesBottomSheet(
                        sTimeFrameDataModel,
                        "account"
                    ) { setSelectedName() }
                        .show(childFragmentManager, "account")

                } else {
                    sTimeFrameDataModel.account = null
                    sTimeFrameDataModel.filter = sTimeFrameDataModel.vendor
                    "Account".let { mAccountBtn.text = it }
                    selectDeselectButton(mAccountBtn, "deselected")
                }
            }

        }
    }


    private fun selectDeselectedButton() {
        if (sTimeFrameDataModel.account != null) {
            selectDeselectButton(mAccountBtn, "selected")
            setBtnText(
                mAccountBtn,
                parseFilterJson(sTimeFrameDataModel.account.toString(), "account"),
                "account"
            )
        }


        if (sTimeFrameDataModel.vendor != null) {
            selectDeselectButton(mVendorBtn, "selected")
            setBtnText(
                mVendorBtn,
                parseFilterJson(sTimeFrameDataModel.vendor.toString(), "cid"),
                "vendor"
            )
        }
    }


    private fun setSelectedName() {
        if (sTimeFrameDataModel.account != null) {
            setBtnText(
                mAccountBtn,
                parseFilterJson(sTimeFrameDataModel.account.toString(), "account"),
                "account"
            )
        } else {
            selectDeselectButton(mAccountBtn, "deselected")
        }


        if (sTimeFrameDataModel.vendor != null) {
            setBtnText(
                mVendorBtn,
                parseFilterJson(sTimeFrameDataModel.vendor.toString(), "cid"),
                "vendor"
            )
        } else {
            selectDeselectButton(mVendorBtn, "deselected")
        }


        if (sTimeFrameDataModel.account != null && sTimeFrameDataModel.vendor != null) {

            val accountJson = JSONObject(sTimeFrameDataModel.account!!).getJSONArray("account")
            val vendorJson = JSONObject(sTimeFrameDataModel.vendor!!).getJSONArray("cid")

            JSONObject().apply {
                put("cid", vendorJson)
                put("account", accountJson)
            }.let {
                sTimeFrameDataModel.filter = it.toString()
            }

        }

    }


    private fun setBtnText(button: Button, name: String, from: String) {
        if (from == "vendor") {
            "Vendor: $name".let { button.text = it }
        } else {
            "Account: $name".let { button.text = it }
        }

    }

}