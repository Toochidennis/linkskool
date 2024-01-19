package com.digitaldream.linkskool.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.adapters.AnswerAdapter;
import com.digitaldream.linkskool.models.AnswerModel;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.QAObject;
//import com.digitaldream.linkskool.utils.AnswerBottomSheet;
import com.digitaldream.linkskool.utils.RefreshListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NewsView extends AppCompatActivity implements RefreshListener, AnswerAdapter.OnAnswerClickListener {
    public static QAObject feed;
    public  static String title;
    private RecyclerView recyclerView;
    private List<AnswerModel> answerList;
    private String db;
    AnswerAdapter adapter;
    ProgressBar progressBar;
    private TextView emptyStateTXT;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("News");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        Intent intent = getIntent();
        feed = (QAObject) intent.getSerializableExtra("feed");
        title = feed.getQuestion().trim();
        String date = feed.getDate();
        try {
            title = title.substring(0,1).toUpperCase()+""+title.substring(1);
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        String content1 = feed.getAnswer();
        TextView titleTXT = findViewById(R.id.news_title);
        TextView dateTXT = findViewById(R.id.date_layout);
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");

        try {
            Date d = simpleDateFormat.parse(date);
            String niceDateStr = String.valueOf(DateUtils.getRelativeTimeSpanString(d.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));
            dateTXT.setText(niceDateStr);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        titleTXT.setText(title);
        TextView authorTXT = findViewById(R.id.author);
        emptyStateTXT = findViewById(R.id.empty_text);

        String author = feed.getUser();
        try {
            author = author.substring(0,1).toUpperCase()+""+author.substring(1);
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        if(author.equals("null")){
            authorTXT.setText("Posted by Admin");

        }else{
            authorTXT.setText("Posted by "+author);

        }
        LinearLayout layout = findViewById(R.id.news_body);
        LayoutInflater inflater = LayoutInflater.from(this);
        try {
            JSONArray array = new JSONArray(content1);
            for (int b=0;b<array.length();b++){
                JSONObject jsonObject = array.getJSONObject(b);
                String type = jsonObject.getString("type");
                View view = null;
                if(type.equals("text")){
                    String content = jsonObject.getString("content");
                    view = inflater.inflate(R.layout.text_layout,layout,false);
                    TextView t = view.findViewById(R.id.text);
                    t.setText(content.substring(0,1).toUpperCase()+""+content.substring(1).toLowerCase());
                }else if(type.equals("image")){
                    String src = jsonObject.getString("src");
                    src = src.substring(2);
                    Log.i("response",src);
                    view = inflater.inflate(R.layout.image_layout,layout,false);
                    ImageView img = view.findViewById(R.id.image);
                    //img.setImageResource(R.drawable.splashscreen);
                    Picasso.get().load(Login.fileBaseUrl+""+src).into(img);
                }
                layout.addView(view);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        EditText replyEDT = findViewById(R.id.reply);
        replyEDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feed.setQuestion(feed.getAnswer());
                FragmentTransaction transaction = ((FragmentActivity) NewsView.this)
                        .getSupportFragmentManager()
                        .beginTransaction();
                /*AnswerBottomSheet answerBottomSheet = AnswerBottomSheet.newInstance("news");
                answerBottomSheet.show(transaction, "answerBottomSheet");
                answerBottomSheet.DismissListener(NewsView.this);*/
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");

        recyclerView = findViewById(R.id.answer_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        answerList = new ArrayList<>();

        adapter = new AnswerAdapter(this,answerList,this);
        recyclerView.setAdapter(adapter);
        progressBar = findViewById(R.id.progress);
        fetchComments(db,feed.getId(),progressBar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    @Override
    public void onRefresh(DialogInterface dialog) {
        fetchComments(db,feed.getId(),progressBar);
    }

    public void fetchComments(String db, String contentId, ProgressBar progressBar1) {
        progressBar1.setVisibility(View.VISIBLE);
        String url = Login.urlBase + "/getComment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar1.setVisibility(View.GONE);
                Log.i("response",response);
                if(!response.isEmpty()) {
                    answerList.clear();

                    try {
                        JSONObject object = new JSONObject(response);
                        Iterator<String> keys = object.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONArray jsonArray = object.optJSONArray(key);
                            for (int a = 0; a < jsonArray.length(); a++) {
                                JSONObject obj = jsonArray.getJSONObject(a);
                                String authorName = obj.getString("author_name");
                                String date = obj.getString("upload_date");
                                String id = obj.getString("id");
                                String parent = obj.getString("parent");
                                String body = obj.getString("body");
                                AnswerModel answerModel = new AnswerModel();
                                answerModel.setAnswer(body);
                                answerModel.setDate(date);
                                answerModel.setLikeNo("0");
                                answerModel.setUser(authorName);
                                answerModel.setAnswerId(id);
                                answerModel.setParent(parent);
                                answerList.add(answerModel);
                            }
                        }
                        if(answerList.isEmpty()){
                            emptyStateTXT.setVisibility(View.VISIBLE);
                        }else{
                            emptyStateTXT.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        emptyStateTXT.setVisibility(View.VISIBLE);

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar1.setVisibility(View.GONE);
                Toast.makeText(NewsView.this,"error",Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("_db", db);
                params.put("id", contentId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onAnswerClick(int position) {

    }
}
