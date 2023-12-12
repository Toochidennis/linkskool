package com.digitaldream.winskool.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitaldream.winskool.models.FormClassModel;
import com.digitaldream.winskool.R;

import java.util.List;

public class FormClassAdapter extends RecyclerView.Adapter<FormClassAdapter.FormClassViewHolder> {
    private Context context;
    private List<FormClassModel> formClassList;
    OnFormClassClickListener onFormClassClickListener;

    public FormClassAdapter(Context context, List<FormClassModel> formClassList,OnFormClassClickListener onFormClassClickListener) {
        this.context = context;
        this.formClassList = formClassList;
        this.onFormClassClickListener = onFormClassClickListener;
    }

    @NonNull
    @Override
    public FormClassViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.all_form_class_item,viewGroup,false);
        return new FormClassViewHolder(view,onFormClassClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FormClassViewHolder formClassViewHolder, int i) {
        formClassViewHolder.className.setText(formClassList.get(i).getClassName().toUpperCase());
    }

    @Override
    public int getItemCount() {
        return formClassList.size();
    }

    class FormClassViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView className;
        OnFormClassClickListener onFormClassClickListener;
        public FormClassViewHolder(@NonNull View itemView,OnFormClassClickListener onFormClassClickListener) {
            super(itemView);
            className = itemView.findViewById(R.id.class_name);
            this.onFormClassClickListener = onFormClassClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onFormClassClickListener.onFormClassClick(getAdapterPosition());
        }
    }

    public interface OnFormClassClickListener{
        void onFormClassClick(int position);
    }
}
