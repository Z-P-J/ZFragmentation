package com.zpj.fragmentation.dialog.animator;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.zpj.fragmentation.dialog.enums.DialogAnimation;

/**
 * 平移动画
 */
public class TranslateAlphaAnimator extends ViewPropertyDialogAnimator {

    //动画起始坐标
    private float startTranslationX, startTranslationY;
    private float defTranslationX, defTranslationY;

    public TranslateAlphaAnimator(View target, DialogAnimation dialogAnimation) {
        super(target, dialogAnimation);
        defTranslationX = targetView.getTranslationX();
        defTranslationY = targetView.getTranslationY();

        targetView.setAlpha(0);
        // 设置移动坐标
        applyTranslation();
        startTranslationX = targetView.getTranslationX();
        startTranslationY = targetView.getTranslationY();
    }

    private void applyTranslation() {
//        int halfWidthOffset = ScreenUtils.getScreenWidth(targetView.getContext())/2 - targetView.getMeasuredWidth()/2;
//        int halfHeightOffset = ScreenUtils.getScreenHeight(targetView.getContext())/2 - targetView.getMeasuredHeight()/2;
        switch (dialogAnimation){
            case TranslateAlphaFromLeft:
                targetView.setTranslationX(-(targetView.getMeasuredWidth()/* + halfWidthOffset*/));
                break;
            case TranslateAlphaFromTop:
                targetView.setTranslationY(-(targetView.getMeasuredHeight() /*+ halfHeightOffset*/));
                break;
            case TranslateAlphaFromRight:
                targetView.setTranslationX(targetView.getMeasuredWidth() /*+ halfWidthOffset*/);
                break;
            case TranslateAlphaFromBottom:
                targetView.setTranslationY(targetView.getMeasuredHeight() /*+ halfHeightOffset*/);
                break;
        }
    }

    @Override
    public void initShowAnimator(ViewPropertyAnimator animator) {
        animator.translationX(defTranslationX)
                .translationY(defTranslationY)
                .alpha(1f)
                .setInterpolator(new FastOutSlowInInterpolator());
    }

    @Override
    public void initDismissAnimator(ViewPropertyAnimator animator) {
        animator.translationX(startTranslationX)
                .translationY(startTranslationY)
                .alpha(0f)
                .setInterpolator(new FastOutSlowInInterpolator());
    }
}
