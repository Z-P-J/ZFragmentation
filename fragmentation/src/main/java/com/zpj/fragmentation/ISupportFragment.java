package com.zpj.fragmentation;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.zpj.fragmentation.anim.FragmentAnimator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by YoKey on 17/6/23.
 * Modified by Z-P-J
 */
public interface ISupportFragment extends ISupport<SupportFragmentDelegate> {
    // LaunchMode
    int STANDARD = 0;
    int SINGLETOP = 1;
    int SINGLETASK = 2;

    // ResultCode
    int RESULT_CANCELED = 0;
    int RESULT_OK = -1;

    @IntDef({STANDARD, SINGLETOP, SINGLETASK})
    @Retention(RetentionPolicy.SOURCE)
    @interface LaunchMode {
    }

    void onEnterAnimationEnd(@Nullable Bundle savedInstanceState);

    void onLazyInitView(@Nullable Bundle savedInstanceState);

    void onSupportVisible();

    void onSupportInvisible();

    boolean isSupportVisible();

    boolean isLazyInit();

    FragmentAnimator onCreateFragmentAnimator();

    void setFragmentResult(int resultCode, Bundle bundle);

    void onFragmentResult(int requestCode, int resultCode, Bundle data);

    void onNewBundle(Bundle args);

    void putNewBundle(Bundle newBundle);

    boolean onBackPressedSupport();

    @LaunchMode
    int getLaunchMode();

    void postOnLazyInit(final Runnable runnable);

    void postOnLazyInitDelayed(final Runnable runnable, long delay);

    void postOnEnterAnimationEnd(final Runnable runnable);

    void postOnEnterAnimationEndDelayed(final Runnable runnable, long delay);

    void postOnSupportVisible(final Runnable runnable);

    void postOnSupportVisibleDelayed(final Runnable runnable, long delay);

}
