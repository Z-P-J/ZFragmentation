package com.zpj.fragmentation.dialog.base;

import android.graphics.drawable.GradientDrawable;
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
import com.zpj.utils.ScreenUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class OverDragBottomDialogFragment<T extends OverDragBottomDialogFragment<T>> extends BaseDialogFragment<T> {

    protected LinearLayout mWrapper;
    protected ViewGroup contentView;
    protected View mOverDragSpace;

    private float cornerRadius;
    protected int mOverDragOffset;

    public OverDragBottomDialogFragment() {
        setMaxWidth(MATCH_PARENT);
        setOverDragOffset(200);
        setCornerRadiusDp(24);
    }

    @Override
    protected final int getGravity() {
        return Gravity.BOTTOM;
    }

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
        initImplView(view, savedInstanceState);
    }

    protected void initImplView(View view, @Nullable Bundle savedInstanceState) {
        FrameLayout flContainer = findViewById(R.id._dialog_fl_container);
        this.rootView = flContainer;

        if (interceptTouch) {
            interceptTouch();
        }

        OverDragLayout overDragLayout = new OverDragLayout(context);
        implView = overDragLayout;
        flContainer.addView(overDragLayout);

        overDragLayout.setShowDuration(getShowAnimDuration());
        overDragLayout.setDismissDuration(getDismissAnimDuration());
        overDragLayout.setMaxOverScrollOffset(mOverDragOffset);
        contentView = (ViewGroup) getLayoutInflater().inflate(getImplLayoutId(), null, false);

        mWrapper = new LinearLayout(context);
        mWrapper.setOrientation(LinearLayout.VERTICAL);
        mWrapper.addView(contentView);
        mOverDragSpace = new View(context);
        mWrapper.addView(mOverDragSpace, new ViewGroup.LayoutParams(MATCH_PARENT, mOverDragOffset));
        overDragLayout.addView(mWrapper, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        overDragLayout.bindContentView(contentView);

        if (bgDrawable != null) {
            mWrapper.setBackground(bgDrawable);
        } else if (cornerRadius > 0){
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(DialogThemeUtils.getDialogBackgroundColor(context));
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadii(new float[]{ cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0, 0, 0, 0 });
            mWrapper.setBackground(drawable);
        }

        LinearLayout.LayoutParams contentParams = (LinearLayout.LayoutParams) contentView.getLayoutParams();
        contentParams.gravity = getGravity();
        contentParams.leftMargin = getMarginStart();
        contentParams.topMargin = 0;
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
        contentView.setFocusableInTouchMode(true);
        contentView.setFocusable(true);
        contentView.setClickable(true);


        overDragLayout.enableDrag(true);
        overDragLayout.dismissOnTouchOutside(true);
        overDragLayout.handleTouchOutsideEvent(true);
        overDragLayout.hasShadowBg(true);

        overDragLayout.setOnCloseListener(new OverDragLayout.OnCloseListener() {
            @Override
            public void onClose() {
                if (isDismissing) {
                    return;
                }
                isDismissing = true;
                postOnEnterAnimationEnd(() -> {
                    OverDragBottomDialogFragment.super.doDismissAnimation();
                    onDismiss();
                    popThis();
                });
            }
            @Override
            public void onOpen() {

            }
        });

        overDragLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected void initLayoutParams(ViewGroup view) {
        // do nothing
    }

    @Override
    public void doShowAnimation() {

        int height = Math.min(contentView.getMeasuredHeight(), getRootView().getMeasuredHeight() - getMarginTop());

        LinearLayout.LayoutParams contentParams = (LinearLayout.LayoutParams) contentView.getLayoutParams();
        contentParams.height = height;
        contentView.setLayoutParams(contentParams);

        ViewGroup.LayoutParams params = mWrapper.getLayoutParams();
        params.height = height + mOverDragOffset;
        mWrapper.setLayoutParams(params);

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

    public T setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        return self();
    }

    public T setCornerRadiusDp(float cornerRadiusDp) {
        return setCornerRadius(ScreenUtils.dp2px(cornerRadiusDp));
    }

}
