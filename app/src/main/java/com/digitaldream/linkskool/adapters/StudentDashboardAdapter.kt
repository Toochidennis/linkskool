package com.digitaldream.linkskool.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.QuestionViewActivity
import com.digitaldream.linkskool.models.AdminDashboardModel
import com.digitaldream.linkskool.utils.FunctionUtils
import kotlin.random.Random

class StudentDashboardAdapter(
    private val itemList: List<AdminDashboardModel>,
) : RecyclerView.Adapter<StudentDashboardAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_dashboard, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTxt: TextView = itemView.findViewById(R.id.usernameTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        private val questionTxt: TextView = itemView.findViewById(R.id.questionTxt)
        private val commentNoTxt: TextView = itemView.findViewById(R.id.comments)
        private val noOfLikesTxt: TextView = itemView.findViewById(R.id.upVotes)
        private val sharesCountTxt: TextView = itemView.findViewById(R.id.share)
        private val imageCard: CardView = itemView.findViewById(R.id.imageHolder)


        fun bind(studentModel: AdminDashboardModel) {
            usernameTxt.text = FunctionUtils.capitaliseFirstLetter(studentModel.username)
            dateTxt.text = FunctionUtils.formatDate2(studentModel.date, "custom")
            questionTxt.text = studentModel.question
            commentNoTxt.text = studentModel.commentsNo
            noOfLikesTxt.text = studentModel.likesNo
            sharesCountTxt.text = studentModel.noOfShared

            val randomTintedColor = generateRandomTintedColor()
            imageCard.setCardBackgroundColor(randomTintedColor)

            viewQuestion(model = studentModel)
        }

        private fun generateRandomTintedColor(): Int {
            val colorResources = listOf(
                R.color.tinted_color_icon_5,
                R.color.tinted_0, R.color.tinted_1, R.color.tinted_2, R.color.tinted_3, R.color
                    .tinted_4, R.color.tinted_5, R.color.tinted_6, R.color.tinted_7
            )

            val randomColorResourceId = colorResources[Random.nextInt(colorResources.size)]

            return ContextCompat.getColor(itemView.context, randomColorResourceId)
        }

        private fun viewQuestion(model: AdminDashboardModel) {
            itemView.setOnClickListener {
                it.context.startActivity(
                    Intent(it.context, QuestionViewActivity::class.java)
                        .putExtra("question_object", model)
                )
            }
        }
    }
}