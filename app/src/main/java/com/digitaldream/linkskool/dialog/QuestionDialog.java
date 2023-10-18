package com.digitaldream.linkskool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.digitaldream.linkskool.activities.TestUpload;
import com.digitaldream.linkskool.adapters.OptionsAdapter;
import com.digitaldream.linkskool.models.OptionsModel;
import com.digitaldream.linkskool.R;

import java.util.ArrayList;
import java.util.List;

public class QuestionDialog extends Dialog {
    private Activity activity;
    RecyclerView optionsRecycler;
    Button addOptionBtn,apply;
    EditText question;
    public static OptionsAdapter adapter;
    private List<OptionsModel> optionsList;
    private int position;
    private List<OptionsModel> opList;
    private TextView questionNumber;

    public QuestionDialog( Activity context,int position,List<OptionsModel> opList) {
        super(context);
        this.activity = context;
        this.position = position;
        this.opList = opList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.question_dialog_layout);

        addOptionBtn = findViewById(R.id.add_option);
        optionsRecycler = findViewById(R.id.options_recycler);
        apply = findViewById(R.id.apply);
        questionNumber = findViewById(R.id.question_number);
        questionNumber.setText(String.valueOf(position+1));
        optionsList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        optionsRecycler.setLayoutManager(linearLayoutManager);
        optionsRecycler.setHasFixedSize(true);
        optionsList = TestUpload.questionAdapter.getOptionsList(position);

        adapter = new OptionsAdapter(getContext(),optionsList);

        question = findViewById(R.id.question_field);
        question.requestFocus();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        },1000);

        optionsRecycler.setAdapter(adapter);
        addOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsModel md = new OptionsModel();
                md.setOptionText("");
                md.setChecked(false);
                optionsList.add(md);
                adapter.notifyDataSetChanged();
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String questionText = question.getText().toString();

                TestUpload.questionAdapter.updateQuestion(position,questionText,optionsList);

                dismiss();

            }
        });

            question.setText(TestUpload.questionsList.get(position).getQuestion());


        if(!opList.isEmpty()) {
            adapter.notifyDataSetChanged();
            adapter = new OptionsAdapter(getContext(),optionsList);
            optionsRecycler.setAdapter(adapter);
            Log.i("response", String.valueOf(optionsList.size()));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String questionText = question.getText().toString();
        //optionsList = oplist();
        TestUpload.questionAdapter.updateQuestion(position,questionText,optionsList);
    }


    @Override
    protected void onStop() {
        super.onStop();
        String questionText = question.getText().toString();
        //optionsList = oplist();
        TestUpload.questionAdapter.updateQuestion(position,questionText,optionsList);
    }

}
