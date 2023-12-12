package com.digitaldream.winskool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.digitaldream.winskool.activities.Login;
import com.digitaldream.winskool.R;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;

public class ImagePreviewDialog extends Dialog {
    private Activity activity;
    private String imageUrl;
    private String from,tag;
    public ImagePreviewDialog(@NonNull Activity context,String url,String from,String tag) {
        super(context);
        this.activity = context;
        this.imageUrl = url;
        this.from = from;
        this.tag = tag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_preview_layout);
        ImageView img = findViewById(R.id.img6);
        if(from.equals("preview")) {
            if(tag.equals("0")) {
                Uri uri = Uri.parse(imageUrl);
                img.setImageURI(uri);
            }else if(tag.equals("1")) {
                Picasso.get().load(Login.urlBase + "/" + imageUrl).into(img);
            }
        }else if(from.equals("assessment")){
            String path=getContext().getResources().getString(R.string.file_url);
            Picasso.get().load(Login.urlBase+"/"+imageUrl).into(img);
        }else if(from.equals("cbt")){
            Picasso.get().load(getContext().getResources().getString(R.string.file_url)+"/"+imageUrl).into(img);

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }
}
