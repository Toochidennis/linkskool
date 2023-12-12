package com.digitaldream.winskool.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.digitaldream.winskool.models.GradeModelCopy;
import com.digitaldream.winskool.R;

import java.util.List;

public class GradeSettingsAdapter extends RecyclerView.Adapter<GradeSettingsAdapter.GradeSettingsViewHolder>{

    private Context context;
    public static List<GradeModelCopy> gradeList;
    OnClearBtnClickListener onClearBtnClickListener;

    public GradeSettingsAdapter(Context context, List<GradeModelCopy> gradeList,OnClearBtnClickListener onClearBtnClickListener) {
        this.context = context;
        this.gradeList = gradeList;
        this.onClearBtnClickListener = onClearBtnClickListener;
    }

    @NonNull
    @Override
    public GradeSettingsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grade_settings_item,viewGroup,false);
        return new GradeSettingsViewHolder(view,onClearBtnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final GradeSettingsViewHolder gradeSettingsViewHolder, int i) {
        GradeModelCopy gm = gradeList.get(i);
        gradeSettingsViewHolder.grade.setText(gm.getGradeName().toUpperCase());
        gradeSettingsViewHolder.gradeRemark.setText(gm.getGradeRemark().toUpperCase());
        gradeSettingsViewHolder.gradeMinimum.setText(gm.getGradeMinimuim().toUpperCase());

    }

    @Override
    public int getItemCount() {
        return gradeList.size();
    }

    class GradeSettingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView clear;
        EditText grade,gradeMinimum,gradeRemark;
        OnClearBtnClickListener onClearBtnClickListener;
        CardView cardView;

        public GradeSettingsViewHolder(@NonNull View itemView,OnClearBtnClickListener onClearBtnClickListener) {
            super(itemView);
            grade = itemView.findViewById(R.id.grade);
            gradeMinimum = itemView.findViewById(R.id.min_mark);
            gradeRemark = itemView.findViewById(R.id.remark);
            cardView = itemView.findViewById(R.id.grade_box);
            clear = itemView.findViewById(R.id.clear_grade);
            this.onClearBtnClickListener = onClearBtnClickListener;
            clear.setOnClickListener(this);
            grade.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    gradeList.get(getAdapterPosition()).setGradeName(grade.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            gradeMinimum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    gradeList.get(getAdapterPosition()).setGradeMinimuim(gradeMinimum.getText().toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            gradeRemark.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    gradeList.get(getAdapterPosition()).setGradeRemark(gradeRemark.getText().toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        @Override
        public void onClick(View v) {
            onClearBtnClickListener.onClearBtnClick(getAdapterPosition());
        }
    }

    public interface OnClearBtnClickListener{
        void onClearBtnClick(int position);
    }
}
