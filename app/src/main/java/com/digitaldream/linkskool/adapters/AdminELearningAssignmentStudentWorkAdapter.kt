package com.digitaldream.linkskool.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.AdminELearningActivity
import com.digitaldream.linkskool.fragments.AdminELearningAssignmentStudentWorkDetailsFragment
import com.digitaldream.linkskool.fragments.AdminELearningQuestionAnswersDetailsFragment
import com.digitaldream.linkskool.models.StudentResponseModel
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter

class AdminELearningAssignmentStudentWorkAdapter(
    private val fragmentManager: FragmentManager,
    private val itemList: MutableList<StudentResponseModel>
) : RecyclerView.Adapter<AdminELearningAssignmentStudentWorkAdapter.StudentWorkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentWorkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_assignment_student_work, parent, false
        )

        return StudentWorkViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentWorkViewHolder, position: Int) {
        val itemModel = itemList[position]
        holder.bind(itemModel)
    }

    override fun getItemCount() = itemList.size

    inner class StudentWorkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val studentNameTxt: TextView = itemView.findViewById(R.id.studentNameTxt)
        private val scoreTxt: TextView = itemView.findViewById(R.id.scoreTxt)

        fun bind(responseModel: StudentResponseModel) {
            studentNameTxt.text = capitaliseFirstLetter(responseModel.studentName)
            scoreTxt.text = responseModel.score

            viewAnswerDetails(itemView, responseModel.id)
        }
    }

    private fun viewAnswerDetails(itemView: View, responseId: String) {
        itemView.setOnClickListener {
            it.context.startActivity(
                Intent(it.context, AdminELearningActivity::class.java)
                    .putExtra("json", responseId)
                    .putExtra("from", "assignment_student_work")
            )
        }
    }
}