package com.zpj.fragmentation.dialog.impl;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.animator.PopupAnimator;
import com.zpj.fragmentation.dialog.animator.TranslateAnimator;
import com.zpj.fragmentation.dialog.base.BaseDialogFragment;
import com.zpj.fragmentation.dialog.enums.PopupAnimation;
import com.zpj.fragmentation.swipeback.ISwipeBack;
import com.zpj.fragmentation.swipeback.SwipeBackLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public abstract class FullScreenDialogFragment extends BaseDialogFragment<FullScreenDialogFragment> implements ISwipeBack {

    private SwipeBackLayout mSwipeBackLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwipeBackLayout = new SwipeBackLayout(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeBackLayout.setLayoutParams(params);
    }

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayoutId() > 0) {
            view = inflater.inflate(getLayoutId(), container, false);
        } else {
            view = super.onCreateView(inflater, container, savedInstanceState);
        }
        SwipeBackLayout.EdgeLevel level = getEdgeLevel();
        setEdgeLevel(level == null ? SwipeBackLayout.EdgeLevel.MAX : level);
        mSwipeBackLayout.attachToFragment(this, view);
        mSwipeBackLayout.setEnableSwipeBack(enableSwipeBack());
        initView(view, savedInstanceState);
        return mSwipeBackLayout;
    }

    @Override
    protected PopupAnimator getDialogAnimator(ViewGroup contentView) {
        return new TranslateAnimator(contentView, PopupAnimation.TranslateFromBottom);
    }

    @Override
    protected PopupAnimator getShadowAnimator(FrameLayout flContainer) {
        return null;
    }

    @Override
    protected void initLayoutParams(ViewGroup view) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.height = MATCH_PARENT;
        layoutParams.width = MATCH_PARENT;
        view.setFocusableInTouchMode(true);
        view.setFocusable(true);
        view.setClickable(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden && mSwipeBackLayout != null) {
            mSwipeBackLayout.hiddenFragment();
        }
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    /**
     * 是否可滑动
     *
     * @param enable
     */
    @Override
    public void setEnableSwipeBack(boolean enable) {
        mSwipeBackLayout.setEnableSwipeBack(enable);
    }

    @Override
    public boolean isSwipeBackEnable() {
        return mSwipeBackLayout.isSwipeBackEnable();
    }

    @Override
    public void setEdgeLevel(SwipeBackLayout.EdgeLevel edgeLevel) {
        mSwipeBackLayout.setEdgeLevel(edgeLevel);
    }

    @Override
    public void setEdgeLevel(int widthPixel) {
        mSwipeBackLayout.setEdgeLevel(widthPixel);
    }

    @Override
    public void setEdgeOrientation(int orientation) {
        mSwipeBackLayout.setEdgeOrientation(orientation);
    }

    /**
     * Set the offset of the parallax slip.
     */
    @Override
    public void setParallaxOffset(@FloatRange(from = 0.0f, to = 1.0f) float offset) {
        mSwipeBackLayout.setParallaxOffset(offset);
    }

    @Override
    public boolean swipeBackPriority() {
        return false;
    }

    @Override
    public void onDestroyView() {
        mSwipeBackLayout.internalCallOnDestroyView();
        super.onDestroyView();
    }

    protected boolean enableSwipeBack() {
        return false;
    }

    public SwipeBackLayout.EdgeLevel getEdgeLevel() {
        return SwipeBackLayout.EdgeLevel.MAX;
    }

}
