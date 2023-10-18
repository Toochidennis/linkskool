package com.digitaldream.linkskool.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.fragments.AdminDashboardFragment;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.dialog.CustomDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddNews extends AppCompatActivity {
    EditText newsTitle,newsBody;
    private String author_id,authorname,db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add News");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        FloatingActionButton addBtn = findViewById(R.id.add_news);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newsTitle.getText().toString().trim().isEmpty() || newsBody.getText().toString().trim().isEmpty()){
                    Toast.makeText(AddNews.this, "News title or body can't be empty", Toast.LENGTH_SHORT).show();
                }else{
                    JSONArray jsonArray = new JSONArray();
                    JSONObject obj2 = new JSONObject();
                    try {
                        obj2.put("content",newsBody.getText().toString());
                        obj2.put("type","text");
                        jsonArray.put(obj2);
                        addNews(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        newsTitle = findViewById(R.id.news_title);
        newsBody = findViewById(R.id.news_body);

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");
        author_id = sharedPreferences.getString("user_id","");
        authorname = sharedPreferences.getString("user","");

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

    private void addNews(JSONArray jsonArray){
        Log.i("response",newsTitle.getText().toString()+" c"+newsBody.getText().toString());
        CustomDialog dialog = new CustomDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        String url = Login.urlBase+"/addNews.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("success")){
                        AdminDashboardFragment.json = "";
                        Intent intent = new Intent(AddNews.this,Dashboard.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(AddNews.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("answer",jsonArray.toString());
                params.put("title",newsTitle.getText().toString());
                params.put("author_id",author_id);
                if(authorname==null){
                    authorname = "Admin";
                }
                params.put("author_name",authorname);
                params.put("_db",db);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(AddNews.this);
        requestQueue.add(stringRequest);
    }
}
