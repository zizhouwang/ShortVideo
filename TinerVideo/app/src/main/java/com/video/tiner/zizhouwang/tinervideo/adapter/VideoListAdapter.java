package com.video.tiner.zizhouwang.tinervideo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.video.tiner.zizhouwang.tinervideo.ImageLoader.ImageLoader;
import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerShareView;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerVideoView;
import com.video.tiner.zizhouwang.tinervideo.videoProxy.HttpGetProxy;
import com.video.tiner.zizhouwang.tinervideo.xListView.XListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by Administrator on 2018/5/28.
 */

public class VideoListAdapter extends BaseAdapter {
    public XListView listView;
    public List<VideoModel> mList;
    private LayoutInflater mInflater;
    private int videoProxyPort = 9180;
    private List<View> convertViews = new ArrayList<>();
    private int leastItemCount = 4;

    private HashMap<String, HttpGetProxy> httpGetProxyHashMap = new HashMap<>();

    // 通过构造器关联数据源与数据适配器
    public VideoListAdapter(Context context, List<VideoModel> list, XListView listView) {
        mList = list;
        // 使用当前要使用的界面对象context去初始化布局装载器对象mInflater
        mInflater = LayoutInflater.from(context);
        this.listView = listView;
        listView.mTotalItemViews = new ArrayList<>();

        for (int i = 0; i < leastItemCount; i++) {
            final ViewHolder viewHolder;
            viewHolder = new ViewHolder();
            View convertView = mInflater.inflate(R.layout.video_list_item, null);
            viewHolder.videoListVerticalLL = convertView.findViewById(R.id.videoListVerticalLL);
            viewHolder.shareLayout = convertView.findViewById(R.id.shareLayout);
            viewHolder.likeLayout = convertView.findViewById(R.id.likeLayout);
            viewHolder.videoChannelTV = convertView.findViewById(R.id.videoChannelTV);
            viewHolder.likeCountTV = convertView.findViewById(R.id.likeCountTV);
            viewHolder.shareCountTV = convertView.findViewById(R.id.shareCountTV);
            viewHolder.downloadCoinsTV = convertView.findViewById(R.id.downloadCoinsTV);
            viewHolder.downloadVideoIV = convertView.findViewById(R.id.downloadVideoIV);
            viewHolder.downloadVideoIV.setImageBitmap(FormatUtil.readBitMap(convertView.getContext(), R.drawable.download_button));
            viewHolder.likeImageView = convertView.findViewById(R.id.likeImageView);
            viewHolder.likeImageView.setImageBitmap(FormatUtil.readBitMap(convertView.getContext(), R.drawable.like_button));
            viewHolder.shareImageView = convertView.findViewById(R.id.shareImageView);
            viewHolder.shareImageView.setImageBitmap(FormatUtil.readBitMap(convertView.getContext(), R.drawable.share_button));
            viewHolder.tinerInteView = convertView.findViewById(R.id.tinerVV);

            // 通过setTag将ViewHolder与convertView绑定
            convertView.setTag(viewHolder);
            convertViews.add(convertView);
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    // 返回指定索引对应的数据项
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {// View未被实例化，即缓冲池中无缓存才创建View
            // 将控件id保存在viewHolder中
            if (convertViews.size() > 0) {
                convertView = convertViews.remove(0);
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.video_list_item, null);
                viewHolder.videoListVerticalLL = convertView.findViewById(R.id.videoListVerticalLL);
                viewHolder.shareLayout = convertView.findViewById(R.id.shareLayout);
                viewHolder.likeLayout = convertView.findViewById(R.id.likeLayout);
                viewHolder.videoChannelTV = convertView.findViewById(R.id.videoChannelTV);
                viewHolder.likeCountTV = convertView.findViewById(R.id.likeCountTV);
                viewHolder.shareCountTV = convertView.findViewById(R.id.shareCountTV);
                viewHolder.downloadCoinsTV = convertView.findViewById(R.id.downloadCoinsTV);
                viewHolder.downloadVideoIV = convertView.findViewById(R.id.downloadVideoIV);
                viewHolder.downloadVideoIV.setImageBitmap(FormatUtil.readBitMap(convertView.getContext(), R.drawable.download_button));
                viewHolder.likeImageView = convertView.findViewById(R.id.likeImageView);
                viewHolder.likeImageView.setImageBitmap(FormatUtil.readBitMap(convertView.getContext(), R.drawable.like_button));
                viewHolder.shareImageView = convertView.findViewById(R.id.shareImageView);
                viewHolder.shareImageView.setImageBitmap(FormatUtil.readBitMap(convertView.getContext(), R.drawable.share_button));
                viewHolder.tinerInteView = convertView.findViewById(R.id.tinerVV);
                // 通过setTag将ViewHolder与convertView绑定
                convertView.setTag(viewHolder);
            }
            if (listView.mTotalItemViews.contains(viewHolder) == false) {
                listView.mTotalItemViews.add(viewHolder);
            }
        } else {
            // 通过ViewHolder对象找到对应控件
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Context context = convertView.getContext();
        final VideoModel bean = mList.get(position);

        String videoWidthAndHeight = bean.getWidth_height();
        String[] widthAndHeight = videoWidthAndHeight.split("x");
        int videoWidth = Integer.parseInt(widthAndHeight[0]);
        int videoHeight = Integer.parseInt(widthAndHeight[1]);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        int height;
        if (videoWidth > 0) {
            height = videoHeight * screenWidth / videoWidth;
            videoWidth = screenWidth;
            if (height > screenHeight / 3) {
                videoWidth = screenWidth * (screenHeight / 3) / height;
                height = screenHeight / 3;
            }
        } else {
            videoWidth = screenHeight / 3;
            height = screenHeight / 3;
        }
        viewHolder.tinerInteView.updateUIAndData(position, height, videoWidth, bean, httpGetProxyHashMap, videoProxyPort + position);
        Collections.sort(listView.mTotalItemViews, new Comparator<ViewHolder>() {
            @Override
            public int compare(ViewHolder o1, ViewHolder o2) {
                Integer tag1 = (int) (o1.tinerInteView.videoThumbnailIV.getTag());
                Integer tag2 = (int) (o2.tinerInteView.videoThumbnailIV.getTag());
                return tag1.compareTo(tag2);
            }
        });
        for (int i = 0; i < listView.mTotalItemViews.size(); i++) {
            Log.v("getViewThumbnailIVTag", "" + listView.mTotalItemViews.get(i).tinerInteView.videoThumbnailIV.getTag());
        }
        viewHolder.videoChannelTV.setText(bean.getChannel());
        int likedNumber = bean.getVideo_details().getLiked_number();
        if (likedNumber > 1000) {
            likedNumber = likedNumber / 1000;
            viewHolder.likeCountTV.setText("" + likedNumber + "k");
        } else {
            viewHolder.likeCountTV.setText("" + likedNumber);
        }
        int shareNumber = bean.getVideo_details().getShare_number();
        if (shareNumber > 1000) {
            shareNumber = shareNumber / 1000;
            viewHolder.shareCountTV.setText("" + shareNumber + "k");
        } else {
            viewHolder.shareCountTV.setText("" + shareNumber);
        }

        viewHolder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appContext = (AppCompatActivity) FormatUtil.mainContext;
                TinerShareView shareView = FormatUtil.getShareView(appContext);
                shareView.bean = bean;
                shareView.shareURL = bean.getVideo_cdn_url();
                FrameLayout tabContentFL = appContext.findViewById(R.id.tabContentFL);
                FrameLayout windowFL = (FrameLayout) tabContentFL.getParent();
                windowFL.addView(shareView);
            }
        });

        viewHolder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    // 避免重复的findViewById的操作
    public class ViewHolder {
        public LinearLayout videoListVerticalLL;
        public LinearLayout shareLayout;
        public LinearLayout likeLayout;
        public TextView videoChannelTV;
        public TextView likeCountTV;
        public TextView shareCountTV;
        public TextView downloadCoinsTV;
        public ImageView downloadVideoIV;
        public ImageView likeImageView;
        public ImageView shareImageView;
        public TinerVideoView tinerInteView;
    }
}
