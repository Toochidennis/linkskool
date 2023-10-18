package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitaldream.linkskool.models.VideoTable;
import com.digitaldream.linkskool.R;

import java.util.List;
import java.util.Random;

public class VideoCategoryAdapter extends RecyclerView.Adapter<VideoCategoryAdapter.VideoCategoryViewHolder> {
    private Context context;
    private List<VideoTable> videoCategoryList;
    OnVideoTitleClickListener onVideoTitleClickListener;

    public VideoCategoryAdapter(Context context, List<VideoTable> videoCategoryList, OnVideoTitleClickListener onVideoTitleClickListener) {
        this.context = context;
        this.videoCategoryList = videoCategoryList;
        this.onVideoTitleClickListener = onVideoTitleClickListener;
    }

    @NonNull
    @Override
    public VideoCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.video_category_item,viewGroup,false);
        return new VideoCategoryViewHolder(view,onVideoTitleClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoCategoryViewHolder videoCategoryViewHolder, int i) {
        videoCategoryViewHolder.categoryTitle.setText(videoCategoryList.get(i).getLevelName().toUpperCase());
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        videoCategoryViewHolder.itemView.setBackgroundColor(currentColor);
    }

    @Override
    public int getItemCount() {
        return videoCategoryList.size();
    }

    class VideoCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView categoryTitle;
        OnVideoTitleClickListener onVideoTitleClickListener;

        public VideoCategoryViewHolder(@NonNull View itemView,OnVideoTitleClickListener onVideoTitleClickListener) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.video_category_title);
            this.onVideoTitleClickListener = onVideoTitleClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onVideoTitleClickListener.onVideoClick(getAdapterPosition());
        }
    }

    public interface OnVideoTitleClickListener{
        void onVideoClick(int position);
    }
}
