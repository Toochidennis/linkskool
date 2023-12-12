package com.digitaldream.winskool.dialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.interfaces.DateListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DatePickerBottomSheet(
    private val sFrom: String,
    private var sDateListener: DateListener
) : BottomSheetDialogFragment() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // inflates view of the dialog
        return inflater.inflate(R.layout.bottom_sheet_date_picker, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        val confirmBtn: CardView = view.findViewById(R.id.confirm_btn)
        val dismissBtn: ImageView = view.findViewById(R.id.close_btn)
        val title: TextView = view.findViewById(R.id.title)
        val datePicker: DatePicker = view.findViewById(R.id.date_picker)

        if (sFrom == "start date") {
            title.text = getString(R.string.start_date)
        } else title.text = getString(R.string.end_date)

        var selectedDate = ""
        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            selectedDate = "${year}-${monthOfYear + 1}-${dayOfMonth}"
        }


        confirmBtn.setOnClickListener {
            if (selectedDate.isEmpty()) {
                selectedDate = "${datePicker.year}-${datePicker.month + 1}-${datePicker.dayOfMonth}"
            }
            sDateListener.selectedItem(selectedDate)
            dismiss()
        }

        dismissBtn.setOnClickListener { dismiss() }

    }

}