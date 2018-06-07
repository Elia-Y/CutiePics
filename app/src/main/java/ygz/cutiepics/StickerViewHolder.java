package ygz.cutiepics;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Raina on 2018-02-01.
 */

public class StickerViewHolder extends RecyclerView.ViewHolder {
    public  ImageView emoji;
    private boolean added;

    public StickerViewHolder(View itemView) {
        super(itemView);
        emoji = (ImageView) itemView.findViewById(R.id.emoji);
    }

    public boolean checkAdded() {
        return this.added;
    }

    public void setAdded(boolean update) {
        this.added = update;
    }

    public  ImageView getEmoji() {return emoji;}
}