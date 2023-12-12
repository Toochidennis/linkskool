package com.digitaldream.winskool.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.digitaldream.winskool.R;
import com.digitaldream.winskool.models.ViewPagerModel;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private List<ViewPagerModel> viewPagerModelList;
    private Context context;
    LayoutInflater layoutInflater;

    public ViewPagerAdapter(List<ViewPagerModel> viewPagerModelList, Context context) {
        this.viewPagerModelList = viewPagerModelList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return viewPagerModelList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.viewpager_item,container,false);
        TextView schoolname,title,number;
        Button btn;
        schoolname = view.findViewById(R.id.school_name);
        title = view.findViewById(R.id.no_of_entity);
        number = view.findViewById(R.id.count);
        btn = view.findViewById(R.id.btn_viewpager);

        schoolname.setText(viewPagerModelList.get(position).getSchoolName());
        title.setText(viewPagerModelList.get(position).getTitle());
        number.setText(viewPagerModelList.get(position).getNumber());
        btn.setText(viewPagerModelList.get(position).getBtn_text());

        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
