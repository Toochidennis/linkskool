package com.digitaldream.winskool.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.dialog.CustomDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseDetails extends AppCompatActivity {
    private String id;
    TextView nameText,dateText,scoreText;
    private List<ResponseModel> responseList;
    private Double totalMarkScore=0.0;
    private Double totalScore=0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        Intent i =getIntent();
        id=i.getStringExtra("id");
        String name = i.getStringExtra("name");
        String score = i.getStringExtra("score");
        String date = i.getStringExtra("date");
        nameText = findViewById(R.id.name);
        dateText = findViewById(R.id.submition_date);
        scoreText = findViewById(R.id.progress_text);
        nameText.setText(name.toUpperCase());
        scoreText.setText(score);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = simpleDateFormat.parse(date);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMM, yyyy");
            dateText.setText("Date submitted "+simpleDateFormat1.format(date1));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        responseList = new ArrayList<>();
        getStudentResponse();

        FloatingActionButton saveRespBtn = findViewById(R.id.save);
        FloatingActionButton convertBtn = findViewById(R.id.convert);
        saveRespBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray responseArr = new JSONArray();

                for(int a =0;a<responseList.size();a++){
                    ResponseModel rm = responseList.get(a);
                    JSONObject object = new JSONObject();
                    try {
                        object.put("question_id",rm.getQuestionId());
                        object.put("mark",rm.getMark());
                        object.put("comment",rm.getComment());
                        totalMarkScore+=Double.parseDouble(rm.getMark());
                        responseArr.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                totalScore = totalMarkScore;
                Log.i("response",responseArr.toString());
                submitMarkingApi(responseArr.toString());


            }
        });

        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }




    private void submitMarkingApi(String json){
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",MODE_PRIVATE);
        String db = sharedPreferences.getString("db","");
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = Login.urlBase+"/addMarking.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if(status.equals("success")){
                        Toast.makeText(ResponseDetails.this,"Operation was successful",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ResponseDetails.this,"Operation failed",Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("data",json);
                params.put("marking_score",totalScore.toString());
                params.put("id",id);
                params.put("_db",db);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ResponseDetails.this);
        requestQueue.add(stringRequest);

    }


    private void getStudentResponse(){
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",MODE_PRIVATE);
        String db = sharedPreferences.getString("db","");
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = Login.urlBase+"/getResponseDetails.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.i("response",response);
                try {
                    JSONObject object = new JSONObject(response);
                    String responseString = object.getString("response");
                    JSONArray responseArr = new JSONArray(responseString);
                    String responseId = object.getString("response_id");
                    String examId = object.getString("exam");
                    String totalscores = object.optString("total_score");
                    String markingScore = object.optString("marking_score");
                    String scor = object.getString("score");
                    double total = Double.parseDouble(markingScore) + Double.parseDouble(scor);

                    totalScore = Double.parseDouble(scor);
                    scoreText.setText(String.valueOf(total));
                    LayoutInflater inflater = LayoutInflater.from(ResponseDetails.this);
                    LinearLayout layout = findViewById(R.id.display_response);
                    View view;
                    for(int a=0;a<responseArr.length();a++){
                        JSONObject jsonObject = responseArr.getJSONObject(a);
                        String question = jsonObject.getString("question");
                        String answer = jsonObject.getString("answer");
                        String correct = jsonObject.getString("correct");
                        String type = jsonObject.getString("type");
                        String questionId = jsonObject.getString("id");
                        if(type.equals("qo")||type.equals("qm")||type.equals("qs")){
                           view = inflater.inflate(R.layout.view_response_qo,layout,false);
                           TextView questionText = view.findViewById(R.id.question);
                           TextView answerText = view.findViewById(R.id.answer);
                           TextView correctText = view.findViewById(R.id.correct);
                            ImageView verdictImg = view.findViewById(R.id.verdict_img);
                            questionText.setText("Question: "+question);
                            answerText.setText("Answer: "+answer);
                            correctText.setText("Correct: "+correct);
                            if(answer.equalsIgnoreCase(correct)){
                                verdictImg.setImageResource(R.drawable.ic_check);
                            }else{
                                verdictImg.setImageResource(R.drawable.close1);

                            }
                        }else {
                            view = inflater.inflate(R.layout.view_response_qs, layout, false);

                            ResponseModel rm = new ResponseModel();
                            rm.setQuestionId(questionId);
                            rm.setQuestion(question);
                            rm.setAnswer(answer);
                            rm.setCorrect(correct);

                            TextView questionText = view.findViewById(R.id.question);
                            TextView answerText = view.findViewById(R.id.answer);
                            TextView correctText = view.findViewById(R.id.correct);
                            EditText markEDT = view.findViewById(R.id.mark);
                            EditText commentEDT = view.findViewById(R.id.comment);
                            questionText.setText("Question: " + question);
                            answerText.setText("Answer: " + answer);
                            correctText.setText("Correct: " + correct);
                            String marking = object.getString("marking");
                            if (!marking.isEmpty()) {
                                JSONArray jsonArray = new JSONArray(marking);
                                for (int b = 0; b < jsonArray.length(); b++) {
                                    JSONObject object1 = jsonArray.getJSONObject(b);
                                    String qId = object1.optString("question_id");
                                    String mark = object1.optString("mark");
                                    String comment = object1.optString("comment");
                                    rm.setMark(mark);
                                    rm.setComment(comment);
                                    rm.setQuestionId(qId);
                                    markEDT.setText(mark);
                                    commentEDT.setText(comment);
                                }
                                markEDT.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        rm.setMark(s.toString());
                                    try {
                                        double scores = 0.0;
                                        scores = totalScore + Double.parseDouble(s.toString());
                                        scoreText.setText(String.valueOf(scores));
                                    }catch (NumberFormatException e){
                                        e.printStackTrace();
                                        scoreText.setText(scor);

                                    }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                commentEDT.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        rm.setComment(s.toString());
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                responseList.add(rm);
                            }
                        }
                        layout.addView(view);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                params.put("_db",db);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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

    public class ResponseModel{
        private String questionId;
        private String question;
        private String answer;
        private String correct;
        private String mark;
        private String comment;

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getCorrect() {
            return correct;
        }

        public void setCorrect(String correct) {
            this.correct = correct;
        }

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
