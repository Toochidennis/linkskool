package com.digitaldream.winskool.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.CourseOutlineTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.utils.FilePath;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateVideoLink extends AppCompatActivity {
    Button uploadContent2,save;
    private static final int PICKFILE_RESULT_CODE = 10;
    private static final int PICKFILE_RESULT_CODE2 = 11;
    private static final int REQUEST_PERMISSIONS = 100;
    String fileName1="",fileName2="";
    LinearLayout uploadContent1;

    private String selectedFilePath="", selectedFilePath2="";
    private TextView filepath1,filepath2;
    private Spinner weeks;
    private List<String> weekList;
    private Toolbar toolbar;
    private EditText courseTitle,courseObjective,videoLink;
    private String weekValue;
    private String levelId,courseId,db,user_name,term,teacherId;
    private DatabaseHelper databaseHelper;
    private Dao<CourseOutlineTable,Long> courseOutlineDao;
    private String otherMaterialFile="";
    private ImageView noteImage;
    private String contentId = "";
    private String oldFileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_video_link);
        Intent a = getIntent();
        levelId = a.getStringExtra("levelId");
        courseId = a.getStringExtra("courseId");
        String from = a.getStringExtra("from");

        databaseHelper = new DatabaseHelper(this);
        try {
            courseOutlineDao = DaoManager.createDao(databaseHelper.getConnectionSource(),CourseOutlineTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Upload Video Link");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");
        user_name = sharedPreferences.getString("user","");
        term = sharedPreferences.getString("term","");
        teacherId =sharedPreferences.getString("user_id","");

        uploadContent1 = findViewById(R.id.note_upload);
        //uploadContent2 = findViewById(R.id.other_material);
        noteImage = findViewById(R.id.file_pics);
        filepath2 = findViewById(R.id.other_file_path);
        save = findViewById(R.id.save_course_outline);
        courseTitle = findViewById(R.id.course_title);
        courseObjective = findViewById(R.id.topic_objective);
        videoLink = findViewById(R.id.video_link);
        weeks = findViewById(R.id.weeks);

        weekList = new ArrayList<>();
        for(int i=1;i<30;i++){
            weekList.add("Week "+i);
        }

        ArrayAdapter adapterTerm = new ArrayAdapter(this, R.layout.spinner_item, weekList);
        adapterTerm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weeks.setAdapter(adapterTerm);
        weeks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekValue = String.valueOf(position +1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        uploadContent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(CreateVideoLink.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(CreateVideoLink.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {

                    } else {
                        ActivityCompat.requestPermissions(CreateVideoLink.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
                    Intent intent = new Intent();

                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent,"Choose File to upload") ,REQUEST_PERMISSIONS);
                }
            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToServer();
            }
        });

        if(from.equalsIgnoreCase("edit")){
            String topicTitle = getIntent().getStringExtra("topic");
            String objectiveText = getIntent().getStringExtra("objective");
            String week = getIntent().getStringExtra("week");
            String noteMaterialUrl =getIntent().getStringExtra("note");
            String url = getIntent().getStringExtra("url");
            contentId = getIntent().getStringExtra("id");
            courseTitle.setText(topicTitle);
            courseObjective.setText(objectiveText);
            weeks.setSelection(getIndex(weeks,week));
            videoLink.setText(url);
            String fileExt = "";
            String fileName="";

            try {
                fileName = noteMaterialUrl.substring(noteMaterialUrl.lastIndexOf("/") + 1);
                oldFileName=fileName;
                selectedFilePath = "";
                Log.i("response",fileName1+" "+selectedFilePath);

                fileExt = getExtentionType(fileName);
            }catch (Exception e){
                e.printStackTrace();
            }

            if (fileName != null && !fileName.equals("")) {
                setImage(fileExt,noteImage);

            }


        }
    }

    private int getIndex(Spinner spinner,String week){

        for(int a=0;a<spinner.getCount();a++){
            if( spinner.getItemAtPosition(a).toString().equalsIgnoreCase(week)){
                return a;
            }
        }
        return 0;
    }

    private void saveToServer(){

        if(courseTitle.getText().toString().equals("")){
            Toast.makeText(this,"Title must not be empty",Toast.LENGTH_SHORT).show();

        }else if(courseObjective.getText().toString().equals("")){
            Toast.makeText(this,"Learning objectives must not be empty",Toast.LENGTH_SHORT).show();

        }else if(videoLink.getText().toString().isEmpty()){
            Toast.makeText(this,"Video link is required",Toast.LENGTH_SHORT).show();

        }

        else{
            uploadData();

        }

    }

    private void uploadData() {
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = Login.urlBase+"/addVideoLink.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                dialog1.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equalsIgnoreCase("success"))
                    {
                        JSONObject detailObj = jsonObject.getJSONObject("details");
                        String id = detailObj.getString("id");
                        String title = detailObj.getString("title");
                        String objective = detailObj.getString("description");
                        String week = detailObj.getString("rank");
                        String courseId = detailObj.getString("course_id");
                        String levelId = detailObj.getString("level");
                        String noteMaterialPath = detailObj.getString("url");
                        String otherMatherialPath = detailObj.getString("picref");
                        String type = detailObj.getString("type");

                        QueryBuilder<CourseOutlineTable,Long> queryBuilder = courseOutlineDao.queryBuilder();
                        queryBuilder.where().eq("id",id);
                        List<CourseOutlineTable> cot = queryBuilder.query();
                        if(cot.isEmpty()) {
                            CourseOutlineTable ct = new CourseOutlineTable();
                            ct.setWeek("Week " + week);
                            ct.setObjective(objective);
                            ct.setTitle(title);
                            ct.setType(type);
                            ct.setCourseId(courseId);
                            ct.setLevelId(levelId);
                            ct.setNoteMaterialPath(noteMaterialPath);
                            ct.setOtherMatherialPath(otherMatherialPath);
                            ct.setId(id);

                            courseOutlineDao.create(ct);
                        }else{
                            UpdateBuilder<CourseOutlineTable,Long> updateBuilder = courseOutlineDao.updateBuilder();
                            updateBuilder.updateColumnValue("week","Week "+week);
                            updateBuilder.updateColumnValue("objective",objective);
                            updateBuilder.updateColumnValue("levelId",levelId);
                            updateBuilder.updateColumnValue("courseId",courseId);
                            updateBuilder.updateColumnValue("noteMaterialPath",noteMaterialPath);
                            updateBuilder.updateColumnValue("otherMatherialPath",otherMatherialPath);
                            updateBuilder.updateColumnValue("title",title);
                            updateBuilder.where().eq("id",id);
                            updateBuilder.update();
                        }

                        onBackPressed();
                        Toast.makeText(CreateVideoLink.this,"Upload was successful",Toast.LENGTH_SHORT).show();


                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog1.dismiss();
                Toast.makeText(CreateVideoLink.this,"Operation failed",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",contentId);
                params.put("level",levelId);
                params.put("course",courseId);
                params.put("week",weekValue );
                params.put("title",courseTitle.getText().toString() );
                params.put("objective",courseObjective.getText().toString());
                params.put("note_file",getStringFile(new File(selectedFilePath)));
                params.put("other_file",otherMaterialFile);
                params.put("note_filename",fileName1);
                params.put("other_filename",fileName2);
                params.put("old_filename",oldFileName);
                params.put("video_url",videoLink.getText().toString());
                params.put("_db",db);
                params.put("term",term);
                params.put("author_id",teacherId);
                params.put("author_name",user_name);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile= "", lastVal;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile =  output.toString();
        }
        catch (FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode==REQUEST_PERMISSIONS){
                if(data == null){
                    Toast.makeText(CreateVideoLink.this,"empty",Toast.LENGTH_SHORT).show();
                    return;

                }

                Uri selectedFileUri = data.getData();
                String fileExt = "";
                selectedFilePath = FilePath.getPath(this, selectedFileUri);
                Log.i("response",selectedFilePath);
                try {
                    fileName1 = selectedFilePath.substring(selectedFilePath.lastIndexOf("/") + 1);
                    fileExt = getExtentionType(selectedFilePath);

                }catch (Exception e){
                    Toast.makeText(CreateVideoLink.this,"File can't be uploaded",Toast.LENGTH_SHORT).show();
                }
                Log.i("TAG", "Selected File Path:" + selectedFilePath);

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    setImage(fileExt,noteImage);


                } else {
                    Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private String getExtentionType(String filepath){
        String extension="";
        try {
            extension = filepath.substring(filepath.lastIndexOf("."));
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return extension;


    }

    private void setImage(String extension, ImageView view){
        switch (extension){
            case ".jpg":
                view.setImageResource(R.drawable.image_icon);
                break;
            case ".docx":
                view.setImageResource(R.drawable.doc_icon);
                break;
            case ".pdf":
                view.setImageResource(R.drawable.pdf_icon);
                break;
            case ".jpeg":
                view.setImageResource(R.drawable.image_icon);
                break;
            case ".mp4":
                view.setImageResource(R.drawable.video_icon);
                break;
            case ".doc":
                view.setImageResource(R.drawable.doc_icon);
                break;

            default:
                view.setImageResource(R.drawable.default_icon);
                break;
        }
    }
}
