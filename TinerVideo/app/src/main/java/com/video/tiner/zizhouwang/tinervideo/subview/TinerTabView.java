package com.video.tiner.zizhouwang.tinervideo.subview;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/8.
 */

public class TinerTabView extends FrameLayout {

    private List<TextView> tabList = new LinkedList<>();
    private HorizontalScrollView scrollView;
    private FrameLayout scrollChildView;
    private int currentIndex = 0;
    private View yellowLine;

    private OnTabClickListener onTabClickListener = null;

    public TinerTabView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TinerTabView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.setBackgroundResource(R.color.whiteColor);
        scrollView = new HorizontalScrollView(context);
        addView(scrollView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollChildView = new FrameLayout(context);
        scrollView.addView(scrollChildView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        yellowLine = new View(context);
        yellowLine.setBackgroundResource(R.color.tabBottomLineColor);
    }

    public void addTab(String tabTitle) {
        final TextView tab = new TextView(FormatUtil.mainContext);
        tab.setBackgroundResource(R.color.clearColor);
        tab.setText(tabTitle);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(18);
        TextPaint tp = tab.getPaint();
        tp.setFakeBoldText(true);
        tab.setTextColor(getResources().getColor(R.color.tabTextColor));
        int screenWidth = FormatUtil.getScreenWidth(FormatUtil.mainContext);
        FrameLayout.LayoutParams tabLayoutParams = new FrameLayout.LayoutParams(screenWidth / 2, ViewGroup.LayoutParams.MATCH_PARENT);
        tabLayoutParams.leftMargin = tabList.size() * screenWidth / 2;
        scrollChildView.addView(tab, tabLayoutParams);

        if (tabList.size() == 0) {
            scrollChildView.addView(yellowLine, new FrameLayout.LayoutParams(FormatUtil.getScreenWidth(FormatUtil.mainContext) / 2, 10));
            FrameLayout.LayoutParams yellowLineLayout = (FrameLayout.LayoutParams) yellowLine.getLayoutParams();
            yellowLineLayout.gravity = Gravity.BOTTOM;
            yellowLineLayout.leftMargin = 0;
            yellowLine.setLayoutParams(yellowLineLayout);
        }

        tabList.add(tab);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout.LayoutParams yellowLineLayout = (FrameLayout.LayoutParams) yellowLine.getLayoutParams();
                yellowLineLayout.gravity = Gravity.BOTTOM;
                yellowLineLayout.leftMargin = tabList.indexOf(tab) * yellowLineLayout.width;
                yellowLine.setLayoutParams(yellowLineLayout);
                if (onTabClickListener != null) {
                    onTabClickListener.tabClicked(tab, tabList.indexOf(tab));
                }
            }
        });
        FrameLayout.LayoutParams scrollChildViewLayout = (FrameLayout.LayoutParams) scrollChildView.getLayoutParams();
        scrollChildViewLayout.width = tabList.size() * screenWidth / 2;
        scrollChildView.setLayoutParams(scrollChildViewLayout);
    }

    public void setTabs(List<String> tabTitles) {
        for (TextView tab : tabList) {
            scrollView.removeView(tab);
        }
        for (String tabTitle : tabTitles) {
            addTab(tabTitle);
        }
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    public interface OnTabClickListener {
        void tabClicked(TextView tab, int index);
    }
}
