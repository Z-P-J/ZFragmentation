package com.zpj.fragmentation.swipeback;

import android.os.Bundle;
import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.zpj.fragmentation.ISupportFragment;
import com.zpj.fragmentation.SupportFragment;

/**
 * You can also refer to {@link SwipeBackFragment} to implement YourSwipeBackFragment
 * (extends Fragment and impl {@link ISupportFragment})
 * <p>
 * Created by YoKey on 16/4/19.
 */
public class SwipeBackFragment extends SupportFragment implements ISwipeBack {

    private SwipeBackLayout mSwipeBackLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwipeBackLayout = new SwipeBackLayout(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeBackLayout.setLayoutParams(params);
//        mSwipeBackLayout.setBackgroundColor(Color.TRANSPARENT);
    }

    public View attachToSwipeBack(View view) {
        mSwipeBackLayout.attachToFragment(this, view);
        return mSwipeBackLayout;
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

}