package ygz.cutiepics;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Random;

/**
 * Created by yuyuxiao on 2018-03-20.
 *
 * This class is used for savedPhoto activity
 */

public class SavePhoto extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference fileRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_photo);

        Bitmap bmp = photoModel.getmPhoto();
        saveImageToGallery(bmp);

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference().child("images");
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("images");

        progressDialog = new ProgressDialog(this);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mUser = mAuth.getCurrentUser();
    }


    public String saveImageToGallery(Bitmap saved) {
        String result = MediaStore.Images.Media.insertImage(getContentResolver(), saved, "" , "");
        return result;
    }

    public void editNext(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        photoModel.getmPhoto().recycle();
    }

    public void sharePhoto(View view) {
        // TODO: share photo to groups
        Uri photo = getImageUri(SavePhoto.this, photoModel.getmPhoto());
        uploadImageAsyncTask uiat = new uploadImageAsyncTask();
        uiat.execute(photo);
    }

    private class uploadImageAsyncTask extends AsyncTask<Uri, String, String> {
        @Override
        protected String doInBackground(Uri... uris) {
            Uri filePath = uris[0];
            if(filePath != null) {
                Random generator = new Random();
                int n = Integer.MAX_VALUE;
                n = generator.nextInt(n);
                String fname = "Image" + n;
                fileRef = storageReference.child(fname);

                fileRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String name = taskSnapshot.getMetadata().getName();
                                String url = taskSnapshot.getDownloadUrl().toString();

                                writeNewImageInfoToDB(name, url);

                                Toast.makeText(SavePhoto.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SavePhoto.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded "+(int)progress+"%");
                            }
                        });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setTitle("Uploading...");
            progressDialog.setMessage(null);
            progressDialog.show();
        }
    }

    private void writeNewImageInfoToDB(String name, String url) {
        UploadInfo info = new UploadInfo(name, url);

        String key = mDatabase.push().getKey();
        mDatabase.child(key).setValue(info);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
