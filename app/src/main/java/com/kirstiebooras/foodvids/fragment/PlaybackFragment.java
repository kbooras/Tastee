package com.kirstiebooras.foodvids.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.kirstiebooras.foodvids.R;
import com.kirstiebooras.foodvids.config.Config;
import com.kirstiebooras.foodvids.util.OnPlaybackEndedListener;
import com.kirstiebooras.foodvids.util.OnYouTubePlayerInitializedListener;

/**
 * Fragment which plays a video
 */
public class PlaybackFragment extends Fragment
        implements YouTubePlayer.OnInitializedListener {

    public static final String VIDEO_ID_ARG = "video-id";
    private OnYouTubePlayerInitializedListener mOnInitializeFailedListener;
    private OnPlaybackEndedListener mOnPlaybackEndedListener;
    private YouTubePlayer mYouTubePlayer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInitializeFailedListener = (OnYouTubePlayerInitializedListener) context;
            mOnPlaybackEndedListener = (OnPlaybackEndedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Context does not implement OnInitializeFailedListener or OnPlaybackEndedListener.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View layout = layoutInflater.inflate(R.layout.fragment_playback, viewGroup, false);
        FrameLayout background = (FrameLayout) layout.findViewById(R.id.background);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPlaybackEndedListener.onPlaybackExited();
            }
        });

        YouTubePlayerSupportFragment fragment = new YouTubePlayerSupportFragment();
        fragment.initialize(Config.YOUTUBE_ANDROID_API_KEY, this);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, fragment).commit();

        return layout;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer,
                                        boolean wasRestored) {
        mYouTubePlayer = youTubePlayer;
        mYouTubePlayer.setShowFullscreenButton(false);
        if (!wasRestored) {
            playVideo();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
        if (result.isUserRecoverableError()) {
            result.getErrorDialog(getActivity(), 1).show();
        }
        mOnInitializeFailedListener.onInitializeFailed();
    }

    public void playVideo() {
        if (mYouTubePlayer != null) {
            mYouTubePlayer.loadVideo(getArguments().getString(VIDEO_ID_ARG));
            mYouTubePlayer.play();
        }
    }

    public void pauseVideo() {
        if (mYouTubePlayer != null) {
            mYouTubePlayer.pause();
        }
    }
}
