package ygz.cutiepics;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import java.io.File;
import java.util.ArrayList;

import ygz.cutiepics.models.FrameObject;
import ygz.cutiepics.models.PhotoModel;


/**
 * Created by Yuxiao Yu on 2018-02-01.
 */

public class FrameActivity extends Activity {
    private static ImageView img;
    private Drawable origin;

    private boolean added = false;
    private int add_pos = -1;
    private ArrayList<FrameObject> frames;

    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        img = findViewById(R.id.ivImage);
        img.setImageURI(PhotoModel.getmUri());

        origin = img.getDrawable();

        final RecyclerView rv = findViewById(R.id.frame_view);
        GridLayoutManager mGrid = new GridLayoutManager(this, 4);
        rv.setLayoutManager(mGrid);
        rv.setHasFixedSize(true);
        FrameAdapter mAdapter = new FrameAdapter(FrameActivity.this, getFrameTestData());
        rv.setAdapter(mAdapter);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, rv ,new RecyclerItemClickListener.OnItemClickListener() {
                    public void onItemClick(View view, int position) {
                        FrameViewHolder fvh = (FrameViewHolder) rv.findViewHolderForAdapterPosition(position);
                        ImageView temp = fvh.getFrame();
                        BitmapDrawable frame_origin = (BitmapDrawable) temp.getDrawable();
                        Bitmap frame = frame_origin.getBitmap();

                        if (added && position == add_pos) {
                                added = false;
                                img.setImageDrawable(origin);

                                mSavePhotoTask savePhoto = new mSavePhotoTask();
                                savePhoto.execute("start");
                                return;
                        }

                        if (position != RecyclerView.NO_POSITION) {
                            added = true;
                            add_pos = position;

                            // add frame to existing photo in async task
                            mAddFrameTask addFrame = new mAddFrameTask();
                            addFrame.execute(frame);
                        }
                    }

                    public void onLongItemClick(View view, int position) {
                        // do nothing
                    }
                })
        );
    }

    private class mSavePhotoTask extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Drawable saved_drawable = img.getDrawable();
            final int width = saved_drawable.getIntrinsicWidth();
            final int height = saved_drawable.getIntrinsicHeight();

            final Bitmap saved_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            saved_drawable.draw(new Canvas(saved_bitmap));
            return saved_bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            PhotoModel.setmPhoto(bitmap);
        }
    }

    private class mAddFrameTask extends AsyncTask<Bitmap, String, LayerDrawable> {

        @Override
        protected LayerDrawable doInBackground(Bitmap... bitmaps) {
            Drawable image = origin;
            Drawable[] array = new Drawable[2];

            Bitmap image_bm = ((BitmapDrawable)image).getBitmap();

            Bitmap frame_bm = resize(bitmaps[0], image_bm.getWidth(), image_bm.getHeight());

            array[0] = image;
            array[1] = new BitmapDrawable(getResources(), frame_bm);
            LayerDrawable layer = new LayerDrawable(array);
            return layer;
        }

        @Override
        protected void onPostExecute(LayerDrawable s) {
            super.onPostExecute(s);

            img.setImageDrawable(s);

            mSavePhotoTask savePhoto = new mSavePhotoTask();
            savePhoto.execute("start");
        }
    }

    // This is a helper function copied from online
    public Bitmap resize(Bitmap bm, int w, int h)
    {

        Bitmap BitmapOrg = bm;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;

    }

    private ArrayList<FrameObject> getFrameTestData() {
        ArrayList<FrameObject> featuredFrame = new ArrayList<FrameObject>();
        featuredFrame.add(new FrameObject("frame1"));
        featuredFrame.add(new FrameObject("frame2"));
        featuredFrame.add(new FrameObject("frame3"));
        featuredFrame.add(new FrameObject("frame4"));
        featuredFrame.add(new FrameObject("frame5"));
        featuredFrame.add(new FrameObject("frame6"));
        featuredFrame.add(new FrameObject("frame7"));
        featuredFrame.add(new FrameObject("frame8"));
        //featuredFrame.add(new FrameObject("frame9"));
        //featuredFrame.add(new FrameObject("frame10"));
        //featuredFrame.add(new FrameObject("frame11"));
        return featuredFrame;
    }

    public void saveImg(View view) {
        Intent intent = new Intent(FrameActivity.this, SavePhotoActivity.class);
        startActivity(intent);
    }
}

