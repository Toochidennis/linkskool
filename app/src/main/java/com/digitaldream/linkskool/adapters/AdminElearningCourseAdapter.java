package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminElearningCourseAdapter extends
        RecyclerView.Adapter<AdminElearningCourseAdapter.StudentElearningCourseViewHolder> {

    private Context context;
    private List<CourseTable> courseList;
    OnCourseClickListener onCourseClickListener;
    private List<Object> list = new ArrayList<>();
    private List<CourseOutlineTable> courseLists = new ArrayList<>();

    public AdminElearningCourseAdapter(Context context, List<CourseTable> courseList,
                                       OnCourseClickListener onCourseClickListener) {
        this.context = context;
        this.courseList = courseList;
        this.onCourseClickListener = onCourseClickListener;

    }

    @NonNull
    @Override
    public StudentElearningCourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_e_learning_course, viewGroup, false);
        return new StudentElearningCourseViewHolder(view, onCourseClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentElearningCourseViewHolder holder, int position) {
        CourseTable ct = courseList.get(position);
        String courseName = ct.getCourseName();
        ;
        if (courseName.isEmpty()) {
            holder.courseInitials.setText(" ");
        } else {
            holder.courseInitials.setText(courseName.substring(0, 1).toUpperCase());
        }

        holder.courseName.setText(courseName.toUpperCase());
        GradientDrawable gd = (GradientDrawable) holder.linearLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        //gd.invalidateSelf();
        holder.linearLayout.setBackground(gd);
    }


    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class StudentElearningCourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView courseName, courseInitials;
        OnCourseClickListener onCourseClickListener;
        LinearLayout linearLayout;

        public StudentElearningCourseViewHolder(@NonNull View itemView, OnCourseClickListener onCourseClickListener) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            courseInitials = itemView.findViewById(R.id.course_initials);
            linearLayout = itemView.findViewById(R.id.initials_bg);
            this.onCourseClickListener = onCourseClickListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onCourseClickListener.onCourseClick(getAdapterPosition());
        }
    }

    public interface OnCourseClickListener {
        void onCourseClick(int position);
    }
}
