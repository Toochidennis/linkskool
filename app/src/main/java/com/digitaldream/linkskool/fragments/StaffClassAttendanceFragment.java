package com.digitaldream.linkskool.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.linkskool.activities.StaffUtils;
import com.digitaldream.linkskool.adapters.StaffClassAttendanceAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;


public class StaffClassAttendanceFragment extends Fragment implements StaffClassAttendanceAdapter.OnLevelClickListener {

    private RecyclerView mRecyclerView;
    private RelativeLayout mLayout;
    private StaffClassAttendanceAdapter mAdapter;
    private List<ClassNameTable> mClassNameTableList;
    private Dao<ClassNameTable, Long> mDao;
    private DatabaseHelper mDatabaseHelper;


    public StaffClassAttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_staff_attendance_class,
                container, false);

        mRecyclerView = view.findViewById(R.id.staff_class_recycler);
        mLayout = view.findViewById(R.id.empty_state);

        mDatabaseHelper = new DatabaseHelper(getContext());

        try {
            mDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource()
                    , ClassNameTable.class);
            mClassNameTableList = mDao.queryForAll();

        } catch (SQLException sE) {
            sE.printStackTrace();
        }

        mAdapter = new StaffClassAttendanceAdapter(getContext(),
                mClassNameTableList, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager( new LinearLayoutManager(getContext()));

        if (!mClassNameTableList.isEmpty()) {
            mRecyclerView.setAdapter(mAdapter);
            mLayout.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mLayout.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onLevelClick(int position) {
        Intent intent = new Intent(getContext(), StaffUtils.class);
        intent.putExtra("classId", mClassNameTableList.get(position).getClassId());
        intent.putExtra("levelId", mClassNameTableList.get(position).getLevel());
        intent.putExtra("class_name", mClassNameTableList.get(position).getClassName());
        intent.putExtra("from","staff");
        startActivity(intent);

    }
}