package com.video.tiner.zizhouwang.tinervideo.CustomFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;

/**
 * Created by zizhouwang on 2018/6/19.
 */

public class MeFragment extends Fragment {

    private View savedView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return createNewView(inflater);
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
        }
        return savedView;
    }
}
