package com.digitaldream.winskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GenericAdapter<T>(
    private val itemList: MutableList<T>,
    private val itemResLayout: Int,
    private val bindItem: (itemView: View, model: T, position: Int) -> Unit,
    private val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<GenericAdapter<T>.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemResLayout, parent,
            false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = itemList[position]

        bindItem(holder.itemView, model, holder.adapterPosition)

        holder.itemView.setOnClickListener {
            onItemClick(holder.adapterPosition)
        }

    }

    override fun getItemCount() = itemList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}