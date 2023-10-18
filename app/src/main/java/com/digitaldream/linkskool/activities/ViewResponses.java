package com.digitaldream.linkskool.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.adapters.ViewResponseAdapter;
import com.digitaldream.linkskool.models.ViewResponseModel;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.dialog.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewResponses extends AppCompatActivity implements ViewResponseAdapter.OnResponseListener {
    private RecyclerView recyclerView;
    private List<ViewResponseModel> list;
    private ViewResponseAdapter adapter;
    private TextView studentNo;
    ViewResponseModel vm;
    private LinearLayout emptyText;
    String db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_responses);
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail",MODE_PRIVATE);
        db = sharedPreferences.getString("db","");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
        Intent intent = getIntent();
        String examId = intent.getStringExtra("id");
        emptyText = findViewById(R.id.empty_string);
        recyclerView = findViewById(R.id.recyclerview);
        list = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new ViewResponseAdapter(this,list,this);
        recyclerView.setAdapter(adapter);
        TextView title = findViewById(R.id.debt_fee_title);
         studentNo = findViewById(R.id.no_of_student);
         title.setText(intent.getStringExtra("title").toUpperCase());
         Log.i("response","id: "+examId+" db: "+db);

        getResponseApiCall(examId);
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

    private void getResponseApiCall(String examId){

        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = Login.urlBase+"/getResponses.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response","respon "+response);
                dialog.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    studentNo.setText("Number of students: "+jsonArray.length());
                    for(int a=0;a<jsonArray.length();a++){
                        JSONObject object = jsonArray.getJSONObject(a);
                        String responseId = object.getString("response_id");
                        String examId = object.getString("exam");
                        String attempted = object.getString("attempted");
                        String score = object.getString("score");
                        String date = object.getString("date");
                        String studentId = object.getString("student");
                        String studentName = object.getString("student_name");
                        vm = new ViewResponseModel();
                        vm.setResponseId(responseId);
                        vm.setExamId(examId);
                        vm.setAttempted(attempted);
                        vm.setDate(date);
                        vm.setScore(score);
                        vm.setStudentId(studentId);
                        vm.setStudentName(studentName);
                        list.add(vm);
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(list.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                error.getMessage();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",examId);
                params.put("_db",db);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResponseClick(int position) {
        ViewResponseModel v = list.get(position);
        Intent intent = new Intent(ViewResponses.this,ResponseDetails.class);
        intent.putExtra("id",v.getResponseId());
        intent.putExtra("name",v.getStudentName());
        intent.putExtra("score",v.getScore());
        intent.putExtra("date",v.getDate());
        startActivity(intent);
    }
}
