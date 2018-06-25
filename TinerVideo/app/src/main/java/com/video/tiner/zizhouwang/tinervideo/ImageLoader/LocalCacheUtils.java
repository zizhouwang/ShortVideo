package com.video.tiner.zizhouwang.tinervideo.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by zizhouwang on 2018/5/31.
 */

class LocalCacheUtils {
//    private static final String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TinerImageCache";
    private static final String CACHE_PATH = FormatUtil.mainContext.getCacheDir() + "/TinerImageCache";

    /**
     * 从本地读取图片
     *
     * @param url
     */
    public Bitmap getBitmapFromLocal(String url) {
        String fileName = null;//把图片的url当做文件名,并进行MD5加密
        try {
            fileName = FormatUtil.md5(url);
            File file = new File(CACHE_PATH, fileName);

            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从网络获取图片后,保存至本地缓存
     *
     * @param url
     * @param bitmap
     */
    public void setBitmapToLocal(String url, Bitmap bitmap) {
        try {
            String fileName = FormatUtil.md5(url);//把图片的url当做文件名,并进行MD5加密
            File file = new File(CACHE_PATH, fileName);

            //通过得到文件的父文件,判断父文件是否存在
            boolean isMkdirs;
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                isMkdirs = parentFile.mkdirs();
                Log.v("isMkdirs", "" + isMkdirs);
            }

            //把图片保存至本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
