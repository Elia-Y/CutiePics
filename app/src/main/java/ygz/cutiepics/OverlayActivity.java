package ygz.cutiepics;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ygz.cutiepics.models.PhotoModel;

/**
 * Created by Raina on 2018-03-18.
 */

public class OverlayActivity extends Activity {
    private SeekBar seekBar;

    private ImageView img;
    private Bitmap img1;
    private Bitmap img2;

    private int prog;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_overlay);

        img = (ImageView) findViewById(R.id.overlayImg);

        List<String> patharr = Arrays.asList(PhotoModel.getPhotos());

        // Get the bitmaps
        img1 = getBitmap(patharr.get(0));
        img2 = getBitmap(patharr.get(1));

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        prog = 50;
        overlay();

        // Set the photo model at the very beginning
        Drawable saved_drawable = img.getDrawable();
        int width = saved_drawable.getIntrinsicWidth();
        int height = saved_drawable.getIntrinsicHeight();

        Bitmap saved_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        saved_drawable.draw(new Canvas(saved_bitmap));

        PhotoModel.setmPhoto(saved_bitmap);
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
            prog = progress;
            overlay();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // when the user first touches the seekbar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // when the user last touches the seekbar
            Toast.makeText(OverlayActivity.this, "Exposure " + Integer.toString(prog), Toast.LENGTH_SHORT).show();

            Drawable saved_drawable = img.getDrawable();
            int width = saved_drawable.getIntrinsicWidth();
            int height = saved_drawable.getIntrinsicHeight();

            Bitmap saved_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            saved_drawable.draw(new Canvas(saved_bitmap));

            PhotoModel.setmPhoto(saved_bitmap);
        }
    };

    RadioGroup.OnCheckedChangeListener radGroupListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton radBut = (RadioButton) findViewById(checkedId);
            Toast.makeText(OverlayActivity.this, "You selected " + radBut.getText(), Toast.LENGTH_SHORT);
        }
    };

    public void saveImg(View view) {
        Intent intent = new Intent(OverlayActivity.this, SavePhotoActivity.class);
        startActivity(intent);
    }

    /*
    another possible way to realize double exposure
    private void overlay() {
        Bitmap bottom_bm = img1;
        Bitmap top_bm = img2;
        Bitmap newBitmap = Bitmap.createBitmap(bottom_bm.getWidth(), bottom_bm.getHeight(), bottom_bm.getConfig());
        Canvas canvas = new Canvas(newBitmap);
        Paint vPaint = new Paint();
        vPaint .setStyle( Paint.Style.STROKE );   //空心
        vPaint .setAlpha( 100-prog );   //
        canvas.drawBitmap (top_bm , 50, 200, vPaint );  //有透明


        img.setImageDrawable(new BitmapDrawable(getResources(), newBitmap));
    }
    */

    private void overlay() {
        Bitmap bottom_bm = img1;
        Bitmap top_bm = img2;
        Drawable[] array = new Drawable[2];

        // TODO: resize one img if the size is different. How to do it is the best way?

        //Bitmap update = adjustOpacity(top_bm, 100-prog);
        Bitmap update = getTransparentBitmap(top_bm,100-prog);

        array[0] = new BitmapDrawable(getResources(), bottom_bm);
        array[1] = new BitmapDrawable(getResources(), update);
        LayerDrawable layer = new LayerDrawable(array);
        img.setImageDrawable(layer);
    }

    public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

                .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

        number = number * 255 / 100;

        for (int i = 0; i < argb.length; i++) {

            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);

        }

        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg

                .getHeight(), Bitmap.Config.ARGB_8888);

        return sourceImg;
    }

    // This is kind of more like adjusting the brightness
    private Bitmap adjustOpacity(Bitmap bitmap, int opacity)
    {
        Bitmap mutableBitmap = bitmap.isMutable()
                ? bitmap
                : bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        int colour = (opacity & 0xFF) << 24;
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN);
        return mutableBitmap;
    }

    // This is repeated code from LayoutTemplate
    public Bitmap getBitmap(String path) {
        try {
            Bitmap bitmap=null;
            File f= new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 3;   // Bitmap we get is compressed

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
     This is repeated code from FrameActivity
        it may not be used in this function
     */
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

        // if you want to rotate the Bitmap
        // matrix.postRotate(45);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;

    }
}
