package com.video.tiner.zizhouwang.tinervideo.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by zizhouwang on 2018/5/31.
 */

class NetCacheUtils {
    private int videoWidth;
    private int videoHeight;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private LinkedList<String> downloadingURLs = new LinkedList<>();

    public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        mLocalCacheUtils = localCacheUtils;
        mMemoryCacheUtils = memoryCacheUtils;
    }

    /**
     * 从网络下载图片
     *
     * @param mImageView 显示图片的imageview
     * @param url        下载图片的网络地址
     */
    public void getBitmapFromNet(ImageView mImageView, String url) {
        new BitmapTask().execute(mImageView, url);//启动AsyncTask

    }

    public void getBitmapFromNetWithTag(ImageView mImageView, String url, int tag) {
        new BitmapTask().execute(mImageView, url, tag);//启动AsyncTask

    }

    public void getBitmapFromNetWithTagWithIden(ImageView mImageView, String url, int tag, String identify, int videoWidth, int videoHeight) {
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        if (downloadingURLs.contains(url)) {
            Log.v("already downloading", "already downloading");
        } else {
            downloadingURLs.add(url);
            new BitmapTask().execute(mImageView, url, tag, identify);//启动AsyncTask
        }
    }

    public void getBitmapFromNetWithTagWithIden(ImageView mImageView, String url, int tag, String identify, int videoWidth, int videoHeight, ImageView centerCropImageView) {
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        if (downloadingURLs.contains(url)) {
            Log.v("already downloading", "already downloading");
        } else {
            downloadingURLs.add(url);
            new BitmapTask().execute(mImageView, url, tag, identify, centerCropImageView);//启动AsyncTask
        }
    }

    /**
     * AsyncTask就是对handler和线程池的封装
     * 第一个泛型:参数类型
     * 第二个泛型:更新进度的泛型
     * 第三个泛型:onPostExecute的返回结果
     */
    class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

        private ImageView mImageView;
        private ImageView centerCropImageView = null;
        private String url;
        private int tag = -1;

        /**
         * 后台耗时操作,存在于子线程中
         *
         * @param params
         * @return
         */
        @Override
        protected Bitmap doInBackground(Object[] params) {
            mImageView = (ImageView) params[0];
            if (params.length > 4) {
                centerCropImageView = (ImageView) params[4];
            }
            url = (String) params[1];
            if (params.length > 2) {
                tag = (int)params[2];
            }
            Bitmap bitmap = null;
            if (params.length > 3) {
//                if (params[3].equals(FormatUtil.getThumbImg)) {
//                    FFmpegMediaMetadataRetriever mm = new FFmpegMediaMetadataRetriever();
//                    try {
//                        //获取视频文件数据
//                        mm.setDataSource(url);
//                        bitmap = mm.getFrameAtTime();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        bitmap = null;
//                    } finally {
//                        mm.release();
//                        mm = null;
//                    }
//                    return bitmap;
//                }
                return downLoadBitmap(url);
            } else {
                return downLoadBitmap(url);
            }
//            return null;
        }

        /**
         * 更新进度,在主线程中
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Void[] values) {
            super.onProgressUpdate(values);
        }

        /**
         * 耗时方法结束后执行该方法,主线程中
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                if (tag == -1 || tag == (int)mImageView.getTag()) {
                    mImageView.setImageBitmap(result);
                    mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    if (centerCropImageView != null) {
                        centerCropImageView.setImageBitmap(result);
                    }
                }
                int width = mImageView.getWidth();
                int height = mImageView.getHeight();

                //从网络获取图片后,保存至本地缓存
                if (videoWidth > 0 && result.getWidth() != videoWidth) {
                    result = FormatUtil.zoomImg(result, videoWidth, videoHeight);
                }
                mLocalCacheUtils.setBitmapToLocal(url, result);
            } else {
                Log.v("noThumbImg", url);
            }
            downloadingURLs.remove(url);
        }
    }

    /**
     * 网络下载图片
     *
     * @param url
     * @return
     */
    private Bitmap downLoadBitmap(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                //图片压缩
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;//宽高压缩为原来的1/2
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream(), null, options);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return null;
    }
}
