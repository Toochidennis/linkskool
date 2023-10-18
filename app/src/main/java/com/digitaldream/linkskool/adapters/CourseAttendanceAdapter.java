package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.R;

import java.util.List;
import java.util.Random;

public class CourseAttendanceAdapter extends RecyclerView.Adapter<CourseAttendanceAdapter.ViewHolder> {

    private final Context mContext;
    private final List<StudentTable> mStudentTableList;
    private final OnCourseClickListener mOnCourseClickListener;

    public CourseAttendanceAdapter(Context sContext,
                                   List<StudentTable> sStudentTables,
                                   OnCourseClickListener sOnCourseClickListener) {
        mContext = sContext;
        mStudentTableList = sStudentTables;
        mOnCourseClickListener = sOnCourseClickListener;
    }

    @NonNull
    @Override
    public CourseAttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(mContext).inflate(R.layout.course_attendance_item, parent, false);
        return new ViewHolder(view, mOnCourseClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentTable studentTable = mStudentTableList.get(position);

        holder.mDate.setText(studentTable.getDate());
        holder.mStudentCount.setText(studentTable.getStudentCount());

        GradientDrawable mutate =
                (GradientDrawable) holder.mLinearLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256),
                rnd.nextInt(256), rnd.nextInt(256));
        mutate.setColor(currentColor);
        holder.mLinearLayout.setBackground(mutate);
    }

    @Override
    public int getItemCount() {
        return mStudentTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        OnCourseClickListener mOnCourseClickListener;
        private final TextView mDate;
        private final TextView mStudentCount;
        private final LinearLayout mLinearLayout;

        public ViewHolder(@NonNull View itemView,
                          OnCourseClickListener sOnCourseClickListener) {
            super(itemView);
            mOnCourseClickListener = sOnCourseClickListener;
            mDate = itemView.findViewById(R.id.attendance_date);
            mStudentCount = itemView.findViewById(R.id.student_count);
            mLinearLayout = itemView.findViewById(R.id.student_count_container);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View sView) {
            mOnCourseClickListener.onDayClick(getAdapterPosition());

        }


    }


    public interface OnCourseClickListener {
        void onDayClick(int position);

    }


}
