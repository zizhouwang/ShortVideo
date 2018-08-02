package com.video.tiner.zizhouwang.tinervideo.CustomUI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.video.tiner.zizhouwang.tinervideo.adapter.VideoListAdapter;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/1.
 */

public class BaseListView extends ListView {
    public List<VideoModel> oldVideoModelList = new LinkedList<>();
    public Boolean isRefreshing = false;
    public int currentFullScreenTag = 0;
    public List<View> mTotalConvertViews = new ArrayList<>();
    public List<VideoListAdapter.ViewHolder> mTotalItemViews = new ArrayList<>();
    public VideoListAdapter videoListAdapter;
    public ImageView loadVideoIV;
    public ProgressBar loadVideoPB;
    public String tagStr;
    public boolean isEditing = false;

    public BaseListView(Context context) {
        super(context);
    }

    public BaseListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        videoListAdapter = (VideoListAdapter) adapter;
    }
}
