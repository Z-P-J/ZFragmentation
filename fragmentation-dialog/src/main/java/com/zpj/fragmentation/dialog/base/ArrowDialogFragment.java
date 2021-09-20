package com.zpj.fragmentation.dialog.base;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.animator.DialogAnimator;
import com.zpj.fragmentation.dialog.animator.ScaleAlphaAnimator;
import com.zpj.fragmentation.dialog.enums.DialogPosition;
import com.zpj.fragmentation.dialog.widget.BubbleLayout;
import com.zpj.utils.ScreenUtils;

public abstract class ArrowDialogFragment<T extends ArrowDialogFragment<T>> extends BaseDialogFragment<T> {

    private static final String TAG = "ArrowDialogFragment";

    protected int defaultOffsetY = 0;
    protected int defaultOffsetX = 0;

    private float cornerRadius;

    protected boolean isCenterHorizontal = false;

    protected View attachView;
    protected PointF touchPoint = null;

    protected DialogPosition dialogPosition = null;

    protected BubbleLayout mBubbleLayout;

    private ViewGroup contentView;

    private boolean mShowShadowAnimator = false;

    public ArrowDialogFragment() {
        cornerRadius = ScreenUtils.dp2px(8);
    }


    @Override
    protected final int getImplLayoutId() {
        return R.layout._dialog_layout_arrow_view;
    }

    protected abstract int getContentLayoutId();

    @Override
    protected DialogAnimator onCreateDialogAnimator(ViewGroup contentView) {
        return null;
    }

    @Override
    protected DialogAnimator onCreateShadowAnimator(FrameLayout flContainer) {
        return mShowShadowAnimator ? super.onCreateShadowAnimator(flContainer) : null;
    }

    @Override
    protected int getGravity() {
        return Gravity.NO_GRAVITY;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        if (attachView == null && touchPoint == null) {
            dismiss();
            return;
        }

        getImplView().setAlpha(0f);

        mBubbleLayout = findViewById(R.id.arrowPopupContainer);
        mBubbleLayout.setRadiusSize((int) cornerRadius);

        if (getContentLayoutId() > 0) {
            contentView = (ViewGroup) getLayoutInflater().inflate(getContentLayoutId(), mBubbleLayout, false);
            mBubbleLayout.addView(contentView);
        }

        if (bgDrawable != null) {
            mBubbleLayout.setBackground(bgDrawable);
        }

    }

    @Override
    public void doShowAnimation() {
        generateAnimation(attachView, touchPoint);
        super.doShowAnimation();
    }

    private void generateAnimation(View anchor, PointF origin) {

        if (origin == null) {
            origin = new PointF(-1, -1);
        }
        RectF frame = new RectF();


        int[] location = reviseFrameAndOrigin(anchor, frame, origin);

        int x = location[0], y = location[1];
        int width = anchor.getWidth(), height = anchor.getHeight();
        int contentWidth = mBubbleLayout.getMeasuredWidth(), contentHeight = mBubbleLayout.getMeasuredHeight();
        Log.d("whwhwhwhwh", "width=" + width + "   height=" + height);

        PointF offset = getOffset(frame, new Rect(x, y + height, contentWidth + x,
                contentHeight + y + height), x + origin.x, y + origin.y);

        float top = y - contentHeight - frame.top;
        float left = x - contentWidth - frame.left;
        float right = frame.right - x - width - contentWidth;
        float bottom = frame.bottom - y - height - contentHeight;
        Log.d("whwhwhwhwh", "top=" + top + "   left=" + left + "  right=" + right + "  bottom=" + bottom);
        float max1 = Math.max(top, bottom);
        float max2 = Math.max(right, left);
        float max = Math.max(max1, max2);
        Log.d("whwhwhwhwh", "max1=" + max1 + "   max2=" + max2 + "  max=" + max);
//        int offsetY = ScreenUtils.getScreenHeight(context) - getRootView().getMeasuredHeight();
        int[] rootLocations = new int[2];
        getRootView().getLocationOnScreen(rootLocations);
        int offsetY = rootLocations[1];
        if (max == bottom) {
            Log.d("whwhwhwhwh", "showAtBottom");
            showAtBottom(anchor, origin, offset.x, -offsetY);
        } else if (max == top) {
            Log.d("whwhwhwhwh", "showAtTop");
            showAtTop(anchor, origin, offset.x, -height - contentHeight - offsetY);
        } else if (bottom > 0) {
            Log.d("whwhwhwhwh", "showAtBottom");
            showAtBottom(anchor, origin, offset.x, -offsetY);
        } else if (top > 0) {
            Log.d("whwhwhwhwh", "showAtTop");
            showAtTop(anchor, origin, offset.x, -height - contentHeight - offsetY);
        } else if (max == right) {
            Log.d("whwhwhwhwh", "showAtRight");
            showAtRight(anchor, origin, width, offset.y - offsetY);
        } else {
            Log.d("whwhwhwhwh", "showAtLeft");
            showAtLeft(anchor, origin, -contentWidth, offset.y - offsetY);
        }
    }

    public void showAtTop(View anchor, PointF origin, float xOff, float yOff) {
        mBubbleLayout.setSiteMode(BubbleLayout.SITE_BOTTOM);
        mBubbleLayout.setOffset(origin.x - xOff);
//        show(anchor, xOff, yOff, PopLayout.SITE_BOTTOM);
        show(anchor, xOff, yOff, origin.x - xOff, mBubbleLayout.getMeasuredHeight());
    }

    public void showAtLeft(View anchor, PointF origin, float xOff, float yOff) {
        mBubbleLayout.setSiteMode(BubbleLayout.SITE_RIGHT);
        mBubbleLayout.setOffset(-origin.y - yOff);
//        show(anchor, xOff, yOff, PopLayout.SITE_RIGHT);
        show(anchor, xOff, yOff, mBubbleLayout.getMeasuredWidth(), -origin.y - yOff);
    }

    public void showAtRight(View anchor, PointF origin, float xOff, float yOff) {
        mBubbleLayout.setSiteMode(BubbleLayout.SITE_LEFT);
        mBubbleLayout.setOffset(-origin.y - yOff);
//        show(anchor, xOff, yOff, PopLayout.SITE_LEFT);
        show(anchor, xOff, yOff, 0, -origin.y - yOff);
    }

    public void showAtBottom(View anchor, PointF origin, float xOff, float yOff) {
        mBubbleLayout.setSiteMode(BubbleLayout.SITE_TOP);
        mBubbleLayout.setOffset(origin.x - xOff);
//        show(anchor, xOff, yOff, PopLayout.SITE_TOP);
        show(anchor, xOff, yOff, origin.x - xOff, 0);
    }

    private void show(View anchor, float xOff, float yOff, float pivotX, float pivotY) {
        Log.d(TAG, "getMeasuredHeight=" + mBubbleLayout.getMeasuredHeight() + " getMeasuredWidth=" + mBubbleLayout.getMeasuredWidth());
        Log.d(TAG, "xOff=" + xOff + " yOff=" + yOff);
        final int[] screenLocation = new int[2];
        anchor.getLocationOnScreen(screenLocation);
        Log.d(TAG, "screenLocation[0]=" + screenLocation[0] + " screenLocation[1]=" + screenLocation[1]);
        float x = screenLocation[0] + xOff;
        float y = screenLocation[1] + anchor.getMeasuredHeight() + yOff;
        Log.d(TAG, "x=" + x + " y=" + y);
        getImplView().setTranslationX(x);
        getImplView().setTranslationY(y);
        getImplView().setAlpha(1f);
        mDialogAnimator = new ScaleAlphaAnimator(getImplView(), pivotX, pivotY);
        mDialogAnimator.setShowDuration(getShowAnimDuration());
        mDialogAnimator.setDismissDuration(getDismissAnimDuration());
    }

    public int[] reviseFrameAndOrigin(View anchor, RectF frame, PointF origin) {
        int[] location = new int[2];
        anchor.getLocationInWindow(location);

        int l1 = location[0];
        int l2 = location[1];
        if (l1 == 0 || l2 == 0) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            location[0] = rect.left;
            location[1] = rect.top;
        }

        if (origin.x < 0 || origin.y < 0) {
            origin.set(anchor.getWidth() >> 1, anchor.getHeight() >> 1);
        }

        if (frame.isEmpty() || !frame.contains(origin.x + location[0], origin.y + location[1])) {
            Rect rect = new Rect();
            anchor.getWindowVisibleDisplayFrame(rect);
            frame.set(rect);
        }

        return location;
    }

    protected PointF getOffset(RectF frame, Rect rect, float x, float y) {
        RectF rt = new RectF(rect);
        rt.offset(x - rt.centerX(), y - rt.centerY());
        Log.d("getOffset", "frame=" + frame.toString());
        Log.d("getOffset", "rt=" + rt.toString());
        if (!frame.contains(rt)) {
            float offsetX = 0, offsetY = 0;
            if (rt.bottom > frame.bottom) {
                offsetY = frame.bottom - rt.bottom;
            } else if (rt.top < frame.top) {
                offsetY = frame.top - rt.top;
            }
//            offsetX = Math.max(frame.bottom - rt.bottom, frame.top - rt.top);
            Log.d("getOffset", "offsetY111111111=" + (frame.bottom - rt.bottom));
            Log.d("getOffset", "offsetY2222222222=" + (frame.top - rt.top));

            if (rt.right > frame.right) {
                offsetX = frame.right - rt.right;
            } else if (rt.left < frame.left) {
                offsetX = frame.left - rt.left;
            }

            Log.d("getOffset", "offsetX1111111111=" + (frame.right - rt.right));
            Log.d("getOffset", "offsetX2222222222=" + (frame.left - rt.left));

            Log.d("getOffset", "offsetX=" + offsetX);
            Log.d("getOffset", "offsetY=" + offsetY);
            rt.offset(offsetX, offsetY);
        }
        return new PointF(rt.left - rect.left, rt.top - rect.top);
    }

    public ViewGroup getContentView() {
        return contentView;
    }

    public final T show(View view) {
        setAttachView(view);
        return show(view.getContext());
    }

    public T setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        if (mBubbleLayout != null) {
            mBubbleLayout.setRadiusSize((int) cornerRadius);
        }
        return self();
    }

    public T setCornerRadiusDp(float cornerRadiusDp) {
        return setCornerRadius(ScreenUtils.dp2px(cornerRadiusDp));
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

    public T setCenterHorizontal(boolean centerHorizontal) {
        isCenterHorizontal = centerHorizontal;
        return self();
    }

    public T setDialogPosition(DialogPosition dialogPosition) {
        this.dialogPosition = dialogPosition;
        return self();
    }

    public T setDefaultOffsetX(int defaultOffsetX) {
        this.defaultOffsetX = defaultOffsetX;
        return self();
    }

    public T setDefaultOffsetY(int defaultOffsetY) {
        this.defaultOffsetY = defaultOffsetY;
        return self();
    }

    public T setShowShadowAnimator(boolean mShowShadowAnimator) {
        this.mShowShadowAnimator = mShowShadowAnimator;

        return self();
    }
}
