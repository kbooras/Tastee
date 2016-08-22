package com.kirstiebooras.foodvids.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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

    private DrawerLayout mDrawerLayout;
    private List<Video> mVideos;
    private VideoAdapter mVideoAdapter;
    private YouTube mYouTube;
    private PlaybackFragment mPlaybackFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                loadView(menuItem.getItemId());
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mVideos = new ArrayList<>();
        mVideoAdapter = new VideoAdapter(this, mVideos);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mVideoAdapter);

        mYouTube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(),
                new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest hr) throws IOException {}
                })
                .setApplicationName(getResources().getString(R.string.app_name))
                .build();

        Bundle args = new Bundle();
        mPlaybackFragment = new PlaybackFragment();
        mPlaybackFragment.setArguments(args);

        fetchVideosFromPlaylist(getResources().getString(R.string.entreePlaylist));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Loads a MenuItem's corresponding view
     * @param id the MenuItem's id
     */
    private void loadView(int id) {
        String playlistId = null;
        switch (id) {
            case R.id.breakfast:
                playlistId = getResources().getString(R.string.breakfastPlaylist);
                break;
            case R.id.salads:
                playlistId = getResources().getString(R.string.saladPlaylist);
                break;
            case R.id.entrees:
                playlistId = getResources().getString(R.string.entreePlaylist);
                break;
            case R.id.desserts:
                playlistId = getResources().getString(R.string.dessertPlaylist);
                break;
        }
        if (playlistId != null) {
            fetchVideosFromPlaylist(playlistId);
        }
    }

    /**
     * Fetches a list of videos from a YouTube playlist
     * @param playlistId the ID of the YouTube playlist to fetch from
     */
    private void fetchVideosFromPlaylist(String playlistId) {
        new GetPlaylistVideosAsyncTask(mYouTube) {
            @Override
            protected void onPostExecute(List<Video> videos) {
                super.onPostExecute(videos);
                mVideos.clear();
                mVideos.addAll(videos);
                mVideoAdapter.notifyDataSetChanged();
            }
        }.execute(playlistId);
    }

}