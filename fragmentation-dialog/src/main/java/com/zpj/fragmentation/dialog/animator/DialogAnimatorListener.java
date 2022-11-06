package com.zpj.fragmentation.dialog.animator;

import android.animation.Animator;
import androidx.annotation.NonNull;

class DialogAnimatorListener implements Animator.AnimatorListener {

    @NonNull
    private final AbsDialogAnimator<?, ?> mAnimator;

    DialogAnimatorListener(@NonNull AbsDialogAnimator<?, ?> animator) {
        this.mAnimator = animator;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        mAnimator.onAnimationStart();
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mAnimator.onAnimationEnd();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        mAnimator.onAnimationCancel();
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
