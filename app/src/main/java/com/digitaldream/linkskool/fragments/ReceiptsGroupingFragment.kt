package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.TimeFrameDataModel
import com.digitaldream.linkskool.utils.FunctionUtils.selectDeselectButton


class ReceiptsGroupingFragment(
    private val sTimeFrameDataModel: TimeFrameDataModel
) : Fragment(R.layout.fragment_receipts_grouping) {

    private lateinit var mClassBtn: Button
    private lateinit var mLevelBtn: Button
    private lateinit var mMonthBtn: Button

    private lateinit var buttons: MutableList<Button>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            mClassBtn = findViewById(R.id.class_btn)
            mLevelBtn = findViewById(R.id.level_btn)
            mMonthBtn = findViewById(R.id.month_btn)
        }


        buttons = mutableListOf(
            mClassBtn, mLevelBtn, mMonthBtn
        )


        when (sTimeFrameDataModel.grouping) {
            "level", "class", "month" -> {
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
            R.id.class_btn -> {
                if (!mClassBtn.isSelected) {
                    deselectAllButtonsExcept(mClassBtn)
                    sTimeFrameDataModel.grouping = "class"
                } else {
                    selectDeselectButton(mClassBtn, "deselected")
                    sTimeFrameDataModel.grouping = null
                }
            }

            R.id.level_btn -> {
                if (!mLevelBtn.isSelected) {
                    deselectAllButtonsExcept(mLevelBtn)
                    sTimeFrameDataModel.grouping = "level"
                } else {
                    selectDeselectButton(mLevelBtn, "deselected")
                    sTimeFrameDataModel.grouping = null
                }
            }

            R.id.month_btn -> {
                if (!mMonthBtn.isSelected) {
                    deselectAllButtonsExcept(mMonthBtn)
                    sTimeFrameDataModel.grouping = "month"
                } else {
                    selectDeselectButton(mMonthBtn, "deselected")
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
            "level" -> mLevelBtn
            "class" -> mClassBtn
            else -> mMonthBtn
        }
    }

}