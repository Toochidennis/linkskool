package com.digitaldream.linkskool.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.linkskool.activities.Login;
import com.digitaldream.linkskool.models.CommentTable;
import com.digitaldream.linkskool.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Comment {
    private Activity context;
    private RelativeLayout replyToContainer;
    private ProgressBar progressBar1;
    private static String commentId;
    private  static int commentPosition,replyPosition;
    private List<CommentTable> commentModelList = new ArrayList<>();
    private static boolean isReply = false;
    private LinearLayout commentsLayout;
    private EditText commentEdittext;
    private ScrollView scrollView;
    private LinearLayout q;
    private static String id;




    public Comment(Activity context) {
        this.context = context;
        commentEdittext = context.findViewById(R.id.comment_text1);
        scrollView = context.findViewById(R.id.scroll);
        replyToContainer = context.findViewById(R.id.replying_to_cont);
        q = context.findViewById(R.id.comments);

    }

    private void createParent(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("0");
            LayoutInflater layoutInflater = LayoutInflater.from(context);


            for (int a = 0; a < jsonArray.length(); a++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(a);
                String id = jsonObject1.getString("id");
                String comment = jsonObject1.getString("body");
                String user = jsonObject1.getString("author_name");
                String dateText = jsonObject1.getString("upload_date");

                CommentTable commentObject = new CommentTable();
                commentObject.setComment_id(id);
                if (user.equalsIgnoreCase("null")) {
                    commentObject.setUsername("Admin");

                } else {
                    commentObject.setUsername(user);
                }
                commentModelList.add(commentObject);
                Log.i("sixe", commentObject.getComment_id());

                View view = layoutInflater.inflate(R.layout.comment_layout, q, false);
                TextView commentText = view.findViewById(R.id.comment_text);
                TextView user_name = view.findViewById(R.id.user);
                TextView reply = view.findViewById(R.id.reply);
                TextView initial = view.findViewById(R.id.initials);
                TextView date = view.findViewById(R.id.date_layout);
                LinearLayout initialBg = view.findViewById(R.id.initials_cont);
                TextView commentId1 = view.findViewById(R.id.comment_id);
                LinearLayout reply_layout = view.findViewById(R.id.replies);
                commentId1.setText(id);
                reply.setText("Reply");
                date.setText(dateText);

                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //InputMethodManager inputMethodManager = (InputMethodManager)getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        //inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        isReply = true;
                        String f="";
                        if(isReply){
                            f="true";
                        }else{
                            f="false";
                        }

                        replyToContainer.setVisibility(View.VISIBLE);
                        TextView replyTo = context.findViewById(R.id.replying_to);
                        TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.user);

                        ImageView clear = context.findViewById(R.id.clear);
                        clear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                replyToContainer.setVisibility(View.GONE);
                            }
                        });

                        commentEdittext.setText("");
                        CommentTable ctm = new CommentTable();
                        //commentPosition = ctm.getPosition();
                        commentPosition = q.indexOfChild(view);
                        commentId = commentModelList.get(commentPosition).getComment_id();
                        Log.i("res", String.valueOf(commentPosition));

                        Toast.makeText(context,commentId,Toast.LENGTH_SHORT).show();
                        //Toast.makeText(context,String.valueOf(commentPosition),Toast.LENGTH_SHORT).show();
                        replyTo.setText(commentModelList.get(commentPosition).getUsername());


                    }
                });
                commentText.setText(comment);
                if (user.equalsIgnoreCase("null")) {
                    user_name.setText("Admin");
                    initial.setText("A");

                } else {
                    user_name.setText(user);
                    String i = user.substring(0, 1).toUpperCase();
                    initial.setText(i);

                }
                GradientDrawable gd = (GradientDrawable) initialBg.getBackground().mutate();
                Random rnd = new Random();
                int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                gd.setColor(currentColor);
                //gd.invalidateSelf();
                initialBg.setBackground(gd);
                q.addView(view);
                commentPosition = q.indexOfChild(view);

                createReply(id, jsonObject, reply_layout,commentPosition);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void createReply(String commentId, JSONObject jsonObject, LinearLayout l,int position) {

        if (jsonObject.has(commentId)) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray(commentId);
                for (int b = 0; b < jsonArray.length(); b++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(b);
                    String id = jsonObject1.getString("id");
                    String comment = jsonObject1.getString("body");
                    String user = jsonObject1.getString("author_name");
                    String dateText = jsonObject1.getString("upload_date");
                    String parent = jsonObject1.getString("parent");
                    if (!commentId.equalsIgnoreCase(parent)) {
                        return;
                    }

                    LinearLayout q = context.findViewById(R.id.comments);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    //LinearLayout l = q.getChildAt(commentPosition).findViewById(R.id.replies);
                    View vi = inflater.inflate(R.layout.reply_layout, l, false);
                    TextView commentText = vi.findViewById(R.id.reply_text);
                    TextView user_name = vi.findViewById(R.id.user);
                    TextView initial = vi.findViewById(R.id.initials);
                    TextView reply = vi.findViewById(R.id.reply);
                    reply.setText("Reply");
                    TextView date = vi.findViewById(R.id.date_layout);

                    date.setText(dateText);
                    reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isReply = true;
                            replyToContainer.setVisibility(View.VISIBLE);
                            TextView replyTo = context.findViewById(R.id.replying_to);
                            TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.user);
                            //replyPosition = l.indexOfChild(vi);
                            //Log.i("replyPosition", String.valueOf(replyPosition));
                            ImageView clear = context.findViewById(R.id.clear);
                            clear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    replyToContainer.setVisibility(View.GONE);
                                }
                            });
                            //commentPosition = q.indexOfChild(vi);
                            commentPosition = position;

                            //commentId = commentModelList.get(commentPosition).getComment_id();
                            commentEdittext.setText("reply");
                            CommentTable ctm = new CommentTable();
                            //commentPosition = ctm.getPosition();
                            //commentPosition = q.indexOfChild(view);
                            replyTo.setText(commentModelList.get(commentPosition).getUsername());
                            commentEdittext.setText("");
                            //Toast.makeText(ContentDownload.this, comment_id, Toast.LENGTH_SHORT).show();

                        }
                    });
                    commentText.setText(comment);
                    if (user.equalsIgnoreCase("null")) {
                        user_name.setText("Admin");
                        initial.setText("A");

                    } else {
                        user_name.setText(user);
                        String i = user.substring(0, 1).toUpperCase();
                        initial.setText(i);
                    }
                    l.addView(vi);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void fetchComments(String db,String contentId){
         ProgressBar progressBar1 = context.findViewById(R.id.progress_bar);

        String url = Login.urlBase+"/getComment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar1.setVisibility(View.GONE);
                Log.i("json","response"+response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    createParent(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("json","error"+error.getMessage());
                progressBar1.setVisibility(View.GONE);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("_db",db);
                params.put("id",contentId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void sendCommentToServer(String comment,String parent,String levelId,String courseId,String user, String topicTitle,String contentId,String userId,String db){
        String  currentDateTime = DateFormat.getDateTimeInstance()
                .format(new Date());

        String url = Login.urlBase+"/addComment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equalsIgnoreCase("success")){


                        JSONObject detailObj = jsonObject.getJSONObject("details");
                        String commentId = detailObj.getString("id");
                        String parent = detailObj.getString("parent");
                        String author = detailObj.getString("author_name");
                        CommentTable comment = new CommentTable();
                        comment.setComment_id(commentId);


                        if(author.equalsIgnoreCase("null")){
                            comment.setUsername("Admin");

                        }else {
                            comment.setUsername(author);
                        }
                        if(parent.equalsIgnoreCase("0")) {
                            commentModelList.add(comment);
                            LinearLayout q = context.findViewById(R.id.comments);
                            TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.reply);
                            TextView commentId1 = q.getChildAt(commentPosition).findViewById(R.id.comment_id);
                            commentId1.setText(commentId);
                            l1.setText("Reply");

                        }else{
                            LinearLayout q = context.findViewById(R.id.comments);
                            LinearLayout l = q.getChildAt(commentPosition).findViewById(R.id.replies);
                            TextView reply = l.getChildAt(replyPosition).findViewById(R.id.reply);
                            reply.setText("Reply");
                        }


                    }else{
                        LinearLayout q = context.findViewById(R.id.comments);
                        TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.reply);
                        l1.setText("Fail");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Check your connection and try again",Toast.LENGTH_SHORT).show();
                /*LinearLayout q = findViewById(R.id.comments);
                TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.reply);
                l1.setText("Fail");*/
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("level",levelId);
                params.put("course",courseId);
                params.put("comment",comment);
                params.put("parent",parent);
                params.put("author_name",user);
                params.put("content_title",topicTitle);
                params.put("content_id",contentId);
                params.put("date",currentDateTime);
                params.put("author_id",userId);
                params.put("_db",db);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    public void sendComment(String comment,String levelId,String courseId,String user, String topicTitle,String contentId,String userId,String db){
        LinearLayout q = context.findViewById(R.id.comments);
        //commentEdittext = context.findViewById(R.id.comment_text1);
        String f ="";
        if(isReply){
            f="true";
        }else{
            f="false";
        }
        Toast.makeText(context,f,Toast.LENGTH_SHORT).show();


        if (!comment.isEmpty()) {

            if (!isReply) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View view = layoutInflater.inflate(R.layout.comment_layout, commentsLayout, false);
                TextView commentText = view.findViewById(R.id.comment_text);
                TextView user_name = view.findViewById(R.id.user);
                TextView reply = view.findViewById(R.id.reply);
                TextView initial = view.findViewById(R.id.initials);
                TextView date = view.findViewById(R.id.date_layout);
                TextView commentId = view.findViewById(R.id.comment_id);
                LinearLayout initialBg = view.findViewById(R.id.initials_cont);
                String  currentTime = DateFormat.getDateTimeInstance()
                        .format(new Date());
                date.setText(currentTime);

                commentEdittext.setText("");
                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //InputMethodManager inputMethodManager = (InputMethodManager)getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        //inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        isReply = true;
                        replyToContainer.setVisibility(View.VISIBLE);
                        TextView replyTo = context.findViewById(R.id.replying_to);
                        TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.user);

                        ImageView clear = context.findViewById(R.id.clear);
                        clear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                replyToContainer.setVisibility(View.GONE);
                            }
                        });

                        commentEdittext.setText("");
                        CommentTable ctm = new CommentTable();
                        //commentPosition = ctm.getPosition();
                        commentPosition = q.indexOfChild(view);
                        Toast.makeText(context,String.valueOf(commentPosition),Toast.LENGTH_SHORT).show();
                        TextView commentId = view.findViewById(R.id.comment_id);
                        //comment_id = commentId.getText().toString();
                        //replyTo.setText(commentModelList.get(commentPosition).getUsername());


                    }
                });
                commentText.setText(comment);
                if (user.equalsIgnoreCase("null")) {
                    user_name.setText("Admin");
                    initial.setText("A");

                } else {
                    user_name.setText(user);
                    String i = user.substring(0, 1).toUpperCase();
                    initial.setText(i);

                }
                GradientDrawable gd = (GradientDrawable) initialBg.getBackground().mutate();
                Random rnd = new Random();
                int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                gd.setColor(currentColor);
                //gd.invalidateSelf();
                initialBg.setBackground(gd);
                q.addView(view);
                commentPosition = q.indexOfChild(view);

                sendCommentToServer(comment, "0",levelId, courseId,user,topicTitle,contentId,userId,db);
            } else {
                isReply = false;
                replyToContainer.setVisibility(View.GONE);


                //ImageView clear = findViewById(R.id.clear);

                LayoutInflater inflater = LayoutInflater.from(context);
                LinearLayout l = q.getChildAt(commentPosition).findViewById(R.id.replies);
                Toast.makeText(context,"comment"+String.valueOf(commentPosition),Toast.LENGTH_SHORT).show();
                View vi = inflater.inflate(R.layout.reply_layout, l, false);
                TextView commentText = vi.findViewById(R.id.reply_text);
                TextView user_name = vi.findViewById(R.id.user);
                TextView initial = vi.findViewById(R.id.initials);
                TextView reply = vi.findViewById(R.id.reply);
                TextView date = vi.findViewById(R.id.date_layout);
                String  currentDateTime = DateFormat.getDateTimeInstance()
                        .format(new Date());
                date.setText(currentDateTime);
                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isReply = true;
                        replyToContainer.setVisibility(View.VISIBLE);
                        TextView replyTo = context.findViewById(R.id.replying_to);
                        TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.user);
                        //replyPosition = l.indexOfChild(vi);
                        //Log.i("replyPosition", String.valueOf(replyPosition));
                        ImageView clear = context.findViewById(R.id.clear);
                        clear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                replyToContainer.setVisibility(View.GONE);
                            }
                        });

                        commentEdittext.setText("reply");
                        CommentTable ctm = new CommentTable();
                        //commentPosition = ctm.getPosition();
                        //commentPosition = q.indexOfChild(view);
                        //replyTo.setText(commentModelList.get(commentPosition).getUsername());
                        commentEdittext.setText("");
                        //Toast.makeText(ContentDownload.this,commentModelList.get(commentPosition).getComment_id(),Toast.LENGTH_SHORT).show();


                    }
                });
                commentText.setText(commentEdittext.getText().toString());
                if (user.equalsIgnoreCase("null")) {
                    user_name.setText("Admin");
                    initial.setText("A");

                } else {
                    user_name.setText(user);
                    String i = user.substring(0, 1).toUpperCase();
                    initial.setText(i);
                }
                l.addView(vi);
                //commentPosition = q.indexOfChild(view);
                //Toast.makeText(context,String.valueOf(commentModelList.size()),Toast.LENGTH_SHORT).show();
                Log.i("response", String.valueOf(commentModelList.size()));

                //commentId = commentModelList.get(commentPosition).getComment_id();
                //Log.i("response", commentId);
                replyPosition = l.indexOfChild(vi);
                Toast.makeText(context,String.valueOf(commentModelList.size()),Toast.LENGTH_SHORT).show();


                //Log.i("reply2", comment_id);
                sendCommentToServer(comment, commentId,levelId, courseId,user,topicTitle,contentId,userId,db);
            }
        }
        if (!isReply) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        commentEdittext.setText("");
    }

}
