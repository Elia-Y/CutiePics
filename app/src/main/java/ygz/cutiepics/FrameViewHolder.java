package ygz.cutiepics;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;
import android.util.*;

/**
 * Created by yuyuxiao on 2018-02-02.
 */

public class FrameViewHolder extends RecyclerView.ViewHolder {
    public ImageView frames;

    public FrameViewHolder(View itemView) {
        super(itemView);
        frames = itemView.findViewById(R.id.frames);
    }

    public ImageView getFrame() {return frames;}
}
