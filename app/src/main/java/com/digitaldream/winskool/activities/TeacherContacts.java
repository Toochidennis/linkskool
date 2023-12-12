package com.digitaldream.winskool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.LevelTable;
import com.digitaldream.winskool.models.TeachersTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.adapters.TeacherContactAdapter;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

public class TeacherContacts extends AppCompatActivity implements TeacherContactAdapter.OnTeacherContactClickListener {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<TeachersTable> teachertContactList;
    private Spinner level;
    private List<String> spinnerLevelList,TeachertContactList;
    private Dao<TeachersTable,Long> teacherDao;
    private DatabaseHelper databaseHelper;
    private Dao<LevelTable,Long> levelDao;
    private List<LevelTable> levelNames;
    TeacherContactAdapter teacherContactAdapter;
    private FloatingActionButton addTeacher;
    private SwipeRefreshLayout swipeRefreshLayout,swipeRefreshLayout2;
    private static Menu myMenu;
    private String accessLevel;
    private TextView teachersTotal,teacherText;
    private String db;
    private FrameLayout layout,emptyLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_contacts);

        databaseHelper = new DatabaseHelper(this);
        try {
            teacherDao = DaoManager.createDao(databaseHelper.getConnectionSource(),TeachersTable.class);
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(),LevelTable.class);
            teachertContactList = teacherDao.queryForAll();
            levelNames = levelDao.queryForAll();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");

        toolbar = findViewById(R.id.toolbar);
        teachersTotal = findViewById(R.id.teacher_total);
        teacherText = findViewById(R.id.teacherText);
        layout = findViewById(R.id.teacher_unempty_state);
        emptyLayout = findViewById(R.id.teacher_empty_state);
        teachersTotal.setText(String.valueOf(teachertContactList.size()));
        if(teachertContactList.size()<2){
            teacherText.setText("Teacher");
        }

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Teacher Contacts");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        accessLevel = sharedPreferences.getString("access_level","");


        recyclerView = findViewById(R.id.teacher_contact_recycler);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshTeacher);
        swipeRefreshLayout2 = findViewById(R.id.swipeRefresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTeacherList();
            }
        });
        swipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTeacherList();
            }
        });


        if(!teachertContactList.isEmpty()) {
            emptyLayout.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            teacherContactAdapter = new TeacherContactAdapter(this, teachertContactList, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(teacherContactAdapter);
        }else{
            layout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }

        addTeacher = findViewById(R.id.fab_teacher);
        addTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(accessLevel.equals("2")) {
                    Intent intent = new Intent(TeacherContacts.this, AddTeacher.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(TeacherContacts.this,"You don't have permission to perform this action",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onTeacherContactClick(int position) {
        Intent intent = new Intent(this, TeacherProfile.class);
        TeachersTable tch = new TeachersTable();
        tch.setStaffSurname(teachertContactList.get(position).getStaffSurname());
        tch.setStaffFirstname(teachertContactList.get(position).getStaffFirstname());
        tch.setStaffGender(teachertContactList.get(position).getStaffGender());
        tch.setStaffAddress(teachertContactList.get(position).getStaffAddress());
        tch.setStaffPhone(teachertContactList.get(position).getStaffPhone());
        tch.setStaffNo(teachertContactList.get(position).getStaffNo());
        tch.setStaffStateOrigin(teachertContactList.get(position).getStaffStateOrigin());
        tch.setStaffEmail(teachertContactList.get(position).getStaffEmail());
        tch.setStaffId(teachertContactList.get(position).getStaffId());
        tch.setStaffBirthday(teachertContactList.get(position).getStaffBirthday());
        tch.setStaffId(teachertContactList.get(position).getStaffId());
        tch.setPassword(teachertContactList.get(position).getPassword());
        intent.putExtra("teacherObject",tch);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String emails = "";
        String phoneNumbers ="";
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.email_selection:
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);

                for(int i = 0; i< TeacherContactAdapter.staffEmails.size(); i++){
                    if(TeacherContactAdapter.staffEmails.get(i).isEmpty()){
                        continue;
                    }
                    emails= emails+""+ TeacherContactAdapter.staffEmails.get(i)+",";
                }
                try {
                    emails = emails.substring(0, emails.length() - 1);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Uri data = Uri.parse("mailto:?subject=" + "subject text"+ "&body=" + "body text " + "&to="+emails);
                emailIntent.setData(data);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                break;
            case R.id.sms_selection:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                for(int i = 0; i<TeacherContactAdapter.staffPhones.size(); i++){
                    if(TeacherContactAdapter.staffPhones.get(i).isEmpty()){
                        continue;
                    }
                    phoneNumbers= phoneNumbers+""+TeacherContactAdapter.staffPhones.get(i)+",";
                }
                try {
                    phoneNumbers = phoneNumbers.substring(0, phoneNumbers.length() - 1);
                }catch (Exception e){
                    e.printStackTrace();
                }
                intent.setData(Uri.parse("smsto:"+phoneNumbers ));
                startActivity(intent);
                break;
            case R.id.select_all:
                TeacherContactAdapter.flagValue = true;
                myMenu.findItem(R.id.select_all).setVisible(false);
                teacherContactAdapter = new TeacherContactAdapter(TeacherContacts.this, teachertContactList, TeacherContacts.this);
                recyclerView.setAdapter(teacherContactAdapter);
                teacherContactAdapter.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myMenu = menu;
        if(teachertContactList.size()==TeacherContactAdapter.staffPhones.size()){
            getMenuInflater().inflate(R.menu.selection_menu, menu);
            menu.findItem(R.id.select_all).setVisible(false);

        }
        else if(TeacherContactAdapter.staffPhones.size()>0) {
            getMenuInflater().inflate(R.menu.selection_menu, menu);

        }
        else {
            getSupportActionBar().setTitle("Teacher Contacts");
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(TeacherContactAdapter.flagValue){
            TeacherContactAdapter.staffPhones.clear();
            TeacherContactAdapter.staffEmails.clear();
            TeacherContactAdapter.flagValue =false;
            teacherContactAdapter.notifyDataSetChanged();
            invalidateOptionsMenu();
        }else if(TeacherContactAdapter.selectedCounter >0){
            TeacherContactAdapter.staffPhones.clear();
            TeacherContactAdapter.staffEmails.clear();
            teacherContactAdapter.notifyDataSetChanged();
            TeacherContactAdapter.selectedCounter =0;
            invalidateOptionsMenu();

        }
        else {
            super.onBackPressed();
        }
    }

    private void refreshTeacherList(){

        String login_url = Login.urlBase+"/allStaff.php?_db="+db;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                try {

                    JSONObject jsonObject = new JSONObject(response.toLowerCase());
                    if(jsonObject.has("allstaffrecords")) {
                        JSONObject object1 = jsonObject.getJSONObject("allstaffrecords");
                        JSONArray rowsArray = object1.getJSONArray("rows");
                        if (rowsArray.length() > 0) {
                            TableUtils.clearTable(databaseHelper.getConnectionSource(), TeachersTable.class);


                            int teacherCounter = 0;
                            for (int i = 0; i < rowsArray.length(); i++) {
                                JSONArray jsonArray3 = rowsArray.getJSONArray(i);
                                String id = jsonArray3.getString(0);
                                String staffFirstName = jsonArray3.getString(3);
                                String staffSurname = jsonArray3.getString(2);
                                String staffMiddlename = jsonArray3.getString(4);
                                String staffGender = jsonArray3.getString(5);
                                String staffAddress = jsonArray3.getString(7);
                                String nationality = jsonArray3.getString(18);
                                String phone = jsonArray3.getString(11);
                                String staffEmail = jsonArray3.getString(12);
                                String birthdate = jsonArray3.getString(6);
                                String staffNo = jsonArray3.getString(13);
                                String staffLga = jsonArray3.getString(16);
                                String staffStateOrigin = jsonArray3.getString(17);
                                String staffAccessLevel = jsonArray3.getString(41);
                                QueryBuilder<TeachersTable, Long> queryBuilder = teacherDao.queryBuilder();
                                queryBuilder.where().eq("staffId", id);
                                List<TeachersTable> teacher = queryBuilder.query();
                                if (teacher.isEmpty()) {
                                    teacherCounter++;
                                    TeachersTable tch = new TeachersTable();
                                    tch.setStaffId(id);
                                    tch.setStaffFirstname(staffFirstName);
                                    tch.setStaffSurname(staffSurname);
                                    tch.setStaffMiddlename(staffMiddlename);
                                    tch.setStaffGender(staffGender);
                                    tch.setStaffAddress(staffAddress);
                                    tch.setStaffNationality(nationality);
                                    tch.setStaffBirthday(birthdate);
                                    tch.setStaffPhone(phone);
                                    tch.setStaffEmail(staffEmail);
                                    tch.setStaffNo(staffNo);
                                    tch.setStaffLGA(staffLga);
                                    tch.setStaffStateOrigin(staffStateOrigin);
                                    tch.setStaffAccessLevel(staffAccessLevel);
                                    tch.setId(teacherCounter);

                                    teacherDao.create(tch);
                                }
                            }
                            teachertContactList = teacherDao.queryForAll();
                            if(!teachertContactList.isEmpty()) {
                                emptyLayout.setVisibility(View.GONE);
                                layout.setVisibility(View.VISIBLE);
                                teacherContactAdapter = new TeacherContactAdapter(TeacherContacts.this, teachertContactList, TeacherContacts.this);
                                teacherContactAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(teacherContactAdapter);
                            }else{
                                layout.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                            }
                            teachersTotal.setText(String.valueOf(teachertContactList.size()));
                            Toast.makeText(TeacherContacts.this, "List updated", Toast.LENGTH_SHORT).show();

                        }
                    }




                    } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout2.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(TeacherContacts.this,"Error: "+error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public static Menu getMenu()
    {
        return myMenu;
    }
}
