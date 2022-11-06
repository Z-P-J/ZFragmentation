package com.zpj.fragmentation.dialog.animator;

import androidx.annotation.NonNull;

import com.zpj.fragmentation.dialog.DialogAnimator;

public class PairDialogAnimator implements DialogAnimator {

    private final DialogAnimator mShowAnimator;
    private final DialogAnimator mDismissAnimator;

    public PairDialogAnimator(@NonNull DialogAnimator mShowAnimator, @NonNull DialogAnimator mDismissAnimator) {
        this.mShowAnimator = mShowAnimator;
        this.mDismissAnimator = mDismissAnimator;
    }

    @Override
    public void setShowDuration(long showAnimDuration) {
        this.mShowAnimator.setShowDuration(showAnimDuration);
    }

    @Override
    public void setDismissDuration(long dismissAnimDuration) {
        this.mDismissAnimator.setDismissDuration(dismissAnimDuration);
    }

    @Override
    public void animateToShow() {
        this.mShowAnimator.animateToShow();
    }

    @Override
    public void animateToDismiss() {
        this.mShowAnimator.animateToDismiss();
    }

    @Override
    public void setAnimationListener(Listener listener) {
        this.mShowAnimator.setAnimationListener(listener);
        this.mDismissAnimator.setAnimationListener(listener);
    }
}
