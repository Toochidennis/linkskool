package com.digitaldream.winskool.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.digitaldream.winskool.models.AssessmentModelCopy;
import com.digitaldream.winskool.R;

import java.util.List;

public class AssessmentSettingsAdapter extends RecyclerView.Adapter<AssessmentSettingsAdapter.AssessmentSettingViewHolder> {
    private Context context;
    public static List<AssessmentModelCopy> assessmentModelCopyList;
    OnClearBtnClickListener onClearBtnClickListener;

    public AssessmentSettingsAdapter(Context context, List<AssessmentModelCopy> assessmentModelCopyList,OnClearBtnClickListener onClearBtnClickListener) {
        this.context = context;
        this.assessmentModelCopyList = assessmentModelCopyList;
        this.onClearBtnClickListener = onClearBtnClickListener;
    }

    @NonNull
    @Override
    public AssessmentSettingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.assessment_setting_item,viewGroup,false);
        return new AssessmentSettingViewHolder(view,onClearBtnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentSettingViewHolder assessmentSettingViewHolder, int i) {

        assessmentSettingViewHolder.assessmentName.setText(assessmentModelCopyList.get(i).getAssessmentName().toUpperCase());
        assessmentSettingViewHolder.maximiumScore.setText(assessmentModelCopyList.get(i).getMaxScore().toUpperCase());

    }

    @Override
    public int getItemCount() {
        return assessmentModelCopyList.size();
    }

    class AssessmentSettingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        EditText assessmentName,maximiumScore;
        ImageView clearBtn;
        OnClearBtnClickListener onClearBtnClickListener;

        public AssessmentSettingViewHolder(@NonNull View itemView,OnClearBtnClickListener onClearBtnClickListener) {
            super(itemView);
            assessmentName = itemView.findViewById(R.id.assessment_name);
            maximiumScore = itemView.findViewById(R.id.assessment_max);
            clearBtn = itemView.findViewById(R.id.clear_assessment);
            this.onClearBtnClickListener = onClearBtnClickListener;
            clearBtn.setOnClickListener(this);

            assessmentName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    assessmentModelCopyList.get(getAdapterPosition()).setAssessmentName(assessmentName.getText().toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            maximiumScore.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    assessmentModelCopyList.get(getAdapterPosition()).setMaxScore(maximiumScore.getText().toString());

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
