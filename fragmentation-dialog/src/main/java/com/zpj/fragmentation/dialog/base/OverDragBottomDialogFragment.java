package com.zpj.fragmentation.dialog.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Space;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.animator.DialogAnimator;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.fragmentation.dialog.widget.OverDragLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class OverDragBottomDialogFragment<T extends OverDragBottomDialogFragment<T>> extends BaseDialogFragment<T> {

    protected ViewGroup contentView;
    protected int mOverDragOffset;

    public OverDragBottomDialogFragment() {
        setMaxWidth(MATCH_PARENT);
//        setOverDragOffset(200);
        setOverDragOffset(0);
    }

    @Override
    protected final int getImplLayoutId() {
        return R.layout.fragment_dialog_bottom_over_drag;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    protected abstract int getContentLayoutId();

    @Override
    protected DialogAnimator onCreateDialogAnimator(ViewGroup contentView) {
        return null;
    }

    @Override
    protected DialogAnimator onCreateShadowAnimator(FrameLayout flContainer) {
        return null;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        marginTop = 0;
        OverDragLayout bottomPopupContainer = (OverDragLayout) getImplView();
//        bottomPopupContainer.setMaxOverScrollOffset(mOverDragOffset);
        bottomPopupContainer.setShowDuration(getShowAnimDuration());
        bottomPopupContainer.setDismissDuration(getDismissAnimDuration());
        contentView = (ViewGroup) getLayoutInflater().inflate(getContentLayoutId(), null, false);

        LinearLayout flContainer = new LinearLayout(context);
        flContainer.setOrientation(LinearLayout.VERTICAL);
        flContainer.addView(contentView);
        flContainer.addView(new Space(context), new ViewGroup.LayoutParams(MATCH_PARENT, mOverDragOffset));
        bottomPopupContainer.addView(flContainer, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        bottomPopupContainer.bindContentView(contentView);

        if (bgDrawable != null) {
            flContainer.setBackground(bgDrawable);
        } else {
            flContainer.setBackgroundColor(DialogThemeUtils.getDialogBackgroundColor(context));
        }

        LinearLayout.LayoutParams contentParams = (LinearLayout.LayoutParams) contentView.getLayoutParams();
        contentParams.gravity = getGravity();
        contentParams.leftMargin = getMarginStart();
        contentParams.topMargin = getMarginTop();
        contentParams.rightMargin = getMarginEnd();
        contentParams.bottomMargin = getMarginBottom();
        contentParams.height = getMaxHeight();
        contentParams.width = getMaxWidth();
        contentView.setLayoutParams(contentParams);

//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flContainer.getLayoutParams();
//        params.gravity = getGravity();
//        params.leftMargin = getMarginStart();
//        params.topMargin = getMarginTop();
//        params.rightMargin = getMarginEnd();
//        params.bottomMargin = mOverDragOffset;
//        params.height = getMaxHeight();
//        params.width = getMaxWidth();
        flContainer.setFocusableInTouchMode(true);
        flContainer.setFocusable(true);
        flContainer.setClickable(true);


        bottomPopupContainer.enableDrag(true);
        bottomPopupContainer.dismissOnTouchOutside(true);
        bottomPopupContainer.handleTouchOutsideEvent(true);
        bottomPopupContainer.hasShadowBg(true);

        bottomPopupContainer.setOnCloseListener(new OverDragLayout.OnCloseListener() {
            @Override
            public void onClose() {
                postOnEnterAnimationEnd(() -> {
                    if (isDismissing) {
                        return;
                    }
                    isDismissing = true;
                    OverDragBottomDialogFragment.super.doDismissAnimation();
                    popThis();
                    onDismiss();
                });
            }
            @Override
            public void onOpen() {

            }
        });

        bottomPopupContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected void initLayoutParams(ViewGroup view) {
    }

    @Override
    public void doShowAnimation() {
        super.doShowAnimation();
        if (getImplView() instanceof OverDragLayout) {
            ((OverDragLayout) getImplView()).open();
        }
    }

    @Override
    public void doDismissAnimation() {
        super.doDismissAnimation();
        if (getImplView() instanceof OverDragLayout) {
            ((OverDragLayout) getImplView()).close();
        }
    }

    public T setOverDragOffset(int mOverDragOffset) {
        this.mOverDragOffset = mOverDragOffset;
        return self();
    }
}
