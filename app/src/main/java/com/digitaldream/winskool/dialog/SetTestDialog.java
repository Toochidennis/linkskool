package com.digitaldream.winskool.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.digitaldream.winskool.activities.TestUpload;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.CourseTable;
import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.models.TestSettingModel;
import com.digitaldream.winskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SetTestDialog extends Dialog implements View.OnClickListener {
    Activity activity;
    Switch aSwitch;
    LinearLayout setTimingLayout;
    TextView apply;
    Spinner week;
    public static EditText title,description,startDate,endDate,duration,startTime,endTime;
    private List<String> weekList;
    private String weekValue;
    List<TestSettingModel> testSettingList;
    private boolean checked = false;
    private String category="0";
    private DatabaseHelper databaseHelper;
    Dao<CourseTable,Long> courseTableDao;
    Dao<LevelTable,Long> levelDao;
    private List<LevelTable> levelList;
    private List<CourseTable> coursesList;
/*    private Dao<CourseOutlineTable,Long> courseOutlineDao;
    private List<CourseOutlineTable> list;
    List<CourseOutlineTable> courseList;*/
    String levelName,levelId,courseId,courseName;
    Spinner levelSpinner;
    Spinner courseSpinner;

    public SetTestDialog(Activity context) {
        super(context);
        this.activity = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.set_test_layout);


        aSwitch = findViewById(R.id.exam_time_switch);
        setTimingLayout = findViewById(R.id.timing_layout);
        apply = findViewById(R.id.apply);
        title = findViewById(R.id.debt_fee_title);
        description = findViewById(R.id.description);
        startDate = findViewById(R.id.date_start);
        endDate = findViewById(R.id.date_end);
        week = findViewById(R.id.weeks);
        duration = findViewById(R.id.duration);
        endTime = findViewById(R.id.time_end);
        startTime = findViewById(R.id.time_start);
         levelSpinner = findViewById(R.id.level);
        courseSpinner = findViewById(R.id.course);


        courseName = TestUpload.testHeaderList.get(0).getCourse();
        courseId = TestUpload.testHeaderList.get(0).getCourseId();
        levelName = TestUpload.testHeaderList.get(0).getLevel();
        levelId = TestUpload.testHeaderList.get(0).getLevelId();

        LinearLayout settingLayout = findViewById(R.id.setting_layout);
        TextView showAdvanceSetting = findViewById(R.id.show_advanced_setting);
        TextView advanceSetting = findViewById(R.id.advanced_settings);
        showAdvanceSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingLayout.setVisibility(View.VISIBLE);
                showAdvanceSetting.setVisibility(View.GONE);
            }
        });
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day,month,year;
                String date = startDate.getText().toString().trim();
                if (date != null && date.length() > 0) {

//if you  have previous set date
                    String data[] = date.split("-");
                    year = Integer.parseInt(data[0]);

                    month = Integer.parseInt(data[1]);
                    month = month - 1;
                    day = Integer.parseInt(data[2]);
                } else {

//if you don't have previous set date then display current date
                    year = calendar.get(Calendar.YEAR);
                    year = year;
                    month = calendar.get(Calendar.MONTH);

                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }
                //day = calendar.get(Calendar.DAY_OF_MONTH);
                //month = calendar.get(Calendar.MONTH);
                //year = calendar.get(Calendar.YEAR);


               DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        startDate.setText( i+ "-" + (i1 + 1) + "-" + i2);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day,month,year;
                String date = endDate.getText().toString().trim();
                if (date != null && date.length() > 0) {

//if you  have previous set date
                    String data[] = date.split("-");
                    year = Integer.parseInt(data[0]);

                    month = Integer.parseInt(data[1]);
                    month = month - 1;
                    day = Integer.parseInt(data[2]);
                } else {

//if you dont have previous set date then display current date
                    year = calendar.get(Calendar.YEAR);
                    year = year;
                    month = calendar.get(Calendar.MONTH);

                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }
                //day = calendar.get(Calendar.DAY_OF_MONTH);
                //month = calendar.get(Calendar.MONTH);
                //year = calendar.get(Calendar.YEAR);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        endDate.setText( i+ "-" + (i1 + 1) + "-" + i2);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });


        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checked = true;
                    setTimingLayout.setVisibility(View.VISIBLE);
                }else {
                    setTimingLayout.setVisibility(View.GONE);
                    checked=false;

                }
            }
        });

        Switch bSwitch = findViewById(R.id.take_exam_switch);
        bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    category = "1";
                }else {
                    category = "0";

                }
            }
        });

        apply.setOnClickListener(this);
        weekList = new ArrayList<>();
        for(int i=1;i<30;i++){
            weekList.add("Week "+i);
        }

        ArrayAdapter adapterTerm = new ArrayAdapter(getContext(), R.layout.spinner_item, weekList);
        adapterTerm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        week.setAdapter(adapterTerm);

        week.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekValue = String.valueOf(position +1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        databaseHelper = new DatabaseHelper(getContext());
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String accessLevel = sharedPreferences.getString("access_level","");
        List<String> levelList1 = new ArrayList<>();
        List<String> courseList = new ArrayList<>();
            try {
                if(accessLevel.equals("3")||accessLevel.equals("1")) {
                    courseTableDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseTable.class);
                    coursesList = courseTableDao.queryBuilder().groupBy("levelId").query();
                    for(int a=0;a<coursesList.size();a++){
                        courseList.add(coursesList.get(a).getCourseName());
                        levelList1.add(coursesList.get(a).getLevelName());
                        if(TestUpload.testHeaderList.get(0).getLevelId().equals(coursesList.get(a).getLevelId())){

                        }
                    }
                }else if(accessLevel.equals("2")) {
                    courseTableDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseTable.class);
                    levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(), LevelTable.class);
                    coursesList = courseTableDao.queryForAll();
                    levelList = levelDao.queryForAll();
                    for(int a=0;a<coursesList.size();a++){
                        courseList.add(coursesList.get(a).getCourseName());

                    }
                    for(int b=0;b<levelList.size();b++){
                        levelList1.add(levelList.get(b).getLevelName());
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        ArrayAdapter adapter = new ArrayAdapter(activity,android.R.layout.simple_spinner_item, levelList1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(adapter);
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(accessLevel.equals("3")||accessLevel.equals("1")) {

                    levelName = coursesList.get(position).getLevelName();
                    levelId = coursesList.get(position).getLevelId();
                }else if(accessLevel.equals("2")){
                    levelName = levelList.get(position).getLevelName();
                    levelId = levelList.get(position).getLevelId();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter adapter1 = new ArrayAdapter(activity,android.R.layout.simple_spinner_item, courseList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter1);
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    courseName = coursesList.get(position).getCourseName();
                    courseId = coursesList.get(position).getCourseId();
                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if(!TestUpload.testHeaderList.isEmpty()) {

            title.setText(TestUpload.testHeaderList.get(0).getTitle());
            description.setText(TestUpload.testHeaderList.get(0).getDescription());
            startDate.setText(TestUpload.testHeaderList.get(0).getStartDate());
            endDate.setText(TestUpload.testHeaderList.get(0).getEndDate());
            duration.setText(TestUpload.testHeaderList.get(0).getDuration());
            startTime.setText(TestUpload.testHeaderList.get(0).getStartTime());
            endTime.setText(TestUpload.testHeaderList.get(0).getEndTime());
            levelSpinner.setSelection(getIndex(levelSpinner,TestUpload.testHeaderList.get(0).getLevel()));
            courseSpinner.setSelection(getIndex(courseSpinner,TestUpload.testHeaderList.get(0).getCourse()));
            week.setSelection(getIndex(week, "Week "+TestUpload.testHeaderList.get(0).getWeek()));
            if(TestUpload.testHeaderList.get(0).isChecked()){
                setTimingLayout.setVisibility(View.VISIBLE);
                aSwitch.setChecked(true);
            }else {
                setTimingLayout.setVisibility(View.GONE);
            }
            if(TestUpload.testHeaderList.get(0).getAccess()!=null && TestUpload.testHeaderList.get(0).getAccess().equals("1")){
                bSwitch.setChecked(true);
            }else{
                bSwitch.setChecked(false);

            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.apply) {
            Log.i("response", courseName + " and " + courseId);
            TestUpload.recyclerView.setVisibility(View.VISIBLE);
            TestUpload.testHeaderList.clear();
            String titleText = title.getText().toString();
            String startDateText = startDate.getText().toString();
            String endDateText = endDate.getText().toString();
            String desc = description.getText().toString();
            String timeDuration = duration.getText().toString();
            TestSettingModel tsm = new TestSettingModel();
            tsm.setWeek(weekValue);
            tsm.setTitle(titleText);
            tsm.setStartDate(startDateText);
            tsm.setDescription(desc);
            tsm.setEndDate(endDateText);
            tsm.setChecked(checked);
            tsm.setAccess(category);
            tsm.setType("assessment");
            tsm.setDuration(timeDuration);
            tsm.setCourse(courseName);
            tsm.setCourseId(courseId);
            tsm.setLevel(levelName);
            tsm.setLevelId(levelId);
            tsm.setStartTime(startTime.getText().toString());
            tsm.setEndTime(endTime.getText().toString());
            TestUpload.testHeaderList.add(tsm);
            TestUpload.adapter.notifyDataSetChanged();
        }
        dismiss();
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
            TestUpload.recyclerView.setVisibility(View.VISIBLE);
        TestUpload.testHeaderList.clear();
        String titleText = title.getText().toString();
        String startDateText = startDate.getText().toString();
        String endDateText = endDate.getText().toString();
        String desc = description.getText().toString();
        String timeDuration = duration.getText().toString();
        TestSettingModel tsm = new TestSettingModel();
        tsm.setWeek(weekValue);
        tsm.setTitle(titleText);
        tsm.setStartDate(startDateText);
        tsm.setDescription(desc);
        tsm.setDuration(timeDuration);
        tsm.setChecked(checked);
        tsm.setEndDate(endDateText);
        tsm.setAccess(category);
        tsm.setType("assessment");
        tsm.setCourse(courseName);
        tsm.setCourseId(courseId);
        tsm.setLevel(levelName);
        tsm.setLevelId(levelId);
        tsm.setStartTime(startTime.getText().toString());
        tsm.setEndTime(endTime.getText().toString());
        TestUpload.testHeaderList.add(tsm);
        TestUpload.adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            TestUpload.recyclerView.setVisibility(View.VISIBLE);
            TestUpload.testHeaderList.clear();
            String titleText = title.getText().toString();
            String startDateText = startDate.getText().toString();
            String endDateText = endDate.getText().toString();
            String desc = description.getText().toString();
            String timeDuration = duration.getText().toString();
            TestSettingModel tsm = new TestSettingModel();
            tsm.setWeek(weekValue);
            tsm.setTitle(titleText);
            tsm.setStartDate(startDateText);
            tsm.setDescription(desc);
            tsm.setEndDate(endDateText);
            tsm.setDuration(timeDuration);
            tsm.setChecked(checked);
            tsm.setType("assessment");
            tsm.setAccess(category);
            tsm.setCourse(courseName);
            tsm.setCourseId(courseId);
            tsm.setLevel(levelName);
            tsm.setLevelId(levelId);
            tsm.setStartTime(startTime.getText().toString());
            tsm.setEndTime(endTime.getText().toString());
            TestUpload.testHeaderList.add(tsm);
            TestUpload.adapter.notifyDataSetChanged();


    }


}
