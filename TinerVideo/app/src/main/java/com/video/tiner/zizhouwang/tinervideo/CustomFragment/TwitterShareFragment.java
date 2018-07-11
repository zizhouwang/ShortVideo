package com.video.tiner.zizhouwang.tinervideo.CustomFragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerNavView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/6/10.
 */

public class TwitterShareFragment extends SubFragment {

//    private AnimatorSet slideInSet;
//    private AnimatorSet slideOutSet;

    public String url;
    public FrameLayout twitterShareFL;
    public WebView twitterShareWV;
    public ProgressBar twitterSharePB;

    public TwitterShareFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout view = (FrameLayout) inflater.inflate(R.layout.twitter_share_layout, null);
        twitterShareFL = view.findViewById(R.id.twitterShareFL);
        twitterShareWV = view.findViewById(R.id.twitterShareWV);
        twitterSharePB = view.findViewById(R.id.twitterSharePB);
        final WebChromeClient webChromeClient = new WebChromeClient() {
            //获取网页标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.i("ansen", "网页标题:" + title);
            }

            //加载进度回调
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                twitterSharePB.setProgress(newProgress);
            }
        };
        final WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {//页面加载完成
                twitterSharePB.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
                twitterSharePB.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

        };
        final TwitterShareFragment thiss = this;
        TinerNavView tinerNavView = FormatUtil.getTinerNavView((AppCompatActivity) view.getContext(), view, twitterShareFL, false);
        tinerNavView.navTextView.setText("");
        tinerNavView.navTextView.setBackgroundColor(Color.argb(0xff, 0xfa, 0xfa, 0xfa));
        tinerNavView.navTextView.setTextColor(Color.argb(0xff, 0x00, 0x00, 0x00));
        super.onCreateView(inflater, container, savedInstanceState, view, tinerNavView);
        slideInSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                twitterShareWV.loadUrl(url);
                WebSettings webSettings = twitterShareWV.getSettings();
                twitterShareWV.setWebChromeClient(webChromeClient);
                twitterShareWV.setWebViewClient(webViewClient);
                webSettings.setJavaScriptEnabled(true);//允许使用js

                /**
                 * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
                 * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
                 * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
                 * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
                 */
//                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.

                //支持屏幕缩放
                webSettings.setSupportZoom(true);
                webSettings.setBuiltInZoomControls(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return view;
    }
}
