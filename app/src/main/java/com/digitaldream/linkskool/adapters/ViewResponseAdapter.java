package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.linkskool.models.ViewResponseModel;
import com.digitaldream.linkskool.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewResponseAdapter extends RecyclerView.Adapter<ViewResponseAdapter.ViewResponseVH>{
    private Context context;
    private List<ViewResponseModel> list;
    OnResponseListener onResponseListener;

    public ViewResponseAdapter(Context context, List<ViewResponseModel> list, OnResponseListener onResponseListener) {
        this.context = context;
        this.list = list;
        this.onResponseListener = onResponseListener;
    }

    @NonNull
    @Override
    public ViewResponseVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_response_item,parent,false);
        return new ViewResponseVH(view, onResponseListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewResponseVH holder, int position) {
        ViewResponseModel vm = list.get(position);
        GradientDrawable gd = (GradientDrawable) holder.bg.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        //gd.invalidateSelf();
        holder.bg.setBackground(gd);

        holder.number.setText(String.valueOf(position+1));
        holder.studentName.setText(vm.getStudentName());
        String date = vm.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = simpleDateFormat.parse(date);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMM, yyyy");
            holder.date.setText(simpleDateFormat1.format(date1));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.score.setText(vm.getScore());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewResponseVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView number,studentName,score,date;
        LinearLayout bg;
        OnResponseListener onResponseListener;
        public ViewResponseVH(@NonNull View itemView, OnResponseListener onResponseListener) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            studentName = itemView.findViewById(R.id.student_name);
            score = itemView.findViewById(R.id.progress_text);
            date = itemView.findViewById(R.id.date_layout);
            bg = itemView.findViewById(R.id.num_bg);
            itemView.setOnClickListener(this);
            this.onResponseListener = onResponseListener;

        }

        @Override
        public void onClick(View v) {
            onResponseListener.onResponseClick(getAdapterPosition());
        }
    }

    public interface OnResponseListener {
        void onResponseClick(int position);
    }
}
