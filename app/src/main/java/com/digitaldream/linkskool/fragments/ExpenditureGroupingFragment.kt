package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.TimeFrameDataModel
import com.digitaldream.linkskool.utils.FunctionUtils.selectDeselectButton


class ExpenditureGroupingFragment(
    private val sTimeFrameDataModel: TimeFrameDataModel
) : Fragment(R.layout.fragment_expenditure_grouping) {

    private lateinit var mMonthBtn: Button
    private lateinit var mVendorBtn: Button
    private lateinit var mAccountBtn: Button

    private lateinit var buttons: MutableList<Button>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMonthBtn = view.findViewById(R.id.month_btn)
        mVendorBtn = view.findViewById(R.id.vendor_btn)
        mAccountBtn = view.findViewById(R.id.account_btn)


        buttons = mutableListOf(
            mMonthBtn, mVendorBtn, mAccountBtn
        )

        when (sTimeFrameDataModel.grouping) {
            "account", "cid", "month" -> {
                selectDeselectButton(getButtonByGroup(sTimeFrameDataModel.grouping), "selected")
            }
        }

        buttonClicks()
    }


    private fun buttonClicks() {
        buttons.forEach { button ->
            button.setOnClickListener {
                handleButtonClick(button)
            }
        }
    }

    private fun handleButtonClick(view: View) {
        when (view.id) {

            R.id.month_btn -> {
                if (!mMonthBtn.isSelected) {
                    deselectAllButtonsExcept(mMonthBtn)
                    sTimeFrameDataModel.grouping = "month"
                } else {
                    selectDeselectButton(mMonthBtn, "deselected")
                    sTimeFrameDataModel.grouping = null
                }

            }

            R.id.vendor_btn -> {
                if (!mVendorBtn.isSelected) {
                    deselectAllButtonsExcept(mVendorBtn)
                    sTimeFrameDataModel.grouping = "cid"
                } else {
                    selectDeselectButton(mVendorBtn, "deselected")
                    sTimeFrameDataModel.grouping = null
                }
            }

            R.id.account_btn -> {
                if (!mAccountBtn.isSelected) {
                    deselectAllButtonsExcept(mAccountBtn)
                    sTimeFrameDataModel.grouping = "account"
                } else {
                    selectDeselectButton(mAccountBtn, "deselected")
                    sTimeFrameDataModel.grouping = null
                }
            }

        }
    }


    private fun deselectAllButtonsExcept(selectedBtn: Button) {
        buttons.forEach { button ->
            if (button != selectedBtn) {
                selectDeselectButton(button, "deselected")
            } else {
                selectDeselectButton(button, "selected")
            }
        }
    }

    private fun getButtonByGroup(group: String?): Button {
        return when (group) {
            "month" -> mMonthBtn
            "cid" -> mVendorBtn
            else -> mAccountBtn
        }
    }

}