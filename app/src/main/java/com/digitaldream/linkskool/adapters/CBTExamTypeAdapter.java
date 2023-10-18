package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.ExamType;
import com.digitaldream.linkskool.utils.FunctionUtils;

import java.util.List;

public class CBTExamTypeAdapter extends RecyclerView.Adapter<CBTExamTypeAdapter.ViewHolder> {

    private final Context mContext;
    private final List<ExamType> mExamTypeList;
    private final OnExamClickListener mOnExamClickListener;

    private final int[] icons = {R.drawable.ic_cbt, R.drawable.ic_cbt_1,
            R.drawable.ic_cbt_2, R.drawable.ic_cbt_3, R.drawable.ic_cbt_4,
            R.drawable.ic_cbt_5, R.drawable.ic_cbt_7, R.drawable.ic_cbt_6,
            R.drawable.ic_cbt_8, R.drawable.ic_cbt_9};

    private final int[] colors = {R.color.color_1, R.color.color_2,
            R.color.color_3, R.color.color_4, R.color.color_5,
            R.color.color_6, R.color.color_7, R.color.color_8,
            R.color.color_2, R.color.color_5};

    public CBTExamTypeAdapter(Context sContext, List<ExamType> sExamTypeList,
                              OnExamClickListener sOnExamClickListener) {
        mContext = sContext;
        mExamTypeList = sExamTypeList;
        mOnExamClickListener = sOnExamClickListener;
    }


    @NonNull
    @Override
    public CBTExamTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                            int viewType) {
        View view =
                LayoutInflater.from(mContext).inflate(R.layout.fragment_cbt_exam_type_item, parent,
                        false);
        return new ViewHolder(view, mOnExamClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CBTExamTypeAdapter.ViewHolder holder, int position) {
        String name = mExamTypeList.get(position).getExamName();
        holder.mTextView.setText(FunctionUtils.capitaliseFirstLetter(name));

        holder.mCardView.setCardBackgroundColor(
                ContextCompat.getColor(mContext, colors[position % 11]));
        holder.mImageView.setImageResource(icons[position % 10]);

    }

    @Override
    public int getItemCount() {
        return mExamTypeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OnExamClickListener mOnExamClickListener;
        private final CardView mCardView;
        private final TextView mTextView;
        private final ImageView mImageView;

        public ViewHolder(@NonNull View itemView, OnExamClickListener sOnExamClickListener) {
            super(itemView);
            mOnExamClickListener = sOnExamClickListener;
            mCardView = itemView.findViewById(R.id.grid_layout);
            mTextView = itemView.findViewById(R.id.exam_name);
            mImageView = itemView.findViewById(R.id.debt_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View sView) {
            mOnExamClickListener.onExamClick(getAdapterPosition());
        }
    }


    public interface OnExamClickListener {
        void onExamClick(int position);
    }

}
