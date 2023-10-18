package com.digitaldream.linkskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils.getRandomColor
import timber.log.Timber

class AdminELearningSelectClassAdapter(
    private val itemList: MutableList<TagModel>,
    private var selectedItems: HashMap<String, String>,
    private val selectAllLayout: RelativeLayout,
    private val selectAllStateLayout: LinearLayout
) : RecyclerView.Adapter<AdminELearningSelectClassAdapter.ClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_select_class_layout,
            parent, false
        )

        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val teachersModel = itemList[position]
        holder.bind(teachersModel)
    }

    override fun getItemCount() = itemList.size

    inner class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemSelectedState: LinearLayout =
            itemView.findViewById(R.id.selectedStateLayout)
        private val classNameTxt: TextView = itemView.findViewById(R.id.classNameTxt)
        private val classLayout: RelativeLayout = itemView.findViewById(R.id.classLayout)
        private val initialsTxt: TextView = itemView.findViewById(R.id.initialsTxt)
        private val initialsLayout: LinearLayout = itemView.findViewById(R.id.initialsLayout)

        fun bind(tagModel: TagModel) {
            classNameTxt.text = tagModel.tagName
            initialsTxt.text = tagModel.tagName.substring(0, 1).uppercase()

            getRandomColor(initialsLayout)

            itemView.isSelected = tagModel.isSelected

            if (itemView.isSelected) {
                itemSelectedState.isVisible = true

                if (selectedItems.size == itemList.size) {
                    itemSelectedState.isVisible = false
                    selectAllStateLayout.isVisible = true

                    setBackgroundDrawable(selectAllLayout, "select")
                    selectAllLayout.isSelected = true

                }

                setBackgroundDrawable(classLayout, "select")

            } else {
                setBackgroundDrawable(classLayout, "deselect")
                setBackgroundDrawable(selectAllLayout, "deselect")
                selectAllStateLayout.isVisible = false
                selectAllLayout.isSelected = false
                itemSelectedState.isVisible = false
            }

            itemView.setOnClickListener {
                tagModel.isSelected = !tagModel.isSelected
                itemView.isSelected = tagModel.isSelected

                if (itemView.isSelected) {
                    selectedItems[tagModel.tagId] = tagModel.tagName
                } else {
                    if (selectedItems.containsKey(tagModel.tagId)) {
                        selectedItems.remove(tagModel.tagId)
                    }
                }

                notifyDataSetChanged()
            }

            selectAllLayout.setOnClickListener {
                if (selectAllLayout.isSelected) {
                    deselectAll()
                } else {
                    selectAll()
                }
            }
        }
    }

    private fun selectAll() {
        itemList.forEach {
            it.isSelected = true
            selectedItems[it.tagId] = it.tagName
        }
        notifyDataSetChanged()
    }

    private fun deselectAll() {
        itemList.forEach { it.isSelected = false }
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private fun setBackgroundDrawable(view: View, state: String) {
        if (state == "select")
            view.background =
                ContextCompat.getDrawable(view.context, R.drawable.left_curved_drawable2)
        else
            view.background =
                ContextCompat.getDrawable(view.context, R.drawable.left_curved_drawable1)
    }

}