package com.digitaldream.linkskool.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.GeneralSettingModel;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ViewCompositeResultWebView extends AppCompatActivity {
    private Toolbar toolbar;
    private WebView mWebview;
    private DatabaseHelper databaseHelper;
    private Dao<GeneralSettingModel, Long> generalSettingsDao;
    private List<GeneralSettingModel> generalSettingModelList;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class_result_webview);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Composite result");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);

        databaseHelper = new DatabaseHelper(this);

        try {
            generalSettingsDao = DaoManager.createDao(databaseHelper.getConnectionSource(), GeneralSettingModel.class);
            generalSettingModelList = generalSettingsDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mWebview = findViewById(R.id.webview_class_result);
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        Intent i = getIntent();
        String year = i.getStringExtra("year");
        String classId = i.getStringExtra("class_id");
        String term = i.getStringExtra("term");


        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String db = sharedPreferences.getString("db", "");
        mWebview.loadUrl(getString(R.string.base_url) + "/composite3.php?class=" + classId + "&year=" + year +
                "&term=" + term + "&_db=" + db);

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
        return false;
    }
}
