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

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.ClassNameTable;

import java.util.List;
import java.util.Random;

public class ClassResultAdapter extends RecyclerView.Adapter<ClassResultAdapter.ClassResultViewHolder> {
    Context context;
    List<ClassNameTable> classResultList;
    OnClassResultClickListener onClassResultClickListener;

    public ClassResultAdapter(Context context,
                              List<ClassNameTable> classResultList,
                              OnClassResultClickListener onClassResultClickListener) {
        this.context = context;
        this.classResultList = classResultList;
        this.onClassResultClickListener = onClassResultClickListener;
    }

    @NonNull
    @Override
    public ClassResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_result_item, viewGroup,
                false);
        return new ClassResultViewHolder(view, onClassResultClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassResultViewHolder classResultViewHolder, int i) {
        classResultViewHolder.name.setText(
                classResultList.get(i).getClassName().toUpperCase());
        classResultViewHolder.classCounter.setText(String.valueOf(i + 1));

        GradientDrawable gd =
                (GradientDrawable) classResultViewHolder.counterBackground.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
                rnd.nextInt(256));
        gd.setColor(currentColor);
        classResultViewHolder.counterBackground.setBackground(gd);
    }

    @Override
    public int getItemCount() {
        return classResultList.size();
    }

    static class ClassResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView icon;
        private final TextView name;
        private final TextView classCounter;
        OnClassResultClickListener onClassResultClickListener;
        private final LinearLayout counterBackground;

        public ClassResultViewHolder(@NonNull View itemView,
                                     OnClassResultClickListener onClassResultClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.name_student_result_item);
            counterBackground = itemView.findViewById(R.id.class_counter_bg);
            classCounter = itemView.findViewById(R.id.class_counter);
            this.onClassResultClickListener = onClassResultClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClassResultClickListener.onClassResultClick(getAdapterPosition());
        }
    }

    public interface OnClassResultClickListener {
        void onClassResultClick(int position);
    }
}
