package com.digitaldream.linkskool.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.adapters.SettingListAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.digitaldream.linkskool.models.ExamType;
import com.digitaldream.linkskool.models.NewsTable;
import com.digitaldream.linkskool.models.StudentCourses;
import com.digitaldream.linkskool.models.StudentResultDownloadTable;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.models.VideoTable;
import com.digitaldream.linkskool.models.VideoUtilTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Objects;

public class Settings extends AppCompatActivity {
    private final String[] settingsTitle = {"General settings", "Level", "Courses",
            "Grade", "Assessment", "Class", "Logout"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        ListView listView = findViewById(R.id.settings_list);

        SettingListAdapter adapter = new SettingListAdapter(this,
                settingsTitle);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(this, GeneralSettings.class));
                    break;
                case 1:
                    startActivity(new Intent(this, LevelSettings.class));
                    break;
                case 2:
                    startActivity(new Intent(this, CourseSettings.class));
                    break;
                case 3:
                    startActivity(new Intent(this, GradeSettings.class));
                    break;
                case 4:
                    startActivity(new Intent(this, AssessmentSetting.class));
                    break;
                case 6:
                    logout();
                    break;
            }
        });
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                "loginDetail", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginStatus", false);
        editor.putString("user", "");
        editor.putString("school_name", "");
        editor.putString("attachment", "");
        editor.apply();

        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }
}
