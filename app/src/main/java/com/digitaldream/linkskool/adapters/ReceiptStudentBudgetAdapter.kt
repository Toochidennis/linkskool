package com.digitaldream.linkskool.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.StudentPaymentModel
import com.digitaldream.linkskool.utils.FunctionUtils
import java.util.*

class ReceiptStudentBudgetAdapter(
    private val sContext: Context,
    private val sBudgetList: MutableList<StudentPaymentModel>,
    private val sOnBudgetClick: OnItemClickListener,
) : RecyclerView.Adapter<ReceiptStudentBudgetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout
                .fragment_receipt_student_budget_item, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val paymentModel = sBudgetList[position]

        when (paymentModel.getTerm()) {
            "1" -> {
                holder.mSession.text = String.format(
                    Locale.getDefault(), "%s " +
                            "%s", paymentModel.getSession(), "First Term Fees"
                )
            }

            "2" -> {
                holder.mSession.text = String.format(
                    Locale.getDefault(), "%s " +
                            "%s", paymentModel.getSession(), "Second Term Fees"
                )
            }
            "3" -> {
                holder.mSession.text = String.format(
                    Locale.getDefault(), "%s " +
                            "%s", paymentModel.getSession(), "Third Term Fees"
                )
            }
        }

        String.format(
            Locale.getDefault(), "%s%s",
            sContext.getString(R.string.naira),
            FunctionUtils.currencyFormat(
                paymentModel.getAmount()!!.toDouble()
            )
        ).also { holder.mAmount.text = it }

    }

    override fun getItemCount() = sBudgetList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mSession: TextView = itemView.findViewById(R.id.payment_session)
        val mAmount: TextView = itemView.findViewById(R.id.amount)

        init {
            itemView.setOnClickListener {
                sOnBudgetClick.onItemClick(adapterPosition)
            }
        }

    }
}