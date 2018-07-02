package com.video.tiner.zizhouwang.tinervideo.CustomUI;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.video.tiner.zizhouwang.tinervideo.adapter.VideoListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/1.
 */

public class BaseListView extends ListView {
    public int currentFullScreenTag = 0;
    public List<VideoListAdapter.ViewHolder> mTotalItemViews = new ArrayList<>();

    public BaseListView(Context context) {
        super(context);
    }

    public BaseListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
