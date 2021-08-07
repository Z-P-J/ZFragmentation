package com.zpj.fragmentation.dialog.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.animator.DialogAnimator;
import com.zpj.fragmentation.dialog.enums.DialogPosition;
import com.zpj.fragmentation.dialog.widget.PopupDrawerLayout;

public abstract class DrawerDialogFragment<T extends DrawerDialogFragment<T>> extends BaseDialogFragment<T> {


    protected DialogPosition dialogPosition = DialogPosition.Left;
    protected PopupDrawerLayout drawerLayout;
    protected FrameLayout drawerContentContainer;

    private boolean enableShadow = true;
    private boolean isDrawStatusBarShadow = false;

    @Override
    protected int getImplLayoutId() {
        return R.layout._dialog_layout_drawer_view;
    }

    protected abstract int getContentLayoutId();

    @Override
    protected DialogAnimator<?> onCreateDialogAnimator(ViewGroup contentView) {
        return null;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerContentContainer = findViewById(R.id.drawerContentContainer);
        View contentView = getLayoutInflater().inflate(getContentLayoutId(), drawerContentContainer, false);
        drawerContentContainer.addView(contentView);

        if (bgDrawable != null) {
            contentView.setBackground(bgDrawable);
        }


        drawerLayout.enableShadow = enableShadow;
        drawerLayout.isCanClose = cancelableInTouchOutside;
        drawerLayout.setOnCloseListener(new PopupDrawerLayout.OnCloseListener() {
            @Override
            public void onClose() {
//                dismiss();
//                setFragmentAnimator(new DefaultNoAnimator());
                postOnEnterAnimationEnd(() -> {
                    popThis();
                    onDismiss();
                });
            }

            @Override
            public void onOpen() {

            }

            @Override
            public void onDismissing(float fraction) {
                drawerLayout.isDrawStatusBarShadow = isDrawStatusBarShadow;
            }
        });

        drawerLayout.setDrawerPosition(dialogPosition);
        drawerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.close();
            }
        });
    }

    @Override
    protected void initLayoutParams(ViewGroup view) {

    }

    @Override
    public void doShowAnimation() {
        drawerLayout.open();
    }

    @Override
    public void doDismissAnimation() {
        drawerLayout.close();
    }

    public T setEnableShadow(boolean enableShadow) {
        this.enableShadow = enableShadow;
        return self();
    }

    public T setDrawStatusBarShadow(boolean drawStatusBarShadow) {
        isDrawStatusBarShadow = drawStatusBarShadow;
        return self();
    }

    public T setDialogPosition(DialogPosition dialogPosition) {
        this.dialogPosition = dialogPosition;
        return self();
    }
}
