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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.digitaldream.winskool.models.OptionsModel;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.dialog.QuestionDialog;

import java.util.ArrayList;
import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.OptionsViewHolder> {
    private Context context;
    private List<OptionsModel> optionsList;


    public OptionsAdapter(Context context, List<OptionsModel> optionsList) {
        this.context = context;
        this.optionsList = optionsList;
    }

    @NonNull
    @Override
    public OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.multichoice_option_layout, viewGroup, false);
        return new OptionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OptionsViewHolder optionsViewHolder, final int i) {
        final OptionsModel op = optionsList.get(i);

        optionsViewHolder.optionText.setText(op.getOptionText());
        optionsViewHolder.optionText.requestFocus();

        optionsViewHolder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsList.remove(i);
                QuestionDialog.adapter.notifyDataSetChanged();
            }
        });

        optionsViewHolder.radioButton.setChecked(op.isChecked());

        optionsViewHolder.radioButton.setOnClickListener(v -> {
            for (int a = 0; a < optionsList.size(); a++) {
                optionsList.get(a).setChecked(false);
            }
            optionsList.get(i).setChecked(true);

            //QuestionDialog.adapter.notifyDataSetChanged();
            //TestUpload.questionAdapter.notifyDataSetChanged();
            op.setCorrectAnswer(optionsList.get(i).getOptionText());
            //Toast.makeText(context,optionsList.get(i).getOptionText(),Toast.LENGTH_SHORT).show();

        });

    }


    public List<OptionsModel> getOptionsList() {
        List<OptionsModel> opList = new ArrayList<>();
        for (int a = 0; a < optionsList.size(); a++) {
            OptionsModel md = new OptionsModel();
            md.setOptionText(optionsList.get(a).getOptionText());

            opList.add(md);
        }
        return opList;
    }


    @Override
    public int getItemCount() {
        return optionsList.size();
    }

    class OptionsViewHolder extends RecyclerView.ViewHolder {
        public EditText optionText;
        ImageView clear;
        RadioGroup radioGroup;
        RadioButton radioButton;

        public OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            optionText = itemView.findViewById(R.id.option_field);
            clear = itemView.findViewById(R.id.clear);
            radioButton = itemView.findViewById(R.id.point);
            optionText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    optionsList.get(getAdapterPosition()).setOptionText(optionText.getText().toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }
    }

}
