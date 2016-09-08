package com.kirstiebooras.foodvids.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.kirstiebooras.foodvids.R;
import com.kirstiebooras.foodvids.firebase.PlaylistVideo;
import com.kirstiebooras.foodvids.util.OnVideoClickedListener;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private Context mContext;
    private OnVideoClickedListener mListener;
    private List<PlaylistVideo> mVideoList;

    public VideoAdapter(Context context, List<PlaylistVideo> videoList) {
        mContext = context;
        mListener = (OnVideoClickedListener) context;
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
        holder.setVideoName(mVideoList.get(position).getName());
        holder.beginAnimation();

        Glide.with(mContext)
                .load(mVideoList.get(position).getThumbnail())
                .into(holder.getImageView());

        final String videoId = mVideoList.get(position).getVideoId();
        holder.getContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onVideoClicked(videoId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

}
