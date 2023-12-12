package com.digitaldream.winskool.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.winskool.R;
import com.digitaldream.winskool.models.StudentTable;

import java.util.List;
import java.util.Random;

public class StudentResultAdapter extends RecyclerView.Adapter<StudentResultAdapter.StudentResultViewHolder> {
    Context context;
    List<StudentTable> studentResultList;
    OnStudentResultClickListener onStudentResultClickListener;

    public StudentResultAdapter(Context context, List<StudentTable> studentResultList,
                                OnStudentResultClickListener onStudentResultClickListener) {
        this.context = context;
        this.studentResultList = studentResultList;
        this.onStudentResultClickListener = onStudentResultClickListener;
    }

    @NonNull
    @Override
    public StudentResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_result_item, viewGroup, false);
        return new StudentResultViewHolder(view, onStudentResultClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentResultViewHolder studentResultViewHolder, int i) {
        final StudentTable st = studentResultList.get(i);
        String studentName =
                st.getStudentSurname() + " " + st.getStudentMiddlename() + " " + st.getStudentFirstname();
        studentResultViewHolder.studentCounter.setText(String.valueOf(i + 1));

        GradientDrawable gd =
                (GradientDrawable) studentResultViewHolder.counterBackground.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        studentResultViewHolder.counterBackground.setBackground(gd);


        String[] strArray = studentName.split(" ");
        StringBuilder builder = new StringBuilder();
        try {
            for (String s : strArray) {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap + " ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        studentResultViewHolder.name.setText(builder.toString());

    }

    @Override
    public int getItemCount() {
        if (studentResultList.isEmpty()) {
            return 0;
        } else {
            return studentResultList.size();
        }
    }

    static class StudentResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView icon;
        private TextView name, studentCounter;
        private LinearLayout counterBackground;
        OnStudentResultClickListener onStudentResultClickListener;


        public StudentResultViewHolder(@NonNull View itemView,
                                       OnStudentResultClickListener onStudentResultClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.name_student_result_item);
            this.onStudentResultClickListener = onStudentResultClickListener;
            studentCounter = itemView.findViewById(R.id.class_counter);
            counterBackground = itemView.findViewById(R.id.class_counter_bg);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onStudentResultClickListener.onStudentResultClick(getAdapterPosition());
        }
    }

    public interface OnStudentResultClickListener {
        void onStudentResultClick(int position);
    }

}