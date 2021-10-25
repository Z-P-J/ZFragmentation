package com.zpj.fragmentation.dialog.animator;

import android.animation.Animator;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.zpj.fragmentation.dialog.AbstractDialogFragment;
import com.zpj.fragmentation.dialog.enums.DialogAnimation;

public abstract class AbsDialogAnimator<S, T> implements DialogAnimator {

    protected View targetView;

    protected long mShowDuration = 360;
    protected long mDismissDuration = 360;

    public DialogAnimation dialogAnimation;

//    protected Listener mListener;

    public AbsDialogAnimator(View target){
        this(target, null);
    }

    public AbsDialogAnimator(View target, DialogAnimation dialogAnimation){
        this.targetView = target;
        this.dialogAnimation = dialogAnimation;
    }

//    @Override
//    public void setListener(Listener listener) {
//        this.mListener = listener;
//    }

    @Override
    public void setShowDuration(long showAnimDuration) {
        this.mShowDuration = showAnimDuration;
    }

    @Override
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
        targetView.post(new Runnable() {
            @Override
            public void run() {
                S animator = onCreateShowAnimator();
                startAnimator(animator, mShowDuration, true);
            }
        });

    }

    @Override
    public void animateToDismiss() {
        targetView.post(new Runnable() {
            @Override
            public void run() {
                T animator = onCreateDismissAnimator();
                startAnimator(animator, mDismissDuration, false);
            }
        });
    }

    protected void startAnimator(Object animator, long duration, boolean isShow) {
        Log.d("startAnimator", "duration=" + duration + " animator=" + animator);
        if (animator instanceof Animator) {
//            if (mListener != null) {
//                ((Animator) animator).addListener(createAnimatorListener(isShow));
//            }
            ((Animator) animator).setDuration(duration);
            ((Animator) animator).start();
        } else if (animator instanceof ViewPropertyAnimator) {
//            if (mListener != null) {
//                ((ViewPropertyAnimator) animator).setListener(createAnimatorListener(isShow));
//            }
            ((ViewPropertyAnimator) animator).setDuration(duration);
            ((ViewPropertyAnimator) animator).start();
        }
    }

//    private Animator.AnimatorListener createAnimatorListener(boolean isShow) {
//        return new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                if (isShow) {
//                    mListener.onShowAnimationStart();
//                } else {
//                    mListener.onDismissAnimationStart();
//                }
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                if (isShow) {
//                    mListener.onShowAnimationEnd();
//                } else {
//                    mListener.onDismissAnimationEnd();
//                }
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        };
//    }

    public abstract S onCreateShowAnimator();
    public abstract T onCreateDismissAnimator();

}
