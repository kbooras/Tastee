package com.kirstiebooras.foodvids;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.youtube.player.YouTubeThumbnailView;
import com.kirstiebooras.foodvids.config.Config;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    private YouTubeThumbnailView mYoutubeView;

    public VideoViewHolder(View view) {
        super(view);
        mYoutubeView = (YouTubeThumbnailView) view.findViewById(R.id.youtube_thumbnail);
    }

    public void initialize(YouTubeThumbnailView.OnInitializedListener listener) {
        mYoutubeView.initialize(Config.YOUTUBE_ANDROID_API_KEY, listener);
    }
}
