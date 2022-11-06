package com.zpj.fragmentation.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.zpj.fragmentation.ISupportFragment;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.anim.DefaultNoAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;

import java.lang.ref.WeakReference;

/**
 * This class is exposed to ZFragmentation-Dialog library.
 *
 * @author Z-P-J
 */
public abstract class AbstractDialogFragment extends SupportFragment {

    protected DialogAnimator mDialogAnimator;

    protected long showAnimDuration = 360;
    protected long dismissAnimDuration = 360;

    private WeakReference<ISupportFragment> preFragment;

    protected boolean isDismissing;

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView(View view, @Nullable Bundle savedInstanceState);

    protected abstract DialogAnimator onCreateDialogAnimator();

    protected abstract boolean onBackPressed();

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (isDismissing()) {
            return null;
        }
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

    private Bundle mSavedInstanceState;

    /**
     * 尽量别重写该方法
     * @param savedInstanceState
     */
    @Deprecated
    @Override
    public final void onEnterAnimationEnd(Bundle savedInstanceState) {
        this.mSavedInstanceState = savedInstanceState;
    }

    protected Bundle getSavedInstanceState() {
        return mSavedInstanceState;
    }

    private boolean mCanPause = true;

    public boolean isCanPause() {
        return mCanPause;
    }

    public void onShowAnimationStart(Bundle savedInstanceState) {
        ISupportFragment fragment = null;
        if (preFragment != null) {
            fragment = preFragment.get();
            preFragment.clear();
        }
        mDelegate.debug("onShowAnimationStart preFragment1=" + fragment);

        if (fragment == null) {
            fragment = getPreFragment();
        }
        mDelegate.debug("onShowAnimationStart preFragment2=" + fragment);

        if (fragment instanceof Fragment) {
            if (!((Fragment) fragment).isAdded() || ((Fragment) fragment).isRemoving() || ((Fragment) fragment).isHidden()) {
                fragment = ((SupportFragment) fragment).getPreFragment();
            } else if (fragment instanceof AbstractDialogFragment) {
                if (((AbstractDialogFragment) fragment).isDismissing()) {
                    fragment = ((AbstractDialogFragment) fragment).getPreFragment();
                }
            }
        }
        mDelegate.debug("onShowAnimationStart preFragment3=" + fragment);

        if (fragment == null && getParentFragment() instanceof ISupportFragment) {
            fragment = (ISupportFragment) getParentFragment();
        }

        if (fragment == null) {
            preFragment = null;
        } else {
            preFragment = new WeakReference<>(fragment);
        }

        mDelegate.debug("onShowAnimationStart preFragment4=" + fragment);
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
    }

    public void onShowAnimationUpdate(float percent) {
        mDelegate.debug("onShowAnimationUpdate percent=" + percent);
    }

    public void onShowAnimationEnd(Bundle savedInstanceState) {
        mDelegate.debug("onShowAnimationEnd");
        super.onEnterAnimationEnd(savedInstanceState);
        this.mSavedInstanceState = null;
    }

    public void onDismissAnimationStart() {
        mDelegate.debug("onDismissAnimationStart");
    }

    public void onDismissAnimationUpdate(float percent) {
        mDelegate.debug("onDismissAnimationUpdate percent=" + percent);
    }

    public void onDismissAnimationEnd() {
        mDelegate.debug("onDismissAnimationEnd");
        if (getView() != null) {
            getView().setVisibility(View.GONE);
        }
        popThis();
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
    public void onDestroy() {
        this.isDismissing = false;
        mCanPause = true;
        super.onDestroy();
    }

    protected void doShowAnimation() {
        mDelegate.debug("doShowAnimation");
        if (mDialogAnimator == null) {
            mDialogAnimator = onCreateDialogAnimator();
        }
        if (mDialogAnimator != null) {
            mDialogAnimator.setShowDuration(getShowAnimDuration());
            mDialogAnimator.setDismissDuration(getDismissAnimDuration());
            mDialogAnimator.setAnimationListener(new DialogAnimator.Listener() {
                @Override
                public void onAnimationStart() {
                    onShowAnimationStart(getSavedInstanceState());
                }

                @Override
                public void onAnimationEnd() {
                    onShowAnimationEnd(getSavedInstanceState());
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationUpdate(float percent) {
                    onShowAnimationUpdate(percent);
                }
            });
            mDialogAnimator.animateToShow();
        } else {
            onShowAnimationStart(getSavedInstanceState());
            onShowAnimationEnd(getSavedInstanceState());
        }
    }

    protected void doDismissAnimation() {
        if (mDialogAnimator != null) {
            mDialogAnimator.setDismissDuration(getDismissAnimDuration());
            mDialogAnimator.setAnimationListener(new DialogAnimator.Listener() {
                @Override
                public void onAnimationStart() {
                    onDismissAnimationStart();
                }

                @Override
                public void onAnimationEnd() {
                    onDismissAnimationEnd();
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationUpdate(float percent) {
                    onDismissAnimationUpdate(percent);
                }
            });
            mDialogAnimator.animateToDismiss();
        } else {
            onDismissAnimationStart();
            onDismissAnimationEnd();
        }
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
        if (!isDismissing) {
            isDismissing = true;
            doDismissAnimation();
            mDelegate.debug("doDismissAnimation");

            onDismiss();
        }
    }

    @Override
    public void pop() {
        dismiss();
    }

    @Override
    public final void popThis() {
        isDismissing = true;
        mDelegate.debug("popThis");
        mDelegate.debug("preFragment=" + preFragment);
        ISupportFragment fragment = null;
        if (preFragment != null) {
            fragment = preFragment.get();
            mDelegate.debug("dismiss preFragment.get=" + fragment);
            preFragment.clear();
            preFragment = null;
        }
        mDelegate.debug("popThis fragment1=" + fragment);

        if (fragment == null) {
            fragment = getPreFragment();
        }

        mDelegate.debug("popThis fragment2=" + fragment);

        if (fragment instanceof Fragment) {
            mDelegate.debug("popThis isDetached=" + ((Fragment) fragment).isDetached());
            mDelegate.debug("popThis isVisible=" + ((Fragment) fragment).isVisible());
            mDelegate.debug("popThis isAdded=" + ((Fragment) fragment).isAdded());
            mDelegate.debug("popThis isRemoving=" + ((Fragment) fragment).isRemoving());
            mDelegate.debug("popThis isHidden=" + ((Fragment) fragment).isHidden());
            mDelegate.debug("popThis isResumed=" + ((Fragment) fragment).isResumed());
            mDelegate.debug("popThis isInLayout=" + ((Fragment) fragment).isInLayout());
            if (!((Fragment) fragment).isAdded() || ((Fragment) fragment).isRemoving() || ((Fragment) fragment).isHidden()) {
                fragment = ((SupportFragment) fragment).getPreFragment();
            } else if (fragment instanceof AbstractDialogFragment) {
                if (((AbstractDialogFragment) fragment).isDismissing()) {
                    fragment = ((SupportFragment) fragment).getPreFragment();
                }
            }
        }

        mDelegate.debug("popThis fragment3=" + fragment);

        if (fragment == null && getParentFragment() instanceof ISupportFragment) {
            fragment = (ISupportFragment) getParentFragment();
        }

        mDelegate.debug("popThis fragment4=" + fragment);

        if (getTopFragment() != AbstractDialogFragment.this) {
            fragment = null;
        }

        mDelegate.debug("popThis fragment5=" + fragment);

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

    protected abstract void onCancel();

}
