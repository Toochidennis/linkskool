package com.digitaldream.winskool.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitaldream.winskool.R;
import com.digitaldream.winskool.models.TeachersTable;

import java.util.List;

public class AllTeacherAdapter extends RecyclerView.Adapter<AllTeacherAdapter.AllStudentViewHolder> {
    private Context context;
    private List<TeachersTable> teachersList;
    OnTeacherClickListener onTeacherClickListener;

    public AllTeacherAdapter(Context context, List<TeachersTable> teachersList, OnTeacherClickListener onTeacherClickListener) {
        this.context = context;
        this.teachersList = teachersList;
        this.onTeacherClickListener = onTeacherClickListener;
    }

    @NonNull
    @Override
    public AllStudentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.all_teacher_item,viewGroup,false);
        return new AllStudentViewHolder(view,onTeacherClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AllStudentViewHolder allStudentViewHolder, int i) {
        TeachersTable tch = teachersList.get(i);
        allStudentViewHolder.teacherName.setText(tch.getStaffSurname().toUpperCase()+" "+tch.getStaffFirstname().toUpperCase());
    }

    @Override
    public int getItemCount() {
        return teachersList.size();
    }

    class AllStudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView teacherName;
        OnTeacherClickListener onTeacherClickListener;

        public AllStudentViewHolder(@NonNull View itemView,OnTeacherClickListener onTeacherClickListener) {
            super(itemView);
            teacherName = itemView.findViewById(R.id.teacher_name);
            this.onTeacherClickListener = onTeacherClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTeacherClickListener.onTeacherClick(getAdapterPosition());
        }
    }

    public interface OnTeacherClickListener{
        void onTeacherClick(int position);
    }
}
