package com.digitaldream.winskool.activities;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.digitaldream.winskool.adapters.TabAdapter;
import com.digitaldream.winskool.fragments.ClassResultFragment;
import com.digitaldream.winskool.fragments.StudentFragment;
import com.digitaldream.winskool.R;

public class ViewResult extends AppCompatActivity {
    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);
        tabLayout = findViewById(R.id.tablayout);
        mViewPager = findViewById(R.id.view_pager);
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabAdapter.addFragment(new StudentFragment(), "Students");
        tabAdapter.addFragment(new ClassResultFragment(), "Class");

        mViewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Result");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
