package com.digitaldream.winskool.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.activities.Login;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.dialog.CustomDialog;
import com.digitaldream.winskool.fragments.AdminDashboardFragment;
import com.digitaldream.winskool.fragments.StaffDashboardFragment;
import com.digitaldream.winskool.fragments.StudentDashboardFragment;
import com.digitaldream.winskool.models.CourseOutlineTable;
import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class QuestionAccessViewSheet extends BottomSheetDialogFragment {
    private DatabaseHelper databaseHelper;
    Dao<CourseTable, Long> courseTableDao;
    Dao<LevelTable, Long> levelDao;
    private List<LevelTable> levelList;
    private List<CourseTable> coursesList;
    private Dao<CourseOutlineTable, Long> courseOutlineDao;
    private List<CourseOutlineTable> list;
    List<CourseOutlineTable> courseList;
    private String accessLevel, sender, db;
    OnQuestionSubmitListener onQuestionSubmitListener;

    public static QuestionAccessViewSheet newInstance() {
        QuestionAccessViewSheet questionAccessViewSheet = new QuestionAccessViewSheet();

        return questionAccessViewSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.question_view_access, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        accessLevel = sharedPreferences.getString("access_level", "");
        db = sharedPreferences.getString("db", "");
        if (accessLevel.equals("3") || accessLevel.equals("2") || accessLevel.equals("1")) {
            sender = "2";
        } else {
            sender = "1";
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


        if (accessLevel.equals("3") || accessLevel.equals("1")) {
            try {
                courseTableDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseTable.class);
                coursesList = courseTableDao.queryBuilder().groupBy("levelId").query();
                for (int b = 0; b < coursesList.size(); b++) {
                    CourseTable ct = coursesList.get(b);
                    View v = layoutInflater.inflate(R.layout.question_access_view_item, levelLayout, false);
                    TextView t = v.findViewById(R.id.text_title);
                    t.setText(ct.getCourseName().toUpperCase());
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ct.isSelected()) {
                                ct.setSelected(false);
                                v.setBackground(getResources().getDrawable(R.drawable.border_outline_gray));
                                t.setTextColor(getResources().getColor(R.color.black));

                            } else {
                                ct.setSelected(true);
                                v.setBackground(getResources().getDrawable(R.drawable.border_outline_blue));
                                t.setTextColor(getResources().getColor(R.color.gray_ligth));
                            }
                        }
                    });
                    courseLayout.addView(v);
                }

                for (int a = 0; a < coursesList.size(); a++) {
                    CourseTable lt = coursesList.get(a);
                    View v = layoutInflater.inflate(R.layout.question_access_view_item, levelLayout, false);
                    TextView t = v.findViewById(R.id.text_title);
                    t.setText(lt.getLevelName().toUpperCase());
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lt.isSelected()) {
                                lt.setSelected(false);
                                v.setBackground(getResources().getDrawable(R.drawable.border_outline_gray));
                                t.setTextColor(getResources().getColor(R.color.black));


                            } else {
                                lt.setSelected(true);
                                v.setBackground(getResources().getDrawable(R.drawable.border_outline_blue));
                                t.setTextColor(getResources().getColor(R.color.gray_ligth));

                            }
                        }
                    });

                    levelLayout.addView(v);
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (accessLevel.equals("2")) {
            for (int a = 0; a < levelList.size(); a++) {
                LevelTable lt = levelList.get(a);
                View v = layoutInflater.inflate(R.layout.question_access_view_item, levelLayout, false);
                TextView t = v.findViewById(R.id.text_title);
                t.setText(lt.getLevelName().toUpperCase());
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lt.isSelected()) {
                            lt.setSelected(false);
                            v.setBackground(getResources().getDrawable(R.drawable.border_outline_gray));
                            t.setTextColor(getResources().getColor(R.color.black));


                        } else {
                            lt.setSelected(true);
                            v.setBackground(getResources().getDrawable(R.drawable.border_outline_blue));
                            t.setTextColor(getResources().getColor(R.color.gray_ligth));

                        }
                    }
                });
                levelLayout.addView(v);
            }
            for (int b = 0; b < coursesList.size(); b++) {
                CourseTable ct = coursesList.get(b);
                View v = layoutInflater.inflate(R.layout.question_access_view_item, levelLayout, false);
                TextView t = v.findViewById(R.id.text_title);
                t.setText(ct.getCourseName().toUpperCase());
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ct.isSelected()) {
                            ct.setSelected(false);
                            v.setBackground(getResources().getDrawable(R.drawable.border_outline_gray));
                            t.setTextColor(getResources().getColor(R.color.black));

                        } else {
                            ct.setSelected(true);
                            v.setBackground(getResources().getDrawable(R.drawable.border_outline_blue));
                            t.setTextColor(getResources().getColor(R.color.gray_ligth));
                        }
                    }
                });
                courseLayout.addView(v);
            }

        } else if (accessLevel.equals("-1")) {
            try {
                courseOutlineDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseOutlineTable.class);
                list = courseOutlineDao.queryBuilder().groupBy("levelId").query();
                QueryBuilder<CourseOutlineTable, Long> queryBuilder = courseOutlineDao.queryBuilder();
                queryBuilder.groupBy("courseId");
                courseList = queryBuilder.query();

                for (int a = 0; a < list.size(); a++) {
                    CourseOutlineTable lt = list.get(a);
                    View v = layoutInflater.inflate(R.layout.question_access_view_item, levelLayout, false);
                    TextView t = v.findViewById(R.id.text_title);
                    t.setText(lt.getLevelName().toUpperCase());
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lt.isSelected()) {
                                lt.setSelected(false);
                                v.setBackground(getResources().getDrawable(R.drawable.border_outline_gray));
                                t.setTextColor(getResources().getColor(R.color.black));

                            } else {
                                lt.setSelected(true);
                                v.setBackground(getResources().getDrawable(R.drawable.border_outline_blue));
                                t.setTextColor(getResources().getColor(R.color.gray_ligth));
                            }
                        }
                    });
                    levelLayout.addView(v);
                }

                for (int b = 0; b < courseList.size(); b++) {
                    CourseOutlineTable ct = courseList.get(b);
                    View v = layoutInflater.inflate(R.layout.question_access_view_item, levelLayout, false);
                    TextView t = v.findViewById(R.id.text_title);
                    t.setText(ct.getCourseName().toUpperCase());
                    v.setOnClickListener(v1 -> {
                        if (ct.isSelected()) {
                            ct.setSelected(false);
                            v1.setBackground(getResources().getDrawable(R.drawable.border_outline_gray));
                            t.setTextColor(getResources().getColor(R.color.black));

                        } else {
                            ct.setSelected(true);
                            v1.setBackground(getResources().getDrawable(R.drawable.border_outline_blue));
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
        closeBtn.setOnClickListener(v -> dismiss());

        TextView submitBtn = view.findViewById(R.id.submit);
        submitBtn.setOnClickListener(v -> {
            JSONArray arr = new JSONArray();
            JSONArray arr1 = new JSONArray();
            JSONObject levelObj = new JSONObject();
            JSONObject courseObj = new JSONObject();

            if (accessLevel.equals("2")) {
                for (int a = 0; a < levelList.size(); a++) {
                    LevelTable lv = levelList.get(a);
                    if (lv.isSelected()) {
                        try {
                            JSONObject object = new JSONObject();

                            object.put("level", lv.getLevelName());
                            object.put("level_id", lv.getLevelId());
                            arr.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                for (int b = 0; b < coursesList.size(); b++) {
                    CourseTable ct = coursesList.get(b);
                    if (ct.isSelected()) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("course_name", ct.getCourseName());
                            object.put("course_id", ct.getCourseId());
                            arr1.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            } else if (accessLevel.equals("3") || accessLevel.equals("1")) {
                for (int b = 0; b < coursesList.size(); b++) {
                    CourseTable ct = coursesList.get(b);
                    if (ct.isSelected()) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("course_name", ct.getCourseName());
                            object.put("course_id", ct.getCourseId());
                            arr1.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

                for (int a = 0; a < coursesList.size(); a++) {
                    CourseTable lv = coursesList.get(a);
                    if (lv.isSelected()) {
                        try {
                            JSONObject object = new JSONObject();

                            object.put("level", lv.getLevelName());
                            object.put("level_id", lv.getLevelId());
                            arr.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }


            } else if (accessLevel.equals("-1")) {
                for (int a = 0; a < list.size(); a++) {
                    CourseOutlineTable lv = list.get(a);
                    if (lv.isSelected()) {
                        try {
                            JSONObject object = new JSONObject();

                            object.put("level", lv.getLevelName());
                            object.put("level_id", lv.getLevelId());
                            arr.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }

                for (int b = 0; b < courseList.size(); b++) {
                    CourseOutlineTable ct = courseList.get(b);

                    if (ct.isSelected()) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("course_name", ct.getCourseName());
                            object.put("course_id", ct.getCourseId());
                            arr1.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }

            }
            try {
                courseObj.put("course_tags", arr1);
                levelObj.put("level_tags", arr);
                if (arr1.length() != 0 && arr.length() != 0) {
                    sendQuestionToSever(courseObj.toString(), levelObj.toString());
                } else {
                    Toast.makeText(getContext(), "Select atleast a course and a level", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void sendQuestionToSever(String courseTag, String levelTag) {

        CustomDialog dialog = new CustomDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        String url = Login.urlBase + "/addQuestion.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            dialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equals("success")) {
                    if (onQuestionSubmitListener != null) {
                        onQuestionSubmitListener.onSubmit();
                    }

                    Toast.makeText(getContext(), "Question added successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                    if (StudentDashboardFragment.questionBottomSheet != null) {
                        StudentDashboardFragment.questionBottomSheet.dismiss();
                        StudentDashboardFragment.refresh = true;
                        getFragmentManager().beginTransaction().replace(R.id.payment_container, new StudentDashboardFragment()).commit();
                    }
                    if (AdminDashboardFragment.questionBottomSheet != null) {
                        AdminDashboardFragment.questionBottomSheet.dismiss();
                        AdminDashboardFragment.refresh = true;
                        getFragmentManager().beginTransaction().replace(R.id.payment_container, new AdminDashboardFragment()).commit();

                    }
                    if (StaffDashboardFragment.questionBottomSheet != null) {
                        StaffDashboardFragment.questionBottomSheet.dismiss();
                        StaffDashboardFragment.refresh = true;
                        getFragmentManager().beginTransaction().replace(R.id.payment_container, new StaffDashboardFragment()).commit();
                    }


                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_name", QuestionBottomSheet.user_name);
                params.put("user_id", QuestionBottomSheet.user_id);
                params.put("question", QuestionBottomSheet.question);
                params.put("access", QuestionBottomSheet.viewAccess);
                params.put("level_tag", levelTag);
                params.put("course_tag", courseTag);
                params.put("access_level", sender);
                params.put("_db", db);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    public interface OnQuestionSubmitListener {
        void onSubmit();
    }

    public void setOnQuestionSubmittListener(OnQuestionSubmitListener listener) {
        this.onQuestionSubmitListener = listener;
    }
}
