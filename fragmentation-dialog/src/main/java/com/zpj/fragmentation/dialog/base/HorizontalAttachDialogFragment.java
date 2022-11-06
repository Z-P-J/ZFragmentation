package com.zpj.fragmentation.dialog.base;

import android.graphics.Rect;
import android.view.ViewGroup;

import com.zpj.fragmentation.dialog.DialogAnimator;
import com.zpj.fragmentation.dialog.animator.ScrollScaleAnimator;
import com.zpj.fragmentation.dialog.enums.DialogAnimation;
import com.zpj.fragmentation.dialog.enums.DialogPosition;
import com.zpj.utils.ScreenUtils;

/**
 * Description: 水平方向的依附于某个View或者某个点的弹窗，可以轻松实现微信朋友圈点赞的弹窗效果。
 * 支持通过popupPosition()方法手动指定想要出现在目标的左边还是右边，但是对Top和Bottom则不生效。
 * Create by lxj, at 2019/3/13
 */
public abstract class HorizontalAttachDialogFragment<T extends HorizontalAttachDialogFragment<T>>
        extends AttachDialogFragment<T> {

    @Override
    protected DialogAnimator onCreateDialogAnimator(ViewGroup contentView) {

        float translationX = 0, translationY = 0;
        int w = getImplView().getMeasuredWidth();
        int h = getImplView().getMeasuredHeight();
        //0. 判断是依附于某个点还是某个View
        if (touchPoint != null) {
            // 依附于指定点
            isShowLeft = touchPoint.x > ScreenUtils.getScreenWidth(context) / 2f;

            // translationX: 在左边就和点左边对齐，在右边就和其右边对齐
            translationX = isShowLeftToTarget() ? (touchPoint.x - w - mOffsetX) : (touchPoint.x + mOffsetX);
            translationY = touchPoint.y - h * .5f + mOffsetY;
        } else {
            // 依附于指定View
            //1. 获取atView在屏幕上的位置
            int[] locations = new int[2];
            attachView.getLocationOnScreen(locations);
            Rect rect = new Rect(locations[0], locations[1], locations[0] + attachView.getMeasuredWidth(),
                    locations[1] + attachView.getMeasuredHeight());

            int centerX = (rect.left + rect.right) / 2;

            isShowLeft = centerX > ScreenUtils.getScreenWidth(context) / 2;

            translationX = isShowLeftToTarget() ? (rect.left - w + mOffsetX) : (rect.right + mOffsetX);
            translationY = rect.top + (rect.height() - h) / 2f + mOffsetY;
        }
        getImplView().setTranslationX(translationX);
        getImplView().setTranslationY(translationY);

        ScrollScaleAnimator animator = new ScrollScaleAnimator(getImplView(),
                isShowLeftToTarget() ? DialogAnimation.ScrollAlphaFromRight
                        : DialogAnimation.ScrollAlphaFromLeft);
        animator.isOnlyScaleX = true;
        return animator;
    }

    private boolean isShowLeftToTarget() {
        return (isShowLeft || dialogPosition == DialogPosition.Left)
                && dialogPosition != DialogPosition.Right;
    }

}
