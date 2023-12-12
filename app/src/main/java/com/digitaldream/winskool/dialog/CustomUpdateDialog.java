package com.digitaldream.winskool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.digitaldream.winskool.R;

public class CustomUpdateDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button updateBtn, cancelBtn;

    public CustomUpdateDialog( Activity c) {
        super(c);
        this.c = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom_update_dialog);
        updateBtn =  findViewById(R.id.update_app);
        cancelBtn =  findViewById(R.id.cancel_update);
        updateBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_app:
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getContext().getPackageName())));
                break;
            case R.id.cancel_update:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

}
