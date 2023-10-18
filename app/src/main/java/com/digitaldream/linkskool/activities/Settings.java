package com.digitaldream.linkskool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.adapters.SettingListAdapter;

import java.util.Objects;

public class Settings extends AppCompatActivity {
    private final String[] settingsTitle = {"General settings", "Level", "Courses",
            "Grade", "Assessment", "Class"};

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
/*
                case 5:
                    startActivity(new Intent(this, AddClass.class));
                    break;*/
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }
}
