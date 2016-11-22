package com.example.user.imagesearch.appdata;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.appcompat.BuildConfig;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import net.gotev.uploadservice.UploadService;


public class AppController extends Application {



    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;


    private static AppController mInstance;




    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;


    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {


        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }





    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }



}