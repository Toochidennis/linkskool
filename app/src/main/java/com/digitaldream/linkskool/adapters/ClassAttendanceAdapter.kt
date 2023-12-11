package com.digitaldream.linkskool.adapters

import android.graphics.Color
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
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.linkskool.utils.FunctionUtils.getRandomColor

class ClassAttendanceAdapter(
    private val itemList: List<TagModel>,
    private var selectedItems: HashMap<String, String>,
    private val selectAllLayout: RelativeLayout,
    private val allTitleTxt: TextView,
    private val listener: AttendanceUpdateListener
) : RecyclerView.Adapter<ClassAttendanceAdapter.ViewHolder>() {

    private var initialSelectedItemsSize = selectedItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class_attendance, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rootLayout: RelativeLayout = itemView.findViewById(R.id.rootLayout)
        private val initialsLayout: LinearLayout = itemView.findViewById(R.id.initialsLayout)
        private val initialsTxt: TextView = itemView.findViewById(R.id.initialsTxt)
        private val selectedStateLayout: LinearLayout =
            itemView.findViewById(R.id.selectedStateLayout)
        private val studentNameTxt: TextView = itemView.findViewById(R.id.studentNameTxt)

        fun bind(tag: TagModel) {
            studentNameTxt.text = capitaliseFirstLetter(tag.tagName)
            initialsTxt.text = tag.tagName.substring(0, 1).uppercase()
            itemView.isSelected = tag.isSelected

            getRandomColor(initialsLayout)

            if (itemView.isSelected) {
                selectedStateLayout.isVisible = true
                studentNameTxt.setTextColor(Color.WHITE)

                if (selectedItems.size == itemList.size) {
                    selectedStateLayout.isVisible = false

                    setBackgroundDrawable(selectAllLayout, "select")
                    selectAllLayout.isSelected = true
                    allTitleTxt.setTextColor(Color.WHITE)
                }

                setBackgroundDrawable(rootLayout, "select")

            } else {
                setBackgroundDrawable(rootLayout, "deselect")
                setBackgroundDrawable(selectAllLayout, "deselect")
                selectedStateLayout.isVisible = false
                selectAllLayout.isSelected = false
                allTitleTxt.setTextColor(Color.BLACK)
                studentNameTxt.setTextColor(Color.BLACK)
            }

            itemView.setOnClickListener {
                tag.isSelected = !tag.isSelected
                itemView.isSelected = tag.isSelected

                if (itemView.isSelected) {
                    selectedItems[tag.tagId] = tag.tagName
                    studentNameTxt.setTextColor(Color.WHITE)
                } else {
                    if (selectedItems.containsKey(tag.tagId)) {
                        selectedItems.remove(tag.tagId)
                    }
                    studentNameTxt.setTextColor(Color.BLACK)
                }

                updateSubmitButtonVisibility()

                notifyDataSetChanged()
            }

            selectAllLayout.setOnClickListener {
                if (selectAllLayout.isSelected) {
                    deselectAll()
                } else {
                    selectAll()
                }

                notifyDataSetChanged()

                updateSubmitButtonVisibility()
            }
        }
    }


    private fun selectAll() {
        itemList.forEach {
            it.isSelected = true
            selectedItems[it.tagId] = it.tagName
        }
    }

    private fun deselectAll() {
        itemList.forEach { it.isSelected = false }
        selectedItems.clear()
    }

    private fun setBackgroundDrawable(view: View, state: String) {
        if (state == "select")
            view.background =
                ContextCompat.getDrawable(view.context, R.drawable.left_curved_drawable2)
        else
            view.background =
                ContextCompat.getDrawable(view.context, R.drawable.left_curved_drawable1)
    }


    private fun updateSubmitButtonVisibility() {
        val hasChanges = selectedItems.size != initialSelectedItemsSize
        initialSelectedItemsSize = selectedItems.size

        listener.onAttendanceUpdate(hasChanges, initialSelectedItemsSize)
    }

    interface AttendanceUpdateListener {
        fun onAttendanceUpdate(hasChanges: Boolean, selectedItemsCount: Int)
    }
}