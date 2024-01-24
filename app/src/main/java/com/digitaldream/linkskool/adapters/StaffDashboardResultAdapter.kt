package com.digitaldream.linkskool.adapters

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.StaffResultBottomSheetFragment
import com.digitaldream.linkskool.models.CourseTable
import com.digitaldream.linkskool.models.StaffResultModel
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter

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
                    val classContainer: LinearLayout = itemView.findViewById(R.id.classContainer)
                    val classImageView: ImageView = itemView.findViewById(R.id.classImageView)

                    classNameTxt.text = model.className

                    val randomDrawable = generateRandomDrawable()
                    classContainer.setBackgroundResource(randomDrawable)

                    val randomTintedColor = generateRandomTintedColor()
                    classImageView.setColorFilter(randomTintedColor, PorterDuff.Mode.SRC_IN)
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
                layoutManager = GridLayoutManager(itemView.context, 3)
                adapter = itemAdapter
            }
        }

        private fun generateRandomDrawable(): Int {
            val drawableResources = listOf(
                R.drawable.ripple_effect1,
                R.drawable.ripple_effect16,
                R.drawable.ripple_effect15
            )

            return drawableResources[kotlin.random.Random.nextInt(drawableResources.size)]
        }

        private fun generateRandomTintedColor(): Int {
            val colorResources = listOf(
                R.color.tinted_1, R.color.tinted_color_2, R.color.tinted_color_1,
                R.color.tinted_color_icon, R.color.tinted_6, R.color.tinted_7
            )

            val randomColorResourceId = colorResources[kotlin.random.Random.nextInt(colorResources.size)]

            return ContextCompat.getColor(itemView.context, randomColorResourceId)
        }


    }

}