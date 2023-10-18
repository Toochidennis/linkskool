package com.digitaldream.linkskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.fragments.AdminELearningQuestionAnswersDetailsFragment
import com.digitaldream.linkskool.models.StudentResponseModel
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter

class AdminELearningQuestionAnswersAdapter(
    private val fragmentManager: FragmentManager,
    private val itemList: MutableList<StudentResponseModel>,
    private val questionData: String
) : RecyclerView.Adapter<AdminELearningQuestionAnswersAdapter.AnswerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_question_answers_layout, parent, false
        )

        return AnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val itemModel = itemList[position]
        holder.bind(itemModel)
    }

    override fun getItemCount() = itemList.size

    inner class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val studentNameTxt: TextView = itemView.findViewById(R.id.studentNameTxt)
        private val scoreTxt: TextView = itemView.findViewById(R.id.studentScoreTxt)

        fun bind(answerModel: StudentResponseModel) {
            studentNameTxt.text = capitaliseFirstLetter(answerModel.studentName)
            scoreTxt.text = answerModel.score

            viewAnswerDetails(itemView, answerModel.id)
        }

    }

    private fun viewAnswerDetails(itemView: View, responseId: String) {
        itemView.setOnClickListener {
            AdminELearningQuestionAnswersDetailsFragment.newInstance(questionData, responseId)
                .show(fragmentManager, "Answer details")
        }
    }

}