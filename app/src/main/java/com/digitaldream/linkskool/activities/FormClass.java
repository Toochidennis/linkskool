package com.digitaldream.linkskool.activities;

import android.content.Intent;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitaldream.linkskool.adapters.FormClassAdapter;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.FormClassModel;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.TeachersTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class FormClass extends AppCompatActivity implements FormClassAdapter.OnFormClassClickListener {
    private RecyclerView recyclerView;
    private List<String> formClassList;
    private Toolbar toolbar;
    List<TeachersTable> tch;
    private DatabaseHelper databaseHelper;
    private Dao<TeachersTable,Long> teacherDao;
    private String accessLevel;
    private ImageButton callIcon,smsIcon,emailIcon,whatsappIcon, classIcon,
            coursesIcon;
    private List<TeachersTable> staffList;
    private static String staffId;
    private RelativeLayout call,sms,email,whatsapp,courses,formClass;
    private TextView teacherName;
    private Dao<FormClassModel,Long> formClassDao;
    private List<FormClassModel> formClassLists;
    private FrameLayout emptyState,layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_class);
        toolbar = findViewById(R.id.toolbar);
        callIcon = findViewById(R.id.call_icon);
        smsIcon = findViewById(R.id.sms_icon);
        emailIcon = findViewById(R.id.email_icon);
        whatsappIcon = findViewById(R.id.whatsapp_icon);
        classIcon = findViewById(R.id.class_icon);
        coursesIcon = findViewById(R.id.courses_icon);
        emptyState = findViewById(R.id.form_class_empty_state);
        layout = findViewById(R.id.form_class_unempty_state);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Form Class");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        Intent i = getIntent();
        staffId = i.getStringExtra("staffId");
        String name = i.getStringExtra("name");

        databaseHelper = new DatabaseHelper(this);
        try {
            teacherDao = DaoManager.createDao(databaseHelper.getConnectionSource(),TeachersTable.class);
            formClassDao = DaoManager.createDao(databaseHelper.getConnectionSource(),FormClassModel.class);
            QueryBuilder<TeachersTable,Long> queryBuilder = teacherDao.queryBuilder();
            queryBuilder.where().eq("staffId",staffId);
            tch = queryBuilder.query();
            QueryBuilder<FormClassModel,Long> queryBuilder1 = formClassDao.queryBuilder();
            queryBuilder1.where().eq("staffId",staffId);
            formClassLists = queryBuilder1.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        teacherName = findViewById(R.id.teacherName_profile);

        teacherName.setText(name);

        call = findViewById(R.id.call_teacher_profile);
        sms = findViewById(R.id.sms_teacher_profile);
        whatsapp = findViewById(R.id.whatsapp_teacher_profile);
        email = findViewById(R.id.email_teacher_profile);
        courses = findViewById(R.id.courses);
        formClass = findViewById(R.id.form_class);

        if(tch.get(0).getStaffEmail().isEmpty()){
            email.setEnabled(false);
            emailIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }


        if(tch.get(0).getStaffPhone().isEmpty()) {
            call.setEnabled(false);
            callIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            sms.setEnabled(false);
            smsIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            whatsapp.setEnabled(false);
            whatsappIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);

        }

        call.setOnClickListener(view -> {
            if(!tch.get(0).getStaffPhone().isEmpty()) {
                Intent i1 = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + tch.get(0).getStaffPhone()));
                startActivity(i1);
            }else{
            }
        });

        sms.setOnClickListener(view -> {
            if(!tch.get(0).getStaffPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:" + tch.get(0).getStaffPhone()));
                startActivity(intent);
            }else{
            }
        });

        whatsapp.setOnClickListener(view -> {
            if(!tch.get(0).getStaffPhone().isEmpty()) {
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + "234" + tch.get(0).getStaffPhone() + "&text=" + "");
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(sendIntent);
            }else{
            }
        });

        email.setOnClickListener(view -> {
            if(!tch.get(0).getStaffEmail().isEmpty()) {
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + "subject text" + "&body=" + "body text " + "&to=" + tch.get(0).getStaffEmail());
                emailIntent.setData(data);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });

        formClass.setEnabled(false);
        classIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray),
                android.graphics.PorterDuff.Mode.SRC_IN);

        courses.setOnClickListener(sView -> {
            Intent intent = new Intent(FormClass.this, CoursesAssigned.class);
            intent.putExtra("staffId",staffId);
            intent.putExtra("name",name);
            startActivity(intent);
            finish();
        });


        recyclerView = findViewById(R.id.form_class_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        if(!formClassLists.isEmpty()) {
            emptyState.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            FormClassAdapter adapter = new FormClassAdapter(this, formClassLists, this);
            recyclerView.setAdapter(adapter);
        }else {
            layout.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFormClassClick(int position) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
