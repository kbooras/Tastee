package com.kirstiebooras.foodvids.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.kirstiebooras.foodvids.R;
import com.kirstiebooras.foodvids.adapter.VideoAdapter;
import com.kirstiebooras.foodvids.fragment.PlaybackFragment;
import com.kirstiebooras.foodvids.util.GetPlaylistVideosAsyncTask;
import com.kirstiebooras.foodvids.util.OnPlaybackEndedListener;
import com.kirstiebooras.foodvids.util.OnVideoClickedListener;
import com.kirstiebooras.foodvids.util.OnYouTubePlayerInitializedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity which displays a list of videos which can be played
 */
public class MainActivity extends AppCompatActivity
        implements OnYouTubePlayerInitializedListener, OnVideoClickedListener, OnPlaybackEndedListener {

    private static final String DEFAULT_PLAYLIST = "PLXBBwKNDaSnV772FI0JUOwcWnroAGE2Al";

    private List<Video> mVideos;
    private VideoAdapter mAdapter;
    private YouTube mYouTube;
    private PlaybackFragment mPlaybackFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);

        mVideos = new ArrayList<>();
        mAdapter = new VideoAdapter(this, mVideos);
        recyclerView.setAdapter(mAdapter);

        mYouTube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(),
                new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest hr) throws IOException {}
                })
                .setApplicationName(getResources().getString(R.string.app_name))
                .build();

        mPlaybackFragment = new PlaybackFragment();
        Bundle args = new Bundle();
        mPlaybackFragment.setArguments(args);

        fetchVideosFromPlaylist(DEFAULT_PLAYLIST);
    }

    /**
     * Shows the playback view for a video with the given ID
     * @param videoId the ID of the video to play
     */
    @Override
    public void onVideoClicked(String videoId) {
        mPlaybackFragment.getArguments().putString(PlaybackFragment.VIDEO_ID_ARG, videoId);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!mPlaybackFragment.isAdded()) {
            ft.add(R.id.container, mPlaybackFragment).commit();
        } else {
            ft.show(mPlaybackFragment).commit();
            mPlaybackFragment.playVideo();
        }
    }

    @Override
    public void onInitializeFailed() {
        onPlaybackExited();
        // TODO Show error message
    }

    /**
     * Hides the PlaybackFragment from the view and pauses playback
     */
    @Override
    public void onPlaybackExited() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(mPlaybackFragment).commit();
        mPlaybackFragment.pauseVideo();
    }

    /**
     * Fetches a list of videos from a YouTube playlist
     * @param playlistId the ID of the YouTube playlist to fetch from
     */
    private void fetchVideosFromPlaylist(String playlistId) {
        new GetPlaylistVideosAsyncTask(mYouTube) {
            @Override
            protected void onPostExecute(List<Video> videos) {
                mVideos.clear();
                mVideos.addAll(videos);
                mAdapter.notifyDataSetChanged();
            }
        }.execute(playlistId);
    }

}