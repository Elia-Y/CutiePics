package ygz.cutiepics;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ygz.cutiepics.models.FrameObject;
import ygz.cutiepics.models.PhotoModel;

/**
 * Created by Raina on 2018-02-27.
 */

public class LayoutActivity extends Activity {
    private ImageView img;
    //private int picNum = 0;
    private PopupWindow pw;
    private List<String> patharr = new ArrayList<>();

    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
        img = (ImageView) findViewById(R.id.ivImage);

        patharr = Arrays.asList(PhotoModel.getPhotos());
        //picNum = patharr.size();
    }

    private Bitmap useLayout(int position) {
        LayoutTemplate layout = new LayoutTemplate();
        layout.position = position;
        return layout.match_temp(this.patharr, getBaseContext());  // should we use base context here?
    }

    private ArrayList<FrameObject> getFrameTestData2() {
        ArrayList<FrameObject> featuredFrame = new ArrayList<FrameObject>();
        featuredFrame.add(new FrameObject("layout2_1"));
        featuredFrame.add(new FrameObject("layout2_2"));
        featuredFrame.add(new FrameObject("layout2_3"));

        return featuredFrame;
    }

    private ArrayList<FrameObject> getFrameTestData3() {
        ArrayList<FrameObject> featuredFrame = new ArrayList<FrameObject>();
        featuredFrame.add(new FrameObject("layout3_1"));
        featuredFrame.add(new FrameObject("layout3_2"));
        featuredFrame.add(new FrameObject("layout3_3"));
        featuredFrame.add(new FrameObject("layout3_4"));
        featuredFrame.add(new FrameObject("layout3_5"));
        featuredFrame.add(new FrameObject("layout3_6"));
        return featuredFrame;
    }

    private ArrayList<FrameObject> getFrameTestData4() {
        ArrayList<FrameObject> featuredFrame = new ArrayList<FrameObject>();
        featuredFrame.add(new FrameObject("layout4_1"));
        featuredFrame.add(new FrameObject("layout4_2"));

        return featuredFrame;
    }

    public void chooseFrame(View view) {
        showPopupWindow();
    }

    private void showPopupWindow() {
        View view = LayoutInflater.from(LayoutActivity.this).inflate(R.layout.sticker_popup, null);

        pw = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, 240);
        pw.setFocusable(true);
        pw.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        pw.setOutsideTouchable(true);
        pw.setAnimationStyle(R.style.Animation);
        pw.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        final RecyclerView rv = (RecyclerView) view.findViewById(R.id.pop_sticker);
        GridLayoutManager mGrid = new GridLayoutManager(this, 4);
        rv.setLayoutManager(mGrid);
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(24);
        rv.setDrawingCacheEnabled(true);
        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rv.setNestedScrollingEnabled(false);

        FrameAdapter mAdapter;
        if (patharr.size() == 2) {
            mAdapter = new FrameAdapter(LayoutActivity.this, getFrameTestData2());
        } else if (patharr.size() == 3) {
            mAdapter = new FrameAdapter(LayoutActivity.this, getFrameTestData3());
        } else {
            mAdapter = new FrameAdapter(LayoutActivity.this, getFrameTestData4());
        }
        rv.setAdapter(mAdapter);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(this, rv, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Bitmap combined = useLayout(position);

                        if (combined == null) {
                            return;
                        }

                        img.setImageDrawable(new BitmapDrawable(getResources(), combined));

                        BitmapDrawable frame_origin = (BitmapDrawable) img.getDrawable();
                        Bitmap saved_bitmap = frame_origin.getBitmap();
                        PhotoModel.setmPhoto(saved_bitmap);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {}
                })
        );
    }

    public void saveImg(View view) {
        Intent intent = new Intent(LayoutActivity.this, SavePhotoActivity.class);
        startActivity(intent);
    }
}
