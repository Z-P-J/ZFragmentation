package com.zpj.fragmentation.dialog.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.zpj.fragmentation.dialog.animator.ShadowMaskAnimator;
import com.zpj.fragmentation.dialog.enums.LayoutStatus;
import com.zpj.fragmentation.dialog.utils.LimitedOvershootInterpolator;
import com.zpj.utils.ScreenUtils;
import com.zpj.utils.ViewUtils;

public class OverDragLayout extends FrameLayout implements NestedScrollingParent {

    private static final String TAG = "OverDragLayout";

    private final ShadowMaskAnimator bgAnimator = new ShadowMaskAnimator(null);

    private final int mTouchSlop;

    private View child;
    private View contentView;
    OverScroller scroller;
    VelocityTracker tracker;

    boolean enableDrag = true;//是否启用手势
    boolean dismissOnTouchOutside = true;
    private boolean handleTouchOutsideEvent = true;
    boolean hasShadowBg = true;
    boolean isUserClose = false;
    LayoutStatus status = LayoutStatus.Close;

    protected long showDuration = 360;
    protected long dismissDuration = 360;

    private int mMaxOverScrollOffset;
    private int mContentHeight;
    private boolean mIsNestedScrollUp;
    private boolean mIsDragging;
    private boolean mIsNestedScrollAccepted;

    private ValueAnimator mAnimator;

    public void setShowDuration(long showDuration) {
        this.showDuration = showDuration;
        bgAnimator.setShowDuration(showDuration);
    }

    public void setDismissDuration(long dismissDuration) {
        this.dismissDuration = dismissDuration;
        bgAnimator.setDismissDuration(dismissDuration);
    }

    public OverDragLayout(Context context) {
        this(context, null);
    }

    public OverDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        if (enableDrag) {
            scroller = new OverScroller(context, new OvershootInterpolator());
        }
    }

    public void setMaxOverScrollOffset(int mMaxOverScrollOffset) {
        this.mMaxOverScrollOffset = mMaxOverScrollOffset;
    }

    @Override
    public void onViewAdded(View c) {
        super.onViewAdded(c);
        child = c;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (contentView == null) {
            return;
        }
        Log.d(TAG, "onLayout changed=" + changed + " left=" + left + " top=" + top + " right=" + right + " bottom=" + bottom + " status=" + status);
        if (status == LayoutStatus.Opening || status == LayoutStatus.Closing) {
            return;
        }
        if (mAnimator != null && mAnimator.isRunning()) {
            return;
        }

        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) contentView.getLayoutParams();
        mContentHeight = contentView.getMeasuredHeight() + params.topMargin + params.bottomMargin;
        int childHeight = mContentHeight + mMaxOverScrollOffset;
        int l = getMeasuredWidth() / 2 - child.getMeasuredWidth() / 2;
        if (enableDrag) {
            // horizontal center
            child.layout(l, getMeasuredHeight(), l + child.getMeasuredWidth(), getMeasuredHeight() + childHeight);
            if (status == LayoutStatus.Open) {
                //通过scroll上移
                scrollTo(getScrollX(), mContentHeight);
            }
        } else {
            // like bottom gravity
            child.layout(l, getMeasuredHeight() - mContentHeight, l + contentView.getMeasuredWidth(), getMeasuredHeight());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        isUserClose = true;
        Log.d(TAG, "dispatchTouchEvent action=" + MotionEvent.actionToString(ev.getAction()));
        if (mIsDragging) {
            super.dispatchTouchEvent(ev);
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!handleTouchOutsideEvent) {
            return super.onInterceptTouchEvent(event);
        }
        Log.d(TAG, "onInterceptTouchEvent action=" + MotionEvent.actionToString(event.getAction()));
        if (tracker == null) {
            tracker = VelocityTracker.obtain();
        }
        tracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsDragging = false;
                if (!scroller.isFinished()) {
                    scroller.forceFinished(true);
                }
                tracker.addMovement(event);
                touchX = event.getX();
                touchY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsNestedScrollAccepted) {
                    return false;
                }

                if (!mIsDragging) {
                    float dx = event.getX() - touchX;
                    float dy = event.getY() - touchY;
                    if (Math.abs(dx) < mTouchSlop && Math.abs(dy) < mTouchSlop) {
                        return false;
                    }
                    if (Math.abs(dy / dx) < 1) {
                        return false;
                    }
                }
                mIsDragging = true;
                return true;
        }
        return false;
    }

    float touchX, touchY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!handleTouchOutsideEvent) {
            return super.onTouchEvent(event);
        }
        Log.d(TAG, "onTouchEvent action=" + MotionEvent.actionToString(event.getAction()));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
//                mIsDragging = true;
                if (enableDrag) {
                    tracker.computeCurrentVelocity(1000);
                    int dy = (int) (event.getY() - touchY);

                    if (dy < 0) {
                        if (dy < -mMaxOverScrollOffset * 5) {
                            dy = -mMaxOverScrollOffset;
                        } else {
                            dy /= 5;
                        }
                    } else {
                        if (getScrollY() > mContentHeight) {
                            dy -= (getScrollY() - mContentHeight);
                        }
                    }

                    scrollTo(getScrollX(), mContentHeight - dy);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // click in child rect
                if (contentView == null) {
                    contentView = child;
                }
                Rect rect = new Rect();
                contentView.getGlobalVisibleRect(rect);
                if (!mIsDragging && !ViewUtils.isInRect(event.getRawX(), event.getRawY(), rect) && dismissOnTouchOutside) {
                    float distance = (float) Math.sqrt(Math.pow(event.getX() - touchX, 2) + Math.pow(event.getY() - touchY, 2));
                    if (distance < mTouchSlop) {
//                        performClick();
                        close();
                        break;
                    }
                }
                if (!mIsDragging && Math.abs(touchX - event.getX()) < mTouchSlop && Math.abs(touchX - event.getX()) < mTouchSlop) {
                    return super.onTouchEvent(event);
                }
                mIsDragging = false;
                if (enableDrag) {
                    float yVelocity = tracker.getYVelocity();
                    if (getScrollY() <= getMeasuredHeight() && yVelocity > 1500) {
                        close();
                    } else {
                        finishScroll(false);
                    }

                    tracker.clear();
                    tracker.recycle();
                    tracker = null;
                }

                break;
        }
        return true;
    }

    private void finishScroll(boolean isOpen) {
        if (enableDrag) {
            int contentHeight = mContentHeight;
            int dy;
            int duration;
            if (getScrollY() > contentHeight) {
                dy = contentHeight - getScrollY();
                duration = isOpen ? 200 : 360;
            } else {
                int threshold = isScrollUp ? contentHeight / 3 : contentHeight * 2 / 3;
                dy = (getScrollY() > threshold ? contentHeight : 0) - getScrollY();
                duration = (int) (dy > 0 ? showDuration : dismissDuration);
            }
            scroller.startScroll(getScrollX(), getScrollY(), 0, dy, duration);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    boolean isScrollUp;

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) y = 0;
        float fraction = y * 1f / mContentHeight;
        isScrollUp = y > getScrollY();
        if (hasShadowBg) {
            setBackgroundColor(bgAnimator.calculateBgColor(fraction));
        }
        if (y <= mContentHeight && listener != null) {
            if (isUserClose && fraction == 0f && status != LayoutStatus.Close) {
                status = LayoutStatus.Close;
                listener.onClose();
            } else if (fraction == 1f && status != LayoutStatus.Open) {
                status = LayoutStatus.Open;
                listener.onOpen();
            }
        }
        super.scrollTo(x, y);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isScrollUp = false;
        isUserClose = false;
        setTranslationY(0);
    }

    public void open() {
        createOpenAnimator().start();
    }

    public Animator createOpenAnimator() {
        scroller.forceFinished(true);
        status = LayoutStatus.Opening;

        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
        ViewGroup.MarginLayoutParams params = (MarginLayoutParams) contentView.getLayoutParams();
        mContentHeight = params.height + params.topMargin + params.bottomMargin;
        mAnimator = ValueAnimator.ofInt(0, mContentHeight);
        if (mMaxOverScrollOffset == 0) {
            mAnimator.setInterpolator(new FastOutSlowInInterpolator());
        } else {
            float topY = Math.min(1f + ScreenUtils.dp2px(8) / mContentHeight, 1f + mMaxOverScrollOffset * 0.25f / mContentHeight);
            mAnimator.setInterpolator(new LimitedOvershootInterpolator(0.68f, topY));
        }
        mAnimator.setDuration(showDuration);
        mAnimator.addUpdateListener(animation -> {
            int scrollY = (int) animation.getAnimatedValue();
            scrollTo(getScrollX(), scrollY);
        });
        return mAnimator;
    }

    public void close() {
        if (isClosing()) {
            return;
        }
        createCloseAnimator().start();
    }

    public Animator createCloseAnimator() {
        scroller.forceFinished(true);
        isUserClose = true;
        status = LayoutStatus.Closing;

        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
        mAnimator = ValueAnimator.ofInt(getScrollY(), 0);
        mAnimator.setDuration(dismissDuration);
        mAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mAnimator.addUpdateListener(animation -> {
            int scrollY = (int) animation.getAnimatedValue();
            scrollTo(getScrollX(), scrollY);
        });
        return mAnimator;
    }

    public boolean isClosing() {
        return status == LayoutStatus.Closing || status == LayoutStatus.Close;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && enableDrag;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        //必须要取消，否则会导致滑动初次延迟
        scroller.abortAnimation();
        mIsNestedScrollAccepted = true;
        Log.d(TAG, "onNestedScrollAccepted");
    }

    @Override
    public void onStopNestedScroll(View target) {
        if (status == LayoutStatus.Open) {
            finishScroll(false);
        }
        mIsNestedScrollAccepted = false;
        Log.d(TAG, "onStopNestedScroll");
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed != 0) {
            if (mIsNestedScrollUp) {
                dyUnconsumed /= 5;
            }
            int newY = getScrollY() + dyUnconsumed;
            scrollTo(getScrollX(), Math.min(newY, mContentHeight + mMaxOverScrollOffset));
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        mIsNestedScrollUp = dy > 0;
        int contentHeight = mContentHeight;
        if (mIsNestedScrollAccepted && getScrollY() < contentHeight) {
            int newY = getScrollY() + dy;
            if (newY < contentHeight) {
                consumed[1] = dy;
            } else {
                consumed[1] = contentHeight - getScrollY();
                newY = contentHeight;
            }
            scrollTo(getScrollX(), newY);
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        boolean isDragging = getScrollY() > 0 && getScrollY() < mContentHeight;
        if (isDragging && velocityY < -1500) {
            close();
        }
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    public void bindContentView(View contentView) {
        this.contentView = contentView;
    }

    public void enableDrag(boolean enableDrag) {
        this.enableDrag = enableDrag;
    }

    public void dismissOnTouchOutside(boolean dismissOnTouchOutside) {
        this.dismissOnTouchOutside = dismissOnTouchOutside;
    }

    public void handleTouchOutsideEvent(boolean handleTouchOutsideEvent) {
        this.handleTouchOutsideEvent = handleTouchOutsideEvent;
    }

    public void hasShadowBg(boolean hasShadowBg) {
        this.hasShadowBg = hasShadowBg;
    }

    private OnCloseListener listener;

    public void setOnCloseListener(OnCloseListener listener) {
        this.listener = listener;
    }

    public interface OnCloseListener {
        void onClose();

        void onOpen();
    }
}
