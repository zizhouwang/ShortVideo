package com.video.tiner.zizhouwang.tinervideo.CustomFragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.video.tiner.zizhouwang.tinervideo.R;
import com.video.tiner.zizhouwang.tinervideo.Util.FormatUtil;
import com.video.tiner.zizhouwang.tinervideo.subview.TinerNavView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/7/11.
 */

public class SubFragment extends BaseFragment {

    public AnimatorSet slideInSet;
    public AnimatorSet slideOutSet;

    public SubFragment() {
        slideInSet = (AnimatorSet) AnimatorInflater.loadAnimator(FormatUtil.mainContext, R.animator.slide_in_left);
        slideOutSet = (AnimatorSet) AnimatorInflater.loadAnimator(FormatUtil.mainContext, R.animator.slide_out_right);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState, View view, TinerNavView tinerNavView) {
        final SubFragment thiss = this;
        slideInSet = (AnimatorSet) AnimatorInflater.loadAnimator(container.getContext(), R.animator.slide_in_left);
        slideOutSet = (AnimatorSet) AnimatorInflater.loadAnimator(container.getContext(), R.animator.slide_out_right);
        ArrayList<Animator> animators = slideInSet.getChildAnimations();
        ObjectAnimator slideInAnim = (ObjectAnimator) animators.get(0);
        slideInAnim.setFloatValues(FormatUtil.getScreenWidth(container.getContext()), 0.0f);
        slideInAnim.setDuration(300);
        slideInSet.setTarget(view);
        animators = slideOutSet.getChildAnimations();
        ObjectAnimator slideOurAnim = (ObjectAnimator) animators.get(0);
        slideOurAnim.setFloatValues(0.0f, FormatUtil.getScreenWidth(container.getContext()));
        slideOurAnim.setDuration(300);
        slideOutSet.setTarget(view);
        slideOutSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                FragmentManager fm = thiss.getFragmentManager();
                FragmentTransaction beginTransaction = fm.beginTransaction();
                beginTransaction.remove(thiss);
                beginTransaction.commit();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        tinerNavView.backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideOutSet.start();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //得到Fragment的根布局并使该布局可以获得焦点
        getView().setFocusableInTouchMode(true);
        //得到Fragment的根布局并且使其获得焦点
        getView().requestFocus();
        //对该根布局View注册KeyListener的监听
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    slideOutSet.start();
                    return true;
                } else if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        slideInSet.start();
    }
}
