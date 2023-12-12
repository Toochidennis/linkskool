package com.digitaldream.winskool.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.AdminPaymentModel
import com.digitaldream.winskool.utils.FunctionUtils
import java.util.*

class AdminTransactionHistoryAdapter(
    private val sContext: Context,
    private val sTransactionList: MutableList<AdminPaymentModel>,
    private val sOnItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<AdminTransactionHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout
                .fragment_history_transaction_admin_item, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adminModel = sTransactionList[position]
        holder.mTransactionDate.text = adminModel.mTransactionDate

        when (adminModel.mTransactionName) {
            "receipts" -> {
                holder.mTransactionCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        sContext, R.color.color_4
                    )
                )
                holder.mTransactionType.setImageResource(R.drawable.ic_receipt)
                holder.mDescription.text = adminModel.mDescription
                String.format(
                    Locale.getDefault(), "%s %s%s", "+", sContext.getString(R.string.naira),
                    FunctionUtils.currencyFormat(adminModel.mReceivedAmount!!.toDouble())
                ).also { holder.mTransactionAmount.text = it }

            }
            else -> {
                holder.mTransactionCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        sContext, R
                            .color.redH
                    )
                )
                holder.mTransactionType.setImageResource(R.drawable.ic_expenditure)
                holder.mDescription.text = adminModel.mDescription
                String.format(
                    Locale.getDefault(), "%s %s%s", "-", sContext.getString(R.string.naira),
                    FunctionUtils.currencyFormat(adminModel.mReceivedAmount!!.toDouble())
                ).also { holder.mTransactionAmount.text = it }
            }
        }
    }

    override fun getItemCount() = sTransactionList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTransactionCard: CardView = itemView.findViewById(R.id.transaction_card)
        val mTransactionType: ImageView = itemView.findViewById(R.id.transaction_type)
        val mDescription: TextView = itemView.findViewById(R.id.description)
        val mTransactionDate: TextView = itemView.findViewById(R.id.transaction_date)
        val mTransactionAmount: TextView = itemView.findViewById(R.id.receipt_amount)

        init {
            itemView.setOnClickListener {
                sOnItemClickListener.onItemClick(adapterPosition)
            }
        }

    }
}

