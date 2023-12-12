package com.digitaldream.winskool.activities;

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
import com.digitaldream.winskool.adapters.AnswerAdapter;
import com.digitaldream.winskool.models.AnswerModel;
import com.digitaldream.winskool.models.CommentTable;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.models.QAObject;
import com.digitaldream.winskool.utils.AnswerBottomSheet;
import com.digitaldream.winskool.utils.RefreshListener;
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

public class AnswerView extends AppCompatActivity implements AnswerAdapter.OnAnswerClickListener, RefreshListener {
    TextView name,date,initial,answer,commentNo,upvotes,sharesCount,user,date1;
    public static QAObject feed;
    private RecyclerView recyclerView;
    private List<AnswerModel> answerList;
    private EditText replyEDT;
    private String db;
    private List<CommentTable> commentModelList ;
    AnswerAdapter adapter;
    ProgressBar progressBar;
    private TextView emptyStateTXT;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        commentModelList = new ArrayList<>();
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        ImageView backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        feed = (QAObject) getIntent().getSerializableExtra("feed");
        TextView questionText = findViewById(R.id.question);
        String question = feed.getQuestion().trim();
        Log.i("response",question);
        try {
            String q = question.substring(question.length() - 1);
            Log.i("response","ans "+q);
            if (q.equalsIgnoreCase("?")) {
                questionText.setText(question.substring(0,1).toUpperCase()+""+question.substring(1));

            } else {
                questionText.setText(question.substring(0,1).toUpperCase()+""+question.substring(1) + "?");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

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
        i=i.substring(0,1).toUpperCase();
        initial.setText(i);
        questionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnswerView.this, QuestionView.class);
                intent.putExtra("feed",feed);
                startActivity(intent);
            }
        });



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

        LinearLayout linearLayout = findViewById(R.id.answer_display);
        String answer = feed.getAnswer();
        LayoutInflater inflater = LayoutInflater.from(this);
        try {
            JSONArray array = new JSONArray(answer);
            for (int b=0;b<array.length();b++){
                JSONObject jsonObject = array.getJSONObject(b);
                String type = jsonObject.getString("type");
                View view = null;
                if(type.equals("text")){
                    String content = jsonObject.getString("content");
                    view = inflater.inflate(R.layout.text_layout,linearLayout,false);
                    TextView t = view.findViewById(R.id.text);
                    content = content.substring(0,1).toUpperCase()+""+content.substring(1);
                    t.setText(content);
                }else if(type.equals("image")){
                    String src = jsonObject.getString("src");
                    src = src.substring(2);
                    view = inflater.inflate(R.layout.image_layout,linearLayout,false);
                    ImageView img = view.findViewById(R.id.image);
                    //img.setImageResource(R.drawable.splashscreen);
                    Picasso.get().load(Login.fileBaseUrl+""+src).into(img);
                }
                linearLayout.addView(view);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        replyEDT = findViewById(R.id.reply);
        replyEDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feed.setQuestion(feed.getAnswer());
                FragmentTransaction transaction = ((FragmentActivity) AnswerView.this)
                        .getSupportFragmentManager()
                        .beginTransaction();
                AnswerBottomSheet answerBottomSheet = AnswerBottomSheet.newInstance("reply");
                answerBottomSheet.show(transaction, "answerBottomSheet");
                answerBottomSheet.DismissListener(AnswerView.this);
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
    protected void onResume() {
        super.onResume();
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
                Toast.makeText(AnswerView.this,"error",Toast.LENGTH_SHORT).show();

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

    @Override
    public void onRefresh(DialogInterface dialog) {
        fetchComments(db,feed.getId(),progressBar);

    }
}
