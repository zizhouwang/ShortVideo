package com.video.tiner.zizhouwang.tinervideo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.video.tiner.zizhouwang.tinervideo.CustomFragment.HomeFragment;
import com.video.tiner.zizhouwang.tinervideo.CustomFragment.MeFragment;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.checkUICaton.LooperPrinter;
import com.video.tiner.zizhouwang.tinervideo.downloadModules.VideoDownloadManager;

import io.fabric.sdk.android.Fabric;

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
        FormatUtil.setStatusBarUpper(this);
        super.onCreate(savedInstanceState);
//        MobileAds.initialize(this, "ca-app-pub-2413503271886460~2547561895");
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        FormatUtil.mInterstitialAd = new InterstitialAd(this);
//        FormatUtil.mInterstitialAd.setAdUnitId("ca-app-pub-2413503271886460/2902785115");
        FormatUtil.mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        FormatUtil.mInterstitialAd.loadAd(new AdRequest.Builder().build());
        UMConfigure.init(getApplicationContext(), "5b56a706a40fa34b8100014f", "GOOGLE_PLAY", UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setLogEnabled(true);
        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
        setContentView(R.layout.activity_main);
        tabContentFL = findViewById(R.id.tabContentFL);
        windowFL = (FrameLayout) tabContentFL.getParent();
        FormatUtil.mainContext = this;
        VideoDownloadManager.startDownloadVideo(this);
        homeFragment = new HomeFragment();
        FormatUtil.homeFragment = homeFragment;

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
                if (FormatUtil.isPlayingVideoView != null && !FormatUtil.isUserSliding) {
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
                    FormatUtil.pauseCurrentVideo();
                    bottomHomeIV.setImageBitmap(FormatUtil.readBitMap(thiss, R.drawable.home_click));
                    bottomHomeTV.setTextColor(getResources().getColor(R.color.tabClickColor));
                    bottomMeIV.setImageBitmap(FormatUtil.readBitMap(thiss, R.drawable.me_button));
                    bottomMeTV.setTextColor(getResources().getColor(R.color.tabNotClickColor));
                    tabContentFL.removeView(meFragment.getSavedView());
                    homeFragment.getSavedView().setVisibility(View.VISIBLE);
//                    getFragmentManager().beginTransaction().replace(R.id.tabContentFL, homeFragment).commit();
                    currentPage = 0;
                } else {
//                    homeFragment.xListViews.get(homeFragment.videoViewPager.getCurrentItem()).setSelection(0);
                    homeFragment.xListViews.get(homeFragment.videoViewPager.getCurrentItem()).smoothScrollToPosition(0);
                }
            }
        });
        FrameLayout bottomMeFL = tabBottomView.findViewById(R.id.bottomMeFL);
        bottomMeFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage != 1) {
                    FormatUtil.pauseCurrentVideo();
                    bottomHomeIV.setImageBitmap(FormatUtil.readBitMap(thiss, R.drawable.home));
                    bottomHomeTV.setTextColor(getResources().getColor(R.color.tabNotClickColor));
                    bottomMeIV.setImageBitmap(FormatUtil.readBitMap(thiss, R.drawable.me_click_button));
                    bottomMeTV.setTextColor(getResources().getColor(R.color.tabClickColor));
                    if (meFragment == null) {
                        meFragment = new MeFragment();
                    }
                    tabContentFL.addView(meFragment.getSavedView());
                    homeFragment.getSavedView().setVisibility(View.INVISIBLE);
//                    getFragmentManager().beginTransaction().replace(R.id.tabContentFL, meFragment).commit();
                    currentPage = 1;
                }
            }
        });
//        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        FormatUtil.mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.v("onAdLoaded", "");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.v("onAdFailedToLoad", "");
            }

            @Override
            public void onAdOpened() {
                Log.v("onAdOpened", "");
            }

            @Override
            public void onAdLeftApplication() {
                Log.v("onAdLeftApplication", "");
            }

            @Override
            public void onAdClosed() {
                Log.v("onAdClosed", "");
                try {
                    if (FormatUtil.isVideoPlayerAd && FormatUtil.isPlayingVideoView != null) {
                        FormatUtil.isPlayingVideoView.videoPlay();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        FormatUtil.pauseCurrentVideo();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }
}