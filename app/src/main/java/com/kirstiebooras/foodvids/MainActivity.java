package com.kirstiebooras.foodvids;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeBaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends YouTubeBaseActivity {

    private List<String> mVideoIds;
    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mVideoIds = new ArrayList<>();
        mAdapter = new VideoAdapter(this, mVideoIds);
        mRecyclerView.setAdapter(mAdapter);

        fetchVideos();
    }

    private void fetchVideos() {
        mVideoIds.add("PG6oAO9sgEU");
        mVideoIds.add("dK_khWxwb94");
        mVideoIds.add("PG6oAO9sgEU");
        mVideoIds.add("dK_khWxwb94");
        mVideoIds.add("PG6oAO9sgEU");
        mVideoIds.add("dK_khWxwb94");
        mAdapter.notifyDataSetChanged();
    }
}
