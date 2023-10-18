package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.Window
import android.widget.Button
import androidx.core.content.ContextCompat
import com.digitaldream.linkskool.R
import com.google.android.material.textfield.TextInputLayout

class PaymentEmailDialog(
    sContext: Context,
    private var sInputListener: OnInputListener
) : Dialog(sContext) {

    private lateinit var inputLayout: TextInputLayout
    private lateinit var confirmInputLayout: TextInputLayout
    private lateinit var continueBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)
        setContentView(R.layout.dialog_email_payment)

        inputLayout = findViewById(R.id.email_input_layout)
        confirmInputLayout = findViewById(R.id.confirm_email_input_layout)
        continueBtn = findViewById(R.id.continue_btn)

        continueBtn.setOnClickListener {
            val email = confirmInputLayout.editText!!.text.toString().trim()
            sInputListener.sendInput(email)
            dismiss()
        }

        val regex = Regex("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]\$")

        continueBtn.isEnabled = false
        continueBtn.setBackgroundResource(R.drawable.ripple_effect)
        continueBtn.setTextColor(ContextCompat.getColor(context, R.color.black))

        val watcher: TextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailAddress = inputLayout.editText!!.text.toString().trim()
                val confirmEmailAddress = confirmInputLayout.editText!!.text.toString().trim()

                if (confirmEmailAddress != emailAddress && emailAddress.isNotBlank() &&
                    confirmEmailAddress.isNotBlank()
                ) {
                    confirmInputLayout.editText!!.error = "Email address miss-match"
                    continueBtn.isEnabled = false
                    continueBtn.setBackgroundResource(R.drawable.ripple_effect)
                    continueBtn.setTextColor(ContextCompat.getColor(context, R.color.black))
                } else if (!confirmEmailAddress.matches(regex) && confirmEmailAddress.isNotBlank()
                ) {
                    confirmInputLayout.editText!!.error = "Invalid email address"
                    continueBtn.isEnabled = false
                    continueBtn.setBackgroundResource(R.drawable.ripple_effect)
                    continueBtn.setTextColor(ContextCompat.getColor(context, R.color.black))
                } else if (!emailAddress.matches(regex) && emailAddress.isNotBlank()) {
                    inputLayout.editText!!.error = "Invalid email address"
                    continueBtn.isEnabled = false
                    continueBtn.setBackgroundResource(R.drawable.ripple_effect)
                    continueBtn.setTextColor(ContextCompat.getColor(context, R.color.black))
                } else if (emailAddress.matches(regex) && confirmEmailAddress.matches(regex)
                    && confirmEmailAddress == emailAddress
                ) {
                    confirmInputLayout.editText!!.error = null
                    inputLayout.editText!!.error = null
                    continueBtn.isEnabled = true
                    continueBtn.setBackgroundResource(R.drawable.ripple_effect2)
                    continueBtn.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        inputLayout.editText!!.addTextChangedListener(watcher)
        confirmInputLayout.editText!!.addTextChangedListener(watcher)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        try {
            sInputListener = context as OnInputListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }
}