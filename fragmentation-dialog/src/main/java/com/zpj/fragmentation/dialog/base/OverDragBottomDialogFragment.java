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

    protected ViewGroup contentView;

    private float cornerRadius;
    protected int mOverDragOffset;

    public OverDragBottomDialogFragment() {
        setMaxWidth(MATCH_PARENT);
        setOverDragOffset(200);
        setCornerRadiusDp(8);
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
        OverDragLayout overDragLayout = (OverDragLayout) getImplView();
        overDragLayout.setShowDuration(getShowAnimDuration());
        overDragLayout.setDismissDuration(getDismissAnimDuration());
        contentView = (ViewGroup) getLayoutInflater().inflate(getContentLayoutId(), null, false);

        LinearLayout flContainer = new LinearLayout(context);
        flContainer.setOrientation(LinearLayout.VERTICAL);
        flContainer.addView(contentView);
        flContainer.addView(new Space(context), new ViewGroup.LayoutParams(MATCH_PARENT, mOverDragOffset));
        overDragLayout.addView(flContainer, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        overDragLayout.bindContentView(contentView);

        if (bgDrawable != null) {
            flContainer.setBackground(bgDrawable);
        } else {
            flContainer.setBackgroundColor(DialogThemeUtils.getDialogBackgroundColor(context));

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(DialogThemeUtils.getDialogBackgroundColor(context));
            drawable.setShape(GradientDrawable.RECTANGLE);
//                int size = ScreenUtils.dp2pxInt(8);
            drawable.setCornerRadii(new float[]{ cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0, 0, 0, 0 });
            contentView.setBackground(drawable);

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

        overDragLayout.setOnClickListener(new View.OnClickListener() {
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

    public T setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        return self();
    }

    public T setCornerRadiusDp(float cornerRadiusDp) {
        return setCornerRadius(ScreenUtils.dp2px(cornerRadiusDp));
    }

}