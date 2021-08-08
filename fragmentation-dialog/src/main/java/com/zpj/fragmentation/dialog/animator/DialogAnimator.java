package com.zpj.fragmentation.dialog.animator;

public interface DialogAnimator {

    void initAnimator();

    void setShowDuration(long showAnimDuration);

    void setDismissDuration(long dismissAnimDuration);

    void animateToShow();

    void animateToDismiss();

}
