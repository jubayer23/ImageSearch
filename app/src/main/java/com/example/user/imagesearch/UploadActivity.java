package com.example.user.imagesearch;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.user.imagesearch.appdata.Constant;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.UUID;

public class UploadActivity extends Activity {


    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String KEY_SEARCH_URL = "url";

    private ImageView imgPreview;
    private ProgressBar progressBar;
    private String filePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        init();

        Intent i = getIntent();
        filePath = i.getStringExtra(MainActivity.KEY_FILE_PATH);
        previewMedia(filePath);


        startUploadingToServer(filePath);
    }

    private void init() {
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Displaying captured image/video on the screen
     */
    private void previewMedia(String path) {

        imgPreview.setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        final Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        imgPreview.setImageBitmap(bitmap);
    }


    private void startUploadingToServer(String path) {
        //Uploading code
        progressBar.setVisibility(View.VISIBLE);


        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, Constant.URL_FILE_UPLOAD)
                    .addFileToUpload(path, "image") //Adding file
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setAutoDeleteFilesAfterSuccessfulUpload(true)
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(UploadInfo uploadInfo) {
                        }

                        @Override
                        public void onError(UploadInfo uploadInfo, Exception exception) {
                            Toast.makeText(UploadActivity.this,"Error During Upload",Toast.LENGTH_LONG).show();
                            finish();

                        }

                        @Override
                        public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {

                            progressBar.setVisibility(View.GONE);

                            lunchWebViewForSearchInGoogle();
                        }

                        @Override
                        public void onCancelled(UploadInfo uploadInfo) {

                        }
                    })
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void lunchWebViewForSearchInGoogle() {

        String image_name = filePath.substring(filePath.indexOf("IMG")).trim();
        Intent i = new Intent(UploadActivity.this, WebViewActivity.class);
        i.putExtra(KEY_SEARCH_URL, Constant.URL_SEARCH + image_name);
        startActivity(i);
        finish();
    }



}