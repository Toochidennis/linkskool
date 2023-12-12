package com.digitaldream.winskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ElearningLevelAdapter1 extends RecyclerView.Adapter<ElearningLevelAdapter1.ElearningLevelViewHolder> {
    private Context context;
    private List<LevelTable> levels= new ArrayList<>();
    ElearningLevelAdapter.OnLevelClickListener onLevelClickListener;
    private List<Object> list = new ArrayList<>();
    private List<LevelTable> courseLists= new ArrayList<>();

    public ElearningLevelAdapter1(Context context, List<LevelTable> levels, ElearningLevelAdapter.OnLevelClickListener onLevelClickListener) {
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
    public ElearningLevelAdapter1.ElearningLevelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.elearning_level_item,viewGroup,false);
        return new ElearningLevelViewHolder(view, onLevelClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ElearningLevelViewHolder holder, int position) {
        LevelTable ct = levels.get(position);
        String levelName = ct.getLevelName();

        holder.levelName.setText(levelName.toUpperCase());
        holder.levelInitials.setText(String.valueOf(position+1));
        GradientDrawable gd = (GradientDrawable) holder.linearLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        //gd.invalidateSelf();
        holder.linearLayout.setBackground(gd);
    }



    @Override
    public int getItemCount() {
        return levels.size();
    }

    class ElearningLevelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView levelName,levelInitials;
        ElearningLevelAdapter.OnLevelClickListener onLevelClickListener;
        LinearLayout linearLayout;

        public ElearningLevelViewHolder(@NonNull View itemView, ElearningLevelAdapter.OnLevelClickListener onLevelClickListener) {
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
