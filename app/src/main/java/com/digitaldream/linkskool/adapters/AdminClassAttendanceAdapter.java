package com.digitaldream.linkskool.adapters;

import android.content.Context;
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

public class AdminClassAttendanceAdapter extends RecyclerView.Adapter<AdminClassAttendanceAdapter.ViewHolder> {

    private final Context mContext;
    private final List<StudentTable> mStudentTableList;
    private final OnStudentClickListener mStudentClickListener;

    public AdminClassAttendanceAdapter(Context sContext, List<StudentTable> sStudentTableList, OnStudentClickListener sStudentClickListener) {
        mContext = sContext;
        mStudentTableList = sStudentTableList;
        mStudentClickListener = sStudentClickListener;
    }

    @NonNull
    @Override
    public AdminClassAttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =
                LayoutInflater.from(mContext).inflate(R.layout.class_attendance_item, parent, false);

        return new ViewHolder(view, mStudentClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentTable studentTable = mStudentTableList.get(position);

        String surName = studentTable.getStudentSurname();
        String middleName =  studentTable.getStudentMiddlename();
        String firstName = studentTable.getStudentFirstname();

        try{
            if(surName!=null) {
                surName = surName.substring(0, 1).toUpperCase() + "" + surName.substring(1).toLowerCase();
            }else {
                surName = "";
            }
            if(middleName!=null) {
                middleName = middleName.substring(0, 1).toUpperCase() + "" + middleName.substring(1).toLowerCase();
            }else {
                middleName="";
            }
            if(firstName!=null) {
                firstName = firstName.substring(0, 1).toUpperCase() + "" + firstName.substring(1).toLowerCase();
            }else {
                firstName = "";
            }

        }catch (StringIndexOutOfBoundsException | NullPointerException e){
            e.printStackTrace();
        }

        String name = surName+ " " + middleName + " " + firstName;

        holder.mName.setText(name);

        if (studentTable.isSelected()){
            holder.mCheckBtn.setVisibility(View.VISIBLE);
            holder.mNameInitial.setVisibility(View.GONE);
        }else{
            holder.mCheckBtn.setVisibility(View.GONE);
            holder.mNameInitial.setVisibility(View.VISIBLE);
        }


        try {
            holder.mNameInitial.setText(name.substring(0, 1).toUpperCase());
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        GradientDrawable mutate =
                (GradientDrawable) holder.mLinearLayout.getBackground().mutate();
        mutate.setColor(studentTable.getColor());

        holder.mLinearLayout.setBackground(mutate);


    }

    @Override
    public int getItemCount() {
        return mStudentTableList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        OnStudentClickListener mStudentClickListener;
        private final TextView mName;
        private final TextView mNameInitial;
        private final ImageView mCheckBtn;
        private final LinearLayout mLinearLayout;

        public ViewHolder(@NonNull View itemView, OnStudentClickListener sStudentClickListener) {
            super(itemView);
            this.mStudentClickListener = sStudentClickListener;
            mName =  itemView.findViewById(R.id.student_name);
            mNameInitial = itemView.findViewById(R.id.name_initial);
            mCheckBtn =  itemView.findViewById(R.id.checkBtn);
            mLinearLayout = itemView.findViewById(R.id.checkBtn_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View sView) {
            mStudentClickListener.onStudentClick(getAdapterPosition());


        }

        @Override
        public boolean onLongClick(View sView) {
            mStudentClickListener.onStudentLongClick(getAdapterPosition());
            return false;
        }
    }

    public interface OnStudentClickListener {
        void onStudentClick(int position);
        void onStudentLongClick(int position);
    }
}
