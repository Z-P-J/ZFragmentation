package com.zpj.fragmentation.dialog.animator;

import android.view.View;
import android.view.ViewPropertyAnimator;

import com.zpj.fragmentation.dialog.enums.DialogAnimation;

public abstract class DialogPropertyAnimator extends AbsDialogAnimator<ViewPropertyAnimator> {

    public DialogPropertyAnimator(View target) {
        super(target);
    }

    public DialogPropertyAnimator(View target, DialogAnimation dialogAnimation) {
        super(target, dialogAnimation);
    }

}
