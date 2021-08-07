package com.zpj.fragmentation.dialog.animator;

public interface DialogAnimator<T> {

    void initAnimator();
    T onCreateShowAnimator();
    T onCreateDismissAnimator();

    void animateToShow();

    void animateToDismiss();

}
