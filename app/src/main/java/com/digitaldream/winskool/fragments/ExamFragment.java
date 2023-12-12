package com.digitaldream.winskool.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.digitaldream.winskool.adapters.ExamOptionsAdapter;
import com.digitaldream.winskool.config.DatabaseHelper;
import com.digitaldream.winskool.models.ExamType;
import com.digitaldream.winskool.R;
import com.digitaldream.winskool.dialog.CustomCBTDialog;
import com.digitaldream.winskool.dialog.CustomDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamFragment extends Fragment {

    ListView examOptions;
    List<ExamType> examTypeList;
    Dao<ExamType,Long> dao;
    DatabaseHelper dbService;
    GridView gridView;
    ExamOptionsAdapter eAdapter;
    public SharedPreferences.Editor editor;
    private Toolbar toolbar;
    CustomCBTDialog cud;
    CustomDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view= inflater.inflate(R.layout.fragment_exam, container, false);
            gridView = view.findViewById(R.id.grid_view);
            dbService = new DatabaseHelper(getContext());
        try {
            dao = DaoManager.createDao(dbService.getConnectionSource(), ExamType.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CBT");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        inflateOptions();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    public void  inflateOptions(){
        try {
            examTypeList = dao.queryForAll();
            if (!examTypeList.isEmpty()) {
                eAdapter = new ExamOptionsAdapter(getContext(), examTypeList);
                gridView.setAdapter(eAdapter);
                eAdapter.notifyDataSetChanged();
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ExamType grids = (ExamType)eAdapter.getItem(i);
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("exam", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("examTypeId",grids.getExamTypeId());
                        editor.putString("examName",grids.getExamName());
                        editor.apply();
                        Activity activity = (Activity)getContext();
                        cud = new CustomCBTDialog(activity);
                        cud.show();
                        Window window = cud.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        //BlankFragment bottomSheetDialogFragment = new BlankFragment();
                        //bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                    }
                });

            } else {
                new examOptionsTask().execute();
            }
        }catch(Exception ignored){}
    }

    private class examOptionsTask extends AsyncTask<String,Void,String> {
        HttpURLConnection urlConnection = null;
        BufferedReader returnedLogin = null;
        @Override
        protected String doInBackground(String... params) {
            final String EXAM_BASE_URL =
                    "http://www.cbtportal.linkskool.com/api/get_course.php?json";
            String jsonString = null;
            Uri login = Uri.parse(EXAM_BASE_URL).buildUpon()
                    .build();
            URL sendLogin = null;
            try {
                sendLogin = new URL(login.toString());
                urlConnection = (HttpURLConnection) sendLogin.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setUseCaches(false);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                returnedLogin = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = returnedLogin.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                jsonString = buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
                if (returnedLogin != null) {
                    try {
                        returnedLogin.close();
                    } catch (final IOException e) {
                    }
                }
            }
            return jsonString;
        }
        @Override
        protected void onPostExecute(String result) {
            if(getActivity()!=null) {
                dialog.dismiss();
            }
            if(result!=null){
                Log.i("results",result);
                try {
                    JSONArray examOb = new JSONArray(result);
                    for(int i=0;i<examOb.length();i++){
                        JSONObject examOb1=examOb.getJSONObject(i);
                        checkExam(examOb1);
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }else{
                new examOptionsTask().execute();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(getActivity()!=null) {
                dialog = new CustomDialog((Activity) getContext());
                dialog.show();
            }
        }
    }
    public void checkExam(JSONObject result){
        try {

            String title = result.getString("t");
            String link = result.getString("p");
            String status = result.getString("s");
            String category = result.getString("t");
            String id = result.getString("i");
            QueryBuilder<ExamType,Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq("examName",title);
            examTypeList = queryBuilder.query();
            if(!examTypeList.isEmpty()){

            }else{
                ExamType examType = new ExamType();
                examType.setExamName(title);
                examType.setStatus(status);
                examType.setExamTypeId(Integer.parseInt(id));
                examType.setCategory(category);
                examType.setExamLogo(link);
                dao.create(examType);
                inflateOptions();
            }
        }catch(Exception ignored){

        }
    }

    public SharedPreferences getPref()
    {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    public SharedPreferences.Editor getEditor()
    {
        SharedPreferences pref = getPref();
        editor = pref.edit();
        return editor;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(cud!=null){
            cud.dismiss();
        }
    }
}
