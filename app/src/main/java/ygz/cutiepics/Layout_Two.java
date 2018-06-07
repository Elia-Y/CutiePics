package ygz.cutiepics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import java.util.List;

/**
 * Created by yuyuxiao on 2018-03-20.
 */

public class Layout_Two extends LayoutTemplate {
    public Layout_Two() {
        this.picNum = 2;
    }

    private void getPics(List<String> patharr) {
        pics = new Bitmap[2];
        Bitmap c = getBitmap(patharr.get(0));
        Bitmap s = getBitmap(patharr.get(1));
        pics[0] = c;
        pics[1] = s;
    }

    public Bitmap combine(List<String> patharr) {

        getPics(patharr);
        int template = position + 1;
        String filename= "layout2_"+template+".json";
        init(filename);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // may need to be changed
        //Bitmap bmp = Bitmap.createBitmap(320, 450, conf);
        Bitmap bmp = Bitmap.createBitmap(this.viewWdh, this.viewHgt, conf);
        Canvas canvas = new Canvas(bmp);
        Draw(canvas);
        return bmp;
    }
}
