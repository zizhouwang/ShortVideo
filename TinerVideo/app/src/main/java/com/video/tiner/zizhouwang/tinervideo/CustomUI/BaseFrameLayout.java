package com.video.tiner.zizhouwang.tinervideo.CustomUI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;

/**
 * Created by zizhouwang on 2018/6/7.
 */

public class BaseFrameLayout extends FrameLayout {

    private Scroller mScroller;

    public BaseFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    public void smoothScroll(int destX) {
        destX = 810;
        int scrollX = getScrollX();
        int deltaX = destX - scrollX;
        mScroller.startScroll(scrollX, 0, deltaX, 0, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
        super.computeScroll();
    }
}
