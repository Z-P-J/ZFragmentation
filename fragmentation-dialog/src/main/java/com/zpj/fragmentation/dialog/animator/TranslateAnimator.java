package com.zpj.fragmentation.dialog.animator;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.zpj.fragmentation.dialog.enums.DialogAnimation;

/**
 * 平移动画，不带渐变
 */
public class TranslateAnimator extends ViewPropertyDialogAnimator {
    //动画起始坐标
    private float startTranslationX, startTranslationY;
    private int oldWidth, oldHeight;
    private float initTranslationX, initTranslationY;
    private boolean hasInitDefTranslation = false;

    public TranslateAnimator(View target, DialogAnimation dialogAnimation) {
        super(target, dialogAnimation);
        if (!hasInitDefTranslation) {
            initTranslationX = targetView.getTranslationX();
            initTranslationY = targetView.getTranslationY();
            hasInitDefTranslation = true;
        }
        // 设置起始坐标
        applyTranslation();
        startTranslationX = targetView.getTranslationX();
        startTranslationY = targetView.getTranslationY();

        oldWidth = targetView.getMeasuredWidth();
        oldHeight = targetView.getMeasuredHeight();
    }

    private void applyTranslation() {
        switch (dialogAnimation) {
            case TranslateFromLeft:
                targetView.setTranslationX(-targetView.getRight());
                break;
            case TranslateFromTop:
                targetView.setTranslationY(-targetView.getBottom());
                break;
            case TranslateFromRight:
                targetView.setTranslationX(((View) targetView.getParent()).getMeasuredWidth() - targetView.getLeft());
                break;
            case TranslateFromBottom:
                targetView.setTranslationY(((View) targetView.getParent()).getMeasuredHeight() - targetView.getTop());
                break;
        }
    }

    @Override
    public void initShowAnimator(ViewPropertyAnimator animator) {
        animator.translationX(initTranslationX)
                .translationY(initTranslationY)
                .setInterpolator(new FastOutSlowInInterpolator());
    }

    @Override
    public void initDismissAnimator(ViewPropertyAnimator animator) {
        //执行消失动画的时候，宽高可能改变了，所以需要修正动画的起始值
        switch (dialogAnimation) {
            case TranslateFromLeft:
                startTranslationX -= targetView.getMeasuredWidth() - oldWidth;
                break;
            case TranslateFromTop:
                startTranslationY -= targetView.getMeasuredHeight() - oldHeight;
                break;
            case TranslateFromRight:
                startTranslationX += targetView.getMeasuredWidth() - oldWidth;
                break;
            case TranslateFromBottom:
                startTranslationY += targetView.getMeasuredHeight() - oldHeight;
                break;
        }

        animator.translationX(startTranslationX)
                .translationY(startTranslationY)
                .setInterpolator(new FastOutSlowInInterpolator());
    }
}
