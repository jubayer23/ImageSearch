package com.example.user.imagesearch;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.user.imagesearch.alertbanner.AlertDialogForAnything;
import com.example.user.imagesearch.utils.AccessDirectory;
import com.example.user.imagesearch.utils.CheckDeviceConfig;
import com.example.user.imagesearch.utils.MarshMallowPermission;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int FROM_GALLERY_CODE = 100;
    private static final int FROM_CAMERA_CODE = 101;
    public static final String KEY_FILE_PATH = "filePath";

    private Button btn_from_gallery, btn_take_pic;


    private CheckDeviceConfig checkDeviceConfig;
    private MarshMallowPermission mp;


    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Search By Image");
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        //Requesting storage permission

        init();

        checkPermission();
    }

    private boolean checkPermission() {

        if (mp.checkPermissionForExternalStorage()) {
            if (!mp.checkPermissionForCamera()) {
                mp.requestPermissionForCamera();
                return false;
            } else {
                return true;
            }
        } else {
            mp.requestPermissionForExternalStorage();
            return false;
        }
    }

    private void init() {

        checkDeviceConfig = new CheckDeviceConfig(this);
        mp = new MarshMallowPermission(this);


        btn_from_gallery = (Button) findViewById(R.id.btn_from_gallery);
        btn_from_gallery.setOnClickListener(this);
        btn_take_pic = (Button) findViewById(R.id.btn_take_pic);
        btn_take_pic.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (!checkDeviceConfig.isConnectingToInternet()) {
            AlertDialogForAnything.showAlertDialogWhenComplte(MainActivity.this, "No Internet Connection",
                    "You don't have any internet connection.", false);
            return;
        }


        if (!checkPermission()) return;

        if (id == R.id.btn_take_pic) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = getOutputMediaFileUri();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, FROM_CAMERA_CODE);
        }
        if (id == R.id.btn_from_gallery) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            fileUri = getOutputMediaFileUri();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(getCropIntent(intent), FROM_GALLERY_CODE);
        }
    }


    private Intent getCropIntent(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        return intent;
    }


    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(AccessDirectory.getOutputMediaFile());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FROM_GALLERY_CODE) {

                Toast.makeText(MainActivity.this, "Image Selected From Gallery", Toast.LENGTH_SHORT).show();

                Uri photoUri = data.getData();
                if (photoUri != null) {
                    fileUri = photoUri;
                }
                launchUploadActivity();
            } else if (requestCode == FROM_CAMERA_CODE) {

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(fileUri, "image/*");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(getCropIntent(intent), FROM_GALLERY_CODE);


                //Intent intent = new Intent("com.android.camera.action.CROP");
                //intent.setDataAndType(fileUri, "image/*");
                //startActivityForResult(getCropIntent(intent), FROM_GALLERY_CODE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void launchUploadActivity() {
        Intent i = new Intent(MainActivity.this, UploadActivity.class);
        i.putExtra(KEY_FILE_PATH, fileUri.getPath());
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.action_share:

                Intent intent2 = new Intent(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id="
                        + BuildConfig.APPLICATION_ID);
                intent2.putExtra(Intent.EXTRA_SUBJECT, "Hey..Find out this wonderful apps that i think you should try it!!");

                startActivity(Intent.createChooser(intent2, "Share"));

                // help action
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == MarshMallowPermission.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
            mp.requestPermissionForCamera();
        }
        if (requestCode == MarshMallowPermission.CAMERA_PERMISSION_REQUEST_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can use the camera", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }

        }
    }

}
