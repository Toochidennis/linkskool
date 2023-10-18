package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.models.Exam;
import com.digitaldream.linkskool.R;

import java.util.List;

public class CBTYearAdapter extends RecyclerView.Adapter<CBTYearAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Exam> mExamList;
    private final OnYearClickListener mOnYearClickListener;

    private final int[] colors = {R.color.color_1, R.color.color_2,
            R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_6, R.color.color_7, R.color.color_8,
            R.color.color_2, R.color.color_5};

    public CBTYearAdapter(Context sContext, List<Exam> sExamList,
                          OnYearClickListener sOnYearClickListener) {
        mContext = sContext;
        mExamList = sExamList;
        mOnYearClickListener = sOnYearClickListener;
    }


    @NonNull
    @Override
    public CBTYearAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                        int viewType) {
        View view =
                LayoutInflater.from(mContext).inflate(R.layout.fragment_cbt_year_item, parent, false);
        return new ViewHolder(view, mOnYearClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String year = mExamList.get(position).getYear();
        holder.mTextView.setText(year);

        holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(mContext, colors[position % 10]));

    }

    @Override
    public int getItemCount() {
        return mExamList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OnYearClickListener mOnYearClickListener;
        private final CardView mCardView;
        private final TextView mTextView;

        public ViewHolder(@NonNull View itemView,
                          OnYearClickListener sOnYearClickListener) {
            super(itemView);
            mOnYearClickListener = sOnYearClickListener;
            mCardView = itemView.findViewById(R.id.year_layout);
            mTextView = itemView.findViewById(R.id.year);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View sView) {
            mOnYearClickListener.onYearClick(getAdapterPosition());
        }
    }


    public interface OnYearClickListener {
        void onYearClick(int position);
    }
}
