package ygz.cutiepics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import java.util.List;

/**
 * Created by yuyuxiao on 2018-03-21.
 */

public class Layout_Four extends LayoutTemplate {

    public Layout_Four() {
        this.picNum = 4;
    }

    private void getPics(List<String> patharr) {
        pics = new Bitmap[4];
        Bitmap a = getBitmap(patharr.get(0));
        Bitmap b = getBitmap(patharr.get(1));
        Bitmap c = getBitmap(patharr.get(2));
        Bitmap d = getBitmap(patharr.get(3));
        pics[0] = a;
        pics[1] = b;
        pics[2] = c;
        pics[3] = d;
    }

    public Bitmap combine(List<String> patharr) {

        getPics(patharr);
        int template = position + 1;
        String filename= "layout4_"+template+".json";
        init(filename);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // may need to be changed
        Bitmap bmp = Bitmap.createBitmap(this.viewWdh, this.viewHgt, conf);
        Canvas canvas = new Canvas(bmp);
        Draw(canvas);
        return bmp;
    }

}
