package com.digitaldream.winskool.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.digitaldream.winskool.activities.ContentUpload;
import com.digitaldream.winskool.activities.CourseOutlines;
import com.digitaldream.winskool.activities.CreateVideoLink;
import com.digitaldream.winskool.activities.TestUpload;
import com.digitaldream.winskool.R;

public class ChooseActionDialog extends Dialog implements View.OnClickListener {

    public Context activity;
    public LinearLayout contentUpload,testUpload,createVideo,creteFlashCard;
    public ChooseActionDialog( Context context) {
        super(context);
        this.activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_action_dialog);
        contentUpload = findViewById(R.id.choose_content_upload);
        testUpload = findViewById(R.id.test);
        createVideo = findViewById(R.id.create_video);
        creteFlashCard = findViewById(R.id.create_flashcard);
        contentUpload.setOnClickListener(this);
        testUpload.setOnClickListener(this);
        createVideo.setOnClickListener(this);
        creteFlashCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_content_upload:
                Intent intent = new Intent(getContext(), ContentUpload.class);
                intent.putExtra("levelId", CourseOutlines.levelId);
                intent.putExtra("courseId",CourseOutlines.courseId);
                intent.putExtra("from","upload");
                getContext().startActivity(intent);
                //((Activity)activity).finish();

                break;
            case R.id.test:
                Intent intent1 = new Intent(getContext(), TestUpload.class);
                intent1.putExtra("levelId", CourseOutlines.levelId);
                intent1.putExtra("courseId",CourseOutlines.courseId);
                intent1.putExtra("courseName",CourseOutlines.courseName);
                intent1.putExtra("from","upload");
                intent1.putExtra("id","");
                getContext().startActivity(intent1);
                //((Activity)activity).finish();
                break;
            case R.id.create_video:
                Intent intent2 = new Intent(getContext(), CreateVideoLink.class);
                intent2.putExtra("levelId",CourseOutlines.levelId);
                intent2.putExtra("courseId",CourseOutlines.courseId);
                intent2.putExtra("from","upload");
                getContext().startActivity(intent2);
                //((Activity)activity).finish();
                break;
            case R.id.create_flashcard:
                Intent intent3 = new Intent(getContext(), TestUpload.class);
                intent3.putExtra("levelId", CourseOutlines.levelId);
                intent3.putExtra("courseId",CourseOutlines.courseId);
                intent3.putExtra("from","flashcard");
                intent3.putExtra("id","");
                getContext().startActivity(intent3);
                break;

            default:
                break;
        }
        dismiss();
    }
}
