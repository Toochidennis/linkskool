package com.digitaldream.winskool.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.winskool.activities.Flashcard;
import com.digitaldream.winskool.activities.Login;
import com.digitaldream.winskool.adapters.FlashCardListAdapter;
import com.digitaldream.winskool.models.FlashCardModel;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.dialog.CustomDialog;
import com.digitaldream.winskool.dialog.FlashCardSettingsDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FlashCardList extends Fragment implements FlashCardListAdapter.OnFlashCardClickListener {
    public static FlashCardSettingsDialog dialog = null;
    private String id;
    private String db;
    private String term;
    private String user_name,level;
    List<FlashCardModel> list;
    FlashCardListAdapter adapter;
    private static String json="";
    public static boolean refresh=false;
    private LinearLayout emptyState;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_flash_card_list, container, false);
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Flashcard");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        setHasOptionsMenu(true);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");
        user_name = sharedPreferences.getString("user","Admin");
        term = sharedPreferences.getString("term","");
        id =sharedPreferences.getString("user_id","");
        level = sharedPreferences.getString("level","");
        emptyState = v.findViewById(R.id.empty_state);




        list = new ArrayList<>();


        RecyclerView recyclerView = v.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FlashCardListAdapter(getContext(),list,this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addCard = v.findViewById(R.id.add_flashcard);
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = ((FragmentActivity) getContext())
                        .getSupportFragmentManager()
                        .beginTransaction();
                dialog = new FlashCardSettingsDialog();
                dialog.show(transaction, "FlashcardBottomSheet");
            }
        });
        if(json.isEmpty()) {
            getFlashCard();
        }else{
            if(refresh){
                getFlashCard();
                refresh=false;
            }else {
                parseJSON(json);
            }
        }

        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }


    @Override
    public void onCardClick(int position) {
        FlashCardModel fm =list.get(position);
        Intent intent = new Intent(getContext(), Flashcard.class);
        intent.putExtra("id",fm.getId());
        intent.putExtra("title",fm.getTerm());
        intent.putExtra("from","flash_list");
        intent.putExtra("date",fm.getDate());
        intent.putExtra("count",fm.getCount());
        startActivity(intent);
    }


    private void getFlashCard(){
        CustomDialog dialog = new CustomDialog((Activity) getContext());
        dialog.show();
        String url = Login.urlBase+"/getFlashCards.php";

        StringRequest stringRequest =new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                json=response;
                dialog.dismiss();
                parseJSON(response);

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
                params.put("_db",db);
                params.put("level",level);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void parseJSON(String response){
        try {
            JSONArray arr = new JSONArray(response);
            for(int a=0;a<arr.length();a++){
                JSONObject object = arr.getJSONObject(a);
                String id = object.getString("id");
                String title = object.getString("title");
                String url = object.getString("url");
                String date =object.getString("upload_date");
                String count="";
                try {
                    String[] countArr = url.split(",");
                    count = String.valueOf(countArr.length);
                }catch (Exception e){
                    e.printStackTrace();
                }

                FlashCardModel cd1 = new FlashCardModel();
                cd1.setTerm(title);
                cd1.setId(id);
                cd1.setDate(date);
                cd1.setCount(count);
                list.add(cd1);

            }
            if(list.isEmpty()){
                emptyState.setVisibility(View.VISIBLE);
            }else{
                emptyState.setVisibility(View.GONE);

            }
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
