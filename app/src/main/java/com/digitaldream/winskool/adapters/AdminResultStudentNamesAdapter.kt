package com.digitaldream.winskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.StudentTable
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.getRandomColor

class AdminResultStudentNamesAdapter(
    private var sStudentList: MutableList<StudentTable>,
    private val sLevelOnclick: OnItemClickListener,
) : RecyclerView.Adapter<AdminResultStudentNamesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.dialog_admin_result_student_names_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val studentTable = sStudentList[position]

        val surName = studentTable.studentSurname
        val middleName = studentTable.studentMiddlename
        val firstName = studentTable.studentFirstname
        val name = "$surName $middleName $firstName"
        studentTable.studentFullName = capitaliseFirstLetter(name)

        holder.mStudentName.text = studentTable.studentFullName

        val count = position + 1

        holder.mCount.text = count.toString()

        getRandomColor(holder.mStudentView)

    }

    override fun getItemCount() = sStudentList.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mStudentView: LinearLayout = itemView.findViewById(R.id.student_view)
        val mStudentName: TextView = itemView.findViewById(R.id.student_name)
        val mCount: TextView = itemView.findViewById(R.id.count)

        init {
            itemView.setOnClickListener {
                sLevelOnclick.onItemClick(adapterPosition)
            }
        }

    }

    fun filterList(filteredList: MutableList<StudentTable>) {
        sStudentList = filteredList
        notifyDataSetChanged()
    }
}