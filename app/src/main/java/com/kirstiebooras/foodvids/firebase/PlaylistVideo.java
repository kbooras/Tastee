package com.kirstiebooras.foodvids.firebase;

import java.util.HashMap;

public class PlaylistVideo {

    private String mName;
    private String mVideoId;
    private String mDescription;
    private String mThumbnail;
    private HashMap<String, Boolean> mFlags;

    public PlaylistVideo() {
         /*Blank default constructor essential for Firebase*/
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getVideoId() {
        return mVideoId;
    }

    public void setVideoId(String videoId) {
        mVideoId = videoId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
    }

    public HashMap<String, Boolean> getFlags() {
        return mFlags;
    }

    public void setFlags(HashMap<String, Boolean> flags) {
        mFlags = flags;
    }
}
