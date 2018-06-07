package ygz.cutiepics.models;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Raina on 2018-03-25.
 */

public class CustomEdittext extends AppCompatEditText {

    Paint paint;

    public CustomEdittext(Context context){
        super(context);
        init();
    }

    public CustomEdittext(Context context, AttributeSet attr){
        super(context, attr);
        init();
    }


    public void init(){
        paint = new Paint();

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(Color.DKGRAY);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }
}
