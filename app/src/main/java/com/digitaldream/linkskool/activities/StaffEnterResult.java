package com.digitaldream.linkskool.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.digitaldream.linkskool.R;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class StaffEnterResult extends AppCompatActivity {
    private WebView mWebview;
    private Toolbar toolbar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_result);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setTitle("Result");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String staffId = sharedPreferences.getString("user_id", "");
        String db = sharedPreferences.getString("db", "");

        Intent i = getIntent();
        String status = i.getStringExtra("from");
        String courseId = i.getStringExtra("course_id");
        String classId = i.getStringExtra("class_id");


        mWebview = findViewById(R.id.webview_staff_result);

        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        mWebview.setWebViewClient(new WebViewClient());

        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    dialog1.dismiss();
                    mWebview.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        if (mWebview.canGoBack()) {
            mWebview.goBack();
        }

        assert status != null;
        String viewUrl = getString(R.string.base_url) + "/viewResult.php?class=" + classId +
                "&course=" + courseId + "&_db=" + db + "&staff_id=" + staffId;
        String addUrl = getString(R.string.base_url) + "/addResults.php?class=" + classId +
                "&course=" + courseId + "&_db=" + db + "&staff_id=" + staffId;

        if (status.equals("view_result")) {
            mWebview.loadUrl(viewUrl);
        } else if (status.equals("add_result")) {
            mWebview.loadUrl(addUrl);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
