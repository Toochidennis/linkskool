package com.digitaldream.winskool.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.R;

import java.util.List;

public class AllStaffCourseAdapter extends RecyclerView.Adapter<AllStaffCourseAdapter.AllStaffCourseViewHolder> {
private Context context;
    private List<CourseTable> staffCourseList;

    public AllStaffCourseAdapter(Context context, List<CourseTable> staffCourseList) {
        this.context = context;
        this.staffCourseList = staffCourseList;
    }

    @NonNull
    @Override
    public AllStaffCourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.staff_course_item,viewGroup,false);
        return new AllStaffCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllStaffCourseViewHolder allStaffCourseViewHolder, int i) {
        allStaffCourseViewHolder.courseName.setText(staffCourseList.get(i).getCourseName().toUpperCase());
        allStaffCourseViewHolder.className.setText(staffCourseList.get(i).getClassName().toUpperCase());
    }

    @Override
    public int getItemCount() {
        return staffCourseList.size();
    }

    class AllStaffCourseViewHolder extends RecyclerView.ViewHolder{
        TextView courseName,className;

        public AllStaffCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName= itemView.findViewById(R.id.course_name);
            className = itemView.findViewById(R.id.class_name);
        }
    }
}
