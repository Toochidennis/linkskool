package com.digitaldream.winskool.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitaldream.winskool.R;
import com.digitaldream.winskool.adapters.QAdapter;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

public class ExamActivity2 extends AppCompatActivity {
    String description;
    boolean timeOn =false;
    Integer grading=1;
    Integer correctGrade=0;
    long time;
    String course;
    String year;
    String json;
    Integer number=1;
    View prevView;
    View nextView;
    public View current;
    String[] numbs={"A","B","C","D","E",""};
    CountDownTimer cTimer = null;
    AlertDialog dialog=null;
    String exam_type_id;
    Boolean exam_on=false;
    public static Boolean checkNext=false;
    String from;
    String[] content;
    public QAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam2);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        Bundle bundle = null;
        bundle = this.getIntent().getExtras();
        course = bundle.getString("course");
        year = bundle.getString("year");
        json = bundle.getString("Json");
        from = bundle.getString("from");
        TextView t=findViewById(R.id.exam_title);
        t.setText(course);
        TextView t1=findViewById(R.id.exam_year);
        t1.setText(year);

        SharedPreferences sharedPreferences =getSharedPreferences("exam", Context.MODE_PRIVATE);
        String examName = sharedPreferences.getString("examName","");
        getSupportActionBar().setTitle(examName.toUpperCase());

        exam_type_id = Integer.toString(sharedPreferences.getInt("examTypeId", 1));
        JSONObject jsonObject = new JSONObject();

        if(from.equalsIgnoreCase("assessment")){
            try {
                jsonObject.put("name",json);

                startDisplay(jsonObject.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            startDisplay(json);
        }
        Log.i("response",json);
    }

    public void startDisplay(String result){
        try {
            JSONObject p = new JSONObject(result);
            JSONObject courses = p.getJSONObject("e");
            LinearLayout lay = findViewById(R.id.displayExam);
            exam(lay, courses);


            try {
                JSONArray exm = p.getJSONArray("q");
                render2("0", exm, null, null);
            }catch (JSONException e) {

                JSONObject exm = p.getJSONObject("q");
                render2("0", exm, null, null);
            }
            if(from.equalsIgnoreCase("assessment")){
                takeTest();
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public  void exam(LinearLayout a,JSONObject b){
        try {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            String title=b.getString("title");
            description=b.getString("description");
            TextView time=findViewById(R.id.intro_exam_time);
            time.setText(description);
            TextView exam=findViewById(R.id.intro_exam_name);
            TextView wasce=findViewById(R.id.exam_name);
            wasce.setText(title);
            exam.setText(course);

            ScrollView exam_intro= findViewById(R.id.exam_introduction);
            exam_intro.setVisibility(View.VISIBLE);
        } catch (JSONException e) {

        }
    }
    public void render2(String a,JSONArray b,JSONObject c, String d){
        try {
            if(b.getJSONArray(0) ==null) return;
            JSONArray g = b.getJSONArray(Integer.parseInt(a));
            if (b == null || g == null) return;
            if(g!=null){
                for(int i = 0; i < g.length(); i++)
                {
                    JSONObject js= g.getJSONObject(i);
                    if(js!=null) {
                        String gid = js.getString("id");
                        String ty = js.getString("type");
                        LinearLayout lay=findViewById(R.id.displayExam);
                        switch (ty){
                            case "section":
                                section(lay,js);
                                break;
                            case "qp":
                                qp(lay,js);
                                break;
                            case "qs":
                                qs(lay,js);
                                break;
                            case "qo":
                                qo(lay,js);
                                break;
                            case "qf":
                                qf(lay,js);
                                break;
                            case "af":
                                af(lay,js,false);
                                break;
                        }

                        render2(gid, b, null, null);
                    }
                }
            }
        }catch (JSONException e){
        }
    }

    public void render2(String a,JSONObject b,JSONObject c, String d){
        try {
            if(b.getJSONArray(a) ==null) return;
            JSONArray g = b.getJSONArray(a);
            if (b == null || g == null) return;
            if(g!=null){
                for(int i = 0; i < g.length(); i++)
                {
                    JSONObject js= g.getJSONObject(i);
                    if(js!=null) {
                        String gid = js.getString("id");
                        String ty = js.getString("type");
                        LinearLayout lay=findViewById(R.id.displayExam);
                        switch (ty){
                            case "section":
                                section(lay,js);
                                break;
                            case "qp":
                                qp(lay,js);
                                break;
                            case "qs":
                                qs(lay,js);
                                break;
                            case "qo":
                                qo(lay,js);
                                break;
                            case "qf":
                                qf(lay,js);
                                break;
                            case "af":
                                af(lay,js,false);
                                break;
                        }
                        render2(gid, b, null, null);
                    }
                }
            }
        }catch (JSONException e){
        }
    }
    public void section(LinearLayout a,JSONObject b){
        try {
            String description = b.getString("description");
            String description1= Html.fromHtml(description).toString();
            String cleaned = b.getString("content");
            String cleaned1= Html.fromHtml(cleaned).toString();
            String content1 = cleaned1.replace("\n","");
            String content = content1.replace("&nbsp;"," ");
            content = content.substring(0,1).toUpperCase()+content.substring(1);
            View v= LayoutInflater.from(this).inflate(R.layout.section,a,false);
            TextView txt1=v.findViewById(R.id.exam_description);
            TextView txt2=v.findViewById(R.id.exam_content);
            txt1.setText(description1);
            txt2.setText(content);
            v.setVisibility(View.GONE);
            a.addView(v);
        }catch(JSONException e){}
    }
    public void qp(LinearLayout a,JSONObject b){
        try {
            String cleaned = b.getString("content");
            String cleaned1= Html.fromHtml(cleaned).toString();
            String content = cleaned1.replace("&nbsp;","");
            content = content.substring(0,1).toUpperCase()+content.substring(1);
            View v=LayoutInflater.from(this).inflate(R.layout.qp,a,false);
            TextView txt2=v.findViewById(R.id.qp_text);
            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this,R.font.kalam);
            }
            txt2.setTypeface(typeface);
            txt2.setText(content);
            v.setVisibility(View.GONE);
            v.setTag("qp");
            a.addView(v);
        }catch(JSONException e){}
    }
    public void qs(LinearLayout a,JSONObject b){
        try {
            String cleaned = b.getString("content");
            String correct1 = b.getString("correct");
            String correct11= Html.fromHtml(correct1).toString();
            String correct= Html.fromHtml(cleaned).toString();
            String content = correct.replace("&nbsp;","");
            content = content.substring(0,1).toUpperCase()+content.substring(1);
            View v=LayoutInflater.from(this).inflate(R.layout.qs,a,false);
            TextView cAns=v.findViewById(R.id.qs_correct_ans);
            TextView nn=v.findViewById(R.id.qs_num);
            nn.setText("QUESTION "+number);
            cAns.setText(correct11);
            TextView txt2=v.findViewById(R.id.qs_text);
            txt2.setText(content);
            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this,R.font.kalam);
            }
            txt2.setTypeface(typeface);
            v.setVisibility(View.GONE);
            v.setTag("qs");
            a.addView(v);
            number++;
        }catch(JSONException e){}
    }
    public void qo(LinearLayout a,JSONObject b){
        try {
            String cleaned = b.getString("content");
            String cleaned1= Html.fromHtml(cleaned).toString();
            String content1 = cleaned1.replace("&nbsp;","");
            String content = content1.replace("\n","");
            try {
                content = content.substring(0, 1).toUpperCase() + content.substring(1);
            }catch (StringIndexOutOfBoundsException e){
                e.printStackTrace();
            }
            View v=LayoutInflater.from(this).inflate(R.layout.qo,a,false);
            TextView txt=v.findViewById(R.id.qo_text);
            TextView nn=v.findViewById(R.id.qs_num);
            nn.setText("QUESTION "+number);
            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this,R.font.kalam);
            }
            txt.setTypeface(typeface);

            txt.setText(content);
            v.setVisibility(View.GONE);
            v.setTag("qo");
            a.addView(v);
            grading++;
            number++;
            af((LinearLayout) v, b,true);
        }catch (JSONException e){

        }
    }
    public void qf(LinearLayout a,JSONObject b){
        try {
            String cleaned = b.getString("content");
            String cleaned1= Html.fromHtml(cleaned).toString();
            String content1 = cleaned1.replace("&nbsp;","");
            String content = content1.replace("\n","");
            content = content.substring(0,1).toUpperCase()+content.substring(1);
            View v = LayoutInflater.from(this).inflate(R.layout.qf, a, false);
            TextView txt2 =  v.findViewById(R.id.exam_question);
            txt2.setText(content);
            v.setVisibility(View.GONE);
            v.setTag("qf");
            a.addView(v);
        }catch(JSONException e){}
    }
    @SuppressLint("RestrictedApi")
    public void af(LinearLayout a,JSONObject b, boolean visible){
        float scale = getResources().getDisplayMetrics().density;
        int dp = (int) (16*scale + 0.5f);
        try {
            if(!b.getString("answer").equals(null))
            {
                String cleaned = b.getString("answer");
                String correct = b.getString("correct");
                String content1;
                View v;
                if(visible){
                    v = LayoutInflater.from(this).inflate(R.layout.optionsview, a, false);
                }else{
                    v = LayoutInflater.from(this).inflate(R.layout.card, a, false);
                }
                content1 = cleaned.replace("&nbsp;","");
                content = content1.split("\\|\\|");
                //Log.i("response",content[1]);
                //Log.i("point","point 1");


                final LinearLayout q = v.findViewById(R.id.qd);
                TextView ans=v.findViewById(R.id.correct_ans);
                final TextView your_ans=v.findViewById(R.id.your_ans);

                final String correct1=Html.fromHtml(correct).toString();
                int t = 0;

                try {
                    if(!correct1.isEmpty()) {
                        if(from.equalsIgnoreCase("cbt")) {
                            String t_st = Character.toString(correct1.charAt(1));
                            t = Integer.parseInt(t_st) - 1;
                        }else if(from.equalsIgnoreCase("assessment")){

                        }
                    }




                    int duration = 300;
                    long startTimev=100;
                    for(int j=0;j<content.length;j++) {


                        String str=Html.fromHtml(content[j]).toString();
                        try {
                            str = str.substring(0, 1).toUpperCase() + str.substring(1);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        str=numbs[j]+".  "+str.replace("\n","");


                        View vi = LayoutInflater.from(this).inflate(R.layout.options_layout,a,false);
                        CardView card = vi.findViewById(R.id.view_cont);

                        TextView k = vi.findViewById(R.id.opt);
                        k.setText(str);
                        q.addView(vi);

                        card.startAnimation(inFromLeftAnimation(duration,j*startTimev));



                        if(from.equalsIgnoreCase("cbt")) {
                            if(j==t) {
                                ans.setText(str);
                            }
                        }else if(from.equalsIgnoreCase("assessment")){
                            if(correct1.equalsIgnoreCase(content[j])) {
                                ans.setText(str);
                            }
                        }
                    }
                }catch (NumberFormatException e){//finish();
                    e.printStackTrace();
                    //Toast.makeText(this, "Exam Cannot Be Loaded at this Time!!", Toast.LENGTH_LONG).show();
                }
                if(!visible){
                    LinearLayout a2=v.findViewById(R.id.caard);
                    View v2 = LayoutInflater.from(this).inflate(R.layout.optionsview, a2, false);
                    v.setVisibility(View.GONE);
                    v.setTag("af");
                    TextView numbering=v.findViewById(R.id.numbering);
                    numbering.setText("QUESTION "+number);
                    number++;
                    grading++;
                    a2.addView(v2);
                    a.addView(v);
                }else {
                    LinearLayout a3=a.findViewById(R.id.card_lin);
                    a3.addView(v);
                }


                for(int c=0;c<q.getChildCount();c++){
                    final CardView r = (CardView) q.getChildAt(c);
                    final TextView k = q.getChildAt(c).findViewById(R.id.opt);
                    r.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkNext=false;
                            your_ans.setText(k.getText().toString());
                            wonder();
                            for(int c=0;c<q.getChildCount();c++) {
                                final CardView ri = (CardView) q.getChildAt(c);
                                TextView k = q.getChildAt(c).findViewById(R.id.opt);
                                ri.setCardBackgroundColor(Color.WHITE);
                                k.setTextColor(Color.BLACK);
                            }
                            r.setCardBackgroundColor(getResources().getColor(R.color.blue));
                            k.setTextColor(Color.WHITE);

                        }

                    });


                }

            }
        }catch(Exception e){finish();
            e.printStackTrace();
            Toast.makeText(this, "Exam Cannot Be Loaded at this Time!!", Toast.LENGTH_LONG).show();}
    }
    public void wonder(){
        final ImageButton jl=findViewById(R.id.fwd);
        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!checkNext)jl.performClick();
                    }
                });
            }
        }, 1000);
    }
    void startTimer() {
        cTimer = new CountDownTimer(time, 1000) {
            TextView m=(TextView)findViewById(R.id.exam);
            RelativeLayout mc=(RelativeLayout)findViewById(R.id.exam_cont);
            public void onTick(long millisUntilFinished) {
                int sec = (int) (millisUntilFinished / 1000);
                int hrs = (int) Math.floor(sec / 3600);
                int minutes = (int)Math.floor((sec % 3600)/60);
                int seconds=(sec % 3600)%60;
                mc.setVisibility(View.VISIBLE);
                m.setText(String.format("%02d", hrs)
                        + ":" + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }
            public void onFinish() {
                m.setText("");
                ScrollView s= findViewById(R.id.scrollview_exam);
                RelativeLayout r=findViewById(R.id.nav);
                ScrollView s1= findViewById(R.id.sub_scroll);
                cTimer.cancel();
                mc.setVisibility(View.GONE);
                s.setVisibility(View.GONE);
                r.setVisibility(View.GONE);
                s1.setVisibility(View.VISIBLE);
            }
        };
        cTimer.start();
        timeOn = true;
    }
    public void check(View view){
        TextView m=findViewById(R.id.exam);
        dialog=new AlertDialog.Builder(this)
                .setTitle("Submit Exam")
                .setMessage("Are you sure you want to submit this exam?"+" Time remaining is "+m.getText().toString())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        submitExam();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.ic_warning)
                .show();
    }
    @Override
    public void onBackPressed() {
        if(exam_on==true){
            Button b=findViewById(R.id.submitBtn);
            b.performClick();
        }else{
            finish();
        }
    }
    public void calcTime(String a){
        String[] b=a.split(" ");
        try {
            int len=b.length;
            char first=Character.toLowerCase(b[0].charAt(0));

            int t1 = NumberFormat.getInstance().parse(b[0]).intValue();
            int t2;
            if(Character.toString(first).equals("h")){
                t1=t1*3600000;
            }else{
                t1=t1*60000;
            }
            if (len>2) {
                t2 = NumberFormat.getInstance().parse(b[0]).intValue();
                t2=t2*60000;
            } else {
                t2 = 0;
            }
            time = t1+t2;
        }catch(java.text.ParseException e){
            e.printStackTrace();
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        startTimer();
    }
    public void anotherTest(View view){
        finish();
    }
    public void takeTest(View view){
        LinearLayout h=findViewById(R.id.hif);
        h.setVisibility(View.GONE);
        RelativeLayout r=findViewById(R.id.nav);
        ScrollView s= findViewById(R.id.scrollview_exam);
        ScrollView e= findViewById(R.id.exam_introduction);
        r.setVisibility(view.VISIBLE);
        s.setVisibility(view.VISIBLE);
        e.setVisibility(view.GONE);
        LinearLayout parent= findViewById(R.id.displayExam);
        current = parent.getChildAt(parent.indexOfChild(current)+1);
        current.setVisibility(view.VISIBLE);
        calcTime(description);
        exam_on=true;
    }
    public void takeTest(){
        LinearLayout h=findViewById(R.id.hif);
        h.setVisibility(View.GONE);
        RelativeLayout r=findViewById(R.id.nav);
        ScrollView s= findViewById(R.id.scrollview_exam);
        ScrollView e= findViewById(R.id.exam_introduction);
        r.setVisibility(View.VISIBLE);
        s.setVisibility(View.VISIBLE);
        e.setVisibility(View.GONE);
        LinearLayout parent= findViewById(R.id.displayExam);
        current = parent.getChildAt(parent.indexOfChild(current)+1);
        current.setVisibility(View.VISIBLE);
        if(!description.isEmpty()) {
            calcTime(description);
        }
        exam_on=true;
    }
    public void back(View view){
        LinearLayout parent =  findViewById(R.id.displayExam);
        prevView = parent.getChildAt(parent.indexOfChild(current) - 1);
        int i=parent.indexOfChild(current);
        ImageButton p=findViewById(R.id.prev);
        ImageButton f=findViewById(R.id.fwd);
        if(i==1)p.setVisibility(View.GONE);
        if(prevView !=null)
        {
            f.setVisibility(View.VISIBLE);
            current.setVisibility(view.GONE);
            current=prevView;
            prevView.setVisibility( view.VISIBLE);
        }
    }
    public void next(View view){
        checkNext=true;
        LinearLayout parent = findViewById(R.id.displayExam);
        int i=parent.indexOfChild(current);
        ImageButton f=findViewById(R.id.fwd);
        ImageButton p=findViewById(R.id.prev);
        nextView = parent.getChildAt(i+1);
        if(parent.getChildAt(i+2)==null)f.setVisibility(View.GONE);
        if(nextView !=null) {
            p.setVisibility(View.VISIBLE);
            current.setVisibility(view.GONE);
            current = nextView;
            nextView.setVisibility(view.VISIBLE);
        }
    }


    public void submitExam(){
        ScrollView s= findViewById(R.id.scrollview_exam);
        RelativeLayout r=findViewById(R.id.nav);
        ScrollView s1= findViewById(R.id.sub_scroll);
        TextView m=findViewById(R.id.exam);
        RelativeLayout mc=findViewById(R.id.exam_cont);
        LinearLayout hif=findViewById(R.id.hif);
        hif.setVisibility(View.VISIBLE);
        mc.setVisibility(View.GONE);
        if(timeOn) {
            cTimer.cancel();
        }
        s.setVisibility(View.GONE);
        r.setVisibility(View.GONE);
        s1.setVisibility(View.VISIBLE);
        calcGrade();
        exam_on=false;
    }
    public void viewResult(View view){
        float scale = getResources().getDisplayMetrics().density;
        int dp = (int) (0*scale + 0.5f);
        ScrollView sub=findViewById(R.id.sub_scroll);
        sub.setVisibility(View.GONE);
        ScrollView s= findViewById(R.id.scrollview_exam);
        s.setVisibility(View.VISIBLE);
        s.setPadding(dp,dp,dp,dp);
        LinearLayout v = findViewById(R.id.displayExam);
        int childCount= v.getChildCount();
        for(int i=0;i<childCount;i++)
        {
            View child= v.getChildAt(i);
            child.setVisibility(View.GONE);
            if(child.getTag() == "qo" || child.getTag() == "af" ){
                RelativeLayout r=findViewById(R.id.nav);
                r.setVisibility(View.GONE);
                LinearLayout group= child.findViewById(R.id.question);
                TextView correct= child.findViewById(R.id.correct_ans);
                TextView correctText= child.findViewById(R.id.t_correct_ans);
                TextView ansText= child.findViewById(R.id.t_your_ans);
                TextView ans= child.findViewById(R.id.your_ans);
                TextView cBar = child.findViewById(R.id.colorBar);
                TextView verdict= child.findViewById(R.id.verdict);
                ImageView img= child.findViewById(R.id.verdict_img);
                FrameLayout img1= child.findViewById(R.id.verdict_fram);
                if(correct !=null)correct.setVisibility(View.VISIBLE);
                if(ans !=null)ans.setVisibility(View.VISIBLE);
                if(group !=null)group.setVisibility(View.GONE);
                if(verdict !=null){
                    correctText.setText("Correct Answer");
                    String h=ans.getText().toString();
                    if(h!=null && !h.equals("")){
                        ansText.setText("Your Answer");
                        ansText.setVisibility(View.VISIBLE);
                        if(img1 !=null)img1.setVisibility(View.VISIBLE);
                    }
                    if(verdict.getText().toString().trim().equals("correct")) {
                        cBar.setVisibility(View.VISIBLE);
                        img.setImageDrawable(getResources().getDrawable(R.drawable.good));
                    }
                    else {
                        cBar.setVisibility(View.VISIBLE);
                        cBar.setBackgroundColor(ContextCompat.getColor(ExamActivity2.this, R.color.redH));
                        img.setImageDrawable(getResources().getDrawable(R.drawable.bad));
                    }
                }
                child.setVisibility(View.VISIBLE);
            }
            if(child.getTag()=="qs"){
                TextInputLayout tt= child.findViewById(R.id.editable);
                if(tt!=null)tt.setVisibility(View.GONE);
                EditText at= child.findViewById(R.id.input_ans);
                TextView at1= child.findViewById(R.id.your_ans);
                String j=at.getText().toString();
                TextView ansText= child.findViewById(R.id.t_your_ans);

                if(j!=null && !j.equals("")){
                    ansText.setText("Your Answer");
                    ansText.setVisibility(View.VISIBLE);
                    at1.setText(j);
                    at1.setVisibility(View.VISIBLE);
                }
                TextView correct= child.findViewById(R.id.qs_correct_ans);
                if(correct !=null)correct.setVisibility(View.VISIBLE);
                child.setVisibility(View.VISIBLE);
                TextView cA= child.findViewById(R.id.qs_correct_ans_txt);
                cA.setVisibility(View.VISIBLE);
            }
        }
    }
    public void calcGrade(){
        LinearLayout v =findViewById(R.id.displayExam);
        int childCount=v.getChildCount();
        for(int i=0;i<childCount;i++)
        {
            View child=v.getChildAt(i);
            if(child.getTag()=="qo" || child.getTag()=="af" ) {
                TextView correct= child.findViewById(R.id.correct_ans);
                TextView ans= child.findViewById(R.id.your_ans);
                TextView verdict= child.findViewById(R.id.verdict);
                if(ans==null || correct==null)continue;
                if(correct.getText().toString().trim().equals(""))continue;
                if(ans.getText().toString().equalsIgnoreCase(correct.getText().toString()))
                {
                    verdict.setText("correct");
                    correctGrade++;
                }else verdict.setText("wrong");
            }
        }
        TextView scoreText=findViewById(R.id.submit_score);
        TextView total=findViewById(R.id.total_score);
        total.setText(""+(grading-1));
        String gradeStatement=""+correctGrade;
        scoreText.setText(gradeStatement);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Animation inFromRightAnimation(int duration) {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(duration);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }


    private Animation inFromLeftAnimation(int duration,Long startTime) {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(duration);
        inFromLeft.setStartOffset(startTime);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }


    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(500);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }

}
