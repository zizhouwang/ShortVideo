package com.video.tiner.zizhouwang.tinervideo.subview;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.google.android.gms.ads.AdRequest;
import com.video.tiner.zizhouwang.tinervideo.CustomFragment.TwitterShareFragment;
import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.adapter.DownloadedVideoListAdapter;
import com.video.tiner.zizhouwang.tinervideo.downloadModules.VideoDownloadManager;
import com.video.tiner.zizhouwang.tinervideo.model.VideoModel;

/**
 * Created by zizhouwang on 2018/6/8.
 */

public class TinerShareView extends FrameLayout {

    private View contentView;
    private FrameLayout bottomShareFL;
    private FrameLayout facebookShareFL;
    private FrameLayout twitterShareFL;
    private FrameLayout messageShareFL;
    public FrameLayout downloadFL;
    public FrameLayout deleteFL;
    private FrameLayout saveFL;
    public VideoModel bean;
    public String shareText;
    public String shareURL;
    public DownloadedVideoListAdapter downloadedVideoListAdapter;

    public TinerShareView(final AppCompatActivity context) {
        super(context);
        tinerShareInit(context, null);
    }

    public TinerShareView(final AppCompatActivity context, AttributeSet attrs) {
        super(context, attrs);
        tinerShareInit(context, attrs);
    }

    private void tinerShareInit(final AppCompatActivity context, AttributeSet attrs) {
        final TinerShareView thiss = this;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.customView);
            ta.recycle();
        }
        LayoutInflater mInflatet = LayoutInflater.from(context);
        contentView = mInflatet.inflate(R.layout.share_view, null);
        addView(contentView);

        bottomShareFL = findViewById(R.id.bottomShareFL);
        bottomShareFL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //什么也不做
            }
        });

        facebookShareFL = findViewById(R.id.shareFacebookFL);
        facebookShareFL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout windowFL = (FrameLayout) thiss.getParent();
                windowFL.removeView(thiss);
//                if (ShareDialog.canShow(ShareVideoContent.class)) {
//                    Uri videoFileUri = Uri.parse(shareURL);
//                    ShareVideo video = new ShareVideo.Builder()
//                            .setLocalUrl(videoFileUri)
//                            .build();
//                    ShareVideoContent content = new ShareVideoContent.Builder()
//                            .setVideo(video)
//                            .build();
//                    FormatUtil.shareDialog.show(content);
//                } else if (ShareDialog.canShow(ShareLinkContent.class)) {
//                    ShareLinkContent content = new ShareLinkContent.Builder()
//                            .setContentUrl(Uri.parse(shareURL))
//                            .build();
//                    FormatUtil.shareDialog.show(content);
//                }

                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(shareURL))
                        .build();
                FormatUtil.shareDialog.show(content);

            }
        });
        twitterShareFL = findViewById(R.id.shareTwitterFL);
        twitterShareFL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout windowFL = (FrameLayout) thiss.getParent();
                windowFL.removeView(thiss);
                TwitterShareFragment twitterShareFragment = new TwitterShareFragment();
                twitterShareFragment.url = "https://twitter.com/intent/tweet?text=Multi-platform+video:+https://play.google.com/store/apps/details?id=com.poptiner.zizhouwang+&url=" + FormatUtil.toURLEncoded(shareURL);
                FragmentManager fm = context.getFragmentManager();
                FragmentTransaction beginTransaction = fm.beginTransaction();
                beginTransaction.replace(windowFL.getId(), twitterShareFragment).addToBackStack(null).commit();
            }
        });
        messageShareFL = findViewById(R.id.shareMessageFL);
        messageShareFL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout windowFL = (FrameLayout) thiss.getParent();
                windowFL.removeView(thiss);
                Uri smsToUri = Uri.parse("smsto:");
                Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                mIntent.putExtra("sms_body", "Multi-platform video: https://play.google.com/store/apps/details?id=com.poptiner.zizhouwang\n" + shareURL);
                context.startActivity(mIntent);
            }
        });
        downloadFL = findViewById(R.id.downloadFL);
        downloadFL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout windowFL = (FrameLayout) thiss.getParent();
                windowFL.removeView(thiss);
                VideoDownloadManager.addNeedDownloadVideo(bean);
                Toast.makeText(FormatUtil.mainContext, "视频开始下载", Toast.LENGTH_LONG).show();
            }
        });
//        saveFL = findViewById(R.id.saveFL);
//        saveFL.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FrameLayout windowFL = (FrameLayout) thiss.getParent();
//                windowFL.removeView(thiss);
//            }
//        });
        deleteFL = findViewById(R.id.deleteFL);
        deleteFL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout windowFL = (FrameLayout) thiss.getParent();
                windowFL.removeView(thiss);
                VideoDownloadManager.removeDownloadedVideo(bean);
                if (downloadedVideoListAdapter != null) {
                    downloadedVideoListAdapter.removeOneItem(bean.getVideo_id());
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
//        layoutParams.height = FormatUtil.getScreenHeightNoStatusBar(this.getContext()) - 20;
//        this.setLayoutParams(layoutParams);
//
//        layoutParams = contentView.getLayoutParams();
//        layoutParams.height = FormatUtil.getScreenHeightNoStatusBar(this.getContext()) - 20;
//        contentView.setLayoutParams(layoutParams);
    }
}
