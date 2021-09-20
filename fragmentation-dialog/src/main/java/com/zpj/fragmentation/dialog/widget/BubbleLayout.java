package com.zpj.fragmentation.dialog.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;

public class BubbleLayout extends FrameLayout implements View.OnLayoutChangeListener {
    private static final String TAG = "PopLayout";

    private float mOffset = 0;

    private int mRadiusSize = DEFAULT_RADIUS;

    private int mShadowRadius = 8;

    private int mShadowColor = Color.parseColor("#20000000");

//    private int mStrokeSize = 4;
//
//    private int mStrokeColor;

    private int mBulgeSize = DEFAULT_BULGE_SIZE;

    private Paint mPaint;

    private Path mPopMaskPath;

    private Path mBulgePath;

    private Path mDestBulgePath;

    private final Path mBgPath = new Path();

    private Matrix mCornuteMatrix;

    private int mSiteMode = SITE_BOTTOM;

    public static final int SITE_TOP = 0;

    public static final int SITE_LEFT = 1;

    public static final int SITE_RIGHT = 2;

    public static final int SITE_BOTTOM = 3;

    private static final int DEFAULT_RADIUS = 8;

    private static final int DEFAULT_BULGE_SIZE = 16;

//    private static final Xfermode MODE = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Xfermode xfermode;

    public BubbleLayout(Context context) {
        this(context, null, 0);
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PopLayout);
        mSiteMode = a.getInt(R.styleable.PopLayout_siteMode, SITE_BOTTOM);
        mRadiusSize = a.getDimensionPixelSize(R.styleable.PopLayout_radiusSize,
                getResources().getDimensionPixelSize(R.dimen.pop_radius));
        mBulgeSize = a.getDimensionPixelSize(R.styleable.PopLayout_bulgeSize,
                getResources().getDimensionPixelSize(R.dimen.bulge_size));
        mOffset = a.getDimensionPixelSize(R.styleable.PopLayout_offsetSize, 0);
        a.recycle();

        if (getBackground() == null) {
            // 需要设置背景，可能是因为没有背景Layout就不会去执行绘制操作
            setBackgroundColor(DialogThemeUtils.getDialogBackgroundColor(context));
//            setBackgroundColor(Color.TRANSPARENT);
        }

//        setLayerType(LAYER_TYPE_NONE, null);


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        } else {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setXfermode(xfermode);
//        mPaint.setStrokeJoin(Paint.Join.ROUND);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setShadowLayer(mShadowRadius, 0, 0, mShadowColor);


//        mStrokeColor = Color.DKGRAY;
//        mStrokeSize = ScreenUtils.dp2pxInt(2f);

        mBulgePath = new Path();
        mPopMaskPath = new Path();
        mDestBulgePath = new Path();
        mCornuteMatrix = new Matrix();

        resetBulge();
        resetMask();
        onBulgeChange();
        addOnLayoutChangeListener(this);
    }

    private void resetBulge() {
        mBulgePath.reset();
//        mBulgePath.lineTo(mBulgeSize << 1, 0);
//        mBulgePath.lineTo(mBulgeSize, mBulgeSize);
//        mBulgePath.close();

        float top = mBulgeSize - mShadowRadius / 2f;
        mBulgePath.cubicTo(mBulgeSize / 2f, 0, mBulgeSize * 3 / 4f, top, mBulgeSize, top);
        mBulgePath.cubicTo(mBulgeSize * 5 / 4f, top, mBulgeSize * 1.5f, 0, mBulgeSize * 2, 0);
        mBulgePath.close();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        onBulgeChange();
    }

    private void onBulgeChange() {
        if (getChildCount() > 0) {
            View view = getChildAt(0);
            if (view instanceof OnBulgeChangeCallback) {
                ((OnBulgeChangeCallback) view).onBulgeChanged(mSiteMode, mBulgeSize);
            }
        }
    }

    private void resetMask() {
        mPopMaskPath.reset();
        mBgPath.reset();
        int width = getMeasuredWidth(), height = getMeasuredHeight();
        if (width <= mRadiusSize || height <= mRadiusSize) {
            return;
        }
        float offset = reviseOffset(mOffset);
        mPopMaskPath.addRect(new RectF(0, 0, width, height), Path.Direction.CW);
//        mPopMaskPath.addRoundRect(new RectF(mBulgeSize, mBulgeSize, width - mBulgeSize, height - mBulgeSize), mRadiusSize, mRadiusSize, Path.Direction.CCW);
        mPopMaskPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);

        mBgPath.addRoundRect(new RectF(mBulgeSize, mBulgeSize, width - mBulgeSize, height - mBulgeSize), mRadiusSize, mRadiusSize, Path.Direction.CCW);

        switch (mSiteMode) {
            case SITE_TOP:
                mCornuteMatrix.setRotate(180, mBulgeSize, 0);
                mCornuteMatrix.postTranslate(0, mBulgeSize);
                mBulgePath.transform(mCornuteMatrix, mDestBulgePath);
                mBgPath.addPath(mDestBulgePath, offset - mBulgeSize, 0);
                break;
            case SITE_LEFT:
                mCornuteMatrix.setRotate(90, mBulgeSize, 0);
                mBulgePath.transform(mCornuteMatrix, mDestBulgePath);
                mBgPath.addPath(mDestBulgePath, 0, offset);
                break;
            case SITE_RIGHT:
                mCornuteMatrix.setRotate(-90, mBulgeSize, 0);
                mCornuteMatrix.postTranslate(-mBulgeSize, 0);
                mBulgePath.transform(mCornuteMatrix, mDestBulgePath);
                mBgPath.addPath(mDestBulgePath, width - mBulgeSize, offset);
                break;
            case SITE_BOTTOM:
                mCornuteMatrix.setTranslate(-mBulgeSize, 0);
                mBulgePath.transform(mCornuteMatrix, mDestBulgePath);
                mBgPath.addPath(mDestBulgePath, offset, height - mBulgeSize);
                break;
        }
        mPopMaskPath.addPath(mBgPath);
    }

    private float reviseOffset(float offset) {
        int size = 0, bulgeWidth = mBulgeSize << 1;
        switch (mSiteMode) {
            case SITE_TOP:
            case SITE_BOTTOM:
                size = getMeasuredWidth();
                break;
            case SITE_LEFT:
            case SITE_RIGHT:
                size = getMeasuredHeight();
                break;
        }
        offset = Math.max(offset, mRadiusSize + bulgeWidth);
        if (size > 0) {
            offset = Math.min(offset, size - mRadiusSize - bulgeWidth);
            if (mRadiusSize + bulgeWidth > offset) {
                offset = size >> 1;
            }
        }
        Log.d("reviseOffset", "offset=" + offset);
        return offset;
    }

    public void setOffset(float offset) {
        Log.d("setOffset", "offset=" + offset);
        if (mOffset != offset) {
            mOffset = offset;
            resetMask();
            postInvalidate();
        }
    }

    public void setRadiusSize(int radius) {
        if (mRadiusSize != radius) {
            mRadiusSize = radius;
            resetMask();
            postInvalidate();
        }
    }

    public void setBulgeSize(int size) {
        if (mBulgeSize != size) {
            mBulgeSize = size;
            resetBulge();
            resetMask();
            postInvalidate();
        }
    }

    public void setSiteMode(int mode) {
        if (mSiteMode != mode) {
            mSiteMode = mode;
            onBulgeChange();
            resetMask();
            postInvalidate();
        }
    }

    public void setShadowColor(int mShadowColor) {
        this.mShadowColor = mShadowColor;
        mPaint.setShadowLayer(mShadowRadius, 0, 0, mShadowColor);
        postInvalidate();
    }

    public void setShadowRadius(int mShadowRadius) {
        this.mShadowRadius = mShadowRadius;
        mPaint.setShadowLayer(mShadowRadius, 0, 0, mShadowColor);
        resetBulge();
        postInvalidate();
    }

    public float getOffset() {
        return mOffset;
    }

    public int getRadiusSize() {
        return mRadiusSize;
    }

    public int getBugleSize() {
        return mBulgeSize;
    }

    public int getSiteMode() {
        return mSiteMode;
    }

    @Override
    public void draw(Canvas canvas) {


//        mPaint.setXfermode(xfermode);

//        super.draw(canvas);

//        canvas.save();


//        mPaint.setMaskFilter(new BlurMaskFilter(mShadowRadius * 2, BlurMaskFilter.Blur.NORMAL));
//        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        mPaint.setStrokeWidth(mStrokeSize);
//        mPaint.setColor(mShadowColor);
//        mPaint.setXfermode(xfermode);
        canvas.drawPath(mBgPath, mPaint);
//        mPaint.clearShadowLayer();
//        mPaint.setMaskFilter(null);

//        canvas.restore();



//        int layer = canvas.saveLayer(0, 0, getWidth(),
//                getHeight(), null, Canvas.ALL_SAVE_FLAG);

        canvas.save();

        canvas.clipPath(mBgPath);
        super.draw(canvas);

        canvas.restore();

//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
//            mPaint.setXfermode(xfermode); // new PorterDuffXfermode(PorterDuff.Mode.DST_IN)
//        } else {
//            mPaint.setXfermode(xfermode); // new PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
//        }
//        mPaint.setXfermode(xfermode);

//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
////            mPaint.setXfermode(xfermode); // new PorterDuffXfermode(PorterDuff.Mode.DST_IN)
//            canvas.drawPath(mPopMaskPath, mPaint);
//        } else {
////            mPaint.setXfermode(xfermode); // new PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
//            final Path path = new Path();
//            path.addRect(0, 0, getWidth(), getHeight(), Path.Direction.CW);
//            path.op(mPopMaskPath, Path.Op.DIFFERENCE);
//            canvas.drawPath(path, mPaint);
//        }

//        canvas.restoreToCount(layer);


    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        resetMask();
        postInvalidate();
    }

    public interface OnBulgeChangeCallback {

        void onBulgeChanged(int site, int size);
    }
}
