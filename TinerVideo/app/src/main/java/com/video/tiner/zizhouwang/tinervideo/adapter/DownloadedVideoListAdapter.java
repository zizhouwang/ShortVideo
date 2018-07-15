package com.video.tiner.zizhouwang.tinervideo.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;

import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.downloadModules.VideoDownloadManager;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;
import com.video.tiner.zizhouwang.tinervideo.xListView.XListView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import bolts.Bolts;

/**
 * Created by Administrator on 2018/7/14.
 */

public class DownloadedVideoListAdapter extends VideoListAdapter {
    public DownloadedVideoListAdapter(Context context, List<VideoModel> list, XListView listView, String tagStr) {
        super(context, list, listView, tagStr);
        final DownloadedVideoListAdapter thiss = this;
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                thiss.notifyDataSetChanged();
            }
        };
        VideoDownloadManager.setOnVideoDownloadListener(new VideoDownloadManager.OnVideoDownloadListener() {
            @Override
            public void videoDownloadProcessUpdate() {
                handler.post(runnable);
            }

            @Override
            public void videoDownloadComplete(int videoId) {
                handler.post(runnable);
            }
        });
    }

    @Override
    protected DownloadViewHolder loadViewHolder(View convertView, String tagStr) {
        ViewHolder viewHolder = super.loadViewHolder(convertView, tagStr);
        DownloadViewHolder downloadViewHolder = new DownloadViewHolder(viewHolder);
        LinearLayout downloadInfoView = (LinearLayout) mInflater.inflate(R.layout.download_info_layout, null);
        downloadViewHolder.downloadInfoView = downloadInfoView;
        downloadViewHolder.downloadProgressBar = downloadInfoView.findViewById(R.id.downloadProgressBar);
        downloadViewHolder.speedTextView = downloadInfoView.findViewById(R.id.speedTextView);
        downloadViewHolder.downloadInfoTextView = downloadInfoView.findViewById(R.id.downloadInfoTextView);
        viewHolder.videoBottomLinearLayout.addView(downloadInfoView);
        return downloadViewHolder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DownloadViewHolder downloadViewHolder;
        if (convertView == null) {// View未被实例化，即缓冲池中无缓存才创建View
            // 将控件id保存在viewHolder中
            if (convertViews.size() > 0) {
                convertView = convertViews.remove(0);
                downloadViewHolder = (DownloadViewHolder) convertView.getTag();
            } else {
                convertView = mInflater.inflate(R.layout.video_list_item, null);
                downloadViewHolder = loadViewHolder(convertView, tagStr);
                convertView.setTag(downloadViewHolder);
            }
            if (listView.mTotalItemViews.contains(downloadViewHolder) == false) {
                listView.mTotalItemViews.add(downloadViewHolder);
            }
        } else {
            downloadViewHolder = (DownloadViewHolder) convertView.getTag();
        }
        View view = super.getView(position, convertView, parent);
        VideoModel bean = mList.get(position);
        if (VideoDownloadManager.isDownloadedVideo(bean)) {
            downloadViewHolder.vLBottomItemLL.setVisibility(View.VISIBLE);
            downloadViewHolder.downloadInfoView.setVisibility(View.GONE);
        } else {
            downloadViewHolder.downloadProgressBar.setMax(100);
            downloadViewHolder.downloadProgressBar.setProgress(0);
            downloadViewHolder.speedTextView.setText("");
            downloadViewHolder.downloadInfoTextView.setText("");
            String localFilePath = VideoDownloadManager.getSavedVideoFilePath(bean.getVideo_id());
            File localFile = new File(localFilePath);
            if (localFile.exists()) {
                long downloadingFileSize = VideoDownloadManager.fileSize;
                long fileLength = localFile.length();
                downloadViewHolder.downloadProgressBar.setMax(100);
                downloadViewHolder.downloadProgressBar.setProgress(0);
                downloadViewHolder.speedTextView.setText(FormatUtil.conversionNumber(VideoDownloadManager.currentSpeed, false));
                downloadViewHolder.downloadInfoTextView.setText(String.format("%s/%s", FormatUtil.conversionNumber(fileLength, false), FormatUtil.conversionNumber(downloadingFileSize, false)));
            }
            downloadViewHolder.vLBottomItemLL.setVisibility(View.GONE);
            downloadViewHolder.downloadInfoView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public class DownloadViewHolder extends ViewHolder {
        public ProgressBar downloadProgressBar;
        public TextView speedTextView;
        public TextView downloadInfoTextView;
        public LinearLayout downloadInfoView;

        public DownloadViewHolder(ViewHolder viewHolder) {
            videoListVerticalLL = viewHolder.videoListVerticalLL;
            shareLayout = viewHolder.shareLayout;
            vLBottomItemLL = viewHolder.vLBottomItemLL;
            videoBottomLinearLayout = viewHolder.videoBottomLinearLayout;
            likeLayout = viewHolder.likeLayout;
            videoChannelTV = viewHolder.videoChannelTV;
            likeCountTV = viewHolder.likeCountTV;
            shareCountTV = viewHolder.shareCountTV;
//            downloadCoinsTV = viewHolder.downloadCoinsTV;
//            downloadVideoIV = viewHolder.downloadVideoIV;
            likeImageView = viewHolder.likeImageView;
            shareImageView = viewHolder.shareImageView;
            tinerInteView = viewHolder.tinerInteView;
        }
    }
}
