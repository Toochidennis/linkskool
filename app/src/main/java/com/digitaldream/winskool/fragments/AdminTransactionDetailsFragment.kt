package com.digitaldream.winskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.digitaldream.winskool.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminTransactionDetailsFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminTransactionDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_transaction_details, container, false)
    }

    private fun receiptDetails(sView: View, sTransactionType: String) {
        val schoolName: TextView = sView.findViewById(R.id.school_name)
        val receiptTitle: TextView = sView.findViewById(R.id.receipt_title)
        val receiptAmount: TextView = sView.findViewById(R.id.receipt_amount)
        val receiptDate: TextView = sView.findViewById(R.id.receipt_date)
        val studentName: TextView = sView.findViewById(R.id.student_name)
        val studentLevel: TextView = sView.findViewById(R.id.student_level)
        val studentClass: TextView = sView.findViewById(R.id.student_class)
        val registrationNo: TextView = sView.findViewById(R.id.registration_no)
        val session: TextView = sView.findViewById(R.id.session)
        val term: TextView = sView.findViewById(R.id.term)
        val referenceNumber: TextView = sView.findViewById(R.id.reference_number)
    }

    private fun expenditureDetails(sView: View, sTransactionType: String) {

    }
}