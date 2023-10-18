package com.digitaldream.linkskool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.linkskool.activities.ReferSchool;
import com.digitaldream.linkskool.R;

public class ContactUsDialog extends Dialog {
    private Activity activity;
    private TextView welcomeHeader;
    private TextView callUs,emailUs;
    private LinearLayout continueBtn,referSchool;
    public ContactUsDialog(Activity context) {
        super(context);
        this.activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contact_us_dialog);
        welcomeHeader = findViewById(R.id.welxcome_header);
        callUs = findViewById(R.id.call_us);
        emailUs = findViewById(R.id.email_us);
        continueBtn = findViewById(R.id.continue_btn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String schoolName = sharedPreferences.getString("school_name", "");

        welcomeHeader.setText("WELCOME TO "+schoolName.toUpperCase()+" LEARNING MANAGEMENT PORTAL");

        callUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_DIAL,
                        Uri.parse("tel:09064660137"));
                getContext().startActivity(i);
            }
        });

        emailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + "subject text" + "&body=" + "body text " + "&to=enquiries@linkskool.com");
                emailIntent.setData(data);
                getContext().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });

        referSchool = findViewById(R.id.refer_a_school);
        referSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ReferSchool.class);
                getContext().startActivity(intent);
            }
        });
    }
}
