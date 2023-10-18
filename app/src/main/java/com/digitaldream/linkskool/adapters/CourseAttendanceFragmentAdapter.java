package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.CourseTable;

import java.util.List;

public class CourseAttendanceFragmentAdapter extends RecyclerView.Adapter<CourseAttendanceFragmentAdapter.ViewHolder> {

    private final Context mContext;
    private final List<CourseTable> mCourseTableList;
    private final OnCourseClickListener mOnCourseClickListener;

    public CourseAttendanceFragmentAdapter(Context sContext,
                                           List<CourseTable> sCourseTableList
            , OnCourseClickListener sOnCourseClickListener) {
        mContext = sContext;
        mCourseTableList = sCourseTableList;
        mOnCourseClickListener = sOnCourseClickListener;
    }

    @NonNull
    @Override
    public CourseAttendanceFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(mContext).inflate(
                        R.layout.fragment_course_attendance_item, parent,
                        false);
        return new ViewHolder(view, mOnCourseClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseTable courseTable = mCourseTableList.get(position);
        String name = courseTable.getCourseName().toUpperCase();
        holder.mCourseTitle.setText(name);

        GradientDrawable mutate =
                (GradientDrawable) holder.mLinearLayout.getBackground().mutate();
        mutate.setColor(courseTable.getColor());
        holder.mLinearLayout.setBackground(mutate);
        holder.mStudentCount.setText(courseTable.getCount());

    }

    @Override
    public int getItemCount() {
        return mCourseTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnCourseClickListener mOnCourseClickListener;
        private final TextView mCourseTitle;
        private final TextView mStudentCount;
        private final LinearLayout mLinearLayout;

        public ViewHolder(@NonNull View itemView,
                          OnCourseClickListener sOnCourseClickListener) {
            super(itemView);
            mOnCourseClickListener = sOnCourseClickListener;
            mCourseTitle = itemView.findViewById(R.id.course_title);
            mStudentCount = itemView.findViewById(R.id.student_count);
            mLinearLayout = itemView.findViewById(R.id.student_count_container);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View sView) {
            mOnCourseClickListener.onCourseClick(getAdapterPosition());

        }


    }


    public interface OnCourseClickListener {
        void onCourseClick(int position);
    }

}
