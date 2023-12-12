package com.digitaldream.winskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElearningCourseAdapter extends RecyclerView.Adapter<ElearningCourseAdapter.ElearningCourseViewHolder> {
private Context context;
private List<CourseTable> courseList;
OnCourseClickListener onCourseClickListener;
private List<Object> list = new ArrayList<>();
private List<CourseTable> courseLists= new ArrayList<>();

    public ElearningCourseAdapter(Context context, List<CourseTable> courseList, OnCourseClickListener onCourseClickListener) {
        this.context = context;
        this.courseList = courseList;
        this.onCourseClickListener = onCourseClickListener;

    }

    @NonNull
    @Override
    public ElearningCourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.elearning_course_item,viewGroup,false);
        return new ElearningCourseViewHolder(view,onCourseClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ElearningCourseViewHolder elearningCourseViewHolder, int i) {
        CourseTable ct = courseList.get(i);
        String courseName = ct.getCourseName();
        elearningCourseViewHolder.courseName.setText(courseName.toUpperCase());
        elearningCourseViewHolder.courseInitials.setText(courseName.substring(0,1).toUpperCase());
        GradientDrawable gd = (GradientDrawable) elearningCourseViewHolder.linearLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        //gd.invalidateSelf();
        elearningCourseViewHolder.linearLayout.setBackground(gd);

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class ElearningCourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView courseName,courseInitials;
        OnCourseClickListener onCourseClickListener;
        LinearLayout linearLayout;

        public ElearningCourseViewHolder(@NonNull View itemView,OnCourseClickListener onCourseClickListener) {
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

    public interface OnCourseClickListener{
        void onCourseClick(int position);
    }
}
