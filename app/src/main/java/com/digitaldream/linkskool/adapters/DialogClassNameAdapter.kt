package com.digitaldream.linkskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.ClassNameTable

class DialogClassNameAdapter(
    private val sClassList: MutableList<ClassNameTable>,
    private val sClassClick: OnClassClickListener,
) : RecyclerView.Adapter<DialogClassNameAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.dialog_class_name_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val classTable = sClassList[position]
        holder.mClassName.text = classTable.className

    }

    override fun getItemCount() = sClassList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mClassName: Button = itemView.findViewById(R.id.class_name)

        init {
            itemView.setOnClickListener {
                sClassClick.onClassClick(adapterPosition)
            }
        }

    }
}

interface OnClassClickListener {
    fun onClassClick(position: Int)
}