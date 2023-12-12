package com.digitaldream.winskool.utils;

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
import com.digitaldream.winskool.activities.Login;
import com.digitaldream.winskool.models.CommentTable;
import com.digitaldream.winskool.R;

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

public class Comments {
    private Activity context;
    private ImageView sendBtn;
    private EditText commentEdittext;
    private ScrollView scrollView;
    private static boolean isReply = false;
    private static int commentPosition,replyPosition;
    private List<CommentTable> commentModelList ;
    private RelativeLayout replyToContainer;



    public Comments(Activity context) {
        this.context = context;
        replyToContainer = context.findViewById(R.id.replying_to_cont);
        replyToContainer.setVisibility(View.GONE);
        scrollView = context.findViewById(R.id.scroll);
        commentModelList = new ArrayList<>();
    }

    public void fetchComments(String db, String contentId, ProgressBar progressBar1) {

        String url = Login.urlBase + "/getComment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar1.setVisibility(View.GONE);
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
                Log.i("json", "error" + error.getMessage());
                progressBar1.setVisibility(View.GONE);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("_db", db);
                params.put("id", contentId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void createParent(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("0 ");
            for (int a = 0; a < jsonArray.length(); a++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(a);
                String id = jsonObject1.getString("id");
                String comment = jsonObject1.getString("body");
                String user = jsonObject1.getString("author_name");
                String dateText = jsonObject1.getString("upload_date");
                LinearLayout q = context.findViewById(R.id.comments);

                CommentTable commentObject = new CommentTable();
                commentObject.setComment_id(id);
                if (user.equalsIgnoreCase("null")) {
                    commentObject.setUsername("Admin");

                } else {
                    commentObject.setUsername(user);
                }
                commentModelList.add(commentObject);

                LayoutInflater layoutInflater = LayoutInflater.from(context);
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
                        commentEdittext = context.findViewById(R.id.comment_text1);
                        commentEdittext.setText("");
                        CommentTable ctm = new CommentTable();
                        //commentPosition = ctm.getPosition();
                        commentPosition = q.indexOfChild(view);
                        int position = commentPosition;
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
                int position = commentPosition;
                createReply(id, jsonObject, reply_layout, position);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void createReply(String commentId, JSONObject jsonObject, LinearLayout l, int position) {

        if (jsonObject.has(commentId)) {
            LinearLayout q = context.findViewById(R.id.comments);

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

                    LayoutInflater inflater = LayoutInflater.from(context);
                    LinearLayout l1 = q.getChildAt(commentPosition).findViewById(R.id.replies);
                    View vi = inflater.inflate(R.layout.reply_layout, l, false);
                    vi.setTag(position);
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
                            //TextView l1 = q.getChildAt(commentPosition).findViewById(R.id.user);
                            //replyPosition = l.indexOfChild(vi);
                            //Log.i("replyPosition", String.valueOf(replyPosition));
                            ImageView clear = context.findViewById(R.id.clear);
                            clear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    replyToContainer.setVisibility(View.GONE);
                                }
                            });
                            commentEdittext = context.findViewById(R.id.comment_text1);

                            commentEdittext.setText("reply");
                            CommentTable ctm = new CommentTable();
                            //commentPosition = ctm.getPosition();
                            //commentPosition = l.indexOfChild(vi);
                            commentPosition = position;
                            replyTo.setText(commentModelList.get(commentPosition).getUsername());
                            commentEdittext.setText("");
                            Toast.makeText(context, String.valueOf(vi.getTag()), Toast.LENGTH_SHORT).show();

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

    public void sendComment(String levelId,String courseId,String user, String topicTitle,String contentId,String userId,String db){
        commentEdittext = context.findViewById(R.id.comment_text1);
        String comment = commentEdittext.getText().toString();
        LinearLayout q = context.findViewById(R.id.comments);
        if (!comment.isEmpty()) {

            if (!isReply) {
                LinearLayout commentsLayout = context.findViewById(R.id.comments);
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View view = layoutInflater.inflate(R.layout.comment_layout, q, false);
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
                try {
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
                            TextView commentId = view.findViewById(R.id.comment_id);
                            //comment_id = commentId.getText().toString();
                            replyTo.setText(commentModelList.get(commentPosition).getUsername());


                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
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

                //sendCommentToServer("0");
            } else {
                isReply = false;
                replyToContainer.setVisibility(View.GONE);


                //ImageView clear = findViewById(R.id.clear);

                LayoutInflater inflater = LayoutInflater.from(context);
                LinearLayout l = q.getChildAt(commentPosition).findViewById(R.id.replies);
                View vi = inflater.inflate(R.layout.reply_layout, l, false);
                TextView commentText = vi.findViewById(R.id.reply_text);
                TextView user_name = vi.findViewById(R.id.user);
                TextView initial = vi.findViewById(R.id.initials);
                TextView reply = vi.findViewById(R.id.reply);
                TextView date = vi.findViewById(R.id.date_layout);
                String  currentDateTime = DateFormat.getDateTimeInstance()
                        .format(new Date());
                date.setText(currentDateTime);
                try {
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
                            replyTo.setText(commentModelList.get(commentPosition).getUsername());
                            commentEdittext.setText("");
                            Toast.makeText(context, commentModelList.get(commentPosition).getComment_id(), Toast.LENGTH_SHORT).show();


                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
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
                ///commentPosition = q.getTag();
                String commentId = commentModelList.get(commentPosition).getComment_id();
                Log.i("response", String.valueOf(commentModelList.size()));
                replyPosition = l.indexOfChild(vi);
                //Log.i("reply2", comment_id);
                //sendCommentToServer(commentId);
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
        //Comment cm = new Comment(ContentDownload.this);
        //cm.sendComment(comment,levelId,courseId,user,topicTitle,contentId,userId,db);


        }


}
