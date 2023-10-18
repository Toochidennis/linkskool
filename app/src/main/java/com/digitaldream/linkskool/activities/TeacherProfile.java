package com.digitaldream.linkskool.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.TeachersTable;
import com.digitaldream.linkskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class TeacherProfile extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listView;
    private List<String> teacherInfoList;
    private RelativeLayout call, sms, email, whatsapp, courses, formclass;
    private TextView teacherName, staff_number, staff_phone_number, staff_address, staff_birthday
            , staff_gender;
    TeachersTable tch;
    private DatabaseHelper databaseHelper;
    private Dao<TeachersTable, Long> teacherDao;
    private String accessLevel;
    private ImageButton callIcon, smsIcon, emailIcon, whatsappIcon;
    private List<TeachersTable> staffList;
    private static String staffId;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);
        callIcon = findViewById(R.id.call_icon);
        smsIcon = findViewById(R.id.sms_icon);
        emailIcon = findViewById(R.id.email_icon);
        whatsappIcon = findViewById(R.id.whatsapp_icon);
        password = findViewById(R.id.password);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        accessLevel = sharedPreferences.getString("access_level", "");

        databaseHelper = new DatabaseHelper(this);
        try {
            teacherDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    TeachersTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent i = getIntent();
        tch = (TeachersTable) i.getSerializableExtra("teacherObject");
        staffId = tch.getStaffId();
        password.setText(tch.getPassword());


        teacherName = findViewById(R.id.teacherName_profile);

        teacherName.setText(
                tch.getStaffSurname().toUpperCase() + " " + tch.getStaffFirstname().toUpperCase());

        call = findViewById(R.id.call_teacher_profile);
        sms = findViewById(R.id.sms_teacher_profile);
        whatsapp = findViewById(R.id.whatsapp_teacher_profile);
        email = findViewById(R.id.email_teacher_profile);
        courses = findViewById(R.id.courses);
        formclass = findViewById(R.id.form_class);
        staff_number = findViewById(R.id.staff_no);
        staff_phone_number = findViewById(R.id.staff_phone);
        staff_address = findViewById(R.id.staff_address);
        staff_birthday = findViewById(R.id.staff_birthday);
        staff_gender = findViewById(R.id.staff_gender);
        if (tch.getStaffEmail().isEmpty()) {
            email.setEnabled(false);
            emailIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }


        if (tch.getStaffPhone().isEmpty()) {
            call.setEnabled(false);
            callIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            sms.setEnabled(false);
            smsIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            whatsapp.setEnabled(false);
            whatsappIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray),
                    android.graphics.PorterDuff.Mode.SRC_IN);

        }

        call.setOnClickListener(view -> {
            if (!tch.getStaffPhone().isEmpty()) {
                Intent i1 = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + tch.getStaffPhone()));
                startActivity(i1);
            } else {
                Toast.makeText(TeacherProfile.this, "phone no is not available",
                        Toast.LENGTH_SHORT).show();
            }
        });

        sms.setOnClickListener(view -> {
            if (!tch.getStaffPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:" + tch.getStaffPhone()));
                startActivity(intent);
            } else {
                Toast.makeText(TeacherProfile.this, "phone number is not available",
                        Toast.LENGTH_SHORT).show();
            }
        });

        whatsapp.setOnClickListener(view -> {
            if (!tch.getStaffPhone().isEmpty()) {
                Uri uri = Uri.parse(
                        "https://api.whatsapp.com/send?phone=" + "234" + tch.getStaffPhone() +
                                "&text=" + "");
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(sendIntent);
            } else {
                Toast.makeText(TeacherProfile.this, "phone number is not available",
                        Toast.LENGTH_SHORT).show();
            }
        });

        email.setOnClickListener(view -> {
            if (!tch.getStaffEmail().isEmpty()) {
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse(
                        "mailto:?subject=" + "subject text" + "&body=" + "body text " + "&to=" + tch.getStaffEmail());
                emailIntent.setData(data);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            } else {
                Toast.makeText(TeacherProfile.this, "Email address is not available",
                        Toast.LENGTH_SHORT).show();
            }
        });

        formclass.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherProfile.this, FormClass.class);
            intent.putExtra("staffId", staffId);
            intent.putExtra("name",
                    tch.getStaffSurname().toUpperCase() + " " + tch.getStaffFirstname().toUpperCase());
            startActivity(intent);
        });

        courses.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherProfile.this, CoursesAssigned.class);
            intent.putExtra("staffId", staffId);
            intent.putExtra("name",
                    tch.getStaffSurname().toUpperCase() + " " + tch.getStaffFirstname().toUpperCase());
            startActivity(intent);
        });


        if (!tch.getStaffNo().isEmpty()) {
            staff_number.setText(tch.getStaffNo().toUpperCase());
        } else {
            staff_number.setText("Not Available");
            staff_number.setTextColor(Color.parseColor("#FF0000"));

        }
        if (!tch.getStaffAddress().isEmpty() && !tch.getStaffAddress().equals("null")) {
            staff_address.setText(tch.getStaffAddress().toUpperCase());
        } else {
            staff_address.setText("Not Available");
            staff_address.setTextColor(Color.parseColor("#FF0000"));
        }
        if (!tch.getStaffPhone().isEmpty()) {
            staff_phone_number.setText(tch.getStaffPhone().toUpperCase());
        } else {
            staff_phone_number.setText("Not Available");
            staff_phone_number.setTextColor(Color.parseColor("#FF0000"));

        }
        /*if(!tch.getStaffBirthday().isEmpty()){
            String date = tch.getStaffBirthday();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date newDate =simpleDateFormat.parse(date);
                staff_birthday.setText(DateFormat.getDateInstance().format(newDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            staff_phone_number.setText("Not Available");
            staff_phone_number.setTextColor(Color.parseColor("#FF0000"));

        }*/
        if (!tch.getStaffGender().isEmpty()) {
            String genderValue;
            if (tch.getStaffGender().equalsIgnoreCase("f")) {
                genderValue = "Female";
            } else if (tch.getStaffGender().equalsIgnoreCase("m")) {
                genderValue = "Male";
            } else {
                genderValue = tch.getStaffGender();
            }
            staff_gender.setText(genderValue.toUpperCase());
        } else {
            staff_phone_number.setText("Not Available");
            staff_phone_number.setTextColor(Color.parseColor("#FF0000"));
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.edit_profile:
                Intent intent = new Intent(TeacherProfile.this, TeacherProfileEdit.class);
                TeachersTable tt = new TeachersTable();
                tt.setStaffSurname(tch.getStaffSurname());
                tt.setStaffFirstname(tch.getStaffFirstname());
                tt.setStaffMiddlename(tch.getStaffMiddlename());
                tt.setStaffEmail(tch.getStaffEmail());
                tt.setStaffAddress(tch.getStaffAddress());
                tt.setStaffGender(tch.getStaffGender());
                tt.setStaffId(tch.getStaffId());
                tt.setStaffNo(tch.getStaffNo());
                tt.setStaffPhone(tch.getStaffPhone());
                tt.setStaffStateOrigin(tch.getStaffStateOrigin());
                tt.setStaffBirthday(tch.getStaffBirthday());
                intent.putExtra("teacherObject", tt);
                startActivity(intent);
                return true;

            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherProfile.this);
                builder.setTitle("Delete ?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String id = tch.getStaffId();
                        deleteTeacherApiCall(id);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteTeacherApiCall(final String id) {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(TeacherProfile.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        String db = sharedPreferences.getString("db", "");
        String deleteUrl = Login.urlBase + "/deleteTeacher.php?id=" + id + "&_db=" + db;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("delete", response);
                        dialog1.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                DeleteBuilder<TeachersTable, Long> deleteBuilder =
                                        teacherDao.deleteBuilder();
                                deleteBuilder.where().eq("staffId", id);
                                deleteBuilder.delete();
                                Intent intent = new Intent(TeacherProfile.this,
                                        TeacherContacts.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        QueryBuilder<TeachersTable, Long> queryBuilder = teacherDao.queryBuilder();
        try {
            queryBuilder.where().eq("staffId", staffId);
            staffList = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        teacherName.setText(staffList.get(0).getStaffSurname().toUpperCase() + " " + staffList.get(
                0).getStaffFirstname().toUpperCase());

        if (!staffList.get(0).getStaffNo().isEmpty()) {
            staff_number.setText(staffList.get(0).getStaffNo().toUpperCase());
        } else {
            staff_number.setText("Not Available");
            staff_number.setTextColor(Color.parseColor("#FF0000"));

        }
        if (!staffList.get(0).getStaffAddress().isEmpty() && !staffList.get(
                0).getStaffAddress().equals("null")) {
            staff_address.setText(staffList.get(0).getStaffAddress().toUpperCase());
        } else {
            staff_address.setText("Not Available");
            staff_address.setTextColor(Color.parseColor("#FF0000"));
        }
        if (!staffList.get(0).getStaffPhone().isEmpty()) {
            staff_phone_number.setText(staffList.get(0).getStaffPhone().toUpperCase());
        } else {
            staff_phone_number.setText("Not Available");
            staff_phone_number.setTextColor(Color.parseColor("#FF0000"));

        }
        try {
            if (!staffList.get(0).getStaffBirthday().isEmpty()) {
                String date = staffList.get(0).getStaffBirthday();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date newDate = simpleDateFormat.parse(date);
                    staff_birthday.setText(DateFormat.getDateInstance().format(newDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                staff_phone_number.setText("Not Available");
                staff_phone_number.setTextColor(Color.parseColor("#FF0000"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!staffList.get(0).getStaffGender().isEmpty()) {
            String genderValue;
            if (staffList.get(0).getStaffGender().equalsIgnoreCase("f")) {
                genderValue = "Female";
            } else if (staffList.get(0).getStaffGender().equalsIgnoreCase("m")) {
                genderValue = "Male";
            } else {
                genderValue = staffList.get(0).getStaffGender();
            }
            staff_gender.setText(genderValue.toUpperCase());
        } else {
            staff_phone_number.setText("Not Available");
            staff_phone_number.setTextColor(Color.parseColor("#FF0000"));
        }

    }
}
