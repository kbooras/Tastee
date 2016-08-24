package com.kirstiebooras.foodvids.firebase;

import java.util.List;

public class PlaylistDetails {

    private String mName;
    private int mResourceId;
    private List<String> mVideoList;

    public PlaylistDetails() {
        /*Blank default constructor essential for Firebase*/
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getResourceId() {
        return mResourceId;
    }

    public void setResourceId(int resourceId) {
        mResourceId = resourceId;
    }

    public List<String> getVideoList() {
        return mVideoList;
    }

    public void setmVideoList(List<String> videoList) {
        mVideoList = videoList;
    }
}
