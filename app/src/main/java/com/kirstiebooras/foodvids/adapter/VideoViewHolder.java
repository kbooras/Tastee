package com.kirstiebooras.foodvids.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kirstiebooras.foodvids.R;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    private FrameLayout mContainer;
    private ImageView mImageView;
    private ImageView mGradient;
    private TextView mVideoName;

    public VideoViewHolder(View view) {
        super(view);
        mContainer = (FrameLayout) view.findViewById(R.id.container);
        mImageView = (ImageView) view.findViewById(R.id.youtube_thumbnail);
        mGradient = (ImageView) view.findViewById(R.id.gradient);
        mVideoName = (TextView) view.findViewById(R.id.video_name);
    }

    public FrameLayout getContainer() {
        return mContainer;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setVisible() {
        mGradient.setVisibility(View.VISIBLE);
        mVideoName.setVisibility(View.VISIBLE);
    }

    public void setVideoName(String videoName) {
        mVideoName.setText(videoName);
    }
}
