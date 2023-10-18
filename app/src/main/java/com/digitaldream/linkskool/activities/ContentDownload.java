package com.digitaldream.linkskool.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.config.DatabaseHelper;
import com.digitaldream.linkskool.models.CommentTable;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.utils.Comments;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ContentDownload extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView topic,objective,topicHeader,otherTopicHeader,body;
    private LinearLayout downloadBtn1,downloadBtn2;
    private String noteMaterialUrl,otherMaterialUrl;
    private ImageView noteImage,otherImage;
    private static final int PICKFILE_RESULT_CODE = 10;
    private static final int REQUEST_PERMISSIONS2 = 11;
    private static final int REQUEST_PERMISSIONS = 100;
    DownloadManager manager;
    private long enq;
    ACProgressFlower progressBar;
    private String courseId,levelId;
    private RelativeLayout otherNoteContainer;
    private TextView author,date;
    private LinearLayout commentsLayout;
    private ImageView sendBtn;
    private EditText commentEdittext;
    private ScrollView scrollView;
    private boolean isReply = false;
    //private int position = 0;
    private int commentPosition,replyPosition;
    List<CommentTable> commentModelList = new ArrayList<>();
    List<Integer> replyPositionList = new ArrayList<>();

    private String userId, user,contentId,topicTitle,comment,db;
    private Dao<CommentTable,Long> commentDao;
    private DatabaseHelper databaseHelper;
    private RelativeLayout replyToContainer,noteContainer;
    private ProgressBar progressBar1;
    private String comment_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_download);
        toolbar = findViewById(R.id.toolbar);
        databaseHelper = new DatabaseHelper(this);
        try {
            commentDao = DaoManager.createDao(databaseHelper.getConnectionSource(),CommentTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        topicTitle = getIntent().getStringExtra("topic");
        String objectiveText = getIntent().getStringExtra("objective");
        String week = getIntent().getStringExtra("week");
        noteMaterialUrl =getIntent().getStringExtra("noteMaterialUrl");
        otherMaterialUrl = getIntent().getStringExtra("otherMaterial");
        courseId = getIntent().getStringExtra("courseId");
        levelId = getIntent().getStringExtra("levelId");
        contentId = getIntent().getStringExtra("content_id");
        String dateText = getIntent().getStringExtra("date");
        String authorText = getIntent().getStringExtra("author");
        String bodyText = getIntent().getStringExtra("body");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(week);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        downloadBtn1 = findViewById(R.id.download_btn1);
        downloadBtn2 = findViewById(R.id.download_btn2);
        otherNoteContainer = findViewById(R.id.other_note);
        noteContainer = findViewById(R.id.note_container);
        replyToContainer = findViewById(R.id.replying_to_cont);
        progressBar1 = findViewById(R.id.progress_bar);
        replyToContainer.setVisibility(View.GONE);
        scrollView = findViewById(R.id.scroll);
        body = findViewById(R.id.body);
        try {

            String f = otherMaterialUrl.substring(otherMaterialUrl.lastIndexOf("/"));
            if (otherMaterialUrl.isEmpty() || f.substring(f.lastIndexOf(".")).isEmpty()) {
                otherNoteContainer.setVisibility(View.GONE);
            }
        }catch (StringIndexOutOfBoundsException e){
            otherNoteContainer.setVisibility(View.GONE);
        }

        try{
        String f1 = noteMaterialUrl.substring(noteMaterialUrl.lastIndexOf("/"));
        if (noteMaterialUrl.isEmpty() || f1.substring(f1.lastIndexOf(".")).isEmpty()) {
            noteContainer.setVisibility(View.GONE);
        }
    }catch (StringIndexOutOfBoundsException e){
        noteContainer.setVisibility(View.GONE);
    }



        //if(bodyText.isEmpty()){
            body.setVisibility(View.GONE);
        //}


        progressBar = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
         progressBar.setCancelable(false);
        progressBar.setCanceledOnTouchOutside(false);

        topic = findViewById(R.id.topic_download_header);
        objective = findViewById(R.id.objective_download);
        //topicHeader = findViewById(R.id.topic_download);
        author = findViewById(R.id.author_name);
        date = findViewById(R.id.date_layout);
        commentsLayout = findViewById(R.id.comments);
        sendBtn = findViewById(R.id.send);
        commentEdittext = findViewById(R.id.comment_text1);
        //otherTopicHeader = findViewById(R.id.topic_other_download);
        //downloadBtn2 = findViewById(R.id.other_material_download);
        noteImage = findViewById(R.id.file_pics);
        otherImage = findViewById(R.id.file_pics2);

        setImage(getExtentionType(noteMaterialUrl),noteImage);

        setImage(getExtentionType(otherMaterialUrl),otherImage);
        if(authorText == null || authorText.equalsIgnoreCase("null") || authorText.isEmpty()){
            author.setText("Admin");

        }else {
            author.setText(authorText);
        }
        date.setText(dateText);

        topic.setText(topicTitle.toUpperCase());
        objective.setText(objectiveText.toUpperCase());
        //topicHeader.setText(topicTitle.toUpperCase());
        //otherTopicHeader.setText(topicTitle.toUpperCase());

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enq);
                    manager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                    Cursor c = manager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            //TODO : Use this local uri and launch intent to open file
                            openFile(Uri.parse(uriString),getMimeType(uriString));

                        }
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        user = sharedPreferences.getString("user","Anonymous");
        userId = sharedPreferences.getString("user_id","");
        db = sharedPreferences.getString("db","");

        //fetchComments();
        Comments cm = new Comments(this);
        cm.fetchComments(db,contentId,progressBar1);
        downloadBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(ContentDownload.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(ContentDownload.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {
                        ActivityCompat.requestPermissions(ContentDownload.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);

                    } else {
                        ActivityCompat.requestPermissions(ContentDownload.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
                    downloadFile(noteMaterialUrl);

                }
            }
        });

        downloadBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(ContentDownload.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(ContentDownload.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {
                        ActivityCompat.requestPermissions(ContentDownload.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS2);

                    } else {
                        ActivityCompat.requestPermissions(ContentDownload.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS2);
                    }
                } else {

                    downloadFile(otherMaterialUrl);

                }

            }
        });




        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                comment = commentEdittext.getText().toString();
                LinearLayout q = findViewById(R.id.comments);
                if (!comment.isEmpty()) {

                    if (!isReply) {
                        LayoutInflater layoutInflater = LayoutInflater.from(ContentDownload.this);
                        View view = layoutInflater.inflate(R.layout.comment_layout, commentsLayout, false);
                        TextView commentText = view.findViewById(R.id.comment_text);
                        TextView user_name = view.findViewById(R.id.user);
                        TextView reply = view.findViewById(R.id.reply);
                        TextView initial = view.findViewById(R.id.initials);
                        TextView date = view.findViewById(R.id.date);
                        TextView commentId = view.findViewById(R.id.comment_id);
                        LinearLayout initialBg = view.findViewById(R.id.initials_cont);
                        String  currentTime = DateFormat.getDateTimeInstance()
                                .format(new Date());
                        date.setText(currentTime);

                        commentEdittext.setText("");
                        reply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //InputMethodManager inputMethodManager = (InputMethodManager)getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                //inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                isReply = true;
                                replyToContainer.setVisibility(View.VISIBLE);
                                TextView replyTo = findViewById(R.id.replying_to);
                                TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.user);

                                ImageView clear = findViewById(R.id.clear);
                                clear.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        replyToContainer.setVisibility(View.GONE);
                                    }
                                });

                                commentEdittext.setText("");
                                CommentTable ctm = new CommentTable();
                                //commentPosition = ctm.getPosition();
                                commentPosition = q.indexOfChild(view);
                                TextView commentId = view.findViewById(R.id.comment_id);
                                comment_id = commentId.getText().toString();
                                replyTo.setText(commentModelList.get(commentPosition).getUsername());


                            }
                        });
                        commentText.setText(comment);
                        if (user.equalsIgnoreCase("null")) {
                            user_name.setText("Admin");
                            initial.setText("A");

                        } else {
                            user_name.setText(user);
                            String i = user.substring(0, 1).toUpperCase();
                            initial.setText(i);

                        }
                        GradientDrawable gd = (GradientDrawable) initialBg.getBackground().mutate();
                        Random rnd = new Random();
                        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        gd.setColor(currentColor);
                        //gd.invalidateSelf();
                        initialBg.setBackground(gd);
                        q.addView(view);
                        commentPosition = q.indexOfChild(view);

                        sendCommentToServer("0");
                    } else {
                        isReply = false;
                        replyToContainer.setVisibility(View.GONE);


                        //ImageView clear = findViewById(R.id.clear);

                        LayoutInflater inflater = LayoutInflater.from(ContentDownload.this);
                        LinearLayout l = q.getChildAt(commentPosition).findViewById(R.id.replies);
                        View vi = inflater.inflate(R.layout.reply_layout, l, false);
                        TextView commentText = vi.findViewById(R.id.reply_text);
                        TextView user_name = vi.findViewById(R.id.user);
                        TextView initial = vi.findViewById(R.id.initials);
                        TextView reply = vi.findViewById(R.id.reply);
                        TextView date = vi.findViewById(R.id.date);
                        String  currentDateTime = DateFormat.getDateTimeInstance()
                                .format(new Date());
                        date.setText(currentDateTime);
                        reply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isReply = true;
                                replyToContainer.setVisibility(View.VISIBLE);
                                TextView replyTo = findViewById(R.id.replying_to);
                                TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.user);
                                //replyPosition = l.indexOfChild(vi);
                                //Log.i("replyPosition", String.valueOf(replyPosition));
                                ImageView clear = findViewById(R.id.clear);
                                clear.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        replyToContainer.setVisibility(View.GONE);
                                    }
                                });

                                commentEdittext.setText("reply");
                                CommentTable ctm = new CommentTable();
                                //commentPosition = ctm.getPosition();
                                //commentPosition = q.indexOfChild(view);
                                replyTo.setText(commentModelList.get(commentPosition).getUsername());
                                commentEdittext.setText("");
                                Toast.makeText(ContentDownload.this,commentModelList.get(commentPosition).getComment_id(),Toast.LENGTH_SHORT).show();


                            }
                        });
                        commentText.setText(commentEdittext.getText().toString());
                        if (user.equalsIgnoreCase("null")) {
                            user_name.setText("Admin");
                            initial.setText("A");

                        } else {
                            user_name.setText(user);
                            String i = user.substring(0, 1).toUpperCase();
                            initial.setText(i);
                        }
                        l.addView(vi);
                        //commentPosition = q.indexOfChild(view);
                        String commentId = commentModelList.get(commentPosition).getComment_id();
                        //Log.i("response", commentId);
                        replyPosition = l.indexOfChild(vi);
                        //Log.i("reply2", comment_id);
                        sendCommentToServer(commentId);
                    }
                }
                if (!isReply) {
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }

                commentEdittext.setText("");
                */
               //Comments cm = new Comments(ContentDownload.this);
               cm.sendComment(levelId,courseId,user,topicTitle,contentId,userId,db);

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode==REQUEST_PERMISSIONS2){
                downloadFile(otherMaterialUrl);

            }



            if(requestCode==REQUEST_PERMISSIONS){
                downloadFile(noteMaterialUrl);

            }
            }

        }

    public void downloadFile(String fileUrl) {
        String url = Login.fileBaseUrl+""+fileUrl.substring(3);
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);

        File applictionFile =  new File(Environment.
                getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName);
        File applicationFile1 = new File( getExternalFilesDir("received")+"/"+fileName);
        if(applictionFile.canRead() ){
            Toast.makeText(this,"file already exist",Toast.LENGTH_SHORT).show();
            openFile(Uri.parse(Environment.
                    getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName),getMimeType(Environment.
                    getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName));

        }else if(applicationFile1.canRead()){
            Toast.makeText(this,"file already exist",Toast.LENGTH_SHORT).show();
            openFile(Uri.parse(getExternalFilesDir("received")+"/"+fileName),getMimeType(getExternalFilesDir("received")+"/"+fileName));
        }
        else {
            progressBar.show();
            Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show();

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            //request.setDescription("Some descrition");
            request.setTitle(fileName);
// in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

// get download service and enqueue file
            manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            enq = manager.enqueue(request);
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

    private void setImage(String extension,ImageView view){
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

    private void openFile(Uri file, String mimeType) {
        progressBar.dismiss();
        File f = new File(file.getPath());
        Intent openFile = new Intent(Intent.ACTION_VIEW);
        openFile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //openFile.setData(file);
        Uri apkURI = FileProvider.getUriForFile(
                this,
                getApplicationContext()
                        .getPackageName() + ".provider", f);
        openFile.setDataAndType(apkURI, mimeType);
        openFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(openFile);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this,"Cannot open file.",Toast.LENGTH_SHORT).show();
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }





    private void sendCommentToServer(String parent){
        String  currentDateTime = DateFormat.getDateTimeInstance()
                .format(new Date());

        String url = Login.urlBase+"/addComment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equalsIgnoreCase("success")){


                        JSONObject detailObj = jsonObject.getJSONObject("details");
                        String commentId = detailObj.getString("id");
                        String parent = detailObj.getString("parent");
                        String author = detailObj.getString("author_name");
                        CommentTable comment = new CommentTable();
                        comment.setComment_id(commentId);


                        if(author.equalsIgnoreCase("null")){
                            comment.setUsername("Admin");

                        }else {
                            comment.setUsername(author);
                        }
                        if(parent.equalsIgnoreCase("0")) {
                            commentModelList.add(comment);
                            LinearLayout q = findViewById(R.id.comments);
                            TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.reply);
                            TextView commentId1 = q.getChildAt(commentPosition).findViewById(R.id.comment_id);
                            commentId1.setText(commentId);
                            l1.setText("Reply");

                        }else{
                            LinearLayout q = findViewById(R.id.comments);
                            LinearLayout l = q.getChildAt(commentPosition).findViewById(R.id.replies);
                            TextView reply = l.getChildAt(replyPosition).findViewById(R.id.reply);
                            reply.setText("Reply");
                        }


                    }else{
                        LinearLayout q = findViewById(R.id.comments);
                        TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.reply);
                        l1.setText("Fail");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ContentDownload.this,"Check your connection and try again",Toast.LENGTH_SHORT).show();
                /*LinearLayout q = findViewById(R.id.comments);
                TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.reply);
                l1.setText("Fail");*/
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("level",levelId);
                params.put("course",courseId);
                params.put("comment",comment);
                params.put("parent",parent);
                params.put("author_name",user);
                params.put("content_title",topicTitle);
                params.put("content_id",contentId);
                params.put("date",currentDateTime);
                params.put("author_id",userId);
                params.put("_db",db);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(ContentDownload.this);
        requestQueue.add(stringRequest);
    }


    private void fetchComments(){

        String url = Login.urlBase+"/getComment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("json","response"+response);
                progressBar1.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    createParent(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("json","error"+error.getMessage());
                progressBar1.setVisibility(View.GONE);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("_db",db);
                params.put("id",contentId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void createParent(JSONObject jsonObject){
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("0");
            for(int a=0; a<jsonArray.length();a++){

                JSONObject jsonObject1 = jsonArray.getJSONObject(a);
                String id = jsonObject1.getString("id");
                String comment = jsonObject1.getString("body");
                String user = jsonObject1.getString("author_name");
                String dateText = jsonObject1.getString("upload_date");
                LinearLayout q = findViewById(R.id.comments);

                CommentTable commentObject = new CommentTable();
                commentObject.setComment_id(id);
                if(user.equalsIgnoreCase("null")){
                    commentObject.setUsername("Admin");

                }else {
                    commentObject.setUsername(user);
                }
                commentModelList.add(commentObject);

                LayoutInflater layoutInflater = LayoutInflater.from(ContentDownload.this);
                View view = layoutInflater.inflate(R.layout.comment_layout, q, false);
                TextView commentText = view.findViewById(R.id.comment_text);
                TextView user_name = view.findViewById(R.id.user);
                TextView reply = view.findViewById(R.id.reply);
                TextView initial = view.findViewById(R.id.initials);
                TextView date = view.findViewById(R.id.date_layout);
                LinearLayout initialBg = view.findViewById(R.id.initials_cont);
                TextView commentId1 = view.findViewById(R.id.comment_id);
                LinearLayout reply_layout=view.findViewById(R.id.replies);
                commentId1.setText(id);
                reply.setText("Reply");
                date.setText(dateText);

                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //InputMethodManager inputMethodManager = (InputMethodManager)getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        //inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        isReply = true;
                        replyToContainer.setVisibility(View.VISIBLE);
                        TextView replyTo = findViewById(R.id.replying_to);
                        TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.user);

                        ImageView clear = findViewById(R.id.clear);
                        clear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                replyToContainer.setVisibility(View.GONE);
                            }
                        });

                        commentEdittext.setText("");
                        CommentTable ctm = new CommentTable();
                        //commentPosition = ctm.getPosition();
                        commentPosition = q.indexOfChild(view);
                        int position = commentPosition;
                        replyTo.setText(commentModelList.get(commentPosition).getUsername());


                    }
                });
                commentText.setText(comment);
                if (user.equalsIgnoreCase("null")) {
                    user_name.setText("Admin");
                    initial.setText("A");

                } else {
                    user_name.setText(user);
                    String i = user.substring(0, 1).toUpperCase();
                    initial.setText(i);

                }
                GradientDrawable gd = (GradientDrawable) initialBg.getBackground().mutate();
                Random rnd = new Random();
                int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                gd.setColor(currentColor);
                //gd.invalidateSelf();
                initialBg.setBackground(gd);
                q.addView(view);
                commentPosition = q.indexOfChild(view);
                int position = commentPosition;
                createReply(id,jsonObject,reply_layout,position);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void createReply(String commentId, JSONObject jsonObject, LinearLayout l,int position){

        if(jsonObject.has(commentId)){
            LinearLayout q = findViewById(R.id.comments);

            try {
                JSONArray jsonArray = jsonObject.getJSONArray(commentId);
                for(int b=0;b<jsonArray.length();b++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(b);
                    String id = jsonObject1.getString("id");
                    String comment = jsonObject1.getString("body");
                    String user = jsonObject1.getString("author_name");
                    String dateText = jsonObject1.getString("upload_date");
                    String parent = jsonObject1.getString("parent");
                    if(!commentId.equalsIgnoreCase(parent)){
                        return;
                    }

                    LayoutInflater inflater = LayoutInflater.from(ContentDownload.this);
                    LinearLayout l1 = q.getChildAt(commentPosition).findViewById(R.id.replies);
                    View vi = inflater.inflate(R.layout.reply_layout, l, false);
                    vi.setTag(position);
                    TextView commentText = vi.findViewById(R.id.reply_text);
                    TextView user_name = vi.findViewById(R.id.user);
                    TextView initial = vi.findViewById(R.id.initials);
                    TextView reply = vi.findViewById(R.id.reply);
                    reply.setText("Reply");
                    TextView date = vi.findViewById(R.id.date_layout);

                    date.setText(dateText);
                    reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isReply = true;
                            replyToContainer.setVisibility(View.VISIBLE);
                            TextView replyTo = findViewById(R.id.replying_to);
                            //TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.user);
                            //replyPosition = l.indexOfChild(vi);
                            //Log.i("replyPosition", String.valueOf(replyPosition));
                            ImageView clear = findViewById(R.id.clear);
                            clear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    replyToContainer.setVisibility(View.GONE);
                                }
                            });

                            commentEdittext.setText("reply");
                            CommentTable ctm = new CommentTable();
                            //commentPosition = ctm.getPosition();
                            //commentPosition = l.indexOfChild(vi);
                            commentPosition = position;
                            replyTo.setText(commentModelList.get(commentPosition).getUsername());
                            commentEdittext.setText("");
                            Toast.makeText(ContentDownload.this,String.valueOf(vi.getTag()),Toast.LENGTH_SHORT).show();

                        }
                    });
                    commentText.setText(comment);
                    if (user.equalsIgnoreCase("null")) {
                        user_name.setText("Admin");
                        initial.setText("A");

                    } else {
                        user_name.setText(user);
                        String i = user.substring(0, 1).toUpperCase();
                        initial.setText(i);
                    }
                    l.addView(vi);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }













   /* boolean isKeyboardShowing = false;
    void onKeyboardVisibilityChanged(boolean opened) {
        print("keyboard " + opened);
    }

// ContentView is the root view of the layout of this activity/fragment
contentView.getViewTreeObserver().addOnGlobalLayoutListener(
    new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {

            Rect r = new Rect();
            contentView.getWindowVisibleDisplayFrame(r);
            int screenHeight = contentView.getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;

            //Log.d(TAG, "keypadHeight = " + keypadHeight);

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true;
                    onKeyboardVisibilityChanged(true);
                }
            }
            else {
                // keyboard is closed
                if (isKeyboardShowing) {
                    isKeyboardShowing = false;
                    onKeyboardVisibilityChanged(false);
                }
            }
        }
    });
    */
}
