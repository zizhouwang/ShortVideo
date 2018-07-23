package com.video.tiner.zizhouwang.tinervideo.CustomFragment;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

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
        TinerNavView tinerNavView = FormatUtil.getTinerNavView((AppCompatActivity) view.getContext(), view, downloadedVideoFL, false);
        tinerNavView.navTextView.setText("Download Video");
        tinerNavView.navTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tinerNavView.navTextView.setGravity(Gravity.CENTER);
        tinerNavView.navTextView.setTextColor(Color.argb(0xff, 0xff, 0xff, 0xff));
        TextView editTextView = new TextView(FormatUtil.mainContext);
        editTextView.setGravity(Gravity.CENTER);
        editTextView.setText("编辑");
        editTextView.setTextColor(getResources().getColor(R.color.whiteColor));
        editTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tinerNavView.addView(editTextView);
        FrameLayout.LayoutParams editTextLayout = (FrameLayout.LayoutParams) editTextView.getLayoutParams();
        editTextLayout.gravity = Gravity.CENTER_HORIZONTAL;
        editTextLayout.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        editTextLayout.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        editTextView.setLayoutParams(editTextLayout);

        super.onCreateView(inflater, container, savedInstanceState, view, tinerNavView);

        downloadedVideoListView = view.findViewById(R.id.downloadedVideoListView);
        downloadedVideoListView.setPullLoadEnable(false);
        downloadedVideoListView.setPullRefreshEnable(false);
        View emptyView = inflater.inflate(R.layout.empty_view, null);
        downloadedVideoListView.setEmptyView(emptyView);
        downloadedVideoFL.addView(downloadedVideoListView.getEmptyView());
        downloadedVideoListView.bringToFront();
        FrameLayout.LayoutParams downloadedVideoListViewLayout = (FrameLayout.LayoutParams) downloadedVideoListView.getLayoutParams();
        downloadedVideoListViewLayout.topMargin -= 3;
        downloadedVideoListView.setLayoutParams(downloadedVideoListViewLayout);
        downloadedVideoListView.setVisibility(View.VISIBLE);
        slideInSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        DownloadedVideoListAdapter videoListAdapter = new DownloadedVideoListAdapter(FormatUtil.mainContext, VideoDownloadManager.getVideoDownloadModels(), downloadedVideoListView, "downloadedVideo");
                        msg.obj = videoListAdapter;
                        handler.sendMessage(msg);
                    }
                }).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return view;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            downloadedVideoListView.setAdapter((DownloadedVideoListAdapter) msg.obj);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
