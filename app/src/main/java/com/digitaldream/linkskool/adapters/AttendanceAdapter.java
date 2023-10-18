package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> implements Filterable {

    private final Context mContext;
    private List<StudentTable> mStudentTableList;
    private final List<StudentTable> mStudentTableListFull;
    private final OnDayClickListener mDayClickListener;


    public AttendanceAdapter(Context sContext, List<StudentTable> sStudentTableList,
                             OnDayClickListener sDayClickListener) {
        mContext = sContext;
        mStudentTableListFull = sStudentTableList;
        mDayClickListener = sDayClickListener;
        mStudentTableList = mStudentTableListFull;
    }


    @NonNull
    @Override
    public AttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =
                LayoutInflater.from(mContext).inflate(R.layout.fragment_class_attendance_item, parent, false);

        return new ViewHolder(view, mDayClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.ViewHolder holder, int position) {
        StudentTable model = mStudentTableList.get(position);

        holder.mAttendanceDate.setText(model.getDate());
        holder.mStudentCount.setText(model.getStudentCount());


        GradientDrawable mutate =
                (GradientDrawable) holder.mLinearLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        mutate.setColor(currentColor);

        holder.mLinearLayout.setBackground(mutate);

    }

    @Override
    public int getItemCount() {
        return mStudentTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OnDayClickListener mDayClickListener;
        private final LinearLayout mLinearLayout;
        private final TextView mStudentCount;
        private final TextView mAttendanceDate;


        public ViewHolder(@NonNull View itemView, OnDayClickListener sDayClickListener) {
            super(itemView);
            this.mDayClickListener = sDayClickListener;
            mLinearLayout = itemView.findViewById(R.id.student_count_container);
            mStudentCount = itemView.findViewById(R.id.student_count);
            mAttendanceDate = itemView.findViewById(R.id.attendance_date);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View sView) {
            mDayClickListener.onDayClick(getAdapterPosition());


        }

    }

    public interface OnDayClickListener {
        void onDayClick(int position);

    }


    @Override
    public Filter getFilter() {
        return attendanceFilter;
    }

    private final Filter attendanceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence sCharSequence) {
            List<StudentTable> filteredList = new ArrayList<>();

            if (sCharSequence == null || sCharSequence.length() == 0) {
                filteredList.addAll(mStudentTableListFull);
            } else {

                String filteredItem =
                        sCharSequence.toString().toLowerCase();

                for (StudentTable item : mStudentTableListFull) {

                    if (item.getDate().toLowerCase().contains(filteredItem)) {
                        filteredList.add(item);
                    }

                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;

        }

        @Override
        protected void publishResults(CharSequence sCharSequence, FilterResults sFilterResults) {

            mStudentTableList.clear();
            mStudentTableList.addAll((List) sFilterResults.values);
            notifyDataSetChanged();

        }
    };

}
