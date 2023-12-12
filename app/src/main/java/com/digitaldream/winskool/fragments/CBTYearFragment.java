package com.digitaldream.winskool.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.adapters.CBTYearAdapter;
import com.digitaldream.winskool.dialog.CBTConfirmationDialog;
import com.digitaldream.winskool.dialog.CustomLoadingView;
import com.digitaldream.winskool.models.Exam;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CBTYearFragment extends Fragment implements CBTYearAdapter.OnYearClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mCourseName;
    private RecyclerView mRecyclerView;
    private List<Exam> mExamList;
    private Dao<Exam, Long> mDao;

    public CBTYearFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CBTYearFragment newInstance(String param1, String param2) {
        CBTYearFragment fragment = new CBTYearFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourseName = getArguments().getString(ARG_PARAM1);
            String examTypeId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_c_b_t_year, container,
                false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        mRecyclerView = view.findViewById(R.id.cbt_year_recycler);


        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

        try {
            mDao = DaoManager.createDao(databaseHelper.getConnectionSource()
                    , Exam.class);
        } catch (SQLException sE) {
            sE.printStackTrace();
        }

        ((AppCompatActivity) (requireActivity())).setSupportActionBar(toolbar);
        ActionBar actionBar =
                ((AppCompatActivity) (getActivity())).getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Choose year");
        setHasOptionsMenu(true);
        toolbar.setNavigationOnClickListener(
                v -> getActivity().onBackPressed());

        return view;
    }

    @Override
    public void onYearClick(int position) {
        Exam exam = mExamList.get(position);

        new Handler().postDelayed((Runnable) () -> {
            CustomLoadingView customLoadingView =
                    new CustomLoadingView(
                            requireActivity());

            customLoadingView.setCancelable(false);
            customLoadingView.show();
            Window window = customLoadingView.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            new Handler().postDelayed(() -> {
                CBTConfirmationDialog confirmationDialog =
                        new CBTConfirmationDialog(
                                requireContext(),
                                mCourseName,
                                exam.getYear());
                confirmationDialog.setCancelable(false);
                confirmationDialog.show();
                Window window1 = confirmationDialog.getWindow();
                window1.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                customLoadingView.dismiss();

            }, 3500);

        }, 50);

    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            try {
                mExamList = mDao.queryBuilder().where().eq("course",
                        mCourseName).query();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(mExamList,
                            Collections.reverseOrder(
                                    Comparator.comparing(Exam::getYear)));
                }

                if (!mExamList.isEmpty()) {
                    CBTYearAdapter adapter = new CBTYearAdapter(getContext(),
                            mExamList,
                            this);
                    GridLayoutManager manager =
                            new GridLayoutManager(getContext(), 2);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(manager);
                    mRecyclerView.setAdapter(adapter);
                }
            } catch (SQLException sE) {
                sE.printStackTrace();
            }

        }
    }

}