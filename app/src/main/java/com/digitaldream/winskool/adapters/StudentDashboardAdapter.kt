package com.digitaldream.winskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.StudentDashboardModel
import com.digitaldream.winskool.models.StudentReplyModel
import com.digitaldream.winskool.utils.FunctionUtils

class StudentDashboardAdapter(
    private val itemList: List<StudentDashboardModel>,
) : RecyclerView.Adapter<StudentDashboardAdapter.ItemViewHolder>() {

    private lateinit var replyAdapter: GenericAdapter<StudentReplyModel>


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
        private val replySeparator: View = itemView.findViewById(R.id.replySeparator)
        private val replyTitle: TextView = itemView.findViewById(R.id.replyTitle)
        private val replyRecyclerView: RecyclerView = itemView.findViewById(R.id.replyRecyclerView)


        fun bind(studentModel: StudentDashboardModel) {
            usernameTxt.text = FunctionUtils.capitaliseFirstLetter(studentModel.username)
            dateTxt.text = FunctionUtils.formatDate2(studentModel.date, "custom")
            questionTxt.text = studentModel.question
            commentNoTxt.text = studentModel.commentsNo
            noOfLikesTxt.text = studentModel.likesNo
            sharesCountTxt.text = studentModel.noOfShared

            replyTitle.isVisible = studentModel.replyList.isNotEmpty()
            replySeparator.isVisible = studentModel.replyList.isNotEmpty()
            setReplyAdapter(studentModel.replyList)
        }

        private fun setReplyAdapter(replyList: List<StudentReplyModel>) {
            replyAdapter = GenericAdapter(
                replyList.toMutableList(),
                R.layout.item_student_reply_layout,
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

            setUpReplyRecyclerView()
        }

        private fun setUpReplyRecyclerView() {
            replyRecyclerView.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = replyAdapter
            }
        }
    }
}