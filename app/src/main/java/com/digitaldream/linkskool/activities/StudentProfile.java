package com.digitaldream.linkskool.activities;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.StudentTable;
import com.digitaldream.linkskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class StudentProfile extends AppCompatActivity {
    private Toolbar toolbar;
    private RelativeLayout viewResult, call, sms, whatsapp, email;
    private TextView studentName, guardianName, guardianPhone, guardianAddress, guardianEmail;
    private Button editBtn;
    StudentTable st = null;
    private DatabaseHelper databaseHelper;
    private Dao<StudentTable, Long> studentDao;
    private String accessLevel, db;
    private List<StudentTable> student;
    private TextView initialsDisplay;
    public static String initials;
    private ImageView callIcon, smsIcon, emailIcon, whatsappIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        callIcon = findViewById(R.id.call_icon);
        smsIcon = findViewById(R.id.sms_icon);
        emailIcon = findViewById(R.id.email_icon);
        whatsappIcon = findViewById(R.id.whatsapp_icon);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        accessLevel = sharedPreferences.getString("access_level", "");
        db = sharedPreferences.getString("db", "");

        databaseHelper = new DatabaseHelper(StudentProfile.this);
        try {
            studentDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    StudentTable.class);
            student = studentDao.queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        st = (StudentTable) getIntent().getSerializableExtra("studentObject");
        call = findViewById(R.id.call_student_profile);
        sms = findViewById(R.id.sms_student_profile);
        whatsapp = findViewById(R.id.whatsapp_student_profile);
        email = findViewById(R.id.email_student_profile);
        guardianName = findViewById(R.id.guardian_name);
        guardianAddress = findViewById(R.id.guardian_address);
        guardianPhone = findViewById(R.id.guardian_phone);

        if (st.getGuardianPhoneNo().isEmpty()) {
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

        if (st.getGuardianEmail().isEmpty()) {
            email.setEnabled(false);
            emailIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray),
                    android.graphics.PorterDuff.Mode.SRC_IN);

        }

        call.setOnClickListener(view -> {
            if (!st.getGuardianPhoneNo().isEmpty()) {
                Intent i1 = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + st.getGuardianPhoneNo()));
                startActivity(i1);
            } else {
                Toast.makeText(StudentProfile.this, "phone number is not available",
                        Toast.LENGTH_SHORT).show();
            }
        });
        sms.setOnClickListener(view -> {
            if (!st.getGuardianPhoneNo().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:" + st.getGuardianPhoneNo()));
                startActivity(intent);
            } else {
                Toast.makeText(StudentProfile.this, "phone number is not available",
                        Toast.LENGTH_SHORT).show();
            }
        });
        whatsapp.setOnClickListener(view -> {
            if (!st.getGuardianPhoneNo().isEmpty()) {
                Uri uri = Uri.parse(
                        "https://api.whatsapp.com/send?phone=" + "234" + st.getGuardianPhoneNo() + "&text=" + "");
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);

                startActivity(sendIntent);
            } else {
                Toast.makeText(StudentProfile.this, "phone number is not available",
                        Toast.LENGTH_SHORT).show();
            }
        });

        email.setOnClickListener(view -> {
            if (!st.getGuardianEmail().isEmpty()) {
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse(
                        "mailto:?subject=" + "subject text" + "&body=" + "body text " + "&to=" + st.getGuardianEmail());
                emailIntent.setData(data);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            } else {
                Toast.makeText(StudentProfile.this, "email is not available",
                        Toast.LENGTH_SHORT).show();
            }
        });

        viewResult = findViewById(R.id.view_result_student_profile);

        viewResult.setOnClickListener(view ->
                startActivity(new Intent(StudentProfile.this, PaymentActivity.class)
                        .putExtra("studentId", st.getStudentId())
                        .putExtra("student_name", studentName.getText().toString())
                        .putExtra("levelId", st.getStudentLevel())
                        .putExtra("classId", st.getStudentClass())
                        .putExtra("reg_no", st.getStudentReg_no())
                        .putExtra("from", "student_profile")
                ));


        if (!st.getGuardianName().equals("") && !st.getGuardianName().equals("null")) {
            String guardianName1 = st.getGuardianName().toLowerCase();
            if (guardianName1.length() > 0) {
                guardianName1 = guardianName1.substring(0,
                        1).toUpperCase() + guardianName1.substring(1);
                guardianName.setText(guardianName1);

            }
        } else {
            guardianName.setText("Not Available");
            guardianName.setTextColor(Color.parseColor("#FF0000"));

        }
        if (!st.getGuardianAddress().equals("") && !st.getGuardianAddress().equals("null")) {
            String guardianAddress1 = st.getGuardianAddress().toLowerCase();
            guardianAddress1 = guardianAddress1.substring(0,
                    1).toUpperCase() + guardianAddress1.substring(1);
            guardianAddress.setText(guardianAddress1);
        } else {
            guardianAddress.setText("Not Available");
            guardianAddress.setTextColor(Color.parseColor("#FF0000"));

        }
        if (!st.getGuardianPhoneNo().equals("") && !st.getGuardianPhoneNo().equals("null")) {
            guardianPhone.setText(st.getGuardianPhoneNo());
        } else {
            guardianPhone.setText("Not Available");
            guardianPhone.setTextColor(Color.parseColor("#FF0000"));

        }
        if (!st.getGuardianEmail().isEmpty() && !st.getGuardianEmail().equals("null")) {
            String guardianEmail = st.getGuardianEmail().toLowerCase();
            guardianEmail = guardianEmail.substring(0, 1).toUpperCase() + guardianEmail.substring(
                    1);

        }

        studentName = findViewById(R.id.studentName_profile);
        studentName.setText(
                st.getStudentSurname().toUpperCase() + " " + st.getStudentMiddlename().toUpperCase() + " " + st.getStudentFirstname().toUpperCase());


        initialsDisplay = findViewById(R.id.initials_student);
        String studentName = st.getStudentSurname();
        initials = studentName.substring(0, 1).toUpperCase();
        initialsDisplay.setText(initials);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (accessLevel.equals("2")) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.edit_profile:
                Intent intent = new Intent(StudentProfile.this, SudentDetailsEdit.class);
                StudentTable st1 = new StudentTable();
                st1.setStudentSurname(st.getStudentSurname());
                st1.setStudentFirstname(st.getStudentFirstname());
                st1.setStudentMiddlename(st.getStudentMiddlename());
                st1.setStudentLevel(st.getStudentLevel());
                st1.setGuardianName(st.getGuardianName());
                st1.setGuardianPhoneNo(st.getGuardianPhoneNo());
                st1.setGuardianEmail(st.getGuardianEmail());
                st1.setGuardianAddress(st.getGuardianAddress());
                st1.setStudentGender(st.getStudentGender());
                st1.setStudentLevel(st.getStudentLevel());
                st1.setStudentClass(st.getStudentClass());
                st1.setState_of_origin(st.getState_of_origin());
                st1.setStudentReg_no(st.getStudentReg_no());
                st1.setDate_of_birth(st.getDate_of_birth());
                st1.setStudentId(st.getStudentId());
                intent.putExtra("studentObject", st1);
                startActivity(intent);

                return true;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(StudentProfile.this);
                builder.setTitle("Delete ?");
                builder.setPositiveButton("Delete", (dialogInterface, i) -> {
                    String id = st.getStudentId();
                    deleteStudentApiCall(id);
                });
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {

                });
                builder.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteStudentApiCall(final String id) {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(StudentProfile.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String deleteUrl = getString(R.string.base_url) + "/delete.php?id=" + id + "&_db=" + db;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, deleteUrl, response -> {
            dialog1.dismiss();
            Log.i("response", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equals("success")) {
                    DeleteBuilder<StudentTable, Long> deleteBuilder = studentDao.deleteBuilder();
                    deleteBuilder.where().eq("studentId", id);
                    deleteBuilder.delete();
                    Intent intent = new Intent(StudentProfile.this, StudentContacts.class);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {

            QueryBuilder<StudentTable, Long> queryBuilder = studentDao.queryBuilder();
            queryBuilder.where().eq("studentId", st.getStudentId());
            student = queryBuilder.query();

            if (student.get(0).getGuardianPhoneNo().isEmpty()) {
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

            if (student.get(0).getGuardianEmail().isEmpty()) {
                email.setEnabled(false);
                emailIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray),
                        android.graphics.PorterDuff.Mode.SRC_IN);

            }

            if (!student.get(0).getGuardianName().equals("") && !st.getGuardianName().equals(
                    "null")) {
                String guardianName1 = student.get(0).getGuardianName().toLowerCase();
                if (guardianName1.length() > 0) {
                    guardianName1 = guardianName1.substring(0,
                            1).toUpperCase() + guardianName1.substring(1);
                    guardianName.setText(guardianName1);

                }
            } else {
                guardianName.setText("Not Available");
                guardianName.setTextColor(Color.parseColor("#FF0000"));

            }
            if (!student.get(0).getGuardianAddress().equals("") && !student.get(
                    0).getGuardianAddress().equals("null")) {
                String guardianAddress1 = student.get(0).getGuardianAddress().toLowerCase();
                guardianAddress1 = guardianAddress1.substring(0,
                        1).toUpperCase() + guardianAddress1.substring(1);
                guardianAddress.setText(guardianAddress1);
            } else {
                guardianAddress.setText("Not Available");
                guardianAddress.setTextColor(Color.parseColor("#FF0000"));

            }
            if (!student.get(0).getGuardianPhoneNo().equals("") && !student.get(
                    0).getGuardianPhoneNo().equals("null")) {
                guardianPhone.setText(student.get(0).getGuardianPhoneNo());
            } else {
                guardianPhone.setText("Not Available");
                guardianPhone.setTextColor(Color.parseColor("#FF0000"));

            }
            if (!student.get(0).getGuardianEmail().isEmpty() && !student.get(
                    0).getGuardianEmail().equals("null")) {
                String guardianEmail = student.get(0).getGuardianEmail().toLowerCase();
                guardianEmail = guardianEmail.substring(0,
                        1).toUpperCase() + guardianEmail.substring(
                        1);

            }

            studentName = findViewById(R.id.studentName_profile);
            studentName.setText(
                    st.getStudentSurname().toUpperCase() + " " +
                            st.getStudentMiddlename().toUpperCase() + " " +
                            st.getStudentFirstname().toUpperCase()
            );

            initialsDisplay = findViewById(R.id.initials_student);
            String studentName = student.get(0).getStudentSurname();
            initials = studentName.substring(0, 1).toUpperCase();
            initialsDisplay.setText(initials);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


}
