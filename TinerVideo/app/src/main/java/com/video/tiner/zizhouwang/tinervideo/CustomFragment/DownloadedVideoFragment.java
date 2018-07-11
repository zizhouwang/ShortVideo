package com.video.tiner.zizhouwang.tinervideo.CustomFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerNavView;

/**
 * Created by Administrator on 2018/7/11.
 */

public class DownloadedVideoFragment extends SubFragment {

    private FrameLayout downloadedVideoFL;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout view = (FrameLayout) inflater.inflate(R.layout.downloaded_video_layout, null);
        downloadedVideoFL = view.findViewById(R.id.downloadedVideoFL);
        TinerNavView tinerNavView = FormatUtil.getTinerNavView((AppCompatActivity) view.getContext(), view, downloadedVideoFL, false);
        tinerNavView.navTextView.setText("");
        tinerNavView.navTextView.setBackgroundColor(Color.argb(0xff, 0xfa, 0xfa, 0xfa));
        tinerNavView.navTextView.setTextColor(Color.argb(0xff, 0x00, 0x00, 0x00));
        super.onCreateView(inflater, container, savedInstanceState, view, tinerNavView);
        return view;
    }
}
