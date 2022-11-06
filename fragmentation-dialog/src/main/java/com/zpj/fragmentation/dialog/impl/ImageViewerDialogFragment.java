package com.zpj.fragmentation.dialog.impl;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeImageTransform;
import androidx.transition.ChangeTransform;
import androidx.transition.Transition;
import androidx.transition.TransitionListenerAdapter;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;
import androidx.transition.TransitionValues;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.DialogAnimator;
import com.zpj.fragmentation.dialog.base.BaseDialogFragment;
import com.zpj.fragmentation.dialog.interfaces.IProgressViewHolder;
import com.zpj.fragmentation.dialog.interfaces.OnDragChangeListener;
import com.zpj.fragmentation.dialog.utils.ImageLoader;
import com.zpj.fragmentation.dialog.utils.DefaultImageLoader;
import com.zpj.fragmentation.dialog.widget.HackyViewPager;
import com.zpj.fragmentation.dialog.widget.ImageViewContainer;
import com.zpj.fragmentation.dialog.widget.PhotoViewContainer;
import com.zpj.fragmentation.dialog.widget.PlaceholderImageView;

import java.util.ArrayList;
import java.util.List;


public class ImageViewerDialogFragment<T> extends BaseDialogFragment<ImageViewerDialogFragment<T>>
        implements OnDragChangeListener {

    protected FrameLayout container;
    protected PhotoViewContainer photoViewContainer;
    protected HackyViewPager pager;
    protected ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    protected final List<T> urls = new ArrayList<>();
    protected ImageLoader<T> loader = new DefaultImageLoader<>();
    protected OnSrcViewUpdateListener<T> srcViewUpdateListener;
    protected int position;
    protected Rect rect = null;
    protected ImageView srcView; //动画起始的View，如果为null，移动和过渡动画效果会没有，只有弹窗的缩放功能
    protected PlaceholderImageView snapshotView;
    protected boolean isAnimationEnd;
    protected int placeholderColor = -1; //占位View的颜色
    protected int placeholderStrokeColor = -1; // 占位View的边框色
    protected int placeholderRadius = -1; // 占位View的圆角
    protected boolean isInfinite = false;//是否需要无限滚动
    protected View customView;
    protected int bgColor = Color.rgb(32, 36, 46);//弹窗的背景颜色，可以自定义

    protected IProgressViewHolder<? extends View> progressViewHolder = new IProgressViewHolder<ProgressBar>() {
        @Override
        public ProgressBar createProgressView(Context context) {
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.setMax(100);
            return progressBar;
        }

        @Override
        public void onProgressChanged(ProgressBar progressView, float progress) {
            progressView.setProgress((int) progress * 100);
        }
    };

    private int backgroundColor = Color.TRANSPARENT;

    public interface OnSrcViewUpdateListener<T> {
        void onSrcViewUpdate(@NonNull ImageViewerDialogFragment<T> popup, int position);
    }

    @Override
    protected final int getImplLayoutId() {
        return R.layout._dialog_layout_image_viewer;
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected DialogAnimator onCreateDialogAnimator(ViewGroup contentView) {
        return null;
    }

    @Override
    protected DialogAnimator onCreateShadowAnimator(FrameLayout flContainer) {
        return null;
    }

    protected int getCustomLayoutId() {
        return 0;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        photoViewContainer = findViewById(R.id.photoViewContainer);

        container = findViewById(R.id._image_viewer_dialog_fl_container);
        if (getCustomLayoutId() > 0) {
            customView = getLayoutInflater().inflate(getCustomLayoutId(), null, false);
            customView.setVisibility(View.INVISIBLE);
            customView.setAlpha(0);
            container.addView(customView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        photoViewContainer.setOnDragChangeListener(this);
        photoViewContainer.setFocusableInTouchMode(true);
        photoViewContainer.setFocusable(true);
        photoViewContainer.setClickable(true);
        pager = findViewById(R.id.pager);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                position = i;
                //更新srcView
                if (isAnimationEnd) {
                    if (srcViewUpdateListener != null) {
                        srcViewUpdateListener.onSrcViewUpdate(ImageViewerDialogFragment.this, i);
                    }
                    ImageViewContainer itemView = pager.findViewWithTag(position);
                    if (itemView != null) {
                        itemView.showPlaceholder(srcView.getDrawable());
                    }
                }

//                if (callback != null) {
//                    srcView = callback.getImageView(position);
//                    srcView.setVisibility(View.INVISIBLE);
//                    int[] locations = new int[2];
//                    srcView.getLocationInWindow(locations);
//                    rect = new Rect(locations[0], locations[1], locations[0] + srcView.getWidth(), locations[1] + srcView.getHeight());
//                }
            }
        });
        pager.setAdapter(new PhotoViewAdapter());
//        pager.setOffscreenPageLimit(1);
        pager.setCurrentItem(position);
        pager.setVisibility(View.INVISIBLE);
//        addOrUpdateSnapshot();
//        if (isInfinite) pager.setOffscreenPageLimit(urls.size() / 2);
    }

    private class ImageViewerDialogAnimator implements DialogAnimator {

        @Override
        public void setShowDuration(long showAnimDuration) {

        }

        @Override
        public void setDismissDuration(long dismissAnimDuration) {

        }

        @Override
        public void animateToShow() {
            _doShowAnimation();
        }

        @Override
        public void animateToDismiss() {
            postOnEnterAnimationEnd(ImageViewerDialogFragment.this::_doDismissAnimation);
        }

        @Override
        public void setAnimationListener(Listener listener) {

        }
    }

    private static abstract class UpdateTransition extends Transition {

        public UpdateTransition() {

        }

        @Override
        public void captureStartValues(@NonNull TransitionValues transitionValues) {

        }

        @Override
        public void captureEndValues(@NonNull TransitionValues transitionValues) {

        }

        @Nullable
        @Override
        public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
            ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
            animator.addUpdateListener(valueAnimator -> {
                onTransitionUpdate((float) (valueAnimator.getAnimatedValue()));
            });
            return animator;
        }

        public abstract void onTransitionUpdate(float percent);

    }

    public void _doShowAnimation() {
        if (srcViewUpdateListener != null) {
            srcViewUpdateListener.onSrcViewUpdate(ImageViewerDialogFragment.this, position);
        } else {
            addOrUpdateSnapshot();
        }

//        addOrUpdateSnapshot();

        if (customView != null) customView.setVisibility(View.VISIBLE);
        if (srcView == null) {
//            photoViewContainer.setBackgroundColor(bgColor);
            pager.setVisibility(View.VISIBLE);
//            showPagerIndicator();
//            photoViewContainer.isReleasing = false;
//            doAfterShow();
//            if (customView != null)
//                customView.setAlpha(1f);
            animateShadowBg(bgColor, getShowAnimDuration(), new DialogAnimator.Listener() {
                @Override
                public void onAnimationStart() {
                    onShowAnimationStart(getSavedInstanceState());
                }

                @Override
                public void onAnimationEnd() {
                    photoViewContainer.isReleasing = false;
                    onShowAnimationEnd(getSavedInstanceState());
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationUpdate(float value) {
                    onShowAnimationUpdate(value);
                    pager.setScaleX(value);
                    pager.setScaleY(value);
                    if (customView != null) {
                        customView.setAlpha(value);
                    }
                }
            });
            return;
        }
        photoViewContainer.isReleasing = true;
        snapshotView.setVisibility(View.VISIBLE);
        snapshotView.setScaleType(srcView.getScaleType());
        snapshotView.post(() -> {
            ImageViewContainer itemView = pager.findViewWithTag(position);
            if (itemView != null && srcView != null) {
//                                    itemView.getPhotoView().setDrawable(srcView.getDrawable());
                itemView.showPlaceholder(srcView.getDrawable());
            }
            TransitionManager.beginDelayedTransition((ViewGroup) snapshotView.getParent(), new TransitionSet()
                    .setDuration(getShowAnimDuration())
                    .addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform())
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .addListener(new TransitionListenerAdapter() {

                        @Override
                        public void onTransitionEnd(@NonNull Transition transition) {
                            isAnimationEnd = true;

                            pager.setVisibility(View.VISIBLE);
//                                actionQueue.start();
                            snapshotView.setVisibility(View.INVISIBLE);
                            photoViewContainer.isReleasing = false;
                        }
                    }));
            snapshotView.setTranslationY(0);
            snapshotView.setTranslationX(0);
//                snapshotView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            snapshotView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            setWidthHeight(snapshotView, photoViewContainer.getWidth(), photoViewContainer.getHeight());

            // do shadow anim.
            animateShadowBg(bgColor, getShowAnimDuration(), new DialogAnimator.Listener() {
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
                    if (customView != null) {
                        customView.setAlpha(percent);
                    }
                }
            });
//                XPopup.getAnimationDuration()
//                if (customView != null)
//                    customView.animate().alpha(1f).setDuration(DEFAULT_ANIM_DURATION).start();
        });

    }

    protected void loadNewUrl(int position, T url) {
        ImageViewContainer ivContainer = pager.findViewWithTag(position);
        ivContainer.showProgressBar();
        if (loader == null) {
            loader = new DefaultImageLoader<>();
        }
        loader.loadImage(url, new ImageLoader.LoadCallback() {
            @Override
            public void progress(float progress) {
                ivContainer.setProgress(progress);
            }

            @Override
            public void loadFinish(Drawable drawable) {
                ivContainer.onLoadFinished();
            }
        }, ivContainer, String.valueOf(ivContainer.hashCode()));
    }

    private void addOrUpdateSnapshot() {
        if (srcView == null) return;
        if (snapshotView == null) {
            snapshotView = new PlaceholderImageView(getContext());
            photoViewContainer.addView(snapshotView);
            snapshotView.setScaleType(srcView.getScaleType());
//            int offset = ScreenUtils.getScreenHeight(context) - getRootView().getMeasuredHeight();
            int[] rootLocations = new int[2];
            getRootView().getLocationOnScreen(rootLocations);
            int offset = rootLocations[1];
            snapshotView.setTranslationX(rect.left);
            snapshotView.setTranslationY(rect.top - offset);
            setWidthHeight(snapshotView, rect.width(), rect.height());
        }
        snapshotView.setImageDrawable(srcView.getDrawable());
    }

    private void animateShadowBg(final int endColor, long duration, DialogAnimator.Listener listener) {
        int start = backgroundColor;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
        animator.addUpdateListener(animation -> {
//                photoViewContainer.setBackgroundColor((Integer) argbEvaluator.evaluate(animation.getAnimatedFraction(),
//                        start, endColor));
            float value = (float) animation.getAnimatedValue();
            backgroundColor = (Integer) argbEvaluator.evaluate(value,
                    start, endColor);
            photoViewContainer.setBackgroundColor(backgroundColor);
            if (listener != null) {
                listener.onAnimationUpdate(value);
//                if (value == 1f) {
//                    listener.onAnimationEnd();
//                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null) {
                    listener.onAnimationStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (listener != null) {
                    listener.onAnimationCancel();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(duration)
                .setInterpolator(new LinearInterpolator());
        animator.start();
    }

    public void _doDismissAnimation() {
        if (srcViewUpdateListener != null) {
            srcViewUpdateListener.onSrcViewUpdate(ImageViewerDialogFragment.this, position);
        }
        if (srcView != null) {
            ImageViewContainer current = pager.findViewWithTag(pager.getCurrentItem());
            if (current != null) {
                Matrix matrix = current.getPhotoView().getSupportMatrix();
                if (matrix != null) {
                    snapshotView.setSupportMatrix(matrix);
                }

            }
        }
        if (srcView == null) {
//            photoViewContainer.setBackgroundColor(Color.TRANSPARENT);
//            doAfterDismiss();
//            pager.setVisibility(View.INVISIBLE);
//            placeholderView.setVisibility(View.INVISIBLE);
            float startScaleX = pager.getScaleX();
            float startScaleY = pager.getScaleY();
            final float startAlpha;
            if (customView == null) {
                startAlpha = 1f;
            } else {
                startAlpha = customView.getAlpha();
            }
            animateShadowBg(Color.TRANSPARENT, getDismissAnimDuration(), new DialogAnimator.Listener() {
                @Override
                public void onAnimationStart() {
                    onDismissAnimationStart();
                }

                @Override
                public void onAnimationEnd() {
//                    actionQueue.onDestroy();
                    pager.setVisibility(View.INVISIBLE);
                    onDismissAnimationEnd();
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationUpdate(float value) {
                    pager.setScaleX((1 - value) * startScaleX);
                    pager.setScaleY((1 - value) * startScaleY);
                    if (customView != null) {
                        customView.setAlpha((1 - value) * startAlpha);
                    }
                    onDismissAnimationUpdate(value);
                }
            });
            return;
        }
        TransitionManager.endTransitions((ViewGroup) snapshotView.getParent());
//        ImageViewContainer current = pager.findViewWithTag(position);
//        if (current.getPlaceholder().getDrawable() instanceof GifDrawable) {
//            srcView.setImageDrawable(current.getPlaceholder().getDrawable());
//            snapshotView.setImageDrawable(current.getPlaceholder().getDrawable());
//        }
        pager.setVisibility(View.INVISIBLE);
        snapshotView.setVisibility(View.VISIBLE);
        photoViewContainer.isReleasing = true;
        Log.d("ImageViewerPopup", "snapshotView.getImageMatrix()=" + snapshotView.getImageMatrix());
        TransitionManager.beginDelayedTransition((ViewGroup) snapshotView.getParent(), new TransitionSet()
                .setDuration(getDismissAnimDuration())
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform())
                .addTransition(new ChangeImageTransform())
                .setInterpolator(new FastOutSlowInInterpolator())
                .addListener(new TransitionListenerAdapter() {

                    @Override
                    public void onTransitionEnd(@NonNull Transition transition) {
//                        actionQueue.onDestroy();
                        ImageViewContainer current = pager.findViewWithTag(position);
                        if (current.getPlaceholder().getDrawable() instanceof GifDrawable) {
                            srcView.setImageDrawable(current.getPlaceholder().getDrawable());
                        }
                    }
                }));

//        int offset = ScreenUtils.getScreenHeight(context) - getRootView().getMeasuredHeight();
        int[] rootLocations = new int[2];
        getRootView().getLocationOnScreen(rootLocations);
        int offset = rootLocations[1];
        snapshotView.setTranslationY(rect.top - offset);
        snapshotView.setTranslationX(rect.left);
        snapshotView.setScaleX(1f);
        snapshotView.setScaleY(1f);
        snapshotView.setScaleType(srcView.getScaleType());
        setWidthHeight(snapshotView, rect.width(), rect.height());

        // do shadow anim.
        animateShadowBg(Color.TRANSPARENT, getDismissAnimDuration(), new DialogAnimator.Listener() {
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
                if (customView != null) {
                    customView.setAlpha(1 -  percent);
                }
                onDismissAnimationUpdate(percent);
            }
        });
    }

    @Override
    protected boolean onBackPressed() {
        dismiss();
        return true;
    }

    public ImageViewerDialogFragment<T> setImageLoader(ImageLoader<T> loader) {
        this.loader = loader;
        return this;
    }

    public ImageViewerDialogFragment<T> setProgressViewHolder(IProgressViewHolder<? extends View> progressViewHolder) {
        this.progressViewHolder = progressViewHolder;
        return this;
    }

    public ImageViewerDialogFragment<T> setImageUrls(List<T> urls) {
        this.urls.addAll(urls);
        return this;
    }

    public ImageViewerDialogFragment<T> setSrcViewUpdateListener(OnSrcViewUpdateListener<T> srcViewUpdateListener) {
        this.srcViewUpdateListener = srcViewUpdateListener;
        return this;
    }

    public ImageViewerDialogFragment<T> isInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;
        return this;
    }

    public ImageViewerDialogFragment<T> setPlaceholderColor(int color) {
        this.placeholderColor = color;
        return this;
    }

    public ImageViewerDialogFragment<T> setPlaceholderRadius(int radius) {
        this.placeholderRadius = radius;
        return this;
    }

    public ImageViewerDialogFragment<T> setPlaceholderStrokeColor(int strokeColor) {
        this.placeholderStrokeColor = strokeColor;
        return this;
    }

    /**
     * 设置单个使用的源View。单个使用的情况下，无需设置url集合和SrcViewUpdateListener
     *
     * @param srcView
     * @return
     */
    public ImageViewerDialogFragment<T> setSingleSrcView(ImageView srcView, T url) {
        urls.clear();
        urls.add(url);
        setSrcView(srcView, 0);
        return this;
    }

    public ImageViewerDialogFragment<T> setSrcView(ImageView srcView, int position) {
        this.srcView = srcView;
        this.position = position;
        if (srcView != null) {
            int[] locations = new int[2];
            this.srcView.getLocationInWindow(locations);
//            int offset;
//            if (getActivity() != null && (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    & getActivity().getWindow().getAttributes().flags)
//                    == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
//                offset = 0;
//            } else {
//                offset = ScreenUtils.getStatusBarHeight(srcView.getContext());
//            }
//            locations[1] = locations[1] - offset;
            rect = new Rect(locations[0], locations[1], locations[0] + srcView.getWidth(), locations[1] + srcView.getHeight());
            Log.d("ImageViewerDialog", "rect=" + rect);
        }
        return this;
    }

    public void updateSrcView(ImageView srcView) {
        setSrcView(srcView, position);
        addOrUpdateSnapshot();
    }

    public void updateSrcView(ImageView srcView, int position) {
        if (this.position != position) {
            return;
        }
        setSrcView(srcView, position);
        addOrUpdateSnapshot();
    }

    @Override
    public void onRelease() {
        dismiss();
    }

    @Override
    public void onDragChange(int dy, float scale, float fraction) {
        if (customView != null) customView.setAlpha(1 - fraction);
        backgroundColor = (int) argbEvaluator.evaluate(fraction * .8f, bgColor, Color.TRANSPARENT);
        photoViewContainer.setBackgroundColor(backgroundColor);
    }

    public class PhotoViewAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return isInfinite ? Integer.MAX_VALUE / 2 : urls.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return o == view;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            final ImageViewContainer ivContainer = new ImageViewContainer(container.getContext());
            ivContainer.setProgressViewHolder(progressViewHolder);
            ivContainer.setTag(position);
            // call LoadImageListener
            ivContainer.showProgressBar();
            if (loader == null) {
                loader = new DefaultImageLoader<>();
            }
            loader.loadImage(urls.get(position), new ImageLoader.LoadCallback() {
                @Override
                public void progress(float progress) {
                    ivContainer.setProgress(progress);
                }

                @Override
                public void loadFinish(Drawable drawable) {
                    ivContainer.onLoadFinished();
                }
            }, ivContainer, String.valueOf(ivContainer.hashCode()));
            container.addView(ivContainer);
//            ivContainer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dismiss();
//                }
//            });
            return ivContainer;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    public static void setWidthHeight(View target, int width, int height) {
        if (width <= 0 && height <= 0) return;
        ViewGroup.LayoutParams params = target.getLayoutParams();
        if (width > 0) params.width = width;
        if (height > 0) params.height = height;
        target.setLayoutParams(params);
    }


}
