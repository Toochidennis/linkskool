package com.digitaldream.linkskool.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.adapters.NewsAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.config.ForceUpdateAsync;
import com.digitaldream.linkskool.dialog.ContactUsDialog;
import com.digitaldream.linkskool.fragments.AdminDashboardFragment;
import com.digitaldream.linkskool.fragments.AdminELearningNavigationFragment;
import com.digitaldream.linkskool.fragments.AdminPaymentDashboardFragment;
import com.digitaldream.linkskool.fragments.AdminResultNavigationGraphFragment;
import com.digitaldream.linkskool.fragments.ELibraryFragment;
import com.digitaldream.linkskool.fragments.FlashCardList;
import com.digitaldream.linkskool.models.GeneralSettingModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;

public class Dashboard extends AppCompatActivity implements NewsAdapter.OnNewsClickListener {
    //private DrawerLayout drawerLayout;

    private DatabaseHelper databaseHelper;
    private Dao<GeneralSettingModel, Long> generalSettingDao;
    private List<GeneralSettingModel> generalSettingsList;
    public static String db;
    private String user_name, school_name, userId;
    private ContactUsDialog dialog;
    private boolean fromLogin = false;
    private boolean isFirstTime = false;
    private BottomNavigationView bottomNavigationView;
    public static String check = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        check = "";

        Intent i = getIntent();
        String from = i.getStringExtra("from");
        databaseHelper = new DatabaseHelper(this);

        try {
            generalSettingDao = DaoManager.createDao(
                    databaseHelper.getConnectionSource(),
                    GeneralSettingModel.class);
            generalSettingsList = generalSettingDao.queryForAll();


        } catch (SQLException e) {
            e.printStackTrace();
        }


        SharedPreferences sharedPreferences = getSharedPreferences(
                "loginDetail", Context.MODE_PRIVATE);
        try {
            school_name = generalSettingsList.get(
                    0).getSchoolName().toLowerCase();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        userId = sharedPreferences.getString("user_id", "");
        user_name = sharedPreferences.getString("user", "User ID: " + userId);
        db = sharedPreferences.getString("db", "");

        bottomNavigationView = findViewById(R.id.bottom_navigation_student);
        if (from != null && from.equals("testupload")) {
            FlashCardList.refresh = true;
            bottomNavigationView.getMenu().findItem(R.id.payment).setChecked(
                    true);
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.payment_container, new FlashCardList()).commit();

        } else {
            bottomNavigationView.getMenu().findItem(
                    R.id.student_dashboard).setChecked(true);
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.payment_container,
                    new AdminDashboardFragment()).commit();
        }

       /* navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.dashboard:
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.student_contacts:
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(Dashboard.this,
                            StudentContacts.class);
                    startActivity(intent);
                    return true;
                case R.id.view_result:
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();

                    Intent intent2 = new Intent(Dashboard.this,
                            ViewResult.class);
                    startActivity(intent2);

                    return true;

                case R.id.teachers_contacts:
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    Intent intent3 = new Intent(Dashboard.this,
                            TeacherContacts.class);
                    startActivity(intent3);
                    return true;

                case R.id.logout:
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    logout();
                    return true;
                default:
                    drawerLayout.closeDrawers();
                    return true;
            }

        });

        navigationView.getMenu().getItem(0).setChecked(true);*/

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.student_dashboard:
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.payment_container,
                            new AdminDashboardFragment()).commit();
                    return true;

                case R.id.student_results:
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.payment_container,
                            new AdminResultNavigationGraphFragment()
                    ).commit();
                    return true;

                case R.id.payment:
                    getSupportFragmentManager().beginTransaction()
                            .replace(
                                    R.id.payment_container,
                                    new AdminPaymentDashboardFragment()
                            ).commit();

                    return true;
                case R.id.student_library:
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.payment_container,
                            new ELibraryFragment()).commit();
                    return true;

                case R.id.student_elearning:
                    getSupportFragmentManager().beginTransaction().replace(R.id.payment_container,
                            new AdminELearningNavigationFragment()).commit();

                    return true;
            }
            return false;
        });
    }


    @Override
    public void onNewsClick(int position) {

        Intent intent = new Intent(this, NewsPage.class);
        //s intent.putExtra("newsId", newsTitleList.get(position).getNewsId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fromLogin && isFirstTime) {
            dialog = new ContactUsDialog(this);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            isFirstTime = false;
        } else {
            forceUpdate();

        }

    }

    @Override
    public void onBackPressed() {
        int selectedItemId = bottomNavigationView.getSelectedItemId();
        if (R.id.student_dashboard != selectedItemId) {
            setHomeItem(Dashboard.this);
        } else {
            super.onBackPressed();
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
        new ForceUpdateAsync(currentVersion, Dashboard.this).execute();
    }

    public void setHomeItem(Activity activity) {

        BottomNavigationView bottomNavigationView = activity.findViewById(
                R.id.bottom_navigation_student);
        bottomNavigationView.setSelectedItemId(R.id.student_dashboard);

    }

}
