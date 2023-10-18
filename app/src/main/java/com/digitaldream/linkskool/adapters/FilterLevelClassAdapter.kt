package com.digitaldream.linkskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.interfaces.OnLevelClassClickListener
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.LevelTable

class FilterLevelClassAdapter(
    private val sLevelList: MutableList<LevelTable>?,
    private val sClassList: MutableList<ClassNameTable>?,
    private val sOnItemClickListener: OnLevelClassClickListener
) : RecyclerView.Adapter<FilterLevelClassAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterLevelClassAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dialog_filter_level_class_item, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: FilterLevelClassAdapter.ViewHolder, position: Int) {
        if (sLevelList == null) {
            val itemPosition = sClassList!![position]
            if (itemPosition.className.isNotBlank()) {
                holder.itemName.text = itemPosition.className
                holder.itemFirstLetter.text =
                    itemPosition.className.substring(0, 1).uppercase()
            }
        } else {
            val itemPosition = sLevelList[position]
            if (itemPosition.levelName.isNotBlank()) {
                holder.itemName.text = itemPosition.levelName
                holder.itemFirstLetter.text =
                    itemPosition.levelName.substring(0, 1).uppercase()
            }

        }

        sOnItemClickListener.onNameClick(holder)

    }

    override fun getItemCount() = sLevelList?.size ?: sClassList!!.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemTextLayout: LinearLayout = itemView.findViewById(R.id.layout_text)
        val itemImageLayout: LinearLayout = itemView.findViewById(R.id.item_image_layout)
        val itemFirstLetter: TextView = itemView.findViewById(R.id.item_first_letter)

    }
}