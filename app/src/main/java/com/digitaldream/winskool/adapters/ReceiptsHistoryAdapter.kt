package com.digitaldream.winskool.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.AdminPaymentModel
import com.digitaldream.winskool.models.ChartModel
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.winskool.utils.FunctionUtils.formatDate2
import java.util.Locale

const val VIEW_TYPE_TYPE_1 = 1
const val VIEW_TYPE_TYPE_2 = 2

class ReceiptsHistoryAdapter(
    private val sContext: Context,
    private val sTransactionList: MutableList<AdminPaymentModel>?,
    private val sChartList: MutableList<ChartModel>?,
    private val sOnItemClickListener: OnItemClickListener?,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when {
            sTransactionList.isNullOrEmpty() -> VIEW_TYPE_TYPE_2
            else -> VIEW_TYPE_TYPE_1
        }
    }

    override fun getItemCount() = sTransactionList?.size ?: sChartList!!.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_TYPE_1 -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.fragment_history_receipts_item, parent, false
                )

                FilterViewHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.fragment_history_grouping_item, parent, false
                )

                GroupViewHolder(view)
            }

        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FilterViewHolder) {
            val adminModel = sTransactionList!![position]
            holder.bindItem(adminModel)
        } else if (holder is GroupViewHolder) {
            val chartModel = sChartList!![position]
            holder.bindItem(chartModel)
        }

    }


    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mStudentName: TextView = itemView.findViewById(R.id.student_name)
        private val mReceiptDate: TextView = itemView.findViewById(R.id.receipt_date)
        private val mReceiptAmount: TextView = itemView.findViewById(R.id.receipt_amount)
        private val mStudentClass: TextView = itemView.findViewById(R.id.student_class)

        fun bindItem(adminPaymentModel: AdminPaymentModel) {
            mStudentName.text = capitaliseFirstLetter(adminPaymentModel.mStudentName!!)
            mReceiptDate.text = formatDate2(adminPaymentModel.mTransactionDate!!)
            mStudentClass.text = adminPaymentModel.mClassName

            String.format(
                Locale.getDefault(), "%s %s%s", "+", sContext.getString(R.string.naira),
                currencyFormat(adminPaymentModel.mReceivedAmount!!.toDouble())
            ).also { mReceiptAmount.text = it }
        }


        init {
            itemView.setOnClickListener {
                sOnItemClickListener?.onItemClick(adapterPosition)
            }
        }

    }


    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val itemName: TextView = itemView.findViewById(R.id.item_name)
        private val itemAmount: TextView = itemView.findViewById(R.id.item_amount)

        fun bindItem(chartModel: ChartModel) {
            itemName.text = chartModel.label

            String.format(
                Locale.getDefault(),
                "%s %s%s",
                "+",
                sContext.getString(R.string.naira),
                currencyFormat(
                    chartModel.value.replace(".00", "").toDouble()
                )
            ).let {
                itemAmount.text = it
            }

        }

    }


}