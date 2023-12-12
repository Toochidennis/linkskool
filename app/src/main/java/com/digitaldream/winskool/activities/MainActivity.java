package com.digitaldream.winskool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.digitaldream.winskool.R;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.config.ForceUpdateAsync;

public class MainActivity extends AppCompatActivity {
    //private final String url = "http://www.linkskool.com/newportal/api/mobile.php";
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        databaseHelper = new DatabaseHelper(this);

        //forceUpdate();
        int SPLASH_DISPLAY_LENGTH = 3000;
        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",
                    Context.MODE_PRIVATE);
            boolean isLogged = sharedPreferences.getBoolean("loginStatus", false);
            String who = sharedPreferences.getString("who", "");
            if (databaseHelper.getWritableDatabase() == null) {
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            } else if (isLogged && who.equals("admin")) {
                startActivity(new Intent(MainActivity.this, Dashboard.class));
                finish();
            } else if (isLogged && who.equals("staff")) {
                startActivity(new Intent(MainActivity.this, StaffDashboardActivity.class));
                finish();
            } else if (isLogged && who.equals("student")) {
                startActivity(new Intent(MainActivity.this, StudentDashboardActivity.class));
                finish();
            } else {
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

        ImageView linkskool = findViewById(R.id.logo);
        long startTime = 100;

        inFromBottomAnimation(linkskool, startTime);

    }


    public void forceUpdate() {
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        new ForceUpdateAsync(currentVersion, MainActivity.this).execute();
    }

    private Animation inFromLeftAnimation(int duration, Long startTime) {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(duration);
        inFromLeft.setStartOffset(startTime);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    private void inFromBottomAnimation(ImageView sImageView, Long startTime) {
        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromBottom.setDuration(1500);
        inFromBottom.setStartOffset(startTime);
        inFromBottom.setInterpolator(new AccelerateInterpolator());

        sImageView.startAnimation(inFromBottom);

        inFromBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                final Animation in = new AlphaAnimation(0.0f, 1.0f);
                in.setDuration(1000);
               /* TextView linkskoolTxt = findViewById(R.id.linkskool_text);
                linkskoolTxt.setVisibility(View.VISIBLE);
                linkskoolTxt.startAnimation(in);*/
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}