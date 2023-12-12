package com.digitaldream.winskool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.winskool.R;

public class ContactUs extends AppCompatActivity {
    TextView welcomeHeader;
    private Button callUs,emailUs;
    private Toolbar toolbar;
    private LinearLayout continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        welcomeHeader = findViewById(R.id.welxcome_header);
        callUs = findViewById(R.id.call_us);
        emailUs = findViewById(R.id.email_us);
        toolbar = findViewById(R.id.toolbar);
        continueBtn = findViewById(R.id.continue_btn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("About LinkSkool");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String schoolName = sharedPreferences.getString("school_name", "");

        welcomeHeader.setText("WELCOME TO "+schoolName.toUpperCase()+" LEARNING MANAGEMENT PORTAL");

        callUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_DIAL,
                        Uri.parse("tel:08147067514"));
                startActivity(i);
            }
        });

        emailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + "subject text" + "&body=" + "body text " + "&to=dikekingsleyoneh@yahoo.com");
                emailIntent.setData(data);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return false;
    }
}
