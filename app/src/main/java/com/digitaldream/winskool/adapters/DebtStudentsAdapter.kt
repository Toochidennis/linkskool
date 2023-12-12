package com.digitaldream.winskool.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.AdminPaymentModel
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import java.util.Random

class DebtStudentsAdapter(
    private val sContext: Context,
    private var sStudentList: MutableList<AdminPaymentModel>,
    private val sOnItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<DebtStudentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DebtStudentsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_debt_students_item,
            parent,
            false
        )

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: DebtStudentsAdapter.ViewHolder, position: Int) {
        val model = sStudentList[position]
        holder.mStudentName.text = capitaliseFirstLetter(model.mStudentName!!)
        val count = position + 1
        holder.mCount.text = count.toString()

        //set random colors to student view using gradient drawable
        val mutate = holder.mStudentView.background.mutate() as GradientDrawable
        val random = Random()
        val colorList = Color.argb(
            255,
            random.nextInt(100),
            random.nextInt(250),
            random.nextInt(190)
        )
        mutate.setColor(colorList)
        holder.mStudentView.background = mutate

        String.format(
            "%s%s",
            sContext.getString(R.string.naira),
            currencyFormat(model.mReceivedAmount!!.toDouble())
        ).let {
            holder.mAmount.text = it
        }

    }

    override fun getItemCount() = sStudentList.size


    fun updateFilterList(name: MutableList<AdminPaymentModel>) {
        sStudentList = name

        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mStudentView: LinearLayout = itemView.findViewById(R.id.student_view)
        val mStudentName: TextView = itemView.findViewById(R.id.student_name)
        val mCount: TextView = itemView.findViewById(R.id.count)
        val mAmount: TextView = itemView.findViewById(R.id.amount)

        init {
            itemView.setOnClickListener {
                sOnItemClickListener.onItemClick(adapterPosition)
            }
        }

    }

}