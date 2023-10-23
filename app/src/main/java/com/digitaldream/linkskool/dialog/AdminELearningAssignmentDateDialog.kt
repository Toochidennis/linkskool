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
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.getDate
import java.util.Calendar

class AdminELearningAssignmentDateDialog(
    context: Context,
    private val onDateSelected: (String) -> Unit
) : Dialog(context) {

    private lateinit var dateBtn: Button
    private lateinit var timeBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var saveBtn: Button

    private var dateText: String? = null
    private var timeText: String? = null
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_admin_e_learning_assignment_date)

        setUpViews()

        initViews()

        handleBtnActions()
    }

    private fun setUpViews() {
        dateBtn = findViewById(R.id.dateBtn)
        timeBtn = findViewById(R.id.timeBtn)
        cancelBtn = findViewById(R.id.cancelBtn)
        saveBtn = findViewById(R.id.saveBtn)
    }

    private fun initViews() {
        dateText = getDate()
        dateBtn.text = formatDate2(dateText ?: "", "custom")
        timeText = "22:59"
    }

    private fun setDate() {
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]

        dateBtn.setOnClickListener {
            DatePickerDialog(
                context,
                { _: DatePicker?, sYear: Int, sMonth: Int, sDayOfMonth: Int ->
                    val currentMonth = sMonth + 1
                    dateText = "$sYear-$currentMonth-$sDayOfMonth"

                    dateBtn.text = formatDate2(dateText ?: "", "custom")
                }, year, month, day
            ).show()
        }
    }

    private fun setTime() {
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        timeBtn.setOnClickListener {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    timeText = "$hourOfDay:$minute"

                    timeBtn.text = timeText
                }, hour, minute, true
            ).show()
        }
    }

    private fun handleBtnActions() {
        setDate()

        setTime()

        cancelBtn.setOnClickListener {
            dismiss()
        }

        saveBtn.setOnClickListener {
            onDateSelected("$dateText $timeText:00")
            dismiss()
        }
    }
}