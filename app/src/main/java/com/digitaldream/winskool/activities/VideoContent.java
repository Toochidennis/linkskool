package com.digitaldream.winskool.activities;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.digitaldream.winskool.R;
import com.digitaldream.winskool.utils.Comments;
import com.digitaldream.winskool.dialog.CustomDialog;
import com.digitaldream.winskool.config.YoutubeConfig;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.File;

public class VideoContent extends YouTubeBaseActivity {
    YouTubePlayerView mYoutubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    private String url;
    TextView videoTitle;
    private Toolbar toolbar;
    private TextView topicTextview,descTextview,noteTopic,author;
    private LinearLayout downloadBtn;
    private String filePath,user,userId,db;
    private static final int REQUEST_PERMISSIONS2 = 11;
    DownloadManager manager;
    private long enq;
    private CustomDialog progressDialog;
    private String courseId,levelId,contentId;
    private ImageView noteImage;
    private LinearLayout cointainer;
    private TextView title,date;
    private LinearLayout commentsLayout;
    private ImageView sendBtn;
    private EditText commentEdittext;
    private RelativeLayout replyToContainer;
    private ProgressBar progressBar1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_content);

        Intent intent = getIntent();
        String week =intent.getStringExtra("week");
        final String topic = intent.getStringExtra("topic");
        String description = intent.getStringExtra("objective");
        courseId = intent.getStringExtra("courseId");
        levelId = intent.getStringExtra("levelId");
        String id = intent.getStringExtra("id");
        filePath = intent.getStringExtra("note");
        String authorText = getIntent().getStringExtra("author");
        String dateText = getIntent().getStringExtra("date");
        contentId = getIntent().getStringExtra("content_id");
        Log.i("response","filepath: "+filePath);

        progressDialog = new CustomDialog(this);

        mYoutubePlayerView = findViewById(R.id.youtube_player);
        toolbar = findViewById(R.id.toolbar);
        topicTextview = findViewById(R.id.video_title);
        descTextview = findViewById(R.id.video_description);
        title = findViewById(R.id.topic_header);
        title.setText(topic);
        author = findViewById(R.id.author_name);
        date = findViewById(R.id.date_layout);
        date.setText(dateText);
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        user = sharedPreferences.getString("user","Anonymous");
        userId = sharedPreferences.getString("user_id","");
        db = sharedPreferences.getString("db","");
        if(authorText==null || authorText.isEmpty()){
            author.setText("Unknown");

        }else {
            author.setText(authorText);
        }
        //author.setText(user);

        downloadBtn = findViewById(R.id.download_btn1);
        //noteTopic = findViewById(R.id.topic_download);
        noteImage = findViewById(R.id.file_pics);
        cointainer = findViewById(R.id.material_container);
        replyToContainer = findViewById(R.id.replying_to_cont);
        progressBar1 = findViewById(R.id.progress_bar);
        replyToContainer.setVisibility(View.GONE);

        //noteTopic.setText(topic);
        descTextview.setText(description);
        setActionBar(toolbar);
        getActionBar().setTitle(Html.fromHtml("<font color='#000'>"+week+" </font>"));
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        url = intent.getStringExtra("url");

        try {
            url = url.substring(url.lastIndexOf("/")+1);


        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(url);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        mYoutubePlayerView.initialize(YoutubeConfig.getYoutubeApiKey(),onInitializedListener);

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

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(VideoContent.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(VideoContent.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {
                        ActivityCompat.requestPermissions(VideoContent.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS2);

                    } else {
                        ActivityCompat.requestPermissions(VideoContent.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS2);
                    }
                } else {

                    //downloadFile(otherMaterialUrl);
                    downloadFile(filePath);

                }
            }
        });
        try {
            String ext = filePath.substring(filePath.lastIndexOf(".")+1).trim();
            if (ext.isEmpty()) {
                cointainer.setVisibility(View.GONE);

            }
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
            cointainer.setVisibility(View.GONE);

        }

        commentsLayout = findViewById(R.id.comments);
        sendBtn = findViewById(R.id.send);
        //commentEdittext = findViewById(R.id.comment_text);

        Comments cm = new Comments(this);
        cm.fetchComments(db,contentId,progressBar1);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cm.sendComment(levelId,courseId,user,topic,contentId,userId,db);
            }
        });
        try {
            setImage(getExtentionType(filePath), noteImage);
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }

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

            }

        }

    }
    public void downloadFile(String fileUrl) {
        String url = Login.fileBaseUrl+""+fileUrl.substring(3);
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
        //Log.i("filename",fileName);

        File applictionFile =  new File(Environment.
                getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName);
        if(applictionFile.canRead()){
            Toast.makeText(this,"file already exist",Toast.LENGTH_SHORT).show();
            openFile(Uri.parse(Environment.
                    getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName),getMimeType(Environment.
                    getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName));

        }else {
            progressDialog.show();
            //String url = "https://www.androidtutorialpoint.com/wp-content/uploads/2016/09/Beauty.jpg";
            Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show();

            Log.i("filename", fileName);
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
        String extension = filepath.substring(filepath.lastIndexOf("."));
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

            default:
                view.setImageResource(R.drawable.default_icon);
                break;
        }
    }

    private void openFile(Uri file, String mimeType) {
        progressDialog.dismiss();
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


}
