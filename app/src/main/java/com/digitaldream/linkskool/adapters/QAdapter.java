package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.digitaldream.linkskool.activities.ExamActivity;
import com.digitaldream.linkskool.models.QViewModel;
import com.digitaldream.linkskool.R;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class QAdapter extends RecyclerView.Adapter<QAdapter.QViewHolder> {
    Context context;
    List<QViewModel> options;
    String[] alphabets = {"a","b","c","d","e","f","g","h","i"};
    OnOptionClickListener onOptionClickListener;
    int row1;


    public QAdapter(Context context, List<QViewModel> options,OnOptionClickListener onOptionClickListener) {
        this.context = context;
        this.options = options;
        this.onOptionClickListener = onOptionClickListener;
    }

    @NonNull
    @Override
    public QViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.options_layout,parent,false);
        return new QViewHolder(view,onOptionClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final QViewHolder holder, final int position) {
        final QViewModel q = options.get(position);
        String option = q.getOpt();
        option = option.substring(0,1).toUpperCase()+option.substring(1);
  if(q.isChecked()){
    holder.cardView.setCardBackgroundColor(Color.GRAY);
 }else{
    holder.cardView.setCardBackgroundColor(Color.WHITE);

}
        //holder.cardView.setCardBackgroundColor(q.isChecked()? Color.parseColor("#ECECEC") : Color.WHITE);

        holder.answerOption.setText(option);
        holder.alphabet.setText(alphabets[position]+".");
        runEnterAnimation(holder.answerOption);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //q.setChecked(!q.isChecked());
                for(int a=0;a<options.size();a++){
                    options.get(a).setChecked(false);
                }
                options.get(position).setChecked(true);
                //holder.cardView.setCardBackgroundColor(q.isChecked()? Color.parseColor("#ECECEC") : Color.WHITE);
                //ExamActivity.adapter.notifyDataSetChanged();*/
                //row1 = position;
                ExamActivity.checkNext=false;
                q.setYourAnswer(options.get(position).getOpt());
                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    class QViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView alphabet,answerOption;
        OnOptionClickListener onOptionClickListener;
        CardView cardView;

        public QViewHolder(@NonNull View itemView, OnOptionClickListener onOptionClickListener) {
            super(itemView);
            answerOption = itemView.findViewById(R.id.opt);
            cardView = itemView.findViewById(R.id.view_cont);
            this.onOptionClickListener = onOptionClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onOptionClickListener.onOptionClick(getAdapterPosition());
        }
    }

    public interface OnOptionClickListener{
        void onOptionClick(int position);
    }

    private void runEnterAnimation(View view) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();



        //getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        view.setTranslationY(height);
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    class QuestionModel{
        boolean isChecked=false;
        String opt;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public String getOpt() {
            return opt;
        }

        public void setOpt(String opt) {
            this.opt = opt;
        }
    }
}
