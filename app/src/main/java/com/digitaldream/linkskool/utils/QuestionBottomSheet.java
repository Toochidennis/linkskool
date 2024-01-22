package com.digitaldream.linkskool.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digitaldream.linkskool.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

public class QuestionBottomSheet extends BottomSheetDialogFragment {
    public static TextView accessText;
    public static ImageView accessImg;
    public static String value;
    public static String viewAccess,question,user_name,user_id;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.question_bottom_sheet,container,false);
        ImageView closeBtn = view.findViewById(R.id.close);
        LinearLayout accessBtn = view.findViewById(R.id.access_cont);
        accessText = view.findViewById(R.id.access_text);
        accessImg = view.findViewById(R.id.access_img);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        user_name = sharedPreferences.getString("user","");
        user_id = sharedPreferences.getString("user_id","");

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView userText = view.findViewById(R.id.user);
        if(user_name.equals("null")||user_name.isEmpty()){
            userText.setText("Admin");
        }else{
            userText.setText(user_name);
        }
        accessBtn.setOnClickListener(v -> {
            value = accessText.getText().toString();
            FragmentTransaction transaction = ((FragmentActivity) getContext())
                    .getSupportFragmentManager()
                    .beginTransaction();
            AccessControlBottomSheet accessControlBottomSheet = new AccessControlBottomSheet();
            accessControlBottomSheet.show(transaction, "accessBottomSheet");

        });

        EditText questionEDT = view.findViewById(R.id.question_text);

        Button addQuestion = view.findViewById(R.id.add);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAccess = accessText.getText().toString();
                question = questionEDT.getText().toString();
                if(question.isEmpty()){
                    Toast.makeText(getContext(),"Question must not be empty",Toast.LENGTH_SHORT).show();
                }else {
                    FragmentTransaction transaction = ((FragmentActivity) requireContext())
                            .getSupportFragmentManager()
                            .beginTransaction();
                    QuestionAccessViewSheet a = new QuestionAccessViewSheet();
                    a.show(transaction, "questionAccessViewSheet");
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }
}
