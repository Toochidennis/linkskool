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
import com.digitaldream.linkskool.utils.FunctionUtils;

import java.util.List;
import java.util.Random;


public class AttendanceDetailsAdapter extends RecyclerView.Adapter<AttendanceDetailsAdapter.ViewHolder> {

    private final Context mContext;
    private final List<StudentTable> mStudentTableList;

    public AttendanceDetailsAdapter(Context sContext, List<StudentTable> sStudentTableList) {
        mContext = sContext;
        mStudentTableList = sStudentTableList;
    }

    @NonNull
    @Override
    public AttendanceDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =
                LayoutInflater.from(mContext).inflate(R.layout.details_attendance_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceDetailsAdapter.ViewHolder holder, int position) {
        StudentTable model = mStudentTableList.get(position);

        String name = model.getStudentFullName();

        holder.mName.setText(FunctionUtils.capitaliseFirstLetter(name));

        try {
            holder.mInitial.setText(name.substring(0, 1).toUpperCase());
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        GradientDrawable mutate =
                (GradientDrawable) holder.mLinearLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256),
                rnd.nextInt(256), rnd.nextInt(240));
        mutate.setColor(currentColor);

        holder.mLinearLayout.setBackground(mutate);
    }

    @Override
    public int getItemCount() {
        return mStudentTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout mLinearLayout;
        private final TextView mName;
        private final TextView mInitial;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.name_initial_container);
            mName = itemView.findViewById(R.id.student_name);
            mInitial = itemView.findViewById(R.id.name_initial);
        }
    }
}
