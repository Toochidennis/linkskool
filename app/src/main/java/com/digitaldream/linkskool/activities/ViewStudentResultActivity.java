package com.digitaldream.linkskool.activities;

import static com.digitaldream.linkskool.utils.FunctionUtils.webViewProgress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.digitaldream.linkskool.R;

public class ViewStudentResultActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Result");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        String studentId = getIntent().getStringExtra("studentId");
        String level = getIntent().getStringExtra("level");
        String term = getIntent().getStringExtra("term");
        String year = getIntent().getStringExtra("year");
        String classId = getIntent().getStringExtra("classId");

        String termValue = "";
        switch (term) {
            case "First Term":
                termValue = "1";
                break;
            case "Second Term":
                termValue = "2";
                break;
            case "Third Term":
                termValue = "3";
                break;
            default:
                break;

        }

        WebView webView = findViewById(R.id.webview_view_result);

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        String db = sharedPreferences.getString("db", "");
        String staffId = sharedPreferences.getString("user_id", "");
        Log.i("student_class", "" + classId + " " + staffId + " " + studentId);

        String url = getString(
                R.string.base_url) + "/result.php?id=" + studentId + "&class=" + classId +
                "&term=" + termValue + "&year=" + year + "&staffId=" + staffId + "&_db=" + db;

        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webViewProgress(this, webView);

        if (webView.canGoBack()) {
            webView.goBack();
        }

    }

}
