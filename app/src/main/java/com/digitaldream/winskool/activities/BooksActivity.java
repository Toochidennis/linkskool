package com.digitaldream.winskool.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.digitaldream.winskool.adapters.SectionPagerAdapter;
import com.digitaldream.winskool.fragments.BooksReadingListFragment;
import com.digitaldream.winskool.fragments.LibraryBooksFragment;
import com.digitaldream.winskool.fragments.MyLibraryBooksFragment;
import com.digitaldream.winskool.R;
import com.google.android.material.tabs.TabLayout;

public class BooksActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.container);

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Books");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        SectionPagerAdapter adapter =
                new SectionPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new LibraryBooksFragment(), "MARKET");
        adapter.addFragment(new MyLibraryBooksFragment(), "MY LIBRARY");
        adapter.addFragment(new BooksReadingListFragment(), "READING LIST");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager, true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


}
