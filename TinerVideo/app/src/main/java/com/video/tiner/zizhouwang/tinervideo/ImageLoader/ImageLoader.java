package com.video.tiner.zizhouwang.tinervideo.ImageLoader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by zizhouwang on 2018/5/31.
 */

public class ImageLoader {
    private static final int noVideoURL = -1;
    private static final int noLocalImage = -1;
    private static final int getLocalImage = 1;

    private static ImageLoader mInstance = null;

    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;

    public static ImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader();
                }
            }
        }
        return mInstance;
    }

    private ImageLoader() {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    /**
     * 加载网络图片
     *
     * @param mImageView
     * @param url
     */
    public void loadImageView(ImageView mImageView, String url) {
        int loadLocalImageResult = noNetworkLoadImage(mImageView, url, -1, 0, 0);
        //网络缓存
        Log.d("", "尝试从网络获取图片...");
        if (loadLocalImageResult == noLocalImage) {
            mNetCacheUtils.getBitmapFromNet(mImageView, url);
        }
    }

    public void loadImageViewWithTag(ImageView mImageView, String url, int tag) {
        int loadLocalImageResult = noNetworkLoadImage(mImageView, url, tag, 0, 0);
        //网络缓存
        Log.d("", "尝试从网络获取图片...");
        if (loadLocalImageResult == noLocalImage) {
            mNetCacheUtils.getBitmapFromNetWithTag(mImageView, url, tag);
        }
    }

    public void loadImageViewWithTagAndVideoPath(final ImageView mImageView, final String videoURL, final int tag, final int videoWidth, final int videoHeight, final ImageView centerCropImageView) {
        reducThumbImageView(mImageView, centerCropImageView);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int loadLocalImageResult = noNetworkLoadImage(mImageView, videoURL, tag, videoWidth, videoHeight, centerCropImageView);
//                //网络缓存
//                if (loadLocalImageResult == noLocalImage) {
//                    Log.d("", "尝试从网络获取图片...");
//                    mNetCacheUtils.getBitmapFromNetWithTagWithIden(mImageView, videoURL, tag, FormatUtil.getThumbImg, videoWidth, videoHeight, centerCropImageView);
//                }
//            }
//        }).start();
        int loadLocalImageResult = noNetworkLoadImage(mImageView, videoURL, tag, videoWidth, videoHeight, centerCropImageView);
        //网络缓存
        if (loadLocalImageResult == noLocalImage) {
            Log.d("", "尝试从网络获取图片...");
            mNetCacheUtils.getBitmapFromNetWithTagWithIden(mImageView, videoURL, tag, FormatUtil.getThumbImg, videoWidth, videoHeight, centerCropImageView);
        }
    }

    private void reducThumbImageView(ImageView mImageView, ImageView centerCropImageView) {
        centerCropImageView.setImageBitmap(null);
        mImageView.setImageBitmap(FormatUtil.readBitMap(mImageView.getContext(), R.drawable.video_thumbnail));
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public int noNetworkLoadImage(ImageView mImageView, String url, int tag, int videoWidth, int videoHeight) {
        return noNetworkLoadImage(mImageView, url, tag, videoWidth, videoHeight, null);
    }

    public int noNetworkLoadImage(final ImageView mImageView, String url, final int tag, int videoWidth, int videoHeight, final ImageView centerCropImageView) {
        if (url.length() == 0) {
            return noVideoURL;
        }
        final Bitmap bitmap;

        //本地缓存
        Log.d("", "尝试从本地获取图片...");
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if (bitmap != null) {
            if (videoWidth > 0 && bitmap.getWidth() != videoWidth) {
                Bitmap newBitmap = FormatUtil.zoomImg(bitmap, videoWidth, videoHeight);
                mLocalCacheUtils.setBitmapToLocal(url, newBitmap);
            }
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    if (tag == -1 || (int)mImageView.getTag() == tag) {
                        mImageView.setImageBitmap(bitmap);
                        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        if (centerCropImageView != null) {
                            centerCropImageView.setImageBitmap(bitmap);
                        }
                    }
                }
            };
            handler.post(task);
            //从本地获取图片后,保存至内存中
            return getLocalImage;
        }
        return noLocalImage;
    }
}
