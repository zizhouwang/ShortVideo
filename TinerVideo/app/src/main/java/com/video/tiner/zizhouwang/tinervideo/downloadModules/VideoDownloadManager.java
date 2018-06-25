package com.video.tiner.zizhouwang.tinervideo.downloadModules;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

/**
 * Created by zizhouwang on 2018/6/14.
 */

public class VideoDownloadManager {

    public static Boolean isStop = false;
    private static long fileSize;
    private static final String downloadDir = FormatUtil.mainContext.getCacheDir() + "/TinerVideoCache";
    private static LinkedList<String> videoInfoJsons = null;
    private static Handler handler = new Handler();
    private static Boolean isRunning = false;

    public static void addNeedDownloadVideo(VideoModel videoModel) {
        Gson gson = new Gson();
        String videoJsonStr = gson.toJson(videoModel);
        if (videoInfoJsons.contains(videoJsonStr)) {
            Toast.makeText(FormatUtil.mainContext, "文件正在下载", Toast.LENGTH_LONG).show();
            return;
        }
        String fileAbsolutePath = downloadDir + "/" + videoModel.getVideo_id() + ".mp4";
        File file = new File(fileAbsolutePath);
        if (file.exists()) {
            Toast.makeText(FormatUtil.mainContext, "文件已存在", Toast.LENGTH_LONG).show();
            return;
        }
        videoInfoJsons.add(videoJsonStr);
        saveVideoInfoJsons(FormatUtil.mainContext, "videoJsons");
        startDownloadVideo(FormatUtil.mainContext);
    }

    public static void startDownloadVideo(final Context context) {
        if (videoInfoJsons == null) {
            videoInfoJsons = new LinkedList<>();
            loadVideoInfoJsons(context, "videoJsons");
        }
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
            if (videoInfoJsons.size() == 0) {
                isRunning = false;
                return;
            }
            Gson gson = new Gson();
            String videoInfoJson;
            VideoModel videoModel;
            int videoId;
            String urlPath;
            String fileAbsolutePath;
            while (true) {
                videoInfoJson = videoInfoJsons.get(0);
                videoModel = gson.fromJson(videoInfoJson, VideoModel.class);
                videoId = videoModel.getVideo_id();
                urlPath = videoModel.getVideo_cdn_url();
                if (fileSize == 0) {
                    fileSize = getFileTotalLength(context, "File_Length" + videoId);
                }
                fileAbsolutePath = downloadDir + "/" + videoId + ".mp4";
                File file = new File(fileAbsolutePath);
                if (file.exists() && fileSize == 0) {
                    videoInfoJsons.remove(0);
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
                    long startOffset = getFileLength(context, "File_startOffset" + videoId);
                    url = new URL(urlPath);
                    if (fileSize == 0) {
                        //获取HttpURLConnection对象
                        Log.v("start get file size", "start get file size");
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        //设置请求方式
                        httpURLConnection.setRequestMethod("HEAD");
                        httpURLConnection.connect();
                        if (httpURLConnection.getResponseCode() == 200) {
                            fileSize = httpURLConnection.getContentLength();
                            saveFileTotalLength(context, fileSize, "File_Length" + videoId);
                            Log.v("got file size", "got file size");
                            if (fileSize > 0 && fileSize < 40960) {
                                fileSize = Long.MAX_VALUE;
                            }
                        } else {
                            continue;
                        }
                    }
                    if (startOffset > 0 && startOffset >= fileSize) {
                        Boolean isHavingMoreNeedDownloadVideo = videoDownloadComplete(videoId, videoInfoJson, videoModel, urlPath, fileAbsolutePath);
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
                    httpURLConnection.connect();
                    Log.v("receive http", "receive http fileSize:" + fileSize + " startOffset:" + startOffset);
                    if (httpURLConnection.getResponseCode() == 206) {
                        //if startOffset ==0 的时候，你就要把你的文件大小保存起来
                        //获取文件的大小httpURLConnection.getContentLength();
                        int fileSize1 = httpURLConnection.getContentLength();
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
                        //fileAbsolutePath 是你具体的文件路径
                        RandomAccessFile randomAccessFile = new RandomAccessFile(fileAbsolutePath, "rwd");
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
                            saveFileLength(context, startOffset, "File_startOffset" + videoId);
                        }
                    } else {
                        Boolean isHavingMoreNeedDownloadVideo = videoDownloadComplete(videoId, videoInfoJson, videoModel, urlPath, fileAbsolutePath);
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

    private static boolean videoDownloadComplete(int videoId, String videoInfoJson, VideoModel videoModel, String urlPath, String fileAbsolutePath) {
        Gson gson = new Gson();
        Context context = FormatUtil.mainContext;
        videoInfoJsons.remove(0);
        saveFileTotalLength(context, (long) 0, "File_Length" + videoId);
        saveFileLength(context, (long) 0, "File_startOffset" + videoId);
        saveVideoInfoJsons(context, "videoJsons");
        if (videoInfoJsons.isEmpty()) {
            Log.v("all download complete", "all download complete");
//            break;
            return false;
        }
        fileSize = 0;
        videoInfoJson = videoInfoJsons.get(0);
        videoModel = gson.fromJson(videoInfoJson, VideoModel.class);
        videoId = videoModel.getVideo_id();
        urlPath = videoModel.getVideo_cdn_url();
        if (fileSize == 0) {
            fileSize = getFileTotalLength(context, "File_Length" + videoId);
        }
        fileAbsolutePath = downloadDir + "/" + videoId + ".mp4";
        Log.v("one download complete", "one download complete");
//        continue;
        return true;
    }

    private static void downloadVideo(final Context context, final int videoId, final String urlPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
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

    private static void saveVideoInfoJsons(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("videoDownload", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        JSONArray videoJsons = new JSONArray();
        for (int i = 0; i < videoInfoJsons.size(); i++) {
            videoJsons.put(videoInfoJsons.get(i));
        }
        editor.putString(key, videoJsons.toString());
        editor.commit();
    }

    private static void loadVideoInfoJsons(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("videoDownload", Context.MODE_PRIVATE);
        try {
            JSONArray videoJsons = new JSONArray(sp.getString(key, "[]"));
            for (int i = 0; i < videoJsons.length(); i++) {
                videoInfoJsons.add(videoJsons.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
