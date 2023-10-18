package com.digitaldream.linkskool.activities;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ReferSchool extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText schoolName,contactPerson,phoneNo,emailEdT,referralName;
    private Button submit;
    private String schoolNameText,contactPersonName,phone,email,refferal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_school);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Refer School");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        schoolName = findViewById(R.id.referal_school);
        contactPerson = findViewById(R.id.contact_person);
        phoneNo = findViewById(R.id.contact_phone);
        emailEdT = findViewById(R.id.contact_email);
        referralName = findViewById(R.id.referral_name);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });
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

    private void validateInput(){
         schoolNameText = schoolName.getText().toString();
         contactPersonName = contactPerson.getText().toString();
         phone = phoneNo.getText().toString();
         email = emailEdT.getText().toString();
        refferal = referralName.getText().toString();
        if(schoolNameText.isEmpty()){
            Toast.makeText(this,"School name is required",Toast.LENGTH_SHORT).show();
        }else if(contactPersonName.isEmpty()){
            Toast.makeText(this,"Contact person name is required",Toast.LENGTH_SHORT).show();
        }else if(phone.isEmpty()){
            Toast.makeText(this,"Phone Number is required",Toast.LENGTH_SHORT).show();
        }else{
            referSchoolApiCall();
        }
    }

    private void referSchoolApiCall(){
        final ACProgressFlower dialog1 = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .textMarginTop(10)
                .fadeColor(Color.DKGRAY).build();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        String url = "http://linkskool.com/cbt/api/addReferal.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                dialog1.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equalsIgnoreCase("success")){
                        Toast.makeText(ReferSchool.this,"Your form was submitted successfully",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }else{
                        Toast.makeText(ReferSchool.this,"Operation Failed",Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog1.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("school_name",schoolNameText);
                params.put("contact_person",contactPersonName);
                params.put("phone",phone);
                params.put("email",email);
                params.put("referral",refferal);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
