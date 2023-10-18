package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.Button
import com.digitaldream.linkskool.R

class StudentELearningQuizCompletionDialog(
    context: Context,
    private val onExit: () -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.apply {
            attributes.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        setCancelable(false)

        setContentView(R.layout.dialog_e_learning_quiz_completion)

        val exitBtn: Button = findViewById(R.id.exitBtn)

        exitBtn.setOnClickListener {
            onExit()
            dismiss()
        }

    }
}