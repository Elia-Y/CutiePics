package ygz.cutiepics;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ygz.cutiepics.models.PhotoModel;
import ygz.cutiepics.models.Utility;

/**
 * Created by Raina on 2018-01-25.
 */

public class ChoosePhotoActivity extends Activity {
    private Button camera, gallery;
    private int REQUEST_CAMERA = 0;
    private final int SELECT_FILE = 1;
    public final int PHOTO_GALLERY_REQUEST = 20;
    private String mCurrentPhotoPath;
    private String type;
    private File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo);

        Bundle tp = getIntent().getExtras();
        type = (String) tp.get("type");

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_FILE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);

        camera = findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
            }
        });

        gallery = findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
            }
        });

        Typeface typeface = Typeface.createFromAsset(getAssets(), "unkempt.ttf");
        camera.setTypeface(typeface);
        gallery.setTypeface(typeface);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent();
                } else {
                    Toast.makeText(ChoosePhotoActivity.this, "Taking photo permision is denined", Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
//                onCaptureImageResult();
                mSavePhotoTask savePhoto = new mSavePhotoTask();
                savePhoto.execute("start");

                startNext();
            }
        } else {
            if (resultCode == Activity.RESULT_OK && data != null) {
                PhotoModel.setmUri(data.getData());

                startNext();
            }
        }
    }

    private class mSavePhotoTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");

            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            PhotoModel.setmUri(Uri.parse(mCurrentPhotoPath));
        }
    }

    private void startNext() {
        Intent intent;
        if (type.equals("sticker")) {
            intent = new Intent(ChoosePhotoActivity.this, StickerActivity.class);
        } else {
            intent = new Intent(ChoosePhotoActivity.this, FrameActivity.class);
        }
        startActivity(intent);
    }

    private void cameraIntent() {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // ensure that htere's a camera activity to hadle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                // create the File where the photo should go
                photoFile = createImageFile();
            } catch (IOException e) {
                // error occurred while creating the File
                Log.e("IOException", "unable to create image file");
                e.printStackTrace();
            }
            // continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(ChoosePhotoActivity.this, "com.example.android.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imgFileName, ".jpg", storageDir);

        // save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        Uri data = Uri.parse(pictureDirectoryPath);
        intent.setType("image/*");
        intent.setDataAndType(data, "image/*");
        startActivityForResult(intent, PHOTO_GALLERY_REQUEST);
    }

    public void onBackPressed() {
        Intent intent = new Intent(ChoosePhotoActivity.this, MainActivity.class);
        startActivity(intent);
    }

}