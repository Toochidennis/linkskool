package com.digitaldream.winskool.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.AdminPaymentModel
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat

class StudentsPaidAdapter(
    private val sContext: Context,
    private var sStudentList: MutableList<AdminPaymentModel>,
    private val sLevelOnclick: OnItemClickListener,
) : RecyclerView.Adapter<StudentsPaidAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_students_paid_item, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val studentList = sStudentList[position]

        holder.mStudentName.text = capitaliseFirstLetter(studentList.mStudentName.toString())
        holder.mDate.text = studentList.mTransactionDate

        String.format(
            "%s%s",
            sContext.getString(R.string.naira),
            currencyFormat(studentList.mReceivedAmount!!.toDouble())
        ).let {
            holder.mAmount.text = it
        }


    }

    override fun getItemCount() = sStudentList.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mStudentName: TextView = itemView.findViewById(R.id.student_name)
        val mDate: TextView = itemView.findViewById(R.id.receipt_date)
        val mAmount: TextView = itemView.findViewById(R.id.receipt_amount)

        init {
            itemView.setOnClickListener {
                sLevelOnclick.onItemClick(adapterPosition)
            }
        }
    }

    fun updateFilterList(name: MutableList<AdminPaymentModel>) {
        sStudentList = name
        notifyDataSetChanged()
    }

}