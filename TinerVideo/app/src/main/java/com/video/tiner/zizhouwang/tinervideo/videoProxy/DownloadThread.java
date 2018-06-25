package com.video.tiner.zizhouwang.tinervideo.videoProxy;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

/**
 * 下载模块
 *
 * @author hellogv
 */
public class DownloadThread extends Thread {
    static private final String TAG = "DownloadThread";
    private String mUrl;
    private String mPath;

    private int mDownloadSize;

    private int mTargetSize;
    private boolean mStop, mDeleteFile;
    private boolean mDownloading;
    private boolean mStarted;
    private boolean mError;

    public DownloadThread(String url, String savePath, int targetSize) {
        mUrl = url;
        mPath = savePath;
        mTargetSize = targetSize;
        mDownloadSize = 0;

        mStop = false;
        mDeleteFile = false;
        mDownloading = false;
        mStarted = false;
        mError = false;
    }

    @Override
    public void run() {
        mDownloading = true;
        download();
    }

    /**
     * 启动下载线程
     */
    public void startThread() {
        if (!mStarted) {
            this.start();

            // 只能启动一次
            mStarted = true;
        }
    }

    /**
     * 停止下载线程, deleteFile是否要删除临时文件
     */
    public void stopThread(boolean deleteFile) {
        mStop = true;
        mDeleteFile = deleteFile;

    }

    /**
     * 是否正在下载
     */
    public boolean isDownloading() {
        return mDownloading;
    }

    /**
     * 是否下载异常
     *
     * @return
     */
    public boolean isError() {
        return mError;
    }

    public int getDownloadedSize() {
        return mDownloadSize;
    }

    /**
     * 是否下载成功
     */
    public synchronized boolean isDownloadSuccessed() {
        return (mDownloadSize != 0 && mDownloadSize == mTargetSize);
    }

    private synchronized void download() {
        int mTotalSize = 0;
        InputStream is = null;
        BufferedInputStream bin = null;
        FileOutputStream os = null;
        HttpURLConnection con = null;
        File file = new File(mPath);
        if (file.exists()) {
            return;
        }
        if (mStop) {
            return;
        }
        try {
//            URL url = new URL(mUrl);
//            URLConnection con = url.openConnection();
//            mTotalSize = con.getContentLength();
//            is = con.getInputStream();
//            os = new FileOutputStream(mPath);
//            Log.e(TAG, mPath);
//            int len = 0;
//            byte[] bs = new byte[1024];
//            if (mStop) {
//                return;
//            }
//            while (!mStop //未强制停止
//                    && mDownloadSize < mTargetSize //未下载足够
//                    && ((len = is.read(bs)) != -1)) {//未全部读取
//                os.write(bs, 0, len);
//                mDownloadSize += len;
//            }

            URL url = new URL(mUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Host", "img.ifcdn.com");
            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestProperty("User-Agent", "stagefright/1.2 (Linux;Android 4.4.4)");
            con.setRequestProperty("Accept-Encoding", "gzip,deflate");
            con.setRequestProperty("Range", "bytes=0-" + mTargetSize);//1083024
            con.connect();
            bin = new BufferedInputStream(con.getInputStream());
            os = new FileOutputStream(mPath);
            int len = 0;
            byte[] bs = new byte[2048];
            while (((len = bin.read(bs)) != -1)) {//未全部读取
                os.write(bs, 0, len);
            }
        } catch (Exception e) {
            mError = true;
            Log.e(TAG, "download error:" + e.toString() + "");
            Log.e(TAG, ProxyUtils.getExceptionMessage(e));
        } finally {
            Log.e(TAG, "mTotalSize:" + mTotalSize + ",mTargetSize:" + mTargetSize);
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }

            if (bin != null) {
                try {
                    bin.close();
                } catch (IOException e) {
                }
            }

            if (mDeleteFile) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }

//                File file = new File(mPath);
//                file.delete();
            }
            mDownloading = false;
        }
    }
}
