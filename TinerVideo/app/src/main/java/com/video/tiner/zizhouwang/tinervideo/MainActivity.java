package com.video.tiner.zizhouwang.tinervideo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.video.tiner.zizhouwang.tinervideo.CustomFragment.HomeFragment;
import com.video.tiner.zizhouwang.tinervideo.CustomFragment.MeFragment;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.downloadModules.VideoDownloadManager;

import io.fabric.sdk.android.Fabric;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "WTAXF6kcis9AkKFVwQrkjSZX4";
    private static final String TWITTER_SECRET = "cFsDxkvzymNYKWARr6knElfJMLkGsyCbC4kHMOFq4BA7z9IpjM";

    private int currentPage = 0;

    private FrameLayout windowFL;
    private FrameLayout tabContentFL;
    private HomeFragment homeFragment;
    private MeFragment meFragment = null;
    private Handler videoPlayHandler;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        FormatUtil.setStatusBarUpper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        new File(C.getBufferDir()).mkdirs();//创建预加载文件的文件夹
        tabContentFL = findViewById(R.id.tabContentFL);
        windowFL = (FrameLayout) tabContentFL.getParent();
        homeFragment = new HomeFragment();
        getFragmentManager().beginTransaction().replace(R.id.tabContentFL, homeFragment).commit();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        try {
            Fabric.with(this, new TweetUi(), new TweetComposer(), new Crashlytics(), new TwitterCore(authConfig), new Digits());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        LooperPrinter.start();

        final Window window = getWindow();
        FormatUtil.setHideVirtualKey(window);
        window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                FormatUtil.setHideVirtualKey(window);
            }
        });

        videoPlayHandler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (FormatUtil.isPlayingVideoView != null && FormatUtil.isUserSliding == false) {
                    Log.v("currentPosition", "" + FormatUtil.isPlayingVideoView.tinerMediaPlayer.getCurrentPosition());
                    float time = FormatUtil.isPlayingVideoView.tinerMediaPlayer.getCurrentPosition() / 1000.0f;
                    int duration = Math.round(time);
                    FormatUtil.isPlayingVideoView.currentTimeSB.setProgress(duration);
                    FormatUtil.isPlayingVideoView.currentTimeTV.setText(FormatUtil.integerToTimeStr(duration));
                }
                videoPlayHandler.postDelayed(this, 1 * 1000);
            }
        };
        videoPlayHandler.postDelayed(task, 1000);

        final AppCompatActivity thiss = this;

        FormatUtil.getShareView(this);

        callbackManager = CallbackManager.Factory.create();
        FormatUtil.shareDialog = new ShareDialog(this);
        FormatUtil.shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        FormatUtil.mainContext = this;
        VideoDownloadManager.startDownloadVideo(this);
        LinearLayout tabBottomView = findViewById(R.id.tabFL);
        FrameLayout.LayoutParams tabBottomViewLayout = (FrameLayout.LayoutParams) tabBottomView.getLayoutParams();
        tabBottomViewLayout.height = tabBottomViewLayout.height * FormatUtil.getScreenHeight(this) / 1280;
        tabBottomView.setLayoutParams(tabBottomViewLayout);
        final ImageView bottomHomeIV = tabBottomView.findViewById(R.id.bottomHomeIV);
        final TextView bottomHomeTV = tabBottomView.findViewById(R.id.bottomHomeTV);
        final ImageView bottomMeIV = tabBottomView.findViewById(R.id.bottomMeIV);
        final TextView bottomMeTV = tabBottomView.findViewById(R.id.bottomMeTV);
        FrameLayout bottomHomeFL = tabBottomView.findViewById(R.id.bottomHomeFL);
        bottomHomeFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage != 0) {
                    bottomHomeIV.setImageBitmap(FormatUtil.readBitMap(thiss, R.drawable.home_click));
                    bottomHomeTV.setTextColor(getResources().getColor(R.color.tabClickColor));
                    bottomMeIV.setImageBitmap(FormatUtil.readBitMap(thiss, R.drawable.me_button));
                    bottomMeTV.setTextColor(getResources().getColor(R.color.tabNotClickColor));
                    tabContentFL.removeView(meFragment.getSavedView());
                    tabContentFL.addView(homeFragment.getSavedView());
//                    getFragmentManager().beginTransaction().replace(R.id.tabContentFL, homeFragment).commit();
                    currentPage = 0;
                }
            }
        });
        FrameLayout bottomMeFL = tabBottomView.findViewById(R.id.bottomMeFL);
        bottomMeFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage != 1) {
                    bottomHomeIV.setImageBitmap(FormatUtil.readBitMap(thiss, R.drawable.home));
                    bottomHomeTV.setTextColor(getResources().getColor(R.color.tabNotClickColor));
                    bottomMeIV.setImageBitmap(FormatUtil.readBitMap(thiss, R.drawable.me_click_button));
                    bottomMeTV.setTextColor(getResources().getColor(R.color.tabClickColor));
                    if (meFragment == null) {
                        meFragment = new MeFragment();
                    }
                    tabContentFL.removeView(homeFragment.getSavedView());
                    tabContentFL.addView(meFragment.getSavedView());
//                    getFragmentManager().beginTransaction().replace(R.id.tabContentFL, meFragment).commit();
                    currentPage = 1;
                }
            }
        });
//        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }
}