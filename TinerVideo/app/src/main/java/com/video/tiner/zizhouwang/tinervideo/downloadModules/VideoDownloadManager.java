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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by zizhouwang on 2018/6/14.
 */

public class VideoDownloadManager {

    public static long fileSize;
    private static final String downloadDir = FormatUtil.mainContext.getCacheDir() + "/TinerVideoCache";
    private static LinkedList<VideoModel> videoDownloadingModels = null;
    private static LinkedList<VideoModel> videoDownloadedModels = null;
    private static Handler handler = new Handler();
    private static Boolean isRunning = false;
    private static OnVideoDownloadListener onVideoDownloadListener;
    private static Thread downloadThread;

    private static String currentUrlPath;
    private static String currentFileAbsolutePath;
    private static VideoModel currentVideoModel;
    public static VideoModel newVideoModel; //如果要增加下载界面的暂停功能 下载切换功能 一定要用这个变量 因为它是在UI线程中更新的！不会有界面错乱的问题！
    private static int currentVideoId;

    public static float currentSpeed;

    final private static Object removeDownloadedVideo = new Object();
    final private static Object downloadLock = new Object();

    public static void addNeedDownloadVideo(VideoModel videoModel) {
        if (isContainsVideoModel(videoDownloadingModels, videoModel.getVideo_id())) {
            Toast.makeText(FormatUtil.mainContext, "视频正在下载", Toast.LENGTH_LONG).show();
            return;
        }
        String currentFileAbsolutePath = getSavedVideoFilePath(videoModel.getVideo_id());
        File file = new File(currentFileAbsolutePath);
        if (file.exists()) {
            if (isContainsVideoModel(videoDownloadedModels, videoModel.getVideo_id())) {
                Toast.makeText(FormatUtil.mainContext, "该视频已下载", Toast.LENGTH_LONG).show();
                return;
            } else {
                boolean deleteResult = file.delete();
                if (deleteResult) {
                    Log.v("删除不必要的文件", "删除成功");
                } else {
                    Log.v("删除不必要的文件", "删除失败");
                }
            }
        }
        saveFileTotalLength(FormatUtil.mainContext, (long) 0, "File_Length" + videoModel.getVideo_id());
        saveFileLength(FormatUtil.mainContext, (long) 0, "File_startOffset" + videoModel.getVideo_id());
        videoDownloadingModels.add(videoModel);
        saveVideoInfoJsons(FormatUtil.mainContext, "videoJsons");
        startDownloadVideo(FormatUtil.mainContext);
    }

    public static void removeDownloadedVideo(VideoModel videoModel) {
        synchronized (removeDownloadedVideo) {
            try {
                downloadLock.wait();
            } catch (Exception e) {
            }
            if (isContainsVideoModel(videoDownloadedModels, videoModel.getVideo_id())) {
                int videoIndex = -1;
                for (int i = 0; i < videoDownloadedModels.size(); i++) {
                    if (videoModel.getVideo_id() == videoDownloadedModels.get(i).getVideo_id()) {
                        videoIndex = i;
                    }
                }
                if (videoIndex > -1) {
                    videoDownloadedModels.remove(videoIndex);
                }
            } else if (isContainsVideoModel(videoDownloadingModels, videoModel.getVideo_id())) {
                int videoIndex = -1;
                for (int i = 0; i < videoDownloadingModels.size(); i++) {
                    if (videoModel.getVideo_id() == videoDownloadingModels.get(i).getVideo_id()) {
                        videoIndex = i;
                    }
                }
                if (videoIndex > -1) {
                    videoDownloadingModels.remove(videoIndex);
                }
            } else {

            }
            if (currentVideoModel != null) {
                if (currentVideoModel.getVideo_id() == videoModel.getVideo_id()) {
                    if (videoDownloadingModels.size() > 0) {
                        newVideoModel = videoDownloadingModels.get(0);
                    } else {
                        downloadThread.interrupt();
                        currentVideoModel = null;
                    }
                }
            }
            String currentFileAbsolutePath = getSavedVideoFilePath(videoModel.getVideo_id());
            File file = new File(currentFileAbsolutePath);
            if (file.exists()) {
                boolean deleteResult = file.delete();
                if (deleteResult) {
                    Log.v("删除不必要的文件", "删除成功");
                } else {
                    Log.v("删除不必要的文件", "删除失败");
                }
                saveFileTotalLength(FormatUtil.mainContext, (long) 0, "File_Length" + videoModel.getVideo_id());
                saveFileLength(FormatUtil.mainContext, (long) 0, "File_startOffset" + videoModel.getVideo_id());
            }
            saveVideoInfoJsons(FormatUtil.mainContext, "videoJsons");
            try {
                downloadLock.notify();
            } catch (Exception e) {
            }
        }
    }

    public static String getSavedVideoFilePath(int videoId) {
        return downloadDir + "/" + videoId + ".mp4";
    }

    public static void startDownloadVideo(final Context context) {
        loadVideoInfoJsons(context, "videoJsons");
        if (isRunning == false) {
            try {
                downloadThread = new Thread(new DownloadVideoRunnable());
                downloadThread.start();
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
                newVideoModel = currentVideoModel;
                currentVideoId = currentVideoModel.getVideo_id();
                currentUrlPath = currentVideoModel.getVideo_cdn_url();
                if (fileSize == 0) {
                    fileSize = getFileTotalLength(context, "File_Length" + currentVideoId);
                }
                currentFileAbsolutePath = downloadDir + "/" + currentVideoId + ".mp4";
                File file = new File(currentFileAbsolutePath);
                if (file.exists() && fileSize == 0) {
//                    VideoModel downloadedVideoModel = videoDownloadingModels.remove(0);
                    VideoModel downloadedVideoModel = videoDownloadingModels.remove(videoDownloadingModels.indexOf(currentVideoModel));
                    if (!isContainsVideoModel(videoDownloadedModels, downloadedVideoModel.getVideo_id())) {
                        videoDownloadedModels.add(downloadedVideoModel);
                    }
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
                    if (videoDownloadingModels.size() == 0) {
                        break;
                    }
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
                        if (videoDownloadingModels.size() == 0) {
                            break;
                        }
                        while ((len = bin.read(buffer)) != -1) {
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
                        boolean isNewVideoModel = isNewVideoModel();
                        if (isNewVideoModel) {
                            continue;
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
        synchronized (downloadLock) {
            try {
                removeDownloadedVideo.wait();
            } catch (Exception e) {
            }
            Context context = FormatUtil.mainContext;
            VideoModel downloadedVideoModel = null;
            for (VideoModel videoModel : videoDownloadingModels) {
                if (videoModel.getVideo_id() == currentVideoModel.getVideo_id()) {
                    downloadedVideoModel = videoDownloadingModels.remove(videoDownloadingModels.indexOf(videoModel));
                    if (!isContainsVideoModel(videoDownloadedModels, downloadedVideoModel.getVideo_id())) {
                        videoDownloadedModels.add(downloadedVideoModel);
                    }
                    break;
                }
            }
            saveFileTotalLength(context, (long) 0, "File_Length" + currentVideoId);
            saveFileLength(context, (long) 0, "File_startOffset" + currentVideoId);
            saveVideoInfoJsons(context, "videoJsons");
            if (onVideoDownloadListener != null) {
                onVideoDownloadListener.videoDownloadComplete(downloadedVideoModel.getVideo_id());
            }
            if (videoDownloadingModels.isEmpty()) {
                Log.v("all download complete", "all download complete");
                try {
                    removeDownloadedVideo.notify();
                } catch (Exception e) {
                }
                return false;
            }
            fileSize = 0;
            currentVideoModel = videoDownloadingModels.get(0);
            newVideoModel = currentVideoModel;
            currentVideoId = currentVideoModel.getVideo_id();
            currentUrlPath = currentVideoModel.getVideo_cdn_url();
            if (fileSize == 0) {
                fileSize = getFileTotalLength(context, "File_Length" + currentVideoId);
            }
            currentFileAbsolutePath = downloadDir + "/" + currentVideoId + ".mp4";
            Log.v("one download complete", "one download complete");
            try {
                removeDownloadedVideo.notify();
            } catch (Exception e) {
            }
            return true;
        }
    }

    public static boolean isNewVideoModel() {
        if (newVideoModel == currentVideoModel) {
            return false;
        } else {
            fileSize = 0;
            currentVideoModel = newVideoModel;
            if (newVideoModel == null) {
                newVideoModel = currentVideoModel;
            }
            currentVideoId = currentVideoModel.getVideo_id();
            currentUrlPath = currentVideoModel.getVideo_cdn_url();
            if (fileSize == 0) {
                fileSize = getFileTotalLength(FormatUtil.mainContext, "File_Length" + currentVideoId);
            }
            currentFileAbsolutePath = downloadDir + "/" + currentVideoId + ".mp4";
            return true;
        }
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
        editor.apply();
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
        editor.apply();
    }

    private static Long getFileTotalLength(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("videoDownload", Context.MODE_PRIVATE);
        return sp.getLong(key, 0);
    }

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
        editor.apply();
    }

    private static void loadVideoInfoJsons(Context context, String key) {
        if (videoDownloadingModels == null || videoDownloadedModels == null) {
            videoDownloadingModels = new LinkedList<>();
            videoDownloadedModels = new LinkedList<>();
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
    }

    public static LinkedList<VideoModel> getVideoDownloadModels() {
        LinkedList<VideoModel> videoDownloadModels = new LinkedList<>();
        for (VideoModel videoModel : videoDownloadedModels) {
            if (!isContainsVideoModel(videoDownloadModels, videoModel.getVideo_id())) {
                videoDownloadModels.add(videoModel);
            }
        }
        for (VideoModel videoModel : videoDownloadingModels) {
            if (!isContainsVideoModel(videoDownloadModels, videoModel.getVideo_id())) {
                videoDownloadModels.add(videoModel);
            }
        }
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

    public static boolean isContainsVideoModel(LinkedList<VideoModel> lists, int videoId) {
        for (VideoModel videoModel : lists) {
            if (videoModel.getVideo_id() == videoId) {
                return true;
            }
        }
        return false;
    }
}
