package ygz.cutiepics;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ygz.cutiepics.models.PhotoModel;
import ygz.cutiepics.models.UploadInfo;

/**
 * Created by Raina on 2018-03-20.
 */

public class SearchUserActivity extends Activity {
    private EditText searchField;
    private Button add, share;
    private GridLayout selected;

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference fileRef;

    private List<String> mUsers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchField = findViewById(R.id.searchField);
        add = findViewById(R.id.addBut);
        share = findViewById(R.id.shareTo);
        selected = findViewById(R.id.selectedGrid);

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference().child("images");
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("images");

        mUsers = new ArrayList<>();

        add.setOnClickListener(searchOnClickListener);

        share.setOnClickListener(shareOnClickListener);
    }

    View.OnClickListener searchOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = searchField.getText().toString();
                // only search for other users that haven't been added
                String mEmail = mUser.getEmail();
                if (!email.equals(mEmail) && !searchAdd(email)) {
                    searchUser(email);
                }
            }
    };

    View.OnClickListener shareOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // add permission to added users about the shared photo
            Uri photo = getImageUri(SearchUserActivity.this, PhotoModel.getmPhoto());
            uploadImage(photo);
        }
    };



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mUser = mAuth.getCurrentUser();
    }

    public void searchUser(final String email) {
        Query query = mDatabase.child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    searchField.setText("");

                    // dataSnapshot is the users node with all children with "email" = email
                    for (DataSnapshot users : dataSnapshot.getChildren()) {
                        final TextView tv = new TextView(SearchUserActivity.this);
                        tv.setText(email);
                        tv.setBackgroundResource(R.drawable.my_button);
                        tv.setPadding(10, 4, 10, 4);
                        selected.addView(tv);
                        mUsers.add(email);

                        // long press tv to delete add
                        tv.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                tv.setVisibility(View.GONE);
                                String tmp = tv.getText().toString();
                                int index = searchIndex(tmp);
                                mUsers.remove(index);
                                return false;
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean searchAdd(String email) {
        for (String addedEmail : mUsers) {
            if (addedEmail.equals(email)) return true;
        }
        return false;
    }

    public int searchIndex(String email) {
        for (int i=0; i<mUsers.size(); i++) {
            String addedEmail = mUsers.get(i);
            if (addedEmail.equals(email)) {
                return i;
            }
        }
        return -1;
    }

    private void uploadImage(Uri filePath) {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            Random generator = new Random();
            int n = Integer.MAX_VALUE;
            n = generator.nextInt(n);
            String fname = "Image" + n;
            fileRef = storageReference.child(fname);

            fileRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            String name = taskSnapshot.getMetadata().getName();
                            String url = taskSnapshot.getDownloadUrl().toString();

                            writeNewImageInfoToDB(name, url);

                            Toast.makeText(SearchUserActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SearchUserActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
