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

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.ClassNameTable;

import java.util.List;
import java.util.Random;

public class StaffClassAttendanceAdapter extends RecyclerView.Adapter<StaffClassAttendanceAdapter.ViewHolder> {

    private final Context mContext;
    private final List<ClassNameTable> mClassNameTableList;
    private final OnLevelClickListener mOnLevelClickListener;

    public StaffClassAttendanceAdapter(Context sContext,
                                       List<ClassNameTable> sClassNameTableList,
                                       OnLevelClickListener sOnLevelClickListener) {
        mContext = sContext;
        mClassNameTableList = sClassNameTableList;
        mOnLevelClickListener = sOnLevelClickListener;
    }

    @NonNull
    @Override
    public StaffClassAttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.fragment_staff_attendance_class_item, parent, false);
        return new ViewHolder(view, mOnLevelClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull StaffClassAttendanceAdapter.ViewHolder holder, int position) {
        holder.mLevelName.setText(
                mClassNameTableList.get(position).getClassName());
        //holder.mInitial.setText(position + 1);

        GradientDrawable gd =
                (GradientDrawable) holder.mLayout.getBackground().mutate();
        Random random = new Random();
        int currentColor = Color.argb(255, random.nextInt(256),
                random.nextInt(256), random.nextInt(256));
        gd.setColor(currentColor);
        holder.mLayout.setBackground(gd);


    }

    @Override
    public int getItemCount() {
        return mClassNameTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OnLevelClickListener mOnLevelClickListener;
        private final TextView mLevelName;
        private final TextView mInitial;
        private final LinearLayout mLayout;


        public ViewHolder(@NonNull View itemView,
                          OnLevelClickListener sOnLevelClickListener) {
            super(itemView);
            mOnLevelClickListener = sOnLevelClickListener;
            mLevelName = itemView.findViewById(R.id.level_name);
            mInitial = itemView.findViewById(R.id.level_initial);
            mLayout = itemView.findViewById(R.id.level_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View sView) {

            mOnLevelClickListener.onLevelClick(getAdapterPosition());

        }
    }


    public interface OnLevelClickListener {
        void onLevelClick(int position);
    }
}
