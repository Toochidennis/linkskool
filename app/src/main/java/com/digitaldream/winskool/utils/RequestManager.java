package com.digitaldream.winskool.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestManager {

    private static RequestManager mRequestManager;
    /**
     * Queue which Manages the Network Requests :-)
     */
    private static RequestQueue mRequestQueue;
    // ImageLoader Instance

    private RequestManager() {

    }

    public static RequestManager get(Context context) {

        if (mRequestManager == null)
            mRequestManager = new RequestManager();

        return mRequestManager;
    }

    /**
     * @param context application context
     */
    public static RequestQueue getnstance(Context context) {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;

    }
}
