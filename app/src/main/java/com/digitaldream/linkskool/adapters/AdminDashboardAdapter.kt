package com.digitaldream.linkskool.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.AdminDashboardModel
import com.digitaldream.linkskool.models.AdminReplyModel
import com.digitaldream.linkskool.utils.FunctionUtils
import kotlin.random.Random

class AdminDashboardAdapter(
    private val itemList: List<AdminDashboardModel>,
) : RecyclerView.Adapter<AdminDashboardAdapter.ItemViewHolder>() {

    private lateinit var replyAdapter: GenericAdapter<AdminReplyModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_dashboard, parent, false)
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

        fun bind(adminModel: AdminDashboardModel) {
            usernameTxt.text = FunctionUtils.capitaliseFirstLetter(adminModel.username)
            dateTxt.text = FunctionUtils.formatDate2(adminModel.date, "custom")
            questionTxt.text = adminModel.question
            commentNoTxt.text = adminModel.commentsNo
            noOfLikesTxt.text = adminModel.likesNo
            sharesCountTxt.text = adminModel.noOfShared

            val randomTintedColor = generateRandomTintedColor()

            imageCard.setCardBackgroundColor(randomTintedColor)
        }

        private fun setReplyAdapter(replyList: List<AdminReplyModel>) {
            replyAdapter = GenericAdapter(
                replyList.toMutableList(),
                R.layout.item_admin_reply_layout,
                bindItem = { itemView, model, _ ->
                    val usernameTxt: TextView = itemView.findViewById(R.id.usernameTxt)
                    val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
                    val replyTxt: TextView = itemView.findViewById(R.id.replyTxt)
                    val replyImage: ImageView = itemView.findViewById(R.id.replyImage)

                    usernameTxt.text = FunctionUtils.capitaliseFirstLetter(model.username)
                    dateTxt.text = FunctionUtils.formatDate2(model.date, "custom")

                    if (model.reply.isNotBlank()) {
                        replyTxt.text = model.reply
                        replyTxt.isVisible = true
                    } else {
                        replyTxt.isVisible = false
                        replyImage.isVisible = true
                    }
                }
            ) {

            }

            // setUpReplyRecyclerView()
        }

        /* private fun setUpReplyRecyclerView() {
             replyRecyclerView.apply {
                 hasFixedSize()
                 layoutManager = LinearLayoutManager(itemView.context)
                 adapter = replyAdapter
             }
         }*/


        private fun generateRandomTintedColor(): Int {
            // Create an HSB color with full saturation and brightness
            val randomColor =
                Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))

            // Tint the color with white
            return ColorUtils.blendARGB(randomColor, Color.WHITE, 0.2f)
        }

    }

}