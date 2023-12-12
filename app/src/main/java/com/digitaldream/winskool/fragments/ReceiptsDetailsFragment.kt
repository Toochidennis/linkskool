package com.digitaldream.winskool.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.digitaldream.winskool.R
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.winskool.utils.FunctionUtils.downloadPDF
import com.digitaldream.winskool.utils.FunctionUtils.sharePDF
import java.util.*


private const val ARG_AMOUNT = "amount"
private const val ARG_NAME = "name"
private const val ARG_LEVEL = "level"
private const val ARG_CLASS = "class"
private const val ARG_REG_NO = "reg"
private const val ARG_REFERENCE = "reference"
private const val ARG_SESSION = "session"
private const val ARG_TERM = "term"
private const val ARG_DATE = "date"


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class ReceiptsDetailsFragment : Fragment() {

    private var mAmount: String? = null
    private var mName: String? = null
    private var mLevelName: String? = null
    private var mClassName: String? = null
    private var mRegNo: String? = null
    private var mReference: String? = null
    private var mSession: String? = null
    private var mTerm: String? = null
    private var mDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mAmount = it.getString(ARG_AMOUNT)
            mName = it.getString(ARG_NAME)
            mLevelName = it.getString(ARG_LEVEL)
            mClassName = it.getString(ARG_CLASS)
            mRegNo = it.getString(ARG_REG_NO)
            mReference = it.getString(ARG_REFERENCE)
            mSession = it.getString(ARG_SESSION)
            mTerm = it.getString(ARG_TERM)
            mDate = it.getString(ARG_DATE)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(
            amount: String,
            name: String,
            levelName: String?,
            className: String?,
            regNo: String,
            reference: String,
            session: String,
            term: String,
            date: String,
        ) =
            ReceiptsDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_AMOUNT, amount)
                    putString(ARG_NAME, name)
                    putString(ARG_LEVEL, levelName)
                    putString(ARG_CLASS, className)
                    putString(ARG_REG_NO, regNo)
                    putString(ARG_REFERENCE, reference)
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
        val view = inflater.inflate(R.layout.fragment_receipts_details, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        toolbar.apply {
            title = "Receipt Details"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }


        if (mLevelName == null)
            expenditureDetails(view)
        else
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
        }

        shareBtn.setOnClickListener {
            sharePDF(mReceiptCard, requireActivity())
        }
    }

    private fun expenditureDetails(sView: View) {
        val schoolName: TextView = sView.findViewById(R.id.school_name2)
        val expenditureAmount: TextView = sView.findViewById(R.id.expenditure_amount)
        val expenditureDate: TextView = sView.findViewById(R.id.expenditure_date)
        val name: TextView = sView.findViewById(R.id.expenditure_name)
        val session: TextView = sView.findViewById(R.id.ex_session)
        val term: TextView = sView.findViewById(R.id.ex_term)
        val phone: TextView = sView.findViewById(R.id.phone)
        val referenceNumber: TextView = sView.findViewById(R.id.ex_reference_number)
        val description: TextView = sView.findViewById(R.id.description)
        val expenditureCard: CardView = sView.findViewById(R.id.expenditure_card)
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
        ).also { expenditureAmount.text = it }

        expenditureDate.text = mDate
        name.text = capitaliseFirstLetter(mName!!)
        session.text = mSession
        term.text = mTerm
        phone.text = mRegNo
        referenceNumber.text = mReference
        description.text = mClassName

        expenditureCard.isVisible = true

        downloadBtn.setOnClickListener {
            downloadPDF(expenditureCard, requireActivity())
        }

        shareBtn.setOnClickListener {
            sharePDF(expenditureCard, requireActivity())
        }
    }
}