package com.zpj.fragmentation.dialog.animator;

public interface DialogAnimator {

    void setShowDuration(long showAnimDuration);

    void setDismissDuration(long dismissAnimDuration);

    void animateToShow();

    void animateToDismiss();

}
