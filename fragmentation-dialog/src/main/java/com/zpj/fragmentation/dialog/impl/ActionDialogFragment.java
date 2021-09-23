package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.IDialog;
import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.base.CardDialogFragment;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.fragmentation.dialog.widget.CustomScrollView;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.ScreenUtils;

public abstract class ActionDialogFragment<T extends ActionDialogFragment<T>> extends CardDialogFragment<T>
        implements View.OnClickListener {

    protected TextView tv_title, tv_cancel, tv_confirm, tv_neutral;
    protected String title, cancelText, neutralText, confirmText;
    protected int positionBtnColor, neutralBtnColor, negativeBtnColor;

    protected View mContentView;

    protected IDialog.OnButtonClickListener<T> cancelListener;
    protected IDialog.OnButtonClickListener<T> confirmListener;
    protected IDialog.OnButtonClickListener<T> onNeutralButtonClickListener;

    protected boolean isHideCancel = false;
    protected boolean autoDismiss = true;

    protected IDialog.OnViewCreateListener<T> onViewCreateListener;

    @Override
    protected final int getImplLayoutId() {
        return R.layout._dialog_layout_action;
    }

    protected abstract int getContentLayoutId();

    protected abstract void initContentView(View contentView);

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);


        ViewStub mContentViewStub = findViewById(R.id._content_view_stub);
        mContentViewStub.setLayoutResource(getContentLayoutId());
        mContentView = mContentViewStub.inflate();

        initContentView(mContentView);

        if (onViewCreateListener != null) {
            onViewCreateListener.onViewCreate(self(), mContentView);
        }


        tv_title = findViewById(R.id.tv_title);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_neutral = findViewById(R.id.tv_neutral);
        if (onNeutralButtonClickListener != null) {
            tv_neutral.setVisibility(View.VISIBLE);
        }
        tv_confirm = findViewById(R.id.tv_confirm);
        tv_title.setTextColor(DialogThemeUtils.getMajorTextColor(context));

        applyPrimaryColor();

        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        tv_neutral.setOnClickListener(this);

        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(cancelText)) {
            tv_cancel.setText(cancelText);
        }
        if (!TextUtils.isEmpty(confirmText)) {
            tv_confirm.setText(confirmText);
        }
        if (!TextUtils.isEmpty(neutralText)) {
            tv_neutral.setText(neutralText);
        }

        if (isHideCancel) tv_cancel.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        if (v == tv_cancel) {
            if (cancelListener != null) {
                cancelListener.onClick(self(), IDialog.BUTTON_NEGATIVE);
            }
            if (autoDismiss) {
                dismiss();
            }

        } else if (v == tv_confirm) {
            if (confirmListener != null) {
                confirmListener.onClick(self(), IDialog.BUTTON_POSITIVE);
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

        if (neutralBtnColor == 0) {
            tv_cancel.setTextColor(DialogThemeUtils.getNegativeTextColor(context));
        } else {
            tv_neutral.setTextColor(neutralBtnColor);
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
        this.confirmListener = listener;
        return self();
    }

    public T setPositiveButton(String btnStr, IDialog.OnButtonClickListener<T> listener) {
        this.confirmText = btnStr;
        this.confirmListener = listener;
        return self();
    }

    public T setPositiveButton(int btnStrId, IDialog.OnButtonClickListener<T> listener) {
        this.confirmText = ContextUtils.getApplicationContext().getString(btnStrId);
        this.confirmListener = listener;
        return self();
    }

    public T setNegativeButton(IDialog.OnButtonClickListener<T> listener) {
        this.cancelListener = listener;
        return self();
    }

    public T setNegativeButton(String btnStr, IDialog.OnButtonClickListener<T> listener) {
        this.cancelText = btnStr;
        this.cancelListener = listener;
        return self();
    }

    public T setNegativeButton(int btnStrId, IDialog.OnButtonClickListener<T> listener) {
        this.cancelText = ContextUtils.getApplicationContext().getString(btnStrId);
        this.cancelListener = listener;
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

    public T setCancelText(String cancelText) {
        this.cancelText = cancelText;
        return self();
    }

    public T setConfirmText(String confirmText) {
        this.confirmText = confirmText;
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
