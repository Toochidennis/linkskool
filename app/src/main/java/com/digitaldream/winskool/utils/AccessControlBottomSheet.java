package com.digitaldream.winskool.utils;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.digitaldream.winskool.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AccessControlBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         View view = inflater.inflate(R.layout.activity_access,container,false);
        ImageView closeBtn = view.findViewById(R.id.close);
        closeBtn.setOnClickListener(v -> dismiss());

        RelativeLayout publicCont = view.findViewById(R.id.public_access);
        RelativeLayout anonymousCont = view.findViewById(R.id.anonymous);
        RelativeLayout limitedCont = view.findViewById(R.id.limited);
        ImageView publicImage = view.findViewById(R.id.img1);
        ImageView limitedImage = view.findViewById(R.id.img3);
        ImageView anonymousImage = view.findViewById(R.id.img2);

        if(QuestionBottomSheet.value.equalsIgnoreCase("public")){
            publicImage.setVisibility(View.VISIBLE);
            anonymousImage.setVisibility(View.GONE);
            limitedImage.setVisibility(View.GONE);
        }else if(QuestionBottomSheet.value.equalsIgnoreCase("anonymous")){
            anonymousImage.setVisibility(View.VISIBLE);
            publicImage.setVisibility(View.GONE);
            limitedImage.setVisibility(View.GONE);
        }else if(QuestionBottomSheet.value.equalsIgnoreCase("limited")){
            limitedImage.setVisibility(View.VISIBLE);
            publicImage.setVisibility(View.GONE);
            anonymousImage.setVisibility(View.GONE);
        }
        publicCont.setOnClickListener(v -> {
            QuestionBottomSheet.accessText.setText("Public");
            QuestionBottomSheet.accessImg.setImageResource(R.drawable.ic_supervisor_account);
            dismiss();
        });
        anonymousCont.setOnClickListener(v -> {
            QuestionBottomSheet.accessText.setText("Anonymous");
            QuestionBottomSheet.accessImg.setImageResource(R.drawable.ic_perm_identity);

            dismiss();
        });
        limitedCont.setOnClickListener(v -> {
            QuestionBottomSheet.accessText.setText("Limited");
            QuestionBottomSheet.accessImg.setImageResource(R.drawable.ic_perm_identity);
            dismiss();
        });
         return view;
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
