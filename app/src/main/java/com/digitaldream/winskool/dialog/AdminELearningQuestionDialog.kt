package com.digitaldream.winskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.digitaldream.winskool.R
import com.digitaldream.winskool.fragments.AdminELearningMultiChoiceDialogFragment
import com.digitaldream.winskool.models.MultiChoiceQuestion
import com.digitaldream.winskool.models.ShortAnswerModel

class AdminELearningQuestionDialog(
    context: Context,
    private val fragmentManager: FragmentManager,
    private val questionModel: MultiChoiceQuestion,
    private val shortAnswerModel: ShortAnswerModel,
    private val onQuestionSelected: (
        question: MultiChoiceQuestion?,
        shortQuestion: ShortAnswerModel?,
        section: String?
    ) -> Unit
) : Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        setContentView(R.layout.dialog_admin_e_learning_question)

        val shortAnswerBtn: TextView = findViewById(R.id.shortAnswerBtn)
        val multiChoiceBtn: TextView = findViewById(R.id.multiChoiceBtn)
        val sectionBtn: TextView = findViewById(R.id.sectionBtn)


        shortAnswerBtn.setOnClickListener {
            AdminELearningShortAnswerDialogFragment(shortAnswerModel) {
                onQuestionSelected(null, it, null)
            }.show(fragmentManager, "")

            dismiss()
        }

        multiChoiceBtn.setOnClickListener {
            AdminELearningMultiChoiceDialogFragment(questionModel) {
                onQuestionSelected(it, null, null)
            }.show(fragmentManager, "")

            dismiss()
        }

        sectionBtn.setOnClickListener {
            AdminELearningSectionDialog(context) {
                onQuestionSelected(null, null, it)
            }.apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            dismiss()
        }
    }
}