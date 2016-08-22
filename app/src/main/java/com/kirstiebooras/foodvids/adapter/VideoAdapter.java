package com.kirstiebooras.foodvids.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.services.youtube.model.Video;
import com.kirstiebooras.foodvids.R;
import com.kirstiebooras.foodvids.util.OnVideoClickedListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private Context mContext;
    private OnVideoClickedListener mListener;
    private List<Video> mVideoList;

    public VideoAdapter(Context context, List<Video> videoList) {
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
        final String videoId = mVideoList.get(position).getId();
        String url = String.format(mContext.getResources().getString(R.string.thumbnailUrl), videoId);
        Picasso.with(mContext)
                .load(url)
                .into(holder.mImageView);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
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
