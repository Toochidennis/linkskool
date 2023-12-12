package com.digitaldream.winskool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.winskool.activities.Login;
import com.digitaldream.winskool.models.AnswerModel;
import com.digitaldream.winskool.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>{
    private Context context;
    private List<AnswerModel> list;
    LayoutInflater inflater ;
    OnAnswerClickListener onAnswerClickListener;

    public AnswerAdapter(Context context, List<AnswerModel> list, OnAnswerClickListener onAnswerClickListener) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        this.onAnswerClickListener = onAnswerClickListener;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.answer_layout_item,parent,false);
        return new AnswerViewHolder(view,onAnswerClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        AnswerModel answer = list.get(position);
        String author = answer.getUser();
        try {
            String initials = author.substring(0, 1).toUpperCase();
            holder.initial.setText(initials);
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        author = author.substring(0,1).toUpperCase()+""+author.substring(1);
        holder.user.setText(author);
        holder.date.setText(answer.getDate());
        holder.likeNo.setText(answer.getLikeNo());
        try {
            JSONArray array = new JSONArray(answer.getAnswer());
            View view = null;
            for (int b=0;b<array.length();b++){
                JSONObject jsonObject = array.getJSONObject(b);
                String type = jsonObject.getString("type");
                if(type.equals("text")){
                    String content = jsonObject.getString("content");
                    view = inflater.inflate(R.layout.text_layout,holder.linearLayout,false);
                    TextView t = view.findViewById(R.id.text);
                    t.setText(content.substring(0,1).toUpperCase()+""+content.substring(1));
                }else if(type.equals("image")){
                    String src = jsonObject.getString("src");
                    src = src.substring(2);
                    view = inflater.inflate(R.layout.image_layout,holder.linearLayout,false);
                    ImageView img = view.findViewById(R.id.image);
                    //img.setImageResource(R.drawable.splashscreen);
                    Picasso.get().load(Login.fileBaseUrl+""+src).into(img);
                }
                holder.linearLayout.removeAllViews();

                holder.linearLayout.addView(view);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        String date = answer.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        try {
            Date d = simpleDateFormat.parse(date);
            String niceDateStr = String.valueOf(DateUtils.getRelativeTimeSpanString(d.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));
            holder.date.setText(niceDateStr);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        GradientDrawable gd = (GradientDrawable) holder.initialBg.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        gd.setColor(currentColor);
        holder.initialBg.setBackground(gd);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class AnswerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView user,date,initial,answer,likeNo;
        LinearLayout initialBg;
        LinearLayout linearLayout;
        OnAnswerClickListener onAnswerClickListener;

        public AnswerViewHolder(@NonNull View itemView,OnAnswerClickListener onAnswerClickListener) {
            super(itemView);
            user = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date_layout);
            initial = itemView.findViewById(R.id.initials);
            answer = itemView.findViewById(R.id.answer);
            likeNo = itemView.findViewById(R.id.like);
            initialBg = itemView.findViewById(R.id.initial_bg);
            linearLayout = itemView.findViewById(R.id.answer_display);
            this.onAnswerClickListener = onAnswerClickListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onAnswerClickListener.onAnswerClick(getAdapterPosition());
        }
    }


    public interface OnAnswerClickListener{
        void onAnswerClick(int position);
    }
}
