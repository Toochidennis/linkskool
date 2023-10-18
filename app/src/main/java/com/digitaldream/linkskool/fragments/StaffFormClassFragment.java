package com.digitaldream.linkskool.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.activities.Login;
import com.digitaldream.linkskool.activities.StaffUtils;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.utils.FunctionUtils;
import com.digitaldream.linkskool.utils.VolleyCallback;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class StaffFormClassFragment extends Fragment {

    private TextView mErrorMessage;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mLinearLayout;
    private RecyclerView mRecyclerView;
    private SectionedRecyclerViewAdapter mAdapter;
    private DatabaseHelper mDatabaseHelper;
    private Dao<StudentTable, Long> studentDao;
    private Dao<ClassNameTable, Long> classDao;
    private Dao<LevelTable, Long> levelDao;

    public StaffFormClassFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_staff_form_class, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        mRecyclerView = view.findViewById(R.id.student_recycler);
        mSwipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        mLinearLayout = view.findViewById(R.id.recycler_container);
        mErrorMessage = view.findViewById(R.id.error_message);

        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setTitle("Form Class");
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        try {
            mDatabaseHelper = new DatabaseHelper(getContext());
            studentDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource(),
                    StudentTable.class);
            classDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource(),
                    ClassNameTable.class);
            levelDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource(),
                    LevelTable.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mAdapter = new SectionedRecyclerViewAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        mSwipeRefreshLayout.setOnRefreshListener(this::refreshStudentList);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshStudentList();
    }

    private void refreshStudentList() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        String staffId = sharedPreferences.getString("user_id", "");

        String url = requireActivity().getString(R.string.base_url) + "/allStaffStudent.php?staff_id=" + staffId;
        HashMap<String, String> hashMap = new HashMap<>();

        FunctionUtils.sendRequestToServer(Request.Method.GET, url, requireContext(), hashMap,
                new VolleyCallback() {
                    @Override
                    public void onResponse(@NonNull String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("teacherStudents")) {
                                JSONObject studentObject = jsonObject.getJSONObject(
                                        "teacherStudents");
                                JSONArray studentArray = studentObject.getJSONArray("rows");
                                if (studentArray.length() > 0) {
                                    TableUtils.clearTable(mDatabaseHelper.getConnectionSource(),
                                            StudentTable.class);
                                    TableUtils.clearTable(mDatabaseHelper.getConnectionSource(),
                                            LevelTable.class);
                                    TableUtils.clearTable(mDatabaseHelper.getConnectionSource(),
                                            ClassNameTable.class);

                                    for (int i = 0; i < studentArray.length(); i++) {
                                        JSONArray studentDetails = studentArray.getJSONArray(i);
                                        String id = studentDetails.getString(0);
                                        String studentSurname = studentDetails.getString(2);
                                        String studentFirstname = studentDetails.getString(3);
                                        String studentMiddleName = studentDetails.getString(4);
                                        String studentGender = studentDetails.getString(5);
                                        String studentReg_no = studentDetails.getString(12);
                                        String studentClass = studentDetails.getString(26);
                                        String studentLevel = studentDetails.getString(27);
                                        String studentDOB = studentDetails.getString(6);
                                        String guardianName = studentDetails.getString(14);
                                        String guardianAddress = studentDetails.getString(15);
                                        String guardianEmail = studentDetails.getString(16);
                                        String guardianPhoneNo = studentDetails.getString(17);
                                        String lga = studentDetails.getString(18);
                                        String state_of_origin = studentDetails.getString(19);
                                        String nationality = studentDetails.getString(20);
                                        String date_admitted = studentDetails.getString(22);

                                        StudentTable studentTable = new StudentTable();
                                        studentTable.setStudentId(id);
                                        studentTable.setStudentSurname(studentSurname);
                                        studentTable.setStudentFirstname(studentFirstname);
                                        studentTable.setStudentMiddlename(studentMiddleName);
                                        studentTable.setStudentGender(studentGender);
                                        studentTable.setStudentReg_no(studentReg_no);
                                        studentTable.setStudentClass(studentClass);
                                        studentTable.setStudentLevel(studentLevel);
                                        studentTable.setGuardianName(guardianName);
                                        studentTable.setGuardianAddress(guardianAddress);
                                        studentTable.setGuardianEmail(guardianEmail);
                                        studentTable.setGuardianPhoneNo(guardianPhoneNo);
                                        studentTable.setLga(lga);
                                        studentTable.setState_of_origin(state_of_origin);
                                        studentTable.setNationality(nationality);
                                        studentTable.setDate_admitted(date_admitted);
                                        studentTable.setDate_of_birth(studentDOB);
                                        studentDao.create(studentTable);
                                    }


                                    JSONObject classObject = jsonObject.getJSONObject("className");
                                    JSONArray classArray = classObject.getJSONArray("rows");
                                    for (int i = 0; i < classArray.length(); i++) {
                                        JSONArray classDetails = classArray.getJSONArray(i);
                                        String classId = classDetails.getString(0);
                                        String className = classDetails.getString(1);
                                        String level = classDetails.getString(2);

                                        ClassNameTable classNameTable = new ClassNameTable();
                                        classNameTable.setClassId(classId);
                                        classNameTable.setClassName(className);
                                        classNameTable.setLevel(level);
                                        classDao.create(classNameTable);
                                    }

                                    JSONObject levelObject = jsonObject.getJSONObject("levelName");
                                    JSONArray levelArray = levelObject.getJSONArray("rows");
                                    for (int i = 0; i < levelArray.length(); i++) {
                                        JSONArray levelDetails = levelArray.getJSONArray(i);
                                        String levelId = levelDetails.getString(0);
                                        String levelName = levelDetails.getString(1);
                                        String schoolType = levelDetails.getString(2);

                                        LevelTable levelTable = new LevelTable();
                                        levelTable.setLevelId(levelId);
                                        levelTable.setLevelName(levelName);
                                        levelTable.setSchoolType(schoolType);
                                        levelDao.create(levelTable);
                                    }
                                }

                                setRecyclerViewItems();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(@NonNull VolleyError error) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mLinearLayout.setVisibility(View.GONE);
                        mErrorMessage.setVisibility(View.VISIBLE);
                        mErrorMessage.setText(getString(R.string.can_not_retrieve));
                    }
                }, true);
    }

    private void setRecyclerViewItems() {
        mAdapter.removeAllSections();

        try {
            List<ClassNameTable> classList = new ArrayList<>();
            List<LevelTable> levelList = levelDao.queryForAll();

            Collections.sort(levelList, (s1, s2) ->
                    s1.getLevelName().compareToIgnoreCase(s2.getLevelName()));

            for (int i = 0; i < levelList.size(); i++) {
                String levelName = levelList.get(i).getLevelName();

                classList = classDao.queryBuilder().where().eq("level",
                        levelList.get(i).getLevelId()).query();

                mAdapter.addSection(new SectionAdapter(requireContext(), classList, levelName));
            }

            if (!classList.isEmpty()) {
                mRecyclerView.setAdapter(mAdapter);
                mLinearLayout.setVisibility(View.VISIBLE);
                mErrorMessage.setVisibility(View.GONE);
            } else {
                mLinearLayout.setVisibility(View.GONE);
                mErrorMessage.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static class SectionAdapter extends Section {
        private final Context mContext;
        private final List<ClassNameTable> mClassNameList;
        private final String headerTitle;

        public SectionAdapter(Context sContext,
                              List<ClassNameTable> sClassList,
                              String sHeaderTitle) {

            super(SectionParameters.builder()
                    .itemResourceId(R.layout.fragment_staff_form_class_item)
                    .headerResourceId(R.layout.head)
                    .build());

            mContext = sContext;
            mClassNameList = sClassList;
            headerTitle = sHeaderTitle;
        }

        @Override
        public int getContentItemsTotal() {
            return mClassNameList.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new SectionAdapter.ItemViewHolder(view);
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new SectionAdapter.HeaderViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder sViewHolder, int sI) {
            SectionAdapter.ItemViewHolder holder =
                    (SectionAdapter.ItemViewHolder) sViewHolder;
            final ClassNameTable classTable = mClassNameList.get(sI);

            holder.mClassName.setText(classTable.getClassName());

            holder.mViewStudents.setOnClickListener(view ->
                    mContext.startActivity(new Intent(mContext, StaffUtils.class)
                            .putExtra("classId", classTable.getClassId())
                            .putExtra("from", "form_class"))
            );

            holder.mComment.setOnClickListener(view ->
                    mContext.startActivity(new Intent(mContext, StaffUtils.class)
                            .putExtra("classId", classTable.getClassId())
                            .putExtra("from", "staff_comment"))
            );

            holder.mSkills.setOnClickListener(view ->
                    mContext.startActivity(new Intent(mContext, StaffUtils.class)
                            .putExtra("classId", classTable.getClassId())
                            .putExtra("from", "skills_behaviour"))
            );

        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            SectionAdapter.HeaderViewHolder headerViewHolder =
                    (SectionAdapter.HeaderViewHolder) holder;

            headerViewHolder.mHeader.setText(headerTitle);
        }

        public static class ItemViewHolder extends RecyclerView.ViewHolder {

            private final TextView mClassName;
            private final LinearLayout mSkills;
            private final LinearLayout mComment;
            private final LinearLayout mViewStudents;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                mClassName = itemView.findViewById(R.id.class_name);
                mSkills = itemView.findViewById(R.id.skills_behaviour);
                mComment = itemView.findViewById(R.id.comment);
                mViewStudents = itemView.findViewById(R.id.view_student);
            }

        }

        public static class HeaderViewHolder extends RecyclerView.ViewHolder {
            private final TextView mHeader;

            public HeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                mHeader = itemView.findViewById(R.id.course_name);
            }
        }
    }
}