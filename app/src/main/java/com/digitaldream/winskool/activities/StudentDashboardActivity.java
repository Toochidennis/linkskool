package com.digitaldream.winskool.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.config.ForceUpdateAsync;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.fragments.ELibraryFragment;
import com.digitaldream.winskool.fragments.FlashCardList;
import com.digitaldream.winskool.fragments.StudentDashboardFragment;
import com.digitaldream.winskool.fragments.StudentELearningFragment;
import com.digitaldream.winskool.fragments.StudentPaymentFragment;
import com.digitaldream.winskool.fragments.StudentResultFragment;
import com.digitaldream.winskool.models.CourseOutlineTable;
import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.models.ExamType;
import com.digitaldream.winskool.models.NewsTable;
import com.digitaldream.winskool.models.StudentCourses;
import com.digitaldream.winskool.models.StudentResultDownloadTable;
import com.digitaldream.winskool.models.StudentTable;
import com.digitaldream.winskool.models.VideoTable;
import com.digitaldream.winskool.models.VideoUtilTable;
import com.digitaldream.winskool.dialog.ContactUsDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
public class StudentDashboardActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    public static Boolean exit = false;
    private DatabaseHelper databaseHelper;
    private Dao<StudentTable, Long> studentDao;
    private Dao<NewsTable, Long> newsDao;
    private Dao<CourseTable, Long> courseDao;
    private boolean isFirstTime = false, fromLogin = false;
    public static String check = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        check = "";
        Intent i = getIntent();
        String from = i.getStringExtra("from");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);
        try {
            studentDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), StudentTable.class);
            newsDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    NewsTable.class);
            courseDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(), CourseTable.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation_student);

        if (from != null && from.equals("testupload")) {
            FlashCardList.refresh = true;
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.payment_container, new FlashCardList()).commit();
            bottomNavigationView.getMenu().findItem(R.id.payment).setChecked(
                    true);

        } else {
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.payment_container, new StudentDashboardFragment()).commit();
            bottomNavigationView.getMenu().findItem(
                    R.id.student_dashboard).setChecked(true);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(
                menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.student_dashboard:
                            getSupportFragmentManager().beginTransaction().replace(
                                    R.id.payment_container,
                                    new StudentDashboardFragment()).commit();
                            return true;
                        case R.id.student_results:
                            getSupportFragmentManager().beginTransaction().replace(
                                    R.id.payment_container,
                                    new StudentResultFragment()).commit();
                            return true;

                        case R.id.payment:
                            getSupportFragmentManager().beginTransaction().replace(
                                    R.id.payment_container,
                                    new StudentPaymentFragment()).commit();
                            return true;

                        case R.id.student_library:
                            getSupportFragmentManager().beginTransaction().replace(
                                    R.id.payment_container,
                                    new ELibraryFragment()).commit();
                            return true;

                        case R.id.student_elearning:
                            getSupportFragmentManager().beginTransaction().replace(
                                    R.id.payment_container,
                                    new StudentELearningFragment()).commit();
                            return true;
                    }
                    return false;
                });

        fromLogin = getIntent().getBooleanExtra("isFromLogin", false);

        if (fromLogin == true) {
            isFirstTime = true;
        }

    }


    @Override
    public void onBackPressed() {
        int selectedItemId = bottomNavigationView.getSelectedItemId();
        if (R.id.student_dashboard != selectedItemId) {
            setHomeItem(StudentDashboardActivity.this);
        } else {
            super.onBackPressed();
        }
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                "loginDetail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginStatus", false);
        editor.putString("user", "");
        editor.putString("school_name", "");
        editor.putString("attachment","");
        editor.apply();
        try {
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    StudentTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    NewsTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    StudentCourses.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    StudentResultDownloadTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    VideoTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    VideoUtilTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    CourseOutlineTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    ExamType.class);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (fromLogin && isFirstTime) {
            ContactUsDialog dialog = new ContactUsDialog(this);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            isFirstTime = false;
        } else {
            forceUpdate();
        }

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
        new ForceUpdateAsync(currentVersion,
                StudentDashboardActivity.this).execute();
    }

    public static void setHomeItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                activity.findViewById(R.id.bottom_navigation_student);
        bottomNavigationView.setSelectedItemId(R.id.student_dashboard);
    }
}
