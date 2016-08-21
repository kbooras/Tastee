package com.kirstiebooras.foodvids;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.kirstiebooras.foodvids.youtube.GetPlaylistVideosAsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity which displays a list of videos which can be played
 */
public class MainActivity extends YouTubeBaseActivity {

    private static final String DEFAULT_PLAYLIST = "PLXBBwKNDaSnV772FI0JUOwcWnroAGE2Al";

    private List<Video> mVideos;
    private VideoAdapter mAdapter;
    private YouTube mYouTube;

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

        fetchVideosFromPlaylist(DEFAULT_PLAYLIST);
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
