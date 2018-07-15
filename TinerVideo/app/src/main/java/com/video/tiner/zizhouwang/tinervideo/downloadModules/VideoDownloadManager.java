package com.video.tiner.zizhouwang.tinervideo.downloadModules;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by zizhouwang on 2018/6/14.
 */

public class VideoDownloadManager {

    public static Boolean isStop = false;
    public static long fileSize;
    private static final String downloadDir = FormatUtil.mainContext.getCacheDir() + "/TinerVideoCache";
    public static LinkedList<VideoModel> videoDownloadingModels = null;
    public static LinkedList<VideoModel> videoDownloadedModels = null;
    private static Handler handler = new Handler();
    private static Boolean isRunning = false;
    private static OnVideoDownloadListener onVideoDownloadListener;

    private static String currentUrlPath;
    private static String currentFileAbsolutePath;
    private static VideoModel currentVideoModel;
    public static int currentVideoId;
    public static float currentSpeed;

    public static void addNeedDownloadVideo(VideoModel videoModel) {
        if (videoDownloadingModels.contains(videoModel)) {
            Toast.makeText(FormatUtil.mainContext, "文件正在下载", Toast.LENGTH_LONG).show();
            return;
        }
        String currentFileAbsolutePath = getSavedVideoFilePath(videoModel.getVideo_id());
        File file = new File(currentFileAbsolutePath);
        if (file.exists()) {
            Toast.makeText(FormatUtil.mainContext, "文件已存在", Toast.LENGTH_LONG).show();
            return;
        }
        videoDownloadingModels.add(videoModel);
        saveVideoInfoJsons(FormatUtil.mainContext, "videoJsons");
        startDownloadVideo(FormatUtil.mainContext);
    }

    public static String getSavedVideoFilePath(int videoId) {
        return downloadDir + "/" + videoId + ".mp4";
    }

    public static void startDownloadVideo(final Context context) {
        loadVideoInfoJsons(context, "videoJsons");
        if (isRunning == false) {
            try {
                new Thread(new DownloadVideoRunnable()).start();
                isRunning = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class DownloadVideoRunnable implements Runnable {
        @Override
        public void run() {
            Context context = FormatUtil.mainContext;
            if (videoDownloadingModels.size() == 0) {
                isRunning = false;
                return;
            }
            while (true) {
                currentVideoModel = videoDownloadingModels.get(0);
//                videoModel = gson.fromJson(videoInfoJson, VideoModel.class);
                currentVideoId = currentVideoModel.getVideo_id();
                currentUrlPath = currentVideoModel.getVideo_cdn_url();
                if (fileSize == 0) {
                    fileSize = getFileTotalLength(context, "File_Length" + currentVideoId);
                }
                currentFileAbsolutePath = downloadDir + "/" + currentVideoId + ".mp4";
                File file = new File(currentFileAbsolutePath);
                if (file.exists() && fileSize == 0) {
                    videoDownloadedModels.add(videoDownloadingModels.remove(0));
                    saveVideoInfoJsons(context, "videoJsons");
                    continue;
                }
                break;
            }
            URL url = null;
            BufferedInputStream bin = null;
            HttpURLConnection httpURLConnection = null;
            while (true) {
                try {
                    //获取开始下载位置
                    long startOffset = getFileLength(context, "File_startOffset" + currentVideoId);
                    url = new URL(currentUrlPath);
                    if (fileSize == 0) {
                        //获取HttpURLConnection对象
                        Log.v("start get file size", "start get file size");
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        //设置请求方式
                        httpURLConnection.setRequestMethod("HEAD");
                        httpURLConnection.connect();
                        if (httpURLConnection.getResponseCode() == 200) {
                            fileSize = httpURLConnection.getContentLength();
                            saveFileTotalLength(context, fileSize, "File_Length" + currentVideoId);
                            Log.v("got file size", "got file size");
                            if (fileSize > 0 && fileSize < 40960) {
                                fileSize = Long.MAX_VALUE;
                            }
                        } else {
                            continue;
                        }
                    }
                    if (startOffset > 0 && startOffset >= fileSize) {
                        Boolean isHavingMoreNeedDownloadVideo = videoDownloadComplete();
                        if (isHavingMoreNeedDownloadVideo) {
                            continue;
                        } else {
                            break;
                        }
                    }
                    if (fileSize < 0) {
                        Toast.makeText(context, "获取文件大小失败！", Toast.LENGTH_LONG).show();
                        isRunning = false;
                        return;
                    }
                    //获取HttpURLConnection对象
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    //设置请求方式
                    httpURLConnection.setRequestMethod("GET");
                    long endOffSet = startOffset + 40960;
                    if (endOffSet >= fileSize) {
                        endOffSet = fileSize;
                    }
                    httpURLConnection.setRequestProperty("Range", "bytes=" + startOffset + "-" + endOffSet);
                    // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
                    Log.v("start get http", "start get http fileSize:" + fileSize + " startOffset:" + startOffset);
                    long startHttpTime = System.currentTimeMillis();
                    httpURLConnection.connect();
                    Log.v("receive http", "receive http fileSize:" + fileSize + " startOffset:" + startOffset);
                    if (httpURLConnection.getResponseCode() == 206) {
                        long endHttpTime = System.currentTimeMillis();
                        float spendTime = (float) (endHttpTime - startHttpTime) / 1000.0f;
                        //if startOffset ==0 的时候，你就要把你的文件大小保存起来
                        //获取文件的大小httpURLConnection.getContentLength();
                        float trueFileSize = (float) httpURLConnection.getContentLength();
                        currentSpeed = trueFileSize / spendTime;
                        //当你第一次下载的时候，也就是你的起始位置是0的时候，这就是这个文件的总大小，如果bytes=xx 的范围大于0，那么你获取的值就是你的文件总大小-bytes
                        //获取文件输出流
                        bin = new BufferedInputStream(httpURLConnection.getInputStream());
                        //这个是你要保存在那个目录的位置
                        File folder = new File(downloadDir);
                        //如果文件夹不存在则新建一个文件夹
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }

                        // 随机访问文件，可以指定断点续传的起始位置
                        //currentFileAbsolutePath 是你具体的文件路径
                        RandomAccessFile randomAccessFile = new RandomAccessFile(currentFileAbsolutePath, "rwd");
                        randomAccessFile.seek(startOffset);
                        byte[] buffer = new byte[2048];
                        int len;
                        //isStop可以用来实现暂停功能
                        while ((len = bin.read(buffer)) != -1 && !isStop) {
                            randomAccessFile.write(buffer, 0, len);
                            startOffset += len;
                            //刷新下载进度
                            Message msg = new Message();
                            msg.what = (int) ((startOffset * 100) / fileSize);
                            //使用handler发送消息刷新UI
                            handler.sendMessage(msg);
                            //保存下载的位置到SharedPreferences,下次下载的时候拿值写入设置字符编码
                            saveFileLength(context, startOffset, "File_startOffset" + currentVideoId);
                        }
                        if (onVideoDownloadListener != null) {
                            onVideoDownloadListener.videoDownloadProcessUpdate();
                        }
                    } else {
                        Boolean isHavingMoreNeedDownloadVideo = videoDownloadComplete();
                        if (isHavingMoreNeedDownloadVideo) {
                            continue;
                        } else {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (url != null) {
                        url = null;
                    }
                    if (bin != null) {
                        try {
                            bin.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }

                }
            }
            isRunning = false;
        }
    }

    private static boolean videoDownloadComplete() {
        Context context = FormatUtil.mainContext;
        videoDownloadedModels.add(videoDownloadingModels.remove(0));
        saveFileTotalLength(context, (long) 0, "File_Length" + currentVideoId);
        saveFileLength(context, (long) 0, "File_startOffset" + currentVideoId);
        saveVideoInfoJsons(context, "videoJsons");
        if (videoDownloadingModels.isEmpty()) {
            Log.v("all download complete", "all download complete");
//            break;
            return false;
        }
        fileSize = 0;
        currentVideoModel = videoDownloadingModels.get(0);
        currentVideoId = currentVideoModel.getVideo_id();
        currentUrlPath = currentVideoModel.getVideo_cdn_url();
        if (fileSize == 0) {
            fileSize = getFileTotalLength(context, "File_Length" + currentVideoId);
        }
        currentFileAbsolutePath = downloadDir + "/" + currentVideoId + ".mp4";
        Log.v("one download complete", "one download complete");
//        continue;
        return true;
    }

    /**
     * 保存文件长度
     *
     * @param context
     * @param fileLength
     */
    private static void saveFileLength(Context context, Long fileLength, String key) {
        SharedPreferences sp = context.getSharedPreferences("videoDownload", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, fileLength);
        editor.commit();
    }

    /**
     * 获取文件长度
     *
     * @param context
     * @return
     */
    private static Long getFileLength(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("videoDownload", Context.MODE_PRIVATE);
        return sp.getLong(key, 0);
    }

    private static void saveFileTotalLength(Context context, Long fileTotalLength, String key) {
        SharedPreferences sp = context.getSharedPreferences("videoDownload", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, fileTotalLength);
        editor.commit();
    }

    private static Long getFileTotalLength(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("videoDownload", Context.MODE_PRIVATE);
        return sp.getLong(key, 0);
    }

//    private static void saveVideoInfoJsons(Context context, String key) {
//        SharedPreferences sp = context.getSharedPreferences("videoDownload", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        JSONArray videoJsons = new JSONArray();
//        for (int i = 0; i < videoInfoJsons.size(); i++) {
//            videoJsons.put(videoInfoJsons.get(i));
//        }
//        editor.putString(key, videoJsons.toString());
//        editor.commit();
//    }

    private static void saveVideoInfoJsons(Context context, String key) {
        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences("videoDownload", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        JSONArray videoJsons = new JSONArray();
        JSONArray videoDownloadedJsons = new JSONArray();
        for (int i = 0; i < videoDownloadingModels.size(); i++) {
            videoJsons.put(gson.toJson(videoDownloadingModels.get(i)));
        }
        for (VideoModel videoModel : videoDownloadedModels) {
            videoDownloadedJsons.put(gson.toJson(videoModel));
        }
        editor.putString(key, videoJsons.toString());
        editor.putString("downloadedVideos", videoDownloadedJsons.toString());
        editor.commit();
    }

    private static void loadVideoInfoJsons(Context context, String key) {
        if (videoDownloadingModels == null) {
            videoDownloadingModels = new LinkedList<>();
        }
        if (videoDownloadedModels == null) {
            videoDownloadedModels = new LinkedList<>();
        }
        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences("videoDownload", Context.MODE_PRIVATE);
        try {
            JSONArray videoJsons = new JSONArray(sp.getString(key, "[]"));
            for (int i = 0; i < videoJsons.length(); i++) {
                videoDownloadingModels.add(gson.fromJson(videoJsons.getString(i), VideoModel.class));
            }
            JSONArray videoDownloadedJsons = new JSONArray(sp.getString("downloadedVideos", "[]"));
            for (int i = 0; i < videoDownloadedJsons.length(); i++) {
                videoDownloadedModels.add(gson.fromJson(videoDownloadedJsons.getString(i), VideoModel.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LinkedList<VideoModel> getVideoDownloadModels() {
        LinkedList<VideoModel> videoDownloadModels = new LinkedList<>();
        videoDownloadModels.addAll(videoDownloadedModels);
        videoDownloadModels.addAll(videoDownloadingModels);
        return videoDownloadModels;
    }

    public static void setOnVideoDownloadListener(OnVideoDownloadListener onVideoDownloadListener) {
        VideoDownloadManager.onVideoDownloadListener = onVideoDownloadListener;
    }

    public interface OnVideoDownloadListener {
        void videoDownloadProcessUpdate();

        void videoDownloadComplete(int videoId);
    }

    public static boolean isDownloadedVideo(VideoModel bean) {
        if (VideoDownloadManager.videoDownloadedModels.contains(bean)) {
            return true;
        } else {
            return false;
        }
    }
}
