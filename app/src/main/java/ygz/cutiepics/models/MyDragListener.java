package ygz.cutiepics.models;

import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Raina on 2018-03-25.
 */

public class MyDragListener implements View.OnDragListener {

    private RelativeLayout.LayoutParams params;

    @Override
    public boolean onDrag(View v, DragEvent event)
    {
        View view = (View) event.getLocalState();


        switch(event.getAction())
        {
            case DragEvent.ACTION_DRAG_STARTED:

                params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                int x = (int) event.getX();
                int y = (int) event.getY();

                break;

            case DragEvent.ACTION_DRAG_EXITED :

                break;

            case DragEvent.ACTION_DRAG_LOCATION  :
                x=  (int) event.getX();
                y =  (int) event.getY();
                break;

            case DragEvent.ACTION_DRAG_ENDED   :

                break;

            case DragEvent.ACTION_DROP:

                x = (int) event.getX();
                y = (int) event.getY();
                //params.leftMargin = x - 100;
                //params.topMargin = y - 30;
                params.leftMargin = x-50;
                params.topMargin = y-30;

                view.setLayoutParams(params);
                view.setVisibility(View.VISIBLE);

                break;
            default: break;
        }
        return true;
    }


}
