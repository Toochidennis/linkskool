package com.digitaldream.linkskool.adapters

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.AdminResultDashboardModel
import com.digitaldream.linkskool.utils.FunctionUtils.animateObject
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters

class AdminResultDashboardAdapter(
    private val sResultList: MutableList<AdminResultDashboardModel>,
    private val sHeaderTitle: String,
    private val sOnItemClickListener: OnItemClickListener,
) : Section(
    SectionParameters.builder()
        .itemResourceId(R.layout.activity_admin_result_dashboard_item)
        .headerResourceId(R.layout.head)
        .build()
) {


    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun getContentItemsTotal() = sResultList.size

    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ItemViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        val viewHolder = holder as HeaderViewHolder
        viewHolder.mSession.text = sHeaderTitle
    }

    override fun onBindItemViewHolder(sViewHolder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = sViewHolder as ItemViewHolder
        val model = sResultList[position]

        viewHolder.mTerm.text = model.term

        animateObject(viewHolder.mProgressBar, viewHolder.mProgressText, 52)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mTerm: TextView = itemView.findViewById(R.id.term_text)
        val mProgressText: TextView = itemView.findViewById(R.id.progress_text)
        val mProgressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)

        init {
            itemView.setOnClickListener {
                sOnItemClickListener.onItemClick(adapterPosition)
            }
        }

    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mSession: TextView = itemView.findViewById(R.id.course_name)
    }
}