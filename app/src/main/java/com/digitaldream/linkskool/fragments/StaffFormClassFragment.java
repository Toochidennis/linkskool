package com.digitaldream.linkskool.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.adapters.StaffFormClassAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.models.StaffFormClassModel;
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


public class StaffFormClassFragment extends Fragment {

    private TextView errorMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private RecyclerView formClassRecyclerView;

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
        return inflater.inflate(R.layout.fragment_staff_form_class, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpViews(view);

        init();
    }

    private void setUpViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        formClassRecyclerView = view.findViewById(R.id.formClassRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        errorMessage = view.findViewById(R.id.emptyTxt);
    }

    private void init() {
        ((AppCompatActivity) (requireActivity())).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) (requireActivity())).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Form class");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> requireActivity().onBackPressed());

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

        swipeRefreshLayout.setOnRefreshListener(this::refreshStudentList);

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
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(@NonNull VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText(getString(R.string.can_not_retrieve));
                    }
                }, true);
    }

    private void setRecyclerViewItems() {
        try {
            List<StaffFormClassModel> formClassModel = new ArrayList<>();
            List<LevelTable> levelList = levelDao.queryForAll();

            Collections.sort(levelList, (s1, s2) ->
                    s1.getLevelName().compareToIgnoreCase(s2.getLevelName()));

            for (int i = 0; i < levelList.size(); i++) {
                String levelName = levelList.get(i).getLevelName();

                List<ClassNameTable> classList =
                        classDao.queryBuilder().where().eq("level",
                                levelList.get(i).getLevelId()).query();

                formClassModel.add(new StaffFormClassModel(levelName, classList));
            }

            if (formClassModel.isEmpty()) {
                errorMessage.setVisibility(View.VISIBLE);
            } else {
                setUpFormClassAdapter(formClassModel);
                errorMessage.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpFormClassAdapter(List<StaffFormClassModel> formClassModel) {
        StaffFormClassAdapter staffFormClassAdapter =
                new StaffFormClassAdapter(getParentFragmentManager(), formClassModel);
        formClassRecyclerView.hasFixedSize();
        formClassRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        formClassRecyclerView.setAdapter(staffFormClassAdapter);
    }

}