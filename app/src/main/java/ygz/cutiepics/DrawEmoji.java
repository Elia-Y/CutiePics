package ygz.cutiepics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import ygz.cutiepics.models.PhotoModel;

/**
 * Created by zhaofan on 2018-03-24.
 */


//cutome DrawEmoji
//Unused Libiary


class DrawEmoji extends View {
    private Bitmap background;  // Store the latest canvas Bitmap
    private Bmp tempBitmap = null;
    //private Canvas canvas = new Canvas(canvasBitmap);
    private Canvas canvas;
    private float X = 0f;
    private float Y = 0f;
    private Bmp pic;

    Bmp bmp;
    Bitmap origional_bg; // the original photo
    float DownX = 0f;
    float DownY = 0f;
    int background_width = 900;
    int background_height = 900;

    //    	construct
    public DrawEmoji(Context context) {
        super(context);
        this.background = Bitmap.createBitmap(background_width, background_height, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(background);
        bmp = new Bmp(background, 0f,  0f);
        //this.canvas = new Canvas(background);
        this.pic = bmp;
        origional_bg = background.copy(background.getConfig(), true);
        /*
        tempBitmap = pic;
        this.canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.getXY(1) - tempBitmap.getPic().getWidth() / 2,
                tempBitmap.getXY(2) - tempBitmap.getPic().getHeight() / 2, null);
                */

    }

    //    	construct
    public DrawEmoji(Context context, Bmp pic, Bitmap background) {
        this(context);
        this.pic = pic;
        int w = Resources.getSystem().getDisplayMetrics().widthPixels;
        int h = Resources.getSystem().getDisplayMetrics().heightPixels;
        this.background = background;
        //this.background = Bitmap.createBitmap(background_width, background_height, Bitmap.Config.ARGB_8888);
        origional_bg = this.background.copy(this.background.getConfig(), true);

        //this.background_width = background.getWidth();
        //this.background_height = background.getHeight();
        this.background_width = 320;
        this.background_height = 450;
        this.background = Bitmap.createBitmap(background_width, background_height, Bitmap.Config.ARGB_8888);
        origional_bg = background.copy(background.getConfig(), true);
        this.canvas = new Canvas(this.background);
        this.canvas.drawBitmap(background, null,
                new RectF((w-this.background.getWidth())/2, 0,
                        (w-this.background.getWidth())/2 + this.background.getWidth(),
                        this.background.getHeight()), null);

/*
        this.pic = bmp;
        tempBitmap = pic;
        this.canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.getXY(1) - tempBitmap.getPic().getWidth() / 2,
                tempBitmap.getXY(2) - tempBitmap.getPic().getHeight() / 2, null);
*/
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawBitmap(background, 0, 0, null);
        tempBitmap = pic;

        this.canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.getXY(1) - tempBitmap.getPic().getWidth() / 2,
                tempBitmap.getXY(2) - tempBitmap.getPic().getHeight() / 2, null);
        this.canvas.drawBitmap(origional_bg, 0,0 , null );
    }

    //    	OntouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.DownX = event.getX();
            this.DownY = event.getY();
            /*
            tempBitmap = pic;
            this.canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.getXY(1) - tempBitmap.getPic().getWidth() / 2,
                    tempBitmap.getXY(2) - tempBitmap.getPic().getWidth() / 2, null);
                    */

        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            this.X = event.getX();
            this.Y = event.getY();

            tempBitmap = pic;
            /*
            this.canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.getXY(1) - tempBitmap.getPic().getWidth() / 2,
                    tempBitmap.getXY(2) - tempBitmap.getPic().getWidth() / 2, null);
            Log.d("Debug", "image is changed in case 3");
            */
            if ((Math.abs(pic.getXY(1) - this.X) < pic.getPic().getWidth() / 2)
                    && (Math.abs(pic.getXY(2) - this.Y) < pic.getPic().getHeight() / 2)) {

                //this.canvas.drawBitmap(origional_bg, 0,0 , null );
                //this.canvas = new Canvas(origional_bg);
                this.canvas.drawBitmap(tempBitmap.getPic(), X - tempBitmap.getPic().getWidth() / 2,
                        Y - tempBitmap.getPic().getWidth() / 2, null);
                pic.preX = X;
                pic.preY = Y;
                //this.canvas.drawBitmap(origional_bg, 0,0 , null );

            } else {
                this.canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.getXY(1) - tempBitmap.getPic().getWidth() / 2,
                        tempBitmap.getXY(2) - tempBitmap.getPic().getWidth() / 2, null);
                //this.canvas.drawBitmap(origional_bg, 0,0 , null );
            }


        }
        Log.d("Debug", "invalidate is called");
        invalidate();
        Log.d("Debug", "invalidate is done");
        return true;
    }
}