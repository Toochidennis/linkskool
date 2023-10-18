package com.digitaldream.linkskool.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.linkskool.activities.Login;
import com.digitaldream.linkskool.adapters.CoursesRecylerAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.ClassNameTable;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.models.NewsTable;
import com.digitaldream.linkskool.adapters.NewsAdapter;
import com.digitaldream.linkskool.activities.NewsPage;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.models.TeachersTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StaffHome extends Fragment implements NewsAdapter.OnNewsClickListener {
    private Toolbar toolbar;
    private TextView no_of_courses,user,schoolName,no_of_form_classes;
    private DatabaseHelper databaseHelper;
    private Dao<StudentTable,Long> studentDao;
    private List<StudentTable> studentList;
    private Dao<NewsTable,Long> newsDao;
    private List<NewsTable> newsTitleList;
    private RecyclerView recyclerView;
    private LinearLayout news_empty_state;
    private RecyclerView coursesAssigned;
    private List<String> courses ;
    private List<String> classList;
    private List<CourseTable> courseList;
    private Dao<CourseTable,Long> courseDao;
    private CardView courseEmptyState;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_staff_home, container, false);
        setHasOptionsMenu(true);
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView =view.findViewById(R.id.news_recycler);
        coursesAssigned = view.findViewById(R.id.staff_courses);
        news_empty_state = view.findViewById(R.id.news_empty_state);
        courseEmptyState = view.findViewById(R.id.course_empty_state);
        schoolName = view.findViewById(R.id.school_name);

        recyclerView.setNestedScrollingEnabled(false);
        coursesAssigned.setNestedScrollingEnabled(false);



        courses = new ArrayList<String>();
        classList = new ArrayList<String>();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Linkskool");
        databaseHelper = new DatabaseHelper(getContext());
        try {
            studentDao = DaoManager.createDao(databaseHelper.getConnectionSource(),StudentTable.class);
            newsDao = DaoManager.createDao(databaseHelper.getConnectionSource(), NewsTable.class);
            courseDao = DaoManager.createDao(databaseHelper.getConnectionSource(),CourseTable.class);
            studentList = studentDao.queryForAll();
            newsTitleList = newsDao.queryForAll();
            courseList = courseDao.queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(int a =0; a<courseList.size();a++){
            courses.add(courseList.get(a).getCourseName());
            classList.add(courseList.get(a).getClassName());
        }

        no_of_courses = view.findViewById(R.id.no_of_courses_staff);
        user = view.findViewById(R.id.userID_display);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String user_name = sharedPreferences.getString("user","");
        String[] strArray = user_name.split(" ");
        String school_name = sharedPreferences.getString("school_name","");
        String[] strArray1 = school_name.split(" ");
        StringBuilder builder = new StringBuilder();
        for(String s : strArray){
            try {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap + " ");

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        StringBuilder builder1 = new StringBuilder();
        for(String s : strArray1){
            try {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder1.append(cap + " ");

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        user.setText("Welcome "+ builder.toString());
        schoolName.setText(builder1.toString());

        no_of_courses.setText(String.valueOf(courseList.size()));
        if(newsTitleList.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            news_empty_state.setVisibility(View.VISIBLE);
        }else {
            NewsAdapter newsAdapter = new NewsAdapter(getContext(), newsTitleList, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false){
                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }

                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(newsAdapter);

        }
        if(courses.isEmpty()){
            coursesAssigned.setVisibility(View.GONE);
            courseEmptyState.setVisibility(View.VISIBLE);
        }else {
            coursesAssigned.setVisibility(View.VISIBLE);
            courseEmptyState.setVisibility(View.GONE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            coursesAssigned.setLayoutManager(linearLayoutManager);
            coursesAssigned.setHasFixedSize(true);
            CoursesRecylerAdapter adapter = new CoursesRecylerAdapter(getContext(),courseList);
            coursesAssigned.setAdapter(adapter);


        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.staff_logout_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.staff_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Continue to logout?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                builder.show();
                break;
        }
        return true;
    }

    private void logout(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginStatus",false);
        editor.putString("user","");
        editor.putString("school_name","");
        editor.apply();
        try {
            TableUtils.clearTable(databaseHelper.getConnectionSource(),StudentTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), TeachersTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), ClassNameTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), LevelTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),NewsTable.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(),CourseTable.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getContext(), Login.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onNewsClick(int position) {
        Intent intent = new Intent(getContext(), NewsPage.class);
        intent.putExtra("newsId",newsTitleList.get(position).getNewsId());
        startActivity(intent);
    }
}
