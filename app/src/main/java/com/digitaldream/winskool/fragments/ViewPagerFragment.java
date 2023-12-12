package com.digitaldream.winskool.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitaldream.winskool.R;
import com.digitaldream.winskool.models.ViewPagerModel;
import com.digitaldream.winskool.adapters.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerFragment extends Fragment {
    private ViewPager viewPager;
    private List<ViewPagerModel> viewPagerModelList;
    ViewPagerAdapter viewPagerAdapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_view_pager, container, false);
        viewPagerModelList = new ArrayList<>();
        viewPagerModelList.add(new ViewPagerModel("55","No. of Students","Student Contacts","Raymond College"));
        viewPagerModelList.add(new ViewPagerModel("5","No. of Teachers","Teachers Contacts","Raymond College"));

        viewPagerAdapter = new ViewPagerAdapter(viewPagerModelList,getContext());
        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPadding(50,0,50,0);

       return view;
    }




}
