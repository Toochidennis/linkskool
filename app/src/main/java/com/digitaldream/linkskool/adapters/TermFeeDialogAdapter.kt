package com.digitaldream.linkskool.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.LevelTable

class TermFeeDialogAdapter(
    private val sContext: Context,
    private val sLevelList: MutableList<LevelTable>,
    private val sLevelOnclick: OnItemClickListener,
    private val sLevelName: String = "",
) : RecyclerView.Adapter<TermFeeDialogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.dialog_fee_term_item, parent, false
        )
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val levelTable = sLevelList[position]
        holder.mLevelName.text = levelTable.levelName
        if (sLevelName == levelTable.levelName) {
            holder.mLevelName.setBackgroundResource(R.drawable.ripple_effect2)
            holder.mLevelName.setTextColor(ContextCompat.getColor(sContext, R.color.white))
        }
    }

    override fun getItemCount() = sLevelList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mLevelName: Button = itemView.findViewById(R.id.level_name)

        init {
            itemView.setOnClickListener {
                sLevelOnclick.onItemClick(adapterPosition)
            }
        }

    }
}