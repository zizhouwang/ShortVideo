package com.video.tiner.zizhouwang.tinervideo.CustomFragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;
import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.adapter.MeListAdapter;
import com.video.tiner.zizhouwang.tinervideo.model.MeModel;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerNavView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zizhouwang on 2018/6/19.
 */

public class MeFragment extends BaseFragment {

    private View savedView = null;
    private FrameLayout meFL;
    private ListView meListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = createNewView(inflater);
        view.setBackgroundResource(R.color.whiteColor);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("mePage");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("mePage");
    }

    public View getSavedView() {
        if (savedView == null) {
            LayoutInflater inflater = LayoutInflater.from(FormatUtil.mainContext);
            savedView = createNewView(inflater);
        }
        return savedView;
    }

    private View createNewView(LayoutInflater inflater) {
        if (savedView == null) {
            final FrameLayout view = (FrameLayout) inflater.inflate(R.layout.me_layout, null);
            savedView = view;
            meFL = view.findViewById(R.id.meFL);
            meListView = view.findViewById(R.id.meListView);
            List<MeModel> meModelList = new ArrayList<>();
            MeModel downloadItem = new MeModel();
            downloadItem.itemTitle = "Downloaded Video";
            meModelList.add(downloadItem);
            downloadItem = new MeModel();
            downloadItem.itemTitle = "feedback: zxzasa1995@gmail.com";
            meModelList.add(downloadItem);
            meListView.setAdapter(new MeListAdapter(savedView.getContext(), meModelList, meListView));
        }
        TinerNavView tinerNavView = FormatUtil.getTinerNavView((AppCompatActivity) savedView.getContext(), meFL, meListView, true);
        tinerNavView.bringToFront();
        tinerNavView.navTextView.setText("Me");
        tinerNavView.navTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tinerNavView.navTextView.setGravity(Gravity.CENTER);
        tinerNavView.navTextView.setTextColor(Color.argb(0xff, 0xff, 0xff, 0xff));
        return savedView;
    }
}
