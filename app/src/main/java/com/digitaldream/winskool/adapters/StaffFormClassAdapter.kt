package com.digitaldream.winskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.StaffFormClassBottomSheetFragment
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.StaffFormClassModel

class StaffFormClassAdapter(
    private val fragmentManager: FragmentManager,
    private val itemList: List<StaffFormClassModel>
) : RecyclerView.Adapter<StaffFormClassAdapter.HeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_staff_form_class_course_name, parent, false
        )

        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseNameTxt: TextView = itemView.findViewById(R.id.courseNameTxt)
        private val itemRecyclerView: RecyclerView = itemView.findViewById(R.id.itemRecyclerView)

        private lateinit var itemAdapter: GenericAdapter<ClassNameTable>

        fun bind(formClassModel: StaffFormClassModel) {
            courseNameTxt.text = formClassModel.levelName

            setUpItemAdapter(formClassModel.classList)
        }


        private fun setUpItemAdapter(classItemList: List<ClassNameTable>) {
            itemAdapter = GenericAdapter(
                classItemList.toMutableList(),
                R.layout.item_staff_form_class_name,
                bindItem = { itemView, model, _ ->
                    val classNameTxt: TextView = itemView.findViewById(R.id.classNameTxt)
                    classNameTxt.text = model.className
                }
            ) { position ->
                val itemPosition = classItemList[position]

                StaffFormClassBottomSheetFragment
                    .newInstance(
                        itemPosition.classId,
                        itemPosition.className,
                        itemPosition.level
                    ).show(fragmentManager, "Form class")

            }

            setUpItemRecyclerView()
        }

        private fun setUpItemRecyclerView() {
            itemRecyclerView.apply {
                hasFixedSize()
                layoutManager = GridLayoutManager(itemView.context, 2)
                adapter = itemAdapter
            }
        }
    }

}