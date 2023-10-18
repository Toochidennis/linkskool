package com.digitaldream.linkskool.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.digitaldream.linkskool.activities.AnswerView;
import com.digitaldream.linkskool.activities.Login;
import com.digitaldream.linkskool.activities.NewsView;
import com.digitaldream.linkskool.activities.QuestionView;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.dialog.CustomDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AnswerBottomSheet extends BottomSheetDialogFragment {
    private ArrayList<String> images;
    private List<String> image1;
    LinearLayout editorContainer;
    LayoutInflater layoutInflater;
    GridView gallery;
    int itemPosition =0;
    private List<Uri> uriList;
    private String db,authorname,author_id;
    String from="";
    private static final int REQUEST_PERMISSIONS = 100;
    RefreshListener refreshListener;
    private boolean open = false;


    public static AnswerBottomSheet newInstance(String from) {
        AnswerBottomSheet bottomSheetFragment = new AnswerBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString("from",from);
        bottomSheetFragment .setArguments(bundle);
        return bottomSheetFragment ;
    }

    public AnswerBottomSheet(){

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.answer_bottom_sheet,container,false);
        from = getArguments().getString("from");

        image1 = new ArrayList<>();


        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");
        author_id = sharedPreferences.getString("user_id","");
        authorname = sharedPreferences.getString("user","");
        ImageView closeBtn = view.findViewById(R.id.close);
        TextView submitBtn = view.findViewById(R.id.submit);
         gallery = (GridView) view.findViewById(R.id.galleryGridView);
         TextView t = view.findViewById(R.id.debt_fee_title);
        EditText edt = view.findViewById(R.id.editT);
        if(from.equals("reply") || from.equals("news")){
             t.setText("Add Comment");
             edt.setHint("Add a comment");
         }
         TextView question = view.findViewById(R.id.question);
         uriList = new ArrayList<>();
         if(from.equals("answer")) {
             String questionText = QuestionView.feed.getQuestion().trim();
             questionText = questionText.substring(0,1).toUpperCase()+""+questionText.substring(1);
             String q = QuestionView.feed.getQuestion().trim();
             try {
                 questionText = questionText.substring(0, 1).toUpperCase() + questionText.substring(1);
                 q = q.substring(questionText.length() - 1).trim();
                 if (q.equalsIgnoreCase("?")) {
                     question.setText(questionText);

                 } else {
                     question.setText(questionText + "?");
                 }
             } catch (StringIndexOutOfBoundsException e) {
                 e.printStackTrace();
             }
         }else if(from.equals("reply")){
             String ansText = AnswerView.feed.getQuestion().trim();

             Object json = null;
             try {
                 json = new JSONTokener(ansText).nextValue();

             if(json instanceof JSONArray) {

                 JSONArray answer = new JSONArray(ansText);
                 boolean checktext =true;boolean checkImage = true;
                 for (int c = 0; c < answer.length(); c++) {
                     JSONObject object1 = answer.optJSONObject(c);
                     String type = object1.optString("type").trim();

                     if (type.equalsIgnoreCase("text")&&checktext) {
                         String content = object1.optString("content");
                         content = content.substring(0,1).toUpperCase()+""+content.substring(1);
                         question.setText(content);

                         //feed.setPreText(content);
                         checktext=false;
                     }

                 }
             }
             } catch (JSONException e) {
                 e.printStackTrace();
             }

         }else if(from.equals("news")){
             question.setText(NewsView.title);
         }
        //gallery.setAdapter(new ImageAdapter(getActivity()));
        layoutInflater = LayoutInflater.from(getContext());
        //itemPosition = editorContainer.getChildCount();
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                itemPosition = editorContainer.getChildCount();
                CharSequence cursorToEnd = "";
                for(int a =0;a<editorContainer.getChildCount();a++){
                    View v = editorContainer.getChildAt(a);
                    if(editorContainer.getFocusedChild()==v){
                        EditText edt = v.findViewById(R.id.editT);
                        int cursorPosition = edt.getSelectionStart();
                        CharSequence enteredText = edt.getText().toString();
                        cursorToEnd = enteredText.subSequence(cursorPosition, enteredText.length());
                        CharSequence beforeCursor = enteredText.subSequence(0,cursorPosition);
                        edt.setText(beforeCursor);
                        itemPosition = a+1;
                    }

                }
                if (null != images && !images.isEmpty()) {
                    View view1 = layoutInflater.inflate(R.layout.image_view_item, editorContainer, false);
                    ImageView image = view1.findViewById(R.id.img_v);
                    EditText editText = view1.findViewById(R.id.editT);
                    editText.setText(cursorToEnd);
                    editText.requestFocus();
                    image.setImageURI(Uri.parse(images.get(position)));
                    //editorContainer.addView(view1);

                    editorContainer.addView(view1,itemPosition);
                    image1.add(images.get(position));
                }


            }
        });

        editorContainer = view.findViewById(R.id.editor_container);
        editorContainer.setOnClickListener(v -> gallery.setVisibility(View.GONE));

        submitBtn.setOnClickListener(v -> buildAnswerJson(view));

        closeBtn.setOnClickListener(v -> dismiss());

        ImageView selectImgBtn = view.findViewById(R.id.select_img);
        selectImgBtn.setOnClickListener(v -> {
            if ((ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE))) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSIONS);

                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSIONS);
                }
            }else {
                gallery.setAdapter(new ImageAdapter(getActivity()));
                gallery.setVisibility(View.VISIBLE);
                open = true;
            }

        });

        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            gallery.setAdapter(new ImageAdapter(getActivity()));
            gallery.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
          super.onCreateDialog(savedInstanceState);

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            BottomSheetBehavior behaviour = BottomSheetBehavior.from(bottomSheet);

        });

       return dialog;

    }


    /*for (EditText view : editList){
        view.setOnFocusChangeListener(focusListener);
    }

    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus){
                focusedView = v;
            } else {
                focusedView  = null;
            }
        }
    }*/



    private class ImageAdapter extends BaseAdapter {

        /** The context. */
        private Activity context;

        /**
         * Instantiates a new image adapter.
         *
         * @param localContext
         *            the local context
         */
        public ImageAdapter(Activity localContext) {
            context = localContext;
            images = getAllShownImagesPath(context);
            //Collections.reverse(images);
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView
                        .setLayoutParams(new GridView.LayoutParams(270, 270));

            } else {
                picturesView = (ImageView) convertView;
            }

            Glide.with(context).load(images.get(position))
                    .placeholder(R.drawable.ic_launcher_background).centerCrop()
                    .into(picturesView);

            return picturesView;
        }

        /**
         * Getting All Images Path.
         *
         * @param activity
         *            the activity
         * @return ArrayList with images Path
         */
        private ArrayList<String> getAllShownImagesPath(Activity activity) {
            Uri uri;
            Cursor cursor;
            int column_index_data, column_index_folder_name;
            ArrayList<String> listOfAllImages = new ArrayList<String>();
            String absolutePathOfImage = null;
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = { MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, null);

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(absolutePathOfImage);
            }
            return listOfAllImages;
        }
    }

    private void buildAnswerJson(View v){
        JSONArray jsonArray = new JSONArray();
        LinearLayout editorContainer = v.findViewById(R.id.editor_container);
        for(int a=0;a<editorContainer.getChildCount();a++){
            if(a==0){
                EditText e = (EditText) editorContainer.getChildAt(a);
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type","text");
                    obj.put("content",e.getText().toString());
                    jsonArray.put(obj);

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }else{
                LinearLayout l = (LinearLayout) editorContainer.getChildAt(a);
                EditText edt = l.findViewById(R.id.editT);
                ImageView img = l.findViewById(R.id.img_v);
                try {
                    JSONObject obj1 = new JSONObject();
                    obj1.put("content",getStringFile(saveBitmapToFile(new File(Uri.parse(image1.get(a-1)).getPath()))));
                    obj1.put("type","image");
                    obj1.put("filename",new File(Uri.parse(image1.get(a-1)).getPath()).getName());
                    jsonArray.put(obj1);
                    JSONObject obj2 = new JSONObject();
                    obj2.put("content",edt.getText().toString());
                    obj2.put("type","text");
                    jsonArray.put(obj2);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
        Log.i("response",jsonArray.toString());
        for(int c=0;c<jsonArray.length();c++){
            if(c<2) {
                try {
                    JSONObject object = jsonArray.getJSONObject(c);
                    String content = object.getString("content");
                    if (content.isEmpty()) {
                        Toast.makeText(getContext(), "Reply is empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        switch (from) {
            case "answer":
                sendAnswerApi(jsonArray);
                break;
            case "reply":
                sendCommentApi(jsonArray);
                break;
            case "news":
                sendCommentApi(jsonArray);
                break;
        }
    }

    public String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile= "", lastVal;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile =  output.toString();
        }
        catch (FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }

    private void sendAnswerApi(JSONArray jsonArray){
        CustomDialog dialog = new CustomDialog((Activity) getContext());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        String url = Login.urlBase+"/addAnswer.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.i("response",response);
                dismiss();
                if(response!=null){
                    Toast.makeText(getContext(),"Answer added",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(getContext(),"error: "+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("answer",jsonArray.toString());
                params.put("title",QuestionView.feed.getQuestion());
                params.put("question_id",QuestionView.feed.getId());
                params.put("author_id",author_id);
                if(authorname==null){
                    authorname = "Admin";
                }
                params.put("author_name",authorname);
                params.put("_db",db);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);


    }

    private void sendCommentApi(JSONArray jsonArray){
        CustomDialog dialog = new CustomDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        String url = Login.urlBase+"/addComment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response","comment :"+response);
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if(status.equals("success")){
                        Toast.makeText(getContext(),"Reply added successfully",Toast.LENGTH_SHORT).show();
                        dismiss();
                    }else {
                        Toast.makeText(getContext(),"operation failed",Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"error"+error.getMessage(),Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                dialog.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("comment",jsonArray.toString());
                if(AnswerView.feed!=null) {
                    params.put("parent", AnswerView.feed.getId());
                    params.put("content_title", AnswerView.feed.getQuestion());
                    params.put("content_id", AnswerView.feed.getId());
                }else if(NewsView.feed!=null){
                    params.put("parent", NewsView.feed.getId());
                    params.put("content_title", NewsView.feed.getQuestion());
                    params.put("content_id", NewsView.feed.getId());
                }
                params.put("author_name",authorname);
                params.put("author_id",author_id);
                params.put("_db",db);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    public void DismissListener(RefreshListener closeListener) {
        this.refreshListener = closeListener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(refreshListener != null) {
            refreshListener.onRefresh(dialog);
        }
    }
    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

}
