package com.zpj.fragmentation.dialog.animator;

import android.animation.Animator;
import android.view.View;

import com.zpj.fragmentation.dialog.enums.DialogAnimation;

/**
 * Description: 没有动画效果的动画器
 * Create by dance, at 2019/6/6
 */
public class EmptyAnimator extends AbsDialogAnimator<Animator> {

    public EmptyAnimator(View target) {
        super(target);
    }

    public EmptyAnimator(View target, DialogAnimation dialogAnimation) {
        super(target, dialogAnimation);
    }

    @Override
    public void initAnimator() {

    }

    @Override
    public Animator onCreateShowAnimator() {
        return null;
    }

    @Override
    public Animator onCreateDismissAnimator() {
        return null;
    }
}
