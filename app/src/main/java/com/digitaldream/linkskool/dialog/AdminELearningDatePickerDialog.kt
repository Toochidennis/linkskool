package com.digitaldream.linkskool.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.getDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdminELearningDatePickerDialog(
    context: Context,
    private val receiveDate: (startDate: String, endDate: String) -> Unit
) : Dialog(context) {

    private lateinit var mStartDateBtn: Button
    private lateinit var mEndDateBtn: Button
    private lateinit var mStartTimeBtn: Button
    private lateinit var mEndTimeBtn: Button
    private lateinit var mCancelBtn: Button
    private lateinit var mSaveBtn: Button

    private val calendar: Calendar = Calendar.getInstance()

    private var mStartDate: String? = null
    private var mEndDate: String? = null
    private var mStartTime: String? = null
    private var mEndTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_admin_e_learning_date_picker)

        mStartDateBtn = findViewById(R.id.startDateBtn)
        mEndDateBtn = findViewById(R.id.endDateBtn)
        mStartTimeBtn = findViewById(R.id.startTimeBtn)
        mEndTimeBtn = findViewById(R.id.endTimeBtn)
        mCancelBtn = findViewById(R.id.cancelBtn)
        mSaveBtn = findViewById(R.id.saveBtn)


        mStartDateBtn.text = formatDate2(getDate(), "custom")
        mEndDateBtn.text = formatDate2(getDate(), "custom")

        setDate(mStartDateBtn, "start")
        setDate(mEndDateBtn, "end")

        setTime(mStartTimeBtn, "start")
        setTime(mEndTimeBtn, "end")

        mSaveBtn.setOnClickListener {
            sendDate()
        }

        mCancelBtn.setOnClickListener {
            dismiss()
        }

    }

    private fun setDate(button: Button, from: String) {
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]

        button.setOnClickListener {
            DatePickerDialog(
                context,
                { _: DatePicker?, sYear: Int, sMonth: Int, sDayOfMonth: Int ->
                    val currentMonth = sMonth + 1
                    val currentDate = "$sYear-$currentMonth-$sDayOfMonth"

                    if (from == "start")
                        mStartDate = currentDate
                    else
                        mEndDate = currentDate

                    button.text = formatDate2(currentDate, "custom")
                }, year, month, day
            ).show()
        }
    }

    private fun setTime(button: Button, from: String) {
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        button.setOnClickListener {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val currentTime = "$hourOfDay:$minute"

                    if (from == "start")
                        mStartTime = currentTime
                    else
                        mEndTime = currentTime

                    button.text = currentTime
                }, hour, minute, true
            ).show()
        }
    }


    private fun sendDate() {
        try {
            if (mStartDate.isNullOrEmpty() && mStartTime.isNullOrEmpty()) {
                Toast.makeText(
                    context, "Start date or start time is empty", Toast.LENGTH_SHORT
                ).show()
            } else if (mEndDate.isNullOrEmpty() && mEndTime.isNullOrEmpty()) {
                Toast.makeText(
                    context, "End date or end time is empty", Toast.LENGTH_SHORT
                ).show()
            } else {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val startDate = "$mStartDate $mStartTime"
                val endDate = "$mEndDate $mEndTime"

                val start = dateFormat.parse(startDate)!!
                val end = dateFormat.parse(endDate)!!

                val startCalendar = Calendar.getInstance().apply {
                    time = start
                }

                val endCalendar = Calendar.getInstance().apply {
                    time = end
                }

                if (endCalendar.before(startCalendar) || endCalendar == startCalendar) {
                    Toast.makeText(
                        context, "End date and time must be in the future",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    receiveDate("$mStartDate $mStartTime:00", "$mEndDate $mEndTime:00")

                    dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}