package com.digitaldream.winskool.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.digitaldream.winskool.activities.AdminResultDashboardActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.activities.AddClass;
import com.digitaldream.winskool.adapters.ClassResultAdapter;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.models.ClassNameTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.models.StudentTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class ClassResultFragment extends Fragment implements ClassResultAdapter.OnClassResultClickListener {
    private RecyclerView recyclerView;
    private List<String> classResultList;
    private Spinner spinner;
    private List<String> spinnerList;
    private DatabaseHelper databaseHelper;
    private Dao<LevelTable, Long> levelDao;
    private Dao<ClassNameTable, Long> classDao;
    private List<LevelTable> levelList;
    private List<ClassNameTable> classList;
    private Dao<StudentTable, Long> studentDao;
    private ClassResultAdapter classResultAdapter;
    private TextView classCount;
    private FrameLayout layout, emptyState;
    private FloatingActionButton fab1, fab2;
    public static String levelId;
    private TextView classCountText;
    private SwipeRefreshLayout swipeRefreshLayout, swipeRefreshLayout2;
    private String db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_class_result, container, false);
        recyclerView = view.findViewById(R.id.class_result_recycler);
        spinner = view.findViewById(R.id.spinner_level_classresult);
        emptyState = view.findViewById(R.id.empty_state);
        layout = view.findViewById(R.id.class_list_unempty_state);
        classCount = view.findViewById(R.id.class_total);
        classCountText = view.findViewById(R.id.class_count_text);
        fab1 = view.findViewById(R.id.fab_class);
        fab2 = view.findViewById(R.id.fab_class1);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshClass);
        swipeRefreshLayout2 = view.findViewById(R.id.swipeRefreshClass2);
        classResultList = new ArrayList<>();
        spinnerList = new ArrayList<>();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");

        databaseHelper = new DatabaseHelper(getContext());
        try {
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(), LevelTable.class);
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    ClassNameTable.class);
            studentDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    StudentTable.class);
            levelList = levelDao.queryForAll();
            classList = classDao.queryForAll();
            for (int a = 0; a < levelList.size(); a++) {
                String levelName = levelList.get(a).getLevelName().toUpperCase();
                spinnerList.add(levelName);
            }
            for (int i = 0; i < classList.size(); i++) {
                classResultList.add(classList.get(i).getClassName().toUpperCase());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        swipeRefreshLayout.setOnRefreshListener(this::refreshClassList);

        swipeRefreshLayout2.setOnRefreshListener(this::refreshClassList);


        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item,
                spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                classResultList.clear();
                levelId = levelList.get(i).getLevelId();
                QueryBuilder<ClassNameTable, Long> queryBuilder = classDao.queryBuilder();
                try {
                    queryBuilder.where().eq("level", levelId);
                    classList = queryBuilder.query();
                    for (int a = 0; a < classList.size(); a++) {

                        String className = classList.get(a).getClassName().substring(0,
                                1).toUpperCase() + classList.get(a).getClassName().substring(1);

                        classResultList.add(className);
                    }
                    Collections.sort(classResultList);
                    if (!classResultList.isEmpty()) {
                        emptyState.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                        classResultAdapter = new ClassResultAdapter(getContext(), classList,
                                ClassResultFragment.this);
                        classResultAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(classResultAdapter);
                        classCount.setText(String.valueOf(classResultList.size()));
                        if (classResultList.size() < 2) {
                            classCountText.setText("Class");
                        } else {
                            classCountText.setText("Classes");

                        }
                    } else {
                        classCount.setText(String.valueOf(classResultList.size()));
                        classCountText.setText("Class");
                        layout.setVisibility(View.GONE);
                        emptyState.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Collections.sort(classResultList);
        classResultAdapter = new ClassResultAdapter(getContext(), classList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(classResultAdapter);

        fab1.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AddClass.class);
            i.putExtra("from", "class");
            startActivity(i);
        });

        fab2.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AddClass.class);
            i.putExtra("from", "class");
            startActivity(i);
        });
        return view;
    }

    @Override
    public void onClassResultClick(int position) {
        Intent intent = new Intent(getContext(), AdminResultDashboardActivity.class);
        intent.putExtra("classId", classList.get(position).getClassId());
        intent.putExtra("class_name", classList.get(position).getClassName());
        intent.putExtra("levelId", levelId);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        classResultList.clear();

        QueryBuilder<ClassNameTable, Long> queryBuilder = classDao.queryBuilder();
        try {
            queryBuilder.where().eq("level", levelId);
            classList = queryBuilder.query();
            for (int a = 0; a < classList.size(); a++) {

                String className = classList.get(a).getClassName().substring(0,
                        1).toUpperCase() + classList.get(a).getClassName().substring(1);

                classResultList.add(className);
            }
            Collections.sort(classResultList);
            if (!classResultList.isEmpty()) {
                emptyState.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                classResultAdapter = new ClassResultAdapter(getContext(), classList,
                        ClassResultFragment.this);
                classResultAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(classResultAdapter);
                classCount.setText(String.valueOf(classResultList.size()));
            } else {
                classCount.setText(String.valueOf(classResultList.size()));
                emptyState.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        Collections.sort(classResultList);
        classResultAdapter = new ClassResultAdapter(getContext(), classList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(classResultAdapter);
    }


    private void refreshClassList() {

        String login_url = getString(R.string.base_url) + "/allClass.php?_db=" + db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, login_url,
                response -> {
                    Log.i("response", response);
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response);
                        JSONObject jsonObject3 = jsonObject.getJSONObject("allClass");
                        JSONArray jsonArray4 = jsonObject3.getJSONArray("rows");
                        if (jsonArray4.length() > 0) {
                            TableUtils.clearTable(databaseHelper.getConnectionSource(),
                                    ClassNameTable.class);

                            for (int i = 0; i < jsonArray4.length(); i++) {
                                JSONArray jsonArray5 = jsonArray4.getJSONArray(i);
                                String classId = jsonArray5.getString(0);
                                String className = jsonArray5.getString(1);
                                String level = jsonArray5.getString(2);
                                QueryBuilder<ClassNameTable, Long> queryBuilder =
                                        classDao.queryBuilder();
                                queryBuilder.where().eq("classId", classId);
                                classList = queryBuilder.query();
                                if (classList.isEmpty()) {
                                    ClassNameTable cn = new ClassNameTable();
                                    cn.setClassId(classId);
                                    cn.setClassName(className);
                                    cn.setLevel(level);
                                    classDao.create(cn);
                                }

                            }
                            QueryBuilder<ClassNameTable, Long> queryBuilder =
                                    classDao.queryBuilder();
                            queryBuilder.where().eq("level", levelId);
                            classList = queryBuilder.query();
                            classResultAdapter = new ClassResultAdapter(getContext(), classList,
                                    ClassResultFragment.this);
                            classResultAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(classResultAdapter);
                        }
                    } catch (JSONException | SQLException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(getContext(), "List updated", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout2.setRefreshing(false);
                }, error -> swipeRefreshLayout.setRefreshing(false));

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

}

