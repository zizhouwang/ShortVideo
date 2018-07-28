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
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
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
    private TextView editTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout view = (FrameLayout) inflater.inflate(R.layout.downloaded_video_layout, null);
        downloadedVideoFL = view.findViewById(R.id.downloadedVideoFL);
        TinerNavView tinerNavView = FormatUtil.getTinerNavView((AppCompatActivity) view.getContext(), view, downloadedVideoFL, false);
        tinerNavView.navTextView.setText("Download Video");
        tinerNavView.navTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tinerNavView.navTextView.setGravity(Gravity.CENTER);
        tinerNavView.navTextView.setTextColor(Color.argb(0xff, 0xff, 0xff, 0xff));
        editTextView = new TextView(FormatUtil.mainContext);
        editTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        editTextView.setText("edit");
        editTextView.setTextColor(getResources().getColor(R.color.whiteColor));
        editTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tinerNavView.navContentFL.addView(editTextView);
        FrameLayout.LayoutParams editTextLayout = (FrameLayout.LayoutParams) editTextView.getLayoutParams();
        editTextLayout.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        editTextLayout.width = 200 * FormatUtil.getScreenWidth(FormatUtil.mainContext) / 1080;
        editTextLayout.height = FrameLayout.LayoutParams.MATCH_PARENT;
        editTextLayout.rightMargin = 30;
        editTextView.setLayoutParams(editTextLayout);

        super.onCreateView(inflater, container, savedInstanceState, view, tinerNavView);

        downloadedVideoListView = view.findViewById(R.id.downloadedVideoListView);
        editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadedVideoListView.isEditing = !(downloadedVideoListView.isEditing);
                HeaderViewListAdapter hAdapter = (HeaderViewListAdapter) downloadedVideoListView.getAdapter();
                DownloadedVideoListAdapter downloadedVideoListAdapter = (DownloadedVideoListAdapter) hAdapter.getWrappedAdapter();
                if (downloadedVideoListView.isEditing) {
                    editTextView.setText("cancel");
                } else {
                    editTextView.setText("edit");
                }
                downloadedVideoListAdapter.changeEditView(downloadedVideoListView.isEditing);
            }
        });
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
                        msg.obj = new DownloadedVideoListAdapter(FormatUtil.mainContext, VideoDownloadManager.getVideoDownloadModels(), downloadedVideoListView, "downloadedVideo");
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("downloadPage");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("downloadPage");
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DownloadedVideoListAdapter downloadedVideoListAdapter = (DownloadedVideoListAdapter) msg.obj;
            if (downloadedVideoListAdapter.mList.size() > 0) {
                downloadedVideoListView.setAdapter((DownloadedVideoListAdapter) msg.obj);
            } else {
                View emptyView = downloadedVideoListView.getEmptyView();
                ImageView noDataImageView = emptyView.findViewById(R.id.noDataImageView);
                noDataImageView.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        downloadedVideoListView.isEditing = false;
        editTextView.setText("edit");
        FormatUtil.pauseCurrentVideo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
