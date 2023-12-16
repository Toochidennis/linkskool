package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.PrevYrModel;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PrevYrsAdapter extends RecyclerView.Adapter<PrevYrsAdapter.PrevYrVH> {
    private Context context;
    private List<PrevYrModel> list;
    OnYearClickListener onYearClickListener;

    public PrevYrsAdapter(Context context, List<PrevYrModel> list,OnYearClickListener onYearClickListener) {
        this.context = context;
        this.list = list;
        this.onYearClickListener = onYearClickListener;
    }

    @NonNull
    @Override
    public PrevYrVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.prev_yr_layout_item,parent,false);
        return new PrevYrVH(view,onYearClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PrevYrVH holder, int position) {
        PrevYrModel pr = list.get(position);
        String year = pr.getYear();
        try {
            int prevYr = Integer.parseInt(year) - 1;
            String titleText = String.format(Locale.getDefault(), "%d/%s %s", prevYr,
                    pr.getYear(), pr.getName());
            holder.title.setText(titleText);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        GradientDrawable gd = (GradientDrawable) holder.linearLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        holder.linearLayout.setBackground(gd);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PrevYrVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        LinearLayout linearLayout;
        OnYearClickListener onYearClickListener;
        public PrevYrVH(@NonNull View itemView,OnYearClickListener onYearClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.debt_fee_title);
            linearLayout = itemView.findViewById(R.id.initials_bg);
            itemView.setOnClickListener(this);
            this.onYearClickListener= onYearClickListener;
        }

        @Override
        public void onClick(View v) {
            onYearClickListener.onYearClick(getAdapterPosition());
        }
    }

    public interface OnYearClickListener{
        void onYearClick(int position);
    }
}
