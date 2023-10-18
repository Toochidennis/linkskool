package com.digitaldream.linkskool.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.adapters.NewsAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.config.ForceUpdateAsync;
import com.digitaldream.linkskool.dialog.AdminClassesDialog;
import com.digitaldream.linkskool.dialog.AdminELearningDialog;
import com.digitaldream.linkskool.dialog.ContactUsDialog;
import com.digitaldream.linkskool.fragments.AdminDashboardFragment;
import com.digitaldream.linkskool.fragments.AdminELearningClassRoomFragment;
import com.digitaldream.linkskool.fragments.AdminPaymentDashboardFragment;
import com.digitaldream.linkskool.fragments.ELibraryFragment;
import com.digitaldream.linkskool.fragments.FlashCardList;
import com.digitaldream.linkskool.interfaces.ELearningListener;
import com.digitaldream.linkskool.models.AssessmentModel;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.models.ExamType;
import com.digitaldream.linkskool.models.FormClassModel;
import com.digitaldream.linkskool.models.GeneralSettingModel;
import com.digitaldream.linkskool.models.GradeModel;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.models.NewsTable;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.models.TeacherCourseModel;
import com.digitaldream.linkskool.models.TeacherCourseModelCopy;
import com.digitaldream.linkskool.models.TeachersTable;
import com.digitaldream.linkskool.models.VideoTable;
import com.digitaldream.linkskool.models.VideoUtilTable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class Dashboard extends AppCompatActivity implements NewsAdapter.OnNewsClickListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private List<NewsTable> newsTitleList;
    private DatabaseHelper databaseHelper;
    private Dao<GeneralSettingModel, Long> generalSettingDao;
    private List<GeneralSettingModel> generalSettingsList;
    public static String db;
    private String user_name, school_name, userId;
    private TextView schoolName, user;
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

        //  setSupportActionBar(toolbar);
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

        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.settings_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.settings:
                        startActivity(new Intent(Dashboard.this, Settings.class));
                        return true;
                    case R.id.setup:
                        startActivity(
                                new Intent(Dashboard.this, PaymentActivity.class).putExtra
                                        ("from", "settings")
                        );
                        return true;
                    case R.id.info:
                        ContactUsDialog dialog = new ContactUsDialog(Dashboard.this);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        return true;

                    case android.R.id.home:
                        drawerLayout.openDrawer(GravityCompat.START);
                        return true;
                }
                return false;
            }
        });


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout.setDescendantFocusability(
                ViewGroup.FOCUS_AFTER_DESCENDANTS);

        View headerView = navigationView.getHeaderView(0);
        user = headerView.findViewById(R.id.user);
        schoolName = headerView.findViewById(R.id.school_name);
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


        if (!user_name.equals("null")) {
            try {
                user_name = user_name.substring(0,
                        1).toUpperCase() + user_name.substring(1);
                school_name = school_name.substring(0,
                        1).toUpperCase() + school_name.substring(1);
                user.setText(user_name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String[] strArray = school_name.split(" ");
        StringBuilder builder = new StringBuilder();
        try {
            for (String s : strArray) {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap).append(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        schoolName.setText(builder.toString());
        //school_Name.setText(builder.toString());
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

        navigationView.setNavigationItemSelectedListener(menuItem -> {
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

        navigationView.getMenu().getItem(0).setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.student_dashboard:

                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.payment_container,
                            new AdminDashboardFragment()).commit();
                    return true;

                case R.id.student_results:
                    /* getSupportFragmentManager().beginTransaction().replace(
                            R.id.payment_container,
                            new AdminResultFragment()).commit();*/

                    AdminClassesDialog adminResultDialog = new AdminClassesDialog(this, "result",
                            null, null);
                    adminResultDialog.setCancelable(true);
                    adminResultDialog.show();
                    Window window = adminResultDialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    return true;

                case R.id.payment:
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.payment_container,
                            new AdminPaymentDashboardFragment()).commit();

                    /*startActivity(new Intent(this, PaymentActivity.class).putExtra("from",
                            "dashboard"));*/
                    return true;
                case R.id.student_library:
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.payment_container,
                            new ELibraryFragment()).commit();
                    return true;

                case R.id.student_elearning:

                    AdminELearningDialog mAdminELearningDialog = new AdminELearningDialog(this,
                            new ELearningListener() {
                                @Override
                                public void goBackToHome() {
                                    setHomeItem(Dashboard.this);

                                }

                                @Override
                                public void openELearning(@NonNull String courseName,
                                                          @NonNull String courseId,
                                                          @NonNull String levelName,
                                                          @NonNull String levelId
                                ) {

                                    getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(
                                                    R.id.payment_container,
                                                    AdminELearningClassRoomFragment.newInstance(
                                                            courseName,
                                                            courseId,
                                                            levelName,
                                                            levelId
                                                    )
                                            ).commit();
                                }
                            });

                    mAdminELearningDialog.setCancelable(false);
                    mAdminELearningDialog.show();
                    Window mWindow = mAdminELearningDialog.getWindow();
                    mWindow.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

                    return true;
            }
            return false;
        });
    }


    @Override
    public void onNewsClick(int position) {

        Intent intent = new Intent(this, NewsPage.class);
        intent.putExtra("newsId", newsTitleList.get(position).getNewsId());
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

        navigationView.getMenu().getItem(0).setChecked(true);

        /*int seletedItemId = bottomNavigationView.getSelectedItemId();
        switch (seletedItemId){
                case R.id.student_dashboard:
                    getSupportFragmentManager().beginTransaction().replace(R
                    .id.fragment_container,new AdminDashbordFragment())
                    .commit();
                    break;
                case R.id.student_results:
                    getSupportFragmentManager().beginTransaction().replace(R
                    .id.fragment_container,new AdminResultFragment()).commit();
                    break;

                case R.id.flashcard:
                    getSupportFragmentManager().beginTransaction().replace(R
                    .id.fragment_container,new FlashCardList()).commit();
                    break;
                case R.id.student_cbt:
                    getSupportFragmentManager().beginTransaction().replace(R
                    .id.fragment_container,new ExamFragment()).commit();
                    break;

                case R.id.student_elearning:
                    getSupportFragmentManager().beginTransaction().replace(R
                    .id.fragment_container,new AdminElearning()).commit();
                    break;

        }*/
    }

    @Override
    public void onBackPressed() {
        int selectedItemId = bottomNavigationView.getSelectedItemId();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (R.id.student_dashboard != selectedItemId) {
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


    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }

    public void setHomeItem(Activity activity) {

        BottomNavigationView bottomNavigationView = activity.findViewById(
                R.id.bottom_navigation_student);
        bottomNavigationView.setSelectedItemId(R.id.student_dashboard);

    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                "loginDetail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginStatus", false);
        editor.putString("user", "");
        editor.putString("school_name", "");
        editor.apply();
        try {
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    StudentTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    TeachersTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    ClassNameTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    LevelTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    NewsTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    CourseTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    GradeModel.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    GeneralSettingModel.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    AssessmentModel.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    TeacherCourseModelCopy.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    TeacherCourseModel.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    FormClassModel.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    CourseOutlineTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    VideoTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    VideoUtilTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                    ExamType.class);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Dashboard.this, Login.class);
        startActivity(intent);
        finish();
    }

}
