package com.digitaldream.linkskool.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.adapters.StudentFeesDetailsAdapter
import com.digitaldream.linkskool.dialog.AddReceiptDialog
import com.digitaldream.linkskool.models.TermFeesDataModel
import com.digitaldream.linkskool.utils.FunctionUtils.currencyFormat
import org.json.JSONArray
import java.util.Locale


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val STUDENT_NAME = "param1"
private const val STUDENT_ID = "param2"
private const val INVOICE_ID = "param3"
private const val LEVEL_ID = "param4"
private const val CLASS_ID = "param5"
private const val YEAR = "param6"
private const val TERM = "param7"
private const val REG_NO = "param8"
private const val AMOUNT = "param9"
private const val JSON = "param0"


class DebtStudentsDetailsFragment : Fragment() {

    private lateinit var mCardView: CardView
    private lateinit var mSchoolName: TextView
    private lateinit var mTermTitle: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPaymentLayout: RelativeLayout
    private lateinit var mFeeTotal: TextView
    private lateinit var mFeeTotal2: TextView
    private lateinit var mPayBtn: Button
    private lateinit var thanksMessage: TextView
    private lateinit var thanksImage: ImageView

    private var mSession: String? = null
    private var mTerm: String? = null
    private var mNameSchool: String? = null
    private var mStudentName: String? = null
    private var mJson: String? = null
    private var mLevelId: String? = null
    private var mClassId: String? = null
    private var mStudentId: String? = null
    private var mInvoiceId: String? = null
    private var mYear: String? = null
    private var mRegNo: String? = null
    private var mAmount: String? = null
    private var mFeesList = mutableListOf<TermFeesDataModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mStudentName = it.getString(STUDENT_NAME)
            mStudentId = it.getString(STUDENT_ID)
            mInvoiceId = it.getString(INVOICE_ID)
            mLevelId = it.getString(LEVEL_ID)
            mClassId = it.getString(CLASS_ID)
            mYear = it.getString(YEAR)
            mTerm = it.getString(TERM)
            mRegNo = it.getString(REG_NO)
            mAmount = it.getString(AMOUNT)
            mJson = it.getString(JSON)

        }
    }

    companion object {


        @JvmStatic
        fun newInstance(
            studentName: String,
            studentId: String,
            invoiceId: String,
            levelId: String,
            classId: String,
            year: String,
            term: String,
            regNo: String,
            amount: String,
            json: String,
        ) = DebtStudentsDetailsFragment().apply {
            arguments = Bundle().apply {
                putString(STUDENT_NAME, studentName)
                putString(STUDENT_ID, studentId)
                putString(INVOICE_ID, invoiceId)
                putString(LEVEL_ID, levelId)
                putString(CLASS_ID, classId)
                putString(YEAR, year)
                putString(TERM, term)
                putString(REG_NO, regNo)
                putString(AMOUNT, amount)
                putString(JSON, json)

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_debt_students_details, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        mCardView = view.findViewById(R.id.card_view)
        mSchoolName = view.findViewById(R.id.school_name)
        mTermTitle = view.findViewById(R.id.debt_fee_title)
        mRecyclerView = view.findViewById(R.id.details_recycler)
        mPaymentLayout = view.findViewById(R.id.payment_layout)
        mFeeTotal = view.findViewById(R.id.fee_total)
        mFeeTotal2 = view.findViewById(R.id.total2)
        mPayBtn = view.findViewById(R.id.pay_btn)
        thanksMessage = view.findViewById(R.id.thanks_message)
        thanksImage = view.findViewById(R.id.thanks_image)


        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Fee Details"
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher
                    .onBackPressed()
            }
        }

        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        mNameSchool = sharedPreferences.getString("school_name", "")

        mSchoolName.text = mNameSchool

        setDetailsTitle()

        feeDetails()

        makePayment()


        return view
    }


    private fun setDetailsTitle() {
        val termText = when (mTerm) {
            "1" -> "First Term School Fee Charges for"
            "2" -> "Second Term School Fee Charges for"
            else -> "Third Term School Fee Charges for"
        }

        val previousYear = mYear!!.toInt() - 1

        mSession = String.format(
            Locale.getDefault(),
            "%d/%s",
            previousYear,
            mYear
        )

        mTermTitle.text = String.format(
            Locale.getDefault(),
            "%s %s %s", termText, mSession, "session"
        )

        mFeeTotal.text = String.format(
            Locale.getDefault(),
            "%s%s",
            requireActivity().getString(R.string.naira),
            currencyFormat(mAmount!!.toDouble())
        )

        mFeeTotal2.text = String.format(
            Locale.getDefault(),
            "%s%s",
            requireActivity().getString(R.string.naira),
            currencyFormat(mAmount!!.toDouble())
        )

    }

    private fun feeDetails() {
        try {

            JSONArray(mJson).let {

                for (i in 0 until it.length()) {
                    val feeObject = it.getJSONObject(i)
                    val feeName = feeObject.getString("fee_name")
                    val feeAmount = feeObject.getString("fee_amount")


                    TermFeesDataModel().apply {
                        mFeeName = feeName
                        mFeeAmount = feeAmount
                    }.let { model ->
                        mFeesList.add(model)
                        mFeesList.sortBy { sort -> sort.mFeeName }
                    }
                }
            }

            StudentFeesDetailsAdapter(requireContext(), mFeesList).let {
                mRecyclerView.apply {
                    isAnimating
                    hasFixedSize()
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = it
                }
            }

            mCardView.isVisible = true
            mPaymentLayout.isVisible = true


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun makePayment() {

        mPayBtn.setOnClickListener {
            AddReceiptDialog(
                requireContext(),
                mInvoiceId!!,
                mStudentId!!,
                mClassId!!,
                mLevelId!!,
                mRegNo!!,
                mStudentName!!,
                mAmount!!,
                mYear!!,
                mTerm!!,
                object : OnItemClickListener {
                    override fun onItemClick(position: Int) {

                        mCardView.isVisible = false
                        mPaymentLayout.isVisible = false
                        thanksMessage.isVisible = true
                        thanksImage.isVisible = true
                    }
                }
            ).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

}