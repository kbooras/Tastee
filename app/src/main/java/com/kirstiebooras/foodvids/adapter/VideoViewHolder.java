package com.kirstiebooras.foodvids.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.kirstiebooras.foodvids.R;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    public ImageView mImageView;

    public VideoViewHolder(View view) {
        super(view);
        mImageView = (ImageView) view.findViewById(R.id.youtube_thumbnail);
    }

}
