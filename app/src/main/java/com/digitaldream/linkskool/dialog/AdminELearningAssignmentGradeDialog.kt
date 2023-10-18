package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.digitaldream.linkskool.R

class AdminELearningAssignmentGradeDialog(
    context: Context,
    private val point: (point: String) -> Unit
) : Dialog(context) {


    private lateinit var mPointBtn: RadioButton
    private lateinit var mNoPointBtn: RadioButton
    private lateinit var mPointEditText: EditText
    private lateinit var mCancelBtn: Button
    private lateinit var mSaveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        setContentView(R.layout.dialog_admin_e_learning_assignment_grade)

        mPointBtn = findViewById(R.id.pointButton)
        mNoPointBtn = findViewById(R.id.noPointButton)
        mPointEditText = findViewById(R.id.pointEditText)
        mCancelBtn = findViewById(R.id.cancelButton)
        mSaveBtn = findViewById(R.id.saveButton)


        mSaveBtn.setOnClickListener {
            if (mPointBtn.isChecked) {
                if (mPointEditText.text.toString().isNotEmpty()) {
                    point(mPointEditText.text.toString())
                    dismiss()
                } else {
                    Toast.makeText(context, "Set point to save", Toast.LENGTH_SHORT).show()
                }
            } else if (mNoPointBtn.isChecked) {
                point("Unmarked")
                dismiss()
            } else {
                Toast.makeText(context, "Please select a grade to save", Toast.LENGTH_SHORT).show()
            }
        }

        mCancelBtn.setOnClickListener {
            dismiss()
        }
    }
}