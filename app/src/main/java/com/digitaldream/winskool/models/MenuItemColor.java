package com.digitaldream.winskool.models;

import static com.digitaldream.winskool.BuildConfig.DEBUG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class MenuItemColor {
    public static void setMenuTextColor(final Context sContext,
                                        final Toolbar sToolbar,
                                        final int sMenuResId, final int sColor){
        sToolbar.post(new Runnable() {
            @Override
            public void run() {
                View view = sToolbar.findViewById(sMenuResId);
                if (view instanceof TextView){
                    if (DEBUG){
                        Log.i("Error", "view textview");
                    }
                    TextView textView = (TextView) (view);
                    textView.setTextColor(ContextCompat.getColor(sContext, sColor));

                }
            }
        });

    }
}
