package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.digitaldream.linkskool.R;

public class SettingListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] settingTitle;

    public SettingListAdapter(Context context, String[] settingTitle) {
        super(context, R.layout.settings_list_item, settingTitle);
        this.context = context;
        this.settingTitle = settingTitle;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.settings_list_item, parent, false);
        TextView Title = rowView.findViewById(R.id.setting_title);
        Title.setText(settingTitle[position]);
        return rowView;
    }
}
