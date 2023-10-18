package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitaldream.linkskool.models.CourseTable;
import com.digitaldream.linkskool.R;

import java.util.ArrayList;
import java.util.List;

public class CoursesSettingsAdapter extends RecyclerView.Adapter<CoursesSettingsAdapter.CoursesSettingViewHolder> {
    private Context context;
    private List<CourseTable> courseList;
    OnEditBtnClickListener onEditBtnClickListener;
    public static List<String> selectedIds=new ArrayList<>();

    public CoursesSettingsAdapter(Context context, List<CourseTable> courseList,OnEditBtnClickListener onEditBtnClickListener) {
        this.context = context;
        this.courseList = courseList;
        this.onEditBtnClickListener = onEditBtnClickListener;
    }

    @NonNull
    @Override
    public CoursesSettingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.course_settings_item,viewGroup,false);
        return new CoursesSettingViewHolder(view,onEditBtnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final CoursesSettingViewHolder coursesSettingViewHolder, int i) {
        final CourseTable ct = courseList.get(i);
        coursesSettingViewHolder.courseName.setText(ct.getCourseName().toUpperCase());
        coursesSettingViewHolder.courseCode.setText(ct.getCourseCode().toUpperCase());
        coursesSettingViewHolder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ct.setSelected(!ct.isSelected());
                coursesSettingViewHolder.rootView.setBackgroundColor(ct.isSelected()? Color.parseColor("#ECECEC") : Color.WHITE);
                if(ct.isSelected()) {
                    String id = ct.getCourseId();
                    selectedIds.add(id);
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportActionBar().setTitle("");
                    activity.invalidateOptionsMenu();
                }else{
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.invalidateOptionsMenu();
                    String id = ct.getCourseId();
                    // insert code here
                    int index = -1;
                    for (int i=0;i<selectedIds.size();i++) {
                        if (selectedIds.get(i).equals(id)) {
                            index = i;
                            break;
                        }
                    }
                    selectedIds.remove(index);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class CoursesSettingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView courseName,courseCode;
        ImageView editBtn;
        OnEditBtnClickListener onEditBtnClickListener;
        View rootView;

        public CoursesSettingViewHolder(@NonNull View itemView,OnEditBtnClickListener onEditBtnClickListener) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name_setting);
            courseCode = itemView.findViewById(R.id.course_code_setting);
            editBtn = itemView.findViewById(R.id.edit_course_setting);
            this.onEditBtnClickListener = onEditBtnClickListener;
            editBtn.setOnClickListener(this);
            rootView = itemView;
        }

        @Override
        public void onClick(View v) {
            onEditBtnClickListener.onEditClick(getAdapterPosition());
        }
    }

    public interface OnEditBtnClickListener{
        void onEditClick(int position);
    }
}
