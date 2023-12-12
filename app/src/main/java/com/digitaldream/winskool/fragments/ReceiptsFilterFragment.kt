package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.FilterLevelClassDialog
import com.digitaldream.winskool.models.TimeFrameDataModel
import com.digitaldream.winskool.utils.FunctionUtils.parseFilterJson
import com.digitaldream.winskool.utils.FunctionUtils.selectDeselectButton
import org.json.JSONObject


class ReceiptsFilterFragment(
    private val sTimeFrameDataModel: TimeFrameDataModel
) : Fragment(R.layout.fragment_receipts_filter) {

    private lateinit var mClassBtn: Button
    private lateinit var mLevelBtn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mClassBtn = view.findViewById(R.id.class_btn)
        mLevelBtn = view.findViewById(R.id.level_btn)

        mClassBtn.setOnClickListener { onClick(it) }
        mLevelBtn.setOnClickListener { onClick(it) }

        selectDeselectedButton()
    }

    private fun onClick(view: View) {
        when (view.id) {
            R.id.class_btn -> {
                if (!mClassBtn.isSelected) {
                    selectDeselectButton(mClassBtn, "selected")

                    FilterLevelClassDialog(requireContext(), sTimeFrameDataModel, "class") {
                        setSelectedName()
                    }.apply {
                        setCancelable(false)
                        show()
                    }.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )


                } else {
                    selectDeselectButton(mClassBtn, "deselected")
                    sTimeFrameDataModel.classData = null
                    sTimeFrameDataModel.filter = sTimeFrameDataModel.levelData
                    "Class".let { mClassBtn.text = it }
                }
            }

            R.id.level_btn -> {
                if (!mLevelBtn.isSelected) {

                    selectDeselectButton(mLevelBtn, "selected")

                    FilterLevelClassDialog(requireContext(), sTimeFrameDataModel, "level") {
                        setSelectedName()
                    }.apply {
                        setCancelable(false)
                        show()
                    }.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                } else {
                    sTimeFrameDataModel.levelData = null
                    sTimeFrameDataModel.filter = sTimeFrameDataModel.classData
                    "Level".let { mLevelBtn.text = it }
                    selectDeselectButton(mLevelBtn, "deselected")
                }
            }

        }
    }

    private fun selectDeselectedButton() {
        if (sTimeFrameDataModel.levelData != null) {
            selectDeselectButton(mLevelBtn, "selected")
            setBtnText(
                mLevelBtn,
                parseFilterJson(sTimeFrameDataModel.levelData.toString(), "level"),
                "level"
            )
        }

        if (sTimeFrameDataModel.classData != null) {
            selectDeselectButton(mClassBtn, "selected")
            setBtnText(
                mClassBtn,
                parseFilterJson(sTimeFrameDataModel.classData.toString(), "class"),
                "class"
            )
        }
    }

    private fun setSelectedName() {
        if (sTimeFrameDataModel.levelData != null) {
            setBtnText(
                mLevelBtn,
                parseFilterJson(sTimeFrameDataModel.levelData.toString(), "level"),
                "level"
            )
            sTimeFrameDataModel.filter = sTimeFrameDataModel.levelData
        } else {
            selectDeselectButton(mLevelBtn, "deselected")
        }

        if (sTimeFrameDataModel.classData != null) {
            setBtnText(
                mClassBtn,
                parseFilterJson(sTimeFrameDataModel.classData.toString(), "class"),
                "class"
            )
            sTimeFrameDataModel.filter = sTimeFrameDataModel.classData
        } else {
            selectDeselectButton(mClassBtn, "deselected")
        }

        if (sTimeFrameDataModel.classData != null && sTimeFrameDataModel.levelData != null) {

            val jsonClass = JSONObject(sTimeFrameDataModel.classData!!).getJSONArray("class")
            val jsonLevel = JSONObject(sTimeFrameDataModel.levelData!!).getJSONArray("level")

            JSONObject().apply {
                put("level", jsonLevel)
                put("class", jsonClass)
            }.let {
                sTimeFrameDataModel.filter = it.toString()
            }

        }
        
    }


    private fun setBtnText(button: Button, name: String, from: String) {
        if (from == "level") {
            "Level: $name".let { button.text = it }
        } else {
            "Class: $name".let { button.text = it }
        }

    }

}

