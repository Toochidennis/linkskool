package com.digitaldream.linkskool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.digitaldream.linkskool.activities.TestUpload;
import com.digitaldream.linkskool.models.TestSettingModel;
import com.digitaldream.linkskool.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class FlashCardDialog extends Dialog {
    Activity activity;
    private List<String> weekList;
    Spinner week;
    private String weekValue;
    private EditText title;


    public FlashCardDialog(@NonNull Activity context) {
        super(context);
        this.activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.flashcard_layout);
        week = findViewById(R.id.weeks);
        title = findViewById(R.id.flashcard_title);
        title.requestFocus();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        },500);
            weekList = new ArrayList<>();
        for(int i=1;i<30;i++){
            weekList.add("Week "+i);
        }

        ArrayAdapter adapterTerm = new ArrayAdapter(getContext(), R.layout.spinner_item, weekList);
        adapterTerm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        week.setAdapter(adapterTerm);
        week.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekValue = String.valueOf(position +1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(!TestUpload.testHeaderList.isEmpty()) {

            title.setText(TestUpload.testHeaderList.get(0).getTitle());
            week.setSelection(getIndex(week, "Week " + TestUpload.testHeaderList.get(0).getWeek()));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        TestUpload.recyclerView.setVisibility(View.VISIBLE);
        TestUpload.testHeaderList.clear();
        String titleText = title.getText().toString();
        TestSettingModel tsm = new TestSettingModel();
        tsm.setWeek(weekValue);
        tsm.setTitle(titleText);
        tsm.setType("flashcard");
        TestUpload.testHeaderList.add(tsm);
        TestUpload.adapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TestUpload.recyclerView.setVisibility(View.VISIBLE);
        TestUpload.testHeaderList.clear();
        String titleText = title.getText().toString();
        TestSettingModel tsm = new TestSettingModel();
        tsm.setWeek(weekValue);
        tsm.setTitle(titleText);
        tsm.setType("flashcard");
        TestUpload.testHeaderList.add(tsm);
        TestUpload.adapter.notifyDataSetChanged();
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

}
