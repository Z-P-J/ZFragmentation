package com.zpj.fragmentation.dialog.utils;

import android.animation.TimeInterpolator;

/**
 * 限制最大值的OvershootInterpolator
 */
public class LimitedOvershootInterpolator implements TimeInterpolator {

    private final float topX;
    private final float topY;

    public LimitedOvershootInterpolator(float topY) {
        this(0.5f, topY);
    }

    public LimitedOvershootInterpolator(float topX, float topY) {
        if (topX < 0.01f) {
            topX = 0.01f;
        } else if (topX > 0.99f) {
            topX = 0.99f;
        }
        this.topX = topX;
        this.topY = topY;
    }

    @Override
    public float getInterpolation(float t) {
        if (t == 0f) {
            return 0f;
        } else if (t == 1f) {
            return 1f;
        }  else if (t < topX) {
            return topY - topY * (t / topX - 1) * (t / topX - 1);
        } else {
            return (float) Math.cos((t - topX) / (1 - topX) * Math.PI) * (topY - 1) / 2 + (topY + 1) / 2;
        }
    }

}
