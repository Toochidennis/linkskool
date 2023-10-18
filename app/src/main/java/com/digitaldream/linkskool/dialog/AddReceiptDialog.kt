package com.digitaldream.linkskool.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.Window
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import java.util.*

class AddReceiptDialog(
    private val sContext: Context,
    private val sInvoiceId: String,
    private val sStudentId: String,
    private val sClassId: String,
    private val sLevelId: String,
    private val sRegistrationNumber: String,
    private val sStudentName: String,
    private val sAmount: String,
    private val sYear: String,
    private val sTerm: String,
    private var sOnItemClickListener: OnItemClickListener,
) : Dialog(sContext) {

    private lateinit var mStudentNameInput: EditText
    private lateinit var mAmountInput: EditText
    private lateinit var mReferenceNumberInput: EditText
    private lateinit var mDateInput: EditText
    private lateinit var mAddBtn: CardView
    private lateinit var mBackBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)
        setContentView(R.layout.dialog_add_receipt)

        mStudentNameInput = findViewById(R.id.student_name_input)
        mAmountInput = findViewById(R.id.amount_input)
        mReferenceNumberInput = findViewById(R.id.reference_input)
        mDateInput = findViewById(R.id.date_input)
        mAddBtn = findViewById(R.id.add_receipt_btn)
        mBackBtn = findViewById(R.id.back_btn)

        mBackBtn.setOnClickListener {
            dismiss()
        }

        mStudentNameInput.setText(sStudentName)
        String.format(
            Locale.getDefault(), "%s%s", context.getString(R.string.naira), currencyFormat(
                sAmount.toDouble()
            )
        ).also { mAmountInput.setText(it) }

        mAmountInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (mAmountInput.text.isEmpty()) {
                    mAmountInput.append(context.getString(R.string.naira))
                }

            }
        })

        setDate()

        mAddBtn.setOnClickListener {
            validateInput()
        }
    }

    private fun validateInput() {
        val studentName = mStudentNameInput.text.toString().trim()

        val amount = mAmountInput.text.toString().trim()
            .replace(context.getString(R.string.naira), "")
            .replace(",", "")

        val reference = mReferenceNumberInput.text.toString().trim()
        val date = mDateInput.text.toString().trim()

        if (studentName.isEmpty() || amount.isEmpty() || reference.isEmpty() || date.isEmpty()) {
            Toast.makeText(context, "Provide all fields", Toast.LENGTH_SHORT).show()
        } else {
            postReferenceNumber(reference, amount, date, studentName)
            dismiss()
        }
    }

    private fun postReferenceNumber(
        sReference: String,
        sAmount: String,
        sDate: String,
        sName: String,
    ) {
        val url = sContext.getString(R.string.base_url) + "/manageReceipts.php"
        val stringMap = hashMapOf<String, String>()
        stringMap["invoice_id"] = sInvoiceId
        stringMap["student_id"] = sStudentId
        stringMap["class"] = sClassId
        stringMap["level"] = sLevelId
        stringMap["reg_no"] = sRegistrationNumber
        stringMap["name"] = sName
        stringMap["amount"] = sAmount
        stringMap["date"] = sDate
        stringMap["reference"] = sReference
        stringMap["year"] = sYear
        stringMap["term"] = sTerm

        sendRequestToServer(Request.Method.POST, url, sContext, stringMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    sOnItemClickListener.onItemClick(0)
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun setDate() {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]

        mDateInput.setOnClickListener {

            DatePickerDialog(
                context,
                { _: DatePicker?, sYear: Int, sMonth: Int, sDayOfMonth: Int ->
                    val mont = sMonth + 1
                    val currentDate = "$sYear-$mont-$sDayOfMonth"
                    mDateInput.setText(currentDate)

                }, year, month, day
            ).show()
        }
    }

}