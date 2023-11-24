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

import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.digitaldream.linkskool.R;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class SubjectResultUtil extends AppCompatActivity {
    private WebView mWebview;
    private Toolbar toolbar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subject_result);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("View Result");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        Intent i = getIntent();

        String courseId = i.getStringExtra("course_id");
        String classId = i.getStringExtra("class_id");
        String from = i.getStringExtra("from");
        String term = i.getStringExtra("term");
        String year = i.getStringExtra("year");


        mWebview = findViewById(R.id.webview_view_result_subject);
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        String db = sharedPreferences.getString("db", "");
        if (from.equals("view")) {
            mWebview.loadUrl(
                    Login.urlBase + "/adminViewResult.php?class=" + classId + "&course=" + courseId
                            + "&_db=" + db + "&year=" + year + "&term=" + term);
        } else if (from.equals("edit")) {
            mWebview.loadUrl(
                    Login.urlBase + "/adminAddResult.php?class=" + classId + "&course=" + courseId
                            + "&_db=" + db + "&year=" + year + "&term=" + term);
        }


        mWebview.getSettings().setJavaScriptEnabled(true);
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
