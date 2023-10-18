package com.digitaldream.linkskool.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.adapters.StudentResultAdapter;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.utils.RefreshListener;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentFragment extends Fragment implements StudentResultAdapter.OnStudentResultClickListener, RefreshListener {
    private RecyclerView recyclerView;
    private Spinner level,classes;
    private List<String> spinnerLevelList,spinnerClassList;
    private DatabaseHelper databaseHelper;
    private Dao<LevelTable,Long> levelDao;
    private Dao<ClassNameTable,Long> classDao;
    private List<LevelTable> levelList;
    private List<ClassNameTable> classList;
    private Dao<StudentTable,Long> studentDao;
    private List<StudentTable> studentResultList;
    private String studentLevelId;
    private StudentResultAdapter studentResultAdapter;
    private LinearLayout emptyState;
    private TextView studentTotal,studentCountText;



    public StudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student, container, false);
        level = view.findViewById(R.id.spinner_level);
        classes = view.findViewById(R.id.spinner_class);
        emptyState = view.findViewById(R.id.student_result_empty_state);
        studentTotal = view.findViewById(R.id.student_total);
        databaseHelper = new DatabaseHelper(getContext());
        try {
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(), LevelTable.class);
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(),ClassNameTable.class);
            studentDao = DaoManager.createDao(databaseHelper.getConnectionSource(),StudentTable.class);
            levelList = levelDao.queryForAll();
            classList = classDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        spinnerLevelList = new ArrayList<>();
        spinnerClassList = new ArrayList<>();

        for(int a =0;a<levelList.size();a++){
            String levelName = levelList.get(a).getLevelName().toUpperCase();
            spinnerLevelList.add(levelName);
        }

        for(int i=0; i<classList.size();i++){
            spinnerClassList.add(classList.get(i).getClassName().toUpperCase());
        }


        ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, spinnerLevelList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level.setAdapter(adapter);
        level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerClassList.clear();
                String levelSelected = adapterView.getItemAtPosition(i).toString();
                studentLevelId = levelList.get(i).getLevelId();
                //getStudentByLevel(studentLevelId);
                try {
                    setSpinnerClass(studentLevelId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter adapterClass = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, spinnerClassList);
        adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classes.setAdapter(adapterClass);
        classes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                QueryBuilder<StudentTable,Long> queryBuilder = studentDao.queryBuilder();
                String studentClass="";
                if(classList.size()>0) {
                    studentClass = classList.get(i).getClassId();
                }

                try {
                    queryBuilder.where().eq("studentLevel",studentLevelId).and().eq("studentClass",studentClass);
                    studentResultList = queryBuilder.query();
                    if(!studentResultList.isEmpty()) {
                        emptyState.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        studentResultAdapter = new StudentResultAdapter(getContext(), studentResultList, StudentFragment.this);
                        recyclerView.setAdapter(studentResultAdapter);
                        studentResultAdapter.notifyDataSetChanged();
                        //studentTotal.setText(String.valueOf(studentResultList.size()));
                        if(studentResultList.size()<2){
                            //studentCountText.setText("Student");
                        }else {
                            //studentCountText.setText("Students");

                        }

                    }else{
                        //studentCountText.setText("Student");
                        //studentTotal.setText(String.valueOf(studentResultList.size()));
                        emptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        recyclerView = view.findViewById(R.id.student_result_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onStudentResultClick(int position) {
        Intent intent = new Intent(getContext(), StudentResultDownloadFragment.class);
        intent.putExtra("student_id",studentResultList.get(position).getStudentId());
        intent.putExtra("phone_number",studentResultList.get(position).getGuardianPhoneNo());
        intent.putExtra("email",studentResultList.get(position).getGuardianEmail());
        intent.putExtra("student_name",studentResultList.get(position).getStudentSurname()+" "+studentResultList.get(position).getStudentMiddlename()+" "+studentResultList.get(position).getStudentFirstname());
        startActivity(intent);

    }


    public void setSpinnerClass(String a) throws SQLException {
        QueryBuilder<ClassNameTable,Long> queryBuilder = classDao.queryBuilder();
        queryBuilder.where().eq("level",a);
        classList = queryBuilder.query();
        Collections.reverse(classList);
        if(!classList.isEmpty()) {
            for (int i =0;i<classList.size();i++){
                try {
                    String classname = classList.get(i).getClassName().toUpperCase();
                    spinnerClassList.add(classname);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            ArrayAdapter adapterClass = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerClassList);
            adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            classes.setAdapter(adapterClass);
        }else {

            spinnerClassList.clear();
            spinnerClassList.add("");
            ArrayAdapter adapterClass = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinnerClassList);
            adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            classes.setAdapter(adapterClass);

        }

    }

    @Override
    public void onRefresh(DialogInterface dialog) {

    }
}
