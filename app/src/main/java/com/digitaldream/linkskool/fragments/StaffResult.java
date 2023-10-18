package com.digitaldream.linkskool.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitaldream.linkskool.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffResult extends Fragment {


    public StaffResult() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_staff_result, container, false);
        return v;
    }

}
