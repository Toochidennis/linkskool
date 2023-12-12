package com.digitaldream.winskool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.digitaldream.winskool.adapters.CustomSpinnerAdapter;
import com.digitaldream.winskool.models.ClassNameTable;
import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.activities.CoursesAssigned;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.TeacherCourseModel;
import com.digitaldream.winskool.models.TeacherCourseModelCopy;
import com.digitaldream.winskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssignCourseDialog extends Dialog {
    private Activity activity;
    private Spinner courseSpinner,classSpinner;
    private Dao<CourseTable,Long> courseDao;
    private Dao<ClassNameTable,Long> classDao;
    private List<String> courseList,classList;
    private DatabaseHelper databaseHelper;
    private List<CourseTable> courses;
    private List<ClassNameTable> classes;
    private Button add,cancel;
    private String classId,courseName,className,courseId;
    private Dao<TeacherCourseModelCopy,Long> teacherCourseModelCopyDao;
    private String db;
    private List<TeacherCourseModelCopy> teacherCourseModelCopyList;
    private Dao<TeacherCourseModel,Long> teacherCourseDao;


    public AssignCourseDialog( Activity context) {
        super(context);
        this.activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_assign_course_dialog);
        courseSpinner = findViewById(R.id.course_spinner);
        classSpinner = findViewById(R.id.class_spinner);
        add = findViewById(R.id.add_tch_course);
        cancel = findViewById(R.id.cancel_tch_course);
        databaseHelper = new DatabaseHelper(getContext());
        try {
            courseDao = DaoManager.createDao(databaseHelper.getConnectionSource(),CourseTable.class);
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(),ClassNameTable.class);
            teacherCourseDao = DaoManager.createDao(databaseHelper.getConnectionSource(),TeacherCourseModel.class);
            teacherCourseModelCopyDao =DaoManager.createDao(databaseHelper.getConnectionSource(),TeacherCourseModelCopy.class);
            classes = classDao.queryForAll();
            courses = courseDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");

        courseList = new ArrayList<>();
        classList = new ArrayList<>();

        for(int i=0;i<classes.size();i++){
            classList.add(classes.get(i).getClassName().toUpperCase());
        }

        for (int a=0;a<courses.size();a++){
            courseList.add(courses.get(a).getCourseName().toUpperCase());
        }

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), (ArrayList<String>) courseList);
        courseSpinner.setAdapter(adapter);
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               courseName= courses.get(position).getCourseName();
               courseId = courses.get(position).getCourseId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CustomSpinnerAdapter adapter1 = new CustomSpinnerAdapter(getContext(), (ArrayList<String>) classList);
        classSpinner.setAdapter(adapter1);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                className = classes.get(position).getClassName();
                classId = classes.get(position).getClassId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    try {

                         teacherCourseModelCopyList = teacherCourseModelCopyDao.queryForAll();

                        if(teacherCourseModelCopyDao.queryBuilder().where().eq("courseId",courseId).and().eq("classId",classId).countOf()==0) {
                            CoursesAssigned coursesAssigned = new CoursesAssigned();
                            if(teacherCourseModelCopyDao.queryBuilder().where().eq("courseId",courseId).countOf()>1){
                                TeacherCourseModelCopy tmc = new TeacherCourseModelCopy();
                                tmc.setId("");
                                tmc.setCourseName(courseName);
                                tmc.setClassName(className);
                                tmc.setCourseId(courseId);
                                tmc.setClassId(classId);
                                tmc.setStaffId(CoursesAssigned.staffId);

                                teacherCourseModelCopyDao.create(tmc);
                                coursesAssigned.sectionedRecyclerViewAdapter.removeAllSections();
                                coursesAssigned.sectionedRecyclerViewAdapter.notifyDataSetChanged();


                                teacherCourseModelCopyList = teacherCourseModelCopyDao.queryForAll();
                                for (int b = 0; b < teacherCourseModelCopyList.size(); b++) {
                                    Log.i("size", String.valueOf(teacherCourseModelCopyList.size()));
                                    String courseName1 = teacherCourseModelCopyList.get(b).getCourseName();
                                    String teacherId = teacherCourseModelCopyList.get(b).getStaffId();
                                    String classId = teacherCourseModelCopyList.get(b).getClassId();
                                    List<TeacherCourseModelCopy> classList = getTeacherCourseClasses(teacherCourseModelCopyList.get(b).getCourseId());
                                    coursesAssigned.sectionedRecyclerViewAdapter.addSection(courseName1, new CoursesAssigned.MySection(courseName1, classList, getContext()));
                                }

                                coursesAssigned.sectionedRecyclerViewAdapter.notifyDataSetChanged();
                                if (coursesAssigned.sectionedRecyclerViewAdapter.getItemCount() > 0) {
                                    CoursesAssigned.emptyLayout.setVisibility(View.GONE);
                                    CoursesAssigned.layout.setVisibility(View.VISIBLE);
                                    CoursesAssigned.recyclerView.setAdapter(coursesAssigned.sectionedRecyclerViewAdapter);
                                    coursesAssigned.sectionedRecyclerViewAdapter.notifyDataSetChanged();
                                } else {
                                    CoursesAssigned.layout.setVisibility(View.GONE);
                                    CoursesAssigned.emptyLayout.setVisibility(View.VISIBLE);
                                }

                                /*QueryBuilder<TeacherCourseModel,Long> queryBuilder4 = teacherCourseDao.queryBuilder();
                                queryBuilder4.where().eq("staffId",CoursesAssigned.staffId);
                                List<TeacherCourseModel> teacherCourseList = queryBuilder4.query();
                                for (int a = 0; a < teacherCourseList.size(); a++) {
                                    TeacherCourseModelCopy tmc1 = new TeacherCourseModelCopy();
                                    tmc1.setClassId(teacherCourseList.get(a).getClassId());
                                    tmc1.setClassName(teacherCourseList.get(a).getClassName());
                                    tmc1.setCourseId(teacherCourseList.get(a).getCourseId());
                                    tmc1.setCourseName(teacherCourseList.get(a).getCourseName());
                                    tmc1.setId(teacherCourseList.get(a).getId());
                                    tmc1.setStaffId(teacherCourseList.get(a).getStaffId());
                                    teacherCourseModelCopyDao.create(tmc1);

                                }*/

                            }else {
                                TeacherCourseModelCopy tmc = new TeacherCourseModelCopy();
                                tmc.setId("");
                                tmc.setCourseName(courseName);
                                tmc.setClassName(className);
                                tmc.setCourseId(courseId);
                                tmc.setClassId(classId);
                                tmc.setStaffId(CoursesAssigned.staffId);

                                teacherCourseModelCopyDao.create(tmc);
                                coursesAssigned.sectionedRecyclerViewAdapter.removeAllSections();
                                coursesAssigned.sectionedRecyclerViewAdapter.notifyDataSetChanged();

                                teacherCourseModelCopyList = teacherCourseModelCopyDao.queryForAll();
                                for (int b = 0; b < teacherCourseModelCopyList.size(); b++) {
                                    Log.i("size", String.valueOf(teacherCourseModelCopyList.size()));
                                    String courseName1 = teacherCourseModelCopyList.get(b).getCourseName();
                                    String teacherId = teacherCourseModelCopyList.get(b).getStaffId();
                                    String classId = teacherCourseModelCopyList.get(b).getClassId();
                                    List<TeacherCourseModelCopy> classList = getTeacherCourseClasses(teacherCourseModelCopyList.get(b).getCourseId());
                                    coursesAssigned.sectionedRecyclerViewAdapter.addSection(courseName1, new CoursesAssigned.MySection(courseName1, classList, getContext()));
                                }

                                coursesAssigned.sectionedRecyclerViewAdapter.notifyDataSetChanged();
                                if (coursesAssigned.sectionedRecyclerViewAdapter.getItemCount() > 0) {
                                    CoursesAssigned.emptyLayout.setVisibility(View.GONE);
                                    CoursesAssigned.layout.setVisibility(View.VISIBLE);
                                    CoursesAssigned.recyclerView.setAdapter(coursesAssigned.sectionedRecyclerViewAdapter);
                                    coursesAssigned.sectionedRecyclerViewAdapter.notifyDataSetChanged();
                                } else {
                                    CoursesAssigned.layout.setVisibility(View.GONE);
                                    CoursesAssigned.emptyLayout.setVisibility(View.VISIBLE);
                                }
                            }

                        }
                        else{
                            Toast.makeText(getContext(), "Course already assigned to teacher", Toast.LENGTH_SHORT).show();
                        }


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    dismiss();


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
               dismiss();
            }
        });

    }

    private List<TeacherCourseModelCopy> getTeacherCourseClasses(String courseId){
        List<TeacherCourseModelCopy> classes = null;
        QueryBuilder<TeacherCourseModelCopy,Long> queryBuilder = teacherCourseModelCopyDao.queryBuilder();
        try {
            queryBuilder.where().eq("courseId",courseId);
            classes = queryBuilder.query();
            for(int a=0;a<classes.size();a++){
                Log.i("clas",classes.get(a).getClassName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }



}
