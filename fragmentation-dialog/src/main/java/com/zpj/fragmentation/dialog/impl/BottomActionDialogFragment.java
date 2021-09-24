package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.base.OverDragBottomDialogFragment;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.utils.ContextUtils;

public abstract class BottomActionDialogFragment<T extends BottomActionDialogFragment<T>> extends OverDragBottomDialogFragment<T>
        implements View.OnClickListener {

    protected TextView mTvTitle, tv_cancel, tv_confirm, tv_neutral;
    protected String title, negativeText, neutralText, positiveText;
    protected int positionBtnColor, neutralBtnColor, negativeBtnColor;

    protected View mContentView;

    protected IDialog.OnButtonClickListener<T> negativeClickListener;
    protected IDialog.OnButtonClickListener<T> positiveClickListener;
    protected IDialog.OnButtonClickListener<T> onNeutralButtonClickListener;

    protected boolean isHideCancel = false;
    protected boolean autoDismiss = true;

    protected IDialog.OnViewCreateListener<T> onViewCreateListener;

    @Override
    protected int getImplLayoutId() {
        return R.layout._dialog_layout_action;
    }

    protected abstract int getContentLayoutId();

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        ViewStub mContentViewStub = findViewById(R.id._content_view_stub);
        mContentViewStub.setLayoutResource(getContentLayoutId());
        mContentView = mContentViewStub.inflate();

        if (onViewCreateListener != null) {
            onViewCreateListener.onViewCreate(self(), mContentView);
        }


        mTvTitle = findViewById(R.id.tv_title);
        tv_cancel = findViewById(R.id.tv_cancel);


        tv_confirm = findViewById(R.id.tv_confirm);
        mTvTitle.setTextColor(DialogThemeUtils.getMajorTextColor(context));

        applyPrimaryColor();

        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);


        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
        } else {
            mTvTitle.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(negativeText)) {
            tv_cancel.setText(negativeText);
        }
        if (!TextUtils.isEmpty(positiveText)) {
            tv_confirm.setText(positiveText);
        }

        if (isHideCancel) tv_cancel.setVisibility(View.GONE);


        tv_neutral = findViewById(R.id.tv_neutral);
        if (tv_neutral != null) {
            if (onNeutralButtonClickListener != null) {
                tv_neutral.setVisibility(View.VISIBLE);
            }
            tv_neutral.setOnClickListener(this);
            if (!TextUtils.isEmpty(neutralText)) {
                tv_neutral.setText(neutralText);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tv_cancel) {
            if (negativeClickListener != null) {
                negativeClickListener.onClick(self(), IDialog.BUTTON_NEGATIVE);
            }
            if (autoDismiss) {
                dismiss();
            }

        } else if (v == tv_confirm) {
            if (positiveClickListener != null) {
                positiveClickListener.onClick(self(), IDialog.BUTTON_POSITIVE);
            }
            if (autoDismiss) {
                dismiss();
            }

        }  else if (v == tv_neutral) {
            if (onNeutralButtonClickListener != null) {
                onNeutralButtonClickListener.onClick(self(), IDialog.BUTTON_NEUTRAL);
            }
            if (autoDismiss) {
                dismiss();
            }
        }
    }

    protected void applyPrimaryColor() {
        if (positionBtnColor == 0) {
            tv_confirm.setTextColor(DialogThemeUtils.getPositiveTextColor(context));
        } else {
            tv_confirm.setTextColor(positionBtnColor);
        }

        if (tv_neutral != null) {
            if (neutralBtnColor == 0) {
                tv_neutral.setTextColor(DialogThemeUtils.getNegativeTextColor(context));
            } else {
                tv_neutral.setTextColor(neutralBtnColor);
            }
        }

        if (negativeBtnColor == 0) {
            tv_cancel.setTextColor(DialogThemeUtils.getNegativeTextColor(context));
        } else {
            tv_cancel.setTextColor(negativeBtnColor);
        }
    }

    public T setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
        return self();
    }

    public T setPositiveButton(IDialog.OnButtonClickListener<T> listener) {
        this.positiveClickListener = listener;
        return self();
    }

    public T setPositiveButton(String btnStr, IDialog.OnButtonClickListener<T> listener) {
        this.positiveText = btnStr;
        this.positiveClickListener = listener;
        return self();
    }

    public T setPositiveButton(int btnStrId, IDialog.OnButtonClickListener<T> listener) {
        this.positiveText = ContextUtils.getApplicationContext().getString(btnStrId);
        this.positiveClickListener = listener;
        return self();
    }

    public T setNegativeButton(IDialog.OnButtonClickListener<T> listener) {
        this.negativeClickListener = listener;
        return self();
    }

    public T setNegativeButton(String btnStr, IDialog.OnButtonClickListener<T> listener) {
        this.negativeText = btnStr;
        this.negativeClickListener = listener;
        return self();
    }

    public T setNegativeButton(int btnStrId, IDialog.OnButtonClickListener<T> listener) {
        this.negativeText = ContextUtils.getApplicationContext().getString(btnStrId);
        this.negativeClickListener = listener;
        return self();
    }

    public T setNeutralButton(IDialog.OnButtonClickListener<T> listener) {
        this.onNeutralButtonClickListener = listener;
        return self();
    }

    public T setNeutralButton(String btnStr, IDialog.OnButtonClickListener<T> listener) {
        this.neutralText = btnStr;
        this.onNeutralButtonClickListener = listener;
        return self();
    }

    public T setNeutralButton(int btnStrId, IDialog.OnButtonClickListener<T> listener) {
        this.neutralText = ContextUtils.getApplicationContext().getString(btnStrId);
        this.onNeutralButtonClickListener = listener;
        return self();
    }

    public T setTitle(String title) {
        this.title = title;
        return self();
    }

    public T setTitle(int titleRes) {
        this.title = ContextUtils.getApplicationContext().getString(titleRes);
        return self();
    }

    public T setNegativeText(String negativeText) {
        this.negativeText = negativeText;
        return self();
    }

    public T setPositiveText(String positiveText) {
        this.positiveText = positiveText;
        return self();
    }

    public T hideCancelBtn() {
        isHideCancel = true;
        return self();
    }

    public T onViewCreate(IDialog.OnViewCreateListener<T> onViewCreateListener) {
        this.onViewCreateListener = onViewCreateListener;
        return self();
    }

    public T setPositionButtonColor(int positionBtnColor) {
        this.positionBtnColor = positionBtnColor;
        return self();
    }

    public T setNeutralButtonColor(int neutralBtnColor) {
        this.neutralBtnColor = neutralBtnColor;
        return self();
    }

    public T setNegativeButtonColor(int negativeBtnColor) {
        this.negativeBtnColor = negativeBtnColor;
        return self();
    }

}
