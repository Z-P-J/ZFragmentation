package com.zpj.fragmentation.helper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentationMagician;
import android.util.Log;

import com.zpj.fragmentation.Fragmentation;
import com.zpj.fragmentation.ISupportFragment;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.dialog.AbstractDialogFragment;

import java.util.List;

/**
 * Created by YoKey on 17/4/4.
 */

public class VisibleDelegate {

    private static final String TAG = "VisibleDelegate";

    private static final String FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE = "fragmentation_invisible_when_leave";
    private static final String FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE = "fragmentation_compat_replace";

    // SupportVisible相关
    private boolean mIsSupportVisible;
    private boolean mNeedDispatch = true;
    private boolean mInvisibleWhenLeave;
    private boolean mIsFirstVisible = true;
    private boolean  mIsLazyInit;
    private boolean mFirstCreateViewCompatReplace = true;

    private Handler mHandler;
    private Bundle mSaveInstanceState;

    private final ISupportFragment mSupportF;
    private final Fragment mFragment;

    public VisibleDelegate(ISupportFragment fragment) {
        this.mSupportF = fragment;
        this.mFragment = (Fragment) fragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSaveInstanceState = savedInstanceState;
            // setUserVisibleHint() may be called before onCreate()
            mInvisibleWhenLeave = savedInstanceState.getBoolean(FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE);
            mFirstCreateViewCompatReplace = savedInstanceState.getBoolean(FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE, mInvisibleWhenLeave);
        outState.putBoolean(FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE, mFirstCreateViewCompatReplace);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        debug("onActivityCreated fragment=" + mFragment);
        debug("onActivityCreated mFirstCreateViewCompatReplace=" + mFirstCreateViewCompatReplace);
        debug("onActivityCreated tag=" + mFragment.getTag());
        if (!mFirstCreateViewCompatReplace && mFragment.getTag() != null && mFragment.getTag().startsWith("android:switcher:")) {
            return;
        }

        if (mFirstCreateViewCompatReplace) {
            mFirstCreateViewCompatReplace = false;
        }

        debug("onActivityCreated mInvisibleWhenLeave=" + mInvisibleWhenLeave);
        debug("onActivityCreated isHidden=" + mFragment.isHidden() + " getUserVisibleHint=" + mFragment.getUserVisibleHint());
        if (!mInvisibleWhenLeave && !mFragment.isHidden() && mFragment.getUserVisibleHint()) {
            debug("onActivityCreated parent=" + mFragment.getParentFragment() + " isFragmentVisible=" + isFragmentVisible(mFragment.getParentFragment()));
            if (mFragment.getParentFragment() == null || isFragmentVisible(mFragment.getParentFragment())) {
                mNeedDispatch = false;
                safeDispatchUserVisibleHint(true);
            }
        }
        debug("onActivityCreated ====================== end");
    }

    public void onResume() {
        debug("onResume mIsFirstVisible=" + mIsFirstVisible);
        if (!mIsFirstVisible) {
            if (mFragment instanceof SupportFragment) {
                ISupportFragment topFragment = ((SupportFragment) mFragment).getTopChildFragment();

                if (topFragment instanceof AbstractDialogFragment
                        && ((AbstractDialogFragment) topFragment).isAdded()
                        && !((AbstractDialogFragment) topFragment).isDismissing()) {
                    ((AbstractDialogFragment) topFragment).onResume();
                    return;
                }
            }
            debug("onResume mIsSupportVisible=" + mIsSupportVisible + " mInvisibleWhenLeave=" + mInvisibleWhenLeave + " isFragmentVisible=" + isFragmentVisible(mFragment));
            if (!mIsSupportVisible && !mInvisibleWhenLeave && isFragmentVisible(mFragment)) {
                mNeedDispatch = false;
                dispatchSupportVisible(true);
            }
        }
    }

    public void onPause() {
        debug("onPause mIsSupportVisible=" + mIsSupportVisible + " isFragmentVisible=" + isFragmentVisible(mFragment));
        if (mFragment instanceof SupportFragment) {
            ISupportFragment topFragment = ((SupportFragment) mFragment).getTopChildFragment();

            if (topFragment instanceof AbstractDialogFragment
                    && ((AbstractDialogFragment) topFragment).isAdded()
                    && !((AbstractDialogFragment) topFragment).isDismissing()) {
                if (((AbstractDialogFragment) topFragment).isCanPause()) {
                    ((AbstractDialogFragment) topFragment).onPause();
                    return;
                }
            }
        }
        if (mIsSupportVisible && isFragmentVisible(mFragment)) {
            mNeedDispatch = false;
            mInvisibleWhenLeave = false;
            dispatchSupportVisible(false);
        } else {
//            mInvisibleWhenLeave = true;
        }
    }

    public void onHiddenChanged(boolean hidden) {
        debug("onHiddenChanged hidden=" + hidden + " isResumed=" + mFragment.isResumed());
        if (!hidden && !mFragment.isResumed()) {
            //if fragment is shown but not resumed, ignore...
            mInvisibleWhenLeave = false;
            return;
        }
        // 当前Fragment包含AbstractDialogFragment时，我们把hidden值传给顶部的AbstractDialogFragment处理
        if (mFragment instanceof SupportFragment) {
            ISupportFragment topFragment = ((SupportFragment) mFragment).getTopChildFragment();
            if (topFragment instanceof AbstractDialogFragment) {
                ((AbstractDialogFragment) topFragment).onHiddenChanged(hidden);
                return;
            }
        }
        if (hidden) {
            safeDispatchUserVisibleHint(false);
        } else {
            enqueueDispatchVisible();
        }
    }

    public void onDestroyView() {
        mIsFirstVisible = true;
        mIsLazyInit = false;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        debug("setUserVisibleHint isResumed=" + mFragment.isResumed()
                + " isAdded=" + mFragment.isAdded() + " isVisibleToUser=" + isVisibleToUser
                + " mIsSupportVisible=" + mIsSupportVisible);
        if (mFragment.isResumed() || (!mFragment.isAdded() && isVisibleToUser)) {
            if (!mIsSupportVisible && isVisibleToUser) {
                safeDispatchUserVisibleHint(true);
            } else if (mIsSupportVisible && !isVisibleToUser) {
                dispatchSupportVisible(false);
            }
        }
    }

    private void safeDispatchUserVisibleHint(boolean visible) {
        debug("safeDispatchUserVisibleHint mIsFirstVisible=" + mIsFirstVisible);
        if (mIsFirstVisible) {
            if (!visible) return;
            enqueueDispatchVisible();
        } else {
            dispatchSupportVisible(visible);
        }
    }

    private void enqueueDispatchVisible() {
        debug("enqueueDispatchVisible");
        getHandler().post(() -> dispatchSupportVisible(true));
    }

    private void dispatchSupportVisible(boolean visible) {
        boolean isParentInvisible = isParentInvisible();
        debug("dispatchSupportVisible visible=" + visible + " isParentInvisible=" + isParentInvisible);
        if (visible && isParentInvisible) return;

        if (mIsSupportVisible == visible) {
            mNeedDispatch = true;
            return;
        }

        mIsSupportVisible = visible;

        if (visible) {
            if (checkAddState()) return;
            mSupportF.onSupportVisible();

            if (mIsFirstVisible) {
                mIsFirstVisible = false;
                mSupportF.onLazyInitView(mSaveInstanceState);
                mIsLazyInit = true;
            }
            dispatchChild(true);
        } else {
            dispatchChild(false);
            mSupportF.onSupportInvisible();
        }
    }

    private void dispatchChild(boolean visible) {
        if (!mNeedDispatch) {
            mNeedDispatch = true;
        } else {
            if (checkAddState()) return;
            FragmentManager fragmentManager = mFragment.getChildFragmentManager();
            List<Fragment> childFragments = FragmentationMagician.getActiveFragments(fragmentManager);
            for (Fragment child : childFragments) {
                if (child instanceof ISupportFragment && !child.isHidden() && child.getUserVisibleHint()) {
                    ((ISupportFragment) child).getSupportDelegate().getVisibleDelegate().dispatchSupportVisible(visible);
                }
            }
        }
    }

    private boolean isParentInvisible() {
        Fragment parentFragment = mFragment.getParentFragment();
        String parentName = parentFragment == null ? "null" : parentFragment.getClass().getSimpleName();
        debug("isParentInvisible parentFragment=" + parentName);
        if (!(mFragment instanceof AbstractDialogFragment) && parentFragment instanceof ISupportFragment) {
            return !((ISupportFragment) parentFragment).isSupportVisible();
        }

        return parentFragment != null && !parentFragment.isVisible();
    }

    private boolean checkAddState() {
        if (!mFragment.isAdded()) {
            mIsSupportVisible = !mIsSupportVisible;
            return true;
        }
        return false;
    }

    private boolean isFragmentVisible(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        return !fragment.isHidden() && fragment.getUserVisibleHint();
    }

    public boolean isSupportVisible() {
        return mIsSupportVisible;
    }

    public boolean isLazyInit() {
        return mIsLazyInit;
    }

    private Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    private void debug(String msg) {
        if (Fragmentation.getDefault().isDebug()) {
            Log.d(TAG, mFragment.getClass().getSimpleName() + " " + msg);
        }
    }

}
