package com.zpj.fragmentation.dialog.base;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.animator.DialogAnimator;
import com.zpj.fragmentation.dialog.animator.ScrollScaleAnimator;
import com.zpj.fragmentation.dialog.enums.DialogAnimation;
import com.zpj.fragmentation.dialog.enums.DialogPosition;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class AttachDialogFragment<T extends AttachDialogFragment<T>> extends BaseDialogFragment<T> {

    private static final String TAG = "AttachDialogFragment";

    protected int mOffsetY = 0;
    protected int mOffsetX = 0;

    protected int mMinWidth = WRAP_CONTENT;
    protected int mMinHeight = WRAP_CONTENT;

    protected View attachView;
    protected PointF touchPoint = null;

    protected DialogPosition dialogPosition = null;

    protected AtViewGravity atViewGravity = AtViewGravity.TOP;

    public enum AtViewGravity {
        AUTO,
        START,
        END,
        TOP, BOTTOM, CENTER
    }

    @Override
    protected int getGravity() {
        return Gravity.NO_GRAVITY;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        if (attachView == null && touchPoint == null) {
            dismiss();
            return;
        }
        super.initView(view, savedInstanceState);
        getImplView().setAlpha(0f);
        if (mMinWidth > 0) {
            getImplView().setMinimumWidth(mMinWidth);
        }
        if (mMinHeight > 0) {
            getImplView().setMinimumHeight(mMinHeight);
        }
    }

    @Override
    protected void initLayoutParams(ViewGroup view) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.height = WRAP_CONTENT;
        params.width = WRAP_CONTENT;
        view.setFocusableInTouchMode(true);
        view.setFocusable(true);
        view.setClickable(true);
    }

    public boolean isShowUp;
    boolean isShowLeft;

    @Override
    protected DialogAnimator onCreateDialogAnimator(ViewGroup contentView) {
        int[] rootLocations = new int[2];
        getRootView().getLocationOnScreen(rootLocations);
        int offset = rootLocations[1];
        int windowWidth = getRootView().getMeasuredWidth();
        int windowHeight = getRootView().getMeasuredHeight();
        int width = getImplView().getMeasuredWidth();
        int height = getImplView().getMeasuredHeight();
        Log.d(TAG, "width=" + width + " height=" + height + " windowWidth=" + windowWidth + " windowHeight=" + windowHeight);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getImplView().getLayoutParams();
        if (getMaxHeight() > 0 && getMaxHeight() < height) {
            height = getMaxHeight();
            if (height > windowHeight) {
                height = windowHeight;
            }
        }
        if (getMaxWidth() > 0 && getMaxWidth() < width) {
            width = getMaxWidth();
            if (width > windowWidth) {
                width = windowWidth;
            }
        }
        getImplView().setLayoutParams(params);
        Log.d(TAG, "width=" + width + " height=" + height);

        RectF touchRect = new RectF();
        if (touchPoint == null) {
            if (attachView == null) {
                throw new RuntimeException("You must attach a view or set the touchPoint!");
            }
            int[] locations = new int[2];
            attachView.getLocationOnScreen(locations);
//            attachView.getLocationInWindow(); // getLocationOnScreen
            touchRect.set(locations[0], locations[1], locations[0] + attachView.getMeasuredWidth(),
                    locations[1] + attachView.getMeasuredHeight());
            Log.d(TAG, "locations[0]=" + locations[0] + " locations[1]=" + locations[1]);
            touchPoint = new PointF(touchRect.centerX(), touchRect.centerY());

            isShowLeft = touchPoint.x < windowWidth / 2f;
            isShowUp = touchPoint.y < windowHeight / 2f;

            if (isShowLeft) {
                if (isShowUp) {
                    touchPoint.set(touchRect.left, touchRect.top);
                } else {
                    touchPoint.set(touchRect.left, touchRect.bottom);
                }
            } else {
                if (isShowUp) {
                    touchPoint.set(touchRect.right, touchRect.top);
                } else {
                    touchPoint.set(touchRect.right, touchRect.bottom);
                }
            }


        } else {
            attachView = null;
            touchRect.set(touchPoint.x, touchPoint.y, touchPoint.x, touchPoint.y);
        }

        touchPoint.y -= offset;
        touchPoint.x += mOffsetX;
        touchPoint.y += mOffsetY;

        if (touchPoint.x < 0) {
            touchPoint.x = 0;
        }
        if (touchPoint.y < 0) {
            touchPoint.y = 0;
        }

        if (touchPoint.x > windowWidth) {
            touchPoint.x = windowWidth;
        }

        if (touchPoint.y > windowHeight) {
            touchPoint.y = windowHeight;
        }

        if (touchPoint.x == 0 && touchPoint.y == 0) {
            touchPoint.x = (windowWidth - width) / 2f;
            touchPoint.y = (windowHeight - height) / 2f;
        }

        isShowLeft = touchPoint.x < windowWidth / 2f;
        isShowUp = touchPoint.y < windowHeight / 2f;

        if (isShowLeft) {
            params.width = (int) Math.min(width, windowWidth - touchPoint.x);
            params.leftMargin = (int) touchPoint.x;
            params.rightMargin = windowWidth - (params.leftMargin + params.width);
        } else {
            params.width = (int) Math.min(width, touchPoint.x);
            params.leftMargin = (int) (touchPoint.x - params.width);
            params.rightMargin = (int) (windowWidth - touchPoint.x);
        }
        params.width -= (getMarginStart() + getMarginEnd());
        params.leftMargin += getMarginStart();
        params.rightMargin += getMarginEnd();

        if (isShowUp) {
            params.height = (int) Math.min(height, windowHeight - touchPoint.y);
            params.topMargin = (int) touchPoint.y;
            params.bottomMargin = (int) (windowHeight - touchPoint.y - params.height);
        } else {
            params.height = (int) Math.min(height, touchPoint.y);
            params.topMargin = (int) (touchPoint.y - params.height);
            params.bottomMargin = (int) (windowHeight - touchPoint.y);
        }

        params.height -= (getMarginTop() + getMarginBottom());
        params.topMargin += getMarginTop();
        params.bottomMargin += getMarginBottom();

        mDelegate.debug("left=" + params.leftMargin + " right=" + params.rightMargin);
        getImplView().setLayoutParams(params);


        DialogAnimation animation;
        if (isShowUp) {
            animation = isShowLeft ? DialogAnimation.ScrollAlphaFromLeftTop
                    : DialogAnimation.ScrollAlphaFromRightTop;
        } else {
            animation = isShowLeft ? DialogAnimation.ScrollAlphaFromLeftBottom
                    : DialogAnimation.ScrollAlphaFromRightBottom;
        }
        return new ScrollScaleAnimator(getImplView(), animation);
    }

    @Override
    public void doShowAnimation() {
        super.doShowAnimation();
        getImplView().setAlpha(1f);
    }

    public final T show(View view) {
        setAttachView(view);
        return show(view.getContext());
    }

    public T setAttachView(View attachView) {
        this.attachView = attachView;
        return self();
    }

    public T attachViewCenter(View attachView) {
        int[] locations = new int[2];
        attachView.getLocationOnScreen(locations);
        return setTouchPoint(locations[0] + attachView.getMeasuredWidth() / 2f,
                locations[1] + attachView.getMeasuredHeight() / 2f);
    }

    public T setTouchPoint(PointF touchPoint) {
        this.touchPoint = touchPoint;
        return self();
    }

    public T setTouchPoint(Point point) {
        this.touchPoint = new PointF(point);
        return self();
    }

    public T setTouchPoint(float x, float y) {
        this.touchPoint = new PointF(x, y);
        return self();
    }

    public T setDialogPosition(DialogPosition dialogPosition) {
        this.dialogPosition = dialogPosition;
        return self();
    }

    public T setOffsetX(int defaultOffsetX) {
        this.mOffsetX = defaultOffsetX;
        return self();
    }

    public T setOffsetY(int defaultOffsetY) {
        this.mOffsetY = defaultOffsetY;
        return self();
    }

    public T setAtViewGravity(AtViewGravity atViewGravity) {
        this.atViewGravity = atViewGravity;
        return self();
    }

    public T setMinHeight(int mMinHeight) {
        this.mMinHeight = mMinHeight;
        return self();
    }

    public T setMinWidth(int mMinWidth) {
        this.mMinWidth = mMinWidth;
        return self();
    }

}
