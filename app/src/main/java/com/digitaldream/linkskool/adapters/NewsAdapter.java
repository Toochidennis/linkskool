package com.digitaldream.linkskool.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.models.NewsTable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private Context context;
    private List<NewsTable> newsTitleList;
    OnNewsClickListener onNewsClickListener;

    public NewsAdapter(Context context, List<NewsTable> newsTitleList,OnNewsClickListener onNewsClickListener) {
        this.context = context;
        this.newsTitleList = newsTitleList;
        this.onNewsClickListener = onNewsClickListener;
    }



    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.news_item,viewGroup,false);
        return new NewsViewHolder(view,onNewsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder newsViewHolder, int i) {
        String newsTitle = newsTitleList.get(i).getNewsSubject().toUpperCase();
        newsViewHolder.newsTitle.setText(stripHtml(newsTitle));
        String content = newsTitleList.get(i).getNewsContent().toLowerCase();
        StringBuilder stringBuilder = new StringBuilder(content.length());
        boolean capitalize = true;
        for (int a=0;a<content.length();a++){
            Character c = content.charAt(a);
            if(c=='.'){
                capitalize=true;
            }else if(capitalize&& Character.isAlphabetic(c)){
                c = Character.toUpperCase(c);
                capitalize = false;
            }
            stringBuilder.append(c);
        }
        //newsViewHolder.newsContent.setText(stringBuilder.toString());
        String date = newsTitleList.get(i).getDatePosted().toUpperCase();
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date newDate = sp.parse(date);

            //sp = new SimpleDateFormat("dd MM yyyy");
            date = sp.format(newDate);
            newsViewHolder.postedDate.setText(DateFormat.getDateInstance().format(newDate).toUpperCase());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        int colorArr[] = {-1036950,-4685395,-15538830,-16544930,-11277987,-10453155,-16740624,-9050143,-9387925,-12764943};
        Random rnd = new Random();
        int currentColor = colorArr[rnd.nextInt(colorArr.length)];
        newsViewHolder.dateContainer.setBackgroundColor(currentColor);
    }

    @Override
    public int getItemCount() {
        if(newsTitleList.isEmpty()){
            return 0;
        }else {
            return newsTitleList.size();
        }
    }

    class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView newsTitle,postedDate,newsContent;
        OnNewsClickListener onNewsClickListener;
        LinearLayout dateContainer;

        public NewsViewHolder(@NonNull View itemView,OnNewsClickListener onNewsClickListener) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.news_title);
            postedDate = itemView.findViewById(R.id.date_posted);
            //newsContent = itemView.findViewById(R.id.news_content);
            dateContainer = itemView.findViewById(R.id.student_count_container);
            this.onNewsClickListener = onNewsClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNewsClickListener.onNewsClick(getAdapterPosition());
        }
    }

    public interface OnNewsClickListener{
        void onNewsClick(int position);
    }

    private String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }
}
