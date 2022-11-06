package com.zpj.fragmentation.dialog.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.support.annotation.FloatRange;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.zpj.fragmentation.dialog.DialogAnimator;
import com.zpj.fragmentation.dialog.enums.DialogAnimation;

public abstract class AbsDialogAnimator<S, T> implements DialogAnimator {

    protected View targetView;

    protected long mShowDuration = 360;
    protected long mDismissDuration = 360;

    public DialogAnimation dialogAnimation;

    private Object mAnim;

    private Listener mListener;

    public AbsDialogAnimator(View target){
        this(target, null);
    }

    public AbsDialogAnimator(View target, DialogAnimation dialogAnimation){
        this.targetView = target;
        this.dialogAnimation = dialogAnimation;
    }

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
        targetView.post(() -> {
            S animator = onCreateShowAnimator();
            startAnimator(animator, mShowDuration, true);
        });
    }

    @Override
    public void animateToDismiss() {
        targetView.post(() -> {
            T animator = onCreateDismissAnimator();
            startAnimator(animator, mDismissDuration, false);
        });
    }

    @Override
    public void setAnimationListener(Listener listener) {
        this.mListener = listener;
    }

    private boolean isAnimationCancel;

    @Override
    public void cancel() {
        isAnimationCancel = true;
        if (mAnim instanceof Animator) {
            ((Animator) mAnim).cancel();
        } else if (mAnim instanceof ViewPropertyAnimator) {
            ((ViewPropertyAnimator) mAnim).cancel();
        } else if (mAnim instanceof Animation) {
            ((Animation) mAnim).cancel();
            onAnimationCancel();
        }
        this.mAnim = null;
    }

    protected void startAnimator(Object animator, long duration, boolean isShow) {
        Log.d("startAnimator", "duration=" + duration + " animator=" + animator);
        if (this.mAnim != null) {
            cancel();
        }
        this.mAnim = animator;
        if (animator instanceof Animator) {

            ((Animator) animator).addListener(new DialogAnimatorListener(this));
            ((Animator) animator).setDuration(duration);


            AnimatorSet set = new AnimatorSet();

            ValueAnimator updateAnimator = ValueAnimator.ofFloat(0f, 1f);
            updateAnimator.setDuration(duration);
            updateAnimator.setInterpolator(new LinearInterpolator());
            updateAnimator.addUpdateListener(animation -> {
                Log.d("AbsDialogAnimator", "onAnimationUpdate percent=" + animation.getAnimatedFraction());
                AbsDialogAnimator.this.onAnimationUpdate(animation.getAnimatedFraction());
            });

            set.setDuration(((Animator) animator).getDuration());
            set.playTogether((Animator) animator, updateAnimator);
            set.start();
        } else if (animator instanceof ViewPropertyAnimator) {
            ((ViewPropertyAnimator) animator).setListener(new DialogAnimatorListener(this));
            ((ViewPropertyAnimator) animator).setDuration(duration);
            ((ViewPropertyAnimator) animator).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    AbsDialogAnimator.this.onAnimationUpdate(animation.getAnimatedFraction());
                }
            });
            ((ViewPropertyAnimator) animator).start();
        } else if (animator instanceof Animation) {
            Log.d("AbsDialogAnimator", "startAnimator interpolator=" + ((Animation) animator).getInterpolator() + " Animation=" + animator);

            Animation animation;
            if (animator instanceof AnimationSet) {
                Animation updateAnimation = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        Log.d("AbsDialogAnimator", "applyTransformation interpolatedTime=" + interpolatedTime);
                        onAnimationUpdate(interpolatedTime);
                    }
                };
                updateAnimation.setDuration(duration);
                ((AnimationSet) animator).addAnimation(updateAnimation);
                animation = (Animation) animator;
            } else {
                AnimationSet animationSet = new AnimationSet(false) {

                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        onAnimationUpdate(interpolatedTime);
                    }
                };
                animationSet.addAnimation((Animation) animator);
                animation = animationSet;
            }
            animation.setDuration(duration);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    AbsDialogAnimator.this.onAnimationStart();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    AbsDialogAnimator.this.onAnimationEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            targetView.startAnimation(animation);
        }
    }

    protected void onAnimationStart() {
        isAnimationCancel = false;
        if (mListener != null) {
            mListener.onAnimationStart();
        }
    }

    protected void onAnimationEnd() {
        if (isAnimationCancel) {
            return;
        }
        if (mListener != null) {
            mListener.onAnimationEnd();
        }
    }

    protected void onAnimationCancel() {
        if (mListener != null) {
            mListener.onAnimationCancel();
        }
    }

    protected void onAnimationUpdate(@FloatRange(from = 0f, to = 1f) float percent) {
        if (mListener != null) {
            mListener.onAnimationUpdate(percent);
        }
    }


    public abstract S onCreateShowAnimator();
    public abstract T onCreateDismissAnimator();

}
