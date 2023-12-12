package com.digitaldream.winskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.StudentPaymentModel
import java.util.*

class StudentPaymentAdapter(
    private val sHistoryList: MutableList<StudentPaymentModel>,
    private val sOnHistoryClickListener: OnHistoryClickListener
) : RecyclerView.Adapter<StudentPaymentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout
                .fragment_student_payment_item, parent, false
        )
        return ViewHolder(view, sOnHistoryClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paymentModel = sHistoryList[position]
        holder.mSession.text = String.format(
            Locale.getDefault(), "%s %s",
            paymentModel.getSession(), paymentModel.getTerm()
        )
        holder.mDate.text = paymentModel.getDate()
        holder.mAmount.text = paymentModel.getAmount()
        holder.mStatus.text = paymentModel.getStatus()
    }

    override fun getItemCount() = sHistoryList.size

    inner class ViewHolder(
        itemView: View, private val sHistoryClickListener:
        OnHistoryClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val mSession: TextView = itemView.findViewById(R.id.payment_session)
        val mDate: TextView = itemView.findViewById(R.id.payment_date)
        val mAmount: TextView = itemView.findViewById(R.id.amount)
        val mStatus: TextView = itemView.findViewById(R.id.status)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            sHistoryClickListener.onHistoryClick(adapterPosition)
        }
    }

    interface OnHistoryClickListener {
        fun onHistoryClick(position: Int)
    }
}