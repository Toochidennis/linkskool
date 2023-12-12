package com.digitaldream.winskool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.digitaldream.winskool.activities.TestUpload;
import com.digitaldream.winskool.adapters.QuestionAdapter2;
import com.digitaldream.winskool.models.QuestionsModel;
import com.digitaldream.winskool.R;

public class QuestionTypeDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private LinearLayout shortAnswer,longAnswer,emailAnswer,numberAnswer,multipleAnswer,checkboxAnswer,dropdownAnswer
            ,dateAnswer,timeAnswer,sectionAnswer;

    public QuestionTypeDialog(@NonNull Activity context) {
        super(context);
        this.activity=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.question_type_dialog);

        shortAnswer = findViewById(R.id.short_answer);
        longAnswer = findViewById(R.id.long_answer);
        emailAnswer = findViewById(R.id.email_answer);
        numberAnswer = findViewById(R.id.number_answer);
        multipleAnswer = findViewById(R.id.multiple_choice);
        checkboxAnswer = findViewById(R.id.checkbox_answer);
        dropdownAnswer = findViewById(R.id.dropdown_answer);
        dateAnswer = findViewById(R.id.date_answer);
        timeAnswer = findViewById(R.id.time_answer);
        sectionAnswer = findViewById(R.id.section_answer);
        shortAnswer.setOnClickListener(this);
        longAnswer.setOnClickListener(this);
        emailAnswer.setOnClickListener(this);
        numberAnswer.setOnClickListener(this);
        multipleAnswer.setOnClickListener(this);
        checkboxAnswer.setOnClickListener(this);
        dropdownAnswer.setOnClickListener(this);
        dateAnswer.setOnClickListener(this);
        timeAnswer.setOnClickListener(this);
        sectionAnswer.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        QuestionsModel qm = new QuestionsModel();
        switch (v.getId()){
            case R.id.short_answer:
                qm.setQuestionType("short_answer");
                qm.setQuestion("");
                qm.setQuestionId("");
                qm.setQuestionImage("");
                TestUpload.questionsList.add(qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                this.dismiss();
                break;
            case R.id.long_answer:
                qm.setQuestionType("long_answer");
                qm.setQuestion("");
                qm.setQuestionId("");
                qm.setQuestionImage("");
                TestUpload.questionsList.add(qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                this.dismiss();
                break;
            case R.id.email_answer:
                qm.setQuestionType("email");
                qm.setQuestion("");
                qm.setQuestionId("");
                TestUpload.questionsList.add(qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                this.dismiss();
                break;
            case R.id.number_answer:
                qm.setQuestionType("number");
                qm.setQuestion("");
                qm.setQuestionId("");
                TestUpload.questionsList.add(qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                this.dismiss();
                break;
            case R.id.multiple_choice:
                qm.setQuestionType("multiple_choice");
                qm.setQuestion("");
                qm.setQuestionId("");
                TestUpload.questionsList.add(qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                this.dismiss();
                break;
            case R.id.checkbox_answer:
                qm.setQuestionType("check_box");
                qm.setQuestion("");
                qm.setQuestionId("");
                TestUpload.questionsList.add(qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                this.dismiss();
                break;
            case R.id.dropdown_answer:
                qm.setQuestionType("drop_down");
                qm.setQuestion("");
                qm.setQuestionId("");
                TestUpload.questionsList.add(qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                this.dismiss();
                break;
            case R.id.date_answer:
                qm.setQuestionType("date");
                qm.setQuestion("");
                qm.setQuestionId("");
                TestUpload.questionsList.add(qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                this.dismiss();
                break;
            case R.id.time_answer:
                qm.setQuestionType("time");
                qm.setQuestion("");
                qm.setQuestionId("");
                TestUpload.questionsList.add(qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                this.dismiss();
                break;
            case R.id.section_answer:
                qm.setQuestionType("section");
                qm.setQuestion("");
                qm.setQuestionId("");
                qm.setSectionTitle("");
                qm.setSectionDescription("");
                TestUpload.questionsList.add(qm);
                TestUpload.questionAdapter2.notifyDataSetChanged();
                this.dismiss();
                break;
        }

        TestUpload.questionRecycler.smoothScrollToPosition(TestUpload.questionAdapter2.getItemCount()-1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*QuestionDialog2 dialog2 = new QuestionDialog2(activity,qm,TestUpload.questionAdapter2.getItemCount()-1, QuestionAdapter2.questionNumber);
                dialog2.setCanceledOnTouchOutside(false);
                dialog2.show();
                Window window = dialog2.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);*/

                FragmentActivity activityF = (FragmentActivity) activity;
                FragmentManager manager = activityF.getSupportFragmentManager();
                QuestionDialog2 dialog2 = new QuestionDialog2(activity,qm,
                        TestUpload.questionAdapter2.getItemCount()-1, QuestionAdapter2.questionNumber);
                dialog2.show(manager, "tag");



            }
        },500);
    }
}
