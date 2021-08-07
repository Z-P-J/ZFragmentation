package com.zpj.fragmentation.dialog.animator;

import android.animation.Animator;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.zpj.fragmentation.dialog.enums.DialogAnimation;

public abstract class AbsDialogAnimator<T> implements DialogAnimator<T> {

    protected View targetView;

    protected long mShowDuration = 360;
    protected long mDismissDuration = 360;

    public DialogAnimation dialogAnimation;

    protected Animator.AnimatorListener mAnimatorListener;

    public AbsDialogAnimator(View target){
        this(target, null);
    }

    public AbsDialogAnimator(View target, DialogAnimation dialogAnimation){
        this.targetView = target;
        this.dialogAnimation = dialogAnimation;
    }

    public void setTargetView(View targetView) {
        this.targetView = targetView;
    }

    public void setAnimatorListener(Animator.AnimatorListener mAnimatorListener) {
        this.mAnimatorListener = mAnimatorListener;
    }

    public void setShowDuration(long showAnimDuration) {
        this.mShowDuration = showAnimDuration;
    }

    public void setDismissDuration(long dismissAnimDuration) {
        this.mDismissDuration = dismissAnimDuration;
    }

    public long getShowDuration() {
        return mShowDuration;
    }

    public long getDismissDuration() {
        return mDismissDuration;
    }

    @Override
    public void animateToShow() {
        T animator = onCreateShowAnimator();
        startAnimator(animator);
    }

    @Override
    public void animateToDismiss() {
        T animator = onCreateDismissAnimator();
        startAnimator(animator);
    }

    protected void startAnimator(T animator) {
        if (animator instanceof Animator) {
            if (mAnimatorListener != null) {
                ((Animator) animator).addListener(mAnimatorListener);
            }
            ((Animator) animator).setDuration(mShowDuration);
            ((Animator) animator).start();
        } else if (animator instanceof ViewPropertyAnimator) {
            ((ViewPropertyAnimator) animator).setListener(mAnimatorListener);
            ((ViewPropertyAnimator) animator).setDuration(mDismissDuration);
            ((ViewPropertyAnimator) animator).start();
        }
    }

    public abstract void initAnimator();

}