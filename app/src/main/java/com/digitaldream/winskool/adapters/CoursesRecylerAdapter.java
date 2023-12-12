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

public class CoursesRecylerAdapter extends RecyclerView.Adapter<CoursesRecylerAdapter.CoursesRecyclerViewHolder> {
    private Context context;
    private List<CourseTable> courseList;

    public CoursesRecylerAdapter(Context context, List<CourseTable> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CoursesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.course_item,viewGroup,false);
        return new CoursesRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesRecyclerViewHolder coursesRecyclerViewHolder, int i) {
        final CourseTable ct = courseList.get(i);
        String courseName = ct.getCourseName();
        String[] strArray = courseName.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for(String s: strArray){
            try{
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                stringBuilder.append(cap + " ");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        coursesRecyclerViewHolder.courseTitle.setText(stringBuilder.toString());
        coursesRecyclerViewHolder.className.setText(ct.getClassName().toUpperCase());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }


    class CoursesRecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView courseTitle,className;

        public CoursesRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.course_title);
            className = itemView.findViewById(R.id.class_name);
        }
    }
}
