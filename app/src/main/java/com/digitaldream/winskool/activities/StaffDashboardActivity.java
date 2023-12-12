package com.digitaldream.winskool.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.digitaldream.winskool.R;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.config.ForceUpdateAsync;
import com.digitaldream.winskool.dialog.ContactUsDialog;
import com.digitaldream.winskool.fragments.ELibraryFragment;
import com.digitaldream.winskool.fragments.FlashCardList;
import com.digitaldream.winskool.fragments.StaffResultDashboardFragment;
import com.digitaldream.winskool.fragments.StaffDashboardFragment;
import com.digitaldream.winskool.fragments.StaffELearningFragment;
import com.digitaldream.winskool.models.ClassNameTable;
import com.digitaldream.winskool.models.CourseOutlineTable;
import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.models.ExamType;
import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.models.NewsTable;
import com.digitaldream.winskool.models.StudentTable;
import com.digitaldream.winskool.models.TeachersTable;
import com.digitaldream.winskool.models.VideoTable;
import com.digitaldream.winskool.models.VideoUtilTable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class StaffDashboardActivity extends AppCompatActivity {
    private Boolean exit = false;
    private DatabaseHelper databaseHelper;
    private boolean fromLogin;
    private ContactUsDialog dialog;
    private boolean isFirstTime = false;
    private BottomNavigationView bottomNavigationView;
    public static String check = null;
    public static boolean refresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);
        check = "";
        Intent i = getIntent();
        String from = i.getStringExtra("from");
        databaseHelper = new DatabaseHelper(this);
        fromLogin = getIntent().getBooleanExtra("isFromLogin", false);

        if (fromLogin == true) {
            isFirstTime = true;
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation_student);

        if (from != null && from.equals("testupload")) {
            FlashCardList.refresh = true;
            bottomNavigationView.getMenu().findItem(R.id.payment).setChecked(true);
            getSupportFragmentManager().beginTransaction().replace(R.id.payment_container, new FlashCardList()).commit();

        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.payment_container, new StaffDashboardFragment()).commit();
            bottomNavigationView.getMenu().findItem(R.id.teacher_dashboard).setChecked(true);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.teacher_dashboard:
                    getSupportFragmentManager().beginTransaction().replace(R.id.payment_container, new StaffDashboardFragment()).commit();
                    return true;
                case R.id.teacher_result:
                    getSupportFragmentManager().beginTransaction().replace(R.id.payment_container, new StaffResultDashboardFragment()).commit();
                    return true;

                case R.id.teacher_library:
                    getSupportFragmentManager().beginTransaction().replace(R.id.payment_container
                            , new ELibraryFragment()).commit();
                    return true;

                case R.id.teacher_e_learning:
                    getSupportFragmentManager().beginTransaction().replace(R.id.payment_container, new StaffELearningFragment()).commit();
                    return true;
            }
            return false;
        });

    }

    @Override
    public void onBackPressed() {
        //BottomNavigationView bottomNavigationView = findViewById(R.id.student_dashboard);
        int seletedItemId = bottomNavigationView.getSelectedItemId();
        if (R.id.teacher_dashboard != seletedItemId) {
            setHomeItem(StaffDashboardActivity.this);
        } else {
            super.onBackPressed();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.staff_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Continue to logout?");
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                });

                builder.setPositiveButton("Logout", (dialog, which) -> logout());
                builder.show();
                break;
            case R.id.info:
                ContactUsDialog dialog = new ContactUsDialog(this);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                break;
        }
        return false;
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginStatus", false);
        editor.putString("user", "");
        editor.putString("school_name", "");
        editor.apply();

        try {
            TableUtils.clearTable(databaseHelper.getConnectionSource(), StudentTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), TeachersTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), ClassNameTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), LevelTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), NewsTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), CourseTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), CourseOutlineTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), VideoTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), VideoUtilTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), ExamType.class);
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
            dialog = new ContactUsDialog(this);
            dialog.show();
            Window window = dialog.getWindow();
            assert window != null;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            isFirstTime = false;
        } else {
            forceUpdate();
        }
        /*int seletedItemId = bottomNavigationView.getSelectedItemId();
        switch (seletedItemId){
            case R.id.student_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new StaffDashboardFragment()).commit();
                break;
            case R.id.student_results:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ResultStaff()).commit();
                break;

            case R.id.flashcard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FlashCardList()).commit();
               break;
            case R.id.student_cbt:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ExamFragment()).commit();
                break;

            case R.id.student_elearning:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new StaffELearningFragment()).commit();
               break;

        }*/
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
        new ForceUpdateAsync(currentVersion, StaffDashboardActivity.this).execute();
    }

    public static void setHomeItem(Activity activity) {
        BottomNavigationView bottomNavigationView =
                activity.findViewById(R.id.bottom_navigation_student);
        bottomNavigationView.setSelectedItemId(R.id.teacher_dashboard);
    }
}
