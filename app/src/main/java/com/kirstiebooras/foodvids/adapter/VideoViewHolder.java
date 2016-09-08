package com.kirstiebooras.foodvids.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kirstiebooras.foodvids.R;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    private FrameLayout mContainer;
    private ImageView mImageView;
    public ImageView mVideoCameraIcon;
    private TextView mVideoName;

    public VideoViewHolder(View view) {
        super(view);
        mContainer = (FrameLayout) view.findViewById(R.id.container);
        mImageView = (ImageView) view.findViewById(R.id.youtube_thumbnail);
        mVideoName = (TextView) view.findViewById(R.id.video_name);
        mVideoCameraIcon = (ImageView) view.findViewById(R.id.videocamera_icon);
    }

    public FrameLayout getContainer() {
        return mContainer;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setVideoName(String videoName) {
        mVideoName.setText(videoName);
    }

    public void beginAnimation() {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        mVideoCameraIcon.startAnimation(animation);
    }
}
