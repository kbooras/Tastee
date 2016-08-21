package com.kirstiebooras.foodvids.util;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.kirstiebooras.foodvids.config.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask which fetches a list of videos from a YouTube playlist
 */
public class GetPlaylistVideosAsyncTask extends AsyncTask<String, Void, List<Video>> {

    private static final String TAG = GetPlaylistVideosAsyncTask.class.getSimpleName();

    private static final String YOUTUBE_PLAYLIST_PART = "snippet,contentDetails";
    private static final String YOUTUBE_PLAYLIST_FIELDS = "pageInfo,items(snippet(resourceId/videoId), contentDetails(note))";
    private static final String YOUTUBE_VIDEOS_PART = "snippet,contentDetails";
    private static final String YOUTUBE_VIDEOS_FIELDS = "items(id,snippet(title,description,thumbnails/high),contentDetails/duration)";
    private static final Long YOUTUBE_PLAYLIST_MAX_RESULTS = 10L;

    private YouTube mYouTube;

    public GetPlaylistVideosAsyncTask(YouTube youTube) {
        mYouTube = youTube;
    }

    @Override
    protected List<Video> doInBackground(String... params) {
        final String playlistId = params[0];

        PlaylistItemListResponse playlistItemListResponse;
        try {
            playlistItemListResponse = mYouTube.playlistItems()
                    .list(YOUTUBE_PLAYLIST_PART)
                    .setPlaylistId(playlistId)
                    .setFields(YOUTUBE_PLAYLIST_FIELDS)
                    .setMaxResults(YOUTUBE_PLAYLIST_MAX_RESULTS)
                    .setKey(Config.YOUTUBE_BROWSER_API_KEY)
                    .execute();
        } catch (IOException e) {
            Log.e(TAG, "Error getting a PlaylistItemListResponse from YouTube: " + e);
            return null;
        }

        if (playlistItemListResponse == null) {
            Log.e(TAG, "Failed to get playlist");
            return null;
        }

        List<String> videoIds = new ArrayList<>();
        for (PlaylistItem item : playlistItemListResponse.getItems()) {
            videoIds.add(item.getSnippet().getResourceId().getVideoId());
        }

        VideoListResponse videoListResponse;
        try {
            videoListResponse = mYouTube.videos()
                    .list(YOUTUBE_VIDEOS_PART)
                    .setFields(YOUTUBE_VIDEOS_FIELDS)
                    .setKey(Config.YOUTUBE_BROWSER_API_KEY)
                    .setId(TextUtils.join(",", videoIds))
                    .execute();
        } catch (IOException e) {
            Log.e(TAG, "Error getting a VideoListResponse from YouTube: " + e);
            return null;
        }

        if (videoListResponse == null) {
            Log.e(TAG, "Failed to get video list");
            return null;
        }

        return videoListResponse.getItems();
    }
}
