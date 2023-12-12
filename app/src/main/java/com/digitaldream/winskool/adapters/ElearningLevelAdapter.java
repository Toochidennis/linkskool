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

import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElearningLevelAdapter extends RecyclerView.Adapter<ElearningLevelAdapter.ElearningLevelViewHolder>{
private Context context;
private List<CourseTable> levels;
OnLevelClickListener onLevelClickListener;
private List<Object> list = new ArrayList<>();
private List<CourseTable> courseLists= new ArrayList<>();

    public ElearningLevelAdapter(Context context, List<CourseTable> levels,OnLevelClickListener onLevelClickListener) {
        this.context = context;
        this.levels = levels;
        this.onLevelClickListener = onLevelClickListener;

        for(int i =0;i<levels.size();i++){
            if(!list.contains(levels.get(i).getLevelName())){

                courseLists.add(levels.get(i));
                list.add(levels.get(i).getLevelName());
            }
        }
    }


    @NonNull
    @Override
    public ElearningLevelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.elearning_level_item,viewGroup,false);
        return new ElearningLevelViewHolder(view,onLevelClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ElearningLevelViewHolder elearningLevelViewHolder, int i) {
        CourseTable ct = courseLists.get(i);
        String levelName = ct.getLevelName();

        elearningLevelViewHolder.levelName.setText(levelName.toUpperCase());
        elearningLevelViewHolder.levelInitials.setText(String.valueOf(i+1));
        GradientDrawable gd = (GradientDrawable) elearningLevelViewHolder.linearLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        //gd.invalidateSelf();
        elearningLevelViewHolder.linearLayout.setBackground(gd);



    }

    @Override
    public int getItemCount() {
        return courseLists.size();
    }

    class ElearningLevelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView levelName,levelInitials;
        OnLevelClickListener onLevelClickListener;
        LinearLayout linearLayout;

        public ElearningLevelViewHolder(@NonNull View itemView,OnLevelClickListener onLevelClickListener) {
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
