package com.digitaldream.linkskool.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.utils.FunctionUtils
import java.util.*

private const val ARG_AMOUNT = "amount"
private const val ARG_REFERENCE = "reference"
private const val ARG_STATUS = "status"
private const val ARG_SESSION = "session"
private const val ARG_TERM = "term"
private const val ARG_DATE = "date"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class StudentTransactionReceiptFragment : Fragment() {

    private var mAmount: String? = null
    private var mReference: String? = null
    private var mStatus: String? = null
    private var mSession: String? = null
    private var mTerm: String? = null
    private var mDate: String? = null

    private lateinit var mReceiptCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mAmount = it.getString(ARG_AMOUNT)
            mReference = it.getString(ARG_REFERENCE)
            mStatus = it.getString(ARG_STATUS)
            mSession = it.getString(ARG_SESSION)
            mTerm = it.getString(ARG_TERM)
            mDate = it.getString(ARG_DATE)
        }

        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission
                    .WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            ),
            PackageManager.PERMISSION_GRANTED
        )

    }

    companion object {

        @JvmStatic
        fun newInstance(
            amount: String,
            reference: String,
            status: String,
            session: String,
            term: String,
            date: String,
        ) =
            StudentTransactionReceiptFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_AMOUNT, amount)
                    putString(ARG_REFERENCE, reference)
                    putString(ARG_STATUS, status)
                    putString(ARG_SESSION, session)
                    putString(ARG_TERM, term)
                    putString(ARG_DATE, date)
                }
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_receipt_transaction_student,
            container, false
        )

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mReceiptCard = view.findViewById(R.id.receipt_card)
        val shareBtn: Button = view.findViewById(R.id.share_receipt)
        val downloadBtn: Button = view.findViewById(R.id.download_receipt)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Payment Receipt"
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }

        downloadBtn.setOnClickListener {
            FunctionUtils.downloadPDF(mReceiptCard, requireActivity())
        }

        shareBtn.setOnClickListener {
            FunctionUtils.sharePDF(mReceiptCard, requireActivity())
        }

        generateReceipt(view)

        return view
    }

    private fun generateReceipt(view: View) {

        val schoolName: TextView = view.findViewById(R.id.school_name)
        val amount: TextView = view.findViewById(R.id.paid_amount)
        val status: TextView = view.findViewById(R.id.status)
        val date: TextView = view.findViewById(R.id.date)
        val name: TextView = view.findViewById(R.id.student_name)
        val level: TextView = view.findViewById(R.id.student_level)
        val studentClass: TextView = view.findViewById(R.id.student_class)
        val studentRegNo: TextView = view.findViewById(R.id.registration_no)
        val session: TextView = view.findViewById(R.id.session)
        val term: TextView = view.findViewById(R.id.term)
        val referenceNumber: TextView = view.findViewById(R.id.reference_number)

        val sharedPreferences = requireContext().getSharedPreferences(
            "loginDetail", Context
                .MODE_PRIVATE
        )
        val mSchoolName = sharedPreferences.getString("school_name", "")
        val mClass = sharedPreferences.getString("student_class", "")
        val studentName = sharedPreferences.getString("user", "")
        val mRegNo = sharedPreferences.getString("student_reg_no", "")
        val mLevelName = sharedPreferences.getString("level_name", "")

        schoolName.text = mSchoolName
        amount.text = mAmount
        status.text = mStatus
        date.text = mDate
        name.text = FunctionUtils.capitaliseFirstLetter(studentName!!)
        level.text = mLevelName
        studentClass.text = mClass
        studentRegNo.text = mRegNo
        session.text = mSession
        term.text = mTerm
        referenceNumber.text = mReference
    }


}