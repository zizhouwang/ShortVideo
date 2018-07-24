package com.video.tiner.zizhouwang.tinervideo.subview;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;

/**
 * Created by zizhouwang on 2018/6/11.
 */

public class TinerNavView extends FrameLayout {

    public int height;
    private View contentView;

    public FrameLayout navContentFL;
    public ImageView backImageView;
    public TextView navTextView;

    public TinerNavView(final AppCompatActivity context) {
        super(context);
        tinerInit(context, null);
    }

    public TinerNavView(final AppCompatActivity context, AttributeSet attrs) {
        super(context, attrs);
        tinerInit(context, attrs);
    }

    public TinerNavView(final AppCompatActivity context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        tinerInit(context, attrs);
    }

    private void tinerInit(final AppCompatActivity context, AttributeSet attrs) {
        final TinerNavView thiss = this;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.customView);
            ta.recycle();
        }
        LayoutInflater mInflatet = LayoutInflater.from(context);
        contentView = mInflatet.inflate(R.layout.nav_view, null);
        navContentFL = contentView.findViewById(R.id.navContentFL);
        backImageView = contentView.findViewById(R.id.backButton);
        navTextView = contentView.findViewById(R.id.navTextView);
        navTextView.setBackgroundColor(Color.argb(0xff, 0x38, 0x38, 0x38));
        addView(contentView);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = height;
        this.setLayoutParams(layoutParams);

        layoutParams = contentView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = height;
        contentView.setLayoutParams(layoutParams);
    }
}
