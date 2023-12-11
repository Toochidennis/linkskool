package com.digitaldream.linkskool.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.digitaldream.linkskool.activities.Dashboard;
import com.digitaldream.linkskool.dialog.CustomDialog;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.fragments.AdminDashboardFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class AddNewsBottomSheet extends BottomSheetDialogFragment {
    private ArrayList<String> images;
    private List<String> image1;

    LinearLayout editorContainer;
    LayoutInflater layoutInflater;
    GridView gallery;
    int itemPosition = 0;
    private List<Uri> uriList;
    private String db, authorname, author_id;
    String from = "";
    private static final int REQUEST_PERMISSIONS = 100;
    RefreshListener refreshListener;
    private EditText titleEDT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_news_bottomsheet, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail",
                Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db", "");
        author_id = sharedPreferences.getString("user_id", "");
        authorname = sharedPreferences.getString("user", "");
        ImageView closeBtn = view.findViewById(R.id.close);
        TextView submitBtn = view.findViewById(R.id.submit);
        gallery = view.findViewById(R.id.galleryGridView);
        //TextView t = view.findViewById(R.id.title);
        //EditText edt = view.findViewById(R.id.editT);
        titleEDT = view.findViewById(R.id.news_title);

        image1 = new ArrayList<>();

        //TextView question = view.findViewById(R.id.question);
        uriList = new ArrayList<>();


        //gallery.setAdapter(new ImageAdapter(getActivity()));
        layoutInflater = LayoutInflater.from(getContext());
        //itemPosition = editorContainer.getChildCount();
        gallery.setOnItemClickListener((arg0, arg1, position, arg3) -> {

            itemPosition = editorContainer.getChildCount();
            CharSequence cursorToEnd = "";
            for (int a = 0; a < editorContainer.getChildCount(); a++) {
                View v = editorContainer.getChildAt(a);
                if (editorContainer.getFocusedChild() == v) {
                    EditText edt = v.findViewById(R.id.editT);
                    int cursorPosition = edt.getSelectionStart();
                    CharSequence enteredText = edt.getText().toString();
                    cursorToEnd = enteredText.subSequence(cursorPosition, enteredText.length());
                    CharSequence beforeCursor = enteredText.subSequence(0, cursorPosition);
                    edt.setText(beforeCursor);
                    itemPosition = a + 1;
                }

            }
            if (null != images && !images.isEmpty()) {
                View view1 = layoutInflater.inflate(R.layout.image_view_item, editorContainer,
                        false);
                ImageView image = view1.findViewById(R.id.img_v);
                EditText editText = view1.findViewById(R.id.editT);
                editText.setText(cursorToEnd);
                editText.requestFocus();
                image.setImageURI(Uri.parse(images.get(position)));
                //editorContainer.addView(view1);
                editorContainer.addView(view1, itemPosition);
                image1.add(images.get(position));
            }


        });

        editorContainer = view.findViewById(R.id.editor_container);
        editorContainer.setOnClickListener(v -> gallery.setVisibility(View.GONE));

        submitBtn.setOnClickListener(v -> {
            if (titleEDT.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "News Title is empty", Toast.LENGTH_SHORT).show();
            } else {
                buildAnswerJson(view);
            }

        });

        closeBtn.setOnClickListener(v -> dismiss());

        ImageView selectImgBtn = view.findViewById(R.id.select_img);
        selectImgBtn.setOnClickListener(v -> {
            if ((ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                    getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(
                        getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE))) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSIONS);

                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSIONS);
                }
            } else {
                gallery.setAdapter(new ImageAdapter(getActivity()));
                gallery.setVisibility(View.VISIBLE);
            }

        });
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = (FrameLayout) d.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            BottomSheetBehavior behaviour = BottomSheetBehavior.from(bottomSheet);

        });

        return dialog;
    }

    private class ImageAdapter extends BaseAdapter {

        /**
         * The context.
         */
        private Activity context;

        /**
         * Instantiates a new image adapter.
         *
         * @param localContext the local context
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
         * @param activity the activity
         * @return ArrayList with images Path
         */
        private ArrayList<String> getAllShownImagesPath(Activity activity) {
            Uri uri;
            Cursor cursor;
            int column_index_data, column_index_folder_name;
            ArrayList<String> listOfAllImages = new ArrayList<String>();
            String absolutePathOfImage = null;
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

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

    private void buildAnswerJson(View v) {
        JSONArray jsonArray = new JSONArray();
        LinearLayout editorContainer = v.findViewById(R.id.editor_container);
        for (int a = 0; a < editorContainer.getChildCount(); a++) {
            if (a == 0) {
                EditText e = (EditText) editorContainer.getChildAt(a);
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "text");
                    obj.put("content", e.getText().toString());
                    jsonArray.put(obj);

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            } else {
                LinearLayout l = (LinearLayout) editorContainer.getChildAt(a);
                EditText edt = l.findViewById(R.id.editT);
                ImageView img = l.findViewById(R.id.img_v);
                try {
                    JSONObject obj1 = new JSONObject();
                    obj1.put("content",
                            getStringFile(new File(Uri.parse(image1.get(a - 1)).getPath())));
                    obj1.put("type", "image");
                    obj1.put("filename",
                            new File(Uri.parse(image1.get(a - 1)).getPath()).getName());
                    jsonArray.put(obj1);
                    JSONObject obj2 = new JSONObject();
                    obj2.put("content", edt.getText().toString());
                    obj2.put("type", "text");
                    jsonArray.put(obj2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

        Log.i("response", "" + jsonArray.toString());
        addNews(jsonArray);
    }

    public String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile = "", lastVal;
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
            encodedFile = output.toString();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }

    private void addNews(JSONArray jsonArray) {
        CustomDialog dialog = new CustomDialog((Activity) getContext());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        String url = getString(R.string.base_url) + "/addNews.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            AdminDashboardFragment.json = "";
                            Intent intent = new Intent(getContext(), Dashboard.class);
                            startActivity(intent);
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("answer", jsonArray.toString());
                params.put("title", titleEDT.getText().toString());
                params.put("author_id", author_id);
                if (authorname == null) {
                    authorname = "Admin";
                }
                params.put("author_name", authorname);
                params.put("_db", db);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}
