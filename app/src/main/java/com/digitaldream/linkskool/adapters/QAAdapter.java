package com.digitaldream.linkskool.adapters;

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

import com.digitaldream.linkskool.R;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class QAAdapter extends RecyclerView.Adapter<QAAdapter.QAVH> {
    private Context context;
    private List<QAObject> list;
    OnQuestionClickListener onQuestionClickListener;

    public QAAdapter(Context context, List<QAObject> list,OnQuestionClickListener onQuestionClickListener) {
        this.context = context;
        this.list = list;
        this.onQuestionClickListener = onQuestionClickListener;
    }

    @NonNull
    @Override
    public QAVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.q_a_item,parent,false);
        return new QAVH(view,onQuestionClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull QAVH holder, int position) {
        QAObject object = list.get(position);
        String name = object.getUser();
        String initial;
        if(name.isEmpty()){
            initial = "U";
        }else{
           initial = name.substring(0,1).toUpperCase();
        }

        holder.name.setText(name);
        if(object.getFeedType().equals("20")){
            holder.feedType.setText("Question");
        }else if(object.getFeedType().equals("21")){
            holder.feedType.setText("Answer");
        }else{
            holder.feedType.setText("News");
        }
        holder.initial.setText(initial);
        String question = object.getQuestion().trim();
        try {
            String q = question.substring(question.length() - 1);

            question = question.substring(0,1).toUpperCase()+""+question.substring(1).toLowerCase();
            if(q.equals("?")){
                holder.question.setText(question);

            }else {
                holder.question.setText(question+"?");
            }
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        try {

        String answer = object.getPreText();
        answer = answer.substring(0,1).toUpperCase()+""+answer.substring(1).toLowerCase();
        holder.answer.setText(answer);
        }catch (Exception e){
            e.printStackTrace();
        }
        String date = object.getDate();
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
         final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");

        try {
            Date d = simpleDateFormat.parse(date);
            String niceDateStr = String.valueOf(DateUtils.getRelativeTimeSpanString(d.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));
            holder.date.setText(niceDateStr);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.sharesCount.setText(object.getShareNo());
        holder.upvotes.setText(object.getLikesNo());
        holder.commentNo.setText(object.commentNo);
            holder.img.setVisibility(View.GONE);


        /*try {
            String imgUrl = object.getPicUrl();
            imgUrl = Login.fileBaseUrl + "" + imgUrl.substring(2);
            if (imgUrl.isEmpty()) {
                holder.img.setVisibility(View.GONE);
            } else {
                holder.img.setVisibility(View.VISIBLE);
                Picasso.with(context).load(imgUrl).into(holder.img);
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/

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

    class QAVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name,date,initial,question,answer,commentNo,upvotes,sharesCount,feedType;
        ImageView img;
        LinearLayout initialBg;
        OnQuestionClickListener onQuestionClickListener;
        public QAVH(@NonNull View itemView,OnQuestionClickListener onQuestionClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date_layout);
            initial = itemView.findViewById(R.id.initials);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            img =itemView.findViewById(R.id.pics);
            commentNo = itemView.findViewById(R.id.comments);
            upvotes = itemView.findViewById(R.id.upvotes);
            sharesCount = itemView.findViewById(R.id.share);
            initialBg = itemView.findViewById(R.id.initial_bg);
            feedType = itemView.findViewById(R.id.feed_type);
            this.onQuestionClickListener = onQuestionClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onQuestionClickListener.onQuestionClick(getAdapterPosition());
        }
    }

    public interface OnQuestionClickListener{
        void onQuestionClick(int position);
    }


    public static class QAObject implements Serializable {
        private String question;
        private String answer;
        private boolean isQuestionOwner=false;
        private String user;
        private String date;
        private String picUrl;
        private String questionId;
        private String commentNo;
        private String likesNo;
        private String shareNo;
        private String feedType;
        private String preText;
        private String preImage;
        private String answerId;
        private String id;


        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public boolean isQuestionOwner() {
            return isQuestionOwner;
        }

        public void setQuestionOwner(boolean questionOwner) {
            isQuestionOwner = questionOwner;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public String getCommentNo() {
            return commentNo;
        }

        public void setCommentNo(String commentNo) {
            this.commentNo = commentNo;
        }

        public String getLikesNo() {
            return likesNo;
        }

        public void setLikesNo(String likesNo) {
            this.likesNo = likesNo;
        }

        public String getShareNo() {
            return shareNo;
        }

        public void setShareNo(String shareNo) {
            this.shareNo = shareNo;
        }

        public String getFeedType() {
            return feedType;
        }

        public void setFeedType(String feedType) {
            this.feedType = feedType;
        }

        public String getPreText() {
            return preText;
        }

        public void setPreText(String preText) {
            this.preText = preText;
        }

        public String getPreImage() {
            return preImage;
        }

        public void setPreImage(String preImage) {
            this.preImage = preImage;
        }


        public String getAnswerId() {
            return answerId;
        }

        public void setAnswerId(String answerId) {
            this.answerId = answerId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
