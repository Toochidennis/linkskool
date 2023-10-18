package com.digitaldream.linkskool.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.VideoTable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>  {

    private Context context;
    private List<VideoTable> videoList;
    OnVideoItemClickListener onVideoItemClickListener;

    public VideoAdapter(Context context, List<VideoTable> videoList, OnVideoItemClickListener onVideoItemClickListener) {
        this.context = context;
        this.videoList = videoList;
        this.onVideoItemClickListener = onVideoItemClickListener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.videos_item,viewGroup,false);

        return new VideoViewHolder(view,onVideoItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoViewHolder videoViewHolder, int i) {
        videoViewHolder.videoTitle.setText(videoList.get(i).getVideoTitle());
        videoViewHolder.videoCategory.setText(videoList.get(i).getVideoSubject());
        //Picasso.get().load(videoList.get(i).getThumbnailUrl()).into(videoViewHolder.thumbnail);
        Picasso.get().load(videoList.get(i).getThumbnail()).into(videoViewHolder.thumbnail, new Callback() {
            @Override
            public void onSuccess() {
                videoViewHolder.progressBar.setVisibility(View.GONE);
                videoViewHolder.thumbnail.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                videoViewHolder.progressBar.setVisibility(View.GONE);
                videoViewHolder.thumbnail.setImageResource(R.drawable.white_background);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView thumbnail;
        TextView videoTitle;
        TextView videoCategory;
        ProgressBar progressBar;
        OnVideoItemClickListener onVideoItemClickListener;
        public VideoViewHolder(@NonNull View itemView,OnVideoItemClickListener onVideoItemClickListener) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            videoTitle = itemView.findViewById(R.id.video_title);
            videoCategory = itemView.findViewById(R.id.video_category);
            progressBar = itemView.findViewById(R.id.progress);
            this.onVideoItemClickListener = onVideoItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onVideoItemClickListener.onVideoItemClick(getAdapterPosition());
        }

    }
    public interface OnVideoItemClickListener{
        void onVideoItemClick(int position);
    }
}
