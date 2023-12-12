package com.digitaldream.winskool.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitaldream.winskool.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

public class RenderAssessment extends AppCompatActivity {
    String description;
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
    Boolean exam_on=false;
    Boolean checkNext=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render_assessment);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        TextView t=findViewById(R.id.exam_title);
        t.setText("course");
        TextView t1=findViewById(R.id.exam_year);
        t1.setText("year");

        getSupportActionBar().setTitle("Assessment");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name","{\"e\":{\"id\":\"351\",\"title\":\"JAMB\",\"description\":\"30M\",\"course_name\":\"LITERATURE IN ENGLISH\",\"course_id\":\"2\",\"body\":null,\"url\":\"\"},\"q\":[[{\"id\":\"20789\",\"parent\":\"0\",\"content\":\"<p><span>Romeo and<\\/span><span> Juliet is a tragedy because <\\/span><\\/p>\",\"title\":\"\",\"type\":\"qo\",\"answer\":\"it is full of noblemen and ladies||it is set in Verona ||it ends unhappily||Juliet is only fourteen years old ||it has five acts\",\"correct\":\"A3\"},{\"id\":\"20790\",\"parent\":\"0\",\"content\":\"<p><span>Essentially, plays are meant <\\/span><\\/p>\",\"title\":\"\",\"type\":\"qo\",\"answer\":\"to be read for sheer pleasure ||to make us laugh ||to be presented on stage through words and action||to keep people out of trouble ||to change the whole world\",\"correct\":\"A3\"},{\"id\":\"20791\",\"parent\":\"0\",\"content\":\"<p><span>In 'Fulani Catt<\\/span><span>le' J. P. Clark expresses <\\/span><\\/p>\",\"title\":\"\",\"type\":\"qo\",\"answer\":\"the anger and sympathy he feels at seeing cattle driven all the way from the North to the South of Nigeria to be slaughtered||admiration for the beauty of Fulani cattle ||grief at the gradual destruction of this fine breed of cattle||the joy of the cattle at being driven over pleasant fields||the idea that more cattle should be taken to the South to be slaughtered\",\"correct\":\"A3\"},{\"id\":\"20792\",\"parent\":\"0\",\"content\":\"<p><span>Dinny<\\/span><span>, Lippy, <\\/span><span>Steepy<\\/span><span> and Fatty are characters in <\\/span><span> <\\/span><\\/p>\",\"title\":\"\",\"type\":\"qo\",\"answer\":\"Kossoh Town Boy ||Kontiki Expedition ||Tell Freedom ||The Concubine ||No Longer at Ease\",\"correct\":\"A2\"},{\"id\":\"20793\",\"parent\":\"0\",\"content\":\"<p><span>In one of the three jugs <\\/span><\\/p><p><span>          <\\/span><span>The three jugs where on certain evenings return <\\/span><\\/p><p><span>          <\\/span><span>the<\\/span><span> <\\/span><span>tranquil souls, <\\/span><\\/p><p><span>          <\\/span><span>the breaths of the ancestors, <\\/span><\\/p><p><span>          <\\/span><span>the ancestors who were men, <\\/span><\\/p><p><span>          <\\/span><span>the ancestors who were sages, <\\/span><\\/p><p><span>          <\\/span><span>Mother has dipped three fingers <\\/span><\\/p><p><span>          <\\/span><span>three fingers of her left hand thumb, <\\/span><\\/p><p><span>          <\\/span><span>forefinger and middle finger <\\/span><\\/p><p><span>          <\\/span><span>I have dipped three fingers <\\/span><\\/p><p><span>          <\\/span><span>three fingers of my right hand: <\\/span><\\/p><p><span>          <\\/span><span>thumb<\\/span><span>, <\\/span><span>forefinger and middle finger. <\\/span><\\/p><p><span>                         <\\/span><span>(From 'Viaticum' by B. <\\/span><span>Diop<\\/span><span>) <\\/span><\\/p><p><span>In this poem, the<\\/span><span> repetitive pattern suggests <\\/span><\\/p>\",\"title\":\"\",\"type\":\"qo\",\"answer\":\"pain, agony and misery||ritual or established form of worship ||the love of a mother for her child||the beauty of the full moon ||A2\",\"correct\":\"the glorious lives of our ancestors\"},{\"id\":\"20794\",\"parent\":\"0\",\"content\":\"<p><span>The language of Da<\\/span><span>vid <\\/span><span>Diop's<\\/span><span> 'Africa' suggests<\\/span><\\/p>\",\"title\":\"\",\"type\":\"qo\",\"answer\":\"the beauty of the African countryside||that black is beautiful ||that the poet remembers the great empire of Africa||that the poet sees Africa as a colonial power\",\"correct\":\"A3\"},{\"id\":\"20795\",\"parent\":\"0\",\"content\":\"<p><span>In 'I Will Pronounce Your Name' Senghor writes: <\\/span><span><\\/span><span>Naett<\\/span><span>, which<\\/span><span> is the dry tornado, the hard clap of lightning. The figure of speech used in the above line is: <\\/span><\\/p>\",\"title\":\"\",\"type\":\"qo\",\"answer\":\"Metaphor||Simile||Hyperbole||Irony||Synecdoche\",\"correct\":\"A1\"}]]}");
            Log.i("json Toochi",jsonObject.getString("name"));

            startDisplay(jsonObject.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //exam_type_id = Integer.toString(sharedPreferences.getInt("examTypeId", 1));

    }

    public void startDisplay(String result){
        try {
            Log.i("point","pointer 1");
            JSONObject p = new JSONObject(result);
            JSONObject courses = p.getJSONObject("e");
            Log.i("point","pointer 2");

            LinearLayout lay = findViewById(R.id.displayExam);
            exam(lay, courses);


            try {
                JSONArray exm = p.getJSONArray("q");
                render2("0", exm, null, null);
            }catch (JSONException e) {

                JSONObject exm = p.getJSONObject("q");
                render2("0", exm, null, null);
            }
            takeTest();



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
                        if(ty.equals("section")){
                            Toast.makeText(RenderAssessment.this,"Section",Toast.LENGTH_SHORT).show();section(lay,js);}
                        else if(ty.equals("qp")){Toast.makeText(RenderAssessment.this,"qp",Toast.LENGTH_SHORT).show();qp(lay,js);}
                        else if(ty.equals("qs")){Toast.makeText(RenderAssessment.this,"qs",Toast.LENGTH_SHORT).show();qs(lay,js);}
                        else if(ty.equals("qo")){qo(lay,js);}
                        else if(ty.equals("qf")){Toast.makeText(RenderAssessment.this,"qf",Toast.LENGTH_SHORT).show();qf(lay,js);}
                        else if(ty.equals("af")){Toast.makeText(RenderAssessment.this,"af",Toast.LENGTH_SHORT).show();af(lay,js,false);}
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
                        if(ty.equals("section")){
                            Toast.makeText(RenderAssessment.this,"Section",Toast.LENGTH_SHORT).show();section(lay,js);}
                        else if(ty.equals("qp")){Toast.makeText(RenderAssessment.this,"qp",Toast.LENGTH_SHORT).show();qp(lay,js);}
                        else if(ty.equals("qs")){Toast.makeText(RenderAssessment.this,"qs",Toast.LENGTH_SHORT).show();qs(lay,js);}
                        else if(ty.equals("qo")){qo(lay,js);}
                        else if(ty.equals("qf")){Toast.makeText(RenderAssessment.this,"qf",Toast.LENGTH_SHORT).show();qf(lay,js);}
                        else if(ty.equals("af")){Toast.makeText(RenderAssessment.this,"af",Toast.LENGTH_SHORT).show();af(lay,js,false);}
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
            View v=LayoutInflater.from(this).inflate(R.layout.qp,a,false);
            TextView txt2=v.findViewById(R.id.qp_text);
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
            View v=LayoutInflater.from(this).inflate(R.layout.qs,a,false);
            TextView cAns=v.findViewById(R.id.qs_correct_ans);
            TextView nn=v.findViewById(R.id.qs_num);
            nn.setText("QUESTION "+number);
            cAns.setText(correct11);
            TextView txt2=v.findViewById(R.id.qs_text);
            txt2.setText(content);
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
            View v=LayoutInflater.from(this).inflate(R.layout.qo,a,false);
            TextView txt=v.findViewById(R.id.qo_text);
            TextView nn=v.findViewById(R.id.qs_num);
            nn.setText("QUESTION "+number);
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
                String[] content = content1.split("\\|\\|");
                final RadioGroup radioGroup =  v.findViewById(R.id.question);
                TextView ans=v.findViewById(R.id.correct_ans);
                final TextView your_ans=v.findViewById(R.id.your_ans);
                final String correct1=Html.fromHtml(correct).toString();
                int t;
                try {
                    String t_st = Character.toString(correct1.charAt(1));
                    t = Integer.parseInt(t_st) -1;
                    int[] col={R.color.blue,R.color.amberD,R.color.colorAccent,R.color.greenC,
                            R.color.colorPrimary};
                    for(int j=0;j<content.length;j++) {
                        ColorStateList colorStateList = new ColorStateList(
                                new int[][]{
                                        new int[]{android.R.attr.state_enabled} //enabled
                                },
                                new int[] {getResources().getColor(col[j]) }
                        );
                        String str=Html.fromHtml(content[j]).toString();
                        str=numbs[j]+".  "+str.replace("\n","");
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.FILL_PARENT);
                        params.weight = 1.0f;
                        params.gravity = Gravity.CENTER_VERTICAL;
                        AppCompatRadioButton rdBtn = new AppCompatRadioButton(this);
                        rdBtn.setSupportButtonTintList(colorStateList);
                        rdBtn.setText(str);
                        rdBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                        rdBtn.setPadding(10,10,10,20);
                        rdBtn.setLayoutParams(params);
                        radioGroup.addView(rdBtn);
                        if(j==t) {
                            ans.setText(str);
                        }
                    }
                }catch (NumberFormatException e){//finish();
                    //Log.e("Exception",e.toString());
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
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup rg, int checkedId) {
                        for(int i=0; i<rg.getChildCount(); i++) {
                            RadioButton btn = (RadioButton) rg.getChildAt(i);
                            if(btn.getId() == checkedId) {
                                String text = btn.getText().toString();
                                checkNext=false;
                                your_ans.setText(text);
                                wonder();
                                return;
                            }
                        }
                    }
                });
            }
        }catch(Exception e){finish();
            Log.e("Exception",e.toString());
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
        int len=b.length;
        char first=Character.toLowerCase(b[0].charAt(0));
        try {
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
        }
        startTimer();
    }

    public void anotherTest(View view){
        finish();
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
        calcTime(description);
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
            prevView.setVisibility(view.VISIBLE);
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
        cTimer.cancel();
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
                        cBar.setBackgroundColor(ContextCompat.getColor(RenderAssessment.this, R.color.redH));
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
                if(correct.getText().toString().trim()=="")continue;
                if(ans.getText().toString()==correct.getText().toString())
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
}
