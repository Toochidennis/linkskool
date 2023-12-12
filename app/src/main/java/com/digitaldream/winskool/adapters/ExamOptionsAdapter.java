package com.digitaldream.winskool.adapters;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.winskool.models.ExamType;
import com.digitaldream.winskool.R;

import java.util.List;

public class ExamOptionsAdapter extends BaseAdapter {
    public List<ExamType> info;
    public Context mContext;
    private LayoutInflater mInflater;
    //public int[] colors = {R.color.amberG,R.color.colorPrimaryDark,R.color.redJ,R.color.greenA};
    public int[] colors = {R.color.color4, R.color.color3,R.color.color1,R.color.color2,R.color.color5,R.color.color4, R.color.color3};

    public ExamOptionsAdapter(Context c, List<ExamType> examType)
    {
        mContext = c;
        info=examType;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return info.size();
    }

    @Override
    public Object getItem(int i) {
        return info.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String title  = info.get(i).getExamName();
        String link  = info.get(i).getExamLogo();
        view = mInflater.inflate(R.layout.exam, null);
        TextView f=view.findViewById(R.id.examT);
        f.setText(title);
        f.setId(info.get(i).getExamTypeId());
        LinearLayout layout = view.findViewById(R.id.grid_layout);
        layout.setBackgroundColor(ContextCompat.getColor(mContext, colors[i%6]));

        //ImageView vi= view.findViewById(R.id.button_x);
        int Measuredwidth;
        Point size = new Point();
        WindowManager w = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
            w.getDefaultDisplay().getSize(size);
            Measuredwidth = size.x;
        }else{
            Display d = w.getDefaultDisplay();
            Measuredwidth = d.getWidth();
        }
        //Picasso.with(mContext).load(link).resize(Measuredwidth,0).into(vi);
        return view;
    }
}
