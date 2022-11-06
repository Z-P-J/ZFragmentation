package com.zpj.fragmentation.dialog.animator;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.view.View;

/**
 * 背景Shadow动画器
 */
public class ShadowMaskAnimator extends AbsDialogAnimator<Animator, Animator> {

    private static final int shadowBgColor = Color.parseColor("#60000000");

    private final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private int startColor = Color.TRANSPARENT;

    public ShadowMaskAnimator(View target) {
        super(target);
        if (target != null) {
            targetView.setBackgroundColor(startColor);
        }
    }

    public void setStartColor(int startColor) {
        this.startColor = startColor;
        if (targetView != null) {
            targetView.setBackgroundColor(startColor);
        }
    }

    public int calculateBgColor(float fraction) {
        return (int) argbEvaluator.evaluate(fraction, startColor, shadowBgColor);
    }

    @Override
    public Animator onCreateShowAnimator() {
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, startColor, shadowBgColor);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                targetView.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        return animator;
    }

    @Override
    public Animator onCreateDismissAnimator() {
        ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator, shadowBgColor, startColor);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                targetView.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        return animator;
    }
}
