package com.zpj.fragmentation.dialog.animator;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.enums.DialogAnimation;

/**
 * 缩放透明动画
 */
public class ScaleAlphaAnimator extends ViewPropertyDialogAnimator {

    private float pivotX = 0;
    private float pivotY = 0;
    private float tension = 1f;

    public ScaleAlphaAnimator(View target, float pivotX, float pivotY, float tension) {
        this(target, pivotX, pivotY);
        this.tension = tension;
    }

    public ScaleAlphaAnimator(View target, float pivotX, float pivotY) {
        super(target, null);
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        initAnimator();
    }

    public ScaleAlphaAnimator(View target, DialogAnimation dialogAnimation) {
        super(target, dialogAnimation);
        initAnimator();
    }

    public void initAnimator() {
        targetView.setScaleX(0f);
        targetView.setScaleY(0f);
        targetView.setAlpha(0);

        if (dialogAnimation == null) {
            targetView.setPivotX(pivotX);
            targetView.setPivotY(pivotY);
        } else {
            applyPivot();
        }
    }

    /**
     * 根据不同的PopupAnimation来设定对应的pivot
     */
    private void applyPivot() {
        switch (dialogAnimation) {
            case ScaleAlphaFromCenter:
                targetView.setPivotX(targetView.getMeasuredWidth() / 2f);
                targetView.setPivotY(targetView.getMeasuredHeight() / 2f);
                break;
            case ScaleAlphaFromLeftTop:
                targetView.setPivotX(0);
                targetView.setPivotY(0);
                break;
            case ScaleAlphaFromRightTop:
                targetView.setPivotX(targetView.getMeasuredWidth());
                targetView.setPivotY(0f);
                break;
            case ScaleAlphaFromLeftBottom:
                targetView.setPivotX(0f);
                targetView.setPivotY(targetView.getMeasuredHeight());
                break;
            case ScaleAlphaFromRightBottom:
                targetView.setPivotX(targetView.getMeasuredWidth());
                targetView.setPivotY(targetView.getMeasuredHeight());
                break;
        }

    }

    @Override
    public void initShowAnimator(ViewPropertyAnimator animator) {
        animator.scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setInterpolator(new OvershootInterpolator(tension));
    }

    @Override
    public void initDismissAnimator(ViewPropertyAnimator animator) {
        animator.scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setInterpolator(new FastOutSlowInInterpolator());
    }

}
