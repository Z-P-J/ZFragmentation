package com.zpj.fragmentation.dialog;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
        preFragment = new WeakReference<>(getPreFragment());
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
                }
                if (fragment == null) {
                    fragment = getPreFragment();
                }
                mDelegate.debug("onEnterAnimationEnd preFragment=" + fragment);
                if (fragment instanceof AbstractDialogFragment
                        && ((AbstractDialogFragment) fragment).isDismissing) {

                } else if (fragment instanceof SupportFragment && fragment == getPreFragment()
                        && ((SupportFragment) fragment).isVisible()) {
                    ((SupportFragment) fragment).onPause();
                }
                onShowAnimationEnd(savedInstanceState);
            }
        }, getShowAnimDuration());
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
                        mDelegate.debug("dismiss fragment=" + fragment);

                        if (fragment instanceof AbstractDialogFragment
                                && ((AbstractDialogFragment) fragment).isDismissing) {

                        } else if (fragment instanceof SupportFragment && fragment == getPreFragment()
                                && ((SupportFragment) fragment).isVisible()) {
                            ((SupportFragment) fragment).onResume();
                        }

                        mDelegate.pop(AbstractDialogFragment.this);
                        isDismissing = false;
                        mDelegate.debug("dismissed");
                    }
                }, getDismissAnimDuration());
            }
        });
    }

    protected abstract void onDismiss();

}
