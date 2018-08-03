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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.video.tiner.zizhouwang.tinervideo.CustomFragment.HomeFragment;
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
    protected LayoutInflater mInflater;
    private int videoProxyPort = 9180;
    public List<View> convertViews = new ArrayList<>();
    private int leastItemCount = 4;
    protected String tagStr;

    private HashMap<String, HttpGetProxy> httpGetProxyHashMap = new HashMap<>();

    // 通过构造器关联数据源与数据适配器
    public VideoListAdapter(Context context, List<VideoModel> list, XListView listView, String tagStr) {
        this.tagStr = tagStr;
        mList = list;
        // 使用当前要使用的界面对象context去初始化布局装载器对象mInflater
        mInflater = LayoutInflater.from(context);
        this.listView = listView;
        listView.mTotalItemViews = new ArrayList<>();

        for (int i = 0; i < leastItemCount; i++) {
            ViewHolder viewHolder;
            View convertView = mInflater.inflate(R.layout.video_list_item, null);
            viewHolder = loadViewHolder(convertView, tagStr);

            convertView.setTag(viewHolder);
            convertViews.add(convertView);
        }
    }

    protected ViewHolder loadViewHolder(View convertView, String tagStr) {
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        viewHolder.videoListVerticalLL = convertView.findViewById(R.id.videoListVerticalLL);
        viewHolder.videoLL = convertView.findViewById(R.id.videoLL);
        viewHolder.shareLayout = convertView.findViewById(R.id.shareLayout);
        viewHolder.vLBottomItemLL = convertView.findViewById(R.id.vLBottomItemLL);
        viewHolder.videoBottomLinearLayout = convertView.findViewById(R.id.videoBottomLinearLayout);
        viewHolder.likeLayout = convertView.findViewById(R.id.likeLayout);
        viewHolder.videoChannelTV = convertView.findViewById(R.id.videoChannelTV);
        viewHolder.likeCountTV = convertView.findViewById(R.id.likeCountTV);
        viewHolder.shareCountTV = convertView.findViewById(R.id.shareCountTV);
//            viewHolder.downloadCoinsTV = convertView.findViewById(R.id.downloadCoinsTV);
//            viewHolder.downloadVideoIV = convertView.findViewById(R.id.downloadVideoIV);
//            viewHolder.downloadVideoIV.setImageBitmap(FormatUtil.readBitMap(convertView.getContext(), R.drawable.download_button));
        viewHolder.viewImageView = convertView.findViewById(R.id.viewImageView);
        viewHolder.viewImageView.setImageBitmap(FormatUtil.readBitMap(convertView.getContext(), R.drawable.view1));
        viewHolder.likeImageView = convertView.findViewById(R.id.likeImageView);
        viewHolder.likeImageView.setImageBitmap(FormatUtil.readBitMap(convertView.getContext(), R.drawable.like_button));
        viewHolder.shareImageView = convertView.findViewById(R.id.shareImageView);
        viewHolder.shareImageView.setImageBitmap(FormatUtil.readBitMap(convertView.getContext(), R.drawable.share_button));
        viewHolder.tinerInteView = convertView.findViewById(R.id.tinerVV);
        viewHolder.deleteVideoImageView = convertView.findViewById(R.id.deleteVideoImageView);

        return viewHolder;
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
                convertView = mInflater.inflate(R.layout.video_list_item, null);
                viewHolder = loadViewHolder(convertView, tagStr);
                convertView.setTag(viewHolder);
            }
            if (!listView.mTotalItemViews.contains(viewHolder)) {
                listView.mTotalConvertViews.add(convertView);
                listView.mTotalItemViews.add(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.position = position;
        Collections.sort(listView.mTotalItemViews, new Comparator<ViewHolder>() {
            @Override
            public int compare(ViewHolder o1, ViewHolder o2) {
                Integer tag1 = (int) (o1.position);
                Integer tag2 = (int) (o2.position);
                return tag1.compareTo(tag2);
            }
        });
        if (position > mList.size() * 3 / 4) {
            if (tagStr.equals("home") && !FormatUtil.homeListView.isRefreshing) {
                HomeFragment.sendRequestWithHttpURLConnection(FormatUtil.mainContext, listView, true);
            }
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
        viewHolder.tinerInteView.updateUIAndData(position, height, videoWidth, bean, tagStr, listView);
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
        int viewNumber = (int)((float)bean.getVideo_details().getLiked_number() * (Math.random() * 0.5f + 1.5f));
        if (viewNumber > 1000) {
            viewNumber = viewNumber / 1000;
            viewHolder.videoChannelTV.setText("" + viewNumber + "k");
        } else {
            viewHolder.videoChannelTV.setText("" + viewNumber);
        }

        viewHolder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appContext = (AppCompatActivity) FormatUtil.mainContext;
                TinerShareView shareView = FormatUtil.getShareView(appContext);
                shareView.downloadFL.setVisibility(View.VISIBLE);
                shareView.deleteFL.setVisibility(View.INVISIBLE);
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
        public int startLeftMargin;
        public int startTopMargin;
        public int startRightMargin;
        public int startBottomMargin;

        public LinearLayout videoListVerticalLL;
        public LinearLayout videoLL;
        public LinearLayout shareLayout;
        public LinearLayout vLBottomItemLL;
        public LinearLayout videoBottomLinearLayout;
        public LinearLayout likeLayout;
        public TextView videoChannelTV;
        public TextView likeCountTV;
        public TextView shareCountTV;
        //        public TextView downloadCoinsTV;
//        public ImageView downloadVideoIV;
        public ImageView viewImageView;
        public ImageView likeImageView;
        public ImageView shareImageView;
        public TinerVideoView tinerInteView;
        public ImageView deleteVideoImageView;

        public int position;
    }
}
