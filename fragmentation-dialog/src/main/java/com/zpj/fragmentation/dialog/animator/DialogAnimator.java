package com.zpj.fragmentation.dialog.animator;

public interface DialogAnimator {

//    void setListener(Listener listener);

    void setShowDuration(long showAnimDuration);

    void setDismissDuration(long dismissAnimDuration);

    void animateToShow();

    void animateToDismiss();

//    interface Listener {
//
//        void onShowAnimationStart();
//
//        void onShowAnimationEnd();
//
//        void onDismissAnimationStart();
//
//        void onDismissAnimationEnd();
//    }

}
