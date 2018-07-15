package com.video.tiner.zizhouwang.tinervideo.CustomFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.adapter.DownloadedVideoListAdapter;
import com.video.tiner.zizhouwang.tinervideo.adapter.VideoListAdapter;
import com.video.tiner.zizhouwang.tinervideo.downloadModules.VideoDownloadManager;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerNavView;
import com.video.tiner.zizhouwang.tinervideo.xListView.XListView;

import java.util.LinkedList;

/**
 * Created by Administrator on 2018/7/11.
 */

public class DownloadedVideoFragment extends SubFragment {

    private FrameLayout downloadedVideoFL;
    private XListView downloadedVideoListView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout view = (FrameLayout) inflater.inflate(R.layout.downloaded_video_layout, null);
        downloadedVideoFL = view.findViewById(R.id.downloadedVideoFL);
        downloadedVideoListView = view.findViewById(R.id.downloadedVideoListView);
        downloadedVideoListView.setPullLoadEnable(false);
        downloadedVideoListView.setPullRefreshEnable(false);
        View emptyView = inflater.inflate(R.layout.empty_view, null);
        downloadedVideoListView .setEmptyView(emptyView);
        downloadedVideoListView.removeHeaderView(downloadedVideoListView.mHeaderView);
        TinerNavView tinerNavView = FormatUtil.getTinerNavView((AppCompatActivity) view.getContext(), view, downloadedVideoFL, false);
        tinerNavView.navTextView.setText("Download Video");
        tinerNavView.navTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tinerNavView.navTextView.setGravity(Gravity.CENTER);
        tinerNavView.navTextView.setTextColor(Color.argb(0xff, 0xff, 0xff, 0xff));
        super.onCreateView(inflater, container, savedInstanceState, view, tinerNavView);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                LinkedList<VideoModel> videoModels = new LinkedList<>();
                msg.obj = VideoDownloadManager.getVideoDownloadModels();
                handler.sendMessage(msg);
            }
        }).start();
        return view;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DownloadedVideoListAdapter videoListAdapter = new DownloadedVideoListAdapter(FormatUtil.mainContext, (LinkedList<VideoModel>)msg.obj, downloadedVideoListView, "downloadedVideo");
            downloadedVideoListView.setAdapter(videoListAdapter);
        }
    };
}
