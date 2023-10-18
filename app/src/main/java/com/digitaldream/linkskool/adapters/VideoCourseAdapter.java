package com.digitaldream.linkskool.adapters;

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

import com.digitaldream.linkskool.models.VideoTable;
import com.digitaldream.linkskool.R;

import java.util.List;
import java.util.Random;

public class VideoCourseAdapter extends RecyclerView.Adapter<VideoCourseAdapter.VideoCourseViewHolder> {
    private Context context;
    private List<VideoTable> videoList;
    OnVideoCourseClickListener onVideoCourseClickListener;

    public VideoCourseAdapter(Context context, List<VideoTable> videoList,OnVideoCourseClickListener onVideoCourseClickListener) {
        this.context = context;
        this.videoList = videoList;
        this.onVideoCourseClickListener = onVideoCourseClickListener;
    }

    @NonNull
    @Override
    public VideoCourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.elearning_course_item,viewGroup,false);
        return new VideoCourseViewHolder(view,onVideoCourseClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoCourseViewHolder videoCourseViewHolder, int i) {
        VideoTable ct = videoList.get(i);
        String courseName = ct.getVideoSubject();
        videoCourseViewHolder.courseName.setText(courseName.toUpperCase());
        videoCourseViewHolder.courseInitials.setText(courseName.substring(0,1).toUpperCase());
        GradientDrawable gd = (GradientDrawable) videoCourseViewHolder.linearLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        //gd.invalidateSelf();
        videoCourseViewHolder.linearLayout.setBackground(gd);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class VideoCourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView courseName,courseInitials;
        LinearLayout linearLayout;
        OnVideoCourseClickListener onVideoCourseClickListener;

        public VideoCourseViewHolder(@NonNull View itemView,OnVideoCourseClickListener onVideoCourseClickListener) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            courseInitials = itemView.findViewById(R.id.course_initials);
            linearLayout = itemView.findViewById(R.id.initials_bg);
            this.onVideoCourseClickListener = onVideoCourseClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onVideoCourseClickListener.onVideoCourseClick(getAdapterPosition());
        }
    }

    public interface OnVideoCourseClickListener{
        void onVideoCourseClick(int position);
    }
}
