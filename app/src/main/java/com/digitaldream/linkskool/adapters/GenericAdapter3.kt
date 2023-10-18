package com.digitaldream.linkskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GenericAdapter3<T>(
    private val itemList: Int,
    private val itemResLayout: Int,
    private val bindItem: (itemView:View, position:Int) -> Unit,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<GenericAdapter3<T>.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            itemResLayout, parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindItem(holder.itemView, position)

        holder.itemView.setOnClickListener {
            onItemClick(holder.adapterPosition)
        }

    }

    override fun getItemCount() = itemList

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}