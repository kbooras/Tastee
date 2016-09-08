package com.kirstiebooras.foodvids.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

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
import java.util.Locale;
import java.util.Map;

import static com.kirstiebooras.foodvids.firebase.Constants.ENTREE_PLAYLIST_VIDEOS_REF;
import static com.kirstiebooras.foodvids.firebase.Constants.GLUTEN_FREE_FLAG;
import static com.kirstiebooras.foodvids.firebase.Constants.PLAYLIST_VIDEOS_REF;
import static com.kirstiebooras.foodvids.firebase.Constants.VEGAN_FLAG;
import static com.kirstiebooras.foodvids.firebase.Constants.VEGETARIAN_FLAG;

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
    private String mSelectedPlaylistId;

    private boolean mApplyGlutenFreeFilter = false;
    private boolean mApplyVeganFilter = false;
    private boolean mApplyVegetarianFilter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView appTitle = (TextView) toolbar.findViewById(R.id.app_title);
        Typeface pacifico = Typeface.createFromAsset(
                getAssets(),
                String.format(Locale.US, "fonts/%s", "Pacifico.ttf"));
        appTitle.setTypeface(pacifico);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_button);
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
        setupNavigationSwitches(view.getMenu());

        mVideos = new ArrayList<>();
        mVideoAdapter = new VideoAdapter(this, mVideos);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mVideoAdapter);

        mPlaylistVideosReference = FirebaseDatabase.getInstance().getReference(PLAYLIST_VIDEOS_REF);
        mSelectedPlaylistId = ENTREE_PLAYLIST_VIDEOS_REF;

        fetchPlaylistVideosIfConnected();

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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(mPlaybackFragment).commit();
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
     * Setup the filter switches' on-check listeners
     * @param menu the NavigationView menu holding the switches
     */
    private void setupNavigationSwitches(Menu menu) {
        MenuItem item = menu.findItem(R.id.gluten_free_switch);
        SwitchCompat gfSwitch =
                (SwitchCompat) MenuItemCompat.getActionView(item).findViewById(R.id.filter_switch);
        gfSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mApplyGlutenFreeFilter = isChecked;
                fetchPlaylistVideosIfConnected();
            }
        });

        item = menu.findItem(R.id.vegan_switch);
        SwitchCompat veganSwitch =
                (SwitchCompat) MenuItemCompat.getActionView(item).findViewById(R.id.filter_switch);
        veganSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mApplyVeganFilter = isChecked;
                fetchPlaylistVideosIfConnected();
            }
        });

        item = menu.findItem(R.id.vegetarian_switch);
        SwitchCompat vegetarianSwitch =
                (SwitchCompat) MenuItemCompat.getActionView(item).findViewById(R.id.filter_switch);
        vegetarianSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mApplyVegetarianFilter = isChecked;
                fetchPlaylistVideosIfConnected();
            }
        });
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
            mSelectedPlaylistId = playlistId;
            fetchPlaylistVideosIfConnected();
        }
    }

    /**
     * Fetches a list of videos from Firebase if we are connected to the network.
     * Otherwise, show the no connection dialog.
     */
    private void fetchPlaylistVideosIfConnected() {
        if (!hasConnection()) {
            showNoConnectionDialog();
            return;
        }

        DatabaseReference playlistVideosReference = mPlaylistVideosReference.child(mSelectedPlaylistId);
        playlistVideosReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<PlaylistVideo> videos = makeCollection(dataSnapshot.getChildren());
                mVideos.clear();
                mVideos.addAll(filterContent(videos));
                mVideoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Applys filters the user has selected to the list of videos
     * @param videos the list of videos to filter
     * @return the filtered list
     */
    private List<PlaylistVideo> filterContent(List<PlaylistVideo> videos) {
        if (!mApplyGlutenFreeFilter && !mApplyVeganFilter && !mApplyVegetarianFilter) {
            return videos;
        }

        List<PlaylistVideo> filteredVideos = new ArrayList<>(videos);
        for (PlaylistVideo video : videos) {
            Map<String, Boolean> flags = video.getFlags();
            if ((mApplyGlutenFreeFilter && !flags.get(GLUTEN_FREE_FLAG).equals(Boolean.TRUE))
                    || (mApplyVeganFilter && !flags.get(VEGAN_FLAG).equals(Boolean.TRUE))
                    || (mApplyVegetarianFilter && !flags.get(VEGETARIAN_FLAG).equals(Boolean.TRUE))) {
                filteredVideos.remove(video);
            }
        }
        return filteredVideos;
    }

    private boolean hasConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showNoConnectionDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_no_connection, null);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        Button button = (Button) dialogView.findViewById(R.id.okay_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                fetchPlaylistVideosIfConnected();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private static ArrayList<PlaylistVideo> makeCollection(Iterable<DataSnapshot> iter) {
        ArrayList<PlaylistVideo> list = new ArrayList<>();
        for (DataSnapshot item : iter) {
            list.add(item.getValue(PlaylistVideo.class));
        }
        return list;
    }

}