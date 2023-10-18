package com.digitaldream.linkskool.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.CommentDataModel
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2

class AdminELearningCommentAdapter(
    val itemList: MutableList<CommentDataModel>
) : RecyclerView.Adapter<AdminELearningCommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_comment_layout,
            parent, false
        )

        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val itemModel = itemList[position]

        holder.bind(itemModel)
    }

    override fun getItemCount() = itemList.size

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //  private val profileImage: CircleImageView = itemView.findViewById(R.id.profileImageView)
        private val authorNameTxt: TextView = itemView.findViewById(R.id.authorNameTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        private val commentTxt: TextView = itemView.findViewById(R.id.commentTxt)

        fun bind(sCommentDataModel: CommentDataModel) {
            authorNameTxt.text = sCommentDataModel.authorName
            val formattedDate = formatDate2(sCommentDataModel.date, "custom")
            dateTxt.text = formattedDate
            commentTxt.text = sCommentDataModel.comment

            setUpPopMenu(itemView, adapterPosition)
        }
    }


    private fun setUpPopMenu(itemView: View, position: Int) {
        itemView.setOnClickListener {
            PopupMenu(it.context, it, Gravity.END).apply {
                inflate(R.menu.section_menu)

                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.editSection -> {

                            true
                        }

                        R.id.deleteSection -> {
                            itemList.removeAt(position)

                            notifyItemRemoved(position)
                            true
                        }

                        else -> false
                    }
                }

            }.show()

        }
    }
}