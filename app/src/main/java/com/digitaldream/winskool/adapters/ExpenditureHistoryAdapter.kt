package com.digitaldream.winskool.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.ChartModel
import com.digitaldream.winskool.models.ExpenditureHistoryModel
import com.digitaldream.winskool.utils.FunctionUtils
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import java.util.*

class ExpenditureHistoryAdapter(
    private val sContext: Context,
    private val sExpenditureList: MutableList<ExpenditureHistoryModel>?,
    private val sChartList: MutableList<ChartModel>?,
    private val sOnItemClickListener: OnItemClickListener?,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when {
            sExpenditureList.isNullOrEmpty() -> VIEW_TYPE_TYPE_2
            else -> VIEW_TYPE_TYPE_1
        }
    }

    override fun getItemCount() = sExpenditureList?.size ?: sChartList!!.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_TYPE_1 -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.fragment_history_expenditure_item, parent, false
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
            val adminModel = sExpenditureList!![position]
            holder.bindItem(adminModel)
        } else if (holder is GroupViewHolder) {
            val chartModel = sChartList!![position]
            holder.bindItem(chartModel)
        }

    }


    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mExpenditureName: TextView = itemView.findViewById(R.id.expenditure_name)
        private val mExpenditureDate: TextView = itemView.findViewById(R.id.expenditure_date)
        private val mExpenditureAmount: TextView = itemView.findViewById(R.id.expenditure_amount)
        private val mExpenditureType: TextView = itemView.findViewById(R.id.expenditure_type)

        init {
            itemView.setOnClickListener {
                sOnItemClickListener?.onItemClick(adapterPosition)
            }
        }

        fun bindItem(expenditureHistoryModel: ExpenditureHistoryModel) {
            mExpenditureName.text =
                capitaliseFirstLetter(expenditureHistoryModel.vendorName.toString())
            mExpenditureDate.text =
                FunctionUtils.formatDate2(expenditureHistoryModel.date.toString())
            mExpenditureType.text = expenditureHistoryModel.type


            String.format(
                Locale.getDefault(), "%s %s%s", "-", sContext.getString(R.string.naira),
                currencyFormat(expenditureHistoryModel.amount!!.toDouble())
            ).also { mExpenditureAmount.text = it }
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