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

import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.R;

import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesVH>{
    private Context context;
    private List<CourseTable> courses;
    private OnCourseClickListener onCourseClickListener;

    public CoursesAdapter(Context context, List<CourseTable> courses,OnCourseClickListener onCourseClickListener) {
        this.context = context;
        this.courses = courses;
        this.onCourseClickListener = onCourseClickListener;
    }

    @NonNull
    @Override
    public CoursesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.course_reg_layout_item,parent,false);
        return new CoursesVH(view,onCourseClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesVH holder, int position) {
        CourseTable st = courses.get(position);
        String name = st.getCourseName().toUpperCase();
        holder.name.setText(name);

        GradientDrawable gd = (GradientDrawable) holder.imgLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(st.getColor());
        holder.imgLayout.setBackground(gd);

        //holder.imgLayout.setBackgroundColor(st.getColor());
        if(st.isSelected()){
            holder.img.setImageResource(R.drawable.good);
        }else {
            holder.img.setImageResource(R.drawable.ic_check_box);
        }
        if(st!=null && st.isShowText()){
            holder.img.setVisibility(View.GONE);
            holder.initial.setVisibility(View.VISIBLE);
            try {
                holder.initial.setText(name.substring(0, 1).toUpperCase());
            }catch (StringIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class CoursesVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private LinearLayout imgLayout;
        private TextView name,initial;
        private ImageView img;
        OnCourseClickListener onCourseClickListener;

        public CoursesVH(@NonNull View itemView,OnCourseClickListener onCourseClickListener) {
            super(itemView);
            imgLayout = itemView.findViewById(R.id.check_layout);
            name = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.check);
            initial = itemView.findViewById(R.id.initial);
            this.onCourseClickListener = onCourseClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCourseClickListener.onCourseClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onCourseClickListener.onCourseLongClick(getAdapterPosition());
            return false;
        }
    }

    public interface OnCourseClickListener{
        void onCourseClick(int position);
        void onCourseLongClick(int position);
    }
}
