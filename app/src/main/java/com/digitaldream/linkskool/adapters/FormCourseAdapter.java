package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.linkskool.R;

import java.util.List;

public class FormCourseAdapter extends RecyclerView.Adapter<FormCourseAdapter.FormCourseViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private List<String> list;
    OnClearClickListener onClearClickListener;
    public FormCourseAdapter(Context context, List<String> list,OnClearClickListener onClearClickListener) {
        this.context = context;
        this.list = list;

        this.onClearClickListener = onClearClickListener;
    }

    @NonNull
    @Override
    public FormCourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.assigned_courses_item,viewGroup,false);
        return new FormCourseViewHolder(view,onClearClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FormCourseViewHolder formCourseViewHolder, int i) {
        String ex = list.get(i);
        ex = ex.substring(ex.lastIndexOf("-")+1,ex.lastIndexOf("-")+2);

        if(Integer.parseInt(ex)==0){

            formCourseViewHolder.itemView.setBackgroundColor(Color.parseColor("#F6F7F9"));
            formCourseViewHolder.clearBtnBg.setBackgroundColor(Color.parseColor("#F6F7F9"));
            formCourseViewHolder.clear.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(formCourseViewHolder.itemView.getLayoutParams());
            marginLayoutParams.setMargins(15, 30, 15, 0);
            formCourseViewHolder.itemView.setLayoutParams(marginLayoutParams);
        }else{
            formCourseViewHolder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(formCourseViewHolder.itemView.getLayoutParams());
            marginLayoutParams.setMargins(15, 0, 15, 0);
            formCourseViewHolder.itemView.setLayoutParams(marginLayoutParams);
            formCourseViewHolder.clear.setVisibility(View.VISIBLE);

        }
        String ex1 = list.get(i);
        int iend = ex1.indexOf("-");
        if(iend!=-1) {
            ex1=ex1.substring(0,iend);

            formCourseViewHolder.title.setText(list.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class FormCourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView clear;
        LinearLayout clearBtnBg;
        OnClearClickListener onClearClickListener;
        public FormCourseViewHolder(@NonNull View itemView,OnClearClickListener onClearClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.classnames);
            clear = itemView.findViewById(R.id.course_clear);
            clearBtnBg = itemView.findViewById(R.id.comp_bg);
            this.onClearClickListener = onClearClickListener;
            clear.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onClearClickListener.onClearClick(getAdapterPosition());
        }
    }

    public interface OnClearClickListener{
        void onClearClick(int position);
    }
}
