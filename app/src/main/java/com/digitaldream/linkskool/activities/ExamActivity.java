package com.digitaldream.linkskool.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.adapters.QAdapter;
import com.digitaldream.linkskool.dialog.CustomDialog;
import com.digitaldream.linkskool.dialog.ImagePreviewDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ExamActivity extends AppCompatActivity {
    String description;
    boolean timeOn = false;
    Integer grading = 1;
    Integer correctGrade = 0;
    long time;
    String course;
    String year;
    String json, courseId, levelId, examId;
    Integer number = 1;
    View prevView;
    View nextView;
    public View current;
    String[] numbs = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    CountDownTimer cTimer = null;
    AlertDialog dialog = null;
    String exam_type_id;
    Boolean exam_on = false;
    public static Boolean checkNext = false;
    String from;
    String[] content;
    public QAdapter adapter;
    String answr = "", accessLevel;
    public static int ab = 0;
    public static int qPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        Bundle bundle = null;
        SharedPreferences sharedPreferences1 = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        accessLevel = sharedPreferences1.getString("access_level", "");


        bundle = this.getIntent().getExtras();
        course = bundle.getString("course");
        year = bundle.getString("year");
        json = bundle.getString("Json");
        from = bundle.getString("from");
        courseId = bundle.getString("course_id");
        levelId = bundle.getString("level");
        examId = bundle.getString("exam_id");

        TextView t = findViewById(R.id.exam_title);
        t.setText(course);
        TextView t1 = findViewById(R.id.exam_year);
        t1.setText(year);

        SharedPreferences sharedPreferences = getSharedPreferences("exam", Context.MODE_PRIVATE);
        String examName = sharedPreferences.getString("examName", "");
        getSupportActionBar().setTitle(examName.toUpperCase());

        exam_type_id = Integer.toString(sharedPreferences.getInt("examTypeId", 1));
        JSONObject jsonObject = new JSONObject();

        if (from.equalsIgnoreCase("assessment")) {
            try {
                jsonObject.put("name", json);

                // startDisplay(jsonObject.getString("name"));
                startDisplay(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            startDisplay(json);
            //Log.i("response","respo "+json);
        }
        Button b = findViewById(R.id.submitBtn);
        if (from.equals("preview")) {
            b.setText("Go Back");
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout l = findViewById(R.id.displayExam);
                    //TextView textView = l.getChildAt(0).findViewById(R.id.hidden_value);
                    Toast.makeText(ExamActivity.this, "position " + qPosition, Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            });
        }

    }

    public void startDisplay(String result) {
        try {
            JSONObject p = new JSONObject(result);
            JSONObject courses = p.getJSONObject("e");
            LinearLayout lay = findViewById(R.id.displayExam);
            exam(lay, courses);

            try {
                JSONArray exm = p.getJSONArray("q");
                render2("0", exm, null, null);
            } catch (JSONException e) {
                e.printStackTrace();
                JSONObject exm = p.getJSONObject("q");
                render2("0", exm, null, null);
            }

            if (from.equalsIgnoreCase("assessment") || from.equals("preview")) {
                takeTest();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void exam(LinearLayout a, JSONObject b) {
        try {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            String title = b.getString("title");
            description = b.getString("description");
            TextView time = findViewById(R.id.intro_exam_time);
            time.setText(description);
            TextView exam = findViewById(R.id.intro_exam_name);
            TextView wasce = findViewById(R.id.exam_name);
            wasce.setText(title);
            exam.setText(course);

            ScrollView exam_intro = findViewById(R.id.exam_introduction);
            exam_intro.setVisibility(View.VISIBLE);
        } catch (JSONException e) {

        }
    }

    public void render2(String a, JSONArray b, JSONObject c, String d) {
        try {
            if (b.getJSONArray(0) == null) return;
            JSONArray g = null;
            try {
                g = b.optJSONArray(Integer.parseInt(a));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (b == null || g == null) return;
            if (g != null) {

                for (int i = 0; i < g.length(); i++) {
                    JSONObject js = g.getJSONObject(i);
                    if (js != null) {
                        String gid = js.optString("id");
                        String ty = js.getString("type");
                        LinearLayout lay = findViewById(R.id.displayExam);
                        switch (ty) {
                            case "section":
                                section(lay, js, i);
                                break;
                            case "qp":
                                qp(lay, js);
                                break;
                            case "qs":
                                qs(lay, js, i);
                                break;
                            case "qo":
                                qo(lay, js, i);
                                break;
                            case "qf":
                                qf(lay, js);
                                break;
                            case "af":
                                af(lay, js, false);
                                break;
                            case "qt":
                                qt(lay, js, i);
                                break;
                            case "em":
                                em(lay, js);
                                break;
                            case "nb":
                                nb(lay, js);
                                break;
                            case "dt":
                                dt(lay, js);
                                break;
                            case "tm":
                                tm(lay, js);
                                break;
                            case "qm":
                                qm(lay, js);
                                break;
                            case "sl":
                                sl(lay, js);
                                break;
                            case "fl":
                                fl(lay, js);
                                break;
                        }
                        render2(gid, b, null, null);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void render2(String a, JSONObject b, JSONObject c, String d) {
        try {
            if (b.getJSONArray(a) == null) return;
            JSONArray g = b.getJSONArray(a);
            if (b == null || g == null) return;
            if (g != null) {
                for (int i = 0; i < g.length(); i++) {
                    JSONObject js = g.getJSONObject(i);
                    if (js != null) {
                        String gid = js.getString("id");
                        String ty = js.getString("type");

                        LinearLayout lay = findViewById(R.id.displayExam);
                        switch (ty) {
                            case "section":
                                section(lay, js, i);
                                break;
                            case "qp":
                                qp(lay, js);
                                break;
                            case "qs":
                                qs(lay, js, i);
                                break;
                            case "qo":
                                qo(lay, js, i);
                                break;
                            case "qf":
                                qf(lay, js);
                                break;
                            case "af":
                                af(lay, js, false);
                                break;
                            case "qt":
                                qt(lay, js, i);
                                break;
                            case "em":
                                em(lay, js);
                                break;
                            case "nb":
                                nb(lay, js);
                                break;
                            case "dt":
                                dt(lay, js);
                                break;
                            case "tm":
                                tm(lay, js);
                                break;
                            case "qm":
                                qm(lay, js);
                                break;
                            case "sl":
                                sl(lay, js);
                                break;
                            case "fl":
                                fl(lay, js);
                                break;
                        }
                        render2(gid, b, null, null);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void section(LinearLayout a, JSONObject b, int position) {
        try {
            String description = b.getString("correct");
            String description1 = Html.fromHtml(description).toString();
            String cleaned = b.getString("content");
            String cleaned1 = Html.fromHtml(cleaned).toString();
            String content1 = cleaned1.replace("\n", "");
            String content = content1.replace("&nbsp;", " ");
            String tag = b.optString("tag");
            try {
                content = content.substring(0, 1).toUpperCase() + content.substring(1);
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            View v = LayoutInflater.from(this).inflate(R.layout.section, a, false);
            TextView txt1 = v.findViewById(R.id.exam_description);
            TextView txt2 = v.findViewById(R.id.exam_content);
            txt1.setText(description1);
            txt2.setText(content);
            v.setVisibility(View.GONE);
            String qImage = b.optString("question_image");
            ImageView questionImage = v.findViewById(R.id.question_pic);
            if (!qImage.isEmpty()) {
                questionImage.setVisibility(View.VISIBLE);
                if (from.equals("assessment")) {
                    Picasso.get().load(Login.urlBase + "/" + qImage).into(questionImage);
                } else if (from.equals("preview")) {
                    if (tag != null && tag.equals("0")) {
                        questionImage.setImageURI(Uri.parse(qImage));
                    } else if (tag != null && tag.equals("1")) {
                        Picasso.get().load(Login.urlBase + "/" + qImage).into(questionImage);
                    }

                }
            }
            questionImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImagePreviewDialog imgDialog = new ImagePreviewDialog(ExamActivity.this, qImage, from, tag);
                    imgDialog.show();
                    imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                }
            });
            ImageView editBtn = v.findViewById(R.id.edit);
            editBtn.setTag(position);
            if (from != null && from.equals("cbt") || Objects.equals(from, "assessment")) {
                editBtn.setVisibility(View.GONE);
            }
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qPosition = position;
                    finish();
                }
            });

            a.addView(v);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void qp(LinearLayout a, JSONObject b) {
        try {
            String cleaned = b.getString("content");
            String cleaned1 = Html.fromHtml(cleaned).toString();
            String content = cleaned1.replace("&nbsp;", "");
            content = content.substring(0, 1).toUpperCase() + content.substring(1);
            View v = LayoutInflater.from(this).inflate(R.layout.qp, a, false);
            TextView txt2 = v.findViewById(R.id.qp_text);
            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this, R.font.kalam);
            }
            txt2.setTypeface(typeface);
            txt2.setText(content);
            v.setVisibility(View.GONE);
            v.setTag("qp");
            a.addView(v);
        } catch (JSONException e) {
        }
    }

    public void qs(LinearLayout a, JSONObject b, int position) {
        try {
            String cleaned = b.getString("content");
            String correct1 = b.optString("correct");
            String questionId = b.getString("id");
            String correct11 = Html.fromHtml(correct1).toString();
            String correct = Html.fromHtml(cleaned).toString();
            String qImage = b.optString("question_image");
            String tag = b.optString("tag");
            //String content = correct.replace("&nbsp;","");
            String content = cleaned;
            content = content.substring(0, 1).toUpperCase() + content.substring(1);
            View v = LayoutInflater.from(this).inflate(R.layout.qs, a, false);
            TextView cAns = v.findViewById(R.id.qs_correct_ans);
            ImageView questionImage = v.findViewById(R.id.question_pic);
            if (!qImage.isEmpty()) {
                questionImage.setVisibility(View.VISIBLE);
                if (from.equals("assessment")) {

                    Picasso.get().load(Login.urlBase + "/" + qImage).into(questionImage);
                } else if (from.equals("preview")) {
                    if (tag != null && tag.equals("0")) {
                        questionImage.setImageURI(Uri.parse(qImage));
                    } else if (tag != null && tag.equals("1")) {
                        Picasso.get().load(Login.urlBase + "/" + qImage).into(questionImage);
                    }

                }
            }
            questionImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImagePreviewDialog imgDialog = new ImagePreviewDialog(ExamActivity.this, qImage, from, tag);
                    imgDialog.show();
                    imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            });
            ImageView editBtn = v.findViewById(R.id.edit);
            editBtn.setTag(position);
            if (from != null && from.equals("cbt") || from.equals("assessment")) {
                editBtn.setVisibility(View.GONE);
            }
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qPosition = position;
                    finish();
                }
            });


            TextView nn = v.findViewById(R.id.qs_num);
            nn.setText("QUESTION " + number);
            TextView qId = v.findViewById(R.id.question_id);
            qId.setText(questionId);
            cAns.setText(correct11);
            TextView txt2 = v.findViewById(R.id.qs_text);
            txt2.setText(content);
            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this, R.font.kalam);
            }
            txt2.setTypeface(typeface);
            v.setVisibility(View.GONE);
            v.setTag("qs");
            a.addView(v);
            ++grading;
            number++;
        } catch (JSONException e) {
        }
    }

    public void fl(LinearLayout a, JSONObject b) {
        try {
            String cleaned = b.getString("content");
            String correct1 = b.optString("correct");
            String correct11 = Html.fromHtml(correct1).toString();
            String correct = Html.fromHtml(cleaned).toString();
            String content = cleaned;
            //content = content.substring(0,1).toUpperCase()+content.substring(1);
            View v = LayoutInflater.from(this).inflate(R.layout.fl, a, false);
            //TextView cAns=v.findViewById(R.id.qs_correct_ans);
            CardView l = v.findViewById(R.id.flash_v);
            l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.setRotation(180f);
                }
            });
            v.setVisibility(View.GONE);
            v.setTag("fl");
            a.addView(v);
            //++grading;
            //number++;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void qt(LinearLayout a, JSONObject b, int position) {
        try {
            String cleaned = b.getString("content");
            String correct1 = b.optString("correct");
            String correct11 = Html.fromHtml(correct1).toString();
            String correct = Html.fromHtml(cleaned).toString();
            String tag = b.optString("tag");
            String content = cleaned;
            content = content.substring(0, 1).toUpperCase() + content.substring(1);
            View v = LayoutInflater.from(this).inflate(R.layout.qt, a, false);
            TextView cAns = v.findViewById(R.id.qs_correct_ans);
            TextView nn = v.findViewById(R.id.qs_num);
            nn.setText("QUESTION " + number);
            cAns.setText(correct11);
            TextView txt2 = v.findViewById(R.id.qs_text);
            txt2.setText(content);
            String qImage = b.optString("question_image");
            ImageView questionImage = v.findViewById(R.id.question_pic);
            if (!qImage.isEmpty()) {
                questionImage.setVisibility(View.VISIBLE);
                if (from.equals("assessment")) {
                    Picasso.get().load(Login.urlBase + "/" + qImage).into(questionImage);
                } else if (from.equals("preview")) {
                    Log.i("response", "rea1" + qImage);
                    if (tag != null && tag.equals("0")) {
                        questionImage.setImageURI(Uri.parse(qImage));
                        Log.i("response", "rea2" + qImage);
                    } else if (tag != null && tag.equals("1")) {
                        Picasso.get().load(Login.urlBase + "/" + qImage).into(questionImage);
                        Log.i("response", "rea3" + qImage);

                    }
                }
            }

            ImageView editBtn = v.findViewById(R.id.edit);
            editBtn.setTag(position);
            if (from != null && from.equals("cbt") || from.equals("assessment")) {
                editBtn.setVisibility(View.GONE);
            }
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qPosition = position;
                    finish();
                }
            });
            questionImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImagePreviewDialog imgDialog = new ImagePreviewDialog(ExamActivity.this, qImage, from, tag);
                    imgDialog.show();
                    imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                }
            });
            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this, R.font.kalam);
            }
            txt2.setTypeface(typeface);
            v.setVisibility(View.GONE);
            v.setTag("qt");
            a.addView(v);
            ++grading;
            number++;
        } catch (JSONException e) {
        }
    }

    public void nb(LinearLayout a, JSONObject b) {
        try {
            String cleaned = b.getString("content");
            String correct1 = b.optString("correct");
            String correct11 = Html.fromHtml(correct1).toString();
            String correct = Html.fromHtml(cleaned).toString();
            String content = cleaned;
            content = content.substring(0, 1).toUpperCase() + content.substring(1);
            View v = LayoutInflater.from(this).inflate(R.layout.nb, a, false);
            TextView cAns = v.findViewById(R.id.qs_correct_ans);
            TextView nn = v.findViewById(R.id.qs_num);
            nn.setText("QUESTION " + number);
            cAns.setText(correct11);
            TextView txt2 = v.findViewById(R.id.qs_text);
            txt2.setText(content);
            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this, R.font.kalam);
            }
            txt2.setTypeface(typeface);
            v.setVisibility(View.GONE);
            v.setTag("nb");
            a.addView(v);
            ++grading;
            number++;
        } catch (JSONException e) {
        }
    }

    public void sl(LinearLayout a, JSONObject b) {
        try {
            String cleaned = b.getString("content");
            String correct1 = b.optString("correct");
            String correct11 = Html.fromHtml(correct1).toString();
            String correct = Html.fromHtml(cleaned).toString();
            String content = cleaned;
            content = content.substring(0, 1).toUpperCase() + content.substring(1);
            View v = LayoutInflater.from(this).inflate(R.layout.sl, a, false);
            TextView cAns = v.findViewById(R.id.qs_correct_ans);
            TextView nn = v.findViewById(R.id.qs_num);
            nn.setText("QUESTION " + number);
            cAns.setText(correct11);
            TextView txt2 = v.findViewById(R.id.qs_text);
            txt2.setText(content);
            Spinner spinner = v.findViewById(R.id.input_ans);
            List<String> list = new ArrayList<>();
            String answers = b.getString("answer");
            String[] optionArray = answers.split("\\|\\|");
            for (int c = 0; c < optionArray.length; c++) {
                list.add(optionArray[c]);
            }
            ArrayAdapter adapter = new ArrayAdapter(ExamActivity.this, android.R.layout.simple_dropdown_item_1line, list);
            spinner.setAdapter(adapter);

            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this, R.font.kalam);
            }
            txt2.setTypeface(typeface);
            v.setVisibility(View.GONE);
            v.setTag("sl");
            a.addView(v);
            ++grading;
            number++;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void tm(LinearLayout a, JSONObject b) {
        try {
            String cleaned = b.getString("content");
            String correct1 = b.optString("correct");
            String correct11 = Html.fromHtml(correct1).toString();
            String correct = Html.fromHtml(cleaned).toString();
            String content = cleaned;
            content = content.substring(0, 1).toUpperCase() + content.substring(1);
            View v = LayoutInflater.from(this).inflate(R.layout.tm, a, false);
            TextView cAns = v.findViewById(R.id.qs_correct_ans);

            EditText time = v.findViewById(R.id.input_ans);
            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(ExamActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            time.setText(selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });
            TextView nn = v.findViewById(R.id.qs_num);
            nn.setText("QUESTION " + number);
            cAns.setText(correct11);
            TextView txt2 = v.findViewById(R.id.qs_text);
            txt2.setText(content);
            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this, R.font.kalam);
            }
            txt2.setTypeface(typeface);
            v.setVisibility(View.GONE);
            v.setTag("tm");
            a.addView(v);
            ++grading;
            number++;
        } catch (JSONException e) {
        }
    }

    public void dt(LinearLayout a, JSONObject b) {
        try {
            String cleaned = b.getString("content");
            String correct1 = b.optString("correct");
            String correct11 = Html.fromHtml(correct1).toString();
            String correct = Html.fromHtml(cleaned).toString();
            String content = cleaned;
            content = content.substring(0, 1).toUpperCase() + content.substring(1);
            View v = LayoutInflater.from(this).inflate(R.layout.dt, a, false);
            TextView cAns = v.findViewById(R.id.qs_correct_ans);
            TextView nn = v.findViewById(R.id.qs_num);
            EditText date = v.findViewById(R.id.input_ans);
            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(ExamActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            date.setText(i + "-" + (i1 + 1) + "-" + i2);
                        }
                    }, year, month, day);
                    datePickerDialog.show();
                }
            });
            nn.setText("QUESTION " + number);
            cAns.setText(correct11);
            TextView txt2 = v.findViewById(R.id.qs_text);
            txt2.setText(content);
            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this, R.font.kalam);
            }
            txt2.setTypeface(typeface);
            v.setVisibility(View.GONE);
            v.setTag("dt");
            a.addView(v);
            ++grading;
            number++;
        } catch (JSONException e) {
        }
    }

    public void em(LinearLayout a, JSONObject b) {
        try {
            String cleaned = b.getString("content");
            String correct1 = b.optString("correct");
            String correct11 = Html.fromHtml(correct1).toString();
            String correct = Html.fromHtml(cleaned).toString();
            String content = cleaned;
            content = content.substring(0, 1).toUpperCase() + content.substring(1);
            View v = LayoutInflater.from(this).inflate(R.layout.em, a, false);
            TextView cAns = v.findViewById(R.id.qs_correct_ans);
            TextView nn = v.findViewById(R.id.qs_num);
            nn.setText("QUESTION " + number);
            cAns.setText(correct11);
            TextView txt2 = v.findViewById(R.id.qs_text);
            txt2.setText(content);
            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this, R.font.kalam);
            }
            txt2.setTypeface(typeface);
            v.setVisibility(View.GONE);
            v.setTag("em");
            a.addView(v);
            ++grading;
            number++;
        } catch (JSONException e) {
        }
    }

    public void qo(LinearLayout a, JSONObject b, int position) {
        try {
            String questionId = b.getString("id");
            String cleaned = b.getString("content");
            String cleaned1 = Html.fromHtml(cleaned).toString();
            //String content1 = cleaned1;
            String tag = b.optString("tag");
            String content = cleaned;
            try {
                content = content.substring(0, 1).toUpperCase() + content.substring(1);
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            View v = LayoutInflater.from(this).inflate(R.layout.qo, a, false);
            TextView txt = v.findViewById(R.id.qo_text);
            TextView qId = v.findViewById(R.id.question_id);
            qId.setText(questionId);
            TextView nn = v.findViewById(R.id.qs_num);
            nn.setText("QUESTION " + number);

            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this, R.font.kalam);
            }
            txt.setTypeface(typeface);

            txt.setText(content);

            ImageView editBtn = v.findViewById(R.id.edit);
            editBtn.setTag(position);
            if (from != null && from.equals("cbt") || Objects.equals(from, "assessment")) {
                editBtn.setVisibility(View.GONE);
            }
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qPosition = position;
                    finish();
                }
            });
            v.setVisibility(View.GONE);
            String qImage = b.optString("question_image");
            ImageView questionImage = v.findViewById(R.id.question_pic);
            if (!qImage.isEmpty()) {
                questionImage.setVisibility(View.VISIBLE);
                if (from.equals("assessment")) {
                    Picasso.get().load(Login.urlBase + "/" + qImage).into(questionImage);
                    Log.i("response", "Img " + qImage);
                    Log.i("responseImg", "Img " + Login.urlBase);

                } else if (from.equals("cbt")) {
                    Picasso.get().load(getResources().getString(R.string.file_url) + "/" + qImage).into(questionImage);

                } else if (from.equals("preview")) {
                    if (tag != null && tag.equals("0")) {
                        questionImage.setImageURI(Uri.parse(qImage));
                    } else if (tag != null && tag.equals("1")) {
                        Picasso.get().load(Login.urlBase + "/" + qImage).into(questionImage);
                    }
                }
            }
            String finalQImage = qImage;
            questionImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImagePreviewDialog imgDialog = new ImagePreviewDialog(ExamActivity.this, finalQImage, from, tag);
                    imgDialog.show();
                    imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                }
            });
            v.setTag("qo");
            a.addView(v);
            grading++;
            number++;
            af((LinearLayout) v, b, true);
        } catch (JSONException e) {

        }
    }

    public void qm(LinearLayout a, JSONObject b) {
        try {
            String cleaned = b.getString("content");
            String cleaned1 = Html.fromHtml(cleaned).toString();
            String questionId = b.getString("id");
            String content1 = cleaned1.replace("&nbsp;", "");
            String content = cleaned;
            try {
                content = content.substring(0, 1).toUpperCase() + content.substring(1);
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            View v = LayoutInflater.from(this).inflate(R.layout.qo, a, false);
            TextView txt = v.findViewById(R.id.qo_text);
            TextView nn = v.findViewById(R.id.qs_num);
            nn.setText("QUESTION " + number);
            TextView qId = v.findViewById(R.id.question_id);
            qId.setText(questionId);
            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = ResourcesCompat.getFont(this, R.font.kalam);
            }
            txt.setTypeface(typeface);

            txt.setText(content);
            v.setVisibility(View.GONE);
            v.setTag("qm");
            a.addView(v);
            grading++;
            number++;
            af2((LinearLayout) v, b, true);
        } catch (JSONException e) {

        }
    }

    public void qf(LinearLayout a, JSONObject b) {
        try {
            String cleaned = b.getString("content");
            String cleaned1 = Html.fromHtml(cleaned).toString();
            String content1 = cleaned1.replace("&nbsp;", "");
            String content = cleaned;
            content = content.substring(0, 1).toUpperCase() + content.substring(1);
            View v = LayoutInflater.from(this).inflate(R.layout.qf, a, false);
            TextView txt2 = v.findViewById(R.id.exam_question);
            txt2.setText(content);
            v.setVisibility(View.GONE);
            v.setTag("qf");
            a.addView(v);
        } catch (JSONException e) {
        }
    }

    @SuppressLint("RestrictedApi")
    public void af(LinearLayout a, JSONObject b, boolean visible) {
        float scale = getResources().getDisplayMetrics().density;
        int dp = (int) (16 * scale + 0.5f);
        try {
            b.optString("answer");
            if (!b.optString("answer").isEmpty()) {
                String cleaned = b.optString("answer");
                String correctValue = b.getString("correct");

                JSONObject correctObject = new JSONObject(correctValue);

                String order = correctObject.optString("order");
                String correct = correctObject.optString("text");
                String content1;
                View v;
                if (visible) {
                    v = LayoutInflater.from(this).inflate(R.layout.optionsview, a, false);
                } else {
                    v = LayoutInflater.from(this).inflate(R.layout.card, a, false);
                }
                content1 = cleaned.replace("&nbsp;", "");
                content = cleaned.split("\\|\\|");
                //Log.i("response",content[1]);
                //Log.i("point","point 1");


                final LinearLayout q = v.findViewById(R.id.qd);
                TextView ans = v.findViewById(R.id.correct_ans);
                final TextView your_ans = v.findViewById(R.id.your_ans);

                final String correct1 = Html.fromHtml(correct).toString();
                if (cleaned.contains("||")) {
                    content = cleaned.split("\\|\\|");

                    renderQO(content, correct1, q, ans, a);

                } else {
                    JSONArray jsonArray = new JSONArray(cleaned);

                    renderQO(jsonArray, correct1, q, ans, a);

                }
                if (!visible) {
                    LinearLayout a2 = v.findViewById(R.id.caard);
                    View v2 = LayoutInflater.from(this).inflate(R.layout.optionsview, a2, false);
                    v.setVisibility(View.GONE);
                    v.setTag("af");
                    TextView numbering = v.findViewById(R.id.numbering);
                    numbering.setText("QUESTION " + number);
                    number++;
                    grading++;
                    a2.addView(v2);
                    a.addView(v);
                } else {
                    LinearLayout a3 = a.findViewById(R.id.card_lin);
                    a3.addView(v);
                }

                for (int c = 0; c < q.getChildCount(); c++) {
                    final CardView r = (CardView) q.getChildAt(c);
                    final TextView k = q.getChildAt(c).findViewById(R.id.opt);
                    r.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkNext = false;
                            your_ans.setText(k.getText().toString());
                            wonder();
                            for (int c = 0; c < q.getChildCount(); c++) {
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
        } catch (Exception e) {
            finish();
            e.printStackTrace();
            Toast.makeText(this, "Exam Cannot Be Loaded at this Time!",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void af2(LinearLayout a, JSONObject b, boolean visible) {
        float scale = getResources().getDisplayMetrics().density;
        int dp = (int) (16 * scale + 0.5f);
        try {
            if (b.getString("answer") != null) {
                String cleaned = b.optString("answer");
                String correct = b.optString("correct");
                String content1;
                View v;
                if (visible) {
                    v = LayoutInflater.from(this).inflate(R.layout.optionsview, a, false);
                } else {
                    v = LayoutInflater.from(this).inflate(R.layout.card, a, false);
                }
                content1 = cleaned.replace("&nbsp;", "");
                content = content1.split("\\|\\|");
                //Log.i("response",content[1]);
                //Log.i("point","point 1");


                final LinearLayout q = v.findViewById(R.id.qd);
                TextView ans = v.findViewById(R.id.correct_ans);
                final TextView your_ans = v.findViewById(R.id.your_ans);

                final String correct1 = Html.fromHtml(correct).toString();
                ans.setText(correct1);
                int t = 0;

                try {
                    if (!correct1.isEmpty()) {
                        if (from.equalsIgnoreCase("cbt")) {
                            String t_st = Character.toString(correct1.charAt(1));
                            t = Integer.parseInt(t_st) - 1;
                        } else if (from.equalsIgnoreCase("assessment")) {

                        }
                    }


                    int duration = 300;
                    long startTimev = 100;
                    for (int j = 0; j < content.length; j++) {
                        String str = Html.fromHtml(content[j]).toString();
                        try {
                            str = str.substring(0, 1).toUpperCase() + str.substring(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        str = numbs[j] + ".  " + str.replace("\n", "");


                        View vi = LayoutInflater.from(this).inflate(R.layout.options2_layout, a, false);
                        CardView card = vi.findViewById(R.id.view_cont);

                        TextView k = vi.findViewById(R.id.opt);
                        k.setText(str);
                        q.addView(vi);

                        card.startAnimation(inFromLeftAnimation(duration, j * startTimev));


                    }
                } catch (NumberFormatException e) {//finish();
                    e.printStackTrace();
                    //Toast.makeText(this, "Exam Cannot Be Loaded at this Time!!", Toast.LENGTH_LONG).show();
                }
                if (!visible) {
                    LinearLayout a2 = v.findViewById(R.id.caard);
                    View v2 = LayoutInflater.from(this).inflate(R.layout.optionsview, a2, false);
                    v.setVisibility(View.GONE);
                    v.setTag("af");
                    TextView numbering = v.findViewById(R.id.numbering);
                    numbering.setText("QUESTION " + number);
                    number++;
                    grading++;
                    a2.addView(v2);
                    a.addView(v);
                } else {
                    LinearLayout a3 = a.findViewById(R.id.card_lin);
                    a3.addView(v);
                }


                for (int c = 0; c < q.getChildCount(); c++) {
                    final CardView r = (CardView) q.getChildAt(c);
                    final TextView k = q.getChildAt(c).findViewById(R.id.opt);
                    CheckBox checkBox = q.getChildAt(c).findViewById(R.id.checkbox);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            try {
                                String h = "";
                                if (isChecked) {
                                    if (!your_ans.getText().toString().isEmpty()) {
                                        h = your_ans.getText().toString() + "||";
                                    }
                                    //answr += k.getText().toString().substring(3) + "||";
                                    String answer = h + k.getText().toString().substring(3) + "||";
                                    answer = answer.substring(0, answer.length() - 2);
                                    answer = answer.replace(" ", "");
                                    your_ans.setText(answer);
                                } else {
                                    String answerText = your_ans.getText().toString().trim();
                                    String answer1 = k.getText().toString().substring(3).trim();
                                    String ans = answerText.replace(answer1, "");
                                    String b = "";
                                    String[] a = answerText.split("\\|\\|");
                                    for (int c = 0; c < a.length; c++) {
                                        b += a[c] + " ";
                                    }
                                    String e = b.replace(" ", "||");
                                    e = e.replace(answer1 + "||", "");
                                    e = e.substring(0, e.length() - 2);
                                    if (e.substring(0, 2).equalsIgnoreCase("\\|\\|")) {
                                        e = e.substring(3);
                                        Log.i("respons1", e.substring(2));

                                    }
                                    Log.i("respons1", e);

                                    your_ans.setText(e);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });/*
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

                    });*/


                }

            }
        } catch (Exception e) {
            finish();
            e.printStackTrace();
            Toast.makeText(this, "Exam Cannot Be Loaded at this Time!!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void wonder() {
        final ImageButton jl = findViewById(R.id.fwd);
        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!checkNext) jl.performClick();
                    }
                });
            }
        }, 1000);
    }

    void startTimer() {
        cTimer = new CountDownTimer(time, 1000) {
            TextView m = (TextView) findViewById(R.id.exam);
            RelativeLayout mc = (RelativeLayout) findViewById(R.id.exam_cont);

            public void onTick(long millisUntilFinished) {
                int sec = (int) (millisUntilFinished / 1000);
                int hrs = (int) Math.floor(sec / 3600);
                int minutes = (int) Math.floor((sec % 3600) / 60);
                int seconds = (sec % 3600) % 60;
                mc.setVisibility(View.VISIBLE);
                m.setText(String.format("%02d", hrs)
                        + ":" + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }

            public void onFinish() {
                m.setText("");
                ScrollView s = findViewById(R.id.scrollview_exam);
                RelativeLayout r = findViewById(R.id.nav);
                ScrollView s1 = findViewById(R.id.sub_scroll);
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

    public void check(View view) {
        TextView m = findViewById(R.id.exam);
        if (!from.equals("preview")) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle("Submit Exam")
                    .setMessage("Are you sure you want to submit this exam?" + " Time remaining is " + m.getText().toString())
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (from.equalsIgnoreCase("assessment")) {
                                if (accessLevel.equalsIgnoreCase("-1")) {
                                    buildJson();
                                } else {
                                    submitExam();
                                }
                            } else {
                                submitExam();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(R.drawable.ic_warning)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (!from.equals("preview")) {
            if (exam_on == true) {
                Button b = findViewById(R.id.submitBtn);
                b.performClick();
            } else {
                finish();
            }
        } else {

            super.onBackPressed();
        }
    }

    public void calcTime(String a) {
        String[] b = a.split(" ");
        try {
            int len = b.length;
            char first = Character.toLowerCase(b[0].charAt(0));

            int t1 = NumberFormat.getInstance().parse(b[0]).intValue();
            int t2;
            if (Character.toString(first).equals("h")) {
                t1 = t1 * 3600000;
            } else {
                t1 = t1 * 60000;
            }
            if (len > 2) {
                t2 = NumberFormat.getInstance().parse(b[0]).intValue();
                t2 = t2 * 60000;
            } else {
                t2 = 0;
            }
            time = t1 + t2;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        startTimer();
    }

    public void anotherTest(View view) {
        finish();
    }

    public void takeTest(View view) {
        LinearLayout h = findViewById(R.id.hif);
        h.setVisibility(View.GONE);
        RelativeLayout r = findViewById(R.id.nav);
        ScrollView s = findViewById(R.id.scrollview_exam);
        ScrollView e = findViewById(R.id.exam_introduction);
        r.setVisibility(view.VISIBLE);
        s.setVisibility(view.VISIBLE);
        e.setVisibility(view.GONE);
        LinearLayout parent = findViewById(R.id.displayExam);
        if (!from.equals("preview")) {
            current = parent.getChildAt(parent.indexOfChild(current) + 1);
        } else {
            current = parent.getChildAt(ab);
        }
        current.setVisibility(view.VISIBLE);
        calcTime(description);
        qPosition = ab;
        exam_on = true;
    }

    public void takeTest() {
        LinearLayout h = findViewById(R.id.hif);
        h.setVisibility(View.GONE);
        RelativeLayout r = findViewById(R.id.nav);
        ScrollView s = findViewById(R.id.scrollview_exam);
        ScrollView e = findViewById(R.id.exam_introduction);
        r.setVisibility(View.VISIBLE);
        s.setVisibility(View.VISIBLE);
        e.setVisibility(View.GONE);
        LinearLayout parent = findViewById(R.id.displayExam);
        if (!from.equals("preview")) {
            current = parent.getChildAt(parent.indexOfChild(current) + 1);
        } else {
            current = parent.getChildAt(ab);
        }
        current.setVisibility(View.VISIBLE);
        if (!description.isEmpty()) {
            calcTime(description);
        }
        qPosition = ab;
        exam_on = true;
    }

    public void back(View view) {
        LinearLayout parent = findViewById(R.id.displayExam);
        prevView = parent.getChildAt(parent.indexOfChild(current) - 1);
        int i = parent.indexOfChild(current);
        ImageButton p = findViewById(R.id.prev);
        ImageButton f = findViewById(R.id.fwd);
        if (i == 1) p.setVisibility(View.GONE);
        if (prevView != null) {
            f.setVisibility(View.VISIBLE);
            current.setVisibility(view.GONE);
            current = prevView;
            prevView.setVisibility(view.VISIBLE);
        }
        ab = ab - 1;
        qPosition = ab;
    }

    public void next(View view) {
        checkNext = true;
        LinearLayout parent = findViewById(R.id.displayExam);
        int i = parent.indexOfChild(current);
        ImageButton f = findViewById(R.id.fwd);
        ImageButton p = findViewById(R.id.prev);
        nextView = parent.getChildAt(i + 1);
        if (parent.getChildAt(i + 2) == null) f.setVisibility(View.GONE);
        if (nextView != null) {
            p.setVisibility(View.VISIBLE);
            current.setVisibility(view.GONE);
            current = nextView;
            nextView.setVisibility(view.VISIBLE);
        }
        ab = ab + 1;
        qPosition = ab;
    }

    public void submitExam() {
        ScrollView s = findViewById(R.id.scrollview_exam);
        RelativeLayout r = findViewById(R.id.nav);
        ScrollView s1 = findViewById(R.id.sub_scroll);
        TextView m = findViewById(R.id.exam);
        RelativeLayout mc = findViewById(R.id.exam_cont);
        LinearLayout hif = findViewById(R.id.hif);
        hif.setVisibility(View.VISIBLE);
        mc.setVisibility(View.GONE);
        if (timeOn) {
            cTimer.cancel();
        }
        s.setVisibility(View.GONE);
        r.setVisibility(View.GONE);
        s1.setVisibility(View.VISIBLE);
        calcGrade();
        exam_on = false;
    }

    public void viewResult(View view) {
        float scale = getResources().getDisplayMetrics().density;
        int dp = (int) (0 * scale + 0.5f);
        ScrollView sub = findViewById(R.id.sub_scroll);
        sub.setVisibility(View.GONE);
        ScrollView s = findViewById(R.id.scrollview_exam);
        s.setVisibility(View.VISIBLE);
        s.setPadding(dp, dp, dp, dp);
        LinearLayout v = findViewById(R.id.displayExam);
        int childCount = v.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = v.getChildAt(i);
            child.setVisibility(View.GONE);
            if (child.getTag() == "qo" || child.getTag() == "af" || child.getTag() == "qm") {
                RelativeLayout r = findViewById(R.id.nav);
                r.setVisibility(View.GONE);
                LinearLayout group = child.findViewById(R.id.question);
                TextView qu = child.findViewById(R.id.qo_text);
                TextView correct = child.findViewById(R.id.correct_ans);
                TextView correctText = child.findViewById(R.id.t_correct_ans);
                TextView ansText = child.findViewById(R.id.t_your_ans);
                TextView ans = child.findViewById(R.id.your_ans);

                TextView cBar = child.findViewById(R.id.colorBar);
                TextView verdict = child.findViewById(R.id.verdict);
                ImageView img = child.findViewById(R.id.verdict_img);
                FrameLayout img1 = child.findViewById(R.id.verdict_fram);
                if (correct != null) correct.setVisibility(View.VISIBLE);
                if (ans != null) ans.setVisibility(View.VISIBLE);
                if (group != null) group.setVisibility(View.GONE);
                if (verdict != null) {
                    correctText.setText("Correct Answer");
                    String h = ans.getText().toString();
                    if (h != null && !h.equals("")) {
                        ansText.setText("Your Answer");
                        ansText.setVisibility(View.VISIBLE);
                        if (img1 != null) img1.setVisibility(View.VISIBLE);
                    }
                    if (verdict.getText().toString().trim().equals("correct")) {
                        cBar.setVisibility(View.VISIBLE);
                        img.setImageDrawable(getResources().getDrawable(R.drawable.good));
                    } else {
                        cBar.setVisibility(View.VISIBLE);
                        cBar.setBackgroundColor(ContextCompat.getColor(ExamActivity.this, R.color.redH));
                        img.setImageDrawable(getResources().getDrawable(R.drawable.bad));
                    }
                }
                child.setVisibility(View.VISIBLE);
            }
            if (child.getTag() == "qs" || child.getTag() == "qt" || child.getTag() == "em" || child.getTag() == "nb" || child.getTag() == "dt" || child.getTag() == "tm") {
                TextInputLayout tt = child.findViewById(R.id.editable);
                if (tt != null) tt.setVisibility(View.GONE);
                EditText at = child.findViewById(R.id.input_ans);
                TextView at1 = child.findViewById(R.id.your_ans);
                String j = at.getText().toString();
                TextView ansText = child.findViewById(R.id.t_your_ans);

                if (j != null && !j.equals("")) {
                    ansText.setText("Your Answer");
                    ansText.setVisibility(View.VISIBLE);
                    at1.setText(j);
                    at1.setVisibility(View.VISIBLE);
                }
                TextView correct = child.findViewById(R.id.qs_correct_ans);
                if (correct != null) correct.setVisibility(View.VISIBLE);
                child.setVisibility(View.VISIBLE);
                TextView cA = child.findViewById(R.id.qs_correct_ans_txt);
                cA.setVisibility(View.VISIBLE);
            }
        }
    }

    public void calcGrade() {
        LinearLayout v = findViewById(R.id.displayExam);
        int t = 0;
        int childCount = v.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = v.getChildAt(i);
            if (child.getTag() == "qo" || child.getTag() == "af" || child.getTag() == "qm") {
                t++;
                TextView correct = child.findViewById(R.id.correct_ans);
                TextView ans = child.findViewById(R.id.your_ans);
                TextView verdict = child.findViewById(R.id.verdict);
                if (ans == null || correct == null) continue;
                if (correct.getText().toString().trim().equals("")) continue;
                if (ans.getText().toString().equalsIgnoreCase(correct.getText().toString())) {
                    verdict.setText("correct");
                    correctGrade++;
                } else verdict.setText("wrong");
            }
            /*else if(child.getTag()=="qs" || child.getTag()=="em" ||child.getTag()=="nb"||child.getTag()=="dt"||child.getTag()=="tm"){
                EditText at= child.findViewById(R.id.input_ans);
                String j=at.getText().toString().trim();


                TextView correct=child.findViewById(R.id.qs_correct_ans);
                TextView verdict= child.findViewById(R.id.verdict);
                if(j==null || correct==null)continue;
                if(correct.getText().toString().trim().equals(""))continue;
                if(j.equalsIgnoreCase(correct.getText().toString().trim()))
                {
                    //verdict.setText("correct");
                    correctGrade++;
                }else {
                    //verdict.setText("wrong");
                }
            }*/
        }
        TextView scoreText = findViewById(R.id.submit_score);
        TextView total = findViewById(R.id.total_score);
        total.setText(String.valueOf(t));
        String gradeStatement = "" + correctGrade;
        scoreText.setText(gradeStatement);
        LinearLayout l = findViewById(R.id.score_layout);
        if (t > 0) {
            l.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    private Animation inFromLeftAnimation(int duration, Long startTime) {
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

    private void buildJson() {

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");
        String username = sharedPreferences.getString("user", "");
        String db = sharedPreferences.getString("db", "");

        LinearLayout v = findViewById(R.id.displayExam);
        int childCount = v.getChildCount();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < childCount; i++) {
            View child = v.getChildAt(i);
            if (child.getTag() == "qo" || child.getTag() == "af" || child.getTag() == "qm") {
                TextView qu = child.findViewById(R.id.qo_text);
                TextView correct = child.findViewById(R.id.correct_ans);
                TextView correctText = child.findViewById(R.id.t_correct_ans);
                TextView ansText = child.findViewById(R.id.t_your_ans);
                TextView qId = child.findViewById(R.id.question_id);
                TextView ans = child.findViewById(R.id.your_ans);
                try {
                    JSONObject object = new JSONObject();
                    object.put("id", qId.getText().toString());
                    object.put("question", qu.getText().toString());
                    object.put("correct", correct.getText().toString());
                    object.put("answer", ans.getText().toString());
                    object.put("type", child.getTag().toString());

                    jsonArray.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            if (child.getTag() == "qs" || child.getTag() == "em" || child.getTag() == "nb" || child.getTag() == "dt" || child.getTag() == "tm" || child.getTag() == "qt") {
                TextInputLayout tt = child.findViewById(R.id.editable);
                TextView qu = child.findViewById(R.id.qs_text);
                EditText at = child.findViewById(R.id.input_ans);
                TextView at1 = child.findViewById(R.id.your_ans);
                String j = at.getText().toString();
                TextView qId = child.findViewById(R.id.question_id);
                TextView correct = child.findViewById(R.id.qs_correct_ans);
                TextView cA = child.findViewById(R.id.qs_correct_ans_txt);
                try {
                    JSONObject object = new JSONObject();
                    object.put("id", qId.getText().toString());
                    object.put("question", qu.getText().toString());
                    object.put("correct", correct.getText().toString());
                    object.put("answer", j);
                    object.put("type", child.getTag().toString());

                    jsonArray.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        boolean check = false;
        for (int b = 0; b < jsonArray.length(); b++) {
            try {
                JSONObject object = jsonArray.getJSONObject(b);
                String answer = object.getString("answer");
                if (!answer.isEmpty()) {
                    check = true;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!check) {
            Toast.makeText(ExamActivity.this, "You have not answered any question", Toast.LENGTH_SHORT).show();
            return;
        }
        submitExam();
        CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        String url = Login.urlBase + "/addResponse.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equalsIgnoreCase("success")) {
                        Toast.makeText(ExamActivity.this, "Test submitted successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(ExamActivity.this, "Response submission failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(ExamActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("data", jsonArray.toString());
                params.put("exam_id", examId);
                params.put("user_id", userId);
                params.put("username", username);
                params.put("level", levelId);
                params.put("course", courseId);
                params.put("score", correctGrade.toString());
                params.put("course_name", course);
                params.put("_db", db);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void renderQO(JSONArray jsonArray, String correct1, LinearLayout q, TextView ans, LinearLayout a) {
        int t = 0;

        try {
            /* Toast.makeText(this, correct1, Toast.LENGTH_SHORT).show();*/
            int duration = 300;
            long startTimev = 100;

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                String text = jsonObject.getString("text");
                String image = jsonObject.optString("filename");
                String tag = jsonObject.optString("tag");

                if (!correct1.isEmpty()) {
                    String ct = correct1.substring(0, 1);
                    char cl1 = correct1.charAt(0);

                    if (ct.equalsIgnoreCase("A") && Character.isDigit(cl1) || ct.equalsIgnoreCase("R") && Character.isDigit(cl1)) {
                        String t_st = Character.toString(correct1.charAt(1));
                        t = Integer.parseInt(t_st) - 1;

                    } else {
                        if (text.trim().equalsIgnoreCase(correct1.trim())) {
                            t = j;


                            //Toochi Dennis :)

                            String str = text;
                            try {
                                str = str.substring(0, 1).toUpperCase() + str.substring(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            str = numbs[j] + ".  " + str.replace("\n", "");

                            ans.setText(str);
                        }
                    }
                                /*String t_st = Character.toString(correct1.charAt(1));
                                try {
                                    t = Integer.parseInt(t_st) - 1;
                                }catch (NumberFormatException e){
                                    e.printStackTrace();
                                    if (content[j].trim().equalsIgnoreCase(correct1.trim())) {
                                    t=j;
                                    }
                                }*/

                }

                String str = text;
                try {
                    str = str.substring(0, 1).toUpperCase() + str.substring(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                str = numbs[j] + ".  " + str.replace("\n", "");


                View vi = LayoutInflater.from(this).inflate(R.layout.options_layout, a, false);
                CardView card = vi.findViewById(R.id.view_cont);

                TextView k = vi.findViewById(R.id.opt);
                ImageView imageView = vi.findViewById(R.id.option_image);
                if (image != null && !image.isEmpty()) {
                    imageView.setVisibility(View.VISIBLE);
                    if (from.equals("assessment") || from.equals("cbt")) {
                        image = image.substring(2);
                        String imagePath = getResources().getString(R.string.file_url) + "" + image;
                        Picasso.get().load(imagePath).into(imageView);
                    } else if (from.equals("preview")) {
                        if (tag != null && tag.equals("0")) {
                            imageView.setImageURI(Uri.parse(image));
                        } else if (tag != null && tag.equals("1")) {
                            image = image.substring(2);
                            Picasso.get().load(Login.urlBase + "/" + image).into(imageView);
                        }
                    }
                }


                String finalImage = image;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePreviewDialog imgDialog = new ImagePreviewDialog(ExamActivity.this, finalImage, from, tag);
                        imgDialog.show();
                        imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }
                });
                k.setText(str);
                q.addView(vi);

                card.startAnimation(inFromLeftAnimation(duration, j * startTimev));


                if (from.equalsIgnoreCase("cbt")) {
                    if (j == t) {
                        ans.setText(str);
                    }
                } else if (from.equalsIgnoreCase("assessment")) {
                    String newst = content[0].replace("text", "");
                    newst = newst.replace("{", "");
                    newst = newst.replace("}", "");
                    newst = newst.replace(":", "");
                    newst = newst.replace("\"", "").trim();
                    String[] list = newst.split(",");


                    if (correct1.trim().equalsIgnoreCase(list[j])) {
                        ans.setText(str);
                    }
                }
            }
        } catch (NumberFormatException | JSONException e) {//finish();
            e.printStackTrace();
            //Toast.makeText(this, "Exam Cannot Be Loaded at this Time!!", Toast.LENGTH_LONG).show();
        }
    }

    private void renderQO(String[] arr, String correct1, LinearLayout q, TextView ans, LinearLayout a) {
        int t = 0;

        try {

            int duration = 300;
            long startTimev = 100;
            for (int j = 0; j < arr.length; j++) {

                if (!correct1.isEmpty()) {
                    String ct = correct1.substring(0, 1);
                    char cl1 = correct1.charAt(1);

                    if (ct.equalsIgnoreCase("A") && Character.isDigit(cl1) || ct.equalsIgnoreCase("R") && Character.isDigit(cl1)) {
                        String t_st = Character.toString(correct1.charAt(1));
                        t = Integer.parseInt(t_st) - 1;


                    } else {
                        if (arr[j].trim().equalsIgnoreCase(correct1.trim())) {
                            t = j;

                            String str = arr[j];
                            try {
                                str = str.substring(0, 1).toUpperCase() + str.substring(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            str = numbs[j] + ".  " + str.replace("\n", "");

                            ans.setText(str);
                        }
                    }
                                /*String t_st = Character.toString(correct1.charAt(1));
                                try {
                                    t = Integer.parseInt(t_st) - 1;
                                }catch (NumberFormatException e){
                                    e.printStackTrace();
                                    if (content[j].trim().equalsIgnoreCase(correct1.trim())) {
                                    t=j;
                                    }
                                }*/

                }

                String str = arr[j];
                try {
                    str = str.substring(0, 1).toUpperCase() + str.substring(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                str = numbs[j] + ".  " + str.replace("\n", "");


                View vi = LayoutInflater.from(this).inflate(R.layout.options_layout, a, false);
                CardView card = vi.findViewById(R.id.view_cont);

                TextView k = vi.findViewById(R.id.opt);

                k.setText(str);
                q.addView(vi);

                card.startAnimation(inFromLeftAnimation(duration, j * startTimev));


                if (from.equalsIgnoreCase("cbt")) {
                    if (j == t) {
                        ans.setText(str);

                    }
                } else if (from.equalsIgnoreCase("assessment")) {
                    if (correct1.trim().equalsIgnoreCase(content[j].trim())) {
                        ans.setText(str);
                    }
                }
            }
        } catch (NumberFormatException e) {//finish();
            e.printStackTrace();
            //Toast.makeText(this, "Exam Cannot Be Loaded at this Time!!", Toast.LENGTH_LONG).show();
        }
    }
}
