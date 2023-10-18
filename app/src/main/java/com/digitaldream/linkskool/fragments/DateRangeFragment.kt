package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.CourseAttendance.getDate
import com.digitaldream.linkskool.dialog.DatePickerBottomSheet
import com.digitaldream.linkskool.interfaces.DateListener
import com.digitaldream.linkskool.models.TimeFrameDataModel
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.selectDeselectButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DateRangeFragment(
    private val sTimeFrameDataModel: TimeFrameDataModel,
) : Fragment(R.layout.fragment_date_range) {

    private lateinit var mCustomBtn: Button
    private lateinit var mTodayBtn: Button
    private lateinit var mYesterdayBtn: Button
    private lateinit var mThisWeekBtn: Button
    private lateinit var mLast7DaysBtn: Button
    private lateinit var mLastWeekBtn: Button
    private lateinit var mThisMonthBtn: Button
    private lateinit var mLast30DaysBtn: Button
    private lateinit var mStartDateInput: EditText
    private lateinit var mEndDateInput: EditText
    private lateinit var mStartDateTxt: TextView
    private lateinit var mEndDateTxt: TextView

    private var mStartDate: String? = null
    private var mEndDate: String? = null

    private lateinit var buttonList: MutableList<Button>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            mCustomBtn = findViewById(R.id.custom_btn)
            mTodayBtn = findViewById(R.id.today_btn)
            mYesterdayBtn = findViewById(R.id.yesterday_btn)
            mThisWeekBtn = findViewById(R.id.this_week_btn)
            mLast7DaysBtn = findViewById(R.id.last_7_days_btn)
            mLastWeekBtn = findViewById(R.id.last_week_btn)
            mThisMonthBtn = findViewById(R.id.this_month_btn)
            mLast30DaysBtn = findViewById(R.id.last_30_days_btn)
            mStartDateInput = findViewById(R.id.start_date)
            mEndDateInput = findViewById(R.id.end_date)
            mStartDateTxt = findViewById(R.id.start_date_txt)
            mEndDateTxt = findViewById(R.id.end_date_txt)

        }

        buttonList = mutableListOf(
            mTodayBtn, mYesterdayBtn, mThisWeekBtn, mLast7DaysBtn,
            mLastWeekBtn, mThisMonthBtn, mLast30DaysBtn, mCustomBtn
        )


        if (sTimeFrameDataModel.startDate != null && sTimeFrameDataModel.endDate != null) {
            mStartDateInput.setText(FunctionUtils.formatDate("${sTimeFrameDataModel.startDate}"))
            mEndDateInput.setText(FunctionUtils.formatDate("${sTimeFrameDataModel.endDate}"))
        } else {
            defaultDate()
        }


        when (sTimeFrameDataModel.duration) {
            "today", "yesterday", "this week", "last 7 days", "last week", "this month", "last 30 days" -> {
                selectDeselectButton(getButtonByDuration(sTimeFrameDataModel.duration), "selected")
                disableDateRangeInputAndText()
            }

            else -> selectDeselectButton(mCustomBtn, "selected")
        }


        customStartDate()

        customEndDate()

        buttonClicks()

    }


    private fun defaultDate() {
        try {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(getDate())
            calendar.time = date!!
            calendar.add(Calendar.MONTH, -1)
            mStartDate = sdf.format(calendar.time)

            mStartDateInput.setText(FunctionUtils.formatDate(mStartDate!!))
            mEndDateInput.setText(FunctionUtils.formatDate((getDate())))

            sTimeFrameDataModel.startDate = mStartDate
            sTimeFrameDataModel.endDate = getDate()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun customStartDate() {

        mStartDateInput.setOnClickListener {

            DatePickerBottomSheet(
                "start date",
                object : DateListener {
                    override fun selectedItem(selectedDate: String) {
                        if (selectedDate.isNotEmpty()) {
                            mStartDateInput.setText(FunctionUtils.formatDate(selectedDate))

                            mEndDate = FunctionUtils.getEndDate(selectedDate)
                            mEndDateInput.setText(FunctionUtils.formatDate(mEndDate!!))

                            sTimeFrameDataModel.startDate = selectedDate
                            sTimeFrameDataModel.endDate = mEndDate
                        }

                    }
                }
            ).show(requireActivity().supportFragmentManager, "Time frame")

        }

    }

    private fun customEndDate() {

        mEndDateInput.setOnClickListener {
            DatePickerBottomSheet(
                "end date",
                object : DateListener {
                    override fun selectedItem(selectedDate: String) {
                        if (selectedDate.isNotEmpty()) {
                            mEndDateInput.setText(FunctionUtils.formatDate(selectedDate))

                            sTimeFrameDataModel.endDate = selectedDate
                        }

                    }
                }
            ).show(requireActivity().supportFragmentManager, "Time frame")
        }
    }

    private fun buttonClicks() {

        buttonList.forEach { button ->
            button.setOnClickListener {
                handleButtonClick(button)
            }

        }
    }

    private fun handleButtonClick(button: Button) {
        when (button.id) {

            R.id.custom_btn -> {
                if (!mCustomBtn.isSelected) {
                    deselectAllButtonExcept(mCustomBtn)
                    enableDateRangeInputAndText()
                    defaultDate()
                } else {
                    selectDeselectButton(mCustomBtn, "deselected")
                    disableDateRangeInputAndText()
                }
            }

            R.id.today_btn -> {
                if (!mTodayBtn.isSelected) {
                    deselectAllButtonExcept(mTodayBtn)
                    sTimeFrameDataModel.duration = "today"
                    disableDateRangeInputAndText()
                } else {
                    selectDeselectButton(mTodayBtn, "deselected")
                    sTimeFrameDataModel.duration = null
                }
            }

            R.id.yesterday_btn -> {
                if (!mYesterdayBtn.isSelected) {
                    deselectAllButtonExcept(mYesterdayBtn)
                    sTimeFrameDataModel.duration = "yesterday"
                    disableDateRangeInputAndText()
                } else {
                    selectDeselectButton(mYesterdayBtn, "deselected")
                    sTimeFrameDataModel.duration = null
                }
            }

            R.id.this_week_btn -> {
                if (!mThisWeekBtn.isSelected) {
                    deselectAllButtonExcept(mThisWeekBtn)
                    sTimeFrameDataModel.duration = "this week"
                    disableDateRangeInputAndText()

                } else {
                    selectDeselectButton(mThisWeekBtn, "deselected")
                    sTimeFrameDataModel.duration = null
                }
            }

            R.id.last_7_days_btn -> {
                if (!mLast7DaysBtn.isSelected) {
                    deselectAllButtonExcept(mLast7DaysBtn)
                    sTimeFrameDataModel.duration = "last 7 days"
                    disableDateRangeInputAndText()
                } else {
                    selectDeselectButton(mLast7DaysBtn, "deselected")
                    sTimeFrameDataModel.duration = null
                }
            }

            R.id.last_week_btn -> {
                if (!mLastWeekBtn.isSelected) {
                    deselectAllButtonExcept(mLastWeekBtn)
                    sTimeFrameDataModel.duration = "last week"
                    disableDateRangeInputAndText()
                } else {
                    selectDeselectButton(mLastWeekBtn, "deselected")
                    sTimeFrameDataModel.duration = null
                }
            }

            R.id.this_month_btn -> {
                if (!mThisMonthBtn.isSelected) {
                    deselectAllButtonExcept(mThisMonthBtn)
                    sTimeFrameDataModel.duration = "this month"
                    disableDateRangeInputAndText()
                } else {
                    selectDeselectButton(mThisMonthBtn, "deselected")
                    sTimeFrameDataModel.duration = null
                }
            }

            R.id.last_30_days_btn -> {
                if (!mLast30DaysBtn.isSelected) {
                    deselectAllButtonExcept(mLast30DaysBtn)
                    sTimeFrameDataModel.duration = "last 30 days"
                    disableDateRangeInputAndText()
                } else {
                    selectDeselectButton(mLast30DaysBtn, "deselected")
                    sTimeFrameDataModel.duration = null

                }

            }

        }

    }

    private fun deselectAllButtonExcept(selectedButton: Button) {
        buttonList.forEach { button ->
            if (button != selectedButton) {
                selectDeselectButton(button, "deselected")
            } else {
                selectDeselectButton(button, "selected")
            }
        }

    }

    private fun getButtonByDuration(duration: String?): Button {
        return when (duration) {
            "today" -> mTodayBtn
            "yesterday" -> mYesterdayBtn
            "this week" -> mThisWeekBtn
            "last 7 days" -> mLast7DaysBtn
            "last week" -> mLastWeekBtn
            "this month" -> mThisMonthBtn
            "last 30 days" -> mLast30DaysBtn
            else -> mCustomBtn
        }
    }

    private fun enableDateRangeInputAndText() {
        sTimeFrameDataModel.duration = null

        mStartDateInput.apply {
            isEnabled = true
            setBackgroundResource(R.drawable.edit_text_bg3)
        }

        mEndDateInput.apply {
            isEnabled = true
            setBackgroundResource(R.drawable.edit_text_bg3)
        }

        mStartDateTxt.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.test_color_1
            )
        )

        mEndDateTxt.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.test_color_1
            )
        )

    }

    private fun disableDateRangeInputAndText() {
        sTimeFrameDataModel.startDate = null
        sTimeFrameDataModel.endDate = null

        mStartDateInput.apply {
            isEnabled = false
            setBackgroundResource(R.drawable.edit_text_bg2)
        }

        mEndDateInput.apply {
            isEnabled = false
            setBackgroundResource(R.drawable.edit_text_bg2)

        }

        mStartDateTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.test_color_7))
        mEndDateTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.test_color_7))
    }


}