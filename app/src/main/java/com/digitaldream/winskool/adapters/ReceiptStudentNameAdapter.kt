package com.digitaldream.winskool.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.StudentTable
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import java.util.*

class ReceiptStudentNameAdapter(
    private var sStudentList: MutableList<StudentTable>,
    private val sLevelOnclick: OnItemClickListener,
) : RecyclerView.Adapter<ReceiptStudentNameAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_receipt_student_name_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val studentTable = sStudentList[position]

        val surName = capitaliseFirstLetter(studentTable.studentSurname)
        val middleName = capitaliseFirstLetter(studentTable.studentMiddlename)
        val firstName = capitaliseFirstLetter(studentTable.studentFirstname)
        val name = "$surName $middleName $firstName"
        studentTable.studentFullName = name

        holder.mStudentName.text = studentTable.studentFullName

        val mutate = holder.mStudentView.background.mutate() as GradientDrawable
        val random = Random()
        val currentColor = Color.argb(
            255, random.nextInt(256),
            random.nextInt(256), random.nextInt(256)
        )
        mutate.setColor(currentColor)
        holder.mStudentView.background = mutate
    }

    override fun getItemCount() = sStudentList.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mStudentView: LinearLayout = itemView.findViewById(R.id.student_view)
        val mStudentName: TextView = itemView.findViewById(R.id.student_name)

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