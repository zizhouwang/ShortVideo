package com.video.tiner.zizhouwang.tinervideo.CustomUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.model.XmlAttrModel;

/**
 * Created by zizhouwang on 2018/6/7.
 */

public class EquScaFrameLayout extends BaseFrameLayout {

    private Boolean isChanged = false;
    private XmlAttrModel xmlAttrModel = new XmlAttrModel();

    public EquScaFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = this.getContext().obtainStyledAttributes(attrs, R.styleable.album);
        try {
            xmlAttrModel.width = typedArray.getLayoutDimension(R.styleable.album_android_layout_width, 0);
            xmlAttrModel.height = typedArray.getLayoutDimension(R.styleable.album_android_layout_height, 0);
            xmlAttrModel.marginBottom = typedArray.getLayoutDimension(R.styleable.album_android_layout_marginBottom, 0);
            xmlAttrModel.marginTop = typedArray.getLayoutDimension(R.styleable.album_android_layout_marginTop, 0);
            xmlAttrModel.marginLeft = typedArray.getLayoutDimension(R.styleable.album_android_layout_marginLeft, 0);
            xmlAttrModel.marginRight = typedArray.getLayoutDimension(R.styleable.album_android_layout_marginRight, 0);
            xmlAttrModel.paddingLeft = typedArray.getLayoutDimension(R.styleable.album_android_paddingLeft, 0);
            xmlAttrModel.paddingRight = typedArray.getLayoutDimension(R.styleable.album_android_paddingRight, 0);
            xmlAttrModel.paddingTop = typedArray.getLayoutDimension(R.styleable.album_android_paddingTop, 0);
            xmlAttrModel.paddingBottom = typedArray.getLayoutDimension(R.styleable.album_android_paddingBottom, 0);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isChanged == false && this.getLayoutParams().getClass().equals(FrameLayout.LayoutParams.class)) {
            FormatUtil.equalScalingWithView(this, xmlAttrModel);
            isChanged = true;
        }
    }
}
