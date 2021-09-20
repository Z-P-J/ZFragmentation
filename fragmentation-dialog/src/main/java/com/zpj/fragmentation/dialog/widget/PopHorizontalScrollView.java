package com.zpj.fragmentation.dialog.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by felix on 17/1/10.
 */
public class PopHorizontalScrollView extends HorizontalScrollView
        implements BubbleLayout.OnBulgeChangeCallback {

    public PopHorizontalScrollView(Context context) {
        super(context);
    }

    public PopHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PopHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBulgeChanged(int site, int size) {
        if (getChildCount() > 0) {
            View view = getChildAt(0);
            if (view instanceof BubbleLayout.OnBulgeChangeCallback) {
                ((BubbleLayout.OnBulgeChangeCallback) view).onBulgeChanged(site, size);
            }
        }
    }
}
