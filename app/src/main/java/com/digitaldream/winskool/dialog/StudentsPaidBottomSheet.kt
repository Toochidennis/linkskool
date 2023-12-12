package com.digitaldream.winskool.dialog

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.digitaldream.winskool.R
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.winskool.utils.FunctionUtils.downloadPDF
import com.digitaldream.winskool.utils.FunctionUtils.sharePDF
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale

private const val STUDENT_NAME = "param1"
private const val LEVEL_NAME = "param4"
private const val CLASS_NAME = "param5"
private const val REG_NO = "param8"
private const val TERM = "param7"
private const val DATE = "param0"
private const val AMOUNT = "param9"
private const val REFERENCE = "param2"
private const val SESSION = "param6"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class StudentsPaidBottomSheet : BottomSheetDialogFragment() {

    private var mName: String? = null
    private var mLevelName: String? = null
    private var mClassName: String? = null
    private var mRegNo: String? = null
    private var mTerm: String? = null
    private var mDate: String? = null
    private var mAmount: String? = null
    private var mReference: String? = null
    private var mSession: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mName = it.getString(STUDENT_NAME)
            mLevelName = it.getString(LEVEL_NAME)
            mClassName = it.getString(CLASS_NAME)
            mRegNo = it.getString(REG_NO)
            mTerm = it.getString(TERM)
            mDate = it.getString(DATE)
            mAmount = it.getString(AMOUNT)
            mReference = it.getString(REFERENCE)
            mSession = it.getString(SESSION)

        }
    }

    companion object {

        fun newInstance(
            studentName: String,
            levelName: String,
            className: String,
            regNo: String,
            term: String,
            date: String,
            amount: String,
            reference: String,
            session: String,
        ) = StudentsPaidBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(STUDENT_NAME, studentName)
                    putString(LEVEL_NAME, levelName)
                    putString(CLASS_NAME, className)
                    putString(REG_NO, regNo)
                    putString(TERM, term)
                    putString(DATE, date)
                    putString(AMOUNT, amount)
                    putString(REFERENCE, reference)
                    putString(SESSION, session)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_students_paid, container, false)

        receiptDetails(view)

        return view
    }


    private fun receiptDetails(sView: View) {
        val schoolName: TextView = sView.findViewById(R.id.school_name)
        val receiptAmount: TextView = sView.findViewById(R.id.receipt_amount)
        val receiptDate: TextView = sView.findViewById(R.id.receipt_date)
        val studentName: TextView = sView.findViewById(R.id.student_name)
        val studentLevel: TextView = sView.findViewById(R.id.student_level)
        val studentClass: TextView = sView.findViewById(R.id.student_class)
        val registrationNo: TextView = sView.findViewById(R.id.registration_no)
        val session: TextView = sView.findViewById(R.id.session)
        val term: TextView = sView.findViewById(R.id.term)
        val referenceNumber: TextView = sView.findViewById(R.id.reference_number)
        val mReceiptCard: CardView = sView.findViewById(R.id.receipt_card)
        val downloadBtn: Button = sView.findViewById(R.id.download_receipt)
        val shareBtn: Button = sView.findViewById(R.id.share_receipt)

        val sharedPreferences = requireContext().getSharedPreferences(
            "loginDetail", Context
                .MODE_PRIVATE
        )
        val mSchoolName = sharedPreferences.getString("school_name", "")

        schoolName.text = mSchoolName

        String.format(
            Locale.getDefault(), "%s%s", getString(R.string.naira),
            currencyFormat(mAmount!!.toDouble())
        ).also { receiptAmount.text = it }

        receiptDate.text = mDate
        studentName.text = capitaliseFirstLetter(mName!!)
        studentLevel.text = mLevelName
        studentClass.text = mClassName
        registrationNo.text = mRegNo
        session.text = mSession
        term.text = mTerm
        referenceNumber.text = mReference

        mReceiptCard.isVisible = true

        downloadBtn.setOnClickListener {
            downloadPDF(mReceiptCard, requireActivity())
            dismiss()
        }

        shareBtn.setOnClickListener {
            sharePDF(mReceiptCard, requireActivity())
            dismiss()
        }
    }

}