package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.Window
import android.widget.Button
import com.digitaldream.linkskool.R
import com.google.android.material.textfield.TextInputLayout

class InsertLinkDialog(
    context: Context,
    private val url: (url: String) -> Unit
) : Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_insert_link)

        val linkEditText: TextInputLayout = findViewById(R.id.linkEditText)
        val cancelButton: Button = findViewById(R.id.cancelButton)
        val addButton: Button = findViewById(R.id.addButton)


        cancelButton.setOnClickListener {
            dismiss()
        }

        addButton.isEnabled = false

        linkEditText.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val url = s.toString().trim()

                val isValidUrl = Patterns.WEB_URL.matcher(url).matches()

                addButton.isEnabled = isValidUrl
            }
        })

        addButton.setOnClickListener {
            url(linkEditText.editText?.text.toString().trim())
            dismiss()
        }
    }
}