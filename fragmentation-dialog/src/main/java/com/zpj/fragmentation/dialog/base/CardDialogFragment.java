package com.zpj.fragmentation.dialog.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.DialogAnimator;
import com.zpj.fragmentation.dialog.animator.ScaleAlphaAnimator;
import com.zpj.fragmentation.dialog.enums.DialogAnimation;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.utils.ScreenUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public abstract class CardDialogFragment<T extends CardDialogFragment<T>> extends BaseDialogFragment<T> {

    private float cornerRadius;
    private boolean transparent = false;

    public CardDialogFragment() {
        setMaxWidth(MATCH_PARENT);
        setMarginHorizontal((int) (ScreenUtils.getScreenWidth() * 0.08f));
        setMarginVertical((int) (ScreenUtils.getScreenHeight() * 0.12f));
        cornerRadius = ScreenUtils.dp2px(16);
    }

    @Override
    protected DialogAnimator onCreateDialogAnimator(ViewGroup contentView) {
        return new ScaleAlphaAnimator(contentView, DialogAnimation.ScaleAlphaFromCenter);
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        initImplView(view, savedInstanceState);
    }

    protected void initImplView(View view, @Nullable Bundle savedInstanceState) {
        FrameLayout flContainer = findViewById(R.id._dialog_fl_container);
        this.rootView = flContainer;

        if (interceptTouch) {
            interceptTouch();
        }

        CardView cardView = new CardView(context);
        implView = cardView;
        flContainer.addView(cardView);
        View contentView = getLayoutInflater().inflate(getImplLayoutId(), cardView, false);

        cardView.setUseCompatPadding(false);
        if (bgDrawable != null) {
            contentView.setBackground(bgDrawable);
            cardView.setCardElevation(0);
            cardView.setRadius(0);
            cardView.setCardBackgroundColor(Color.TRANSPARENT);
        } else {
            cardView.setCardElevation(ScreenUtils.dp2px(4));
            cardView.setRadius(cornerRadius);
            cardView.setCardBackgroundColor(DialogThemeUtils.getDialogBackgroundColor(context));
        }
        cardView.addView(contentView);
        initLayoutParams(cardView);

        if (transparent) {
            cardView.setCardElevation(0);
            cardView.setUseCompatPadding(false);
            cardView.setCardBackgroundColor(Color.TRANSPARENT);
        }
    }

    public T setTransparentBackground(boolean transparent) {
        this.transparent = transparent;
        return self();
    }

    public T setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        return self();
    }

    public T setCornerRadiusDp(float cornerRadiusDp) {
        return setCornerRadius(ScreenUtils.dp2px(cornerRadiusDp));
    }

}
