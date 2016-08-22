package com.kirstiebooras.foodvids.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.android.youtube.player.YouTubeThumbnailView.OnInitializedListener;
import com.google.api.services.youtube.model.Video;
import com.kirstiebooras.foodvids.R;
import com.kirstiebooras.foodvids.util.OnVideoClickedListener;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private OnVideoClickedListener mOnVideoClickedListener;
    private List<Video> mVideoList;

    public VideoAdapter(OnVideoClickedListener listener, List<Video> videoList) {
        mOnVideoClickedListener = listener;
        mVideoList = videoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_row, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, int position) {
        OnInitializedListener onInitializedListener = new OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView,
                                                YouTubeThumbnailLoader youTubeThumbnailLoader) {
                final String videoId = mVideoList.get(holder.getAdapterPosition()).getId();
                youTubeThumbnailLoader.setVideo(videoId);

                youTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnVideoClickedListener.onVideoClicked(videoId);
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                // TODO
            }
        };

        holder.initialize(onInitializedListener);
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

}
