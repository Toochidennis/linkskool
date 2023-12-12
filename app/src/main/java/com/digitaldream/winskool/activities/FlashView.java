package com.digitaldream.winskool.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.digitaldream.winskool.adapters.FlashCardAdapter;
import com.digitaldream.winskool.models.FlashCardModel;
import com.digitaldream.winskool.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FlashView extends AppCompatActivity {
    public static List<FlashCardModel> flashCardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_view);
        Intent i = getIntent();
        String json = i.getStringExtra("Json");
        Log.i("json",json);
        flashCardList = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray("q");
            JSONArray jsonArray1 = jsonArray.getJSONArray(0);
            for(int a=0;a<jsonArray1.length();a++){
                JSONObject jsonObject = jsonArray1.getJSONObject(a);
                String term = jsonObject.getString("content");
                String definition = jsonObject.getString("correct");
                FlashCardModel md = new FlashCardModel();
                md.setTerm(term);
                md.setDefinition(definition);
                flashCardList.add(md);
            }
            String title = object.getJSONObject("e").getString("title");
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(title.substring(0,1).toUpperCase()+""+title.substring(1).toLowerCase());
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FlashCardAdapter pageAdapter = new FlashCardAdapter(getSupportFragmentManager(),flashCardList);
        ViewPager pager = (ViewPager)findViewById(R.id.view_pager);
        pager.setAdapter(pageAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
}
