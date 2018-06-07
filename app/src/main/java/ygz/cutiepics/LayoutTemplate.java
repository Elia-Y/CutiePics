package ygz.cutiepics;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import ygz.cutiepics.models.Coordinates;

/**
 * Created by yuyuxiao on 2018-03-21.
 */

public class LayoutTemplate {
    public int position;
    public Context context;
    public Bitmap[] pics;
    public int picNum;

    public Path[] path;
    public Bitmap[] bitmaps;
    //public boolean[] bitmapsFlag;

    public float[][] pathLT;
    public float[][] pathOffset;

    public int viewWdh, viewHgt;
    public List< List<Coordinates> > coordinateSetList;

    public LayoutTemplate() {}

    public void init(String fileName) {

        String data = readAsset(fileName);
        coordinateSetList = new ArrayList<>();
        List<Coordinates> temp;

        try {
            JSONObject jsonObj = new JSONObject(data);
            viewWdh = jsonObj.getInt("width");
            viewHgt = jsonObj.getInt("height");
            JSONArray pic = jsonObj.getJSONArray("style");
            JSONObject temp1 = pic.getJSONObject(0);
            JSONArray style = temp1.getJSONArray("pic");

            Log.d("Debug", "Number of coordinate sets is "+style.length());
            for (int i = 0; i < style.length(); ++i) {

                temp = new ArrayList<>();
                JSONObject rec = style.getJSONObject(i);
                JSONArray coord = rec.getJSONArray("coordinates");

                for (int j = 0; j < coord.length(); ++j) {    // always four points?

                    JSONObject current = coord.getJSONObject(j);
                    float x = (float) current.getInt("x");
                    float y = (float) current.getInt("y");
                    Coordinates c = new Coordinates(x, y);
                    temp.add(c);
                }

                coordinateSetList.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (coordinateSetList.size() != picNum) {
            Log.d("Error", "Wrong number of coordinates set of layout template");
        } else {
            Log.d("Debug", "Correct number of coordinates set of layout template");
        }

        // this is temp use, should improve after adding more layout
        //bitmapsFlag = new boolean[1];
        //bitmapsFlag[0] = true;

        path = new Path[picNum];
        for (int i = 0; i < picNum; ++i) {
            path[i] = new Path();
        }

        pathLT = new float[picNum][2];
        pathOffset = new float[picNum][2];
        for (int i = 0; i < picNum; i++) {
            //bitmapsFlag[i] = false;
            pathLT[i][0] = 0f;
            pathLT[i][1] = 0f;
            pathOffset[i][0] = 0f;
            pathOffset[i][1] = 0f;
        }

        // Understand what this parts does
        for (int i = 0; i < picNum; i++) {
            for (int j = 0; j < coordinateSetList.get(i).size(); j++) {
                float x = coordinateSetList.get(i).get(j).getX();
                float y = coordinateSetList.get(i).get(j).getY();
                if (j == 0) {
                    //path[i].moveTo(dp2px(x), dp2px(y));
                    path[i].moveTo(x, y);
                } else {
                    //path[i].lineTo(dp2px(x), dp2px(y));
                    path[i].lineTo(x, y);
                }
            }
            path[i].close();
        }

        // Init all bitmaps
        bitmaps = new Bitmap[picNum];
        for (int i = 0; i < picNum; ++i) {
            Coordinates coordinate = caculateViewSize(coordinateSetList.get(i));
            bitmaps[i] = scaleImage(pics[i], (int) coordinate.getX(), (int) coordinate.getY());
            if (bitmaps[i] == null) {
                Log.d("Error", "Bitmap is not scaled correctly");
            }
            Log.d("Debug", "Scaled x is "+coordinate.getX()+" and y is "+coordinate.getY());
        }

        Log.d("Debug", "Size of bitmaps is "+bitmaps.length);

    }

    public Bitmap getBitmap(String path) {
        try {
            Bitmap bitmap=null;
            File f= new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inPreferredConfig = Bitmap.Config.RGB_565;
            //options.inSampleSize = 3;   // Bitmap we get is compressed

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap match_temp(List<String> patharr, Context c) {
        context = c;
        int pic = patharr.size();
        LayoutTemplate real;
        if (pic == 2) {
            real = new Layout_Two();
        } else if (pic == 3) {
            real = new Layout_Three();
        } else if (pic == 4) {
            real = new Layout_Four();
        } else {
            Log.d("Error", "Can't match template");
            return null;
        }

        real.position = this.position;
        real.context = this.context;
        return real.combine(patharr);
        //return null;
    }


    // this is a virtual method
    public Bitmap combine(List<String> patharr) {
        return null;
    }



    /*
        Some helper function
    */


    // Read and get information of the json file
    public String readAsset(String fileName) {
        AssetManager am = context.getAssets();

        String data = "";

        InputStream is = null;
        try {
            is = am.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        data = readDataFromInputStream(is);
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    private String readDataFromInputStream(InputStream is) {
        BufferedInputStream bis = new BufferedInputStream(is);

        String str = "", s = "";

        int c = 0;
        byte[] buf = new byte[1024];
        while (true) {
            try {
                c = bis.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (c == -1)
                break;
            else {
                try {
                    s = new String(buf, 0, c, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                str += s;
            }
        }

        try {
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;
    }

    public Coordinates caculateViewSize(List<Coordinates> list) {

        float viewWidth;
        float viewHeight;

        viewWidth = caculateMaxCoordinateX(list) - caculateMinCoordinateX(list);
        viewHeight = caculateMaxCoordinateY(list) - caculateMinCoordinateY(list);

        return new Coordinates(viewWidth, viewHeight);
    }


    private int caculateSampleSize(int picWdh, int picHgt, int showWdh,
                                   int showHgt) {
        // 如果此时显示区域比图片大，直接返回
        if ((showWdh < picWdh) && (showHgt < picHgt)) {
            int wdhSample = picWdh / showWdh;
            int hgtSample = picHgt / showHgt;
            // 利用小的来处理
            int sample = wdhSample > hgtSample ? hgtSample : wdhSample;
            int minSample = 2;
            while (sample > minSample) {
                minSample *= 2;
            }
            return minSample >> 1;
        } else {
            return 0;
        }
    }

    private float caculateMinCoordinateX(List<Coordinates> list) {

        float minX;
        minX = list.get(0).getX();
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getX() < minX) {
                minX = list.get(i).getX();
            }
        }
        return minX;
    }

    private float caculateMaxCoordinateX(List<Coordinates> list) {

        float maxX;
        maxX = list.get(0).getX();
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getX() > maxX) {
                maxX = list.get(i).getX();
            }
        }
        return maxX;
    }

    private float caculateMinCoordinateY(List<Coordinates> list) {

        float minY;
        minY = list.get(0).getY();
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getY() < minY) {
                minY = list.get(i).getY();
            }
        }
        return minY;
    }

    private float caculateMaxCoordinateY(List<Coordinates> list) {

        float maxY;
        maxY = list.get(0).getY();
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getY() > maxY) {
                maxY = list.get(i).getY();
            }
        }
        return maxY;
    }

    // This function may need to be improved

    private Bitmap scaleImage1(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null) {
            return null;
        }

        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        float scale = 1;

        if (scaleWidth >= scaleHeight) {
            scale = scaleWidth;
        } else {
            scale = scaleHeight;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


    private Bitmap scaleImage(Bitmap bm, int w, int h)
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

    /**
     * Scales the provided bitmap to have the height and width provided.
     * (Alternative method for scaling bitmaps
     * since Bitmap.createScaledBitmap(...) produces bad (blocky) quality bitmaps.)
     *
     * @param bitmap is the bitmap to scale.
     * @param newWidth is the desired width of the scaled bitmap.
     * @param newHeight is the desired height of the scaled bitmap.
     * @return the scaled bitmap.
     */

    public Bitmap scaleImage3(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }


    protected void Draw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        /*
        Paint paint= new Paint(Paint.FILTER_BITMAP_FLAG |
                Paint.DITHER_FLAG |
                Paint.ANTI_ALIAS_FLAG);
        */
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        startDraw(canvas, paint);
    }


    private void startDraw(Canvas canvas, Paint paint) {
        for (int i = 0; i < picNum; i++) {
            canvas.save();
            drawScene(canvas, paint, i);
            canvas.restore();
        }
    }

    private void drawScene(Canvas canvas, Paint paint, int idx) {
        canvas.clipPath(path[idx]);
        canvas.drawColor(Color.GRAY);

        if (bitmaps == null) {
            Log.d("Error", "bitmaps has size "+bitmaps.length);
            return;
        }

        canvas.drawBitmap(bitmaps[idx], caculateMinCoordinateX(coordinateSetList.get(idx)),
                    caculateMinCoordinateY(coordinateSetList.get(idx)), paint);
    }

}
