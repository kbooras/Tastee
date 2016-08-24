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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kirstiebooras.foodvids.R;
import com.kirstiebooras.foodvids.adapter.VideoAdapter;
import com.kirstiebooras.foodvids.firebase.Constants;
import com.kirstiebooras.foodvids.firebase.PlaylistVideo;
import com.kirstiebooras.foodvids.fragment.PlaybackFragment;
import com.kirstiebooras.foodvids.util.OnPlaybackEndedListener;
import com.kirstiebooras.foodvids.util.OnVideoClickedListener;
import com.kirstiebooras.foodvids.util.OnYouTubePlayerInitializedListener;

import java.util.ArrayList;
import java.util.List;

import static com.kirstiebooras.foodvids.firebase.Constants.ENTREE_PLAYLIST_VIDEOS_REF;
import static com.kirstiebooras.foodvids.firebase.Constants.PLAYLIST_VIDEOS_REF;

/**
 * Activity which displays a list of videos which can be played
 */
public class MainActivity extends AppCompatActivity
        implements OnYouTubePlayerInitializedListener, OnVideoClickedListener, OnPlaybackEndedListener {

    private DrawerLayout mDrawerLayout;
    private List<PlaylistVideo> mVideos;
    private VideoAdapter mVideoAdapter;
    private DatabaseReference mPlaylistVideosReference;
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

        mPlaylistVideosReference = FirebaseDatabase.getInstance().getReference(PLAYLIST_VIDEOS_REF);
        fetchPlaylistVideos(ENTREE_PLAYLIST_VIDEOS_REF);

        Bundle args = new Bundle();
        mPlaybackFragment = new PlaybackFragment();
        mPlaybackFragment.setArguments(args);
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
                playlistId = Constants.BREAKFAST_PLAYLIST_VIDEOS_REF;
                break;
            case R.id.salads:
                playlistId = Constants.SALAD_PLAYLIST_VIDEOS_REF;
                break;
            case R.id.entrees:
                playlistId = Constants.ENTREE_PLAYLIST_VIDEOS_REF;
                break;
            case R.id.desserts:
                playlistId = Constants.DESSERTS_PLAYLIST_VIDEOS_REF;
                break;
        }
        if (playlistId != null) {
            fetchPlaylistVideos(playlistId);
        }
    }

    /**
     * Fetches a list of videos from Firebase
     * @param playlistId the ID of the Firebase playlist to fetch from
     */
    private void fetchPlaylistVideos(String playlistId) {
        DatabaseReference playlistVideosReference = mPlaylistVideosReference.child(playlistId);
        playlistVideosReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<PlaylistVideo> videos = makeCollection(dataSnapshot.getChildren());
                mVideos.clear();
                mVideos.addAll(videos);
                mVideoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private static ArrayList<PlaylistVideo> makeCollection(Iterable<DataSnapshot> iter) {
        ArrayList<PlaylistVideo> list = new ArrayList<>();
        for (DataSnapshot item : iter) {
            list.add(item.getValue(PlaylistVideo.class));
        }
        return list;
    }

}