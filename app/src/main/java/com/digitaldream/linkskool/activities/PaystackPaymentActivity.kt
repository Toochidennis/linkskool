package com.digitaldream.linkskool.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.linkskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.linkskool.utils.FunctionUtils.downloadPDF
import com.digitaldream.linkskool.utils.FunctionUtils.getDate
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.FunctionUtils.sharePDF
import com.digitaldream.linkskool.utils.VolleyCallback
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class PaystackPaymentActivity : AppCompatActivity(R.layout.activity_payment_paystack) {

    private var mStudentId: String? = null
    private var mClassId: String? = null
    private var mLevelId: String? = null
    private var mRegistrationNumber: String? = null
    private var mStudentName: String? = null
    private var mSchoolName: String? = null
    private var mClassName: String? = null
    private var mLevelName: String? = null
    private var mYear: String? = null
    private var mTerm: String? = null
    private var mSession: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission
                    .WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            ),
            PackageManager.PERMISSION_GRANTED
        )

        val intent = intent
        val reference = intent.getStringExtra("reference")
        val authorizationURL = intent.getStringExtra("url")
        val transactionId = intent.getStringExtra("transaction_id")
        mSession = intent.getStringExtra("session")
        mTerm = intent.getStringExtra("term")
        mYear = intent.getStringExtra("year")
        val amount = intent.getStringExtra("amount")

        val toolbar: Toolbar = findViewById(R.id.toolbar)

        toolbar.apply {
            title = "Pay"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        val sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        mStudentId = sharedPreferences.getString("user_id", "")
        mClassId = sharedPreferences.getString("classId", "")
        mLevelId = sharedPreferences.getString("level", "")
        mRegistrationNumber = sharedPreferences.getString("student_reg_no", "")
        mStudentName = sharedPreferences.getString("user", "")
        mSchoolName = sharedPreferences.getString("school_name", "")
        mClassName = sharedPreferences.getString("student_class", "")
        mLevelName = sharedPreferences.getString("level_name", "")

        loadCheckOut(authorizationURL!!, reference!!, transactionId!!, amount!!)

        writeReceipt()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadCheckOut(
        authorizationURL: String,
        sReference: String,
        sTransactionId: String,
        sAmount: String,
    ) {
        val webView: WebView = findViewById(R.id.web_view)
        val card: NestedScrollView = findViewById(R.id.receipt_view)

        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
        }

        webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?,
            ): Boolean {
                postReferenceNumber(sReference, sTransactionId, sAmount)
                webView.isVisible = false
                card.isVisible = true
                generateReceipt(sReference, sAmount)
                return false
            }
        }

        webView.loadUrl(authorizationURL)
    }

    private fun postReferenceNumber(sReference: String, sTransactionId: String, sAmount: String) {
        val url = Login.urlBase + "/manageReceipts.php"
        val stringMap = hashMapOf<String, String>()
        stringMap["invoice_id"] = sTransactionId
        stringMap["student_id"] = mStudentId!!
        stringMap["class"] = mClassId!!
        stringMap["level"] = mLevelId!!
        stringMap["reg_no"] = mRegistrationNumber!!
        stringMap["name"] = mStudentName!!
        stringMap["amount"] = sAmount
        stringMap["date"] = getDate()
        stringMap["reference"] = sReference
        stringMap["year"] = mYear!!
        stringMap["term"] = mTerm!!

        sendRequestToServer(Request.Method.POST, url, this, stringMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {

                }

                override fun onError(error: VolleyError) {

                }
            })
    }

    private fun generateReceipt(sReference: String, sAmount: String) {
        val schoolName: TextView = findViewById(R.id.school_name)
        val amount: TextView = findViewById(R.id.paid_amount)
        val status: TextView = findViewById(R.id.status)
        val date: TextView = findViewById(R.id.date)
        val name: TextView = findViewById(R.id.student_name)
        val level: TextView = findViewById(R.id.student_level)
        val studentClass: TextView = findViewById(R.id.student_class)
        val studentRegNo: TextView = findViewById(R.id.registration_no)
        val session: TextView = findViewById(R.id.session)
        val term: TextView = findViewById(R.id.term)
        val referenceNumber: TextView = findViewById(R.id.reference_number)

        schoolName.text = mSchoolName
        amount.text = String.format(
            Locale.getDefault(), "%s%s", getString(R.string.naira),
            currencyFormat(sAmount.toDouble())
        )
        status.text = getString(R.string.success)
        date.text = getDate()
        name.text = capitaliseFirstLetter(mStudentName!!)
        level.text = mLevelName
        studentClass.text = mClassName
        studentRegNo.text = mRegistrationNumber
        session.text = mSession
        when (mTerm) {
            "1" -> term.text = getString(R.string.first_term)
            "2" -> term.text = getString(R.string.second_term)
            "3" -> term.text = getString(R.string.third_term)
        }

        referenceNumber.text = sReference
    }

    private fun writeReceipt() {
        val receiptCard: CardView = findViewById(R.id.receipt_card)
        val shareReceipt: Button = findViewById(R.id.share_receipt)
        val downloadReceipt: Button = findViewById(R.id.download_receipt)

        downloadReceipt.setOnClickListener {
           downloadPDF(receiptCard, this)
        }

        shareReceipt.setOnClickListener {
            sharePDF(receiptCard, this)
        }
    }

}