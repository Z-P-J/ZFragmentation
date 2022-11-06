package com.zpj.fragmentation.dialog;

public interface DialogAnimator {

    void setShowDuration(long showAnimDuration);

    void setDismissDuration(long dismissAnimDuration);

    void animateToShow();

    void animateToDismiss();

    default void cancel() {
        // TODO
    }

    void setAnimationListener(Listener listener);

    interface Listener {

        void onAnimationStart();

        void onAnimationEnd();

        void onAnimationCancel();

        void onAnimationUpdate(float percent);

    }

}
