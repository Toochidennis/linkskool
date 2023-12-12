package com.digitaldream.winskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.winskool.models.CourseOutlineTable;
import com.digitaldream.winskool.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentLevelAdapter extends RecyclerView.Adapter<StudentLevelAdapter.StudentElearningLevelViewHolder> {
    private Context context;
    private List<CourseOutlineTable> levels;
    OnLevelClickListener onLevelClickListener;
    private List<String> lt = new ArrayList<>();
    private List<Object> list = new ArrayList<>();
    private List<CourseOutlineTable> courseLists= new ArrayList<>();

    public StudentLevelAdapter(Context context, List<CourseOutlineTable> levels, OnLevelClickListener onLevelClickListener) {
        this.context = context;
        this.levels = levels;
        this.onLevelClickListener = onLevelClickListener;

    }

    @NonNull
    @Override
    public StudentElearningLevelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.elearning_level_item,viewGroup,false);
        return new StudentElearningLevelViewHolder(view,onLevelClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentElearningLevelViewHolder studentElearningLevelViewHolder, int i) {
        CourseOutlineTable ct = levels.get(i);
        String levelName = ct.getLevelName();
        try {
            studentElearningLevelViewHolder.levelName.setText(levelName.toUpperCase());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        studentElearningLevelViewHolder.levelInitials.setText(String.valueOf(i+1));
        GradientDrawable gd = (GradientDrawable) studentElearningLevelViewHolder.linearLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        //gd.invalidateSelf();
        studentElearningLevelViewHolder.linearLayout.setBackground(gd);
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    class StudentElearningLevelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView levelName,levelInitials;
        OnLevelClickListener onLevelClickListener;
        LinearLayout linearLayout;
    public StudentElearningLevelViewHolder(@NonNull View itemView, OnLevelClickListener onLevelClickListener) {
        super(itemView);
        levelName = itemView.findViewById(R.id.e_learning_level);
        levelInitials = itemView.findViewById(R.id.level_initials);
        this.onLevelClickListener = onLevelClickListener;
        linearLayout = itemView.findViewById(R.id.initials_bg);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onLevelClickListener.onLevelClick(getAdapterPosition());
    }
}

public interface OnLevelClickListener{
    void onLevelClick(int position);
}
}
