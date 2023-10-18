package com.digitaldream.linkskool.activities;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.adapters.AnswerAdapter;
import com.digitaldream.linkskool.adapters.QAAdapter;
import com.digitaldream.linkskool.models.AnswerModel;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.utils.AnswerBottomSheet;
import com.digitaldream.linkskool.utils.RefreshListener;

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

public class QuestionView extends AppCompatActivity implements AnswerAdapter.OnAnswerClickListener, RefreshListener  {
    TextView name,date,initial,question,answer,commentNo,upvotes,sharesCount,user,date1,iniitials;
    public static QAAdapter.QAObject feed;
    private RecyclerView recyclerView;
    private List<AnswerModel> answerList;
    private ProgressBar progressBar;
    private String db;
    AnswerAdapter adapter;
    private TextView emptyStateTXT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        progressBar = findViewById(R.id.progress);
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");
        ImageView backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        feed = (QAAdapter.QAObject) getIntent().getSerializableExtra("feed");
        TextView questionText = findViewById(R.id.question);
        String question = feed.getQuestion().trim();
        try {
            question = question.substring(question.length() - 1);
            String q="";
            if (question.equalsIgnoreCase("?")) {
                q =QuestionView.feed.getQuestion();
                q=q.substring(0,1).toUpperCase()+""+q.substring(1).toLowerCase();
                questionText.setText(q);

            } else {
                q=feed.getQuestion();
                q=q.substring(0,1).toUpperCase()+""+q.substring(1).toLowerCase();
                questionText.setText(q+ "?");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        LinearLayout commentEDT = findViewById(R.id.comment);
        commentEDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = ((FragmentActivity) QuestionView.this)
                        .getSupportFragmentManager()
                        .beginTransaction();
                AnswerBottomSheet answerBottomSheet = AnswerBottomSheet.newInstance("answer");
                answerBottomSheet.show(transaction, "answerBottomSheet");
                answerBottomSheet.DismissListener(QuestionView.this);
            }
        });

        name = findViewById(R.id.name);
        initial = findViewById(R.id.initial);
        answer = findViewById(R.id.answer);
        commentNo = findViewById(R.id.comments);
        upvotes = findViewById(R.id.upvotes);
        sharesCount = findViewById(R.id.share);
        user = findViewById(R.id.user);
        date1 = findViewById(R.id.date1);
        emptyStateTXT = findViewById(R.id.empty_text);
        String i = feed.getUser();
        try {
            i = i.substring(0, 1).toUpperCase();
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        initial.setText(i);



        commentNo.setText(feed.getCommentNo());
        upvotes.setText(feed.getLikesNo());
        sharesCount.setText(feed.getShareNo());
        user.setText(feed.getUser());
        String date = feed.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        try {
                Date d = simpleDateFormat.parse(date);
                String niceDateStr = String.valueOf(DateUtils.getRelativeTimeSpanString(d.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));
            date1.setText(niceDateStr);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        recyclerView = findViewById(R.id.answer_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        answerList = new ArrayList<>();

        adapter = new AnswerAdapter(this,answerList,this);
        recyclerView.setAdapter(adapter);

        getAnswers();

    }

    public void getAnswers(){
        progressBar.setVisibility(View.VISIBLE);
        String url = Login.urlBase+"/getAnswers.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                answerList.clear();
                Log.i("response","answers "+response);
                Log.i("response","size "+answerList.size());

                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);
                    Iterator<String> keys = object.keys();
                        String key = keys.next();
                        JSONArray jsonArray = object.optJSONArray(key);
                        for(int a=0;a<jsonArray.length();a++){
                            JSONObject obj = jsonArray.getJSONObject(a);
                            String authorName =obj.getString("author_name");
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
                    Log.i("response","size "+answerList.size());


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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",feed.getId());
                params.put("_db",db);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAnswerClick(int position) {
        Intent intent = new Intent(this, AnswerView.class);
        feed.setAnswer(answerList.get(position).getAnswer());
        feed.setQuestionId(answerList.get(position).getParent());
        feed.setAnswerId(answerList.get(position).getAnswerId());
        intent.putExtra("feed", feed);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh(DialogInterface dialog) {
        getAnswers();
    }
}
