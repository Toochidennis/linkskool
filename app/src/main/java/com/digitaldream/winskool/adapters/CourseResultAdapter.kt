package com.digitaldream.winskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.CourseResultModel
import com.digitaldream.winskool.utils.FunctionUtils.animateObject

class CourseResultAdapter(
    private val sCourseList: MutableList<CourseResultModel>,
    private val sOnItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<CourseResultAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.activity_course_result_item,
            parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = sCourseList[position]
        holder.courseName.text = model.courseName

        animateObject(holder.mProgressBar, holder.mProgressText, 52)

    }

    override fun getItemCount() = sCourseList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseName: TextView = itemView.findViewById(R.id.course_text)
        val mProgressText: TextView = itemView.findViewById(R.id.progress_text)
        val mProgressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)

        init {
            itemView.setOnClickListener {
                sOnItemClickListener.onItemClick(adapterPosition)
            }
        }
    }


/* subjectDownloadViewHolder.viewResult.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SubjectResultUtil.class);
            intent.putExtra("courseId",smd.getCourseId());
            intent.putExtra("class_id",smd.getClassId());
            intent.putExtra("status","view");
            context.startActivity(intent);
        }
    });*/
}