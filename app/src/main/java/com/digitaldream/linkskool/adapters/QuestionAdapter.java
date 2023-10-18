package com.digitaldream.linkskool.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitaldream.linkskool.activities.TestUpload;
import com.digitaldream.linkskool.models.OptionsModel;
import com.digitaldream.linkskool.models.QuestionsModel;
import com.digitaldream.linkskool.R;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private Context context;
    private final List<QuestionsModel> questionList;
    OnQuestionClickListener onQuestionClickListener;

    public QuestionAdapter(Context context, List<QuestionsModel> questionList,OnQuestionClickListener onQuestionClickListener) {
        this.context = context;
        this.questionList = questionList;
        this.onQuestionClickListener = onQuestionClickListener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.multiple_choice_layout,viewGroup,false);
        return new QuestionViewHolder(view, onQuestionClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder questionViewholder, final int i) {

        questionViewholder.questionField.setText(i + 1 +". "+ questionList.get(i).getQuestion());
        if(questionList.get(i).getOptions().size()>0){
            questionViewholder.addOptionBtn.setText("Show Options("+questionList.get(i).getOptions().size()+")");
            //questionList.get(i).getOptions().clear();
        }else{
            questionViewholder.addOptionBtn.setText("Add Options");

        }
        if(!questionList.get(i).getQuestion().isEmpty()){
            questionViewholder.titleError.setVisibility(View.INVISIBLE);
        }else{
            questionViewholder.titleError.setVisibility(View.VISIBLE);

        }

        questionViewholder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionList.remove(i);
                TestUpload.questionAdapter.notifyDataSetChanged();
            }
        });

        questionViewholder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionsModel qm = new QuestionsModel();
                qm.setQuestion(questionList.get(i).getQuestion());
                qm.setOptions(questionList.get(i).getOptions());
                questionList.add(i+1,qm);
                TestUpload.questionAdapter.notifyDataSetChanged();
            }
        });

    }

    public void updateQuestion(int position, String question, List<OptionsModel> optionList){
        questionList.get(position).setQuestion(question);
        questionList.get(position).setOptions(optionList);
        this.notifyDataSetChanged();
    }

    public List<OptionsModel> getOptionsList(int position){
      return questionList.get(position).getOptions();
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView questionField;
        TextView titleError,addOptionBtn;
        ImageView copy,delete;
        OnQuestionClickListener onQuestionClickListener;

        public QuestionViewHolder(@NonNull View itemView, OnQuestionClickListener onQuestionClickListener) {
            super(itemView);
            questionField = itemView.findViewById(R.id.question_field);
            titleError = itemView.findViewById(R.id.required_title);
            addOptionBtn = itemView.findViewById(R.id.addOptionsBtn);
            copy = itemView.findViewById(R.id.copy);
            delete = itemView.findViewById(R.id.delete);
            this.onQuestionClickListener = onQuestionClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onQuestionClickListener.onQuestionClick(getAdapterPosition());
        }
    }

    public interface OnQuestionClickListener{
        void onQuestionClick(int position);
    }
}
