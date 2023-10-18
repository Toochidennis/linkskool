package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.digitaldream.linkskool.R;

import java.util.List;

public class StaffCourseListAdapter extends ArrayAdapter<String> {
    private List<String> courseTitle;
    private List<String> classList;
    private Context context;

    public StaffCourseListAdapter( Context context, List<String> courseTitle, List<String> classList) {
        super(context, R.layout.contact_item,courseTitle);
        this.courseTitle = courseTitle;
        this.classList = classList;
        this.context = context;
    }

    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View rowView=inflater.inflate(R.layout.course_item, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.course_title);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.class_name);

        titleText.setText(courseTitle.get(position).toUpperCase());
        subtitleText.setText(classList.get(position).toUpperCase());

        return rowView;
    }
}
