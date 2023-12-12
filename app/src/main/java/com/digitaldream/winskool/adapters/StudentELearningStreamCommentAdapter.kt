package com.digitaldream.winskool.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.CommentDataModel
import com.digitaldream.winskool.utils.FunctionUtils.formatDate2
import de.hdodenhof.circleimageview.CircleImageView

class StudentELearningStreamCommentAdapter(
    private val itemList: MutableList<CommentDataModel>
) : RecyclerView.Adapter<StudentELearningStreamCommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_stream_comment_layout, parent, false)

        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val itemModel = itemList[position]
        holder.bind(itemModel)
    }

    override fun getItemCount() = itemList.size


    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: CircleImageView = itemView.findViewById(R.id.imageView)
        private val authorNameTxt: TextView = itemView.findViewById(R.id.authorNameTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        private val commentTxt: TextView = itemView.findViewById(R.id.commentTxt)

        fun bind(dataModel: CommentDataModel) {
            authorNameTxt.text = dataModel.authorName
            val formattedDate = formatDate2(dataModel.date, "custom")
            dateTxt.text = formattedDate
            commentTxt.text = dataModel.comment

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