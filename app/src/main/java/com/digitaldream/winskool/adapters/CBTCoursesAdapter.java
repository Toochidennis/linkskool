package com.digitaldream.winskool.adapters;

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

import com.digitaldream.winskool.models.Exam;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.utils.FunctionUtils;

import java.util.List;
import java.util.Random;

public class CBTCoursesAdapter extends RecyclerView.Adapter<CBTCoursesAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Exam> mExamList;
    private final OnCourseClickListener mOnCourseClickListener;

    public CBTCoursesAdapter(Context sContext, List<Exam> sExamList, OnCourseClickListener sOnCourseClickListener) {
        mContext = sContext;
        mExamList = sExamList;
        mOnCourseClickListener = sOnCourseClickListener;
    }

    @NonNull
    @Override
    public CBTCoursesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(mContext).inflate(R.layout.fragment_cbt_courses_item, parent, false);
        return new ViewHolder(view, mOnCourseClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CBTCoursesAdapter.ViewHolder holder, int position) {
        String name = mExamList.get(position).getCourse();
        holder.mCourseName.setText(FunctionUtils.capitaliseFirstLetter(name));

        try {
            String initial = name.substring(0, 1).toUpperCase();
            holder.mInitial.setText(initial);
        } catch (Exception sE) {
            sE.printStackTrace();
        }

        GradientDrawable drawable =
                (GradientDrawable) holder.mLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256),
                rnd.nextInt(256), rnd.nextInt(240));
        drawable.setColor(currentColor);

    }

    @Override
    public int getItemCount() {
        return mExamList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final OnCourseClickListener mOnCourseClickListener;
        private final LinearLayout mLayout;
        private final TextView mCourseName;
        private final TextView mInitial;

        public ViewHolder(@NonNull View itemView, OnCourseClickListener sOnCourseClickListener) {
            super(itemView);
            mOnCourseClickListener = sOnCourseClickListener;
            mLayout = itemView.findViewById(R.id.exam_container);
            mCourseName = itemView.findViewById(R.id.course_name);
            mInitial = itemView.findViewById(R.id.course_initial);
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
