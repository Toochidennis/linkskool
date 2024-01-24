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
import com.digitaldream.linkskool.dialog.StaffFormClassBottomSheetFragment
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.StaffFormClassModel
import kotlin.random.Random

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
                    val classContainer: LinearLayout = itemView.findViewById(R.id.classContainer)
                    val classImageView: ImageView = itemView.findViewById(R.id.classImageView)

                    classNameTxt.text = model.className

                    val randomDrawable = generateRandomDrawable()
                    classContainer.setBackgroundResource(randomDrawable)

                    val randomTintedColor = generateRandomTintedColor()
                    classImageView.setColorFilter(randomTintedColor, PorterDuff.Mode.SRC_IN)
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

            return drawableResources[Random.nextInt(drawableResources.size)]
        }

        private fun generateRandomTintedColor(): Int {
            val colorResources = listOf(
                R.color.tinted_1, R.color.tinted_color_2, R.color.tinted_color_1,
                R.color.tinted_color_icon, R.color.tinted_6, R.color.tinted_7
            )

            val randomColorResourceId = colorResources[Random.nextInt(colorResources.size)]

            return ContextCompat.getColor(itemView.context, randomColorResourceId)
        }
    }

}