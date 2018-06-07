package ygz.cutiepics;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ygz.cutiepics.models.StickerObject;

/**
 * Created by Raina on 2018-02-01.
 */

public class StickerAdapter extends RecyclerView.Adapter<StickerViewHolder>{
    private static final String TAG = StickerAdapter.class.getSimpleName();
    private Context context;
    private List<StickerObject> productList;

    public StickerAdapter(Context context, List<StickerObject> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public StickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emoji_list, parent, false);
        return new StickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StickerViewHolder holder, int position) {
        StickerObject stickerObject = productList.get(position);
        int imageRes = getResourceId(context, stickerObject.getName(), "drawable", context.getPackageName());
        holder.emoji.setImageResource(imageRes);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static int getResourceId(Context context, String pVariableName, String pResourcename, String pPackageName) throws RuntimeException {
        try {
            return context.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            throw new RuntimeException("Error getting Resource ID.", e);
        }
    }
}
