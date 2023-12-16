package com.digitaldream.linkskool.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.StaffResultBottomSheetFragment
import com.digitaldream.linkskool.models.CourseTable
import com.digitaldream.linkskool.models.StaffResultModel
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter
import timber.log.Timber
import java.util.Random

class StaffDashboardResultAdapter(
    private val fragmentManager: FragmentManager,
    private val itemList: List<StaffResultModel>
) : RecyclerView.Adapter<StaffDashboardResultAdapter.HeaderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_course_name_layout,
            parent, false
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

        private lateinit var itemAdapter: GenericAdapter<CourseTable>

        fun bind(resultModel: StaffResultModel) {
            courseNameTxt.text = capitaliseFirstLetter(resultModel.courseName)

            setUpItemAdapter(resultModel.courseList)
        }


        private fun setUpItemAdapter(courseItemList: List<CourseTable>) {
            itemAdapter = GenericAdapter(
                courseItemList.toMutableList(),
                R.layout.item_class_name_layout,
                bindItem = { itemView, model, _ ->
                    val classNameTxt: TextView = itemView.findViewById(R.id.classNameTxt)

                    classNameTxt.text = model.className
                }
            ) { position ->
                val itemPosition = courseItemList[position]

                StaffResultBottomSheetFragment
                    .newInstance(
                        itemPosition.levelId,
                        itemPosition.courseId,
                        itemPosition.classId,
                        itemPosition.courseName
                    ).show(fragmentManager, "")

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

        private fun randomColor(): ColorStateList {
            val hue = Random().nextInt(256)
            val color = Color.HSVToColor(floatArrayOf(hue.toFloat(), 21f, 5f))
            return ColorStateList.valueOf(color)
        }

    }

}