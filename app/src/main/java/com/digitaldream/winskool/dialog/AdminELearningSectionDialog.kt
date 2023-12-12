package com.digitaldream.winskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.digitaldream.winskool.R
import com.google.android.material.textfield.TextInputLayout

class AdminELearningSectionDialog(
    context: Context,
    private val section: String = "",
    private val onSection: (section: String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(R.layout.dialog_admin_e_learning_section)


        val sectionInput: TextInputLayout = findViewById(R.id.sectionInput)
        val cancelButton: Button = findViewById(R.id.cancelButton)
        val addButton: Button = findViewById(R.id.addButton)

        sectionInput.editText?.setText(section)

        cancelButton.setOnClickListener {
            dismiss()
        }

        addButton.setOnClickListener {
            if (sectionInput.editText?.text.toString().isNotEmpty()) {
                onSection(sectionInput.editText?.text.toString().trim())
                dismiss()
            } else {
                sectionInput.error = "Please enter a section"
            }

        }
    }
}