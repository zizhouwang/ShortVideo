package com.video.tiner.zizhouwang.tinervideo.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Scroller;

import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.InterstitialAd;
import com.video.tiner.zizhouwang.tinervideo.CustomFragment.HomeFragment;
import com.video.tiner.zizhouwang.tinervideo.CustomUI.BaseFrameLayout;
import com.video.tiner.zizhouwang.tinervideo.CustomUI.BaseListView;
import com.video.tiner.zizhouwang.tinervideo.CustomUI.EquScaImageView;
import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.adapter.VideoListAdapter;
import com.video.tiner.zizhouwang.tinervideo.model.XmlAttrModel;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerNavView;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerShareView;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerVideoView;
import com.video.tiner.zizhouwang.tinervideo.xListView.XListView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Dictionary;
import java.util.Locale;

/**
 * Created by zizhouwang on 2018/5/31.
 */

public class FormatUtil {

    public static Boolean isUserSliding = false;
    public static final String getThumbImg = "getThumbImg";
    private static TinerShareView shareView = null;
    public static Dictionary userInfo = null;
    public static ShareDialog shareDialog;
    public static Context mainContext;
    private static HttpProxyCacheServer proxy;
    public static Scroller mScroller;
    public static HomeFragment homeFragment;

    public static XListView homeListView;
    public static BaseListView currentListView;
    public static TinerVideoView waitPlayingVideoView;
    public static TinerVideoView isPlayingVideoView;
    public static TinerVideoView currentItemView = null;

    private static FrameLayout videoGuideFrameLayout = null;

    public static int waitAdCount = 10;
    public static int adCount = waitAdCount;
    public static InterstitialAd mInterstitialAd;
    public static boolean isVideoPlayerAd = false;

    public static int getCurrentItemIndex() {
        return getCurrentItemIndex(currentItemView);
    }

    public static int getCurrentItemIndex(TinerVideoView tinerVideoView) {
        for (int i = 0; i < currentListView.mTotalItemViews.size(); i++) {
            if (currentListView.mTotalItemViews.get(i).tinerInteView == tinerVideoView) {
                return i;
            }
        }
        return -1;
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String integerToTimeStr(int duration) {
        int totalVideoMin = duration / 60;
        int totalVideoSec = duration % 60;
        int totalVideoHour = totalVideoMin / 60;
        totalVideoMin = totalVideoMin % 60;
        if (totalVideoHour == 0) {
            return String.format("%02d:%02d", totalVideoMin, totalVideoSec);
        } else {
            return String.format("%02d:%02d:%02d", totalVideoHour, totalVideoMin, totalVideoSec);
        }
    }

    public static float equalScalingWdith(Context context, float width) {
        if (width < 0) {
            return width;
        }
        float screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        return width * screenWidth / 1080.0f;
    }

    public static float equalScalingHeight(Context context, float height) {
        if (height < 0) {
            return height;
        }
        float screenHeight = FormatUtil.getScreenHeight(context);
        return height * screenHeight / 1920.0f;
    }

    public static void equalScalingWithView(View view, XmlAttrModel xmlAttrModel) {
        Context context = view.getContext();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = (int) (FormatUtil.equalScalingWdith(context, xmlAttrModel.width));
        layoutParams.height = (int) (FormatUtil.equalScalingHeight(context, xmlAttrModel.height));
        if (view.getClass().equals(EquScaImageView.class)) {
            if (layoutParams.width > layoutParams.height) {
                layoutParams.width = layoutParams.height;
            } else {
                layoutParams.height = layoutParams.width;
            }
        }
        layoutParams.setMargins((int) (FormatUtil.equalScalingWdith(context, xmlAttrModel.marginLeft)), (int) (FormatUtil.equalScalingHeight(context, xmlAttrModel.marginTop)), (int) (FormatUtil.equalScalingWdith(context, xmlAttrModel.marginRight)), (int) (FormatUtil.equalScalingHeight(context, xmlAttrModel.marginBottom)));
        view.setLayoutParams(layoutParams);
        view.setPadding((int) (FormatUtil.equalScalingWdith(context, xmlAttrModel.paddingLeft)), (int) (FormatUtil.equalScalingHeight(context, xmlAttrModel.paddingTop)), (int) (FormatUtil.equalScalingWdith(context, xmlAttrModel.paddingRight)), (int) (FormatUtil.equalScalingHeight(context, xmlAttrModel.paddingBottom)));
    }

    public static TinerShareView getShareView(final AppCompatActivity context) {
        if (shareView == null) {
            shareView = new TinerShareView(context);
            shareView.findViewById(R.id.shareFL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FrameLayout windowFL = (FrameLayout) shareView.getParent();
                    windowFL.removeView(shareView);
                }
            });
        }
        return shareView;
    }

    public static TinerNavView getTinerNavView(final AppCompatActivity context, ViewGroup vg, View contentView, Boolean isHideBackButton) {
        TinerNavView tinerNavView = new TinerNavView(context);
        if (isHideBackButton == true) {
            tinerNavView.backImageView.setVisibility(View.INVISIBLE);
        } else {
            tinerNavView.backImageView.setVisibility(View.VISIBLE);
        }
        vg.addView(tinerNavView);
        int statusBarHeight = FormatUtil.getStatusBarHeight(context);
        int height = FormatUtil.getNavHeight(context) + statusBarHeight;
        tinerNavView.height = height;
        FormatUtil.changeViewTopMargin(contentView);
        FrameLayout navContentFL = tinerNavView.findViewById(R.id.navContentFL);
        FrameLayout.LayoutParams navContentLayoutParams = (FrameLayout.LayoutParams) navContentFL.getLayoutParams();
        navContentLayoutParams.topMargin = statusBarHeight;
        navContentFL.setLayoutParams(navContentLayoutParams);
        return tinerNavView;
    }

    public static void changeViewTopMargin(View view) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
        lp.topMargin = FormatUtil.getNavHeight((AppCompatActivity) view.getContext()) + FormatUtil.getStatusBarHeight(view.getContext());
        view.setLayoutParams(lp);
    }

    public static int getNavHeight(final AppCompatActivity context) {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = 100 * width / 720;
        return height;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    public static int getVirtualBarHeight(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    public static void setHideVirtualKey(Window window) {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = window.getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = window.getDecorView();
            int systemUiVIsi = decorView.getSystemUiVisibility();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    ;
            systemUiVIsi |= uiOptions;
            decorView.setSystemUiVisibility(systemUiVIsi);
        }
    }

    public static Intent getTwitterIntent(Context ctx, String shareText) {
        Intent shareIntent;

//        if(doesPackageExist(ctx, "com.twitter.android"))
        if (true) {
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setClassName("com.twitter.android",
                    "com.twitter.android.PostActivity");
            shareIntent.setType("text/*");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            return shareIntent;
        } else {
            String tweetUrl = "https://twitter.com/intent/tweet?text=" + shareText;
            Uri uri = Uri.parse(tweetUrl);
            shareIntent = new Intent(Intent.ACTION_VIEW, uri);
            return shareIntent;
        }
    }

    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }

        try {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static ViewGroup getWindow(AppCompatActivity context) {
        return (ViewGroup) (context.findViewById(Window.ID_ANDROID_CONTENT));
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        }
        return result;
    }

    @SuppressLint("RestrictedApi")
    public static void hideSupportActionBar(Context context) {
        AppCompatActivity appContext = (AppCompatActivity) context;
        Window window = appContext.getWindow();
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = window.getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = window.getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @SuppressLint("RestrictedApi")
    public static void showSupportActionBar(Context context) {
        AppCompatActivity appContext = (AppCompatActivity) context;
        Window window = appContext.getWindow();
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = window.getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = window.getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static void setStatusBarColor(AppCompatActivity context, int statusColor) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = context.getWindow();
            //取消状态栏透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(statusColor);
            //设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            //让view不根据系统窗口来调整自己的布局
            ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                ViewCompat.setFitsSystemWindows(mChildView, false);
                ViewCompat.requestApplyInsets(mChildView);
            }
        }
    }

    public static void setStatusBarUpper(Context context) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            AppCompatActivity appContext = (AppCompatActivity) context;
            Window window = appContext.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            ViewGroup mContentView = appContext.findViewById(Window.ID_ANDROID_CONTENT);
            View statusBarView = mContentView.getChildAt(0);
            //移除假的 View
            if (statusBarView != null && statusBarView.getLayoutParams() != null && statusBarView.getLayoutParams().height == FormatUtil.getStatusBarHeight(context)) {
                mContentView.removeView(statusBarView);
            }
            //不预留空间
            if (mContentView.getChildAt(0) != null) {
                ViewCompat.setFitsSystemWindows(mContentView.getChildAt(0), false);
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            AppCompatActivity appContext = (AppCompatActivity) context;
            Window window = appContext.getWindow();
            //设置透明状态栏,这样才能让 ContentView 向上
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
//            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(colorId);

            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            ViewGroup mContentView = appContext.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null && mChildView.getLayoutParams() != null && mChildView.getLayoutParams().height == FormatUtil.getStatusBarHeight(context)) {
                mContentView.removeView(mChildView);
            }
            int identifier = appContext.getResources().getIdentifier("statusBarBackground", "id", "android");
            View anoStatusBarView = window.findViewById(identifier);
            if (anoStatusBarView != null && anoStatusBarView.getLayoutParams() != null) {
                ((ViewGroup) anoStatusBarView.getParent()).removeView(anoStatusBarView);
            }
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
                ViewCompat.setFitsSystemWindows(mChildView, false);
            }
        }
    }

    public static BitmapDrawable createBitmapDrawable(Context context, int imageID) {
        InputStream is = null;
        try {
            Log.d("", "Begin createBitmapDrawable");
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            is = context.getResources().openRawResource(imageID);
            //decodeStream直接调用JNI>>nativeDecodeAsset()来完成decode，无需再使用java层的createBitmap，节省了java层的空间
            Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
            BitmapDrawable bd = new BitmapDrawable(context.getResources(), bm);
            Log.d("", "finish createBitmapDrawable---BitmapDrawable " + bd == null ? "==null" : "!=null");
            return bd;
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    public static HttpProxyCacheServer getProxy() {
        return proxy == null ? (proxy = FormatUtil.newProxy(FormatUtil.mainContext)) : proxy;
    }

    private static HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context).maxCacheSize(200 * 1024 * 1024).build();
    }

    public static ProgressBar getLoadVideoPB() {
        ProgressBar loadVideoPB = new ProgressBar(FormatUtil.mainContext);
        loadVideoPB.setBackgroundColor(FormatUtil.mainContext.getResources().getColor(R.color.clearColor));
        loadVideoPB.setIndeterminate(false);
        loadVideoPB.setIndeterminateDrawable(FormatUtil.mainContext.getResources().getDrawable(R.drawable.anim));
        return loadVideoPB;
    }

    public static int dpToPx(float dpValue) {
        final float scale = FormatUtil.mainContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int pxToDp(float pxValue) {
        final float scale = FormatUtil.mainContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private static final int COMPLEX_UNIT_PX = 1;
    private static final int COMPLEX_UNIT_DIP = 2;
    private static final int COMPLEX_UNIT_SP = 3;
    private static final int COMPLEX_UNIT_PT = 4;
    private static final int COMPLEX_UNIT_IN = 5;
    private static final int COMPLEX_UNIT_MM = 6;

    public static float applyDimension(int unit, float value, DisplayMetrics metrics) {
        switch (unit) {
            case COMPLEX_UNIT_PX: // 转换为px(像素)值
                return value;
            case COMPLEX_UNIT_DIP: // 转换为dp(密度)值
                return value * metrics.density;
            case COMPLEX_UNIT_SP: // 转换为sp(与刻度无关的像素)值
                return value * metrics.scaledDensity;
            case COMPLEX_UNIT_PT: // 转换为pt(磅)值
                return value * metrics.xdpi * (1.0f / 72);
            case COMPLEX_UNIT_IN: // 转换为in(英寸)值
                return value * metrics.xdpi;
            case COMPLEX_UNIT_MM: // 转换为mm(毫米)值
                return value * metrics.xdpi * (1.0f / 25.4f);
        }
        return 0;
    }

    private int moveCount = 0;
    public boolean isCorrecting = false;

    public void moveViewByMargin(final FrameLayout frameLayout, final int leftMargin, final int topMargin, final int rightMargin, final int bottomMargin, int duration, final boolean isClickedView) {
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
        final int startLeftMargin = layoutParams.leftMargin;
        final int startTopMargin = layoutParams.topMargin;
        final int startRightMargin = layoutParams.rightMargin;
        final int startBottomMargin = layoutParams.bottomMargin;
        if (duration == 0) {
            layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            frameLayout.setLayoutParams(layoutParams);
        } else {
//            if (isClickedView) {
//                adjustVideoListViews(duration, isClickedView);
//            }
            moveCount = 0;
            final int frameNumber = 10;
            final int times = duration / frameNumber;
            final Handler handler = new Handler(Looper.getMainLooper());
            final Runnable changeCorrectRunnable = new Runnable() {
                @Override
                public void run() {
                    isCorrecting = false;
                }
            };
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    moveCount++;
                    int leftMargenResult = startLeftMargin + (leftMargin - startLeftMargin) * moveCount / times;
                    int topMargenResult = startTopMargin + (topMargin - startTopMargin) * moveCount / times;
                    int rightMargenResult = startRightMargin + (rightMargin - startRightMargin) * moveCount / times;
                    int bottomMargenResult = startBottomMargin + (bottomMargin - startBottomMargin) * moveCount / times;
                    layoutParams.setMargins(leftMargenResult, topMargenResult, rightMargenResult, bottomMargenResult);
                    frameLayout.setLayoutParams(layoutParams);
                    if (moveCount < times) {
                        handler.postDelayed(this, frameNumber);
                    } else {
                        layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                        frameLayout.setLayoutParams(layoutParams);
                        if (isClickedView && !currentItemView.isNeedPlay) {
                            currentItemView.videoStart();
                        }
                        smoothScrollListView(isClickedView);
                        handler.postDelayed(changeCorrectRunnable, 100);
                    }
                }
            };
            handler.postDelayed(runnable, frameNumber);
        }
    }

    private static int staticMoveCount = 0;

    public static void adjustVideoListViews(int duration, final boolean isClickedView) {
        for (VideoListAdapter.ViewHolder videoModel :
                currentListView.mTotalItemViews) {
            BaseFrameLayout videoFL = videoModel.tinerInteView.videoFL;
            final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoFL.getLayoutParams();
            videoModel.startLeftMargin = layoutParams.leftMargin;
            videoModel.startTopMargin = layoutParams.topMargin;
            videoModel.startRightMargin = layoutParams.rightMargin;
            videoModel.startBottomMargin = layoutParams.bottomMargin;
        }
        staticMoveCount = 0;
        final int frameNumber = 10;
        final int times = duration / frameNumber;
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (VideoListAdapter.ViewHolder videoModel :
                        currentListView.mTotalItemViews) {
                    BaseFrameLayout videoFL = videoModel.tinerInteView.videoFL;
                    final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoFL.getLayoutParams();
                    staticMoveCount++;
//                    int leftMargenResult = videoModel.startLeftMargin + (videoModel.startLeftMargin - videoModel.startLeftMargin) * staticMoveCount / times;
                    int topMargenResult = videoModel.startTopMargin + (videoModel.tinerInteView.fullScreenCorrectedTopMargin - videoModel.startTopMargin) * staticMoveCount / times;
//                    int rightMargenResult = videoModel.startRightMargin + (videoModel.startRightMargin - videoModel.startRightMargin) * staticMoveCount / times;
                    int bottomMargenResult = videoModel.startBottomMargin + (videoModel.tinerInteView.fullScreenCorrectedBottomMargin - videoModel.startBottomMargin) * staticMoveCount / times;
                    layoutParams.setMargins(videoModel.startLeftMargin, topMargenResult, videoModel.startRightMargin, bottomMargenResult);
                    videoFL.setLayoutParams(layoutParams);
                    if (staticMoveCount < times) {
                        handler.postDelayed(this, frameNumber);
                    } else {
                        layoutParams.setMargins(videoModel.startLeftMargin, videoModel.tinerInteView.fullScreenCorrectedTopMargin, videoModel.startRightMargin, videoModel.tinerInteView.fullScreenCorrectedBottomMargin);
                        videoFL.setLayoutParams(layoutParams);
                        if (currentItemView == videoModel.tinerInteView) {
                            currentItemView.videoStart();
                        }
                        smoothScrollListView(isClickedView);
                        handler.postDelayed(videoModel.tinerInteView.changeCorrectRunnable, 100);
                    }
                }
            }
        };

        handler.postDelayed(runnable, frameNumber);
    }

    public static boolean isCouldSlideVideoListView() {
        for (int i = 0; i < currentListView.mTotalItemViews.size(); i++) {
            if (currentListView.mTotalItemViews.get(i).tinerInteView.formatUtil.isCorrecting) {
                return true;
            }
        }
        return false;
    }

    public static void smoothScrollListView(boolean isClickedView) {
        if (Build.VERSION.SDK_INT < 230) {
            FormatUtil.currentListView.smoothScrollToPositionFromTop(currentItemView.customPosition + 1, 100, 0);
        }
    }

    public static String conversionNumber(float number, boolean isNeedInt) {
        String unit = "";
        if (number < 1000000 && number > 1000) {
            unit = "KB";
            number = number / 1000.0f;
        } else if (number >= 1000000) {
            unit = "MB";
            number = number / 1000000.0f;
        } else {
            unit = "B";
        }
        if (isNeedInt) {
            return String.format(Locale.US, "%d%s/s", (int) number, unit);
        } else {
            return String.format(Locale.US, "%.1f%s", number, unit);
        }
    }

    public static void showVideoGuide() {
        SharedPreferences sp = mainContext.getSharedPreferences("isShowVideoGuide", Context.MODE_PRIVATE);
        boolean isShowVideoGuide = sp.getBoolean("isShowVideoGuide", false);
        if (!isShowVideoGuide && videoGuideFrameLayout == null) {
            FrameLayout tabContentFL = ((AppCompatActivity) mainContext).findViewById(R.id.tabContentFL);
            final FrameLayout windowFL = (FrameLayout) tabContentFL.getParent();
            LayoutInflater mInflater = LayoutInflater.from(mainContext);
            videoGuideFrameLayout = (FrameLayout) mInflater.inflate(R.layout.video_guide_layout, null);
            windowFL.addView(videoGuideFrameLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            videoGuideFrameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    windowFL.removeView(videoGuideFrameLayout);
                    videoGuideFrameLayout = null;
                }
            });
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isShowVideoGuide", true);
            editor.apply();
        }
    }

    public static void pauseCurrentVideo() {
        if (FormatUtil.isPlayingVideoView != null) {
            FormatUtil.isPlayingVideoView.videoPause();
        }
    }
}
