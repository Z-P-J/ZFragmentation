package com.zpj.fragmentation.dialog.animator;

import android.view.View;
import android.view.ViewPropertyAnimator;

import com.zpj.fragmentation.dialog.enums.DialogAnimation;

public abstract class ViewPropertyDialogAnimator
        extends AbsDialogAnimator<ViewPropertyAnimator, ViewPropertyAnimator> {

    private final ViewPropertyAnimator mAnimator;

    public ViewPropertyDialogAnimator(View target) {
        this(target, null);
    }

    public ViewPropertyDialogAnimator(View target, DialogAnimation dialogAnimation){
        super(target, dialogAnimation);
        this.targetView = target;
        this.dialogAnimation = dialogAnimation;
        mAnimator = target.animate();
    }

    @Override
    public final ViewPropertyAnimator onCreateShowAnimator() {
        initShowAnimator(mAnimator);
        return mAnimator;
    }

    @Override
    public final ViewPropertyAnimator onCreateDismissAnimator() {
        initDismissAnimator(mAnimator);
        return mAnimator;
    }

    @Override
    public void cancel() {

    }

    public abstract void initShowAnimator(ViewPropertyAnimator animator);

    public abstract void initDismissAnimator(ViewPropertyAnimator animator);

}
