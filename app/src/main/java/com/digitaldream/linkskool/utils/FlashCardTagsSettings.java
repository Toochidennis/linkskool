package com.digitaldream.linkskool.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digitaldream.linkskool.activities.CourseOutlines;
import com.digitaldream.linkskool.activities.TestUpload;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.models.LevelTable;
import com.digitaldream.linkskool.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FlashCardTagsSettings extends BottomSheetDialogFragment {
    private DatabaseHelper databaseHelper;
    Dao<CourseTable,Long> courseTableDao;
    Dao<LevelTable,Long> levelDao;
    private List<LevelTable> levelList;
    private List<CourseTable> coursesList;
    private Dao<CourseOutlineTable,Long> courseOutlineDao;
    private List<CourseOutlineTable> list;
    List<CourseOutlineTable> courseList;
    private String accessLevel,sender,db;
    public static String title="";
    public static JSONObject levelObj ;
    public static JSONObject courseObj;
    public static String level,course;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.flashcard_tag_settings,container,false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        accessLevel = sharedPreferences.getString("access_level","");
        db = sharedPreferences.getString("db","");
        if(accessLevel.equals("3")||accessLevel.equals("2")||accessLevel.equals("1")){
            sender ="2";
        }else{
            sender="1";
        }

        databaseHelper = new DatabaseHelper(getContext());
        try {
            courseTableDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseTable.class);
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(), LevelTable.class);
            //coursesList = courseTableDao.queryBuilder().groupBy("levelId").query();
            coursesList = courseTableDao.queryForAll();
            levelList = levelDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        GridLayout levelLayout = view.findViewById(R.id.level_container);
        GridLayout courseLayout = view.findViewById(R.id.class_container);


        if(accessLevel.equals("3")||accessLevel.equals("1")){
            try {
                courseTableDao = DaoManager.createDao(databaseHelper.getConnectionSource(),CourseTable.class);
                coursesList = courseTableDao.queryBuilder().groupBy("levelId").query();
                for(int b=0;b<coursesList.size();b++){
                    CourseTable ct = coursesList.get(b);
                    View v = layoutInflater.inflate(R.layout.question_access_view_item,levelLayout,false);
                    TextView t = v.findViewById(R.id.text_title);
                    t.setText(ct.getCourseName().toUpperCase());
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int b =0;b<courseLayout.getChildCount();b++){
                                LinearLayout l = (LinearLayout) courseLayout.getChildAt(b);
                                courseLayout.getChildAt(b).setBackgroundColor(getResources().getColor(R.color.white));
                                TextView t = l.findViewById(R.id.text_title);
                                t.setTextColor(getResources().getColor(R.color.black));
                                l.setBackground(getResources().getDrawable(R.drawable.border_outline_gray));
                                ct.setSelected(false);
                            }
                                ct.setSelected(true);
                            course=ct.getCourseId();
                            v.setBackground(getResources().getDrawable(R.drawable.border_outline_blue));
                                t.setTextColor(getResources().getColor(R.color.gray_ligth));

                        }
                    });
                    courseLayout.addView(v);
                }

                for(int a = 0;a<coursesList.size();a++){
                    CourseTable lt = coursesList.get(a);
                    View v = layoutInflater.inflate(R.layout.question_access_view_item,levelLayout,false);
                    TextView t = v.findViewById(R.id.text_title);
                    t.setText(lt.getLevelName().toUpperCase());
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int b =0;b<levelLayout.getChildCount();b++){
                                LinearLayout l = (LinearLayout) levelLayout.getChildAt(b);
                                levelLayout.getChildAt(b).setBackgroundColor(getResources().getColor(R.color.white));
                                TextView t = l.findViewById(R.id.text_title);
                                t.setTextColor(getResources().getColor(R.color.black));
                                l.setBackground(getResources().getDrawable(R.drawable.border_outline_gray));
                                lt.setSelected(false);
                            }
                                lt.setSelected(true);
                            level = lt.getLevelId();
                            v.setBackground(getResources().getDrawable(R.drawable.border_outline_blue));
                                t.setTextColor(getResources().getColor(R.color.gray_ligth));


                        }
                    });

                    levelLayout.addView(v);
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if(accessLevel.equals("2")){
            for(int a = 0;a<levelList.size();a++){
                LevelTable lt = levelList.get(a);
                View v = layoutInflater.inflate(R.layout.question_access_view_item,levelLayout,false);
                TextView t = v.findViewById(R.id.text_title);
                t.setText(lt.getLevelName().toUpperCase());
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int b =0;b<levelLayout.getChildCount();b++){
                            LinearLayout l = (LinearLayout) levelLayout.getChildAt(b);
                            levelLayout.getChildAt(b).setBackgroundColor(getResources().getColor(R.color.white));
                            TextView t = l.findViewById(R.id.text_title);
                            t.setTextColor(getResources().getColor(R.color.black));
                            l.setBackground(getResources().getDrawable(R.drawable.border_outline_gray));
                            lt.setSelected(false);
                        }

                            lt.setSelected(true);
                            level = lt.getLevelId();
                            v.setBackground(getResources().getDrawable(R.drawable.border_outline_blue));
                            t.setTextColor(getResources().getColor(R.color.gray_ligth));


                    }
                });
                levelLayout.addView(v);
            }
            for(int b=0;b<coursesList.size();b++){
                CourseTable ct = coursesList.get(b);
                View v = layoutInflater.inflate(R.layout.question_access_view_item,levelLayout,false);
                TextView t = v.findViewById(R.id.text_title);
                t.setText(ct.getCourseName().toUpperCase());
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int b =0;b<courseLayout.getChildCount();b++){
                            LinearLayout l = (LinearLayout) courseLayout.getChildAt(b);
                            courseLayout.getChildAt(b).setBackgroundColor(getResources().getColor(R.color.white));
                            TextView t = l.findViewById(R.id.text_title);
                            t.setTextColor(getResources().getColor(R.color.black));
                            l.setBackground(getResources().getDrawable(R.drawable.border_outline_gray));
                            ct.setSelected(false);
                        }

                        ct.setSelected(true);
                        course=ct.getCourseId();
                        v.setBackground(getResources().getDrawable(R.drawable.border_outline_blue));
                        t.setTextColor(getResources().getColor(R.color.gray_ligth));

                    }
                });
                courseLayout.addView(v);
            }

        }else if(accessLevel.equals("-1")){
            try {
                courseOutlineDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseOutlineTable.class);
                list = courseOutlineDao.queryBuilder().groupBy("levelId").query();
                QueryBuilder<CourseOutlineTable,Long> queryBuilder = courseOutlineDao.queryBuilder();
                queryBuilder.groupBy("courseId");
                courseList = queryBuilder.query();

                for(int a = 0;a<list.size();a++){
                    CourseOutlineTable lt = list.get(a);
                    View v = layoutInflater.inflate(R.layout.question_access_view_item,levelLayout,false);
                    TextView t = v.findViewById(R.id.text_title);
                    t.setText(lt.getLevelName().toUpperCase());
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int b =0;b<levelLayout.getChildCount();b++){
                                LinearLayout l = (LinearLayout) levelLayout.getChildAt(b);
                                levelLayout.getChildAt(b).setBackgroundColor(getResources().getColor(R.color.white));
                                TextView t = l.findViewById(R.id.text_title);
                                t.setTextColor(getResources().getColor(R.color.black));
                                l.setBackground(getResources().getDrawable(R.drawable.border_outline_gray));
                                lt.setSelected(false);
                            }
                                lt.setSelected(true);
                                level = lt.getLevelId();
                                v.setBackground(getResources().getDrawable(R.drawable.border_outline_blue));
                                t.setTextColor(getResources().getColor(R.color.gray_ligth));

                        }
                    });
                    levelLayout.addView(v);
                }

                for(int b=0;b<courseList.size();b++){
                    CourseOutlineTable ct = courseList.get(b);
                    View v = layoutInflater.inflate(R.layout.question_access_view_item,levelLayout,false);
                    TextView t = v.findViewById(R.id.text_title);
                    t.setText(ct.getCourseName().toUpperCase());
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int b =0;b<courseLayout.getChildCount();b++){
                                LinearLayout l = (LinearLayout) courseLayout.getChildAt(b);
                                courseLayout.getChildAt(b).setBackgroundColor(getResources().getColor(R.color.white));
                                TextView t = l.findViewById(R.id.text_title);
                                t.setTextColor(getResources().getColor(R.color.black));
                                l.setBackground(getResources().getDrawable(R.drawable.border_outline_gray));
                                ct.setSelected(false);
                            }

                            ct.setSelected(true);
                            course = ct.getCourseId();
                            v.setBackground(getResources().getDrawable(R.drawable.border_outline_blue));
                            t.setTextColor(getResources().getColor(R.color.gray_ligth));
                        }


                    });
                    courseLayout.addView(v);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        ImageView closeBtn = view.findViewById(R.id.close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView submitBtn = view.findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONArray arr = new JSONArray();
                JSONArray arr1 = new JSONArray();
                levelObj = new JSONObject();
                courseObj = new JSONObject();

                if(accessLevel.equals("2")){
                    for(int a = 0;a<levelList.size();a++){
                        LevelTable lv = levelList.get(a);
                        if(lv.isSelected()){
                            try {
                                JSONObject object = new JSONObject();

                                object.put("level",lv.getLevelName());
                                object.put("level_id",lv.getLevelId());
                                arr.put(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    for(int b=0;b<coursesList.size();b++){
                        CourseTable ct = coursesList.get(b);
                        if(ct.isSelected()){
                            try {
                                JSONObject object = new JSONObject();
                                object.put("course_name",ct.getCourseName());
                                object.put("course_id",ct.getCourseId());
                                arr1.put(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                }else if(accessLevel.equals("3") || accessLevel.equals("1")){
                    for(int b=0;b<coursesList.size();b++){
                        CourseTable ct = coursesList.get(b);
                        if(ct.isSelected()){
                            try {
                                JSONObject object = new JSONObject();
                                object.put("course_name",ct.getCourseName());
                                object.put("course_id",ct.getCourseId());
                                arr1.put(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    for(int a = 0;a<coursesList.size();a++){
                        CourseTable lv = coursesList.get(a);
                        if(lv.isSelected()){
                            try {
                                JSONObject object = new JSONObject();

                                object.put("level",lv.getLevelName());
                                object.put("level_id",lv.getLevelId());
                                arr.put(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }


                }else if(accessLevel.equals("-1")){
                    for(int a = 0;a<list.size();a++){
                        CourseOutlineTable lv = list.get(a);
                        if(lv.isSelected()){
                            try {
                                JSONObject object = new JSONObject();

                                object.put("level",lv.getLevelName());
                                object.put("level_id",lv.getLevelId());
                                arr.put(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    for(int b=0;b<courseList.size();b++){
                        CourseOutlineTable ct = courseList.get(b);

                        if(ct.isSelected()){
                            try {
                                JSONObject object = new JSONObject();
                                object.put("course_name",ct.getCourseName());
                                object.put("course_id",ct.getCourseId());
                                arr1.put(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }


                    }

                }
                try {
                    courseObj.put("course_tags",arr1);
                    levelObj.put("level_tags",arr);
                    if(arr1.length()!=0 && arr.length()!=0) {
                        Intent intent3 = new Intent(getContext(), TestUpload.class);
                        intent3.putExtra("levelId", CourseOutlines.levelId);
                        intent3.putExtra("courseId",CourseOutlines.courseId);
                        intent3.putExtra("from","flash_card");
                        intent3.putExtra("id","");
                        getContext().startActivity(intent3);
                    }else{
                        Toast.makeText(getContext(),"Select atleast a course and a level",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
}
