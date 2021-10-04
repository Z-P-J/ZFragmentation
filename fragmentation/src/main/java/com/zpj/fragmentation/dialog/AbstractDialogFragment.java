package com.zpj.fragmentation.dialog;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.zpj.fragmentation.ISupportFragment;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.SupportHelper;
import com.zpj.fragmentation.anim.DefaultNoAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;

import java.lang.ref.WeakReference;

/**
 * This class is exposed to ZFragmentation-Dialog library.
 *
 * @author Z-P-J
 */
public abstract class AbstractDialogFragment extends SupportFragment {

    protected long showAnimDuration = 360;
    protected long dismissAnimDuration = 360;

    private WeakReference<ISupportFragment> preFragment;

    protected boolean isDismissing;

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView(View view, @Nullable Bundle savedInstanceState);

    protected abstract void doShowAnimation();

    protected abstract void doDismissAnimation();

    protected abstract boolean onBackPressed();

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ISupportFragment fragment = getPreFragment();
        if (fragment == null && getParentFragment() instanceof ISupportFragment) {
            fragment = (ISupportFragment) getParentFragment();
        }
        preFragment = new WeakReference<>(fragment);
        mDelegate.debug("onCreateView preFragment=" + preFragment.get());
        if (getLayoutId() > 0) {
            view = inflater.inflate(getLayoutId(), container, false);
        } else {
            view = super.onCreateView(inflater, container, savedInstanceState);
        }
        initView(view, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        doShowAnimation();
                        mDelegate.debug("doShowAnimation");
                        return false;
                    }
                });
    }

    @Deprecated
    @Override
    public final void setFragmentAnimator(FragmentAnimator fragmentAnimator) {

    }

    @Override
    public final FragmentAnimator getFragmentAnimator() {
        return onCreateFragmentAnimator();
    }

    @Override
    public final FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }

    /**
     * 尽量别重写该方法
     * @param savedInstanceState
     */
    @Deprecated
    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ISupportFragment fragment = null;
                if (preFragment != null) {
                    fragment = preFragment.get();
                    preFragment.clear();
                }
//                if (fragment == null) {
//                    fragment = getPreFragment();
//                    if (fragment == null && getParentFragment() instanceof ISupportFragment) {
//                        fragment = (ISupportFragment) getParentFragment();
//                    }
//                }

                if (fragment == null) {
                    fragment = getPreFragment();
                }

                if (fragment instanceof Fragment) {
                    if (!((Fragment) fragment).isAdded() || ((Fragment) fragment).isRemoving() || ((Fragment) fragment).isHidden()) {
                        fragment = null;
                    } else if (fragment instanceof AbstractDialogFragment) {
                        if (((AbstractDialogFragment) fragment).isDismissing()) {
                            fragment = null;
                        }
                    }
                }

                if (fragment == null && getParentFragment() instanceof ISupportFragment) {
                    fragment = (ISupportFragment) getParentFragment();
                }

                if (fragment == null) {
                    preFragment = null;
                } else {
                    preFragment = new WeakReference<>(fragment);
                }

                mDelegate.debug("onEnterAnimationEnd preFragment=" + fragment);
                if (fragment instanceof AbstractDialogFragment
                        && ((AbstractDialogFragment) fragment).isDismissing) {

                } else if (fragment instanceof SupportFragment
                        && (fragment == getPreFragment() || (getPreFragment() == null && fragment == getParentFragment()))
                        && ((SupportFragment) fragment).isVisible()
                        && fragment.isSupportVisible()
                        && !((SupportFragment) fragment).isHidden()
                        && ((SupportFragment) fragment).getUserVisibleHint()) {
                    mCanPause = false;
                    ((SupportFragment) fragment).onPause();
                    mCanPause = true;
                }
                onShowAnimationEnd(savedInstanceState);
            }
        }, getShowAnimDuration());
    }

    private boolean mCanPause = true;

    public boolean isCanPause() {
        return mCanPause;
    }

    public void onShowAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        mDelegate.debug("onShowAnimationEnd");
    }

    @Override
    public final boolean onBackPressedSupport() {
        if (onBackPressed()) {
            return true;
        }
        dismiss();
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        this.isDismissing = false;
        mCanPause = true;
        super.onDestroy();
    }

    public long getShowAnimDuration() {
        if (showAnimDuration < 0) {
            return 0;
        }
        return showAnimDuration;
    }

    public long getDismissAnimDuration() {
        if (dismissAnimDuration < 0) {
            return 0;
        }
        return dismissAnimDuration;
    }

    /**
     * 尽量别重写该方法
     */
    public void dismiss() {
        postOnEnterAnimationEnd(() -> {
            if (!isDismissing) {
                isDismissing = true;
                doDismissAnimation();
                mDelegate.debug("doDismissAnimation");

                onDismiss();
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popThis();
                        mDelegate.debug("dismissed");
                    }
                }, getDismissAnimDuration());
            }
        });
    }

    @Override
    public void pop() {
        dismiss();
    }

    @Override
    public final void popThis() {
        mDelegate.debug("popThis");
        mDelegate.debug("preFragment=" + preFragment);
        ISupportFragment fragment = null;
        if (preFragment != null) {
            fragment = preFragment.get();
            mDelegate.debug("dismiss preFragment.get=" + fragment);
            preFragment.clear();
            preFragment = null;
        }

        if (fragment == null) {
            fragment = getPreFragment();
        }

        if (fragment instanceof Fragment) {
            mDelegate.debug("popThis fragment=" + fragment);
            mDelegate.debug("popThis isDetached=" + ((Fragment) fragment).isDetached());
            mDelegate.debug("popThis isVisible=" + ((Fragment) fragment).isVisible());
            mDelegate.debug("popThis isAdded=" + ((Fragment) fragment).isAdded());
            mDelegate.debug("popThis isRemoving=" + ((Fragment) fragment).isRemoving());
            mDelegate.debug("popThis isHidden=" + ((Fragment) fragment).isHidden());
            mDelegate.debug("popThis isResumed=" + ((Fragment) fragment).isResumed());
            mDelegate.debug("popThis isInLayout=" + ((Fragment) fragment).isInLayout());
            if (!((Fragment) fragment).isAdded() || ((Fragment) fragment).isRemoving() || ((Fragment) fragment).isHidden()) {
                fragment = null;
            } else if (fragment instanceof AbstractDialogFragment) {
                if (((AbstractDialogFragment) fragment).isDismissing()) {
                    fragment = null;
                }
            }
        }

        if (fragment == null && getParentFragment() instanceof ISupportFragment) {
            fragment = (ISupportFragment) getParentFragment();
        }

        if (getTopFragment() != AbstractDialogFragment.this) {
            fragment = null;
        }

        mDelegate.debug("dismiss fragment=" + fragment);

        if (fragment instanceof AbstractDialogFragment
                && ((AbstractDialogFragment) fragment).isDismissing) {

        } else if (fragment instanceof SupportFragment
                && (fragment == getPreFragment() || (getPreFragment() == null && fragment == getParentFragment()))
                && ((SupportFragment) fragment).isVisible() && !fragment.isSupportVisible()) {
            ((SupportFragment) fragment).onResume();
        }
        super.popThis();
    }

    public boolean isDismissing() {
        return isDismissing;
    }

    protected abstract void onDismiss();

}
