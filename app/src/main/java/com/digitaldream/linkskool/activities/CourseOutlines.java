package com.digitaldream.linkskool.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.dialog.ChooseActionDialog;
import com.digitaldream.linkskool.dialog.CustomDialog;
import com.digitaldream.linkskool.dialog.ShareDialog;
import com.digitaldream.linkskool.models.CourseOutlineTable;
import com.digitaldream.linkskool.utils.FileTransferService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class CourseOutlines extends AppCompatActivity {
    FloatingActionButton addCourseOutline1, addCourseOutline2;
    public static RecyclerView recyclerView;
    public static List<CourseOutlineTable> courseOutlineList;
    public static SectionedRecyclerViewAdapter adapter;
    public static Dao<CourseOutlineTable, Long> courseOutlineDao;
    private DatabaseHelper databaseHelper;
    private Toolbar toolbar;
    public static String courseId, levelId;
    public static FrameLayout layout, emptyLayout;
    public static String accessLevel;
    private SwipeRefreshLayout swipeRefreshLayout1, swipeRefreshLayout2;
    public static String db;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;
    private boolean isWifiP2pEnabled = false;
    private static final int REQUEST_CHECK_SETTINGS = 4;
    public static String courseName, levelName;
    private boolean show = false;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_outlines);
        toolbar = findViewById(R.id.toolbar);

        levelId = getIntent().getStringExtra("levelId");
        courseId = getIntent().getStringExtra("courseId");

        courseName = getIntent().getStringExtra("courseName");
        levelName = getIntent().getStringExtra("levelName");

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("Course Outline");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        addCourseOutline1 = findViewById(R.id.add_course_outline);
        addCourseOutline2 = findViewById(R.id.add_course_outline2);

        accessLevel = sharedPreferences.getString("access_level", "");
        if (accessLevel.equalsIgnoreCase("-1")) {
            addCourseOutline2.setVisibility(View.GONE);
            addCourseOutline1.setVisibility(View.GONE);
        }
        recyclerView = findViewById(R.id.course_outline_recycler);
        layout = findViewById(R.id.courseoutline_unempty_state);
        emptyLayout = findViewById(R.id.course_outline_empty_state);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        courseOutlineList = new ArrayList<>();
        adapter = new SectionedRecyclerViewAdapter();
        if (adapter.getItemCount() > 0) {
            emptyLayout.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
        } else {
            layout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }

        addCourseOutline1.setOnClickListener(v -> {
            ChooseActionDialog dialog = new ChooseActionDialog(CourseOutlines.this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        });

        addCourseOutline2.setOnClickListener(v -> {
            ChooseActionDialog dialog = new ChooseActionDialog(CourseOutlines.this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        });

        databaseHelper = new DatabaseHelper(this);
        swipeRefreshLayout1 = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout2 = findViewById(R.id.swipeRefresh2);

        swipeRefreshLayout1.setOnRefreshListener(() -> {
            show = true;
            refreshCourseOutline();
        });

        swipeRefreshLayout2.setOnRefreshListener(() -> {
            show = true;
            refreshCourseOutline();
        });


        //refreshCourseOutline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        courseOutlineList.clear();
        adapter.removeAllSections();
        try {
            courseOutlineDao = DaoManager.createDao(databaseHelper.getConnectionSource(),
                    CourseOutlineTable.class);
            QueryBuilder<CourseOutlineTable, Long> queryBuilder = courseOutlineDao.queryBuilder();
            queryBuilder.where().eq("levelId", levelId).and().eq("courseId", courseId);
            courseOutlineList = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> tpList = new ArrayList<>();
        tpList.clear();
        Collections.sort(courseOutlineList, (o1, o2) -> {
            int i1 = Integer.parseInt(o1.getWeek().substring(5));
            int i2 = Integer.parseInt(o2.getWeek().substring(5));
            return Integer.compare(i1, i2);
        });
        for (int b = 0; b < courseOutlineList.size(); b++) {
            try {
                List<CourseOutlineTable> courseOutlist = getWeeksTopics(
                        courseOutlineList.get(b).getWeek());
                if (!tpList.contains(courseOutlineList.get(b).getWeek())) {
                    adapter.addSection(
                            new MySection(courseOutlineList.get(b).getWeek(), courseOutlist, this));
                    tpList.add(courseOutlineList.get(b).getWeek());

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 0) {
            emptyLayout.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
        } else {
            layout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    public static List<CourseOutlineTable> getWeeksTopics(String week) throws SQLException {
        QueryBuilder<CourseOutlineTable, Long> queryBuilder = courseOutlineDao.queryBuilder();
        queryBuilder.where().eq("levelId", levelId).and().eq("courseId", courseId).and().eq("week",
                week);
        return queryBuilder.query();
    }

    public static boolean locationServicesEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean net_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            net_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gps_enabled || net_enabled;
    }

    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(
                Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            // wifi is enabled
            return true;
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.discover:
                //displayLocationSettingsRequest(CourseOutlines.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            CourseOutlines.PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
                    // After this point you wait for callback in
                    // onRequestPermissionsResult(int, String[], int[]) overridden method
                } else if (!isWifiEnabled()) {
                    Toast.makeText(CourseOutlines.this, "Turn on wifi connection",
                            Toast.LENGTH_SHORT).show();

                } else if (!locationServicesEnabled(CourseOutlines.this)) {
                    Toast.makeText(CourseOutlines.this, "Turn on location",
                            Toast.LENGTH_SHORT).show();

                } else {
                    ShareDialog dialog = new ShareDialog(CourseOutlines.this);
                    dialog.show();
                }
                return true;
        }
        return false;
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder().addLocationRequest(
                locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                googleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    //Log.i("tag", "All location settings are satisfied.");
                    Toast.makeText(CourseOutlines.this, "Wifi turned on",
                            Toast.LENGTH_SHORT).show();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check
                        // the result
                        // in onActivityResult().
                        status.startResolutionForResult(CourseOutlines.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //Log.e(TAG, "Fine location permission is not granted!");
                    Toast.makeText(CourseOutlines.this, "location permission is not granted!",
                            Toast.LENGTH_SHORT).show();

                } else {
                    ShareDialog dialog = new ShareDialog(CourseOutlines.this);
                    dialog.show();
                }
                break;
            case REQUEST_CHECK_SETTINGS:

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.discover_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spannableString =
                    new SpannableString(menu.getItem(i).getTitle().toString());
            spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
                    spannableString.length(), 0);
            item.setTitle(spannableString);
        }
        return true;

    }


    public static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
// This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(
                    Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        }
        return false;
    }

    private void refreshCourseOutline() {

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        final String username = sharedPreferences.getString("username", "");
        final String password = sharedPreferences.getString("userpassword", "");
        final String pin = sharedPreferences.getString("schoolcode", "");
        String url = Login.urlBase + "/login.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            swipeRefreshLayout1.setRefreshing(false);
            swipeRefreshLayout2.setRefreshing(false);
            Log.i("tag", response);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response);
                JSONArray contentArray = null;
                if (accessLevel.equalsIgnoreCase("-1") || accessLevel.equalsIgnoreCase("2")) {
                    contentArray = jsonObject.getJSONArray("content");
                } else {
                    contentArray = jsonObject.getJSONArray("staffContent");
                }
                if (contentArray.length() > 0) {
                    TableUtils.clearTable(databaseHelper.getConnectionSource(),
                            CourseOutlineTable.class);
                    for (int a = 0; a < contentArray.length(); a++) {
                        JSONObject contentObj = contentArray.getJSONObject(a);
                        String id = contentObj.getString("id");
                        String week = contentObj.getString("rank");
                        String title = contentObj.getString("title");
                        String objective = contentObj.getString("description");
                        String noteMaterialPath = contentObj.getString("url");
                        String otherMatherialPath = contentObj.getString("picref");
                        String levelID = contentObj.getString("level");
                        String courseID = contentObj.getString("course_id");
                        String courseName = contentObj.getString("course_name");
                        String levelName = contentObj.getString("level_name");
                        String type = contentObj.getString("type");
                        String body = contentObj.getString("body");

                        CourseOutlineTable cot = new CourseOutlineTable();
                        cot.setLevelId(levelID);
                        cot.setCourseId(courseID);
                        cot.setTitle(title);
                        cot.setObjective(objective);
                        cot.setWeek("Week " + week);
                        cot.setId(id);
                        cot.setCourseName(courseName);
                        cot.setLevelName(levelName);
                        cot.setNoteMaterialPath(noteMaterialPath);
                        cot.setOtherMatherialPath(otherMatherialPath);
                        cot.setAssessmentUrl(noteMaterialPath);
                        cot.setBody(body);

                        if (type.equals("2")) {
                            cot.setObjective(body);
                            cot.setDuration(objective);
                        }
                        cot.setType(type);
                        courseOutlineDao.create(cot);

                    }
                    QueryBuilder<CourseOutlineTable, Long> queryBuilder =
                            courseOutlineDao.queryBuilder();
                    queryBuilder.where().eq("levelId", levelId).and().eq("courseId", courseId);
                    courseOutlineList = queryBuilder.query();

                    adapter = new SectionedRecyclerViewAdapter();
                    List<String> tpList = new ArrayList<>();
                    tpList.clear();
                    List<Section> list;
                    Collections.sort(courseOutlineList, (o1, o2) -> {
                        int i1 = Integer.parseInt(o1.getWeek().substring(5));
                        int i2 = Integer.parseInt(o2.getWeek().substring(5));
                        return Integer.compare(i1, i2);
                    });
                    adapter.removeAllSections();

                    for (int b = 0; b < courseOutlineList.size(); b++) {
                        try {
                            List<CourseOutlineTable> courseOutlist = getWeeksTopics(
                                    courseOutlineList.get(b).getWeek());

                            if (!tpList.contains(courseOutlineList.get(b).getWeek())) {
                                adapter.addSection(new MySection(courseOutlineList.get(b).getWeek(),
                                        courseOutlist, CourseOutlines.this));
                                tpList.add(courseOutlineList.get(b).getWeek());


                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }

                    if (adapter.getItemCount() > 0) {
                        emptyLayout.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        layout.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                    if (show) {
                        Toast.makeText(CourseOutlines.this, "Course Outline updated",
                                Toast.LENGTH_SHORT).show();
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }, error -> {
            swipeRefreshLayout1.setRefreshing(false);
            swipeRefreshLayout2.setRefreshing(false);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", pin);
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CourseOutlines.this);
        requestQueue.add(stringRequest);
    }

    public static class MySection extends Section {
        String headerTitle;
        public List<CourseOutlineTable> childList;
        public CourseOutlineTable courseOutlineObject;
        private ArrayList<File> files = new ArrayList<>();

        Context context;

        public MySection(String headerTitle, List<CourseOutlineTable> list, Context context) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.course_outlite_item)
                    .headerResourceId(R.layout.header)
                    .build());
            this.headerTitle = headerTitle;
            this.childList = list;
            this.context = context;
        }

        @Override
        public int getContentItemsTotal() {
            return childList.size();
        }


        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            courseOutlineObject = childList.get(position);
            String title = childList.get(position).getTitle();
            title = title.substring(0, 1).toUpperCase() + "" + title.substring(1).toLowerCase();
            itemHolder.topic.setText(title);
            GradientDrawable gd =
                    (GradientDrawable) ((ItemViewHolder) holder).bgColor.getBackground().mutate();
            Random rnd = new Random();
            int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
                    rnd.nextInt(256));
            gd.setColor(currentColor);
            ((ItemViewHolder) holder).bgColor.setBackground(gd);
            /*Random rnd = new Random();
            int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt
            (256));
            itemHolder.bgColor.setBackgroundColor(currentColor);*/
            itemHolder.menuOption.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, itemHolder.menuOption);

                if (childList.get(position).getType().equalsIgnoreCase("1") || childList.get(
                        position).getType().equalsIgnoreCase("null")) {
                    popup.inflate(R.menu.pop_menu_courseoutline);

                } else if (childList.get(position).getType().equalsIgnoreCase("2")) {
                    popup.inflate(R.menu.pop_menu_assessment);
                } else if (childList.get(position).getType().equalsIgnoreCase("3")) {
                    popup.inflate(R.menu.pop_menu_courseoutline);

                } else if (childList.get(position).getType().equals("6")) {
                    popup.inflate(R.menu.pop_menu_courseoutline);
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.delete:
                                final AlertDialog.Builder builder = new AlertDialog.Builder(
                                        context);
                                builder.setTitle("Are you sure you want to delete");
                                builder.setCancelable(false);
                                builder.setNegativeButton("Cancel", (dialog, which) -> {

                                });

                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    String id = childList.get(position).getId();
                                    deleteContent(id);
                                });

                                builder.show();
                                return true;
                            case R.id.edit:

                                if (childList.get(position).getType() == null || childList.get(
                                        position).getType().equalsIgnoreCase(
                                        "null") || childList.get(
                                        position).getType().equalsIgnoreCase("1")) {
                                    Intent intent = new Intent(context, ContentUpload.class);
                                    intent.putExtra("topic", childList.get(position).getTitle());
                                    intent.putExtra("objective",
                                            childList.get(position).getObjective());
                                    intent.putExtra("week", childList.get(position).getWeek());
                                    intent.putExtra("noteMaterialUrl",
                                            childList.get(position).getNoteMaterialPath());
                                    intent.putExtra("otherMaterial",
                                            childList.get(position).getOtherMatherialPath());
                                    intent.putExtra("courseId",
                                            childList.get(position).getCourseId());
                                    intent.putExtra("levelId",
                                            childList.get(position).getLevelId());
                                    intent.putExtra("author", childList.get(position).getAuthor());
                                    intent.putExtra("date", childList.get(position).getDate());
                                    intent.putExtra("content_id", childList.get(position).getId());
                                    intent.putExtra("from", "edit");
                                    context.startActivity(intent);

                                } else if (childList.get(position).getType().equalsIgnoreCase(
                                        "2") ||
                                        childList.get(position).getType().equalsIgnoreCase("6")) {

                                    String id = childList.get(position).getId();
                                    String levelId = childList.get(position).getLevelId();
                                    String courseId = childList.get(position).getCourseId();
                                    String week = childList.get(position).getWeek();
                                    getExam(id, levelId, courseId, week);

                                } else if (childList.get(position).getType().equalsIgnoreCase(
                                        "3")) {
                                    Intent intent = new Intent(context, CreateVideoLink.class);
                                    intent.putExtra("topic", childList.get(position).getTitle());
                                    intent.putExtra("objective",
                                            childList.get(position).getObjective());
                                    intent.putExtra("week", childList.get(position).getWeek());
                                    intent.putExtra("id", childList.get(position).getId());
                                    intent.putExtra("courseId",
                                            childList.get(position).getCourseId());
                                    intent.putExtra("levelId",
                                            childList.get(position).getLevelId());
                                    intent.putExtra("url",
                                            childList.get(position).getNoteMaterialPath());
                                    intent.putExtra("note",
                                            childList.get(position).getOtherMatherialPath());
                                    intent.putExtra("author", childList.get(position).getAuthor());
                                    intent.putExtra("date", childList.get(position).getDate());
                                    intent.putExtra("from", "edit");
                                    context.startActivity(intent);
                                }
                                return true;

                            case R.id.share:
                                if (ShareDialog.info != null) {
                                    String fileUrl = childList.get(position).getNoteMaterialPath();
                                    String fileUrl2 = childList.get(
                                            position).getOtherMatherialPath();
                                    String fileName2 = "";
                                    String fileName = "";
                                    try {
                                        fileName2 = fileUrl2.substring(
                                                fileUrl2.lastIndexOf("/") + 1);
                                        fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

                                    } catch (StringIndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                        fileName2 = "";
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }


                                    File applictionFile = new File(Environment.
                                            getExternalStoragePublicDirectory(Environment
                                                    .DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName);
                                    files.add(applictionFile);
                                    if (!fileName2.isEmpty()) {
                                        File applictionFile2 = new File(Environment.
                                                getExternalStoragePublicDirectory(Environment
                                                        .DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName2);
                                        files.add(applictionFile2);
                                    }
                                    Uri uri = Uri.fromFile(applictionFile);
                                    String f = Environment.
                                            getExternalStoragePublicDirectory(Environment
                                                    .DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName;

                                    Intent serviceIntent = new Intent(context,
                                            FileTransferService.class);
                                    serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
                                    serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, f);
                                    serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_ARRAY,
                                            files);
                                    if (fileName.isEmpty()) {
                                        serviceIntent.putExtra(FileTransferService.TYPE, "json");
                                    } else {
                                        serviceIntent.putExtra(FileTransferService.TYPE, "file");

                                    }
                                    serviceIntent.putExtra(FileTransferService.JSON,
                                            buildJson(courseOutlineObject));
                                    if (ShareDialog.info.groupFormed && !ShareDialog.info.isGroupOwner) {
                                        serviceIntent.putExtra(
                                                FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                                                ShareDialog.info.groupOwnerAddress.getHostAddress());
                                    } else {
                                        serviceIntent.putExtra(
                                                FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                                                ShareDialog.clientIp.substring(1));
                                    }

                                    //Log.i("client",info.groupOwnerAddress.getHostAddress());
                                    serviceIntent.putExtra(
                                            FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
                                    context.startService(serviceIntent);
                                } else {
                                    Toast.makeText(context, "You are not connected to any device",
                                            Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            case R.id.view_responses:
                                Intent intent = new Intent(context, ViewResponses.class);
                                intent.putExtra("id", childList.get(position).getId());
                                intent.putExtra("title", childList.get(position).getTitle());
                                context.startActivity(intent);
                                return true;
                        }
                        return false;
                    }
                });

                popup.show();
            });

            if (CourseOutlines.accessLevel.equalsIgnoreCase("-1")) {
                itemHolder.menuOption.setVisibility(View.GONE);
            }

            if (childList.get(position).getType() == null || childList.get(
                    position).getType().equalsIgnoreCase("1") || childList.get(
                    position).getType().equalsIgnoreCase("null")) {
                itemHolder.icon.setImageResource(R.drawable.ic_library_books);
            } else if (childList.get(position).getType().equalsIgnoreCase("2")) {
                itemHolder.icon.setImageResource(R.drawable.ic_poll);

            } else if (childList.get(position).getType().equalsIgnoreCase("3")) {
                itemHolder.icon.setImageResource(R.drawable.ic_video_library_black_24dp);

            } else if (childList.get(position).getType().equalsIgnoreCase("6")) {
                itemHolder.icon.setImageResource(R.drawable.ic_flash);
            }

            itemHolder.itemView.setOnClickListener(v -> {

                if (childList.get(position).getType() == null || childList.get(
                        position).getType().equalsIgnoreCase("null") || childList.get(
                        position).getType().equalsIgnoreCase("1")) {

                    Intent intent = new Intent(context, ContentDownload.class);
                    intent.putExtra("topic", childList.get(position).getTitle());
                    intent.putExtra("objective", childList.get(position).getObjective());
                    intent.putExtra("week", childList.get(position).getWeek());
                    intent.putExtra("noteMaterialUrl",
                            childList.get(position).getNoteMaterialPath());
                    intent.putExtra("otherMaterial",
                            childList.get(position).getOtherMatherialPath());
                    intent.putExtra("courseId", childList.get(position).getCourseId());
                    intent.putExtra("levelId", childList.get(position).getLevelId());
                    intent.putExtra("author", childList.get(position).getAuthor());
                    intent.putExtra("date", childList.get(position).getDate());
                    intent.putExtra("content_id", childList.get(position).getId());
                    context.startActivity(intent);
                    //((Activity)context).finish();

                } else if (childList.get(position).getType().equalsIgnoreCase("2")) {
                    Intent intent = new Intent(context, Assessment.class);
                    intent.putExtra("topic", childList.get(position).getTitle());
                    intent.putExtra("objective", childList.get(position).getObjective());
                    intent.putExtra("week", childList.get(position).getWeek());
                    intent.putExtra("id", childList.get(position).getId());
                    intent.putExtra("url", childList.get(position).getAssessmentUrl());
                    intent.putExtra("courseId", childList.get(position).getCourseId());
                    intent.putExtra("levelId", childList.get(position).getLevelId());
                    intent.putExtra("body", childList.get(position).getBody());

                    Toast.makeText(layout.getContext(),
                            courseOutlineList.get(position).getCourseName(),
                            Toast.LENGTH_SHORT).show();

                    intent.putExtra("course", childList.get(position).getCourseName());
                    intent.putExtra("duration", childList.get(position).getDuration());
                    intent.putExtra("end_date", childList.get(position).getEndDate());
                    intent.putExtra("start_date", childList.get(position).getStartDate());
                    context.startActivity(intent);

                    //((Activity)context).finish();
                } else if (childList.get(position).getType().equalsIgnoreCase("3")) {
                    Intent intent = new Intent(context, VideoContent.class);
                    intent.putExtra("topic", childList.get(position).getTitle());
                    intent.putExtra("objective", childList.get(position).getObjective());
                    intent.putExtra("week", childList.get(position).getWeek());
                    intent.putExtra("id", childList.get(position).getId());
                    intent.putExtra("courseId", childList.get(position).getCourseId());
                    intent.putExtra("levelId", childList.get(position).getLevelId());
                    intent.putExtra("url", childList.get(position).getNoteMaterialPath());
                    intent.putExtra("note", childList.get(position).getOtherMatherialPath());
                    intent.putExtra("author", childList.get(position).getAuthor());
                    intent.putExtra("date", childList.get(position).getDate());
                    //intent.putExtra("content_id",childList.get(position).getId());
                    context.startActivity(intent);
                    //((Activity)context).finish();

                } else if (childList.get(position).getType().equalsIgnoreCase("6")) {
                    Intent intent = new Intent(context, Flashcard.class);
                    intent.putExtra("title", childList.get(position).getTitle());
                    intent.putExtra("objective", childList.get(position).getObjective());
                    intent.putExtra("week", childList.get(position).getWeek());
                    intent.putExtra("id", childList.get(position).getId());
                    intent.putExtra("date", childList.get(position).getDate());
                    String count = "";
                    try {
                        String url = childList.get(position).getNoteMaterialPath();
                        String[] countArr = url.split(",");
                        count = String.valueOf(countArr.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("count", count);

                    context.startActivity(intent);
                }
            });


        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            String header = headerTitle.substring(0, 1).toUpperCase() + "" + headerTitle.substring(
                    1).toLowerCase();
            headerHolder.headerText.setText(header);

        }

        private static class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView headerText;

            public HeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                headerText = itemView.findViewById(R.id.coursename);

            }
        }

        private static class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView topic;
            LinearLayout menuOption, bgColor;
            ImageView icon;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                topic = itemView.findViewById(R.id.topic);
                menuOption = itemView.findViewById(R.id.comp_bg);
                icon = itemView.findViewById(R.id.lib_book);
                bgColor = itemView.findViewById(R.id.bg_color);
            }

        }

        private void getExam(String id, String levelId, String courseId, String week) {
            final CustomDialog dialog = new CustomDialog((Activity) context);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            String url = Login.urlBase + "/get_exam.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        dialog.dismiss();
                        Log.i("response", response);

                        if (!response.isEmpty()) {

                            Intent intent1 = new Intent(context, TestUpload.class);
                            intent1.putExtra("json", response);
                            intent1.putExtra("from", "edit");
                            intent1.putExtra("courseId", courseId);
                            intent1.putExtra("levelId", levelId);
                            intent1.putExtra("week", week);
                            intent1.putExtra("id", id);
                            intent1.putExtra("levelName", levelName);
                            intent1.putExtra("courseName", courseName);
                            context.startActivity(intent1);
                        }

                    }, error -> {
                        error.printStackTrace();
                        Toast.makeText(context, "Network Error",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> param = new HashMap<>();
                    param.put("_db", db);
                    param.put("id", id);
                    return param;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }


        public void deleteContent(final String id) {

            final ACProgressFlower dialog1 = new ACProgressFlower.Builder(context)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .textMarginTop(10)
                    .fadeColor(Color.DKGRAY).build();
            dialog1.setCancelable(false);
            dialog1.setCanceledOnTouchOutside(false);
            dialog1.show();
            String url = Login.urlBase + "/deleteContent.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("response", response);
                            dialog1.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                if (status.equalsIgnoreCase("success")) {
                                    DeleteBuilder<CourseOutlineTable, Long> deleteBuilder =
                                            courseOutlineDao.deleteBuilder();
                                    deleteBuilder.where().eq("id", id);
                                    deleteBuilder.delete();
                                    QueryBuilder<CourseOutlineTable, Long> queryBuilder =
                                            courseOutlineDao.queryBuilder();
                                    queryBuilder.where().eq("levelId", levelId).and().eq("courseId",
                                            courseId);
                                    courseOutlineList = queryBuilder.query();

                                    adapter = new SectionedRecyclerViewAdapter();
                                    List<String> tpList = new ArrayList<>();
                                    tpList.clear();
                                    List<Section> list;
                                    Collections.sort(courseOutlineList,
                                            (o1, o2) -> o1.getWeek().compareToIgnoreCase(
                                                    o2.getWeek()));
                                    adapter.removeAllSections();

                                    for (int b = 0; b < courseOutlineList.size(); b++) {
                                        try {
                                            List<CourseOutlineTable> courseOutlist = getWeeksTopics(
                                                    courseOutlineList.get(b).getWeek());
                                            if (!tpList.contains(
                                                    courseOutlineList.get(b).getWeek())) {
                                                adapter.addSection(new MySection(
                                                        courseOutlineList.get(b).getWeek(),
                                                        courseOutlist, context));
                                                tpList.add(courseOutlineList.get(b).getWeek());

                                            }

                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (adapter.getItemCount() > 0) {
                                        emptyLayout.setVisibility(View.GONE);
                                        layout.setVisibility(View.VISIBLE);
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        layout.setVisibility(View.GONE);
                                        emptyLayout.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Toast.makeText(context, "Operation failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (SQLException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, error -> {
                error.printStackTrace();
                dialog1.dismiss();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", id);
                    params.put("_db", db);
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);

        }

        public String buildJson(CourseOutlineTable ct) {
            try {
                JSONObject object = new JSONObject();
                object.put("week", ct.getWeek());
                object.put("title", ct.getTitle());
                object.put("objective", ct.getObjective());
                object.put("levelId", ct.getLevelId());
                object.put("courseId", ct.getCourseId());
                object.put("noteMaterialPath", ct.getNoteMaterialPath());
                object.put("otherMaterialPath", ct.getOtherMatherialPath());
                object.put("id", ct.getId());
                object.put("levelName", ct.getLevelName());
                object.put("courseName", ct.getCourseName());
                object.put("startDate", ct.getStartDate());
                object.put("endDate", ct.getEndDate());
                object.put("duration", ct.getDuration());
                object.put("type", ct.getType());
                object.put("assessmentUrl", ct.getAssessmentUrl());
                object.put("json", ct.getJson());
                object.put("author", ct.getAuthor());
                object.put("date", ct.getDate());
                object.put("body", ct.getBody());

                return object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
