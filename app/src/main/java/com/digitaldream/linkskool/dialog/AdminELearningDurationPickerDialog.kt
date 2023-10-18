package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.digitaldream.linkskool.R
import java.util.Calendar
import java.util.Locale

/**
 * AdminELearningDurationPickerDialog is a custom dialog that allows the user to select a time duration.
 *
 * @param context The context in which the dialog is created.
 * @param duration A callback function that receives the selected duration as a string.
 */

class AdminELearningDurationPickerDialog(
    context: Context,
    private var duration: (duration: String) -> Unit
) : Dialog(context) {

    private lateinit var startDurationBtn: Button
    private lateinit var endDurationBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var saveBtn: Button

    val calendar: Calendar = Calendar.getInstance()

    private var startTime: String? = null
    private var endTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_admin_e_learning_duration_picker)

        setUpViews()

        setTime(startDurationBtn, "start")
        setTime(endDurationBtn, "end")

        cancelBtn.setOnClickListener {
            dismiss()
        }

        saveBtn.setOnClickListener {
            sendData()
        }
    }

    private fun setUpViews() {
        startDurationBtn = findViewById(R.id.startDurationBtn)
        endDurationBtn = findViewById(R.id.endDurationBtn)
        cancelBtn = findViewById(R.id.cancelBtn)
        saveBtn = findViewById(R.id.saveBtn)
    }

    private fun setTime(button: Button, from: String) {
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        button.setOnClickListener {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val selectedTime =
                        String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)

                    if (from == "start")
                        startTime = selectedTime
                    else
                        endTime = selectedTime

                    button.text = selectedTime
                }, hour, minute, true
            ).show()
        }
    }

    private fun sendData() {
        if (startTime?.isNotBlank() == true && endTime?.isNotBlank() == true) {
            val startParts = startTime!!.split(":")
            val endParts = endTime!!.split(":")

            val startHour = startParts[0].toInt()
            val startMinute = startParts[1].toInt()

            val endHour = endParts[0].toInt()
            val endMinute = endParts[1].toInt()

            val totalStartMinutes = startHour * 60 + startMinute
            val totalEndMinutes = endHour * 60 + endMinute

            if (totalStartMinutes < totalEndMinutes) {
                val minutesDifference = totalEndMinutes - totalStartMinutes

                duration(minutesDifference.toString())

                dismiss()
            } else {
                Toast.makeText(
                    context, "Invalid duration. End time should be after start time",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(context, "Please provide duration", Toast.LENGTH_SHORT).show()
        }
    }
}