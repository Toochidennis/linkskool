package com.digitaldream.winskool.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.TagModel

class AdminELearningQuestionSettingsAdapter(
    private val selectedItems: HashMap<String, String>,
    private val itemList: MutableList<TagModel>,
    private val selectAllBtn: Button,
) : RecyclerView.Adapter<AdminELearningQuestionSettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_admin_e_learning_question_settings_class, parent, false
        )

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemModel = itemList[position]
        holder.bindItem(itemModel)
    }

    override fun getItemCount() = itemList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: Button = itemView.findViewById(R.id.itemName)

        fun bindItem(tag: TagModel) {
            itemName.text = tag.tagName
            itemView.isSelected = tag.isSelected

            if (itemView.isSelected) {
                changeButtonBackground(itemName, "select")
                selectedItems[tag.tagId] = tag.tagName
                if (selectedItems.size == itemList.size) {
                    changeButtonBackground(selectAllBtn, "select")
                }
            } else {
                changeButtonBackground(itemName, "deselect")
            }

            itemView.setOnClickListener {
                tag.isSelected = !tag.isSelected
                itemView.isSelected = tag.isSelected

                if (itemView.isSelected) {
                    changeButtonBackground(itemName, "select")
                    selectedItems[tag.tagId] = tag.tagName
                    if (selectedItems.size == itemList.size) {
                        changeButtonBackground(selectAllBtn, "select")
                    }
                } else {
                    if (tag.tagId.isNotEmpty() && selectedItems.contains(tag.tagId)) {
                        selectedItems.remove(tag.tagId)

                        changeButtonBackground(itemName, "deselect")
                        changeButtonBackground(selectAllBtn, "deselect")
                    }
                }
            }

            selectAllBtn.setOnClickListener {
                if (!selectAllBtn.isSelected) {
                    selectAll()
                } else {
                    deselectAll(selectAllBtn, itemName)
                }
            }
        }
    }

    private fun selectAll() {
        itemList.forEach { item ->
            item.isSelected = true
        }
        notifyDataSetChanged()
    }

    private fun deselectAll(selectAllBtn: Button, itemName: Button) {
        itemList.forEach { item ->
            item.isSelected = false
            changeButtonBackground(selectAllBtn, "deselect")
            changeButtonBackground(itemName, "deselect")
        }
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private fun changeButtonBackground(button: Button, from: String) {
        if (from == "select") {
            button.apply {
                setBackgroundResource(R.drawable.ripple_effect10)
                isSelected = true
                setTextColor(Color.WHITE)
            }
        } else {
            button.apply {
                setBackgroundResource(R.drawable.ripple_effect6)
                isSelected = false
                setTextColor(Color.BLACK)
            }
        }
    }
}
