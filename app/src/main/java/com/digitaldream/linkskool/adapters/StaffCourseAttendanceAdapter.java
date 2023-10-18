package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.R;

import java.util.List;
import java.util.Random;

public class StaffCourseAttendanceAdapter extends RecyclerView.Adapter<StaffCourseAttendanceAdapter.ViewHolder> {

    private final Context mContext;
    private List<StudentTable> mStudentTableList;
    private final OnStudentClickListener mStudentClickListener;

    public StaffCourseAttendanceAdapter(Context sContext,
                                        List<StudentTable> sStudentTableList, OnStudentClickListener sStudentClickListener) {
        mContext = sContext;
        mStudentTableList = sStudentTableList;
        mStudentClickListener = sStudentClickListener;
    }

    @NonNull
    @Override
    public StaffCourseAttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                      int viewType) {

        View view =
                LayoutInflater.from(mContext).inflate(R.layout.class_attendance_item, parent, false);

        return new ViewHolder(view, mStudentClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentTable studentTable = mStudentTableList.get(position);

        String name = studentTable.getStudentFullName();
        String[] strings = name.toLowerCase().split(" ");
        StringBuilder builder = new StringBuilder();
        for (String letter: strings) {
            try {
                String caps =
                        letter.substring(0,1).toUpperCase()+letter.substring(1).toLowerCase();
                builder.append(caps).append(" ");
            } catch (Exception sE) {
                sE.printStackTrace();
            }

        }

        holder.mName.setText(builder.toString());

        if (studentTable.isSelected()){
            holder.mCheckBtn.setVisibility(View.VISIBLE);
            holder.mNameInitial.setVisibility(View.GONE);
        }else{
            holder.mCheckBtn.setVisibility(View.GONE);
            holder.mNameInitial.setVisibility(View.VISIBLE);
        }


        try {
            holder.mNameInitial.setText(name.substring(0, 1).toUpperCase());
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        GradientDrawable mutate =
                (GradientDrawable) holder.mLinearLayout.getBackground().mutate();
        Random random =  new Random();
        int color = Color.argb(255, random.nextInt(256),random.nextInt(256),
                random.nextInt(240));
        mutate.setColor(color);

        holder.mLinearLayout.setBackground(mutate);

    }

    @Override
    public int getItemCount() {
        return mStudentTableList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnStudentClickListener mStudentClickListener;
        private final TextView mName;
        private final TextView mNameInitial;
        private final ImageView mCheckBtn;
        private final LinearLayout mLinearLayout;

        public ViewHolder(@NonNull View itemView, OnStudentClickListener sStudentClickListener) {
            super(itemView);
            this.mStudentClickListener = sStudentClickListener;
            mName = itemView.findViewById(R.id.student_name);
            mNameInitial = itemView.findViewById(R.id.name_initial);
            mCheckBtn = itemView.findViewById(R.id.checkBtn);
            mLinearLayout = itemView.findViewById(R.id.checkBtn_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View sView) {
            mStudentClickListener.onStudentClick(getAdapterPosition());

        }

    }

    public interface OnStudentClickListener {
        void onStudentClick(int position);
    }

}
