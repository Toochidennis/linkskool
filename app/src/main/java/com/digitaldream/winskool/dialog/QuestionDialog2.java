package com.digitaldream.winskool.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.digitaldream.winskool.activities.Login;
import com.digitaldream.winskool.activities.TestUpload;
import com.digitaldream.winskool.adapters.OptionsAdapter2;
import com.digitaldream.winskool.adapters.QuestionAdapter2;
import com.digitaldream.winskool.activities.ExamActivity;
import com.digitaldream.winskool.models.OptionsModel;
import com.digitaldream.winskool.models.QuestionsModel;
import com.digitaldream.winskool.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class QuestionDialog2 extends DialogFragment implements OptionsAdapter2.OnClickListener1 {
    private Activity activity;
    public static QuestionsModel qm;
    int position, questionNumber;
    public static OptionsAdapter2 adapter;
    private EditText questionText, answerText, sectionTitle, sectionDesc;
    public static List<OptionsModel> optionsList1, optionsList;
    private static final int REQUEST_PERMISSIONS = 100;
    public static final int SELECT_IMAGE = 5, SELECT_OPTION_IMAGE = 6;
    public static ImageView pic;
    private LinearLayout backBtn, forwardBtn, applyBtn, addOptionBtn, previewBtn;
    public static LinearLayout qNumberContainer, addImgContainer;
    public static RelativeLayout imageContainer;
    private ImageView deleteBtn;
    private boolean show = true;
    String imagePath;
    public static int optionPosition;


    public QuestionDialog2(@NonNull Activity context, QuestionsModel qm, int position, int questionNumber) {
        // super(context);
        this.activity = context;
        this.position = position;
        this.qm = qm;
        this.questionNumber = questionNumber;
    }


/*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  requestWindowFeature(Window.FEATURE_NO_TITLE);
     //   new Dialog(getContext(), R.style.DialogTheme);
      //  setContentView(R.layout.question_dialog2);
*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.question_dialog2, container,
                false);


        LinearLayout layout = view.findViewById(R.id.root_view);
        backBtn = view.findViewById(R.id.back);
        forwardBtn = view.findViewById(R.id.forward);
        hideShow(position);
        setDailog(layout);

        backBtn.setOnClickListener(v -> {
            position = position - 1;
            qm = QuestionAdapter2.list.get(position);
            setDailog(layout);
        });
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = position + 1;
                qm = QuestionAdapter2.list.get(position);
                setDailog(layout);
            }
        });

        applyBtn = view.findViewById(R.id.apply2);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply2();
                apply1();
            }
        });

        addOptionBtn = view.findViewById(R.id.add_opt);
        addOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qm.getQuestionType().equals("multiple_choice")) {
                    OptionsModel md = new OptionsModel();
                    md.setOptionText("");
                    md.setType("multichoice");
                    md.setChecked(false);
                    optionsList.add(md);
                    adapter.notifyDataSetChanged();
                } else if (qm.getQuestionType().equals("check_box")) {
                    OptionsModel md = new OptionsModel();
                    md.setOptionText("");
                    md.setType("checkbox");
                    md.setChecked(false);
                    optionsList1.add(md);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        previewBtn = view.findViewById(R.id.previ);

        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExamActivity.ab = position;
                apply1();
                new TestUpload().preview();
                Intent intent1 = new Intent(getContext(),
                        ExamActivity.class);
                intent1.putExtra("course", "");
                intent1.putExtra("Json", TestUpload.jsonObject1.toString());
                intent1.putExtra("year", "");
                intent1.putExtra("from", "preview");
                getContext().startActivity(intent1);

            }
        });

        if (qm.getQuestionType().equals("multiple_choice") || qm.getQuestionType().equals("check_box")) {
            addOptionBtn.setVisibility(View.VISIBLE);
        }

        ImageView imageView = view.findViewById(R.id.backBtn);

        imageView.setOnClickListener(v -> {
            dismiss();
        });


        return view;
    }


    private void setDailog(LinearLayout a) {
        a.removeAllViews();
        hideShow(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = null;
        TextView type, questionNumberText;
        ImageView typeImage;
        String qType = qm.getQuestionType();
        switch (qType) {
            case "short_answer":
                view = inflater.inflate(R.layout.q_short_long_answer, a, false);
                questionText = view.findViewById(R.id.question_field);
                questionNumberText = view.findViewById(R.id.question_number);
                ImageView attachBtn = view.findViewById(R.id.add_image);
                questionNumberText.setText(qm.getQuestionNumber());
                questionText.setText(qm.getQuestion());
                answerText = view.findViewById(R.id.answer_field);
                answerText.setText(qm.getAnswer());
                type = view.findViewById(R.id.question_type);
                type.setText(qm.getQuestionType());
                typeImage = view.findViewById(R.id.desc_logo);
                typeImage.setImageResource(R.drawable.ic_short_text);
                questionText.requestFocus();
                pic = view.findViewById(R.id.question_pic);
                if (qm.getQuestionImageUri() != null && !qm.getQuestionImageUri().toString().isEmpty()) {
                    pic.setImageURI(qm.getQuestionImageUri());
                    pic.setVisibility(View.VISIBLE);

                } else if (qm.getQuestionImage() != null && !qm.getQuestionImage().isEmpty()) {
                    pic.setVisibility(View.VISIBLE);
                    Picasso.get().load(Login.urlBase + "/" + qm.getQuestionImage()).into(pic);

                }
                pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (qm.getQuestionImageUri() != null && !qm.getQuestionImageUri().toString().isEmpty()) {
                            ImagePreviewDialog imgDialog = new ImagePreviewDialog(activity, qm.getQuestionImageUri().toString(), "preview", "0");
                            imgDialog.show();
                            imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        } else if (qm.getQuestionImage() != null && !qm.getQuestionImage().isEmpty()) {
                            ImagePreviewDialog imgDialog = new ImagePreviewDialog(activity, qm.getQuestionImage(), "preview", "1");
                            imgDialog.show();
                            imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }

                    }
                });

                new Handler().postDelayed(() -> {
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }, 500);
                attachBtn.setOnClickListener(v -> selectImage());
                a.addView(view);
                return;
            case "long_answer":
                view = inflater.inflate(R.layout.q_short_long_answer, a, false);
                questionText = view.findViewById(R.id.question_field);
                questionText.setText(qm.getQuestion());
                answerText = view.findViewById(R.id.answer_field);
                answerText.setText(qm.getAnswer());
                pic = view.findViewById(R.id.question_pic);
                if (qm.getQuestionImageUri() != null && !qm.getQuestionImageUri().toString().isEmpty()) {
                    pic.setImageURI(qm.getQuestionImageUri());
                    pic.setVisibility(View.VISIBLE);
                } else if (qm.getQuestionImage() != null && !qm.getQuestionImage().isEmpty()) {
                    pic.setVisibility(View.VISIBLE);
                    Picasso.get().load(Login.urlBase + "/" + qm.getQuestionImage()).into(pic);
                }
                pic.setOnClickListener(v -> {
                    if (qm.getQuestionImageUri() != null && !qm.getQuestionImageUri().toString().isEmpty()) {
                        ImagePreviewDialog imgDialog = new ImagePreviewDialog(activity, qm.getQuestionImageUri().toString(), "preview", "0");
                        imgDialog.show();
                        imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    } else if (qm.getQuestionImage() != null && !qm.getQuestionImage().isEmpty()) {
                        ImagePreviewDialog imgDialog = new ImagePreviewDialog(activity, qm.getQuestionImage(), "preview", "1");
                        imgDialog.show();
                        imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }
                });
                type = view.findViewById(R.id.question_type);
                type.setText(qm.getQuestionType());
                typeImage = view.findViewById(R.id.desc_logo);
                typeImage.setImageResource(R.drawable.ic_long_ans);
                questionNumberText = view.findViewById(R.id.question_number);
                questionNumberText.setText(qm.getQuestionNumber());
                questionText.requestFocus();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }, 500);
                a.addView(view);
                ImageView attachBtn1 = view.findViewById(R.id.add_image);
                attachBtn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                    }
                });

                return;
            case "email":
                view = inflater.inflate(R.layout.q_item_layout, a, false);
                questionText = view.findViewById(R.id.question_field);
                questionText.setText(qm.getQuestion());
                type = view.findViewById(R.id.question_type);
                type.setText(qm.getQuestionType());
                typeImage = view.findViewById(R.id.desc_logo);
                typeImage.setImageResource(R.drawable.ic_mail);
                questionNumberText = view.findViewById(R.id.question_number);
                questionNumberText.setText(qm.getQuestionNumber());
                questionText.requestFocus();

                new Handler().postDelayed(() -> {
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }, 500);
                a.addView(view);
                return;
            case "number":
                view = inflater.inflate(R.layout.q_item_layout, a, false);
                questionText = view.findViewById(R.id.question_field);
                questionText.setText(qm.getQuestion());
                type = view.findViewById(R.id.question_type);
                type.setText(qm.getQuestionType());
                typeImage = view.findViewById(R.id.desc_logo);
                typeImage.setImageResource(R.drawable.ic_number);
                questionNumberText = view.findViewById(R.id.question_number);
                questionNumberText.setText(qm.getQuestionNumber());
                questionText.requestFocus();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }, 500);
                a.addView(view);
                return;
            case "multiple_choice":
                view = inflater.inflate(R.layout.question_dialog_layout, a, false);
                questionText = view.findViewById(R.id.question_field);
                questionText.setText(qm.getQuestion());
                type = view.findViewById(R.id.question_type);
                type.setText(qm.getQuestionType());
                typeImage = view.findViewById(R.id.desc_logo);
                typeImage.setImageResource(R.drawable.ic_launcher_background);
                RecyclerView optionsRecycler = view.findViewById(R.id.options_recycler);
                optionsList = qm.getOptions();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                optionsRecycler.setLayoutManager(linearLayoutManager);
                optionsRecycler.setHasFixedSize(true);
                questionText.requestFocus();

                adapter = new OptionsAdapter2(getContext(), optionsList, this);
                optionsRecycler.setAdapter(adapter);
                Button addBtn = view.findViewById(R.id.add_option);
                addBtn.setOnClickListener(v -> {
                    OptionsModel md = new OptionsModel();
                    md.setOptionText("");
                    md.setType("multichoice");
                    md.setChecked(false);
                    optionsList.add(md);
                    adapter.notifyDataSetChanged();

                });
                imageContainer = view.findViewById(R.id.image_container);
                deleteBtn = view.findViewById(R.id.delete_pic);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteImage(qm, imageContainer);
                        qNumberContainer.setVisibility(View.VISIBLE);
                        addImgContainer.setVisibility(View.VISIBLE);
                    }
                });
                ImageView attachBtn2 = view.findViewById(R.id.add_image);
                attachBtn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                    }
                });
                pic = view.findViewById(R.id.question_pic);

                if (qm.getQuestionImageUri() != null && !qm.getQuestionImageUri().toString().isEmpty()) {
                    pic.setImageURI(qm.getQuestionImageUri());
                    pic.setVisibility(View.VISIBLE);
                    imageContainer.setVisibility(View.VISIBLE);

                } else if (qm.getQuestionImage() != null && !qm.getQuestionImage().isEmpty()) {
                    pic.setVisibility(View.VISIBLE);
                    imagePath = qm.getQuestionImage();
                    //imagePath = imagePath.substring(2);
                    imageContainer.setVisibility(View.VISIBLE);
                    Picasso.get().load(Login.urlBase + "/" + imagePath).into(pic);
                }
                pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (qm.getQuestionImageUri() != null && !qm.getQuestionImageUri().toString().isEmpty()) {
                            ImagePreviewDialog imgDialog = new ImagePreviewDialog(activity, qm.getQuestionImageUri().toString(), "preview", "0");
                            imgDialog.show();
                            imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        } else if (qm.getQuestionImage() != null && !qm.getQuestionImage().isEmpty()) {
                            ImagePreviewDialog imgDialog = new ImagePreviewDialog(activity, imagePath, "preview", "1");
                            imgDialog.show();
                            imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                    }
                });
                if (show) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            show = false;
                        }
                    }, 500);
                }

                questionNumberText = view.findViewById(R.id.question_number);
                TextView questionNumber1 = view.findViewById(R.id.question_number1);
                questionNumberText.setText("Question " + qm.getQuestionNumber());
                questionNumber1.setText(qm.getQuestionNumber());
                qNumberContainer = view.findViewById(R.id.qN);
                addImgContainer = view.findViewById(R.id.aM);
                //previewQuestion(view);
                a.addView(view);
                return;
            case "check_box":
                view = inflater.inflate(R.layout.question_dialog_layout, a, false);
                questionText = view.findViewById(R.id.question_field);
                questionText.setText(qm.getQuestion());
                type = view.findViewById(R.id.question_type);
                type.setText(qm.getQuestionType());
                typeImage = view.findViewById(R.id.desc_logo);
                typeImage.setImageResource(R.drawable.ic_check_box);
                RecyclerView optionsRecycler1 = view.findViewById(R.id.options_recycler);
                optionsList1 = qm.getOptions();
                LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
                optionsRecycler1.setLayoutManager(linearLayoutManager1);
                optionsRecycler1.setHasFixedSize(true);
                questionText.requestFocus();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }, 500);


                adapter = new OptionsAdapter2(getContext(), optionsList1, this);
                optionsRecycler1.setAdapter(adapter);
                Button addBtn1 = view.findViewById(R.id.add_option);
                addBtn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OptionsModel md = new OptionsModel();
                        md.setOptionText("");
                        md.setType("checkbox");
                        md.setChecked(false);
                        optionsList1.add(md);
                        adapter.notifyDataSetChanged();
                    }
                });

                questionNumberText = view.findViewById(R.id.question_number);
                questionNumberText.setText(qm.getQuestionNumber());
                a.addView(view);
                return;
            case "drop_down":
                view = inflater.inflate(R.layout.question_dialog_layout, a, false);
                questionText = view.findViewById(R.id.question_field);
                questionText.setText(qm.getQuestion());
                type = view.findViewById(R.id.question_type);
                type.setText(qm.getQuestionType());
                typeImage = view.findViewById(R.id.desc_logo);
                typeImage.setImageResource(R.drawable.ic_arrow_drop1);
                RecyclerView optionsRecycler2 = view.findViewById(R.id.options_recycler);
                List<OptionsModel> optionsList2 = qm.getOptions();
                LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
                optionsRecycler2.setLayoutManager(linearLayoutManager2);
                optionsRecycler2.setHasFixedSize(true);
                apply1();
                questionText.requestFocus();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }, 500);

                adapter = new OptionsAdapter2(getContext(), optionsList2, this);
                optionsRecycler2.setAdapter(adapter);
                Button addBtn2 = view.findViewById(R.id.add_option);
                addBtn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OptionsModel md = new OptionsModel();
                        md.setOptionText("");
                        md.setType("dropdown");
                        md.setChecked(false);
                        optionsList2.add(md);
                        adapter.notifyDataSetChanged();
                    }
                });
                questionNumberText = view.findViewById(R.id.question_number);
                questionNumberText.setText(qm.getQuestionNumber());
                a.addView(view);
                return;
            case "date":
                view = inflater.inflate(R.layout.q_item_layout, a, false);
                questionText = view.findViewById(R.id.question_field);
                questionText.setText(qm.getQuestion());
                type = view.findViewById(R.id.question_type);
                type.setText(qm.getQuestionType());
                typeImage = view.findViewById(R.id.desc_logo);
                typeImage.setImageResource(R.drawable.ic_date);
                questionNumberText = view.findViewById(R.id.question_number);
                questionNumberText.setText(qm.getQuestionNumber());
                questionText.requestFocus();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager =
                                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }, 500);
                a.addView(view);
                return;
            case "time":
                view = inflater.inflate(R.layout.q_item_layout, a, false);
                questionText = view.findViewById(R.id.question_field);
                questionText.setText(qm.getQuestion());
                type = view.findViewById(R.id.question_type);
                type.setText(qm.getQuestionType());
                typeImage = view.findViewById(R.id.desc_logo);
                typeImage.setImageResource(R.drawable.ic_timer);
                questionNumberText = view.findViewById(R.id.question_number);
                questionNumberText.setText(qm.getQuestionNumber());
                questionText.requestFocus();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }, 500);
                a.addView(view);
                return;
            case "section":
                view = inflater.inflate(R.layout.section_dialog_item, a, false);
                sectionTitle = view.findViewById(R.id.section_title);
                sectionTitle.setText(qm.getSectionTitle());
                sectionTitle.requestFocus();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }, 500);
                ImageView attachBtn3 = view.findViewById(R.id.add_image);
                attachBtn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                    }
                });
                pic = view.findViewById(R.id.question_pic);
                if (qm.getQuestionImageUri() != null && !qm.getQuestionImageUri().toString().isEmpty()) {
                    pic.setImageURI(qm.getQuestionImageUri());
                    pic.setVisibility(View.VISIBLE);

                } else if (qm.getQuestionImage() != null && !qm.getQuestionImage().isEmpty()) {
                    pic.setVisibility(View.VISIBLE);
                    Picasso.get().load(getString(R.string.base_url) + "/" + qm.getQuestionImage()).into(pic);
                }
                pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (qm.getQuestionImageUri() != null && !qm.getQuestionImageUri().toString().isEmpty()) {
                            ImagePreviewDialog imgDialog = new ImagePreviewDialog(activity, qm.getQuestionImageUri().toString(), "preview", "0");
                            imgDialog.show();
                            imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        } else if (qm.getQuestionImage() != null && !qm.getQuestionImage().isEmpty()) {
                            ImagePreviewDialog imgDialog = new ImagePreviewDialog(activity, qm.getQuestionImage(), "preview", "1");
                            imgDialog.show();
                            imgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                    }
                });
                a.addView(view);
                return;
            case "flash_card":
                view = inflater.inflate(R.layout.flashcard_dialog, a, false);
                questionText = view.findViewById(R.id.flash_term);
                answerText = view.findViewById(R.id.flash_definition);
                questionText.setText(qm.getQuestion());
                answerText.setText(qm.getAnswer());
                questionText.requestFocus();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }, 500);
                a.addView(view);

                break;
        }


    }

    @Override
    public void dismiss() {
        if (validateDialog()) {
            super.dismiss();
            String question = "";
            if (qm.getQuestionType().equals("section")) {
                qm.setSectionTitle(sectionTitle.getText().toString());
                TestUpload.questionAdapter2.updateSection(position, qm);
            } else {
                question = questionText.getText().toString().trim();
                if (qm.getQuestionType().equals("short_answer") || qm.getQuestionType().equals("long_answer") || qm.getQuestionType().equals("flash_card")) {
                    String answer = answerText.getText().toString();
                    qm.setAnswer(answer);
                    qm.setQuestionImageUri(TestUpload.selecetedUri);
                }
            }
            TestUpload.questionAdapter2.updateQuestion(position, question, qm);
        }


    }


    private boolean validateDialog() {
        boolean validate = true;
        int count = 0;
        if (qm.getQuestionType().equals("check_box") || qm.getQuestionType().equals("multiple_choice")) {
            for (int a = 0; a < qm.getOptions().size(); a++) {
                if (qm.getOptions().get(a).isChecked()) {
                    count++;
                }
            }
        }
        if (questionText != null && questionText.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Question is empty", Toast.LENGTH_SHORT).show();
            validate = false;

        } else if (qm.getQuestionType().equals("multiple_choice") && qm.getOptions().size() < 2 || qm.getQuestionType().equals("check_box") && qm.getOptions().size() < 2) {
            Toast.makeText(getContext(), "Options must be 2 or more", Toast.LENGTH_SHORT).show();
            validate = false;
        } else if (qm.getQuestionType().equals("multiple_choice") && count < 1) {
            Toast.makeText(getContext(), "Select a correct answer", Toast.LENGTH_SHORT).show();
            validate = false;

        } else if (qm.getQuestionType().equals("short_answer") && answerText.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Answer is empty", Toast.LENGTH_SHORT).show();
            validate = false;
        } else if (qm.getQuestionType().equals("check_box") && count < 2) {
            Toast.makeText(getContext(), "Select at least two correct answers" +
                    " " + count, Toast.LENGTH_SHORT).show();
            validate = false;

        } else if (qm.getQuestionType().equals("section")) {
            if (sectionTitle.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Section title is empty", Toast.LENGTH_SHORT).show();
                validate = false;
            }
        }

        return validate;

    }

    private void apply1() {

        boolean a = validateDialog();
        if (a) {
            String question = "";
            if (qm.getQuestionType().equals("section")) {
                qm.setSectionTitle(sectionTitle.getText().toString());
                //qm.setSectionDescription(sectionDesc.getText().toString());
                TestUpload.questionAdapter2.updateSection(position, qm);
            } else {
                question = questionText.getText().toString();
                if (qm.getQuestionType().equals("short_answer") || qm.getQuestionType().equals("long_answer") || qm.getQuestionType().equals("flash_card")) {
                    String answer = answerText.getText().toString();
                    qm.setAnswer(answer);
                }
                TestUpload.questionAdapter2.updateQuestion(position, question, qm);

            }
            dismiss();
        }

    }

    private void apply2() {

        boolean a = validateDialog();
        if (a) {
            String question = "";
            if (qm.getQuestionType().equals("section")) {
                qm.setSectionTitle(sectionTitle.getText().toString());
                TestUpload.questionAdapter2.updateSection(position, qm);
            } else {
                question = questionText.getText().toString();
                if (qm.getQuestionType().equals("short_answer") || qm.getQuestionType().equals("long_answer") || qm.getQuestionType().equals("flash_card")) {
                    String answer = answerText.getText().toString();
                    qm.setAnswer(answer);
                    //qm.setQuestionImageUri(TestUpload.selecetedUri);
                }
            }
            TestUpload.questionAdapter2.updateQuestion(position, question, qm);
            dismiss();
        }

    }

    private void selectImage() {
        if ((ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activity.startActivityForResult(galleryIntent, SELECT_IMAGE);
            //activity.startActivityForResult(Intent.createChooser(intent,"Complete action using"), SELECT_IMAGE);
        }
    }

    private void hideShow(int position) {
        if (position == 0) {
            backBtn.setEnabled(false);
        } else {
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setEnabled(true);
        }

        if (QuestionAdapter2.list.size() == position + 1) {
            forwardBtn.setEnabled(false);
        } else {
            forwardBtn.setVisibility(View.VISIBLE);
            forwardBtn.setEnabled(true);
        }
    }

    private void deleteImage(QuestionsModel qm, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Delete ?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                qm.setQuestionImage("");
                qm.setQuestionImageUri(null);
                v.setVisibility(View.GONE);
            }
        });
        builder.show();

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onAddImageClick(int position) {
        optionPosition = position;
        selectOptionImage();
    }

    @Override
    public void onImagePreviewClick(int position) {

    }


    private void selectOptionImage() {
        if ((ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activity.startActivityForResult(galleryIntent, SELECT_OPTION_IMAGE);
            //activity.startActivityForResult(Intent.createChooser(intent,"Complete action using"), SELECT_IMAGE);
        }
    }
}
