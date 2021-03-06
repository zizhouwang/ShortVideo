package com.video.tiner.zizhouwang.tinervideo.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;

import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.downloadModules.VideoDownloadManager;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerShareView;
import com.video.tiner.zizhouwang.tinervideo.xListView.XListView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import bolts.Bolts;

/**
 * Created by Administrator on 2018/7/14.
 */

public class DownloadedVideoListAdapter extends VideoListAdapter {
    public DownloadedVideoListAdapter(Context context, List<VideoModel> list, final XListView listView, String tagStr) {
        super(context, list, listView, tagStr);
        final DownloadedVideoListAdapter thiss = this;
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < thiss.listView.mTotalItemViews.size(); i++) {
                    DownloadViewHolder downloadViewHolder = (DownloadViewHolder) thiss.listView.mTotalItemViews.get(i);
                    if (downloadViewHolder.position < mList.size()) {
                        VideoModel bean = mList.get(downloadViewHolder.position);
                        if (VideoDownloadManager.isDownloadedVideo(bean)) {
                            downloadViewHolder.vLBottomItemLL.setVisibility(View.VISIBLE);
                            downloadViewHolder.downloadInfoView.setVisibility(View.GONE);
//                            downloadViewHolder.tinerInteView.videoThumbnailIV.setEnabled(true);
                            downloadViewHolder.tinerInteView.playVideoIV.setEnabled(true);
//                            try {
//                                downloadViewHolder.tinerInteView.tinerMediaPlayer.setDataSource(VideoDownloadManager.getSavedVideoFilePath(bean.getVideo_id()));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                        } else {
                            downloadViewHolder.downloadProgressBar.setMax(100);
                            downloadViewHolder.downloadProgressBar.setProgress(0);
                            downloadViewHolder.speedTextView.setText("");
                            downloadViewHolder.downloadInfoTextView.setText("");
                            String localFilePath = VideoDownloadManager.getSavedVideoFilePath(bean.getVideo_id());
                            File localFile = new File(localFilePath);
                            long localFileLength = 0;
                            if (localFile.exists()) {
                                localFileLength = localFile.length();
                            }
                            long downloadingFileSize = VideoDownloadManager.fileSize;
                            downloadViewHolder.downloadProgressBar.setMax((int) downloadingFileSize);
                            downloadViewHolder.downloadProgressBar.setProgress((int) localFileLength);
                            if (bean.getVideo_id() == VideoDownloadManager.newVideoModel.getVideo_id()) {
                                downloadViewHolder.speedTextView.setText(FormatUtil.conversionNumber(VideoDownloadManager.currentSpeed, true));
                                downloadViewHolder.downloadInfoTextView.setText(String.format("%s/%s", FormatUtil.conversionNumber(localFileLength, false), FormatUtil.conversionNumber(downloadingFileSize, false)));
                            } else {
                                downloadViewHolder.speedTextView.setText("");
                                downloadViewHolder.downloadInfoTextView.setText(String.format("%s/%s", "0B", "0B"));
                            }
                            downloadViewHolder.vLBottomItemLL.setVisibility(View.GONE);
                            downloadViewHolder.downloadInfoView.setVisibility(View.VISIBLE);
//                            downloadViewHolder.tinerInteView.videoThumbnailIV.setEnabled(false);
                            downloadViewHolder.tinerInteView.playVideoIV.setEnabled(false);
                        }
                    }
                }
            }
        };
        final Handler videoDownloadCompleteHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                thiss.notifyDataSetChanged();
                int videoId = (int) msg.obj;
                for (VideoModel videoModel : mList) {
                    if (videoModel.getVideo_id() == videoId) {
                        thiss.getView(mList.indexOf(videoModel), listView.mTotalConvertViews.get(mList.indexOf(videoModel)), listView);
                        mList.indexOf(videoModel);
                    }
                }
            }
        };
        VideoDownloadManager.setOnVideoDownloadListener(new VideoDownloadManager.OnVideoDownloadListener() {
            @Override
            public void videoDownloadProcessUpdate() {
                handler.post(runnable);
            }

            @Override
            public void videoDownloadComplete(int videoId) {
                Message msg = new Message();
                msg.obj = videoId;
                videoDownloadCompleteHandler.sendMessage(msg);
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
        viewHolder.videoBottomLinearLayout.addView(downloadInfoView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 184));
        return downloadViewHolder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DownloadedVideoListAdapter thiss = this;
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
            if (!listView.mTotalItemViews.contains(downloadViewHolder)) {
                listView.mTotalConvertViews.add(convertView);
                listView.mTotalItemViews.add(downloadViewHolder);
            }
        } else {
            downloadViewHolder = (DownloadViewHolder) convertView.getTag();
            if (downloadViewHolder.tinerInteView.tinerMediaPlayer != null && downloadViewHolder.tinerInteView.tinerMediaPlayer.isPlaying()) {
                return convertView;
            }
        }
        View view = super.getView(position, convertView, parent);
        final VideoModel bean = mList.get(position);
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
            long localFileLength = 0;
            if (localFile.exists()) {
                localFileLength = localFile.length();
            }
            long downloadingFileSize = VideoDownloadManager.fileSize;
            downloadViewHolder.downloadProgressBar.setMax((int) downloadingFileSize);
            downloadViewHolder.downloadProgressBar.setProgress((int) localFileLength);
            downloadViewHolder.speedTextView.setText(FormatUtil.conversionNumber(VideoDownloadManager.currentSpeed, true));
            downloadViewHolder.downloadInfoTextView.setText(String.format("%s/%s", FormatUtil.conversionNumber(localFileLength, false), FormatUtil.conversionNumber(downloadingFileSize, false)));
            downloadViewHolder.vLBottomItemLL.setVisibility(View.GONE);
            downloadViewHolder.downloadInfoView.setVisibility(View.VISIBLE);
        }
        downloadViewHolder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appContext = (AppCompatActivity) FormatUtil.mainContext;
                TinerShareView shareView = FormatUtil.getShareView(appContext);
                shareView.downloadFL.setVisibility(View.INVISIBLE);
                shareView.deleteFL.setVisibility(View.VISIBLE);
                shareView.bean = bean;
                shareView.downloadedVideoListAdapter = thiss;
                shareView.shareURL = bean.getVideo_cdn_url();
                FrameLayout tabContentFL = appContext.findViewById(R.id.tabContentFL);
                FrameLayout windowFL = (FrameLayout) tabContentFL.getParent();
                windowFL.addView(shareView);
            }
        });
        downloadViewHolder.deleteVideoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDownloadManager.removeDownloadedVideo(bean);
                removeOneItem(bean.getVideo_id());
            }
        });
        FrameLayout.LayoutParams videoParentLayout = (FrameLayout.LayoutParams) downloadViewHolder.videoListVerticalLL.getLayoutParams();
        if (listView.isEditing) {
            videoParentLayout.leftMargin = FormatUtil.dpToPx(-100);
            videoParentLayout.rightMargin = FormatUtil.dpToPx(100);
        } else {
            videoParentLayout.leftMargin = 0;
            videoParentLayout.rightMargin = 0;
        }
        downloadViewHolder.videoListVerticalLL.setLayoutParams(videoParentLayout);
        return view;
    }

    public class DownloadViewHolder extends ViewHolder {
        public ProgressBar downloadProgressBar;
        public TextView speedTextView;
        public TextView downloadInfoTextView;
        public LinearLayout downloadInfoView;

        public DownloadViewHolder(ViewHolder viewHolder) {
            videoListVerticalLL = viewHolder.videoListVerticalLL;
            videoLL = viewHolder.videoLL;
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
            deleteVideoImageView = viewHolder.deleteVideoImageView;
        }
    }

    public void removeOneItem(int videoId) {
        int videoIndex = -1;
        for (int i = 0; i < mList.size(); i++) {
            if (videoId == mList.get(i).getVideo_id()) {
                videoIndex = i;
            }
        }
        if (videoIndex > -1) {
            mList.remove(videoIndex);
        }
        notifyDataSetChanged();
        if (mList.size() == 0) {
            View emptyView = listView.getEmptyView();
            ImageView noDataImageView = emptyView.findViewById(R.id.noDataImageView);
            noDataImageView.setVisibility(View.VISIBLE);
        }
    }

    public void changeEditView(boolean isEditing) {
        if (isEditing) {
            for (int i = 0; i < listView.mTotalItemViews.size(); i++) {
                ViewHolder viewHolder = listView.mTotalItemViews.get(i);
                FrameLayout.LayoutParams videoParentLayout = (FrameLayout.LayoutParams) viewHolder.videoListVerticalLL.getLayoutParams();
                videoParentLayout.leftMargin = FormatUtil.dpToPx(-100);
                videoParentLayout.rightMargin = FormatUtil.dpToPx(100);
                viewHolder.videoListVerticalLL.setLayoutParams(videoParentLayout);
            }
        } else {
            for (int i = 0; i < listView.mTotalItemViews.size(); i++) {
                ViewHolder viewHolder = listView.mTotalItemViews.get(i);
                FrameLayout.LayoutParams videoParentLayout = (FrameLayout.LayoutParams) viewHolder.videoListVerticalLL.getLayoutParams();
                videoParentLayout.leftMargin = 0;
                videoParentLayout.rightMargin = 0;
                viewHolder.videoListVerticalLL.setLayoutParams(videoParentLayout);
            }
        }
    }
}
