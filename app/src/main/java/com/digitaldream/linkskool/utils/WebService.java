package com.digitaldream.linkskool.utils;

import com.android.volley.RequestQueue;

public class WebService {

    private RequestQueue mRequestQueue;
    private static WebService apiRequests = null;

    public static WebService getInstance() {
        if (apiRequests == null) {
            apiRequests = new WebService();
            return apiRequests;
        }
        return apiRequests;
    }
    /*public void uploadContent(Context context, String week, String title, String courseObjective, String filePath1,String filePath2, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        SimpleMultiPartRequest request = new SimpleMultiPartRequest(Request.Method.POST, "YOUR URL HERE", listener, errorListener);
//        request.setParams(data);
        mRequestQueue = RequestManager.getnstance(context);
        request.addMultipartParam("week", "text", week);
        request.addMultipartParam("title", "text", title);
        request.addMultipartParam("objective", "text", courseObjective);
        request.addFile("note_material", filePath1);
        request.addFile("other_material",filePath2);

        request.setFixedStreamingMode(true);
        mRequestQueue.add(request);
    }*/
}
